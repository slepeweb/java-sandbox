package com.slepeweb.cms.except;

public class MissingDataException extends ResourceException {

	private static final long serialVersionUID = 1L;

	public MissingDataException(String message) {
		super(message);
	}
}
