package com.slepeweb.cms.control;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.slepeweb.cms.bean.CmsBeanFactory;
import com.slepeweb.cms.bean.Field;
import com.slepeweb.cms.bean.Field.FieldType;
import com.slepeweb.cms.bean.FieldForType;
import com.slepeweb.cms.bean.FieldValue;
import com.slepeweb.cms.bean.FieldValueSet;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemGist;
import com.slepeweb.cms.bean.ItemIdentifier;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.ItemUpdateHistory;
import com.slepeweb.cms.bean.ItemUpdateRecord.Action;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.LinkName;
import com.slepeweb.cms.bean.LinkNameOption;
import com.slepeweb.cms.bean.LinkType;
import com.slepeweb.cms.bean.Media;
import com.slepeweb.cms.bean.MoverItem;
import com.slepeweb.cms.bean.MoverItem.RelativeLocation;
import com.slepeweb.cms.bean.Ownership;
import com.slepeweb.cms.bean.RestResponse;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.SolrParams4Cms;
import com.slepeweb.cms.bean.StickyAddNewControls;
import com.slepeweb.cms.bean.Template;
import com.slepeweb.cms.bean.UndoRedoStatus;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.bean.guidance.IGuidance;
import com.slepeweb.cms.component.CmsHooker;
import com.slepeweb.cms.component.ICmsHook;
import com.slepeweb.cms.component.Navigation.Node;
import com.slepeweb.cms.constant.AttrName;
import com.slepeweb.cms.except.MissingDataException;
import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.cms.json.LinkParams;
import com.slepeweb.cms.service.CookieService;
import com.slepeweb.cms.service.FieldForTypeService;
import com.slepeweb.cms.service.FieldService;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.cms.service.ItemUpdateUndoService;
import com.slepeweb.cms.service.ItemWorkerService;
import com.slepeweb.cms.service.LinkService;
import com.slepeweb.cms.service.MediaFileService;
import com.slepeweb.cms.service.MediaService;
import com.slepeweb.cms.service.SolrService4Cms;
import com.slepeweb.cms.service.TagService;
import com.slepeweb.commerce.bean.Product;
import com.slepeweb.common.solr.bean.SolrConfig;
import com.slepeweb.common.util.DateUtil;
import com.slepeweb.common.util.HttpUtil;
import com.slepeweb.common.util.ImageUtil;

@Controller
@RequestMapping("/rest")
public class RestController extends BaseController {
	private static Logger LOG = Logger.getLogger(RestController.class);
	
	@Autowired private ItemService itemService;
	@Autowired private LinkService linkService;
	@Autowired private ItemWorkerService itemWorkerService;
	@Autowired private FieldService fieldService;
	@Autowired private FieldForTypeService fieldForTypeService;
	@Autowired private TagService tagService;
	@Autowired private CookieService cookieService;
	@Autowired private SolrService4Cms solrService4Cms;
	@Autowired private NavigationController navigationController;
	@Autowired private CmsHooker cmsHooker;
	@Autowired private ItemUpdateUndoService itemUpdateUndoService;
	@Autowired private MediaService mediaService;
	@Autowired private MediaFileService mediaFileService;
	
	/* 
	 * This mapping is used by the main left-hand navigation.
	 * 
	 */
	@RequestMapping("/item/editor")
	public String doItemEditor(ModelMap model, 
			@RequestParam(value="key", required=true) Long origId,
			@RequestParam(value="language", required=false) String requestedLanguage,
			HttpServletRequest req, HttpServletResponse res) {	
		
		User u = getUser(req);
		Item i = this.getEditableVersion(origId, u);
		
		if (i != null) {
			String lang = chooseLanguage(i.getSite().isMultilingual(), requestedLanguage, i.getSite().getLanguage());
			model.addAttribute("_requestedLanguage", lang);
			
			model.put("editingItem", i);
			model.put("allVersions", i.getAllVersions());
			model.addAttribute("availableTemplatesForType", i.getSite().getAvailableTemplates(i.getType().getId()));
			
			if (i.isProduct()) {
				model.addAttribute("availableAxes", this.cmsService.getAxisService().get());
			}
			
			// Work out form data to build the field editor page
			model.addAttribute("_fieldSupport", fieldEditorSupport(i));
			
			// Store this item's id in a cookie			
			this.cookieService.updateBreadcrumbsCookie(i, req, res);
			
			// Last relative position selection for 'addnew'
			model.addAttribute("_stickyAddNewControls", this.cookieService.getStickyAddNewControls(req));
			
			// Total number of editable items in this section
			String path = i.isSiteRoot() ? "/" : i.getPath() + "/";
			model.addAttribute("_numItemsInSection", 
					1 + this.cmsService.getItemService().getCountByPath(i.getSite().getId(), path));
			
			// Get recently-used tags, and full list of tags for the site
			model.addAttribute(AttrName.TAG_INPUT_SUPPORT, getTagInfo(i.getSite().getId(), req));
			
			// Get up, down, next, previous navigation links
			model.addAttribute("_navkeys", getNavigationLinks(i));
			
			// Flagged items
			Map<Long, ItemGist> trashFlags = getFlaggedItems(req);			
			model.addAttribute(AttrName.FLAGGED_ITEMS, getSortedFlaggedItems(trashFlags));
			model.addAttribute(AttrName.ITEM_IS_FLAGGED, trashFlags.get(i.getOrigId()) != null);
			
			// Item update history - supporting undo/redo functionality
			model.addAttribute(AttrName.UNDO_REDO_STATUS, new UndoRedoStatus(this.getItemUpdateHistory(req)));
			
			// Site content ownership
			model.addAttribute(AttrName.OWNERSHIP, new Ownership(i, u));
		}		
		
		return "cms.item.editor";		
	}
	
	private Map<String, Long> getNavigationLinks(Item i) {
		Map<String, Long> map = new HashMap<String, Long>(5);
		Long next = -1L, previous = -1L, parent = -1L, firstChild = -1L;
		
		if (i.getBoundItems().size() > 0) {
			firstChild = i.getBoundItems().get(0).getOrigId();
		}
		
		if (i.isSiteRoot()) {
			Item contentRoot = i.getSite().getContentItem("");
			if (contentRoot != null) {
				next = contentRoot.getOrigId();
			}
		}
		else if (i.isContentRoot()) {
			Item siteRoot = i.getSite().getItem("/");
			if (siteRoot != null) {
				previous = siteRoot.getOrigId();
			}
		}
		else {
			Link parentLink = i.getOrthogonalParentLink();
			Item parentItem = parentLink.getChild();
			
			if (parentItem != null) {			
				parent = parentItem.getOrigId();
				List<Item> siblings = parentItem.getBoundItems();
				
				for (int j = 0; j < siblings.size(); j++ ) {
					if (siblings.get(j).getId().longValue() == i.getId().longValue()) {						
						// The 'next link will take you ...
						if (j < (siblings.size() - 1)) {
							// ... to the next sibling
							next = siblings.get(j + 1).getOrigId();
						}
						/*
						else {
							// ... down the tree to the first child
							next = firstChild;
						}
						*/
						
						// The previous link will take you to ...
						if (j > 0) {
							// ... the previous sibling
							previous = siblings.get(j - 1).getOrigId();
						}
						/*
						else {
							// ... up the tree to the parent item
							previous = parent;
						}
						*/
						
						break;
					}
				}
			}
		}

		map.put("up", parent);
		map.put("left", previous);
		map.put("right", next);
		map.put("down", firstChild);
		
		return map;
	}
	
	private String chooseLanguage(boolean isMultilingualSite, String requested, String siteDefault) {
		if (isMultilingualSite) {
			return StringUtils.isBlank(requested) ? siteDefault : requested;
		}
		return siteDefault;
	}
	
	/*
	 * NOTE: that I was getting 'Bad Request' returned by Spring when I had a long list of @RequestParam's,
	 * so instead used HttpServletRequest in the method signature, and that seemed to cure the problem.
	 * (But it could have been something else!)
	 */
	@RequestMapping(value="/item/{origId}/update/core", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public RestResponse updateItemCore(
			@PathVariable long origId, HttpServletRequest req, ModelMap model) {	
				
		// Keep a copy of the item being edited 
		User u = getUser(req);
		Item before = getEditableVersion(origId, u, true);
		before.getTags();

		// Get the item again, and update this instance
		Item i = getEditableVersion(origId, u, true);	
		
		RestResponse resp = new RestResponse();
		Long templateId = getLongParam(req, "template");
		Template t = templateId != null ?
				this.cmsService.getTemplateService().getTemplate(templateId) : null;
		
		Object[] data = new Object[4];
		
		boolean wasPublished = i.isPublished();
		String oldName = i.getName();
		
		i = i.setName(getParam(req, "name")).
			setSimpleName(getParam(req, "simplename")).
			setDateUpdated(new Timestamp(System.currentTimeMillis())).
			setSearchable(getBooleanParam(req, "searchable")).
			setPublished(getBooleanParam(req, "published")).
			setTemplate(t);
		
		Long ownerId = getLongParam(req, "owner");
		if (ownerId != null) {
			i.setOwnerId(ownerId);
		}
		
		if (i.isShortcut()) {
			// Whilst the corresponding form fields are disabled ...
			i.setPublished(true);
			i.setSearchable(false);
		}
		
		Product p = null;
		
		if (i.isProduct()) {
			p = (Product) i;
			p.
				setPartNum(getParam(req, "partNum")).
				setStock(getLongParam(req, "stock")).
				setPrice(getLongParam(req, "price"));					
		}
		
		try {
			if (i.isProduct()) {
				p.save();
				resp.addMessage("Core product data successfully updated");
			}
			else {
				i.save();
				resp.addMessage("Core item data successfully updated");
			}
			
			// Save item tags, if changes have been made
			@SuppressWarnings("unchecked")
			List<String> recentTags = (List<String>) req.getSession().getAttribute(AttrName.RECENT_TAGS);
			req.getSession().setAttribute(AttrName.RECENT_TAGS, 
					this.itemWorkerService.saveTags(i, getParam(req, "tags"), recentTags));		
			
			LOG.info(userLog(u, "updated core data for", i));
			
			// Navigation node with title assigned to saved item name IF changed
			data[0] = i.getName().equals(oldName) ? null : i.getName();
			
			// Boolean value assigned IF version 1 has been published
			data[1] = i.getVersion() == 1 && ! wasPublished && i.isPublished();
			
			// Boolean value assigned IF published status has changed
			data[2] = wasPublished ^ i.isPublished();
			
			data[3] = pushItemUpdateRecord(req, before, i, Action.core);
			
			resp.setData(data);
		}
		catch (Exception e) {
			return resp.setError(true).addMessage(e.getMessage());		
		}
		
		return resp;
	}
	
	private String getParam(HttpServletRequest req, String name) {
		return req.getParameter(name);
	}
	
	private Long getLongParam(HttpServletRequest req, String name) {
		String s = getParam(req, name);
		return s != null ? Long.valueOf(s) : null;
	}
	
	private boolean getBooleanParam(HttpServletRequest req, String name) {
		return getParam(req, name).equals("true");
	}
	
	@RequestMapping(value="/item/{origId}/update/media", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public RestResponse updateItemMedia(
			@PathVariable Long origId, 
			@RequestParam("media") MultipartFile file, 
			@RequestParam(value="thumbnail", required=false) String thumbnailAction, 
			@RequestParam(value="width", required=false) Integer thumbnailWidth, 
			HttpServletRequest req,
			ModelMap model) {	
		
		RestResponse resp = new RestResponse();
		String msg;
		
		if (thumbnailAction == null) {
			thumbnailAction = "none";
		}
		
		if (file != null) {
			Item i = getEditableVersion(origId, getUser(req), true);
			
			if (i != null) {
				saveTempMedia(i);
				
				try {
					InputStream is = file.getInputStream();
					InputStream scaledIs;

					if (thumbnailAction.equals("onlythumb")) {
						scaledIs = ImageUtil.scaleImage(is, thumbnailWidth, -1, "jpg");
						this.mediaService.save(i.getId(), scaledIs, true);
					}
					else {
						this.mediaService.save(i.getId(), is, false);
						
						if (thumbnailAction.equals("autoscale") && i.getType().isImage()) {
							scaledIs = ImageUtil.scaleImage(file.getInputStream(), thumbnailWidth, -1, "jpg");
							this.mediaService.save(i.getId(), scaledIs, true);
						}
					}
					
					// Set the updatedate for this change in the db, and get a new Item object from the same.
					Item after = this.itemService.updateDateUpdated(i);
					
					saveTempMedia(after);
										
					LOG.info(msg = "Media successfully uploaded");
					return resp.
							setError(false).
							addMessage(msg).
							setData(pushItemUpdateRecord(req, i, after, Action.media));
				}
				catch (Exception e) {
					LOG.error(msg = "Problem with input streams", e);
					return resp.setError(true).addMessage(msg);		
				}
			}			
		}
		else {
			LOG.error(msg = "No file specified");
			return resp.setError(true).addMessage(msg);
		}
		
		LOG.error(msg = "No media upload undertaken");
		return resp.setError(true).addMessage(msg);
	}
	
	private void saveTempMedia(Item i) {
		for (Media m : i.getAllMedia()) {
			if (m.getSize() > 0) {
				this.mediaFileService.saveTempFile(i, m);
			}
		}
	}
	
	@RequestMapping(value="/item/{origId}/update/fields", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public RestResponse updateFields(@PathVariable long origId, HttpServletRequest request, ModelMap model) {	
		
		RestResponse resp = new RestResponse();
		
		// Keep a copy of the item being edited
		User u = getUser(request);
		Item before = getEditableVersion(origId, u, true);
		
		// Populate 'before' with field values
		before.getFieldValueSet();

		// Get the item again, and update this instance
		Item i = getEditableVersion(origId, u, true);		

		String variable, stringValue, dateValueStr = null, timeValueStr = null;
		FieldType ft;
		FieldValue fv;
		SimpleDateFormat sdf;
		Timestamp stamp;
		Calendar cal;
		int c = 0;
		
		try {
			request.setCharacterEncoding("utf-8");
		} 
		catch (UnsupportedEncodingException e1) {
		}
		
		String language = chooseLanguage(i.getSite().isMultilingual(), 
				request.getParameter("language"), i.getSite().getLanguage());
		
		// Identify FieldValue objects for this item
		FieldValueSet fvs = i.getFieldValueSet();
		
		// Build a list of FieldValue objects that need to be saved
		List<FieldValue> fvList2Save = new ArrayList<FieldValue>();
		
		// Store error messages
		List<String> errors = new ArrayList<String>();
		boolean isErrors = false;
		
		// Loop through fields for this item type
		for (FieldForType fft : i.getType().getFieldsForType()) {
			// Only interested in multilingual fields for additional languages IFF site is multilingual
			if (i.getSite().isMultilingual() && 
					! (language.equals(i.getSite().getLanguage()) || fft.getField().isMultilingual())) {
				
				continue;
			}
			
			// For this field, see if there is a matching query parameter
			variable = fft.getField().getVariable();
			ft = fft.getField().getType();
			
			// Current FieldValue for this field/language
			fv = fvs.getFieldValueObj(variable, language);
			
			// Form input value, as a string.
			stringValue = request.getParameter(variable);
			
			// If field type is date/datetime, then stringvalue is calculated 
			// from 2 separate form input fields:
			if (ft == FieldType.date || ft == FieldType.datetime) {
				/* 
				 * For a datetime field, there will be 2 query parameters, eg. for a field 
				 * named 'datepublished', param 'datepublished_d' will hold the date value,
				 * and param 'datepublished_t' will hold the time.
				 */
				dateValueStr = request.getParameter(String.format("%s_d", variable));
				timeValueStr = null;
				
				if (ft == FieldType.datetime) {
					timeValueStr = request.getParameter(String.format("%s_t", variable));
					if (timeValueStr != null) {
						// Validate time
						int hours = Integer.parseInt(timeValueStr.substring(0, 2));
						int minutes = Integer.parseInt(timeValueStr.substring(3));
						
						if (hours > 23 || minutes > 59) {
							errors.add(String.format("Invalid time value [%s]", timeValueStr));
							continue;
						}
					}
				}
				
				stringValue = dateValueStr + (timeValueStr == null ? "" : " " + timeValueStr);
			}
			
			if (stringValue != null) {			
				if (fv == null) {
					fv = CmsBeanFactory.makeFieldValue().
							setField(fft.getField()).
							setItemId(i.getId()).
							setValue(fft.getField().getDefaultValueObject()).
							setLanguage(language);
				}
				
				if (ft == FieldType.integer && StringUtils.isNotBlank(stringValue)) {
					fv.setValue(Integer.parseInt(stringValue));
				}
				else if (ft == FieldType.date || ft == FieldType.datetime) {
					if (ft == FieldType.date) {
						sdf = DateUtil.DATE_PATTERN_B;
					}
					else {
						sdf = DateUtil.DATE_AND_TIME_PATTERN;
					}
					
					try {
						cal = Calendar.getInstance();
						cal.setTime(sdf.parse(stringValue));
						cal.set(Calendar.MILLISECOND, 0);
						cal.set(Calendar.SECOND, 1);
						
						if (ft == FieldType.date) {
							cal.set(Calendar.MINUTE, 0);
							cal.set(Calendar.HOUR, 0);
						}
						
						stamp = new Timestamp(cal.getTimeInMillis());
						fv.setDateValue(stamp);
						fv.setStringValue(stringValue);
					}
					catch (ParseException e) {
						errors.add(String.format("Date not parseable [%s]", stringValue));
						continue;
					}
				}
				else {
					fv.setValue(stringValue);
				}
				
				// Does this field value require validation?
				ICmsHook hook = this.cmsHooker.getHook(i.getSite().getShortname());
				if (! validateFieldValue(fv, hook.getFieldGuidance(fv.getField().getVariable()), errors)) {
					continue;
				}
				
				/* 
				 * Save this FieldValue for saving later, as long as there are no subsequent 
				 * errors relating to other fields on this item.
				 */
				fvList2Save.add(fv);
			}
		}
		
		
		isErrors = errors.size() > 0;
		if (! isErrors) {			
			for (FieldValue fvx : fvList2Save) {
				try {
					fvx.save();
					c++;
				}
				catch (ResourceException e) {
					LOG.error(e.getMessage());
					isErrors = true;
				}
			}
		}
		
		resp.setError(isErrors);
		
		if (isErrors) {
			for (String s : errors) {
				resp.addMessage(s);		
			}
		}
		else {
			try {
				// Update dateUpdated for the item, and save the core data
				i.resetDateUpdated();	
				i = i.save();
				
				// Now save the item's field values
				i.saveFieldValues();
				
				resp.setData(pushItemUpdateRecord(request, before, i, Action.field));
				resp.addMessage(String.format("%d fields updated", c));
			}
			catch (ResourceException e) {
				resp.setError(true).addMessage("Item could not be saved: missing data");					
			}
		}
		
		return resp;
	}	
	
	private boolean validateFieldValue(FieldValue fv, IGuidance guidance, List<String> errors) {		
		if (guidance != null) {
			if (! guidance.validate(fv.getStringValue())) {
				errors.add(String.format("Field value '%s' fails validation", fv.getStringValue()));
				return false;
			}
			
			// Clean up input, if necessary
			fv.setStringValue(guidance.clean(fv.getStringValue()));
		}
		
		return true;
	}
	
	@RequestMapping(value="/item/{parentOrigId}/add", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public RestResponse addItem(
			@PathVariable long parentOrigId, 
			@RequestParam("relativePosition") String relativePosition, 
			@RequestParam("template") long templateId, 
			@RequestParam("itemtype") long itemTypeId, 
			@RequestParam("linktype") String linkType, 
			@RequestParam("linkname") String linkName, 
			@RequestParam("name") String name, 
			@RequestParam(value="simplename", required=false) String simplename, 
			@RequestParam(value="partNum", required=false) String partNum, 
			@RequestParam(value="price", required=false) Long price, 
			@RequestParam(value="stock", required=false) Long stock, 
			@RequestParam(value="alphaaxis", required=false) Long alphaAxisId, 
			@RequestParam(value="betaaxis", required=false) Long betaAxisId, 
			HttpServletRequest req,
			HttpServletResponse res,
			ModelMap model) {	
		
		RestResponse resp = new RestResponse();
		
		Template t = null;
		if (templateId > 0) {
			t = this.cmsService.getTemplateService().getTemplate(templateId);
			if (t != null) {
				itemTypeId = t.getItemTypeId();
			}
		}
		
		ItemType it = this.cmsService.getItemTypeService().getItemType(itemTypeId);
		
		StickyAddNewControls stick = new StickyAddNewControls(relativePosition, templateId, itemTypeId);
		this.cookieService.saveCookie(CookieService.STICKY_ADDNEW_CONTROLS, stick.toString(), CookieService.CMS_COOKIE_PATH, res);
		
		User u = getUser(req);
		Item parent = getEditableVersion(parentOrigId, u, true);
		if (relativePosition.equals("alongside") && ! parent.isRoot()) {
			parent = parent.getParent();
		}
		 
		Item i = CmsBeanFactory.makeItem(it.getName()).
				setSite(parent.getSite()).
				setPath(String.format("%s/%s", parent.getPath(), simplename)).
				setTemplate(t).
				setType(it).
				setLinkType(linkType).
				setLinkName(linkName).
				setOwnerId(u.getId()).
				setName(name).
				setSimpleName(simplename).
				setDateCreated(new Timestamp(System.currentTimeMillis())).
				setDeleted(false);
		
		i.setDateUpdated(i.getDateCreated());
		
		// There should only ever be one version of a Shortcut item, and that needs to
		// be published in order for it to be 'visible' by the delivery server.
		if (i.isShortcut()) {
			i.setPublished(true);
		}
		
		Product p = null;
		
		if (i.isProduct()) {
			p = (Product) i;
			p.
				setPartNum(partNum).
				setStock(stock).
				setPrice(price).
				setAlphaAxisId(alphaAxisId).
				setBetaAxisId(betaAxisId);
		}
		
		try {
			if (i.isProduct()) {
				p = p.save();
			}
			else {
				i = i.save();
			}
			
			LOG.info(userLog(u, "saved item", i));
			
			// Site-specific actions on adding a new item
			ICmsHook h = this.cmsHooker.getHook(i.getSite().getShortname());
			h.addItemPost(i);
			
			Object[] o = new Object[]{
					Node.toNode(i), 
					i.isShortcut(), 
					i.getType().isMedia(),
					pushItemUpdateRecord(req, i, i, Action.none)};	
			
			resp.addMessage("Item added").setData(o);
			
			if (i.isShortcut()) {
				resp.addMessage("Don't forget to identify shortcut target");
			}
			else if (i.getType().isMedia()) {
				resp.addMessage("Don't forget to load media from file");
			}
			
			return resp;
		}
		catch (Exception e) {
			return resp.setError(true).addMessage(e.getMessage()).setData(parentOrigId);
		}
	}
	
	@RequestMapping(value="/item/{origId}/copy", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public RestResponse copyItem(
			@PathVariable long origId, 
			@RequestParam(value="name", required=true) String name, 
			@RequestParam(value="simplename", required=true) String simplename, 
			HttpServletRequest req,
			ModelMap model) {	
		
		RestResponse resp = new RestResponse();
		Item i = getEditableVersion(origId, getUser(req));
		
		try {
			Item c = this.cmsService.getItemWorkerService().copy(i, name, simplename);	
			if (c != null) {
				resp.
					addMessage("Item copied").
					setData(new Object[] {
							Node.toNode(c),
							pushItemUpdateRecord(req, i, i, Action.none)});				
			}
			else {
				resp.setError(true).addMessage("Failed to copy item");
			}
		}
		catch (Exception e) {
			resp.setError(true).addMessage(e.getMessage()).setData(origId);
		}
		
		return resp;
	}
	
	@RequestMapping(value="/item/{origId}/version", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public RestResponse versionItem(
			@PathVariable long origId,
			HttpServletRequest req,
			ModelMap model) {	
		
		RestResponse resp = new RestResponse();
		Item i = getEditableVersion(origId, getUser(req), true);
		
		try {
			Item c = this.cmsService.getItemWorkerService().version(i);			
			return resp.
					setError(false).
					setData(Node.toNode(c)).
					addMessage("New version created");
		}
		catch (Exception e) {
			return resp.setError(true).setData(i.getId()).addMessage(e.getMessage());
		}
	}
	
	@RequestMapping(value="/item/{origId}/revert", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public RestResponse revertItem(
			@PathVariable long origId,
			HttpServletRequest req,
			ModelMap model) {	
		
		RestResponse resp = new RestResponse();
		Item i = getEditableVersion(origId, getUser(req));
		
		if (i != null) {
			try {
				Item r = this.cmsService.getItemWorkerService().revert(i);
				return resp.setError(false).addMessage("Item reverted to previous version").setData(r.getId());
			}
			catch (ResourceException e) {
				return resp.setError(true).addMessage(String.format("No item with this id", origId));
			}
		}
		
		return resp.setError(true).addMessage(String.format("No item with this id", origId));
	}
	
	@RequestMapping(value="/item/{origId}/trash", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public RestResponse trashItem(@PathVariable long origId, HttpServletRequest req, ModelMap model) {	
		
		RestResponse resp = new RestResponse();
		Item i = getEditableVersion(origId, getUser(req), true);
		
		// First, remove item from flagged list, if present
		flagItem(i.getOrigId(), req, true);
			
		// Now trash it
		i.trash();
		
		return resp.
				addMessage("Item trashed").
				addMessage("PARENT ITEM IS NOW CURRENT").
				setData(pushItemUpdateRecord(req, i, i, Action.none));
	}
	
	@RequestMapping(value="/item/{origId}/publish/section", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public RestResponse publishSection(
			@PathVariable long origId, 
			@RequestParam(value="publish_option", required=true) String publishOption,
			HttpServletRequest req,
			ModelMap model) {	
		
		RestResponse resp = new RestResponse();		
		Item i = getEditableVersion(origId, getUser(req), true);
		int count = 0;
		
		if (i != null) {
			count = actionSection(i, "published", publishOption.equals("publish"));
			
			return resp.
					addMessage(String.format("Section publication status updated, total %d items", count)).
					setData(pushItemUpdateRecord(req, i, i, Action.none));
		}
		
		return resp.addMessage(String.format("Section not identifiable [%d]", origId)).setError(true);
	}
	
	@RequestMapping(value="/item/{origId}/searchable/section", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public RestResponse searchableSection(
			@PathVariable long origId, 
			@RequestParam(value="searchable_option", required=true) String searchableOption,
			HttpServletRequest req,
			ModelMap model) {	
		
		RestResponse resp = new RestResponse();		
		Item i = getEditableVersion(origId, getUser(req), true);
		int count = 0;
		
		if (i != null) {
			if (searchableOption.equals("re-index")) {
				count = this.solrService4Cms.indexSection(i);
				return resp.
						addMessage(String.format("Section re-indexed, total %d items", count)).
						setData(pushItemUpdateRecord(req, i, i, Action.none));
			}
			else {
				count = actionSection(i, "searchable", searchableOption.equals("searchable"));
				return resp.
						addMessage(String.format("Section searchability actioned, total %d items updated", count)).
						setData(pushItemUpdateRecord(req, i, i, Action.none));
			}
		}
		
		return resp.addMessage(String.format("Section not identifiable [%d]", origId)).setError(true);
	}
	
	private int actionSection(Item i, String selector, boolean value) {
		int count = actionItem(i, selector, value);

		for (Link l : i.getBindings()) {
			count += actionSection(l.getChild(), selector, value);
		}
		
		return count;
	}
	
	private int actionItem(Item i, String selector, boolean value) {
		if (selector.equals("published") && i.isPublished() != value) {
			this.cmsService.getItemService().updatePublished(i.getId(), value);
			return 1;
		}
		else if (selector.equals("searchable") && i.isSearchable() != value) {
			this.cmsService.getItemService().updateSearchable(i.getId(), value);
			return 1;
		}
		
		return 0;
	}
	
	@RequestMapping(value="/trash/get")
	public String getTrashedItems(ModelMap model) {			
		model.put("_trashContents", this.cmsService.getItemService().getTrashedItems());			
		return "cms.trash.contents";
	}
	
	@RequestMapping(value="/trash/empty/all", produces="application/json")
	@ResponseBody
	public RestResponse deleteAllTrashedItems(ModelMap model) {	
		
		RestResponse resp = new RestResponse();
		int num = this.cmsService.getItemService().deleteTrashedItems(null);			
		return resp.setError(false).addMessage(String.format("Emptied %d items from the trash", num));
	}
	
	@RequestMapping(value="/trash/restore/selected", produces="application/json")
	@ResponseBody
	public RestResponse restoreSelectedTrashedItems(
			@RequestParam(value="id", required=true) String origIdList,
			ModelMap model) {	
		
		RestResponse resp = new RestResponse();

		if (origIdList.endsWith(",")) {
			origIdList = origIdList.substring(0, origIdList.length() - 1);
		}
		
		if (StringUtils.isBlank(origIdList)) {
			return resp.setError(true).addMessage("No items selected by user");
		}
		
		String[] origIdStr = origIdList.split(",");
		int len = origIdStr.length;
	
		long[] origIds = new long[len];
		for (int i = 0; i < len; i++) {
			origIds[i] = Integer.parseInt(origIdStr[i]);
		}
		
		int numRestored = this.cmsService.getItemService().restoreSelectedItems(origIds);
		
		if (numRestored == origIds.length) {
			// Get the first of the restored items
			Item i = this.cmsService.getItemService().getEditableVersion(origIds[0]);
			if (i != null) {
				Item parent = i.getParent();
				
				return resp.
						setError(false).
						addMessage(
						String.format("Restored %d items from the trash", numRestored)).
						setData(new Object[] {Node.toNode(parent), Node.toNode(i)});
			}
		}
		
		return resp.setError(true).addMessage("No items restored");
	}
	
	@RequestMapping(value="/trash/restore/all", produces="application/json")
	@ResponseBody
	public RestResponse restoreAllTrashedItems(ModelMap model) {	
		
		RestResponse resp = new RestResponse();
		
		// Identify one of the restored items, so that we can present it to the user
		List<Item> allTrashedItems = this.cmsService.getItemService().getTrashedItems();
		
		if (allTrashedItems.size() > 0) {
			int numRestored = this.cmsService.getItemService().restoreSelectedItems(null);
			resp.setError(false).addMessage(String.format("Restored %d items from the trash", numRestored));
			
			Node iNode = Node.toNode(allTrashedItems.get(0));
			Node pNode = Node.toNode(allTrashedItems.get(0).getParent());
			
			return resp.setData(new Object[] {pNode, iNode});
		}
		
		return resp.setError(true).addMessage("No items to restore");
	}
	
	@RequestMapping(value="/trash/empty/selected", produces="application/json")
	@ResponseBody
	public RestResponse deleteSelectedTrashedItems(
			@RequestParam(value="id", required=true) String origIdList,
			ModelMap model) {	
		
		RestResponse resp = new RestResponse();

		if (origIdList.endsWith(",")) {
			origIdList = origIdList.substring(0, origIdList.length() - 1);
		}
		
		if (StringUtils.isNotBlank(origIdList)) {
			String[] idStr = origIdList.split(",");
			int len = idStr.length;
		
			long[] ids = new long[len];
			for (int i = 0; i < len; i++) {
				ids[i] = Integer.parseInt(idStr[i]);
			}
			
			return resp.setError(false).addMessage(
					String.format("Emptied %d items from the trash bin", this.cmsService.getItemService().deleteTrashedItems(ids)));
		}
		else {
			return resp.setError(true).addMessage("No items selected by user");
		}
	}
	
	@RequestMapping(value="/item/{moverId}/move", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public RestResponse moveItem(
			@PathVariable long moverId,
			@RequestParam(value="targetId", required=true) Long targetId,
			@RequestParam(value="mode", required=true) String mode,
			HttpServletRequest req) {	
		
		Item i = getEditableVersion(moverId, getUser(req), true);
		Item[] movers = moveItem(i, mode, targetId);
				
		RestResponse resp =  new RestResponse();
		if (movers != null) {
			resp.
				setData(pushItemUpdateRecord(req, movers[0], movers[1], Action.move)).
				addMessage("Item moved");
		}
		else {
			resp.setError(true).addMessage("Move error");
		}
		
		return resp;
	}
	
	private Item[] moveItem(Item i, String mode, long targetId) {
		if (i.isRoot()) {
			LOG.error("Cannot move the root item");
			return null;
		}
		
		// Keep a record of the mover item, to store in ItemUpdateHistory
		MoverItem mover = new MoverItem(i, new RelativeLocation(targetId, mode));
		
		try {
			MoverItem moved =  mover.move();
			return new Item[] {mover, moved};
		}
		catch (MissingDataException e) {
			LOG.error("Missing data", e);
		}
		catch (ResourceException e) {
			LOG.error("Missing data", e);
		}
		
		return null;
	}
	
	@RequestMapping(value="/links/{parentId}/save", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public RestResponse saveLinks(
			@RequestBody LinkParams[] linkParams, 
			@PathVariable long parentId, 
			HttpServletRequest req, 
			ModelMap model) {	

		RestResponse resp = new RestResponse();
		
		// Keep a copy of the item being edited 
		User u = getUser(req);
		Item before = getEditableVersion(parentId, u, true);
		before.getLinks();

		// Get the item again, and update this instance
		Item parent = getEditableVersion(parentId, u, true);
		Item child;
		List<Link> links = new ArrayList<Link>();
		Link l;
		Item i;
		int count = 0;
		
		// Need to return extra information if parent is a Shortcut, and its target has been completed/broken
		String shortcutAction = "none";
		String shortCutTargetType = null;
		
		if (parent.isShortcut()) {
			for (Link old : this.cmsService.getLinkService().getLinks(parentId)) {
				if (old.getType().equals(LinkType.shortcut)) {
					shortCutTargetType = old.getChild().getType().getName();
					break;
				}
			}
		}
		
		for (LinkParams lp : linkParams) {
			l = CmsBeanFactory.makeLink().
					setParentId(parent.getId()).
					setName(lp.getName()).
					setOrdering(count++).
					setType(lp.getType());
			
			if (StringUtils.isNotBlank(lp.getData())) {
				try {
					l.setData(URLDecoder.decode(lp.getData(), "utf-8"));
				} 
				catch (UnsupportedEncodingException e) {}
			}
			
			child = getEditableVersion(lp.getChildId(), u);
			i = CmsBeanFactory.makeItem(null).setId(child.getId());			
			l.setChild(i);			
			links.add(l);
			
			if (parent.isShortcut() && lp.getType().equals(LinkType.shortcut)) {
				shortcutAction = "add";
				shortCutTargetType = child.getType().getName();
			}
		}
		
		parent.setLinks(links);
		
		if (links.size() == 0) {
			shortcutAction = "remove";
			// original shortcutType was captured earlier
		}
		
		try {
			parent.saveLinks();
			
			// Related items are stored in solr for pho site.
			ICmsHook hook = this.cmsHooker.getHook(parent.getSite().getShortname());
			hook.updateLinksPost(parent);			
			
			resp.addMessage(String.format("%d links saved", links.size()));
			
			Object[] response = new Object[] {
					this.navigationController.doLazyNavOneLevel(parent.getOrigId(), parent.getSite().getId(), req),
					shortcutAction,
					shortCutTargetType == null ? null : shortCutTargetType.toLowerCase(),
					pushItemUpdateRecord(req, before, parent, Action.links)};
			
			// Return a list of shortcuts as response data, so that leftnav tree can be refreshed
			resp.setData(response);
		}
		catch (ResourceException e) {
			resp.setError(true).addMessage(e.getMessage());
		}
		
		return resp;
	}
	
	@RequestMapping(value="/updatebinding/{childId}", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public RestResponse updateBindings(
			@RequestBody String[] params, 
			@PathVariable long childId, 
			HttpServletRequest req, 
			ModelMap model) {	

		RestResponse resp = new RestResponse();
		
		// Keep a copy of the item being edited 
		long parentId = Long.valueOf(params[0]);
		String linkType = params[1];
		String linkName = params[2];
		
		Link l = this.linkService.getLink(parentId, childId);
		if (l == null) {
			return resp.setError(true).addMessage("Failed to identify link");
		}
		
		l.setType(linkType);
		l.setName(linkName);
		
		try {
			this.linkService.save(l);
			resp.setError(false).addMessage("Link updated");
		}
		catch (ResourceException e) {
			resp.setError(true).addMessage(e.getMessage());
		}
		
		return resp;
	}
	
	@RequestMapping(value="/linknames/{siteId}/{linkType}", method=RequestMethod.POST)
	public String getLinkNameOptions(
			@PathVariable long siteId, @PathVariable String linkType, ModelMap model) {	
		
		List<LinkNameOption> options = new ArrayList<LinkNameOption>();
		
		if (! linkType.equals("unknown")) {
			LinkType lt = this.cmsService.getLinkTypeService().getLinkType(linkType);
			ICmsHook hook = this.cmsHooker.getHook(siteId);
			IGuidance guidance;
			
			if (lt != null) {
				for (LinkName ln : this.cmsService.getLinkNameService().getLinkNames(siteId, lt.getId())) {
					guidance = hook.getLinknameGuidance(ln.getName());
					options.add(new LinkNameOption(ln.getName(), guidance));
				}
			}
		}
		
		model.addAttribute("_linknameOptions", options);
		return "cms.linknameOptions";
	}
	
	@RequestMapping(value="/item/{origId}/name", method=RequestMethod.POST, produces="text/text")
	@ResponseBody
	public String getItemName(@PathVariable long origId, HttpServletRequest req, ModelMap model) {	
		Item i = getEditableVersion(origId, getUser(req));
		if (i != null) {
			return i.getName();
		}
		return "n/a";
	}
	
	@RequestMapping(value="/item/history/{siteId}", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public List<ItemIdentifier> history(@PathVariable long siteId, HttpServletRequest req) {	
		
		Site s = this.cmsService.getSiteService().getSite(siteId);
		return this.cookieService.getBreadcrumbsCookieValue(s, req);
	}

	@RequestMapping(value="/js", method=RequestMethod.GET, produces="text/javascript")
	//@ResponseBody
	public void javascript(HttpServletRequest req, HttpServletResponse res, ModelMap model) {	
		
		LOG.info("assembling js files");
		
		StringBuilder sb = new StringBuilder();
		File folder = new File(req.getServletContext().getRealPath("/WEB-INF/js"));
		BufferedReader r;
		int bufflen = 1000, len;
		char[] buffer = new char[bufflen];
		List<File> jsfiles = Arrays.asList(folder.listFiles());
		
		Collections.sort(jsfiles, new Comparator<File>() {
			public int compare(File f1, File f2) {
				return f1.getName().compareTo(f2.getName());
			}});
		
		for (File f : jsfiles) {			
			try {
				r = new BufferedReader(new FileReader(f));
				do {
					len = r.read(buffer, 0, bufflen);					
					sb.append(buffer, 0, len);
				} 
				while (len > -1);
			}
			catch (Exception e) {}
			
			sb.append("\n");
		}
		
		long now = DateUtil.now().getTime();
		HttpUtil.setCacheHeaders(now, now, 1200000L, 600000L, res);
		res.setContentType("text/javascript;charset=utf-8");
		res.setCharacterEncoding("utf-8");
		res.setHeader("Content-Length", String.valueOf(sb.length()));	
		ByteArrayInputStream is = null;
		
		try {
			is = new ByteArrayInputStream(sb.toString().getBytes("utf-8"));
			HttpUtil.stream(is, res.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@RequestMapping("/search")
	public String search(ModelMap model, 
			@RequestParam(value="key", required=true) Long origId,
			@RequestParam(value="searchtext", required=true) String searchtext,
			HttpServletRequest req) {	
		
		Item i = this.getEditableVersion(origId, getUser(req));
		
		if (i != null) {
			SolrParams4Cms params = new SolrParams4Cms(new SolrConfig().setPageSize(20)).
					setSearchText(searchtext).
					setSiteId(i.getSite().getId()).setLanguage(i.getLanguage());
			
			model.addAttribute("_response", this.solrService4Cms.query(params));
		}
		
		return "cms.searchresults";		
	}
	
	@RequestMapping(value="/item/{origId}/flag", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public boolean flagItem(@PathVariable long origId, HttpServletRequest request) {
		return flagItem(origId, request, false);
	}
	
	@RequestMapping(value="/item/{origId}/flag/siblings", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public int flagSiblings(@PathVariable long origId, HttpServletRequest request) {
		Item i = this.getEditableVersion(origId, getUser(request));
		Item parent = i.getParent();
		Map<Long, ItemGist> flaggedItems = getFlaggedItems(request);
		Date now = new Date();
		int count = 0;
		
		for (Item sibling : parent.getBoundItems()) {
			if (! flaggedItems.containsKey(sibling.getOrigId())) {
				flaggedItems.put(sibling.getOrigId(), new ItemGist(sibling).setDate(now));
				count++;
			}
		}
		
		return count;		
	}
	
	@RequestMapping(value="/item/{origId}/unflag", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public boolean unflagItem(@PathVariable long origId, HttpServletRequest request) {
		return flagItem(origId, request, true);
	}
	
	@RequestMapping(value="/flaggedItems/list", method=RequestMethod.GET)
	public String listFlaggedItems(HttpServletRequest request, ModelMap model) {
		model.addAttribute("_flaggedItems", getSortedFlaggedItems(getFlaggedItems(request)));
		return "cms.refresh.flaggedItemsList";		
	}
	
	@RequestMapping(value="/flaggedItems/unflag/all", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public int unflagAllFlaggedItems(HttpServletRequest request, ModelMap model) {
		Map<Long, ItemGist> flaggedItems = getFlaggedItems(request);
		int count = flaggedItems.size();
		flaggedItems.clear();
		return count;		
	}
	
	@RequestMapping(value="/flaggedItems/trash/all", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public RestResponse trashAllFlaggedItems(HttpServletRequest request, ModelMap model) {
		Map<Long, ItemGist> flaggedItems = getFlaggedItems(request);
		Iterator<Long> iter = flaggedItems.keySet().iterator();
		Long origId;
		Item i;
		int count = 0;
		
		while (iter.hasNext()) {
			origId = iter.next();
			i = getEditableVersion(origId, getUser(request));
			if (i != null) {
				count += this.itemService.trashItemAndDirectChildren(i);
			}
		}
		
		flaggedItems.clear();
		
		return new RestResponse().
				addMessage(String.format("%d items have been moved to the trash bin", count));		
	}
	
	@RequestMapping(value="/flaggedItems/move", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public RestResponse moveFlaggedItems(
			@RequestParam(value="target", required=true) Long targetId,
			@RequestParam(value="mode", required=true) String mode,
			HttpServletRequest req) {
		
		Map<Long, ItemGist> flaggedItems = getFlaggedItems(req);
		Iterator<Long> iter = flaggedItems.keySet().iterator();
		Long origId;
		Item i, lastMoved = null;
		Item[] movers;
		int count = 0;
		
		while (iter.hasNext()) {
			origId = iter.next();
			i = getEditableVersion(origId, getUser(req));
			
			if (i != null) {
				movers = moveItem(i, mode, targetId);
				if (movers != null) {
					lastMoved = movers[1];
					mode = "after";
					targetId = lastMoved.getOrigId();
					count += 1;
					
					// Update flagged items, esp. item path
					flaggedItems.put(movers[1].getOrigId(), new ItemGist(movers[1]));
				}
			}
		}
		
		RestResponse resp = new RestResponse();
		
		if (count > 0) {
			// This action cannot be undone
			pushItemUpdateRecord(req, lastMoved, lastMoved, Action.none);
			
			// No need to return UndoRedoStatus object, since entire page will get re-loaded.
			resp.addMessage(String.format("%d moves completed", count)).
				setData(lastMoved.getOrigId());
		}
		else {
			resp.setError(true).addMessage("No items moved");
		}
		
		return resp;
	}
	
	// @currentItemOrigId is the origId of the current item, which may or may not be included in 
	// the set of flagged items.
	@RequestMapping(value="/flaggedItems/copy/all/{currentItemOrigId}", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public RestResponse copyAllFlaggedItems(@PathVariable long currentItemOrigId, HttpServletRequest request, ModelMap model) 
			throws ResourceException {
		RestResponse resp = new RestResponse();
		Map<Long, ItemGist> flaggedItems = getFlaggedItems(request);
		Iterator<Long> itemIter = flaggedItems.keySet().iterator();
		
		Item i;
		String identifier, type, key, strValue;
		Object value = null;
		Map<String, Object> coreData = new HashMap<String, Object>();
		Map<String, Object> fieldValues = new HashMap<String, Object>();
		long origId;
		Field field;
		boolean currentItemAffected = false;

		Enumeration<String> enumer = request.getParameterNames();
		while (enumer.hasMoreElements()) {
			identifier = enumer.nextElement();
			type = identifier.substring(0, 1);
			key = identifier.substring(2);
			strValue = request.getParameter(identifier);
			value = strValue;
			
			// Core data
			if (type.equals("0")) {
				if (key.equals("published") || key.equals("searchable")) {
					value = strValue.equals("checked") ? true : false;
				}
				coreData.put(key, value);
			}
			// Field values
			else if (type.equals("1")) {
				field = this.fieldService.getField(key);
				if (field.getType() == FieldType.integer) {
					value = Integer.valueOf(strValue);
				}
				fieldValues.put(key, value);
			}
		}
		
		boolean isCoreData = coreData.size() > 0;
		boolean isFieldValues = fieldValues.size() > 0;
		Iterator<String> keyIter;
		Item last = null;
		
		while (itemIter.hasNext()) {
			origId = itemIter.next();
			
			if (origId == currentItemOrigId) {
				currentItemAffected = true;
			}
			
			i = getEditableVersion(origId, getUser(request));
			if (i != null) {
				last = i;
				
				if (isCoreData) {
					keyIter = coreData.keySet().iterator();
					while (keyIter.hasNext()) {
						key = keyIter.next();
						value = coreData.get(key);
						
						if (key.equals("tags")) {
							this.tagService.save(i, (String) value);
						}
						else if (key.equals("published")) {
							i.setPublished((Boolean) value);
						}
						else if (key.equals("searchable")) {
							i.setSearchable((Boolean) value);
						}
					}
					
					i.save();
				}
				
				if (isFieldValues) {
					keyIter = fieldValues.keySet().iterator();
					while (keyIter.hasNext()) {
						key = keyIter.next();
						value = fieldValues.get(key);
						
						for (FieldForType fft : this.fieldForTypeService.getFieldsForType(i.getType().getId())) {
							if (fft.getField().getVariable().equals(key)) {
								i.setFieldValue(key, value);
								break;
							}
						}
					}
					
					i.saveFieldValues();
				}
			}
		}
		
		resp.
			addMessage("Copy ALL process completed").
			setData(new Object[] {
					currentItemAffected,
					pushItemUpdateRecord(request, last, last, Action.none)});
		
		return resp;		
	}
	
	@RequestMapping(value="/item/update/undo", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public RestResponse undoItemUpdate(HttpServletRequest req, ModelMap model) {	
		return this.itemUpdateUndoService.undo(getItemUpdateHistory(req));
	}

	
	@RequestMapping(value="/item/update/redo", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public RestResponse redoItemUpdate(HttpServletRequest req, ModelMap model) {	
		return this.itemUpdateUndoService.redo(getItemUpdateHistory(req));
	}

	
	@RequestMapping(value="/item/update/clear", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public RestResponse clearItemUpdate(HttpServletRequest req, ModelMap model) {	
		
		ItemUpdateHistory history = getItemUpdateHistory(req);
		history.clear();
		return new RestResponse().addMessage("Item update history cleared").
				setData(new Object[] {new UndoRedoStatus(history), null, 0});
	}

	
	private boolean flagItem(long origId, HttpServletRequest request, boolean reverse) {
		Item i = this.getEditableVersion(origId, getUser(request));
		Map<Long, ItemGist> flaggedItems = getFlaggedItems(request);
		ItemGist ig = flaggedItems.get(i.getOrigId());
		Date now = new Date();
		
		if (! reverse) {
			if (ig == null) {
				flaggedItems.put(i.getOrigId(), new ItemGist(i).setDate(now));
			}
		}
		else {
			if (ig != null) {
				flaggedItems.remove(i.getOrigId());
			}
		}
		
		return ! reverse;
	}
	
}
