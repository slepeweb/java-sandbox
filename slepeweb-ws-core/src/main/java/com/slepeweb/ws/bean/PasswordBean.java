package com.slepeweb.ws.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="password")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "password", namespace = "http://server.ws.slepeweb.com/", propOrder = {"seed", "password", "error"})
public class PasswordBean {
	private String seed, password, error;

	public String getSeed() {
		return seed;
	}

	public void setSeed(String seed) {
		this.seed = seed;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
