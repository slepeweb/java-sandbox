package com.slepeweb.money.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Service;

import com.slepeweb.money.bean.LoginResponse;
import com.slepeweb.money.bean.User;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class LoginServiceImpl implements LoginService {
	
	private static Logger LOG = Logger.getLogger(LoginServiceImpl.class);
	
	@Autowired private UserService userService;
	
	public LoginResponse login(String alias, String password) {
		
		LoginResponse resp = new LoginResponse();
		
		if (StringUtils.isNotBlank(alias) && StringUtils.isNotBlank(password)) {
			User u = this.userService.getUser(alias);
			
			if (u != null) {
				resp.setUser(u);
				
				if (u.getPassword() != null) {
					StandardPasswordEncoder encoder = new StandardPasswordEncoder();					
					if (encoder.matches(password, u.getPassword())) {
						if (u.isEnabled()) {
							resp.setSuccess(true);
							LOG.info(String.format("Successful login [%s]", alias));
						}
						else {
							resp.setErrorMessage("The user account is disabled right now.");
							LOG.info(String.format("Failed attempt to login to disabled account [%s]", alias));
						}
					}
					else {
						resp.setErrorMessage("Invalid account details!");
						LOG.info(String.format("Failed login [%s]", alias));
					}
				}
				else {
					resp.setErrorMessage("Account availability error!");
					LOG.warn(String.format("Account setup not complete [%s]", u));
				}
			}
			else {
				resp.setErrorMessage("Invalid username and/or password!");
				LOG.info(String.format("Failed login [%s]", alias));
			}
		}
		else {
			resp.setErrorMessage("All form fields must be populated!");
			LOG.warn("Form data incomplete]");
		}
		
		return resp;
	}
	
	public void logout(HttpServletRequest req) {
		User u = getUser(req);
		req.getSession().removeAttribute(User.USER_ATTR);
		LOG.info(String.format("User logout [%s]", u.getAlias()));
	}
	
	private User getUser(HttpServletRequest req) {
		return (User) req.getSession().getAttribute(User.USER_ATTR);
	}
}
