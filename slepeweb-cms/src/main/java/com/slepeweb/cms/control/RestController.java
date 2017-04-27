package com.slepeweb.cms.control;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.LinkName;
import com.slepeweb.cms.bean.LinkType;
import com.slepeweb.cms.bean.Media;
import com.slepeweb.cms.bean.RestResponse;
import com.slepeweb.cms.bean.Template;
import com.slepeweb.cms.except.DuplicateItemException;
import com.slepeweb.cms.except.MissingDataException;
import com.slepeweb.cms.except.NotRevertableException;
import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.cms.json.LinkParams;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.cms.service.ItemTypeService;
import com.slepeweb.cms.service.LinkNameService;
import com.slepeweb.cms.service.LinkTypeService;
import com.slepeweb.cms.service.MediaService;
import com.slepeweb.cms.service.TagService;
import com.slepeweb.cms.service.TemplateService;

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
	
	@RequestMapping("/item/editor")
	public String doItemEditor(ModelMap model, @RequestParam(value="key", required=true) Long id) {	
		Item i = this.itemService.getItem(id);
		if (i != null) {
			model.put("editingItem", i);
			model.addAttribute("availableTemplatesForType", i.getSite().getAvailableTemplates(i.getType().getId()));
		}
		return "cms.item.editor";		
	}
	
	@RequestMapping(value="/item/{itemId}/update/core", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public RestResponse updateItemCore(
			@PathVariable long itemId, 
			@RequestParam("name") String name, 
			@RequestParam("simplename") String simplename, 
			@RequestParam("searchable") boolean searchable, 
			@RequestParam("published") boolean published, 
			@RequestParam("template") Long templateId, 
			@RequestParam("tags") String tagStr, 
			ModelMap model) {	
		
		RestResponse resp = new RestResponse();
		Item i = this.itemService.getItem(itemId);
		Template t = this.templateService.getTemplate(templateId);
		
		if (i != null) {
			i = i.setName(name).
				setSimpleName(simplename).
				setDateUpdated(new Timestamp(System.currentTimeMillis())).
				setSearchable(searchable).
				setPublished(published).
				setTemplate(t);
			
			try {
				i.save();
				resp.addMessage("Core item data successfully updated");
			}
			catch (Exception e) {
				return resp.setError(true).addMessage(e.getMessage());		
			}
			
			List<String> existingTags = i.getTags();
			List<String> latestTags = Arrays.asList(tagStr.split("[ ,]+"));
			if (existingTags.size() != latestTags.size() || ! existingTags.containsAll(latestTags)) {
				this.tagService.save(i, tagStr);
				resp.addMessage("Tags updated");
			}
			
			return resp.setError(false);
		}
				
		return resp.setError(true).addMessage(String.format("No item found with id %d", itemId));		
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
				catch (MissingDataException e) {
					String s = "Missing media data - not saved";
					LOG.error(s, e);
					return resp.setError(true).addMessage(s);		
				}
				
				// Update the timestamp on the owning item
				try {
					i.setDateUpdated(new Timestamp(System.currentTimeMillis()));
					i.save();
				}
				catch (MissingDataException e) {
					String s = "Missing item data ??? - not saved";
					LOG.error(s, e);
					return resp.setError(true).addMessage(s);		
				}
				catch (DuplicateItemException e) {
					// Shouldn't ever happen for this update
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
		
		// Identify FieldValue objects for this item
		Map<String, FieldValue> fieldValuesMap = i.getFieldValuesMap();
		
		// Build a list of FieldValue objects that need to be saved
		List<FieldValue> fvList2Save = new ArrayList<FieldValue>();
		
		// Store error messages
		List<String> errors = new ArrayList<String>();
		boolean isErrors = false;
		
		// Loop through fields for this item type
		for (FieldForType fft : i.getType().getFieldsForType()) {
			// For this field, see if there is a matching query parameter
			param = fft.getField().getVariable();
			ft = fft.getField().getType();
			fv = fieldValuesMap.get(param);
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
							setValue(fft.getField().getDefaultValueObject());
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
				catch (MissingDataException e) {
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
			catch (MissingDataException e) {
				resp.setError(true).addMessage("Item could not be saved: missing data");					
			}
			catch (DuplicateItemException e) {
				resp.setError(true).addMessage(e.getMessage());					
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
		 
		Item i = CmsBeanFactory.makeItem().
				setSite(parent.getSite()).
				setPath(String.format("%s/%s", parent.getPath(), simplename)).
				setTemplate(t).
				setType(it).
				setName(name).
				setSimpleName(simplename).
				setDateCreated(new Timestamp(System.currentTimeMillis())).
				setDeleted(false);
		
		i.setDateUpdated(i.getDateCreated());
		
		try {
			i.save();
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
			catch (NotRevertableException e) {
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
	
	@RequestMapping(value="/item/{itemId}/move", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public RestResponse moveItem(
			@PathVariable long itemId,
			@RequestParam(value="targetId", required=true) Long targetId,
			@RequestParam(value="parentId", required=true) Long parentId,
			@RequestParam(value="shortcut", required=true) boolean shortcut,
			@RequestParam(value="mode", required=true) String mode,
			ModelMap model) {	
		
		RestResponse resp = new RestResponse();
		Item mover = this.itemService.getItem(itemId);
		Item target = this.itemService.getItem(targetId);
		Item currentParent = this.itemService.getItem(parentId);
		
		try {
			mover.move(currentParent, target, shortcut, mode);		
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
					setType(lp.getType());
			
			i = CmsBeanFactory.makeItem().
					setId(lp.getChildId());
			
			l.setChild(i);			
			links.add(l);
		}
		
		parent.setLinks(links);
		
		try {
			parent.saveLinks();		
			return resp.setError(false).addMessage(String.format("%d links saved", links.size()));
		}
		catch (MissingDataException e) {
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
}
