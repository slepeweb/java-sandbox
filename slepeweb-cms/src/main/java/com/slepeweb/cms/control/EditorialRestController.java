package com.slepeweb.cms.control;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slepeweb.cms.bean.FieldValue;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Field.FieldType;
import com.slepeweb.cms.service.ItemService;

@Controller
@RequestMapping("/rest/cms")
public class EditorialRestController extends BaseController {
	
	@Autowired private ItemService itemService;
	
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
}
