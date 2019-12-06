package com.slepeweb.ifttt.control;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.slepeweb.ifttt.bean.JsonObj;
import com.slepeweb.ifttt.bean.PasswordAction;
import com.slepeweb.ifttt.bean.PasswordActionFields;
import com.slepeweb.ifttt.bean.PasswordTrigger;
import com.slepeweb.ifttt.bean.QueueManager;
import com.slepeweb.ifttt.bean.Request;
import com.slepeweb.ifttt.bean.Status;

@Controller
@RequestMapping("/ifttt/v1")
public class RestController extends BaseController {
	
	private static Logger LOG = Logger.getLogger(RestController.class);
	private Map<String, QueueManager<PasswordAction>> passwordRequestQueues = new HashMap<String, QueueManager<PasswordAction>>();
	
	public static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
	private static final String STATUS_PATH = "/status";
	private static final String TEST_SETUP_PATH = "/test/setup";
	private static final String PASSWORD_INPUT_PATH = "/actions/password_in";
	private static final String PASSWORD_OUTPUT_PATH = "/triggers/password_out";
	private static final String USER_INFO_PATH = "/user/info";
	
	private boolean isValidService(HttpServletResponse response, ModelMap model) {
		String key = (String) model.get("_serviceKey");
		if (key == null || ! key.equals(Request.SERVICE_KEY)) {
			response.setStatus(Response.SC_UNAUTHORIZED);
			return false;
		}
		return true;
	}
	
	private boolean isValidUser(HttpServletResponse response, ModelMap model) {
		String bearerCode = getBearerCode(model);
		if (bearerCode == null || ! bearerCode.equals(OauthController.USER_ID_CODE)) {
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
	
	@RequestMapping(value=TEST_SETUP_PATH, method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public String testSetup(HttpServletRequest request, HttpServletResponse response, ModelMap model) {	
		
		LOG.info(String.format("Request for [%s]", TEST_SETUP_PATH));
		
		if (! isValidService(response, model)) {
			return null;
		}
		
		JsonObj top = JsonObj.createStruc().
			put("data", JsonObj.createStruc().
				put("accessToken", JsonObj.create(OauthController.USER_ID_CODE)).
				put("samples", JsonObj.createStruc().
					put("triggers", JsonObj.createStruc().
						put("password_out", JsonObj.createStruc().
							put("party", JsonObj.create("Halifax")).
							put("password", JsonObj.create("abcdefg")))).
					put("actions", JsonObj.createStruc().
						put("password_in", JsonObj.createStruc().
							put("party", JsonObj.create("Halifax"))))));
		
		return top.stringify();
	}

	@RequestMapping(value=STATUS_PATH, method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public Status status(HttpServletResponse response, ModelMap model) {
		
		LOG.info(String.format("Request for [%s]", STATUS_PATH));
		
		if (! isValidService(response, model)) {
			return new Status(false);
		}

		return new Status(true);
	}

	@RequestMapping(value=PASSWORD_OUTPUT_PATH, method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public String sendPassword(@RequestBody PasswordTrigger trigger, HttpServletResponse response, ModelMap model) {	
		LOG.info(String.format("Request for [%s]", PASSWORD_OUTPUT_PATH));
		
		if (! isValidUser(response, model)) {
			return error("Unauthorized access");
		}
		
		/*
		 * See comment below regarding this trigger.
		 * 
		if (trigger.isMissingFields()) {
			response.setStatus(Response.SC_BAD_REQUEST);
			return error("Missing 'greeting' trigger field");
		}
		*/
		
		if (isTestMode(model)) {
			return sendPasswordTest(trigger.getLimit());
		}
		
		/*
		 * We have 2 applets effectively chained together:
		 *   1) Google assistant -> Password Manager Action
		 *   2) Password Manager Trigger -> Android SMS
		 * 
		 * The ingredients used in 1) are stored in a FIFO queue by this service - IFTTT does NOT need
		 * to provide them, so the RequestBody is currently ignored, and the ingredients for 2) are
		 * drawn from the queued data.
		 */
		QueueManager<PasswordAction> queue = getQueue(getBearerCode(model));
		return buildPasswordJson(queue.getBufferReversed(), trigger.getLimit());
	}

	@RequestMapping(value=PASSWORD_INPUT_PATH, method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public String requestPassword(@RequestBody PasswordAction action, HttpServletResponse response, ModelMap model) {	
		LOG.info(String.format("Request for [%s]", PASSWORD_INPUT_PATH));
		
		if (! isValidUser(response, model)) {
			return error("Unauthorized access");
		}
		
		if (action.isMissingFields()) {
			response.setStatus(Response.SC_BAD_REQUEST);
			return error("Missing 'party' action field");
		}
		
		if (isTestMode(model)) {
			return requestPasswordTest();
		}
				
		LOG.info(String.format("Password request queued [%s]", action.getFields().getParty()));
		action.setCreated(new Date());
		
		QueueManager<PasswordAction> queue = getQueue(getBearerCode(model));
		queue.add(action);
		
		// TODO: For the moment, return dummy data - doesn't seem to affect the result at this stage
		return requestPasswordTest();
	}
	
	@RequestMapping(value=USER_INFO_PATH, method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public String userInfo(HttpServletRequest req, HttpServletResponse response, ModelMap model) {
		
		LOG.info("User info request in progress ...");
		
		if (! isValidUser(response, model)) {
			return error("Unauthorized access");
		}

		return JsonObj.createStruc().
			put("data", JsonObj.createStruc().
				put("name", JsonObj.create("Butty Bear")).
				put("id", JsonObj.create("buttybear")).
				put("url", JsonObj.create("https//ifttt.buttigieg.org.uk"))
			).stringify();
	}
	
	private String sendPasswordTest(int max) {
		List<PasswordAction> actions = new ArrayList<PasswordAction>();
		long id = new Date().getTime();
		PasswordAction pa;
		
		pa = new PasswordAction();
		pa.setCreated(new Date(id));
		pa.setFields(new PasswordActionFields().setParty("Hello Joe"));
		actions.add(pa);
		
		pa = new PasswordAction();
		pa.setCreated(new Date(id - 1000));
		pa.setFields(new PasswordActionFields().setParty("Hello Georgie"));
		actions.add(pa);
		
		pa = new PasswordAction();
		pa.setCreated(new Date(id - 2000));
		pa.setFields(new PasswordActionFields().setParty("Hello GB"));
		actions.add(pa);
		
		return buildPasswordJson(actions, max);
	}
	
	private String buildPasswordJson(List<PasswordAction> list, int max) {
		int count = 0;
		String timestamp;
		JsonObj data = JsonObj.createList();
		JsonObj top = JsonObj.createStruc().put("data", data);
		
		LOG.info(String.format("Queue has %d entries", list.size()));
		
		for (PasswordAction action : list) {
			if (count >= max) {
				break;
			}
			
			// This needs to be in seconds
			timestamp = String.valueOf((long) (action.getCreated().getTime() / 1000));	
			
			data.add(JsonObj.createStruc().
					put("party", JsonObj.create(action.getFields().getParty())).
					put("password", JsonObj.create("dummyPassword")).
					put("created_at", JsonObj.create(SDF.format(action.getCreated()))).
					put("meta", JsonObj.createStruc().
						put("id", JsonObj.create(action.getCreated().getTime())).
						put("timestamp", JsonObj.create(timestamp))));	
			
			LOG.info(String.format("Trigger actioned [%s]", action.getFields().getParty()));
			count++;
		}
		
		return top.stringify();
	}
	
	private String requestPasswordTest() {
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
	
	private QueueManager<PasswordAction> getQueue(String bearer) {
		QueueManager<PasswordAction> queue = this.passwordRequestQueues.get(bearer);
		if (queue == null) {
			queue = new QueueManager<PasswordAction>(50);
			this.passwordRequestQueues.put(bearer, queue);
		}
		
		return queue;
	}
}