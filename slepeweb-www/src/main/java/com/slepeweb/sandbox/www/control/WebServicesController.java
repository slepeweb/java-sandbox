package com.slepeweb.sandbox.www.control;

import net.webservicex.GlobalWeather;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slepeweb.sandbox.ws.rest.LotteryNumbersBean;
import com.slepeweb.sandbox.ws.soap.PasswordBean;
import com.slepeweb.sandbox.ws.soap.WeatherBean;
import com.slepeweb.sandbox.www.service.PasswordService;
import com.slepeweb.sandbox.www.service.XmlMarshallingService;

@Controller
@RequestMapping(value = "/ws")
public class WebServicesController {

	@Autowired
	private PasswordService passwordService;
	
	@Autowired
	private XmlMarshallingService xmlMarshallingService;
	
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
	
	@RequestMapping(value="/weather/{country}/{city}", method=RequestMethod.GET, produces="application/json")	
	@ResponseBody
	public WeatherBean doWeather(@PathVariable String country, @PathVariable String city) {
		GlobalWeather gw = new GlobalWeather();
		String xml = gw.getGlobalWeatherSoap().getWeather(city, country);
		Object obj = this.xmlMarshallingService.unmarshall(xml, new WeatherBean());
		if (obj != null && obj instanceof WeatherBean) {
			return (WeatherBean) obj;
		}
		
		WeatherBean error = new WeatherBean();
		error.setStatus("Failed");
		return error;
	}	
}
