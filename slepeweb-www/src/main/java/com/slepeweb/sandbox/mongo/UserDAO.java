package com.slepeweb.sandbox.mongo;

import com.slepeweb.sandbox.www.model.User;

public interface UserDAO {
	User findUser(Integer id);
	User findUser(String alias);
}
