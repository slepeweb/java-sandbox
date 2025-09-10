package com.slepeweb.cms.control;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.slepeweb.cms.bean.Host;
import com.slepeweb.cms.bean.QandAList;
import com.slepeweb.cms.bean.QandAList.QandA;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.constant.AttrName;
import com.slepeweb.cms.constant.FieldName;
import com.slepeweb.cms.service.QandAService;
import com.slepeweb.cms.service.UserService;
import com.slepeweb.common.util.HttpUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {
	
	//private static Logger LOG = Logger.getLogger(HighSecurityController.class);
	
	@Autowired private QandAService qandAService;
	@Autowired private UserService userService;
	
	@RequestMapping(value="/update/form", method=RequestMethod.GET)
	public String form(
		HttpServletRequest req,
		ModelMap model) throws Exception { 		
		
		User u = getUser(req);
		Long siteId = getSiteId(req);
		
		if (siteId != null && u.hasRole(siteId, User.MI6)) {
			QandAList qal = this.qandAService.getQandAList(u);
			model.addAttribute("_qandA", qal);
		}
		
		return "userForm"; 
	}

	@RequestMapping(value="/update/action", method=RequestMethod.POST)
	public String formSubmit(
		HttpServletRequest req,
		HttpServletResponse res,
		ModelMap model) throws Exception {
 		
		User u = getUser(req);
		
		// First update names, addresses, etc.
		u.setFirstName(req.getParameter(FieldName.FIRSTNAME));
		u.setLastName(req.getParameter(FieldName.LASTNAME));
		u.setEmail(req.getParameter(FieldName.EMAIL));
		u.setPhone(req.getParameter(FieldName.PHONE));		
				
		Long siteId = getSiteId(req);
		boolean qaUpdated = false;
		
		if (siteId != null && u.hasRole(siteId, User.MI6)) {
		
			// Now update secret Q and A's
			QandAList qal = new QandAList();
			String q, a;
			
			for (int i = 0; i < 3; i++) {
				q = req.getParameter("q" + i);
				if (StringUtils.isNotBlank(q)) {
					a = req.getParameter("a" + i);
					qal.getList().add(new QandA(q.trim(), a.trim()));
				}
			}
			
			if (qal.getList().size() > 0) {
				this.qandAService.update(u, qal);
				qaUpdated = true;
			}		
		}
		
		// Save the changes
		this.userService.save(u);

		// Replace user object in the session, assuming it has been updated
		req.getSession().setAttribute(AttrName.USER, u);
		
		// Re-direct request to form page
		String msg = "User details saved" + (qaUpdated ? " >>> Question/Answer pairs saved" : "");
		res.sendRedirect(String.format("%s/user/update/form?flash=%s", req.getContextPath(), HttpUtil.encodeUrl(msg)));
		return null; 
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

}
