package com.slepeweb.sandbox.acm.mvc.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.slepeweb.sandbox.acm.constants.RequestAttribute;
import com.slepeweb.sandbox.acm.mvc.annotation.AcmObjectAnno;
import com.slepeweb.sandbox.acm.mvc.bean.AcmObject;
import com.slepeweb.sandbox.acm.mvc.service.HelloWorldService;

@Controller
public class GenericController {
	
	@Autowired
	HelloWorldService helloWorldService;
	
	@RequestMapping(value = "/generic")
	public ModelAndView doGeneric(@AcmObjectAnno Object obj) {
		ModelAndView modelAndView = new ModelAndView("generic.page");
		modelAndView.addObject( RequestAttribute.GENERIC_LIST, this.helloWorldService.getLevelOneItems( ( AcmObject ) obj ) );
		return modelAndView;
	}
}
