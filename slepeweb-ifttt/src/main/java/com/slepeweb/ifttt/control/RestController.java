package com.slepeweb.ifttt.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Response;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slepeweb.ifttt.bean.HelloWorldTrigger;
import com.slepeweb.ifttt.bean.JsonObj;
import com.slepeweb.ifttt.bean.PasswordAction;
import com.slepeweb.ifttt.bean.Request;
import com.slepeweb.ifttt.bean.Status;

@Controller
@RequestMapping("/ifttt/v1")
public class RestController extends BaseController {
	
	private static Logger LOG = Logger.getLogger(RestController.class);
	public static final String SERVICE_KEY = "vQAqaeOMiU0p9vO-mlNv9SESl7KacisWVTV3Wf7iduKhbvZrVdCrAgH0_oEJX1XE";
	
	private boolean isValidService(HttpServletResponse response, ModelMap model) {
		String key = (String) model.get("_serviceKey");
		if (key == null || ! key.equals(Request.SERVICE_KEY)) {
			response.setStatus(Response.SC_UNAUTHORIZED);
			return false;
		}
		return true;
	}
	
	private boolean isTestMode(ModelMap model) {
		String value = (String) model.get("_testMode");
		return value != null && value.equals("1");
	}
	
	@ModelAttribute(value="_serviceKey")
	public String serviceKey(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		return request.getHeader("IFTTT-Service-Key");
	}
	
	@ModelAttribute(value="_testMode")
	public String testMode(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		return request.getHeader("IFTTT-Test-Mode");
	}
	
	@RequestMapping(value="/test/setup", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public String testSetup(HttpServletRequest request, HttpServletResponse response, ModelMap model) {	
		LOG.info(String.format("Request for [%s]", "/test/setup"));
		if (! isValidService(response, model)) {
			return null;
		}
		
		JsonObj top = JsonObj.createStruc().
			put("data", JsonObj.createStruc().
				put("samples", JsonObj.createStruc().
					put("triggers", JsonObj.createStruc().
						put("hello_world", JsonObj.createStruc().
							put("greeting", JsonObj.create("Hello Wolrd")))).
					put("actions", JsonObj.createStruc().
						put("get_password", JsonObj.createStruc().
							put("party", JsonObj.create("Halifax"))))));
		
		return top.stringify();
	}

	@RequestMapping(value="/status", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public Status status(HttpServletResponse response, ModelMap model) {
		LOG.info(String.format("Request for [%s]", "/status"));
		if (! isValidService(response, model)) {
			return new Status(false);
		}

		return new Status(true);
	}

	@RequestMapping(value="/triggers/hello_world", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public String helloWorldTrigger(@RequestBody HelloWorldTrigger trigger, HttpServletResponse response, ModelMap model) {	
		LOG.info(String.format("Request for [%s]", "/triggers/hello_world"));
		
		if (! isValidService(response, model)) {
			return error("Wrong service ID");
		}
		
		if (trigger.isMissingFields()) {
			response.setStatus(Response.SC_BAD_REQUEST);
			return error("Missing 'greeting' trigger field");
		}
		
		if (isTestMode(model)) {
			return helloWorldTriggerTest(trigger.getLimit());
		}
		
		JsonObj top = JsonObj.createStruc().
			put("data", JsonObj.createList().
				add(JsonObj.createStruc().
					put("greeting", JsonObj.create("Hello Joey")).
					put("created_at", JsonObj.create("2019-08-28T12:52:59-07:00")).
					put("meta", JsonObj.createStruc().
						put("id", JsonObj.create("423456799")).
						put("timestamp", JsonObj.create(423456789)))));		
		
		return top.stringify();
	}

	@RequestMapping(value="/actions/get_password", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public String getPasswordAction(@RequestBody PasswordAction action, HttpServletResponse response, ModelMap model) {	
		LOG.info(String.format("Request for [%s]", "/actions/get_password"));
		
		if (! isValidService(response, model)) {
			return error("Wrong service ID");
		}
		
		if (action.isMissingFields()) {
			response.setStatus(Response.SC_BAD_REQUEST);
			return error("Missing 'party' action field");
		}
		
		if (isTestMode(model)) {
			return passwordActionTest();
		}
		
		return null;
	}
	
	private String helloWorldTriggerTest(int limit) {
		JsonObj data = JsonObj.createList();
		JsonObj top = JsonObj.createStruc().put("data", data);
		
		if (limit > 0) {
			data.add(JsonObj.createStruc().
				put("greeting", JsonObj.create("Hello Joe")).
				put("created_at", JsonObj.create("2017-08-27T12:52:59-07:00")).
				put("meta", JsonObj.createStruc().
					put("id", JsonObj.create("323456789")).
					put("timestamp", JsonObj.create(323456789))));
		}
		
		if (limit > 1) {
			data.add(JsonObj.createStruc().
				put("greeting", JsonObj.create("Hello Georgie")).
				put("created_at", JsonObj.create("2017-07-27T12:52:59-07:00")).
				put("meta", JsonObj.createStruc().
					put("id", JsonObj.create("223456789")).
					put("timestamp", JsonObj.create(223456789))));
		}
			
		if (limit > 2) {
			data.add(JsonObj.createStruc().
				put("greeting", JsonObj.create("Hello GB")).
				put("created_at", JsonObj.create("2017-06-27T12:52:59-07:00")).
				put("meta", JsonObj.createStruc().
					put("id", JsonObj.create("123456789")).
					put("timestamp", JsonObj.create(123456789))));
		}
		
		return top.stringify();
	}
	
	private String passwordActionTest() {
		JsonObj top = JsonObj.createStruc().
			put("data", JsonObj.createList().
				add(JsonObj.createStruc().
					put("id", JsonObj.create("123456")).
					put("url", JsonObj.create("http://www.buttigieg.org.uk/example/url"))));
		
		return top.stringify();
	}
	
	private String error(String msg) {
		return JsonObj.createStruc().
				put("errors", JsonObj.createList().
					add(JsonObj.createStruc().put("message", JsonObj.create(msg)))).stringify();
	}
	
}