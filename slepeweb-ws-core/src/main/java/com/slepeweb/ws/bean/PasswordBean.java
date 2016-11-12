package com.slepeweb.ws.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

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
	
	public String getChunked() {
		int strlen = -1;
		if (StringUtils.isNotBlank(this.password) && (strlen = this.password.length()) > 4) {
			int a=0, b;
			StringBuilder sb = new StringBuilder();
			
			do {
				b = a + 4;
				if (b > strlen) { 
					b = strlen;
				}
				
				sb.append(this.password.substring(a, b));
				
				if (b < strlen) {
					sb.append("  ");
				}
				
				a = b;
			}
			while (a < strlen);
			
			return sb.toString();
		}
		return getPassword();
	}
}
