package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.Role;
import com.slepeweb.cms.bean.User;


public interface UserService {
	void delete(Long id);
	User get(String name);
	User getBySecret(String secret);
	User get(Long id);
	User save(User u);
	User save(User u, boolean doRoles);
	void saveRoles(User u);
	List<Role> getRoles(Long userId);
	User partialUpdate(User u);
}
