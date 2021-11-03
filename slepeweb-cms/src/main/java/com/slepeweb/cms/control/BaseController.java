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
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.constant.ItemTypeName;
import com.slepeweb.cms.service.CmsService;

@Controller
public class BaseController {
	
	@Autowired protected CmsService cmsService;
	
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
		checkWriteAccess(i, throwable);
		return i;
	}
	
	protected Item getEditableVersion(Site s, String path, User u) throws RuntimeException {
		return getEditableVersion(s, path, u, false);
	}
	
	protected Item getEditableVersion(Site s, String path, User u, boolean throwable) throws RuntimeException {
		Item i = this.cmsService.getItemService().getEditableVersion(s.getId(), path);
		i.setUser(u);
		checkWriteAccess(i, throwable);
		return i;
	}
	
	protected boolean checkWriteAccess(Item i, boolean throwable) throws RuntimeException {
		i.setWriteAccess(this.cmsService.getSiteAccessService().hasWriteAccess(i));	
		
		if (throwable && ! i.isWriteAccessGranted()) {
			throw new RuntimeException("No write access");
		}
		
		return i.isWriteAccessGranted();
	}
	
	protected boolean checkReadAccess(Item i, boolean throwable) throws RuntimeException {
		i.setReadAccess(this.cmsService.getSiteAccessService().hasReadAccess(i, null));	
		
		if (throwable && ! i.isReadAccessGranted()) {
			throw new RuntimeException("No read access");
		}
		
		return i.isReadAccessGranted();
	}
}

