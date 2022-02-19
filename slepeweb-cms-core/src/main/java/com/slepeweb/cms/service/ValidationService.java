package com.slepeweb.cms.service;

import com.slepeweb.cms.bean.guidance.IValidator;

public interface ValidationService {	
	IValidator get(String classPath);
}
