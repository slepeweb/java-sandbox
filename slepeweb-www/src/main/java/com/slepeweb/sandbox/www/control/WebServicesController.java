package com.slepeweb.sandbox.www.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slepeweb.sandbox.ws.rest.LotteryNumbersBean;
import com.slepeweb.sandbox.ws.soap.PasswordBean;
import com.slepeweb.sandbox.www.service.PasswordService;

@Controller
@RequestMapping(value = "/ws")
public class WebServicesController {

	@Autowired
	private PasswordService passwordService;
	
	@RequestMapping(value="/password", method=RequestMethod.GET, produces={"application/json", "text/xml"})	
	@ResponseBody
	public PasswordBean doPassword(@RequestParam String org) {
		return this.passwordService.getPassword(org);
	}
		
	@RequestMapping(value="/lotterynumbers/{howmany}", method=RequestMethod.GET, produces={"application/json", "text/xml"})	
	@ResponseBody
	public LotteryNumbersBean doLottery(@PathVariable Integer howmany) {
		return new LotteryNumbersBean(howmany);
	}	
}
