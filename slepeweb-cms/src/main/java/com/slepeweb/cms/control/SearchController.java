package com.slepeweb.cms.control;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.RestResponse;
import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.cms.service.SolrService4Cms;

@Controller
@RequestMapping("/rest/search")
public class SearchController extends BaseController {
	
	private static Logger LOG = Logger.getLogger(SearchController.class);
	
	@Autowired private ItemService itemService;
	@Autowired private SolrService4Cms solrService4Cms;
	
	@RequestMapping(value="/reindex/{origId}", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public RestResponse reindexSection( 
			@PathVariable long origId,
			ModelMap model) {	
		
		RestResponse resp = new RestResponse();
		Item i = this.itemService.getEditableVersion(origId);
		
		if (i != null) {
			this.solrService4Cms.indexSection(i);
			LOG.info(String.format("Re-indexed section [%s]", i));
			return resp.addMessage("Section re-indexed");		
		}
		
		return resp.setError(true).addMessage("No item found with given id");		
	}
	
	@RequestMapping(value="/publish/{origId}", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public RestResponse publishSection( 
			@PathVariable long origId,
			ModelMap model) {	
		
		RestResponse resp = new RestResponse();
		Item i = this.itemService.getEditableVersion(origId);
		
		if (i != null) {
			int count = publishRecursively(i);
			String s = String.format("Published %d items in section [%s]", count, i.getName());
			LOG.info(s);
			return resp.addMessage(s);		
		}
		
		return resp.setError(true).addMessage("No item found with given id");		
	}
	
	private int publishRecursively(Item i) {
		int count = 0;
		
		if (! i.isPublished()) {
			i.setPublished(true);
			
			try {
				i.save();
				count++;
			} 
			catch (ResourceException e) {}
		}
		
		for (Link l : i.getBindingsNoShortcuts()) {
			count += publishRecursively(l.getChild());
		}
		
		return count;
	}
}
