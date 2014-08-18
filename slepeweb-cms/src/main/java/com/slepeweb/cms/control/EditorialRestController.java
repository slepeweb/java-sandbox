package com.slepeweb.cms.control;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slepeweb.cms.bean.Field.FieldType;
import com.slepeweb.cms.bean.CmsBeanFactory;
import com.slepeweb.cms.bean.FieldValue;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.Template;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.cms.service.ItemTypeService;
import com.slepeweb.cms.service.SiteService;
import com.slepeweb.cms.service.TemplateService;

@Controller
@RequestMapping("/rest/cms")
public class EditorialRestController extends BaseController {
	
	@Autowired private SiteService siteService;
	@Autowired private ItemService itemService;
	@Autowired private ItemTypeService itemTypeService;
	@Autowired private TemplateService templateService;
	
	@RequestMapping("/item-editor")
	public String doItemEditor(ModelMap model, @RequestParam(value="key", required=true) Long id) {	
		model.put("requestItem", this.itemService.getItem(id));
		return "cms.item.editor";		
	}
	
	@RequestMapping(value="/item/{itemId}/update/core", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public boolean updateItemCore(
			@PathVariable long itemId, 
			@RequestParam("name") String name, 
			@RequestParam("simplename") String simplename, 
			ModelMap model) {	
		
		Item i = this.itemService.getItem(itemId);
		if (i != null) {
			i.setName(name);
			i.setSimpleName(simplename);
			i.setDateUpdated(new Timestamp(System.currentTimeMillis()));
			i.save();
			
			return true;
		}
		return false;		
	}
	
	@RequestMapping(value="/item/{itemId}/update/fields", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public boolean updateFields(@PathVariable long itemId, HttpServletRequest request, ModelMap model) {	
		
		Item i = this.itemService.getItem(itemId);
		String param, stringValue;
		FieldType ft;
		
		for (FieldValue fv : i.getFieldValues()) {
			param = fv.getField().getVariable();
			ft = fv.getField().getType();
			stringValue = request.getParameter(param);
			
			if (stringValue != null) {
				if (ft == FieldType.integer) {
					fv.setValue(Integer.parseInt(stringValue));
				}
				else if (ft == FieldType.date) {
					// TODO: complete
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
				setSite((Site) model.get("site")).
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
			@RequestParam(value="mode", required=true) String mode,
			ModelMap model) {	
		
		Item mover = this.itemService.getItem(itemId);
		Item target = this.itemService.getItem(targetId);
		Item moved = null;
		
		if (mode.equals("before") || mode.equals("after")) {
			// TODO: move into parent folder, and position 'before' or 'after' the target item
			moved = mover.move(target.getParent());
		}
		else if (mode.equals("over")) {
			// TODO: move into parent folder, and place at end of child list
			moved = mover.move(target);
		}
			
		return moved.getId();
	}
	
	@ModelAttribute("site")
	public Site getSite() {
		return this.siteService.getSite("Integration Testing");
	}
}
