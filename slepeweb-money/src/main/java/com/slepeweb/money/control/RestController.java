package com.slepeweb.money.control;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.bean.RestResponse;
import com.slepeweb.money.service.CategoryService;
import com.slepeweb.money.service.PayeeService;


@Controller
@RequestMapping("/rest")
public class RestController extends BaseController {
	//private static Logger LOG = Logger.getLogger(SiteRestController.class);
	@Autowired private CategoryService categoryService;
	@Autowired private PayeeService payeeService;

	@RequestMapping(value="/category/minor/list/{major}", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public RestResponse getMinorsForMajor(@PathVariable String major, ModelMap model) {	
		return new RestResponse().setData(this.categoryService.getAllMinorValues(major));
	}

	@RequestMapping(value="/payee/list/all", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public List<String> getAllPayees(HttpServletRequest req, ModelMap model) {	
		List<Payee> allPayees = this.payeeService.getAll();
		List<String> allPayeeNames = new ArrayList<String>(allPayees.size());
		
		for (Payee p : allPayees) {
			allPayeeNames.add(p.getName());
		}
		
		return allPayeeNames;
	}
}
