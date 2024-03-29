package com.slepeweb.cms.control;

import java.net.URL;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slepeweb.cms.bean.Host;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.cms.service.HostService;
import com.slepeweb.cms.service.SolrService4Cms;
import com.slepeweb.cms.setup.CommerceSetup;
import com.slepeweb.cms.setup.SiteSetup;

@Controller
public class SetupController extends BaseController {
	private static Logger LOG = Logger.getLogger(SetupController.class);
	
	@Autowired private SiteSetup siteSetup;
	@Autowired private CommerceSetup commerceSetup;
	@Autowired private HostService hostService;
	@Autowired private SolrService4Cms solrService4Cms;
	
	@RequestMapping(value="/setup", produces="text/text")	
	@ResponseBody
	public String initSite(@RequestParam(value="file", required=true) String fileName) {	
		String resource = "/xls/" + fileName;
		URL url = getClass().getClassLoader().getResource(resource);
		if (url != null) {
			try {
				this.siteSetup.load(url.getPath());
			}
			catch (ResourceException e) {
				LOG.warn(String.format("Site initialisation incomplete : %s", e.getMessage()));				
			}
			
			return "finished";
		}
		else {
			LOG.warn(String.format("Spreadsheet not found [%s]", resource));
			return String.format("Resource not found [%s]", resource);
		}
	}
	
	@RequestMapping(value="/setup/commerce", produces="text/text")	
	@ResponseBody
	public String initCommerce(
			@RequestParam(value="site", required=true) String siteName,
			@RequestParam(value="file", required=true) String fileName,
			@RequestParam(value="ptemp", required=true) String productTemplate,
			@RequestParam(value="ctemp", required=true) String categoryTemplate) {	
		
		String resource = "/xls/" + fileName;
		URL url = getClass().getClassLoader().getResource(resource);
		
		if (url != null) {
			this.commerceSetup.load(siteName, url.getPath(), productTemplate, categoryTemplate);
			return "finished";
		}
		else {
			LOG.warn(String.format("Spreadsheet not found [%s]", resource));
			return String.format("Resource not found [%s]", resource);
		}
	}
	
	@RequestMapping(value="/solr/index", produces="text/text")	
	@ResponseBody
	public String solrIndex(@RequestParam(value="host", required=true) String hostName,
			@RequestParam(value="port", required=true) Integer port,
			@RequestParam(value="path", required=true) String startPath) {
		
		String msg;
		Host h = this.hostService.getHost(hostName, port);
		if (h == null) {
			LOG.warn(msg = String.format("Host not found [%s]", hostName));	
			return msg;
		}
		
		Item i = h.getSite().getItem(startPath);
		if (i == null) {
			LOG.warn(msg = String.format("Item not found [%s%s]", hostName, startPath));	
			return msg;
		}
		
		// Empty the index for this site
		this.solrService4Cms.remove(h.getSite());
		
		// Index the site
		this.solrService4Cms.indexSection(i);
		return "Indexing complete";
	}	
	
}
