package com.slepeweb.cms.control;

import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import com.slepeweb.cms.bean.Template;
import com.slepeweb.cms.json.LinkParams;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.cms.service.ItemTypeService;
import com.slepeweb.cms.service.LinkNameService;
import com.slepeweb.cms.service.LinkTypeService;
import com.slepeweb.cms.service.MediaService;
import com.slepeweb.cms.service.TagService;
import com.slepeweb.cms.service.TemplateService;
import com.slepeweb.cms.utils.LogUtil;

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
	public boolean updateItemCore(
			@PathVariable long itemId, 
			@RequestParam("name") String name, 
			@RequestParam("simplename") String simplename, 
			@RequestParam("published") boolean published, 
			@RequestParam("template") Long templateId, 
			@RequestParam("tags") String tagStr, 
			ModelMap model) {	
		
		Item i = this.itemService.getItem(itemId);
		Template t = this.templateService.getTemplate(templateId);
		
		if (i != null) {
			i = i.setName(name).
				setSimpleName(simplename).
				setDateUpdated(new Timestamp(System.currentTimeMillis())).
				setPublished(published).
				setTemplate(t).
				save();
			
			List<String> existingTags = i.getTags();
			List<String> latestTags = Arrays.asList(tagStr.split("[ ,]+"));
			if (existingTags.size() != latestTags.size() || ! existingTags.containsAll(latestTags)) {
				this.tagService.save(i, tagStr);
			}
			return true;
		}
		return false;		
	}
	
	@RequestMapping(value="/item/{itemId}/update/media", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public boolean updateItemMedia(
			@PathVariable Long itemId, 
			@RequestParam("media") MultipartFile file, 
			ModelMap model) {	
		
		InputStream is = null;
		
		try {
			is = file.getInputStream();
			Item i = this.itemService.getItem(itemId);
			if (i != null) {
				Media m = CmsBeanFactory.makeMedia().
						setItemId(itemId).
						setInputStream(is).
						setSize(file.getSize());
				
				this.mediaService.save(m);
				i.setDateUpdated(new Timestamp(System.currentTimeMillis()));
				i.save();
				return true;
			}
		}
		catch (Exception e) {
			LOG.error("Failed to get input stream for media upload", e);
		}
		
		return false;		
	}
	
	@RequestMapping(value="/item/{itemId}/update/fields", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public boolean updateFields(@PathVariable long itemId, HttpServletRequest request, ModelMap model) {	
		
		Item i = this.itemService.getItem(itemId);
		String param, stringValue;
		FieldType ft;
		FieldValue fv;
		Map<String, FieldValue> fieldValuesMap = i.getFieldValuesMap();
		SimpleDateFormat sdf;
		Timestamp stamp;
		
		for (FieldForType fft : i.getType().getFieldsForType()) {
			param = fft.getField().getVariable();
			ft = fft.getField().getType();
			stringValue = request.getParameter(param);
			fv = fieldValuesMap.get(param);
			
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
				else if (ft == FieldType.date) {
					sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
					try {
						stamp = new Timestamp(sdf.parse(stringValue).getTime());
						stamp.setNanos(0);
						fv.setDateValue(stamp);
						fv.setStringValue(stringValue.replace("T", " "));
					}
					catch (Exception e) {
						LOG.warn(LogUtil.compose("Date not parseable", stringValue));
						continue;
					}
				}
				else {
					fv.setValue(stringValue);
				}
				
				fv.save();
			}
		}
		
		// Update dateUpdated for the item
		i.resetDateUpdated();
		i.save();
		
		return false;		
	}	
	
	@RequestMapping(value="/item/{itemId}/add", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public long addItem(
			@PathVariable long itemId, 
			@RequestParam("template") long templateId, 
			@RequestParam("itemtype") long itemTypeId, 
			@RequestParam("name") String name, 
			@RequestParam("simplename") String simplename, 
			ModelMap model) {	
		
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
		i.save();
			
		return i.getId();
	}
	
	@RequestMapping(value="/item/{itemId}/trash", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public long trashItem(@PathVariable long itemId, ModelMap model) {	
		
		Item i = this.itemService.getItem(itemId);
		Item parent = i.getParent();
		i.trash();
			
		return parent.getId();
	}
	
	@RequestMapping(value="/item/{itemId}/move", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public long moveItem(
			@PathVariable long itemId,
			@RequestParam(value="targetId", required=true) Long targetId,
			@RequestParam(value="parentId", required=true) Long parentId,
			@RequestParam(value="shortcut", required=true) boolean shortcut,
			@RequestParam(value="mode", required=true) String mode,
			ModelMap model) {	
		
		Item mover = this.itemService.getItem(itemId);
		Item target = this.itemService.getItem(targetId);
		Item currentParent = this.itemService.getItem(parentId);
		mover.move(currentParent, target, shortcut, mode);
		
		return mover.getId();
	}
	
	@RequestMapping(value="/links/{parentId}/save", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public String saveLinks(@RequestBody LinkParams[] linkParams, @PathVariable long parentId, ModelMap model) {	
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
		parent.saveLinks();
		
		return "success";
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
