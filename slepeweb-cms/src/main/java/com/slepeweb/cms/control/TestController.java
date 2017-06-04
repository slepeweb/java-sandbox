package com.slepeweb.cms.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.cms.test.BuildTest;
import com.slepeweb.cms.test.CacheTest;
import com.slepeweb.cms.test.FieldTest;
import com.slepeweb.cms.test.ItemTest;
import com.slepeweb.cms.test.MediaTest;
import com.slepeweb.cms.test.ProductTest;
import com.slepeweb.cms.test.PurgeTest;
import com.slepeweb.cms.test.SolrTest;
import com.slepeweb.cms.test.VersionTest;

@Controller
@RequestMapping("/test")
public class TestController extends BaseController {
	
	private static final String TEST_VIEW = "cms.test";
	
	@Autowired private BuildTest buildTest;
	@Autowired private FieldTest fieldTest;
	@Autowired private ItemTest itemTest;
	@Autowired private MediaTest mediaTest;
	@Autowired private PurgeTest purgeTest;
	@Autowired private CacheTest cacheTest;
	@Autowired private VersionTest versionTest;
	@Autowired private SolrTest solrTest;
	@Autowired private ProductTest productTest;
	
	@RequestMapping("/build")
	public String doBuild(ModelMap model) {
		model.addAttribute("testResults", this.buildTest.execute());
		return TEST_VIEW;
	}
	
	@RequestMapping("/fields")
	public String doFields(ModelMap model) {
		model.addAttribute("testResults", this.fieldTest.execute());
		return TEST_VIEW;
	}
	
	@RequestMapping("/items")
	public String doItems(ModelMap model) {		
		model.addAttribute("testResults", this.itemTest.execute());
		return TEST_VIEW;
	}
	
	@RequestMapping("/media")
	public String doMedia(ModelMap model) {
		model.addAttribute("testResults", this.mediaTest.execute());
		return TEST_VIEW;
	}
	
	@RequestMapping("/purge")
	public String doPurge(ModelMap model) {
		model.addAttribute("testResults", this.purgeTest.execute());
		return TEST_VIEW;
	}
	
	@RequestMapping("/cache")
	public String doCache(ModelMap model) {
		model.addAttribute("testResults", this.cacheTest.execute());
		return TEST_VIEW;
	}
	
	@RequestMapping("/version")
	public String doVersion(ModelMap model) {
		model.addAttribute("testResults", this.versionTest.execute());
		return TEST_VIEW;
	}
	
	@RequestMapping("/solr")
	public String doSolr(ModelMap model) {
		model.addAttribute("testResults", this.solrTest.execute());
		return TEST_VIEW;
	}
	
	@RequestMapping("/product")
	public String doProduct(ModelMap model) {
		model.addAttribute("testResults", this.productTest.execute());
		return TEST_VIEW;
	}
	
}
