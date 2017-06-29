package com.slepeweb.cms.except;

public class DuplicateItemException extends ResourceException {

	private static final long serialVersionUID = 1L;

	public DuplicateItemException(String message) {
		super(message);
	}
}
