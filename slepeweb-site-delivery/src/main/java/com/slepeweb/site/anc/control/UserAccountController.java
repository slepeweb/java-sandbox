package com.slepeweb.site.anc.control;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.cms.bean.CmsBeanFactory;
import com.slepeweb.cms.bean.Host;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.constant.AttrName;
import com.slepeweb.cms.service.LoginService;
import com.slepeweb.cms.service.UserService;
import com.slepeweb.common.service.SendMailService;
import com.slepeweb.common.util.HttpUtil;
import com.slepeweb.site.control.BaseController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/spring/anc")
public class UserAccountController extends BaseController {
	
	private static Logger LOG = Logger.getLogger(UserAccountController.class);
	public static final String USER_ATTR = "_user";
	public static final String ADMIN_EMAIL = "admin@buttigieg.org.uk";
	
	@Autowired private UserService userService;
	@Autowired private SendMailService sendMailService;
	@Autowired private LoginService loginService;
	
	/*
	 * A GET request renders a blank form. A POST request 
	 * a) returns an error message if the user is already registered
	 * b) otherwise creates a user record, and sends a notification email to the system admin
	 */
	@RequestMapping(value="/register/form")	
	public String registerForm (
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			HttpServletRequest req,
			ModelMap model) throws IOException {	
				
		Map<String, String[]> legend = parseMultiblockLegend(i.getFieldValue("legend"));
		
		pickBlockFields(i, legend, "registrationForm", model);
		
		if (req.getMethod().equalsIgnoreCase("post")) {
			// The registration form has been submitted. Create user record, create secret, send email.
			User u = CmsBeanFactory.makeUser().
					setFirstName(req.getParameter("firstname")).
					setLastName(req.getParameter("lastname")).
					setEmail(req.getParameter("email")).
					setPhone(req.getParameter("phone")).
					addRole(i.getSite().getId(), User.VISITOR);
			
			// Does this user already exist?
			User check = this.userService.get(u.getEmail());
			
			if (check == null) {				
				u = this.userService.save(u, i.getSite(), true);
				
				model.addAttribute("_u", u);
				String msg = composeNewRegistrationEmail(u);

				// Send email to administrator
				// Log message in case of email failure
				if (! this.sendMailService.sendMail(
					"admin@buttigieg.org.uk",
					"george@buttigieg.org.uk",
					"System Admin",
					"New Ancestry Registration",
					msg)) {
					
					LOG.warn("Failed to notify system administrator of new registrant:");
					LOG.warn(msg);
				}
				else {
					LOG.info(String.format("Email sent with interim status message [%s]", u.getEmail()));
				}
				
				String[] values = pickBlockFields(i, legend, "interimStatusMessage", model);
				values[1] = updateBodyField(model, values[1].replaceAll("__email__", u.getEmail()));
				
			}
			else {
				pickBlockFields(i, legend, "emailRegistered", model);
				model.addAttribute("_error_1", "This email is already registered");
				model.addAttribute("_u", u);
				LOG.info(String.format("Failed registration attempt: user already registered [%s]", u.getEmail()));
			}
		}
		
		return composeJspPath(shortSitename, "register/userform"); 
	}
	
	private String composeNewRegistrationEmail(User u) {
		StringBuilder sb = new StringBuilder("You have a new registration proposal to review:\n")
			.append("Id:    ").append(u.getId()).append("\n")
			.append("Name:  ").append(u.getFullName()).append("\n")
			.append("Email: ").append(u.getEmail()).append("\n")
			.append("Phone: ").append(u.getPhone()).append("\n");
		
		return sb.toString();
	}
	
	@RequestMapping(value="/profile")	
	public String profileUpdate (
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			HttpServletRequest req,
			ModelMap model) throws IOException {	
				
		Map<String, String[]> legend = parseMultiblockLegend(i.getFieldValue("legend"));
		
		User u = (User) req.getSession().getAttribute(USER_ATTR);
		
		model.addAttribute("_u", u);
		pickBlockFields(i, legend, "profileForm", model);
		
		if (req.getMethod().equalsIgnoreCase("post")) {			
			if (u != null) {
				StandardPasswordEncoder encoder = new StandardPasswordEncoder();
				
				if (encoder.matches(req.getParameter("current"), u.getPassword())) {
					u.
						setFirstName(req.getParameter("firstname")).
						setLastName(req.getParameter("lastname")).
						setPhone(req.getParameter("phone"));
					
					u = this.userService.save(u);				
					model.addAttribute("_u", u);
					pickBlockFields(i, legend, "profileUpdated", model);
				}
				else {
					pickBlockFields(i, legend, "passwordError", model);
					model.addAttribute("_error_1", "Incorrect password");
					LOG.info(String.format("The password entered is incorrect [%s]", u.getEmail()));
				}
			}
			else {
				LOG.error("System error: user is not logged in");
			}
		}
		
		return composeJspPath(shortSitename, "register/updateprofile"); 
	}
	
	/*
	 * The system admin gets this resource when approving an application for a user account.
	 * A check is made in case a) the specified user does not exist, or b) the user is active/enabled.
	 * If the test passes, the user is sent an email with a unique link to click.
	 */
	@RequestMapping(value="/register/approve/{userId}")	
	public String registerApprove (
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			@PathVariable Long userId,
			HttpServletResponse res,
			ModelMap model) throws IOException {	
				
		Map<String, String[]> legend = parseMultiblockLegend(i.getFieldValue("legend"));
		
		doNotCachePage(i, res);
		User u = this.userService.get(userId);
		
		if (u != null) {
			if (u.isEnabled() || StringUtils.isNotBlank(u.getSecret())) {
				model.addAttribute("_message", "User state is incompatible with approval process.");
			}
			else {
				String secret = encodeSecret(u);
				u.setSecret(secret);
				this.userService.partialUpdate(u);
				
				Host h = i.getSite().getDeliveryHost();
				String href = String.format("%s/%s/login/register?view=password/%s", 
						h != null ? h.getNamePortAndProtocol() : "",
						i.getLanguage(), 
						secret);
				
				String [] values = pickBlockFields(i, legend, "validationEmailContent", model);
				values[1] = updateBodyField(model, values[1].
						replaceAll("__user__", u.getFullName()).
						replaceAll("__href__", href));
				
				model.addAttribute("_message", "User record for " + u.getFullName() + " updated, and user notified by email.");
				
				if (! this.sendMailService.sendMail(ADMIN_EMAIL, u.getEmail(), u.getFullName(), "Set your password", values[1])) {
					model.addAttribute("_message", "Send email failure");
				}
			}
		}
		else {
			model.addAttribute("_message", String.format("User does not exist [%d]", userId));
		}
		
		return composeJspPath(shortSitename, "register/approve"); 
	}
	
	private String encodeSecret(User u) {
		StandardPasswordEncoder encoder = new StandardPasswordEncoder();
		return encoder.encode(u.getEmail());
	}
	
	private void doNotCachePage(Item item, HttpServletResponse res) {
		long requestTime = new Date().getTime();
		long lastModified = item.getType().isMedia() ? 
				item.getDateUpdated().getTime() :
				requestTime;
				
		HttpUtil.setCacheHeaders(new Date().getTime(), lastModified, 0L, 0L, res);		
	}
	
	@RequestMapping(value="/password/reset/{secret}")	
	public String forgottenReset (
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			@PathVariable String secret,
			HttpServletRequest req,
			ModelMap model) throws IOException {	

		registerPassword(i, shortSitename, secret, req, model);
		return composeJspPath(shortSitename, "forgotten/passwordform"); 
	}	
	
	@RequestMapping(value="/register/password/{secret}")	
	public String registerPassword (
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			@PathVariable String secret,
			HttpServletRequest req,
			ModelMap model) throws IOException {	
				
		Map<String, String[]> legend = parseMultiblockLegend(i.getFieldValue("legend"));
		
		User u = this.userService.getBySecret(secret);
		model.addAttribute("_user", u);
		
		if (u != null) {
			if (req.getMethod().equalsIgnoreCase("get")) {
				model.addAttribute("_secret", secret);
				String[] fieldValues = pickBlockFields(i, legend, "passwordForm", model);
				fieldValues[1] = updateBodyField(model, fieldValues[1].replaceAll("__firstname__", u.getFirstName()));
			}
			else if (req.getMethod().equalsIgnoreCase("post")) {
				String passwordA = req.getParameter("pwdA");
				String passwordB = req.getParameter("pwdB");
				
				if (passwordA.equals(passwordB)) {
					StandardPasswordEncoder encoder = new StandardPasswordEncoder();
					u.setPassword(encoder.encode(passwordA));
					u.setEnabled(true);
					u.setSecret(null);
					this.userService.partialUpdate(u);

					pickBlockFields(i, legend, "processComplete", model);
					LOG.info(String.format("User successfully set password; process complete [%s]", u.getEmail()));
				}
				else {
					model.addAttribute("_error_1", "Passwords must match");
					pickBlockFields(i, legend, "nonMatchingPasswords", model);
				}
			}
		}
		else {
			// Invalid secret key ... hacking attempt?
			model.addAttribute("_error_2", "User state not compatible with password reset.");
			LOG.warn("Bad secret used in process to set password");
		}
		
		return composeJspPath(shortSitename, "register/passwordform"); 
	}
	
	@RequestMapping(value="/password/forgotten")	
	public String forgottenPassword (
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			HttpServletRequest req,
			ModelMap model) throws IOException {	
				
		Map<String, String[]> legend = parseMultiblockLegend(i.getFieldValue("legend"));
		
		if (req.getMethod().equalsIgnoreCase("post")) {
			// The forgotten password form has been submitted. Create secret, update user record, send email.
			String email = req.getParameter("email");
			
			if (StringUtils.isNotBlank(email)) {
				User u = this.userService.get(email);
								
				if (u != null) {
					LOG.info(String.format("User has forgotten password [%s]", u.getEmail()));
					
					model.addAttribute("_u", u);
					String secret = encodeSecret(u);	
					
					String href = String.format("http://ancestry.slepeweb.com:8081/%s/login/forgotten?view=reset/%s", 
							i.getLanguage(), secret);
					
					String[] fieldValues = pickBlockFields(i, legend, "validationEmailContent", model);
					String msg = updateBodyField(model, fieldValues[1].replaceAll("__href__", href));
					
					// Disable user record, and store secret
					u.setEnabled(false);
					u.setSecret(secret);
					this.userService.partialUpdate(u);
					LOG.info(String.format("User has been disabled, until password has been reset [%s]", u.getEmail()));
	
					// Send email to user with link to reset password.
					// Log message in case of email failure.
					if (! this.sendMailService.sendMail(
						"admin@buttigieg.org.uk",
						u.getEmail(),
						u.getFullName(),
						"Forgotton Password Request",
						msg)) {
						
							LOG.error("Email failure, sending message to user: " + msg);
					}
					
					fieldValues = pickBlockFields(i, legend, "interimStatusMessage", model);
					updateBodyField(model, fieldValues[1].replaceAll("__email__", email));
				}
				else {
					model.addAttribute("_error_1", "This email is NOT registered");
					model.addAttribute("_email", email);
					pickBlockFields(i, legend, "emailNotRegisteredError", model);
					LOG.info(String.format("Attempt to change password for an un-registered account [%s]", email));
				}
			}
			else {
				model.addAttribute("_error_1", "Email address NOT specified");
				model.addAttribute("_email", email);
				pickBlockFields(i, legend, "emailIdentificationForm", model);
			}
		}
		else {
			pickBlockFields(i, legend, "emailIdentificationForm", model);
		}
		
		return composeJspPath(shortSitename, "forgotten/emailform"); 
	}
	
	@RequestMapping(value="/changepwd")	
	public String changePasswordForm (
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			HttpServletRequest req,
			ModelMap model) throws IOException {	
				
		Map<String, String[]> legend = parseMultiblockLegend(i.getFieldValue("legend"));
		
		if (req.getMethod().equalsIgnoreCase("post")) {
			// The current and new passwords have been submitted. 
			String currentPassword = req.getParameter("current");
			String pwdA = req.getParameter("pwdA");
			String pwdB = req.getParameter("pwdB");
			
			if (StringUtils.isNotBlank(currentPassword) && StringUtils.isNotBlank(pwdA) && StringUtils.isNotBlank(pwdB)) {
				User u = (User) req.getSession().getAttribute(USER_ATTR);
								
				if (u != null) {					
					// First check his current password
					StandardPasswordEncoder encoder = new StandardPasswordEncoder();					
					if (encoder.matches(currentPassword, u.getPassword())) {
						if (pwdA.equals(pwdB)) {
							u.setPassword(encoder.encode(pwdA));
							this.userService.partialUpdate(u);
							pickBlockFields(i, legend, "processComplete", model);
							this.loginService.logout(req);
							LOG.info(String.format("User successfully changed password [%s]", u.getEmail()));
						}
						else {
							model.addAttribute("_error_1", "New password mis-match - they should be identical");
							LOG.info(String.format("Mis-matching passwords [%s]", u.getEmail()));
						}
					}
					else {
						model.addAttribute("_error_1", "The value entered for current password was not recognised");
						pickBlockFields(i, legend, "currentPasswordError", model);
						LOG.info(String.format("Failed attempt to change password - current password error [%s]", u.getEmail()));
					}
				}
				else {
					model.addAttribute("_error_1", "This user is NOT currently logged in, and shouldn't have got here!");
					LOG.warn("Attempt to change password when not currently logged in");
				}
			}
			else {
				model.addAttribute("_error_1", "Change form is incomplete - shouldn't have got here!");
				pickBlockFields(i, legend, "emailIdentificationForm", model);
			}
		}
		else {
			pickBlockFields(i, legend, "setPasswordForm", model);
		}
		
		return composeJspPath(shortSitename, "register/changepassword"); 
	}
	
	private Map<String, String[]> parseMultiblockLegend(String s) {
		Map<String, String[]> map = new HashMap<String, String[]>();
		String[] parts;
		
		for (String line : s.split("[\\n\\r]")) {
			if (! StringUtils.isBlank(line) && ! line.startsWith("//")) {
				parts = line.split("\\.");
				if (parts.length == 2) {
					map.put(parts[1].trim(), composeMultiblockFieldNames(Integer.valueOf(parts[0])));
				}
			}
		}
		
		return map;
	}
	
	private String[] pickBlockFields(Item i, Map<String, String[]> legend, String key, ModelMap model) {
		String[] fields = legend.get(key);
		String[] values = new String[] {"", ""};
		
		if (fields != null && fields.length == 2) {
			values[0] = i.getFieldValue(fields[0]);
			values[1] = i.getFieldValue(fields[1]);
			model.addAttribute("_heading", values[0]);
			model.addAttribute("_body", values[1]);
			model.addAttribute("_block", key);
		}
		else {
			LOG.error(String.format("Multiblock content error [%s]", i.getPath()));
		}
		
		return values;
	}
	
	private String[] composeMultiblockFieldNames(int num) {
		return new String[] {"heading_" + num, "body_" + num};
	}

	private String updateBodyField(ModelMap model, String value) {
		model.put("_body", value);
		return value;
	}
}
