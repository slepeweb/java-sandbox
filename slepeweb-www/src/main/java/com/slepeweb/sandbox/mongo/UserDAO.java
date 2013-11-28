package com.slepeweb.sandbox.mongo;


public interface UserDAO {
	MongoUser findUser(Integer id);
	MongoUser findUser(String alias);
}
