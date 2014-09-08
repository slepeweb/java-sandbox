package com.slepeweb.site.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.slepeweb.cms.component.Config;

@Controller
public class BaseController {
	
	@Autowired protected Config config;

	@ModelAttribute(value="config")
	public Config getConfig() {
		this.config.setLiveDelivery(false);
		return this.config;
	}
	
}
