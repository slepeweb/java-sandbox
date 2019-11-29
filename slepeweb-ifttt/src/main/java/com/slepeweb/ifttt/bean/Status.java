package com.slepeweb.ifttt.bean;

public class Status {
	private boolean ok;
	
	public Status(boolean b) {
		this.ok = b;
	}

	public boolean isOk() {
		return ok;
	}

	public Status setOk(boolean ok) {
		this.ok = ok;
		return this;
	}

}
