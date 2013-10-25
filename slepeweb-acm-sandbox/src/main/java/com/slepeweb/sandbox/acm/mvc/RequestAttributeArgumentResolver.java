package com.slepeweb.sandbox.acm.mvc;

import java.lang.annotation.Annotation;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.slepeweb.sandbox.acm.mvc.annotation.RequestAttributeAnno;

/**
 * Custom WebArgumentResolver which can use the
 * RequestAttribute to inject an javax.servlet.http.HttpRequest attribute
 * as a method argument.
 * 
 * 
 * @author adam
 */
public class RequestAttributeArgumentResolver implements WebArgumentResolver {

	public Object resolveArgument(MethodParameter methodParameter,
			NativeWebRequest webRequest) throws Exception {
		
		Object paramAnn = null;
		Annotation[] paramAnns = methodParameter.getParameterAnnotations();
		Object result = UNRESOLVED;
		
		for (int j = 0; j < paramAnns.length; j++) {
			paramAnn = paramAnns[j];
			
			if(paramAnn instanceof RequestAttributeAnno) {
				RequestAttributeAnno attribute = (RequestAttributeAnno) paramAnn;
				String attributeName = attribute.value();
				result = getRequestAttribute(webRequest, attributeName);
				break;
			} 
		}
		
		return result;
	}
	
	private Object getRequestAttribute(NativeWebRequest webRequest, String attributeName) {
		return webRequest.getAttribute(attributeName, WebRequest.SCOPE_REQUEST);
	}


}
