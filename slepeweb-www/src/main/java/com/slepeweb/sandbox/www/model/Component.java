package com.slepeweb.sandbox.www.model;

import java.io.Serializable;

public class Component implements Serializable {
	private static final long serialVersionUID = 1L;
	private String view;

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}
}
