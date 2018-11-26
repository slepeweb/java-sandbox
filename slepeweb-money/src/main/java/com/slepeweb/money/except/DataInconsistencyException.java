package com.slepeweb.money.except;

public class DataInconsistencyException extends Exception {

	private static final long serialVersionUID = 1L;

	public DataInconsistencyException(String message) {
		super(message);
	}
}
