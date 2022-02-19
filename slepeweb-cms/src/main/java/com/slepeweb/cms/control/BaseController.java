package com.slepeweb.cms.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.slepeweb.cms.bean.Field.FieldType;
import com.slepeweb.cms.bean.FieldEditorSupport;
import com.slepeweb.cms.bean.FieldForType;
import com.slepeweb.cms.bean.FieldValue;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.Tag;
import com.slepeweb.cms.bean.TagInputSupport;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.constant.ItemTypeName;
import com.slepeweb.cms.service.CmsService;
import com.slepeweb.cms.service.ValidationService;

@Controller
public class BaseController {
	
	public static final String TAG_INPUT_SUPPORT_ATTR = "_tis";
	public static final String RECENT_TAGS_ATTR = "_recentTags";
	
	@Autowired protected CmsService cmsService;
	@Autowired private ValidationService validationService;
	
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
	
	@ModelAttribute(value="_user")
	protected User getUser(HttpServletRequest req) {
		return (User) req.getSession().getAttribute("_user");
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
		
		for (String language : i.getSite().getAllLanguages()) {
			languageValuesMap = i.getFieldValueSet().getFieldValues(language);
			list = new ArrayList<FieldEditorSupport>();
			
			for (FieldForType fft : i.getType().getFieldsForType(false)) {
				if (language.equals(i.getSite().getLanguage()) || fft.getField().isMultilingual()) {
					variable = fft.getField().getVariable();
					
					fes = new FieldEditorSupport().
							setField(fft.getField()).
							setLabel(fft.getField().getName());
					
					fv = languageValuesMap == null ? null : languageValuesMap.get(variable);
					
					if (fft.getField().getType() != FieldType.layout) {
						if (fv == null) {
							fes.setInputTag(fft.getField().getInputTag());
						}
						else {
							fes.setFieldValue(fv);
							fes.setInputTag(fv.getInputTag());
						}
					}
					
					if (fft.getField().isValidateable()) {
						fes.setValidator(this.validationService.get(fft.getField().getValidatorClass()));
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
		i.setUser(u);
		checkAccess(i, throwable);
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
		tis.setAll(this.cmsService.getTagService().getTagValues4Site(siteId));		
		
		// Recently added tags are stored in the session
		Object recent = req.getSession().getAttribute(RECENT_TAGS_ATTR);
		if (recent == null) {
			recent = new ArrayList<Tag>();
			req.getSession().setAttribute(RECENT_TAGS_ATTR, recent);
		}
		
		tis.setRecent((List<String>) recent);
		return tis;
	}
}

