package com.slepeweb.money.control;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slepeweb.money.bean.RestResponse;
import com.slepeweb.money.service.CategoryService;


@Controller
@RequestMapping("/rest")
public class RestController extends BaseController {
	//private static Logger LOG = Logger.getLogger(SiteRestController.class);
	@Autowired private CategoryService categoryService;

	@RequestMapping(value="/category/minor/list/{major}", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public RestResponse getMinorsForMajor(
			@PathVariable String major, HttpServletRequest req, ModelMap model) {	
		
		List<String> values = this.categoryService.getAllMinorValues(major);
		return new RestResponse().setData(values);
	}
}
