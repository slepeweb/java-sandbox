package com.slepeweb.cms.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.LoginSupport;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.component.BadActorMonitor;
import com.slepeweb.cms.component.BadActorMonitor.BadActorRecord;
import com.slepeweb.cms.constant.AttrName;
import com.slepeweb.common.service.SendMailService;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class LoginServiceImpl implements LoginService {
	
	private static Logger LOG = Logger.getLogger(LoginServiceImpl.class);
	
	@Autowired private UserService userService;
	@Autowired private SendMailService sendMailService;
	@Autowired private BadActorMonitor badActorMonitor;
	
	public LoginSupport login(String alias, String password, HttpServletRequest req) {
		return login(alias, password, false, req);
	}
	
	public LoginSupport login(String alias, String password, boolean asContentEditor, HttpServletRequest req) {
		
		LoginSupport supp = new LoginSupport().setRequest(req).setAlias(alias).setPassword(password);
		
		// Don't send an email if EITHER alias or password field is blank
		if (StringUtils.isBlank(alias) || StringUtils.isBlank(password)) {
			supp.setSendmailFlag(! (StringUtils.isBlank(alias) && StringUtils.isBlank(password)));
			LOG.warn("Form data incomplete");
			return communicateResult(supp, "All form fields must be populated!");
		}
		
		// Look for secret flag that prohibits emails being sent
		if (StringUtils.contains(password, "^")) {
			password = StringUtils.replace(password, "^", "");
		}
		else {
			supp.setSendmailFlag(true);
		}
		
		// Check user exists in the database
		User u = this.userService.get(alias);		
		if (u == null) {
			return communicateResult(supp, "Invalid username and/or password!", 
					String.format("Failed login [%s]/[%s]", alias, password));
		}
		
		supp.setUser(u);
		
		// Check content-editor privelege is granted for content editing app
		if (asContentEditor && ! u.isEditor()) {
			return communicateResult(supp, String.format("'%s' does not have permission to use the content editor", alias));
		}
		
		// Check supplied password against database record 
		StandardPasswordEncoder encoder = new StandardPasswordEncoder();					
		if (! encoder.matches(password, u.getPassword())) {
			// Failed login request - inform monitor
			return communicateResult(supp, "Invalid account details!", 
					String.format("Failed login [%s]/[%s]", alias, password));
		}
		
		if (u.isEnabled()) {
			supp.setSuccess(true);
			req.getSession().setAttribute(AttrName.USER, supp.getUser().setLoggedIn(true));	
			return communicateResult(supp, String.format("Successful login [%s], session %s, for site %s", alias, 
					req.getSession().getId(), req.getAttribute(AttrName.SITE)));
		}
		else {
			return communicateResult(supp, "The user account is disabled right now.", 
					String.format("Failed attempt to login to disabled account [%s]", alias));
		}
	}
	
	private LoginSupport communicateResult(LoginSupport supp, String userMsg) {
		return communicateResult(supp, userMsg, userMsg);
	}
	
	private LoginSupport communicateResult(LoginSupport supp, String userMsg, String logMsg) {
		LOG.info(String.format("%s - (%s)", logMsg, supp.getIp()));
		supp.setUserMessage(userMsg);
		
		/* 
		 * Note that an email IS sent 
		 * a) on successful login, if the user doesn't know about the 'back door', or
		 * b) on failed login
		 */
		sendMailIf(supp);
		
		return supp;
	}
	
	private String composeEmailMessage(LoginSupport supp) {
		HttpServletRequest req = supp.getRequest();
		String userMsg = supp.getUserMessage();
		String servletPath = req.getServletPath();
		servletPath = (servletPath == null) ? "" : servletPath;
		String pathInfo = req.getPathInfo();
		pathInfo = pathInfo != null ? servletPath + pathInfo : servletPath;
		StringBuffer emailMsg = new StringBuffer();
		
		emailMsg.append("""
				<style>
				  td {padding: 6px;}
				  td:first-child {background-color: #b0c4de;}
				</style>""");
		
		emailMsg.append(String.format("<p>User message: %s</p>", userMsg));

		emailMsg.append("<h3>Request headers</h3><table>");
		emailMsg.append(String.format("<tr><td>Host</td><td>%s</td></tr>", notNull(req.getHeader("host"))));
		emailMsg.append(String.format("<tr><td>Origin</td><td>%s</td></tr>", notNull(req.getHeader("origin"))));
		emailMsg.append(String.format("<tr><td>Referer</td><td>%s</td></tr>", notNull(req.getHeader("referer"))));
		emailMsg.append(String.format("<tr><td>X-Forwarded-For</td><td>%s</td></tr>", notNull(req.getHeader("X-Forwarded-For"))));
		emailMsg.append(String.format("<tr><td>Agent</td><td>%s</td></tr>", notNull(req.getHeader("user-agent"))));
		emailMsg.append("</table>");
		
		emailMsg.append("<h3>HttpServletRequest properties</h3><table>");
		emailMsg.append(String.format("<tr><td>Server name</td><td>%s</td></tr>", notNull(req.getServerName())));
		emailMsg.append(String.format("<tr><td>Request URL</td><td>%s</td></tr>", notNull(req.getRequestURL().toString())));
		emailMsg.append(String.format("<tr><td>Remote host</td><td>%s</td></tr>", notNull(req.getRemoteHost())));
		emailMsg.append(String.format("<tr><td>Path</td><td>%s</td></tr>", notNull(req.getPathInfo())));
		emailMsg.append(String.format("<tr><td>Query</td><td>%s</td></tr>", notNull(req.getQueryString())));
		emailMsg.append(String.format("<tr><td>Method</td><td>%s</td></tr>", notNull(req.getMethod())));
		emailMsg.append(String.format("<tr><td>Remote address</td><td>%s</td></tr>", notNull(req.getRemoteAddr())));
		emailMsg.append(String.format("<tr><td>Local address</td><td>%s</td></tr>", notNull(req.getLocalAddr())));
		emailMsg.append("</table>");
		
		return emailMsg.toString();
	}
	
	private String notNull(String s) {
		return s == null ? "" : s;
	}
	
	private void sendMailIf(LoginSupport supp) {
		String from = "george.buttigieg56@gmail.com";
		String to = "george@buttigieg.org.uk";
		String name = "George Buttigieg";
		
		BadActorRecord rec = this.badActorMonitor.getRecord(supp.getIp());
		
		/*
		 * Proceed to send email IFF
		 * a) either login failed OR login did not go through backdoor
		 *    AND
		 * b) either no login failures registered for this ip OR it's the first one
		 */
		if ((! supp.isSuccess() || supp.isSendmailFlag()) && (rec == null || rec.getCount() == 1)) {
			supp.setEmailMessage(composeEmailMessage(supp));
			String msg = supp.getUserMessage() != null ?  supp.getUserMessage() : ""; 
			this.sendMailService.sendMail(from, to, name, "CMS login: " + msg, supp.getEmailMessage());
		}		
	}

	
	public void logout(HttpServletRequest req) {
		User u = (User) req.getSession().getAttribute(AttrName.USER);
		
		if (u != null) {
			// Attribute removal doesn't seem to happen immediately, so user object now has a 'loggedIn' property
			u.setLoggedIn(false);
			LOG.info(String.format("User logout [%s]", u.getEmail()));
		}
		
		req.getSession().removeAttribute(AttrName.USER);
	}
}
