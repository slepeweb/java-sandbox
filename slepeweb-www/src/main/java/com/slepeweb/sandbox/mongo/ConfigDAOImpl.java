package com.slepeweb.sandbox.mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Service("configDAOservice")
public class ConfigDAOImpl implements ConfigDAO {
	
	@Autowired
	private MongoConnection mongoConnection;
	
	public String findValue(String key) {
		return findValue(key, null);
	}
	
	public String findValue(String key, String dflt) {
		DB db = this.mongoConnection.getDb();
		BasicDBObject q = new BasicDBObject("key", key);
		DBCursor c = db.getCollection("config").find(q);
		
		if (c.hasNext()) {
			DBObject obj = c.next();
			Object o = obj.get("value");
			if (o instanceof String) {
				return (String) o;
			}			
		}
		
		if (dflt != null) {
			return dflt;
		}
		
		return null;
	}
}
