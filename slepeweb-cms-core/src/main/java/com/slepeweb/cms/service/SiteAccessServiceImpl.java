package com.slepeweb.cms.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.AccessRule;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.SolrDocument4Cms;
import com.slepeweb.cms.bean.User;

@Repository
public class SiteAccessServiceImpl extends BaseServiceImpl implements SiteAccessService {
	
//	private static Logger LOG = Logger.getLogger(HostServiceImpl.class);	
	
	@Autowired private CmsService cmsService;
	@Autowired private AccessService accessService;

	private Map<Long, List<AccessRule>> readRules = new HashMap<Long, List<AccessRule>>();
	private Map<Long, List<AccessRule>> writeRules = new HashMap<Long, List<AccessRule>>();
	
	public boolean isAccessible(Item i) {
		if (this.cmsService.isEditorialContext()) {
			return isAccessible(i, getWriteRules(i.getSite().getId()));
		}
		else {
			if (i.getSite().isSecured()) {
				return isAccessible(i, getReadRules(i.getSite().getId()));
			}
			else {
				return true;
			}
		}
	}
	
	private boolean isAccessible(Item i, List<AccessRule> rules) {		
		// The first matching rule applies - rules should be ordered from most specific to least.
		for (AccessRule rule : rules) {
			if (itemMatchesRule(i, rule)) {
				
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
				return userRolesMatchRule(i.getUser(), i.getSite().getId(), rule);
			}
		}
			
		return false; 
	}

	private List<AccessRule> getReadRules(Long siteId) {
		List<AccessRule> list = this.readRules.get(siteId);
		if (list == null) {
			list = this.accessService.getReadable(siteId);
			this.readRules.put(siteId, list);
		}
		return list;
	}

	private List<AccessRule> getWriteRules(Long siteId) {
		List<AccessRule> list = this.writeRules.get(siteId);
		if (list == null) {
			list = this.accessService.getWriteable(siteId);
			this.writeRules.put(siteId, list);
		}
		return list;
	}

	public boolean isAccessible(SolrDocument4Cms doc, User u) {
		Long key = Long.valueOf(doc.getSiteId());
		boolean access = true;
		
		if (this.cmsService.isEditorialContext()) {
			access = isAccessible(doc, u, getWriteRules(key));
		}
		else {
			access = isAccessible(doc, u, getReadRules(key));
		}
		
		doc.setAccessible(access);
		return access;
	}
	
	private boolean isAccessible(SolrDocument4Cms doc, User u, List<AccessRule> rules) {		
		// The first matching rule applies - rules should be ordered from most specific to least.
		for (AccessRule rule : rules) {
			if (documentMatchesRule(doc, rule)) {
				
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
				return userRolesMatchRule(u, Long.valueOf(doc.getSiteId()), rule);
			}
		}
			
		return false; 
	}

	public boolean itemMatchesRule(Item i, AccessRule rule) {
		return ruleMatches(
				i.getTemplate() == null ? null : i.getTemplate().getController(), 
				i.getType().getName(), i.getPath(), rule);
	}
	
	public boolean documentMatchesRule(SolrDocument4Cms doc, AccessRule rule) {
		return ruleMatches(null, doc.getType(), doc.getPath(), rule);
	}

	public boolean ruleMatches(String springTemplatePath, String type, String path, AccessRule rule) {
		
		// Must match ALL constraints to return positive
		
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
			if (! type.matches(rule.getItemTypePattern())) {			
				return false; 
			}
		}

		if (StringUtils.isNotBlank(rule.getItemPathPattern())) {
			// Item path rule applies
			if (! path.matches(rule.getItemPathPattern())) {			
				return false; 
			}
		}
		
		return true;
	}
	
	public boolean userRolesMatchRule(User u, Long siteId, AccessRule rule) {
		
		if (u != null) {
			// Must match ANY of the user's roles
			for (String r : u.getRoles(siteId)) {
				if (r.matches(rule.getRolePattern())) {
					return true;
				}
			}
		}
		
		return false;
	}
}
