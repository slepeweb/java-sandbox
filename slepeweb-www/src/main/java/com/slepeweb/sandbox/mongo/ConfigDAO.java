package com.slepeweb.sandbox.mongo;


public interface ConfigDAO {
	String findValue(String key);
	String findValue(String key, String dflt);
}
