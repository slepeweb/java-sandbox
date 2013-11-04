package com.slepeweb.sandbox.mongo;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.slepeweb.sandbox.www.model.User;
import com.slepeweb.sandbox.www.model.User.Role;

@Service("userDAOservice")
public class UserDAOImpl implements UserDAO {
	private static Logger LOG = Logger.getLogger(UserDAOImpl.class);
	
	@Autowired
	private MongoConnection mongoConnection;
	
	public User findUser(Integer id) {
		DB db = this.mongoConnection.getDb();
		DBObject obj = db.getCollection("user").findOne();
		String alias = (String) obj.get("alias");
		User user = new User().setAlias(alias);
		LOG.info(String.format("Found user [%s]", alias));
		return user;
	}
	
	public User findUser(String alias) {
		DB db = this.mongoConnection.getDb();
		BasicDBObject q = new BasicDBObject("alias", alias);
		DBCursor c = db.getCollection("user").find(q);
		
		if (c.hasNext()) {
			User user = toUser(c.next());
			LOG.info(String.format("Found user [%s]", user.getName()));
			return user;
		}
		
		return null;
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
				append("password", encryptedPassword);//.
				//append("roles", new String[] {"agent", "admin"});
		
		coll.insert(obj);
	}
	
	private User toUser(DBObject obj) {
		if (obj != null) {
			User u = new User().
				setAlias(getValue(obj, "alias")).
				setName(getValue(obj, "name")).
				setEncryptedPassword(getValue(obj, "password"));
			
			List<String> roleStr = getValuesAsList(obj, "roles");
			List<Role> roles = new ArrayList<Role>();
			for (String s : roleStr) {
				roles.add(Role.valueOf(s.toUpperCase()));
			}
			
			u.setRoles(roles);			
			return u;
		}
		return null;
	}
	
	private String getValue(DBObject obj, String key) {
		return getValue(obj, key, "");
	}
	
	private String getValue(DBObject obj, String key, String dflt) {
		if (obj != null) {
			Object o = obj.get(key);
			return o instanceof String ? (String) o : dflt;
		}
		return dflt;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<String> getValuesAsList(DBObject obj, String key) {
		if (obj != null) {
			Object o = obj.get(key);
			return o instanceof List ? (List) o : new ArrayList<String>();
		}
		return new ArrayList<String>();
	}

}
