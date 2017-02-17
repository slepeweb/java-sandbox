package com.slepeweb.cms.except;

public class NotRevertableException extends Exception {

	private static final long serialVersionUID = 1L;

	public NotRevertableException(String message) {
		super(message);
	}
}
