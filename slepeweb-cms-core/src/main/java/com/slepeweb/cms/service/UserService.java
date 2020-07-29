package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.User;


public interface UserService {
	void delete(Long id);
	User get(String name);
	User get(Long id);
	User save(User u);
	List<String> getRoles(Long userId);
}
