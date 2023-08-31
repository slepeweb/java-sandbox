package com.slepeweb.cms.service;

import java.util.List;
import com.slepeweb.cms.bean.AccessRule;

public interface AccessService {
	void delete(Long id);
	AccessRule get(Long id);
	AccessRule get(Long siteId, String ruleName);
	List<AccessRule> getReadable(Long siteId);
	List<AccessRule> getWriteable(Long siteId);
	AccessRule save(AccessRule ar);
}
