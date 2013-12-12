package com.slepeweb.sandbox.acm.mvc;

import java.lang.annotation.Annotation;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;

import com.slepeweb.sandbox.acm.constants.RequestAttribute;
import com.slepeweb.sandbox.acm.mvc.annotation.AcmObjectAnno;

public class AcmObjectArgumentResolver implements WebArgumentResolver {

	public Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) throws Exception {
		Object paramAnn = null;
		Annotation[] paramAnns = methodParameter.getParameterAnnotations();
		Object result = UNRESOLVED;
		
		for (int j = 0; j < paramAnns.length; j++) {
			paramAnn = paramAnns[j];
			
			if (paramAnn instanceof AcmObjectAnno) {
				// Get a ACM specific security context.
				result = getRequestAttribute(webRequest, RequestAttribute.ACM_OBJECT);
				break;
			}
		}
		
		return result;
	}
	
	private Object getRequestAttribute(NativeWebRequest webRequest, String attributeName) {
		Object result = webRequest.getAttribute(attributeName, RequestAttributes.SCOPE_REQUEST);
		return result;
	}

}
