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
		String msg = "";
		
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
					String s = String.format("'%s' does not have permission to use the content editor", alias);
					supp.setUserMessage(s);
					LOG.info(s);
				}
				else if (u.getPassword() != null) {
					StandardPasswordEncoder encoder = new StandardPasswordEncoder();					
					if (encoder.matches(password, u.getPassword())) {
						if (u.isEnabled()) {
							supp.setSuccess(true);
							req.getSession().setAttribute(AttrName.USER, supp.getUser().setLoggedIn(true));				
							LOG.info(msg = String.format("Successful login [%s], session %s, for site %s", alias, 
									req.getSession().getId(), req.getAttribute(AttrName.SITE)));
						}
						else {
							supp.setUserMessage("The user account is disabled right now.");
							LOG.info(msg = String.format("Failed attempt to login to disabled account [%s]", alias));
						}
					}
					else {
						supp.setUserMessage("Invalid account details!");
						LOG.info(msg = String.format("Failed login [%s]/[%s]", alias, password));
					}
				}
				else {
					supp.setUserMessage("Account availability error!");
					LOG.warn(msg = String.format("Account setup not complete [%s]", u));
				}
			}
			else {
				supp.setUserMessage("Invalid username and/or password!");
				LOG.info(msg = String.format("Failed login [%s]/[%s]", alias, password));
			}
		}
		else {
			supp.setUserMessage("All form fields must be populated!");
			LOG.warn(msg = "Form data incomplete]");
		}
		
		sendMailIf(supp.setEmailMessage(msg));
		return supp;
	}
	
	private void sendMailIf(LoginSupport supp) {
		String from = "george.buttigieg56@gmail.com";
		String to = "george@buttigieg.org.uk";
		String name = "George Buttigieg";
		
		if (! supp.isSuccess() || supp.isSendmailFlag()) {
			String msg = supp.getUserMessage() + "\n\n" + supp.getEmailMessage();
			this.sendMailService.sendMail(from, to, name, "CMS login", msg);
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
