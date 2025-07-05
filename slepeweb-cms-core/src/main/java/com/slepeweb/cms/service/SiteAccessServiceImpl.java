package com.slepeweb.cms.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.AccessRule;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.SolrDocument4Cms;
import com.slepeweb.cms.bean.User;

@Repository
public class SiteAccessServiceImpl extends BaseServiceImpl implements SiteAccessService {
	
	private static Logger LOG = Logger.getLogger(SiteAccessServiceImpl.class);
	
	@Autowired private CmsService cmsService;
	@Autowired private AccessService accessService;
	@Autowired private PasskeyService passkeyService;
	@Autowired private UserService userService;

	private Map<Long, List<AccessRule>> readRules = new HashMap<Long, List<AccessRule>>();
	private Map<Long, List<AccessRule>> writeRules = new HashMap<Long, List<AccessRule>>();
	
	public boolean isAccessible(Item i) {
		if (this.cmsService.isEditorialContext()) {
			if (i.getUser() == null) {
				return false;
			}
			return isAccessible(i, getWriteRules(i.getSite().getId()));
		}
		else {
			if (! i.getSite().isSecured()) {
				// site is not secured, so all items are accessible
				return true;
			}
			
			if (i.getUser() == null && i.getRequestPack().hasPasskey()) {
				// User is not logged in. If valid passkey is offered, log him in
				if (this.passkeyService.validateKey(i.getRequestPack().getPasskey())) {
					String alias = i.getRequestPack().getPasskey().getAlias();
					i.getRequestPack().setUser(this.userService.get(alias));
					LOG.info(String.format("User %s logged in with valid passkey", i.getRequestPack().getUser().getAlias()));
				}
			}

			if (i.getUser() == null) {
				return false;
			}
			
			return isAccessible(i, getReadRules(i.getSite().getId()));
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

	private boolean itemMatchesRule(Item i, AccessRule rule) {
		return ruleMatches(
				i.getTemplate() == null ? null : i.getTemplate().getController(), 
				
				// Don't evaluate item tags unless rule specifies tag pattern
				// This will eliminate unnecessary db queries
				StringUtils.isNotBlank(rule.getTagPattern()) ? i.getTagsAsString() : null, 
						
				i.getPath(), i.getOwnerId(), rule);
	}
	
	private boolean documentMatchesRule(SolrDocument4Cms doc, AccessRule rule) {
		return ruleMatches(null, doc.getType(), doc.getPath(), doc.getOwnerId(), rule);
	}

	private boolean ruleMatches(String springTemplatePath, String tagList, String path, Long ownerId, AccessRule rule) {
		
		// Must match ALL constraints to return positive
		
		if (StringUtils.isNotBlank(rule.getTemplatePattern())) {
			// Template rule applies
			if (
				springTemplatePath == null || 
				! springTemplatePath.matches(rule.getTemplatePattern())) {
			
				return false; 
			}
		}

		if (StringUtils.isNotBlank(rule.getTagPattern())) {
			// Item tags rule applies
			
			if (StringUtils.isBlank(tagList)) {
				return false;
			}
			
			boolean tagIsMatch = false;
			
			for (String tag : tagList.split(",")) {
				if (tag.trim().matches(rule.getTagPattern())) {		
					tagIsMatch = true; 
					break;
				}
			}
			
			if (! tagIsMatch) {
				return false;
			}
		}

		if (StringUtils.isNotBlank(rule.getItemPathPattern())) {
			// Item path rule applies
			if (! path.matches(rule.getItemPathPattern())) {			
				return false; 
			}
		}
		
		if (StringUtils.isNotBlank(rule.getOwnerIdPattern())) {
			// Item owner rule applies
			if (ownerId == null || ! String.valueOf(ownerId).matches(rule.getOwnerIdPattern())) {			
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
