package com.slepeweb.site.sws.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.site.model.TwitterComponent;
import com.slepeweb.site.ntc.service.NtcHtmlScraperService;
import com.slepeweb.site.service.TwitterService;
import com.slepeweb.site.sws.bean.LotteryNumbersBean;
import com.slepeweb.ws.bean.PasswordBean;
import com.slepeweb.ws.bean.WeatherBean;
import com.slepeweb.ws.bean.WeatherBeanWrapper;
import com.slepeweb.ws.client.PasswordJaxwsClient;
import com.slepeweb.ws.client.WeatherJaxwsClient;

@Controller
@RequestMapping(value = "/ws")
public class WebServicesController {

	@Autowired private PasswordJaxwsClient passwordJaxwsClient;	
	@Autowired private WeatherJaxwsClient weatherJaxwsClient;
	@Autowired private NtcHtmlScraperService scraperService;
	@Autowired private TwitterService twitterService;
	@Autowired private ItemService itemService;
	
	@RequestMapping(value="/password", method=RequestMethod.GET, produces={"application/json", "text/xml"})	
	@ResponseBody
	public PasswordBean doPassword(@RequestParam String org, 
			@RequestParam(value="key", required=false) String key,
			@AuthenticationPrincipal User u) {
		
		if (! hasAuthority(u, "SWS_PWD")) {
			key = "";
		}
		
		return this.passwordJaxwsClient.getPassword(org, key);
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
	
	@RequestMapping(value="/scrape/{organiserId}/{tableId}", method=RequestMethod.POST, produces="text/html")	
	@ResponseBody
	public String scrape(@RequestParam String url, @PathVariable Integer organiserId, 
			@PathVariable Integer tableId) {
		return this.scraperService.scrape(url, organiserId, tableId);
	}	
	
	@RequestMapping(value="/login/user", method=RequestMethod.GET, produces="application/json")	
	@ResponseBody
	public User getUser(@AuthenticationPrincipal User u) {
		return u;
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
	
	private boolean hasAuthority(User u, String name) {
		if (u != null) {
			for (GrantedAuthority auth : u.getAuthorities()) {
				if (auth.getAuthority().equals(name)) {
					return true;
				}
			}
		}
		return false;
	}

}
