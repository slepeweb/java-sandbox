package com.slepeweb.money.control;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slepeweb.money.bean.FlatTransaction;
import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.bean.RestResponse;

import jakarta.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("/rest")
public class RestController extends BaseController {
	//private static Logger LOG = Logger.getLogger(SiteRestController.class);

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
	
	@RequestMapping(value="/transaction/latest/bypayee/{payeeName}", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public FlatTransaction getRecentTransaction(@PathVariable String payeeName, ModelMap model) {	
		return this.solrService.queryLatestTransactionByPayee(payeeName);
	}
}
