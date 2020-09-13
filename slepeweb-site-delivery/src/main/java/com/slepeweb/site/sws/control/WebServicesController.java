package com.slepeweb.site.sws.control;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.common.util.HttpUtil;
import com.slepeweb.site.model.TwitterComponent;
import com.slepeweb.site.service.TwitterService;
import com.slepeweb.site.sws.bean.LotteryNumbersBean;
import com.slepeweb.ws.bean.WeatherBean;
import com.slepeweb.ws.bean.WeatherBeanWrapper;
import com.slepeweb.ws.client.WeatherJaxwsClient;

@Controller
@RequestMapping(value = "/ws")
public class WebServicesController {

	@Autowired private WeatherJaxwsClient weatherJaxwsClient;
	@Autowired private TwitterService twitterService;
	@Autowired private ItemService itemService;
	
	// All requests in this package should by default not be cached. This method
	// will get executed before any of the request mappings below.
	@ModelAttribute(value="_void")
	public String nocache(HttpServletResponse res) {
		HttpUtil.setCacheHeaders(System.currentTimeMillis(), -1L, 0L, 0L, res);
		return "nocache";
	}

	@RequestMapping(value="/lotterynumbers/{howmany}", method=RequestMethod.GET, produces={"application/json", "text/xml"})	
	@ResponseBody
	public LotteryNumbersBean doLottery(@PathVariable Integer howmany) {
		return new LotteryNumbersBean(howmany);
	}	
	
	@RequestMapping(value="/weather/{country}/{city}", method=RequestMethod.GET, produces="application/json")	
	@ResponseBody
	public WeatherBean doWeather(@PathVariable String country, @PathVariable String city) {
		return this.weatherJaxwsClient.getWeather(country, city);
	}	
	
	@RequestMapping(value="/weatherw/{country}/{city}", method=RequestMethod.GET, produces="application/json")	
	@ResponseBody
	public WeatherBeanWrapper doWeatherWrapper(@PathVariable String country, @PathVariable String city) {
		return this.weatherJaxwsClient.getWeatherWrapper(country, city);
	}	
	
	@RequestMapping(value="/tweets/{componentId}", method=RequestMethod.GET, produces="application/json")	
	@ResponseBody
	public TwitterComponent getTweets(@PathVariable(value="componentId") Long id) {
		Item child = this.itemService.getItem(id);
		Link dummy = new Link().setName("std").setChild(child);
		TwitterComponent c = new TwitterComponent().setup(dummy);
		c = this.twitterService.getSyndicatedTweets(c);		
		return c;
	}
	
}
