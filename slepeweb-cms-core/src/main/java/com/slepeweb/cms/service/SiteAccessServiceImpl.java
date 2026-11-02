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
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.LinkType;
import com.slepeweb.cms.bean.SolrDocument4Cms;
import com.slepeweb.cms.bean.User;

import jakarta.annotation.PostConstruct;

@Repository
public class SiteAccessServiceImpl extends BaseServiceImpl implements SiteAccessService {
	
	/*
	 * The columns in the 'access' table in the database are:
	 * 
	 * siteid	: self-explanatory
	 * mode		: r (read/site delivery access) or w (write/editorial access)
	 * ordering : numeric setting order in which rules are tested
	 * name		: rule name, used to sort rules in order, which is VERY significant ^^
	 * 
	 * Then we have a set of columns that hold item properties to match against. Each
	 * of these columns may contain a regular expression (re), otherwise NULL. For an item to 
	 * match a rule, ALL non-NULL rules must match the item's properties. (If an re is NULL,
	 * then the corresponding item property is ignored when considering the match.)
	 * 
	 * tag		: re (regular expression) to match item's tag values (ie a string of values)
	 * template	: re to match item's spring template (ie its 'forward' property)
	 * path		: re to match the item's path
	 * ownerid	: re to match item's owner
	 * 
	 * If ALL non-NULL rules match this item, then the next 2 columns are considered.
	 * 
	 * access	: 1  (access is granted, and further rules can be ignored)
	 *            0  (access is granted BUT further rules must be considered)
	 *            -1 (access is denied)

	 * role		: re to match the user's roles
	 * 
	 * Finally, the last column can be used to enable/disable the rule.
	 * 
	 * enabled	: 1 (yes) or 0 (no)
	 * 
	 * ^^
	 * Rules are grouped by mode, and for each mode, ordered by 'ordering', in numeric order.
	 */
	
	private static Logger LOG = Logger.getLogger(SiteAccessServiceImpl.class);
	
	@Autowired private CmsService cmsService;
	@Autowired private AccessService accessService;
	@Autowired private PasskeyService passkeyService;
	@Autowired private UserService userService;

	private Map<Long, List<AccessRule>> readRules;
	private Map<Long, List<AccessRule>> writeRules;
	
	@PostConstruct
	private void init() {
		forceCacheRefresh();
	}
	
	public boolean isAccessible(Item i) {
		if (this.cmsService.isEditorialContext()) {
			if (i.getUser() == null) {
				return false;
			}
			
			Long siteId = i.getSite().getId();
			boolean accessible = isAccessible(i, getWriteRules(siteId));
			
			/*
			 *  Is item a component? If so, for it to be accessible, ancestor page item must
			 *  also be accessible.
			 */			
			if (accessible) {
				Item pageOwningComponent = getOwningPage(i);
				if (pageOwningComponent != null) {
					accessible = isAccessible(pageOwningComponent, getWriteRules(siteId));
				}
			}
			
			return accessible;
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

			return isAccessible(i, getReadRules(i.getSite().getId()));
		}
	}
	
	
	private Item getOwningPage(Item child) {
		// Ignore root item, and first level items
		if (child.getPath().split("\\/").length <= 2) {
			return null;
		}
		
		Link parentLink = child.getOrthogonalParentLink();
		if (parentLink.getType().equals(LinkType.component)) {
			return getOwningPage(parentLink.getChild());
		}
		
		return child;
	}
	
	private boolean isAccessible(Item i, List<AccessRule> rules) {		

		boolean accessible = false;
		
		for (AccessRule rule : rules) {
			if (itemMatchesRule(i, rule)) {
				accessible = rule.givesAccess();
				
				if (! accessible ) {
					LOG.info(String.format("Access denied: item = %s >>> rule = %s", i, rule));
					return accessible;
				}
				else {
					LOG.debug(String.format("Access granted: item = %s >>> rule = %s", i, rule));
				}
				
				if (! rule.considerNextRule()) {
					break;
				}
			}
		}
		
		return accessible; 
	}
	
	private List<AccessRule> getReadRules(Long siteId) {
		List<AccessRule> list = this.readRules.get(siteId);
		if (list == null) {
			synchronized (this) {
				list = this.accessService.getReadable(siteId);
				this.readRules.put(siteId, list);
			}
		}
		return list;
	}

	private List<AccessRule> getWriteRules(Long siteId) {
		List<AccessRule> list = this.writeRules.get(siteId);
		if (list == null) {
			synchronized (this) {
				list = this.accessService.getWriteable(siteId);
				this.writeRules.put(siteId, list);
			}
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
			if (documentMatchesRule(doc, rule, u)) {
				
				// This rule matches. All later rules in the list are ignored.
				if (rule.givesAccess()) {
					/*
					 * ie. the 'access' column in db has value 'true', which means
					 * the rule is satisfied, and we don't need to consult the 'role' column.
					 */
					return true;
				}
			}
		}
			
		return false; 
	}

	private boolean itemMatchesRule(Item i, AccessRule rule) {
		boolean matches = ruleMatches(
				i.getType().getName(),
				i.getTemplate() == null ? null : i.getTemplate().getController(), 
				
				// Don't evaluate item tags unless rule specifies tag pattern
				// This will eliminate unnecessary db queries
				StringUtils.isNotBlank(rule.getTagPattern()) ? i.getTagsAsString() : null, 
						
				i.getPath(), 
				i.getOwnerId(), 
				rule);
		
		matches =  matches && userRolesMatchRule(i.getUser(), i.getSite().getId(), rule);
		return matches;
	}
	
	private boolean documentMatchesRule(SolrDocument4Cms doc, AccessRule rule, User u) {
		boolean matches = ruleMatches(doc.getType(), doc.getTemplate(), doc.getTags(), doc.getPath(), doc.getOwnerId(), rule);
		matches =  matches && userRolesMatchRule(u, Long.valueOf(doc.getSiteId()), rule);
		return matches;
	}

	private boolean ruleMatches(String itemType, String springTemplatePath, String tagList, String path, Long ownerId, AccessRule rule) {
		
		// Must match ALL constraints to return positive
		
		if (StringUtils.isNotBlank(rule.getTypePattern())) {
			// Item type rule applies
			if (
				itemType == null || 
				! itemType.matches(rule.getTypePattern())) {
			
				return false; 
			}
		}

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
	
	private boolean userRolesMatchRule(User u, Long siteId, AccessRule rule) {
		
		/*
		 *  If a user does NOT need to be logged in in order to access an item, then the 'role' column
		 *  in the 'access' table MUST be blank, or null.
		 */
		if (StringUtils.isBlank(rule.getRolePattern())) {
			return true;
		}
		
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
	
	public synchronized void forceCacheRefresh() {
		this.readRules = new HashMap<Long, List<AccessRule>>();
		this.writeRules = new HashMap<Long, List<AccessRule>>();		
		LOG.info("(Re-)initialised the site access rules cache");
	}
}
