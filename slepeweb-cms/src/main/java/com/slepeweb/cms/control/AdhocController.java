package com.slepeweb.cms.control;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.component.CacheRefresher;
import com.slepeweb.cms.service.ItemService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/adhoc")
public class AdhocController extends BaseController {
	
	private static final String ADHOC_VIEW = "adhoc";
	private static Logger LOG = Logger.getLogger(AdhocController.class);
	
	@Autowired private ItemService itemService;
	@Autowired private CacheRefresher cacheRefresher;
	
	@RequestMapping("/orphan/media/files")
	public String doOrphanMediaFiles(HttpServletRequest req, ModelMap model) {
		Pattern p = Pattern.compile("(\\d{1,6})(t?)");
		Matcher m;
		Long itemId;
		Item i;
		List<String> orphans = new ArrayList<String>();
		
		File root = new File("/home/photos");
		for (File child : root.listFiles()) {
			for (File grandchild : child.listFiles()) {
				m = p.matcher(grandchild.getName());
				if (m.matches()) {
					itemId = Long.parseLong(m.group(1));
					i = this.itemService.getItem(itemId);
					
					if (i == null) {
						LOG.info(String.format("Orphan? [%s]", grandchild.getAbsolutePath()));
						orphans.add(grandchild.getAbsolutePath());
					}
				}
			}
		}
		
		LOG.info(String.format("Found %d orphan files for deletion", orphans.size()));
		String message = null;
		
		if (req.getParameter("delete") != null) {
			File f;
			int count = 0;
			
			for (String path : orphans) {
				f = new File(path);
				
				if (f.delete()) {
					LOG.info(String.format("File %s successfully deleted", path));
					count++;
				}
				else {
					LOG.error(String.format("FAILED to delete file [%s]", path));
				}
			}			
			
			message = String.format("Deleted %d files from /home/photos", count);
		}
		else {
			message = "Instruction to delete files not given.";
		}
		
		model.addAttribute("_message", message);
		return ADHOC_VIEW;
	}
	

	@RequestMapping(value="/$cache_refresh$")	
	public String updateCaches(ModelMap model) throws Exception {		
		this.cacheRefresher.execute();
		model.addAttribute("_message", "Cached refreshed - use browser Back to return to editing item");
		return ADHOC_VIEW;
	}
}
