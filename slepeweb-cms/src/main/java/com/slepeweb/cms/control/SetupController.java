package com.slepeweb.cms.control;

import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slepeweb.cms.setup.SiteSetup;

@Controller
public class SetupController extends BaseController {
	
	@Autowired private SiteSetup siteSetup;
	
	@RequestMapping(value="/setup", produces="text/text")	
	@ResponseBody
	public String initSite(@RequestParam(value="file", required=true) String fileName) {	
		String resource = "/xls/" + fileName;
		URL url = getClass().getClassLoader().getResource(resource);
		if (url != null) {
			this.siteSetup.load(url.getPath());
			return "finished";
		}
		else {
			return String.format("Resource not found [%s]", resource);
		}
	}	
}
