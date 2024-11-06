package com.slepeweb.cms.control;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.slepeweb.cms.bean.Field.FieldType;
import com.slepeweb.cms.bean.FieldEditorSupport;
import com.slepeweb.cms.bean.FieldForType;
import com.slepeweb.cms.bean.FieldValue;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemGist;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.ItemUpdateHistory;
import com.slepeweb.cms.bean.ItemUpdateRecord.Action;
import com.slepeweb.cms.bean.LinkName;
import com.slepeweb.cms.bean.LinkType;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.Tag;
import com.slepeweb.cms.bean.TagInputSupport;
import com.slepeweb.cms.bean.UndoRedoStatus;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.bean.guidance.IGuidance;
import com.slepeweb.cms.component.CmsHooker;
import com.slepeweb.cms.component.ICmsHook;
import com.slepeweb.cms.constant.AttrName;
import com.slepeweb.cms.constant.ItemTypeName;
import com.slepeweb.cms.service.CmsService;
import com.slepeweb.cms.service.LinkNameService;
import com.slepeweb.cms.service.LinkTypeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class BaseController {
	
	@Autowired protected CmsService cmsService;
	@Autowired protected CmsHooker cmsHooker;
	@Autowired protected LinkTypeService linkTypeService;
	@Autowired protected LinkNameService linkNameService;
	
	private String contextPath;

	@ModelAttribute(value="applicationContextPath")
	public String getApplicationContextPath(HttpSession session) {
		if (this.contextPath == null) {
			this.contextPath = session.getServletContext().getContextPath();
		}
		return this.contextPath;
	}
	
	@ModelAttribute(value="_cmsService")
	public CmsService getCmsService() {
		return this.cmsService;
	}
	
	@ModelAttribute(value=AttrName.USER)
	protected User getUser(HttpServletRequest req) {
		return (User) req.getSession().getAttribute(AttrName.USER);
	}
	
	protected String userLog(User u, Object... message) {
		StringBuilder sb = new StringBuilder(String.format("User '%s'", u.getFullName()));
		for (Object o : message) {
			sb.append(" ").append(o.toString());
		}		
		return sb.toString();
	}
	
	@ModelAttribute(value="_loglevel")
	protected boolean getLogLevelTrigger(@RequestParam(value="loglevel", required=false) String trigger) {
		if (trigger != null) {
			this.cmsService.getLoglevelUpdateService().updateLoglevels();
			return true;
		}		
		return false;
	}
	
	@ModelAttribute(value="_productTypeId")
	protected String getProductTypeId() {
		ItemType productType = this.cmsService.getItemTypeService().getItemType(ItemTypeName.PRODUCT);
		if (productType != null) {
			return String.valueOf(productType.getId());
		}
		return "0";
	}
	
	protected Map<String, List<FieldEditorSupport>> fieldEditorSupport(Item i) {
		Map<String, FieldValue> languageValuesMap;
		Map<String, List<FieldEditorSupport>> fieldSupport = new HashMap<String, List<FieldEditorSupport>>();
		List<FieldEditorSupport> list;
		FieldValue fv;
		FieldEditorSupport fes;
		String variable;
		
		ICmsHook hook = this.cmsHooker.getHook(i.getSite().getShortname());
		IGuidance guidance = null;
		
		for (String language : i.getSite().getAllLanguages()) {
			languageValuesMap = i.getFieldValueSet().getFieldValues(language);
			list = new ArrayList<FieldEditorSupport>();
			
			for (FieldForType fft : i.getType().getFieldsForType(false)) {
				if (language.equals(i.getSite().getLanguage()) || fft.getField().isMultilingual()) {
					variable = fft.getField().getVariable();
					guidance = hook.getFieldGuidance(variable);
					
					fes = new FieldEditorSupport().
							setField(fft.getField()).
							setLabel(fft.getField().getName()).
							setGuidance(guidance);
					
					fv = languageValuesMap == null ? null : languageValuesMap.get(variable);
					
					if (fft.getField().getType() != FieldType.layout) {
						if (fv == null) {
							fes.setInputTag(fft.getField().getInputTag(guidance));
						}
						else {
							fes.setFieldValue(fv);
							fes.setInputTag(fv.getInputTag(guidance));
						}
					}
					
					list.add(fes);
				}
			}
			
			fieldSupport.put(language, list);
		}
		
		return fieldSupport;
	}
	
	protected Item getEditableVersion(Long origId, User u) throws RuntimeException {
		return getEditableVersion(origId, u, false);
	}
	
	protected Item getEditableVersion(Long origId, User u, boolean throwable) throws RuntimeException {
		Item i = this.cmsService.getItemService().getEditableVersion(origId);
		
		if (i != null) {
			i.setUser(u);
			checkAccess(i, throwable);
		}
		return i;
	}
	
	protected boolean checkAccess(Item i, boolean throwable) throws RuntimeException {
		boolean access = i.isAccessible();	
		
		if (throwable && ! access) {
			throw new RuntimeException("No access to item");
		}
		
		return access;
	}	
	
	@SuppressWarnings("unchecked")
	protected TagInputSupport getTagInfo(Long siteId, HttpServletRequest req) {
		TagInputSupport tis = new TagInputSupport();
		tis.setAll(this.cmsService.getTagService().getDistinctTagValues4Site(siteId));		
		
		// Recently added tags are stored in the session
		Object recent = req.getSession().getAttribute(AttrName.RECENT_TAGS);
		if (recent == null) {
			recent = new ArrayList<Tag>();
			req.getSession().setAttribute(AttrName.RECENT_TAGS, recent);
		}
		
		tis.setRecent((List<String>) recent);
		return tis;
	}

	@SuppressWarnings("unchecked")
	protected Map<Long, ItemGist> getFlaggedItems(HttpServletRequest request) {
		Map<Long, ItemGist> flags = (Map<Long, ItemGist>) request.getSession().getAttribute(AttrName.FLAGGED_ITEMS);
		if (flags == null) {
			flags = new HashMap<Long, ItemGist>();
			request.getSession().setAttribute(AttrName.FLAGGED_ITEMS, flags);
		}
		return flags;
	}
	
	protected List<ItemGist>  getSortedFlaggedItems(Map<Long, ItemGist> flaggedItems) {
		List<ItemGist> gists = new ArrayList<ItemGist>(flaggedItems.size());
		gists.addAll(flaggedItems.values());
		gists.sort(new Comparator<ItemGist>() {
			@Override
			public int compare(ItemGist o1, ItemGist o2) {
				return o1.getDate().compareTo(o2.getDate());
			}
		});
		
		return gists;
	}
	
	protected ItemUpdateHistory getItemUpdateHistory(HttpServletRequest request) {
		ItemUpdateHistory h = (ItemUpdateHistory) request.getSession().getAttribute(AttrName.UNDO_REDO_STATUS);
		if (h == null) {
			h = new ItemUpdateHistory();
			request.getSession().setAttribute(AttrName.UNDO_REDO_STATUS, h);
		}
		
		return h;
	}
	
	protected UndoRedoStatus pushItemUpdateRecord(HttpServletRequest request, Item before, Item after, Action a) {
		ItemUpdateHistory h = getItemUpdateHistory(request);
		h.push(before, after, a);
		return new UndoRedoStatus(h);
	}
	
	protected Map<String, String> getLinkTypeNameOptions(Site s) {
		Map<String, String> m = new HashMap<String, String> ();
		
		List<LinkType> types = this.linkTypeService.getLinkTypes();
		List<LinkName> names;
		String json;
		boolean isFirst;
		
		for (LinkType t : types) {
			names = this.linkNameService.getLinkNames(s.getId(), t.getId());
			json = "[";
			isFirst = true;
			
			for (LinkName ln : names) {
				if (! isFirst) {
					json += ", ";
				}
				json += String.format("'%s'", ln.getName());
				isFirst = false;
			}
			json += "]";
			m.put(t.getName(), json);
		}
		return m;
	}
}

