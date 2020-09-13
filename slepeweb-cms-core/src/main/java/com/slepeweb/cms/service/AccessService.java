package com.slepeweb.cms.service;

import java.util.List;
import com.slepeweb.cms.bean.AccessRule;

public interface AccessService {
	void delete(Long id);
	AccessRule get(Long id);
	AccessRule get(String siteName, String ruleName);
	List<AccessRule> getReadable(String siteName);
	List<AccessRule> getWriteable(String siteName);
	AccessRule save(AccessRule ar);
}
