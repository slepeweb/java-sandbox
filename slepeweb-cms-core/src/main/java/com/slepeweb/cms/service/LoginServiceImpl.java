package com.slepeweb.cms.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.LoginSupport;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.constant.AttrName;
import com.slepeweb.common.service.SendMailService;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class LoginServiceImpl implements LoginService {
	
	private static Logger LOG = Logger.getLogger(LoginServiceImpl.class);
	
	@Autowired private UserService userService;
	@Autowired private SendMailService sendMailService;
	
	public LoginSupport login(String alias, String password, HttpServletRequest req) {
		return login(alias, password, false, req);
	}
	
	public LoginSupport login(String alias, String password, boolean asContentEditor, HttpServletRequest req) {
		
		LoginSupport supp = new LoginSupport().setAlias(alias).setPassword(password);
		String userMsg = "";
		
		if (StringUtils.isNotBlank(alias) && StringUtils.isNotBlank(password)) {
			// Look for secret flag
			if (StringUtils.contains(password, "^")) {
				password = StringUtils.replace(password, "^", "");
			}
			else {
				supp.setSendmailFlag(true);
			}
			
			User u = this.userService.get(alias);
			
			if (u != null) {
				supp.setUser(u);
				
				if (asContentEditor && ! u.isEditor()) {
					LOG.info(userMsg = String.format("'%s' does not have permission to use the content editor", alias));
				}
				else if (u.getPassword() != null) {
					StandardPasswordEncoder encoder = new StandardPasswordEncoder();					
					if (encoder.matches(password, u.getPassword())) {
						if (u.isEnabled()) {
							supp.setSuccess(true);
							req.getSession().setAttribute(AttrName.USER, supp.getUser().setLoggedIn(true));	
							LOG.info(String.format("Successful login [%s], session %s, for site %s", alias, 
									req.getSession().getId(), req.getAttribute(AttrName.SITE)));
						}
						else {
							userMsg = "The user account is disabled right now.";
							LOG.info(String.format("Failed attempt to login to disabled account [%s]", alias));
						}
					}
					else {
						userMsg = "Invalid account details!";
						LOG.info(String.format("Failed login [%s]/[%s]", alias, password));
					}
				}
				else {
					userMsg = "Account availability error!";
					LOG.warn(String.format("Account setup not complete [%s]", u));
				}
			}
			else {
				userMsg = "Invalid username and/or password!";
				LOG.info(String.format("Failed login [%s]/[%s]", alias, password));
			}
		}
		else {
			userMsg = "All form fields must be populated!";
			// Don't send an email if BOTH fields are blank
			supp.setSendmailFlag(! (StringUtils.isBlank(alias) && StringUtils.isBlank(password)));
			LOG.warn("Form data incomplete");
		}
		
		supp.setUserMessage(userMsg);
		
		if (! supp.isSuccess()) {
			supp.setEmailMessage(getEmailMessage(req, userMsg));
		}
		
		/* 
		 * Note that an email IS sent 
		 * a) on successful login, if the user doesn't know about the 'back door', or
		 * b) on failed login
		 */
		sendMailIf(supp);
		
		return supp;
	}
	
	private String getEmailMessage(HttpServletRequest req, String userMsg) {
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
		
		if (! supp.isSuccess() || supp.isSendmailFlag()) {
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
