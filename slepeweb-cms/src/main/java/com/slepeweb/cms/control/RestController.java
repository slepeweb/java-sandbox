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
import java.util.List;

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
import com.slepeweb.cms.bean.Field.FieldType;
import com.slepeweb.cms.bean.FieldForType;
import com.slepeweb.cms.bean.FieldValue;
import com.slepeweb.cms.bean.FieldValueSet;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemIdentifier;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.LinkName;
import com.slepeweb.cms.bean.LinkNameOption;
import com.slepeweb.cms.bean.LinkType;
import com.slepeweb.cms.bean.Media;
import com.slepeweb.cms.bean.RestResponse;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.SolrParams4Cms;
import com.slepeweb.cms.bean.Template;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.bean.guidance.IValidator;
import com.slepeweb.cms.component.Navigation.Node;
import com.slepeweb.cms.except.MissingDataException;
import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.cms.json.LinkParams;
import com.slepeweb.cms.service.CookieService;
import com.slepeweb.cms.service.SolrService4Cms;
import com.slepeweb.cms.service.ValidationService;
import com.slepeweb.commerce.bean.Product;
import com.slepeweb.common.solr.bean.SolrConfig;
import com.slepeweb.common.util.DateUtil;
import com.slepeweb.common.util.HttpUtil;
import com.slepeweb.common.util.ImageUtil;

@Controller
@RequestMapping("/rest")
public class RestController extends BaseController {
	private static Logger LOG = Logger.getLogger(RestController.class);
	public static final String THUMBNAIL_EXT = "-thumb";
	
	@Autowired private CookieService cookieService;
	@Autowired private SolrService4Cms solrService4Cms;
	@Autowired private NavigationController navigationController;
	@Autowired private ValidationService validationService;
	
	/* 
	 * This mapping is used by the main left-hand navigation.
	 * 
	 */
	@RequestMapping("/item/editor")
	public String doItemEditor(ModelMap model, 
			@RequestParam(value="key", required=true) Long origId,
			@RequestParam(value="language", required=false) String requestedLanguage,
			HttpServletRequest req, HttpServletResponse res) {	
		
		Item i = this.getEditableVersion(origId, getUser(req));
		
		if (i != null) {
			String lang = chooseLanguage(i.getSite().isMultilingual(), requestedLanguage, i.getSite().getLanguage());
			model.addAttribute("_requestedLanguage", lang);
			
			model.put("editingItem", i);
			model.put("allVersions", i.getAllVersions());
			model.addAttribute("availableTemplatesForType", i.getSite().getAvailableTemplates(i.getType().getId()));
			
			// Host to render content
			model.addAttribute("_host", i.getSite().getPublicStagingHost());
			
			if (i.isProduct()) {
				model.addAttribute("availableAxes", this.cmsService.getAxisService().get());
			}
			
			// Work out form data to build the field editor page
			model.addAttribute("_fieldSupport", fieldEditorSupport(i));
			
			// Store this item's id in a cookie			
			this.cookieService.updateBreadcrumbsCookie(i, req, res);
			
			// Last relative position selection for 'addnew'
			model.addAttribute("_lastRelativePosition", this.cookieService.getRelativePositionCookieValue(req));
			
			// Total number of editable items in this section
			model.addAttribute("_numItemsInSection", this.cmsService.getItemService().getCountByPath(i));
			
			// Get recently-used tags, and full list of tags for the site
			model.addAttribute(TAG_INPUT_SUPPORT_ATTR, getTagInfo(i.getSite().getId(), req));
		}
		
		return "cms.item.editor";		
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
		
		RestResponse resp = new RestResponse();
		User u = getUser(req);
		Item i = getEditableVersion(origId, u, true);
		Template t = this.cmsService.getTemplateService().getTemplate(getLongParam(req, "template"));
		Object[] data = new Object[3];
		
		if (i != null) {
			boolean wasPublished = i.isPublished();
			String oldName = i.getName();
			
			i = i.setName(getParam(req, "name")).
				setSimpleName(getParam(req, "simplename")).
				setDateUpdated(new Timestamp(System.currentTimeMillis())).
				setSearchable(getBooleanParam(req, "searchable")).
				setPublished(getBooleanParam(req, "published")).
				setTemplate(t);
			
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
				
				LOG.info(userLog(u, "updated core data for", i));
				
				// Navigation node with title assigned to saved item name IF changed
				data[0] = i.getName().equals(oldName) ? null : i.getName();
				
				// Boolean value assigned IF version 1 has been published
				data[1] = i.getVersion() == 1 && ! wasPublished && i.isPublished();
				
				// Boolean value assigned IF published status has changed
				data[2] = wasPublished ^ i.isPublished();
				
				resp.setData(data);
			}
			catch (Exception e) {
				return resp.setError(true).addMessage(e.getMessage());		
			}
			
			// Save item tags, if changes have been made
			saveTags(i, req, resp);
			
			return resp;
		}
				
		return resp.setError(true).addMessage(String.format("No item found with id %d", origId));		
	}
	
	private void saveTags(Item i, HttpServletRequest req, RestResponse resp) {
		List<String> existingTagValues = i.getTagValues();
		String tagStr = getParam(req, "tags");
		List<String> latestTagValues = Arrays.asList(tagStr.split("[ ,]+"));
		if (existingTagValues.size() != latestTagValues.size() || ! existingTagValues.containsAll(latestTagValues)) {
			this.cmsService.getTagService().save(i, tagStr);
			resp.addMessage("Tags updated");
			
			// Identify recently-applied tags - remove existing tags from latest, and see what's left.
			List<String> freshTagValues = new ArrayList<String>();
			for (String v : latestTagValues) {
				if (! existingTagValues.contains(v)) {
					freshTagValues.add(v);
				}
			}
			
			// latestTagValues now contains new selections
			if (freshTagValues.size() > 0) {
				@SuppressWarnings("unchecked")
				List<String> storedTagValues = (List<String>) req.getSession().getAttribute(RECENT_TAGS_ATTR);
				
				// Filter out duplicates
				for (String v : storedTagValues) {
					if (! freshTagValues.contains(v)) {
						freshTagValues.add(v);
					}
				}
				
				// And store result
				req.getSession().setAttribute(RECENT_TAGS_ATTR, freshTagValues);
			}
		}
	}
	
	private String getParam(HttpServletRequest req, String name) {
		return req.getParameter(name);
	}
	
	private Long getLongParam(HttpServletRequest req, String name) {
		return Long.valueOf(getParam(req, name));
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
				try {
					if (thumbnailAction.equals("onlythumb")) {
						InputStream is = ImageUtil.scaleImage(file.getInputStream(), thumbnailWidth, -1, "jpg");
						saveMedia(i.getId(), is, true);
					}
					else {
						saveMedia(i.getId(), file.getInputStream(), false);
						
						if (thumbnailAction.equals("autoscale") && i.getType().isImage()) {
							InputStream is = ImageUtil.scaleImage(file.getInputStream(), thumbnailWidth, -1, "jpg");
							saveMedia(i.getId(), is, true);
						}
					}
					
					// Update the timestamp on the owning item
					try {
						i.setDateUpdated(new Timestamp(System.currentTimeMillis()));
						i.save();
					}
					catch (ResourceException e) {
						LOG.error(msg = "Missing item data ??? - not saved", e);
						return resp.setError(true).addMessage(msg);		
					}
					
					LOG.info(msg = "Media successfully uploaded");
					return resp.setError(false).addMessage(msg);
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
	
	private boolean saveMedia(Long itemId, InputStream is, boolean isThumbnail) {
		try {
			Media m = CmsBeanFactory.makeMedia().
					setItemId(itemId).
					setUploadStream(is).
					setThumbnail(isThumbnail);
			
			this.cmsService.getMediaService().save(m);
			is.close();
			
			return true;
		}
		catch (Exception e) {
			LOG.error("Failed to save media", e);
		}
		
		return false;
	}
	
	@RequestMapping(value="/item/{origId}/update/fields", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public RestResponse updateFields(@PathVariable long origId, HttpServletRequest request, ModelMap model) {	
		
		RestResponse resp = new RestResponse();
		Item i = getEditableVersion(origId, getUser(request), true);
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
				if (! validateFieldValue(fv, errors)) {
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
				
				resp.addMessage(String.format("%d fields updated", c));
			}
			catch (ResourceException e) {
				resp.setError(true).addMessage("Item could not be saved: missing data");					
			}
		}
		
		return resp;
	}	
	
	private boolean validateFieldValue(FieldValue fv, List<String> errors) {
		IValidator iv = this.validationService.get(fv.getField().getValidatorClass());
		
		if (iv != null) {
			if (! iv.validate(fv.getStringValue())) {
				errors.add(String.format("Field value '%s' fails validation", fv.getStringValue()));
				return false;
			}
			
			// Clean up input, if necessary
			fv.setStringValue(iv.clean(fv.getStringValue()));
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
		
		this.cookieService.saveCookie(CookieService.RELATIVE_POSITION_NAME, relativePosition, CookieService.CMS_COOKIE_PATH, res);
		
		Template t = null;
		if (templateId > 0) {
			t = this.cmsService.getTemplateService().getTemplate(templateId);
			if (t != null) {
				itemTypeId = t.getItemTypeId();
			}
		}
		
		ItemType it = this.cmsService.getItemTypeService().getItemType(itemTypeId);
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
				p.save();
			}
			else {
				i.save();
			}
			
			LOG.info(userLog(u, "saved item", i));
			
			Object[] o = new Object[]{Node.toNode(i), i.isShortcut(), i.getType().isMedia()};		
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
			Item c = this.cmsService.getItemService().copy(i, name, simplename);	
			if (c != null) {
				Node n = Node.toNode(c);
				resp.addMessage("Item copied").setData(n);
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
			Item c = this.cmsService.getItemService().version(i);			
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
				Item r = this.cmsService.getItemService().revert(i);
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
		Item parent = i.getParent();
		i.trash();
			
		return resp.addMessage("Item trashed").addMessage("PARENT ITEM IS NOW CURRENT").setData(parent.getOrigId());
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
			return resp.addMessage(String.format("Section publication status updated, total %d items", count));
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
				return resp.addMessage(String.format("Section re-indexed, total %d items", count));
			}
			else {
				count = actionSection(i, "searchable", searchableOption.equals("searchable"));
				return resp.addMessage(String.format("Section searchability actioned, total %d items updated", count));
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
		
		if (StringUtils.isNotBlank(origIdList)) {
			String[] origIdStr = origIdList.split(",");
			int len = origIdStr.length;
		
			long[] origIds = new long[len];
			for (int i = 0; i < len; i++) {
				origIds[i] = Integer.parseInt(origIdStr[i]);
			}
			
			return resp.setError(false).addMessage(
					String.format("Restored %d items from the trash", this.cmsService.getItemService().restoreSelectedItems(origIds)));
		}
		else {
			return resp.setError(true).addMessage("No items selected by user");
		}
	}
	
	@RequestMapping(value="/trash/restore/all", produces="application/json")
	@ResponseBody
	public RestResponse restoreAllTrashedItems(ModelMap model) {	
		
		RestResponse resp = new RestResponse();
		int num = this.cmsService.getItemService().restoreSelectedItems(null);		
		return resp.setError(false).addMessage(String.format("Restored %d items from the trash", num));
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
			@RequestParam(value="targetParentId", required=true) Long targetParentId,
			@RequestParam(value="moverParentId", required=true) Long moverParentId,
			@RequestParam(value="mode", required=true) String mode,
			HttpServletRequest req,
			ModelMap model) {	
		
		RestResponse resp = new RestResponse();
		User u = getUser(req);
		Item mover = getEditableVersion(moverId, u, true);
		if (mover.isRoot()) {
			return resp.setError(true).setData(mover.getId()).addMessage("Cannot move the root item");
		}
		
		Item target = getEditableVersion(targetId, u, true);
		Item currentParent = getEditableVersion(moverParentId, u, true);
		Item targetParent = getEditableVersion(targetParentId, u, true);
		
		try {
			mover.move(currentParent, targetParent, target, mode);		
			return resp.setError(false).setData(mover.getId()).addMessage("Item moved");
		}
		catch (MissingDataException e) {
			return resp.setError(true).setData(mover.getId()).addMessage(e.getMessage());
		}
		catch (ResourceException e) {
			return resp.setError(true).setData(mover.getId()).addMessage(e.getMessage());
		}
	}
	
	@RequestMapping(value="/links/{parentId}/save", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public RestResponse saveLinks(
			@RequestBody LinkParams[] linkParams, 
			@PathVariable long parentId, 
			HttpServletRequest req, 
			ModelMap model) {	

		RestResponse resp = new RestResponse();
		User u = getUser(req);
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
			resp.addMessage(String.format("%d links saved", links.size()));
			
			Object[] response = new Object[] {
					this.navigationController.doLazyNavOneLevel(parent.getOrigId(), parent.getSite().getId(), req),
					shortcutAction,
					shortCutTargetType == null ? null : shortCutTargetType.toLowerCase()
			};
			// Return a list of shortcuts as response data, so that leftnav tree can be refreshed
			resp.setData(response);
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
			IValidator iv;
			
			if (lt != null) {
				for (LinkName ln : this.cmsService.getLinkNameService().getLinkNames(siteId, lt.getId())) {
					iv = this.validationService.get(ln.getValidatorClass());
					options.add(new LinkNameOption(ln.getName(), iv));
				}
			}
		}
		
		model.addAttribute("_linknameOptions", options);
		return "cms.linknameOptions";
	}
	
	@RequestMapping(value="/item/{origId}/name", method=RequestMethod.POST, produces="application/json")
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
	
}
