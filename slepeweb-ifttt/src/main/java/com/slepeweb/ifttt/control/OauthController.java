package com.slepeweb.ifttt.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slepeweb.ifttt.bean.JsonObj;

@Controller
public class OauthController extends BaseController {
	
	private static Logger LOG = Logger.getLogger(OauthController.class);
	public static final String AUTHORIZE_PATH = "/oauth2/authorize";
	public static final String TOKEN_PATH = "/oauth2/token";
	public static final String USER_ID_CODE = "1860c61994c74964bfc246cbad2c9786";
	public static final String SECRET = "arsene wenger";
	
	@RequestMapping(value=AUTHORIZE_PATH, method=RequestMethod.GET)
	public String authorize(
			@RequestParam("client_id") String clientId,
			@RequestParam("redirect_uri") String redirectUri,
			@RequestParam("state") String state,
			ModelMap model) {	
		
		LOG.info("User authorization in progress ...");
		LOG.info(String.format("... client_id    [%s]", clientId));
		LOG.info(String.format("... redirect_uri [%s]", redirectUri));
		LOG.info(String.format("... state        [%s]", state));
		LOG.info(String.format("... Bearer       [%s]", getBearerCode(model)));
		
		// This should redirect to a login page, where we collect user details and identify that user.
		// One of the properties of a user will be a unique, non-readable user id code.
		// We will work for the moment with a single user ...
			
		String redirectUrl = String.format(
			"https://ifttt.com/channels/password_manager/authorize?code=%s&state=%s", 
			USER_ID_CODE, state);		
		
		LOG.info(String.format("Redirecting to [%s]", redirectUrl));
		return String.format("redirect:%s", redirectUrl);
	}

	@RequestMapping(value=TOKEN_PATH, method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public String token(HttpServletRequest req, HttpServletResponse resp, ModelMap model) {
		
		//String clientId = req.getParameter("client_id");
		String code = req.getParameter("code");
		//String redirectUri = req.getParameter("redirect_uri");
		//String state = req.getParameter("client_secret"); 	
		
		LOG.info("Token exchange in progress ...");
		LOG.info(String.format("... grant_type    [%s]", req.getParameter("grant_type")));
		LOG.info(String.format("... code          [%s]", code));
		LOG.info(String.format("... client_id     [%s]", req.getParameter("client_id")));
		LOG.info(String.format("... client_secret [%s]", req.getParameter("client_secret")));
		LOG.info(String.format("... redirect_uri  [%s]", req.getParameter("redirect_uri")));
		LOG.info(String.format("... Bearer       [%s]", getBearerCode(model)));
		
		if (! code.equals(USER_ID_CODE)) {
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return error("Not authorized to use this service");
		}
		
		JsonObj obj = JsonObj.createStruc().
			put("token_type", JsonObj.create("Bearer")).
			put("access_token", JsonObj.create(USER_ID_CODE));
		
		String json = obj.stringify();
		LOG.info(String.format("Returning json [%s]", json));
		return json;
	}

	private String error(String msg) {
		return JsonObj.createStruc().
				put("errors", JsonObj.createList().
					add(JsonObj.createStruc().put("message", JsonObj.create(msg)))).stringify();
	}
	
}