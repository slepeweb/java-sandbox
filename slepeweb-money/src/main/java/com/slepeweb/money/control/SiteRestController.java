package com.slepeweb.money.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/rest")
public class SiteRestController extends BaseController {
	//private static Logger LOG = Logger.getLogger(SiteRestController.class);

	@RequestMapping(value="/sitemap", method=RequestMethod.GET, produces="text/plain")
	@ResponseBody
	public String example() {	

		return "eg. list of urls in sitemap";		
	}	
}
