package com.slepeweb.cms.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.AccessRule;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Role;
import com.slepeweb.cms.bean.User;

@Repository
public class SiteAccessServiceImpl extends BaseServiceImpl implements SiteAccessService {
	
//	private static Logger LOG = Logger.getLogger(HostServiceImpl.class);	
	
	@Autowired private CmsService cmsService;
	@Autowired private AccessService accessService;

	private Map<String, List<AccessRule>> readRules = new HashMap<String, List<AccessRule>>();
	private Map<String, List<AccessRule>> writeRules = new HashMap<String, List<AccessRule>>();
	
	public boolean isAccessible(Item i) {
		String springTemplatePath = i.getTemplate() != null ? i.getTemplate().getController() : null;
		return isAccessible(i, springTemplatePath);
	}
	
	public boolean isAccessible(Item i, String springTemplatePath) {
		if (this.cmsService.isEditorialContext()) {
			return isAccessible(i, null, getWriteRules(i.getSite().getShortname()));
		}
		else {
			if (i.getSite().isSecured()) {
				return isAccessible(i, springTemplatePath, getReadRules(i.getSite().getShortname()));
			}
			else {
				return true;
			}
		}
	}
	
	private boolean isAccessible(Item i, String springTemplatePath, List<AccessRule> rules) {		
		// The first matching rule applies - rules should be ordered from most specific to least.
		for (AccessRule rule : rules) {
			if (itemMatchesRule(i, springTemplatePath, rule)) {
				
				// This rule matches. All later rules in the list are ignored.
				if (rule.givesAccess()) {
					/*
					 * ie. the 'access' column in db has value 'true', which means
					 * the rule is satisfied, and we don't need to consult the 'role' column.
					 */
					return true;
				}
				
				/*
				 * So, the 'access' column in db has value 'false', which means
				 * we must now test whether the user has the role specified in the 'role' column.
				 * User does NOT get access by this rule UNLESS he has specified role.
				 */
				return userRolesMatchRule(i.getUser(), rule);
			}
		}
			
		return false; 
	}

	private List<AccessRule> getReadRules(String siteName) {
		List<AccessRule> list = this.readRules.get(siteName);
		if (list == null) {
			list = this.accessService.getReadable(siteName);
			this.readRules.put(siteName, list);
		}
		return list;
	}

	private List<AccessRule> getWriteRules(String siteName) {
		List<AccessRule> list = this.writeRules.get(siteName);
		if (list == null) {
			list = this.accessService.getWriteable(siteName);
			this.writeRules.put(siteName, list);
		}
		return list;
	}

	public boolean itemMatchesRule(Item i, AccessRule rule) {
		return itemMatchesRule(i, 
				i.getTemplate() == null ? null : i.getTemplate().getController(), rule);
	}
	
	public boolean itemMatchesRule(Item i, String springTemplatePath, AccessRule rule) {
		// Must match ALL constraints
		
		if (StringUtils.isNotBlank(rule.getTemplatePattern())) {
			// Template rule applies
			if (
				springTemplatePath == null || 
				! springTemplatePath.matches(rule.getTemplatePattern())) {
			
				return false; 
			}
		}

		if (StringUtils.isNotBlank(rule.getItemTypePattern())) {
			// Item type rule applies
			if (! i.getType().getName().matches(rule.getItemTypePattern())) {			
				return false; 
			}
		}

		if (StringUtils.isNotBlank(rule.getItemPathPattern())) {
			// Item path rule applies
			if (! i.getPath().matches(rule.getItemPathPattern())) {			
				return false; 
			}
		}
		
		return true;
	}
	
	public boolean userRolesMatchRule(User u, AccessRule rule) {
		
		if (u != null) {
			// Must match ANY of the user's roles
			for (Role r : u.getRoles()) {
				if (r.getName().matches(rule.getRolePattern())) {
					return true;
				}
			}
		}
		
		return false;
	}
}
