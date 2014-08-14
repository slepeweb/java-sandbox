package com.slepeweb.cms.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.cms.service.ItemService;

@Controller
@RequestMapping("/cms")
public class EditorPageController extends BaseController {
	
	@Autowired private ItemService itemService;
	
	@RequestMapping(value="/editor")	
	public String doMain(ModelMap model) {		
		return "cms.editor";
	}
	
	@RequestMapping(value="/editor/{itemId}")	
	public String doWithItem(@PathVariable long itemId, ModelMap model) {		
		model.addAttribute("editingItem", this.itemService.getItem(itemId));
		return "cms.editor";
	}
	
}
