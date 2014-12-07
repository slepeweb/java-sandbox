package com.slepeweb.site.sws.orm;

import java.util.List;

public interface UserDao {
	void addUser(User u);
	void updateUser(User u);
	List<User> getAllUsers(boolean onlyDemoUsers);
	void deleteUser(Integer id);
	void deleteUser(User u);
	String[] getAvailableRoles();
	Role getRole(Integer id);
	Role getRole(String name);
	User getUser(String alias);
	User getUser(Integer id);
}
