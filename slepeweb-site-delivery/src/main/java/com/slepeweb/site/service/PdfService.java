package com.slepeweb.site.service;

import com.slepeweb.cms.bean.Item;

public interface PdfService {
	String assemble(Item root, String sessionId);
}
