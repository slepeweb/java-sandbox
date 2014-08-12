package com.slepeweb.cms.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.cms.test.BuildTest;
import com.slepeweb.cms.test.FieldTest;
import com.slepeweb.cms.test.ItemTest;
import com.slepeweb.cms.test.MediaTest;
import com.slepeweb.cms.test.PurgeTest;

@Controller
public class TestController extends BaseController {
	
	private static final String TEST_VIEW = "cms.test";
	
	@Autowired private BuildTest buildTest;
	@Autowired private FieldTest fieldTest;
	@Autowired private ItemTest itemTest;
	@Autowired private MediaTest mediaTest;
	@Autowired private PurgeTest purgeTest;
	
	@RequestMapping("/test/build")
	public String doBuild(ModelMap model) {
		model.addAttribute("testResults", this.buildTest.execute());
		return TEST_VIEW;
	}
	
	@RequestMapping("/test/fields")
	public String doFields(ModelMap model) {
		model.addAttribute("testResults", this.fieldTest.execute());
		return TEST_VIEW;
	}
	
	@RequestMapping("/test/items")
	public String doItems(ModelMap model) {		
		model.addAttribute("testResults", this.itemTest.execute());
		return TEST_VIEW;
	}
	
	@RequestMapping("/test/media")
	public String doMedia(ModelMap model) {
		model.addAttribute("testResults", this.mediaTest.execute());
		return TEST_VIEW;
	}
	
	@RequestMapping("/test/purge")
	public String doPurge(ModelMap model) {
		model.addAttribute("testResults", this.purgeTest.execute());
		return TEST_VIEW;
	}
	
}
