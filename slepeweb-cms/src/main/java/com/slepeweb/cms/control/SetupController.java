package com.slepeweb.cms.control;

import java.net.URL;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slepeweb.cms.except.DuplicateItemException;
import com.slepeweb.cms.except.MissingDataException;
import com.slepeweb.cms.setup.SiteSetup;

@Controller
public class SetupController extends BaseController {
	private static Logger LOG = Logger.getLogger(SetupController.class);
	
	@Autowired private SiteSetup siteSetup;
	
	@RequestMapping(value="/setup", produces="text/text")	
	@ResponseBody
	public String initSite(@RequestParam(value="file", required=true) String fileName) {	
		String resource = "/xls/" + fileName;
		URL url = getClass().getClassLoader().getResource(resource);
		if (url != null) {
			try {
				this.siteSetup.load(url.getPath());
			}
			catch (MissingDataException e) {
				LOG.warn("Missing data - site initialisation incomplete");				
			}
			catch (DuplicateItemException e) {
				LOG.warn("Item(s) already exist - site initialisation incomplete");				
			}
			
			return "finished";
		}
		else {
			LOG.warn(String.format("Spreadsheet not found [%s]", resource));
			return String.format("Resource not found [%s]", resource);
		}
	}	
}
