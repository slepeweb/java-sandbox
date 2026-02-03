package com.slepeweb.site.control;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.constant.AttrName;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/spring/common")
public class CommonController extends BaseController {
	
	//private static Logger LOG = Logger.getLogger(CommonController.class);
		
	@RequestMapping(value="/gotofirstchild")
	public String gotoFirstChild (
			@ModelAttribute(AttrName.ITEM) Item i, 
			HttpServletResponse res) throws IOException {
		
		List<Item> children = i.getBoundPages();
		
		if (children.size() > 0) {
			res.sendRedirect(children.get(0).getUrl());
		}
		else {
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
		
		return null;
	}

}
