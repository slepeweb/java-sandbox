package com.slepeweb.cms.service;

import com.slepeweb.cms.bean.Role;


public interface RoleService {
	void delete(Long id);
	Role get(String name);
	Role get(Long id);
	Role save(Role r);
}
