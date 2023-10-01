package com.slepeweb.cms.service;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.LoginSupport;
import com.slepeweb.cms.bean.User;

@Service
public class LoginServiceImpl implements LoginService {
	
	private static Logger LOG = Logger.getLogger(LoginServiceImpl.class);
	private static final String USER_ATTR = "_user";
	
	@Autowired private UserService userService;
	
	public LoginSupport login(String email, String password, HttpServletRequest req) {
		return login(email, password, false, req);
	}
	
	public LoginSupport login(String email, String password, boolean asContentEditor, HttpServletRequest req) {
		
		LoginSupport supp = new LoginSupport();
		
		if (StringUtils.isNotBlank(email) && StringUtils.isNotBlank(password)) {
			User u = this.userService.get(email);
			
			if (u != null) {
				supp.setUser(u);
				
				if (asContentEditor && ! u.isEditor()) {
					String s = String.format("'%s' does not have permission to use the content editor", email);
					supp.setErrorMessage(s);
					LOG.info(s);
				}
				else if (u.getPassword() != null) {
					StandardPasswordEncoder encoder = new StandardPasswordEncoder();					
					if (encoder.matches(password, u.getPassword())) {
						if (u.isEnabled()) {
							supp.setSuccess(true);
							req.getSession().setAttribute(USER_ATTR, supp.getUser().setLoggedIn(true));				
							LOG.info(String.format("Successful login [%s]", email));
						}
						else {
							supp.setErrorMessage("The user account is disabled right now.");
							LOG.info(String.format("Failed attempt to login to disabled account [%s]", email));
						}
					}
				}
				else {
					supp.setErrorMessage("Account availability error!");
					LOG.warn(String.format("Account setup not complete [%s]", u));
				}
			}
			else {
				supp.setErrorMessage("Invalid username and/or password!");
				LOG.info(String.format("Failed login [%s]", email));
			}
		}
		else {
			supp.setErrorMessage("All form fields must be populated!");
			LOG.warn("Form data incomplete]");
		}
		
		return supp;
	}
	
	public void logout(HttpServletRequest req) {
		User u = (User) req.getSession().getAttribute(USER_ATTR);
		
		if (u != null) {
			// Attribute removal doesn't seem to happen immediately, so user object now has a 'loggedIn' property
			u.setLoggedIn(false);
			LOG.info(String.format("User logout [%s]", u.getEmail()));
		}
		
		req.getSession().removeAttribute(USER_ATTR);
	}
}
