package com.slepeweb.cms.control;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.slepeweb.cms.bean.Host;
import com.slepeweb.cms.bean.QandAList;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.SiteConfigCache;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.constant.AttrName;
import com.slepeweb.cms.constant.FieldName;
import com.slepeweb.cms.constant.SiteConfigKey;
import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.cms.service.CmsService;
import com.slepeweb.cms.service.QandAService;
import com.slepeweb.cms.service.UserService;
import com.slepeweb.common.service.SendMailService;
import com.slepeweb.common.util.HttpUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {
	
	private static Logger LOG = Logger.getLogger(UserController.class);
	
	@Autowired private CmsService cmsService;
	@Autowired private QandAService qandAService;
	@Autowired private UserService userService;
	@Autowired private SendMailService sendMailService;
	@Autowired private SiteConfigCache siteConfigCache;
	
	
	@RequestMapping(value="/update/form/{userId}", method=RequestMethod.GET)
	public String form4OtherUser(
		@PathVariable long userId,
		HttpServletRequest req,
		ModelMap model) throws Exception { 		
		
		User loggedInUser = getUser(req);
		Long siteId = getSiteId(req);
		UserInfo info = getUserInfo(loggedInUser, userId, siteId);
		
		model.addAttribute("_nominatedUser", info.nominatedUser);
		model.addAttribute("_qandA", this.qandAService.getQandAList(info.nominatedUser));		
		model.addAttribute("_isError", info.isError);
		model.addAttribute("_isAdmin", info.isAdmin);
		model.addAttribute("_allUsers", info.isAdmin ? this.userService.getAll() : null);
		
		return "user/userForm"; 
	}
	
	public record UserInfo(boolean isError, boolean isAdmin, User nominatedUser) {}
	
	private UserInfo getUserInfo(User loggedInUser, long nominatedUserId, long siteId) {
		boolean isAdmin = loggedInUser.hasRole(siteId, User.ADMIN);
		boolean isError = false;
		User nominatedUser = loggedInUser;
		
		if (nominatedUserId != loggedInUser.getId().longValue()) {
			if (isAdmin) {
				nominatedUser = this.userService.get(nominatedUserId);
				if (nominatedUser == null) {
					LOG.error("No such user " + nominatedUserId);
					nominatedUser = loggedInUser;
					isError = true;
				}
			}
			else {
				isError = true;
			}
		}
		
		return new UserInfo(isError, isAdmin, nominatedUser);
	}

	@RequestMapping(value="/update/action/{userId}", method=RequestMethod.POST)
	public String formSubmit(
		@PathVariable long userId,
		HttpServletRequest req,
		HttpServletResponse res,
		ModelMap model) throws Exception {
 		
		User loggedInUser = getUser(req);
		Long siteId = getSiteId(req);
		UserInfo info = getUserInfo(loggedInUser, userId, siteId);
		
		// First update names, addresses, etc.
		User u = info.nominatedUser;
		u.setFirstName(req.getParameter(FieldName.FIRSTNAME));
		u.setLastName(req.getParameter(FieldName.LASTNAME));
		u.setEmail(req.getParameter(FieldName.EMAIL));
		u.setPhone(req.getParameter(FieldName.PHONE));
		
		String password = req.getParameter("password");
		if (StringUtils.isNotBlank(password)) {
			StandardPasswordEncoder encoder = new StandardPasswordEncoder();
			u.setPassword(encoder.encode(password));
		}
				
		// Now update secret Q and A's
		QandAList qal = new QandAList().fillFromRequest(req, 3).trim();
		this.qandAService.update(u, qal);
		this.userService.save(u);

		// IFF logged-in user has been update THEN replace user object in the session
		if (loggedInUser.getId().equals(u.getId())) {
			req.getSession().setAttribute(AttrName.USER, u);
			LOG.info(String.format("Session updated for user %s", loggedInUser));
		}
		
		// Re-direct request to form page
		String msg = "User details saved";
		res.sendRedirect(String.format("%s/user/update/form/%d?flash=%s", 
				req.getContextPath(), u.getId(), HttpUtil.encodeUrl(msg)));
		
		return null; 
	}
	
	@RequestMapping(value="/forgot/password")
	public String forgotPassword(
			HttpServletRequest req,
			ModelMap model) throws Exception {
		
		if (req.getMethod().equalsIgnoreCase("post")) {
			
			String email = req.getParameter("email");
			model.addAttribute("_emailSubmitted", email);
			LOG.info(String.format("Forgotten password notification received from '%s' ... ", email));
			
			User dbRecord = this.userService.getByEmail(email);
			
			if (dbRecord != null) {
				// Update 'secret' info for user, and email this to him/her
				User u = updateSecretAndSaveUser(dbRecord);

				String href = String.format("%s%s/user/password/reset?email=%s&code=%s", 
						this.cmsService.getEditorialHost(), req.getContextPath(), u.getEmail(), u.getSecret());
				
				String message = String.format(
						"You have requested a password reset on your account. Please <a href=\"%s\" target=\"_blank\">click here</a> to proceed.", href);
				
				String from = this.siteConfigCache.getValue(0L, "email.sender");
				
				this.sendMailService.sendMail(from, u.getEmail(), u.getFullName(), "Password reset", message);
				
				model.addAttribute("error", false);
				model.addAttribute("msg", "An email is on its way to " + u.getEmail() + " with instructions on what to do next.");
			}
			else {
				model.addAttribute("error", true);
				model.addAttribute("msg", "No such user with email: " + email);
				LOG.error("No such user for password reset request");
			}
		}
		
		return "user/forgotPassword";
	}
	
	private User updateSecretAndSaveUser(User u) {
		StandardPasswordEncoder encoder = new StandardPasswordEncoder();
		String encoded = encoder.encode(u.getEmail());
		u.setSecret(encoded);
		u = this.userService.save(u);
		return u;
	}
	
	private Long getSiteId(HttpServletRequest req) {
		Host h = this.cmsService.getHostService().getHost(req.getServerName(), req.getServerPort());
		Site s = null;
		
		if (h != null) {
			s = h.getSite();
			return s.getId();
		}
		
		return null;
	}

	@RequestMapping(value="/password/reset")
	public String passwordReset(
			HttpServletRequest req,
			ModelMap model) throws Exception {
		
		QandAList qalStored = null;
		User u = null;
		boolean error = false;
		
		String email = req.getParameter("email");
		String code = req.getParameter("code");
		model.addAttribute("_code", code);
		
		model.addAttribute("completed", false);
		model.addAttribute("error", false);
		
		try {
			if (StringUtils.isBlank(email) || StringUtils.isBlank(code)) {
				throw new ResourceException("Bad request");
			}
			
			u = this.userService.getByEmail(email);
			if (u == null) {
				throw new ResourceException("No such user");
			}
			model.addAttribute("_u", u);
					
			if (! (u.getEmail().equalsIgnoreCase(email) && u.getSecret().equals(code))) {
				throw new ResourceException("Bad user credentials - maybe the link in the email has expired");
			}
			
			qalStored = this.qandAService.getQandAList(u);
			int min = this.siteConfigCache.getIntValue(0L, SiteConfigKey.NUM_QANDA_MIN, 2);
			if (qalStored.getSize() < min) {
				throw new ResourceException("Cannot proceed - insufficient secret information recorded for user");
			}
			model.addAttribute("_qandA", qalStored);
			
		}
		catch (ResourceException e) {
			error = true;
			model.addAttribute("error", error);
			model.addAttribute("msg", e.getMessage());
		}
			
				
		if (error || req.getMethod().equalsIgnoreCase("get")) {
			return "user/passwordResetForm";
		}
		
		// This is a post request
		String password = req.getParameter("password");
		model.addAttribute("_passwordSubmitted", password);
			
		try {
			// NOTE: form doesn't provide the questions - get these from the db
			QandAList qalSubmitted = new QandAList().fillFromRequest(req, qalStored.getSize());
			model.addAttribute("_qandASubmitted", qalSubmitted);
			
			// The questions are not submitted by the form, but instead copied from the db
			for (int i = 0; i < qalStored.getSize(); i++) {
				qalSubmitted.getList().get(i).setQuestion(qalStored.getList().get(i).getQuestion());
			}
			
			if (! qalSubmitted.equals(qalStored)) {
				throw new ResourceException("Failed security checks - please check your answers");
			}
			
			if (StringUtils.isBlank(password)) {				
				throw new ResourceException("No password offered");
			}
			
			updateSecretAndSaveUser(u);			
			model.addAttribute("completed", true);
			model.addAttribute("msg", "Password successfully updated");
		}
		catch (ResourceException e) {
			model.addAttribute("error", true);
			model.addAttribute("msg", e.getMessage());
		}
		
		return "user/passwordResetForm";
	}

}
