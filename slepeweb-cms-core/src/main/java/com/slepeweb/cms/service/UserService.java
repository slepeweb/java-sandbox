package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.User;


public interface UserService {
	void delete(Long id);
	User get(String name);
	User getBySecret(String secret);
	User getByPassword(String pwd);
	User get(Long id);
	User save(User u);
	User save(User u, Site s, boolean doRoles);
	void saveRoles(User u, Site s);
	List<String> getRoles(Long userId, Long siteId);
	User partialUpdate(User u);
}
