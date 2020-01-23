package com.slepeweb.cms.control;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
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
import com.slepeweb.cms.bean.Field.FieldType;
import com.slepeweb.cms.bean.FieldEditorSupport;
import com.slepeweb.cms.bean.FieldForType;
import com.slepeweb.cms.bean.FieldValue;
import com.slepeweb.cms.bean.FieldValueSet;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemIdentifier;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.LinkName;
import com.slepeweb.cms.bean.LinkType;
import com.slepeweb.cms.bean.Media;
import com.slepeweb.cms.bean.RestResponse;
import com.slepeweb.cms.bean.Template;
import com.slepeweb.cms.except.MissingDataException;
import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.cms.json.LinkParams;
import com.slepeweb.cms.service.CookieService;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.cms.service.ItemTypeService;
import com.slepeweb.cms.service.LinkNameService;
import com.slepeweb.cms.service.LinkTypeService;
import com.slepeweb.cms.service.MediaService;
import com.slepeweb.cms.service.TagService;
import com.slepeweb.cms.service.TemplateService;
import com.slepeweb.commerce.bean.Product;
import com.slepeweb.commerce.service.AxisService;

@Controller
@RequestMapping("/rest")
public class RestController extends BaseController {
	private static Logger LOG = Logger.getLogger(RestController.class);
	
	@Autowired private ItemService itemService;
	@Autowired private ItemTypeService itemTypeService;
	@Autowired private TemplateService templateService;
	@Autowired private MediaService mediaService;
	@Autowired private LinkTypeService linkTypeService;
	@Autowired private LinkNameService linkNameService;
	@Autowired private TagService tagService;
	@Autowired private AxisService axisService;
	@Autowired private CookieService cookieService;
	
	/* 
	 * This mapping is used by the main left-hand navigation.
	 * 
	 */
	@RequestMapping("/item/editor")
	public String doItemEditor(ModelMap model, 
			@RequestParam(value="key", required=true) Long id,
			@RequestParam(value="language", required=false) String requestedLanguage,
			HttpServletRequest req, HttpServletResponse res) {	
		
		Item i = this.itemService.getItem(id);
		if (i != null) {
			String lang = chooseLanguage(i.getSite().isMultilingual(), 
					requestedLanguage, i.getSite().getLanguage());
			model.addAttribute("_requestedLanguage", lang);
			
			model.put("editingItem", i);
			model.addAttribute("availableTemplatesForType", i.getSite().getAvailableTemplates(i.getType().getId()));
			
			if (i.isProduct()) {
				model.addAttribute("availableAxes", this.axisService.get());
			}
			
			// Work out form data to build the field editor page
			model.addAttribute("_fieldSupport", fieldEditorSupport(i));
			
			// Store this item's id in a cookie			
			this.cookieService.updateHistoryCookie(i, req, res);
		}
		
		return "cms.item.editor";		
	}
	
	private Map<String, List<FieldEditorSupport>> fieldEditorSupport(Item i) {
		Map<String, FieldValue> languageValuesMap;
		Map<String, List<FieldEditorSupport>> fieldSupport = new HashMap<String, List<FieldEditorSupport>>();
		List<FieldEditorSupport> list;
		FieldValue fv;
		FieldEditorSupport fes;
		String variable;
		
		for (String language : i.getSite().getAllLanguages()) {
			languageValuesMap = i.getFieldValueSet().getFieldValues(language);
			list = new ArrayList<FieldEditorSupport>();
			
			for (FieldForType fft : i.getType().getFieldsForType(false)) {
				if (language.equals(i.getSite().getLanguage()) || fft.getField().isMultilingual()) {
					variable = fft.getField().getVariable();
					
					fes = new FieldEditorSupport().
							setField(fft.getField()).
							setLabel(fft.getField().getName());
					
					fv = languageValuesMap == null ? null : languageValuesMap.get(variable);
					
					if (fft.getField().getType() != FieldType.layout) {
						if (fv == null) {
							fes.setInputTag(fft.getField().getInputTag());
						}
						else {
							fes.setFieldValue(fv);
							fes.setInputTag(fv.getInputTag());
						}
					}
					
					list.add(fes);
				}
			}
			
			fieldSupport.put(language, list);
		}
		
		return fieldSupport;
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
	@RequestMapping(value="/item/{itemId}/update/core", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public RestResponse updateItemCore(
			@PathVariable long itemId, HttpServletRequest req, ModelMap model) {	
		
		RestResponse resp = new RestResponse();
		Item i = this.itemService.getItem(itemId);
		Template t = this.templateService.getTemplate(getLongParam(req, "template"));
		
		if (i != null) {
			i = i.setName(getParam(req, "name")).
				setSimpleName(getParam(req, "simplename")).
				setDateUpdated(new Timestamp(System.currentTimeMillis())).
				setSearchable(getBooleanParam(req, "searchable")).
				setPublished(getBooleanParam(req, "published")).
				setTemplate(t);
			
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
			}
			catch (Exception e) {
				return resp.setError(true).addMessage(e.getMessage());		
			}
			
			List<String> existingTags = i.getTags();
			String tagStr = getParam(req, "tags");
			List<String> latestTags = Arrays.asList(tagStr.split("[ ,]+"));
			if (existingTags.size() != latestTags.size() || ! existingTags.containsAll(latestTags)) {
				this.tagService.save(i, tagStr);
				resp.addMessage("Tags updated");
			}
			
			return resp.setError(false);
		}
				
		return resp.setError(true).addMessage(String.format("No item found with id %d", itemId));		
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
	
	@RequestMapping(value="/item/{itemId}/update/media", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public RestResponse updateItemMedia(
			@PathVariable Long itemId, 
			@RequestParam("media") MultipartFile file, 
			ModelMap model) {	
		
		RestResponse resp = new RestResponse();
		InputStream is = null;
		
		try {
			is = file.getInputStream();
			Item i = this.itemService.getItem(itemId);
			if (i != null) {
				Media m = CmsBeanFactory.makeMedia().
						setItemId(itemId).
						setInputStream(is).
						setSize(file.getSize());
					
				// Save the media item
				try {
					this.mediaService.save(m);
				}
				catch (ResourceException e) {
					String s = "Missing media data - not saved";
					LOG.error(s, e);
					return resp.setError(true).addMessage(s);		
				}
				
				// Update the timestamp on the owning item
				try {
					i.setDateUpdated(new Timestamp(System.currentTimeMillis()));
					i.save();
				}
				catch (ResourceException e) {
					String s = "Missing item data ??? - not saved";
					LOG.error(s, e);
					return resp.setError(true).addMessage(s);		
				}
				
				return resp.setError(false).addMessage("Media successfully uploaded");
			}
			
			return resp.setError(true).addMessage(String.format("No item found with id %d", itemId));		
		}
		catch (IOException e) {
			String s = "Failed to get input stream for media upload";
			LOG.error(s, e);
			return resp.setError(true).addMessage(s);		
		}		
	}
	
	@RequestMapping(value="/item/{itemId}/update/fields", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public RestResponse updateFields(@PathVariable long itemId, HttpServletRequest request, ModelMap model) {	
		
		RestResponse resp = new RestResponse();
		Item i = this.itemService.getItem(itemId);
		String param, stringValue, dateValueStr = null, timeValueStr = null;
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
			// Only interested in multilingual fields for additional languages
			if (i.getSite().isMultilingual() && 
					! (language.equals(i.getSite().getLanguage()) || fft.getField().isMultilingual())) {
				
				continue;
			}
			
			// For this field, see if there is a matching query parameter
			param = fft.getField().getVariable();
			ft = fft.getField().getType();
			fv = fvs.getFieldValueObj(param, language);
			stringValue = request.getParameter(param);
			
			if (ft == FieldType.date || ft == FieldType.datetime) {
				/* 
				 * For a datetime field, there will be 2 query parameters, eg. for a field 
				 * named 'datepublished', param 'datepublished_d' will hold the date value,
				 * and param 'datepublished_t' will hold the time.
				 */
				dateValueStr = request.getParameter(String.format("%s_d", param));
				timeValueStr = null;
				
				if (ft == FieldType.datetime) {
					timeValueStr = request.getParameter(String.format("%s_t", param));
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
				
				if (ft == FieldType.integer) {
					fv.setValue(Integer.parseInt(stringValue));
				}
				else if (ft == FieldType.date || ft == FieldType.datetime) {
					if (ft == FieldType.date) {
						sdf = new SimpleDateFormat("yyyy-MM-dd");
					}
					else {
						sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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
		
		// Update dateUpdated for the item
		i.resetDateUpdated();
		
		if (isErrors) {
			for (String s : errors) {
				resp.addMessage(s);		
			}
		}
		else {
			try {
				i.save();
				resp.addMessage(String.format("%d fields updated", c));
			}
			catch (ResourceException e) {
				resp.setError(true).addMessage("Item could not be saved: missing data");					
			}
		}
		
		return resp;
	}	
	
	@RequestMapping(value="/item/{itemId}/add", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public RestResponse addItem(
			@PathVariable long itemId, 
			@RequestParam("template") long templateId, 
			@RequestParam("itemtype") long itemTypeId, 
			@RequestParam("name") String name, 
			@RequestParam("simplename") String simplename, 
			@RequestParam(value="partNum", required=false) String partNum, 
			@RequestParam(value="price", required=false) Long price, 
			@RequestParam(value="stock", required=false) Long stock, 
			@RequestParam(value="alphaaxis", required=false) Long alphaAxisId, 
			@RequestParam(value="betaaxis", required=false) Long betaAxisId, 
			ModelMap model) {	
		
		RestResponse resp = new RestResponse();
		Template t = null;
		if (templateId > 0) {
			t = this.templateService.getTemplate(templateId);
			if (t != null) {
				itemTypeId = t.getItemTypeId();
			}
		}
		
		ItemType it = this.itemTypeService.getItemType(itemTypeId);
		Item parent = this.itemService.getItem(itemId);
		 
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
		}
		catch (Exception e) {
			return resp.setError(true).addMessage(e.getMessage()).setData(itemId);
		}
		
		return resp.setError(false).addMessage("Item added").setData(i.getId());
	}
	
	@RequestMapping(value="/item/{itemId}/copy", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public RestResponse copyItem(
			@PathVariable long itemId, 
			@RequestParam(value="name", required=true) String name, 
			@RequestParam(value="simplename", required=true) String simplename, 
			ModelMap model) {	
		
		RestResponse resp = new RestResponse();
		Item i = this.itemService.getItem(itemId);
		
		try {
			Item c = this.itemService.copy(i, name, simplename);	
			return resp.setError(c == null).addMessage("Item copied").setData(c.getId());
		}
		catch (Exception e) {
			return resp.setError(true).addMessage(e.getMessage()).setData(itemId);
		}		
	}
	
	@RequestMapping(value="/item/{itemId}/version", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public RestResponse versionItem(
			@PathVariable long itemId, 
			ModelMap model) {	
		
		RestResponse resp = new RestResponse();
		Item i = this.itemService.getItem(itemId);
		
		try {
			Item c = this.itemService.version(i);			
			return resp.setError(false).setData(c.getId()).addMessage("New version created");
		}
		catch (Exception e) {
			return resp.setError(true).setData(i.getId()).addMessage(e.getMessage());
		}
	}
	
	@RequestMapping(value="/item/{itemId}/revert", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public RestResponse revertItem(
			@PathVariable long itemId, 
			ModelMap model) {	
		
		RestResponse resp = new RestResponse();
		Item i = this.itemService.getItem(itemId);
		
		if (i != null) {
			try {
				Item r = this.itemService.revert(i);
				return resp.setError(false).addMessage("Item reverted to previous version").setData(r.getId());
			}
			catch (ResourceException e) {
				return resp.setError(true).addMessage(String.format("No item with this id", itemId));
			}
		}
		
		return resp.setError(true).addMessage(String.format("No item with this id", itemId));
	}
	
	@RequestMapping(value="/item/{itemId}/trash", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public RestResponse trashItem(@PathVariable long itemId, ModelMap model) {	
		
		RestResponse resp = new RestResponse();
		Item i = this.itemService.getItem(itemId);
		Item parent = i.getParent();
		i.trash();
			
		return resp.setError(false).addMessage("Item trashed").setData(parent.getId());
	}
	
	@RequestMapping(value="/trash/get")
	public String getTrashedItems(ModelMap model) {			
		model.put("_trashContents", this.itemService.getTrashedItems());			
		return "cms.trash.contents";
	}
	
	@RequestMapping(value="/trash/empty/all", produces="application/json")
	@ResponseBody
	public RestResponse deleteAllTrashedItems(ModelMap model) {	
		
		RestResponse resp = new RestResponse();
		int num = this.itemService.deleteTrashedItems(null);			
		return resp.setError(false).addMessage(String.format("Emptied %d items from the trash", num));
	}
	
	@RequestMapping(value="/trash/restore/selected", produces="application/json")
	@ResponseBody
	public RestResponse restoreSelectedTrashedItems(
			@RequestParam(value="id", required=true) String idList,
			ModelMap model) {	
		
		RestResponse resp = new RestResponse();

		if (idList.endsWith(",")) {
			idList = idList.substring(0, idList.length() - 1);
		}
		
		if (StringUtils.isNotBlank(idList)) {
			String[] idStr = idList.split(",");
			int len = idStr.length;
		
			long[] ids = new long[len];
			for (int i = 0; i < len; i++) {
				ids[i] = Integer.parseInt(idStr[i]);
			}
			
			return resp.setError(false).addMessage(
					String.format("Restored %d items from the trash", this.itemService.restoreSelectedItems(ids)));
		}
		else {
			return resp.setError(true).addMessage("No items selected by user");
		}
	}
	
	@RequestMapping(value="/trash/restore/all", produces="application/json")
	@ResponseBody
	public RestResponse restoreAllTrashedItems(ModelMap model) {	
		
		RestResponse resp = new RestResponse();
		int num = this.itemService.restoreSelectedItems(null);		
		return resp.setError(false).addMessage(String.format("Restored %d items from the trash", num));
	}
	
	@RequestMapping(value="/trash/empty/selected", produces="application/json")
	@ResponseBody
	public RestResponse deleteSelectedTrashedItems(
			@RequestParam(value="id", required=true) String idList,
			ModelMap model) {	
		
		RestResponse resp = new RestResponse();

		if (idList.endsWith(",")) {
			idList = idList.substring(0, idList.length() - 1);
		}
		
		if (StringUtils.isNotBlank(idList)) {
			String[] idStr = idList.split(",");
			int len = idStr.length;
		
			long[] ids = new long[len];
			for (int i = 0; i < len; i++) {
				ids[i] = Integer.parseInt(idStr[i]);
			}
			
			return resp.setError(false).addMessage(
					String.format("Emptied %d items from the trash bin", this.itemService.deleteTrashedItems(ids)));
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
			@RequestParam(value="moverIsShortcut", required=true) boolean moverIsShortcut,
			@RequestParam(value="mode", required=true) String mode,
			ModelMap model) {	
		
		RestResponse resp = new RestResponse();
		Item mover = this.itemService.getItem(moverId);
		if (mover.isRoot()) {
			return resp.setError(true).setData(mover.getId()).addMessage("Cannot move the root item");
		}
		
		Item target = this.itemService.getItem(targetId);
		Item currentParent = this.itemService.getItem(moverParentId);
		Item targetParent = this.itemService.getItem(targetParentId);
		
		try {
			mover.move(currentParent, targetParent, target, moverIsShortcut, mode);		
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
	public RestResponse saveLinks(@RequestBody LinkParams[] linkParams, @PathVariable long parentId, ModelMap model) {	

		RestResponse resp = new RestResponse();
		Item parent = this.itemService.getItem(parentId);
		List<Link> links = new ArrayList<Link>();
		Link l;
		Item i;
		
		for (LinkParams lp : linkParams) {
			l = CmsBeanFactory.makeLink().
					setParentId(lp.getParentId()).
					setName(lp.getName()).
					setOrdering(lp.getOrdering()).
					setType(lp.getType()).
					setData(lp.getData());
			
			i = CmsBeanFactory.makeItem(null).
					setId(lp.getChildId());
			
			l.setChild(i);			
			links.add(l);
		}
		
		parent.setLinks(links);
		
		try {
			parent.saveLinks();		
			return resp.setError(false).addMessage(String.format("%d links saved", links.size()));
		}
		catch (ResourceException e) {
			return resp.setError(true).addMessage(e.getMessage());
		}
	}
	
	@RequestMapping(value="/linknames/{parentId}/{linkType}", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public List<String> getLinkNameOptions(
			@PathVariable long parentId, @PathVariable String linkType, ModelMap model) {	
		
		List<String> names = new ArrayList<String>();
		
		if (! linkType.equals("unknown")) {
			LinkType lt = this.linkTypeService.getLinkType(linkType);
			
			if (lt != null) {
				Item parent = this.itemService.getItem(parentId);		
			
				for (LinkName ln : this.linkNameService.getLinkNames(parent.getSite().getId(), lt.getId())) {
					names.add(ln.getName());
				}
			}
		}
		
		return names;
	}
	
	@RequestMapping(value="/item/{itemId}/name", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public String getItemName(@PathVariable long itemId, ModelMap model) {	
		Item i =  this.itemService.getItem(itemId);
		if (i != null) {
			return i.getName();
		}
		return "n/a";
	}
	
	@RequestMapping(value="/item/history/{siteId}", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public List<ItemIdentifier> history(@PathVariable long siteId, HttpServletRequest req) {	
		
		return this.cookieService.getHistoryCookieValue(siteId, req);
	}

}
