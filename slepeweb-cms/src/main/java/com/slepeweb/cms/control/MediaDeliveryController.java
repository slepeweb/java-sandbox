package com.slepeweb.cms.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.cms.servlet.MediaDeliveryServlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/media")
public class MediaDeliveryController extends BaseController {
	
	@Autowired MediaDeliveryServlet mediaDeliveryServlet;
	
	@RequestMapping(value="/**")	
	public void doMain(HttpServletRequest req, HttpServletResponse res) throws Exception {		
		this.mediaDeliveryServlet.doGet(req,  res);
	}	
}
