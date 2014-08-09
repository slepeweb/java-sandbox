package com.slepeweb.cms.control;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cms")
public class EditorPageController extends BaseController {
	
	@RequestMapping(value="/editor")	
	public String doMain(ModelMap model) {		

		return "cmseditor";
	}
	
}
