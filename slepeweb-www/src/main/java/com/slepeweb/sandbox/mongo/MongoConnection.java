package com.slepeweb.sandbox.mongo;

import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MongoConnection {
	private static Logger LOG = Logger.getLogger(MongoConnection.class);
	private MongoClient client;
	private DB db;
	private String dbName;
	
	public MongoConnection() {
	}
	
	public void init() {
		try {
			this.client = new MongoClient();
		}
		catch (UnknownHostException e) {
			LOG.error("Failed to connect to MongoDB", e);
		}
		
		this.db = this.client.getDB(getDbName());
	}

	public MongoClient getClient() {
		return client;
	}

	public DB getDb() {
		return db;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
}
