package com.slepeweb.site.service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.User;

public interface PdfService {
	String build(Item root, User u);
}
