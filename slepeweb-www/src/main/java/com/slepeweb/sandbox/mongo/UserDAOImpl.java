package com.slepeweb.sandbox.mongo;

import org.apache.log4j.Logger;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.slepeweb.sandbox.www.model.User;

@Service("userDAOservice")
public class UserDAOImpl implements UserDAO {
	private static Logger LOG = Logger.getLogger(UserDAOImpl.class);
	
	@Autowired
	private MongoConnection mongoConnection;
	
	public User findUser(Integer id) {
		DB db = this.mongoConnection.getDb();
		DBObject obj = db.getCollection("user").findOne();
		String name = (String) obj.get("name");
		User user = new User().setAlias(name);
		LOG.info(String.format("Found user [%s]", name));
		return user;
	}
	
	public static void main(String[] args) {
		String userName = args[0];
		String userPassword = args[1];
		String fullName = args[2];
		BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
		String encryptedPassword = passwordEncryptor.encryptPassword(userPassword);
		
		MongoConnection conn = new MongoConnection();
		conn.setDbName("mydb");
		conn.init();
		DB db = conn.getDb();
		DBCollection coll = db.getCollection("user");
		BasicDBObject obj = new BasicDBObject().
				append("name", fullName).
				append("alias", userName).
				append("password", encryptedPassword).
				append("roles", new String[] {"agent", "admin"});
		
		coll.insert(obj);
	}
}
