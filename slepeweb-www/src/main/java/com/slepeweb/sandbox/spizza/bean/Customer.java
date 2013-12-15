package com.slepeweb.sandbox.spizza.bean;

import java.io.Serializable;

import static org.apache.commons.lang3.StringUtils.isBlank;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;

public class Customer implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name, address, phoneNumber, zipCode;

	public void validateRegistrationForm(ValidationContext ctx) {
	     MessageContext mctx = ctx.getMessageContext();
	     validateMandatory("name", getName(), "Please enter your name", mctx);
	     validateMandatory("phoneNumber", getPhoneNumber(), "Please enter your phone number", mctx);
	     validateMandatory("zipCode", getZipCode(), "Please enter your postcode", mctx);
	}
	
	private void validateMandatory(String property, String value, String msg, MessageContext messages) {
		if (isBlank(value)) {
            messages.addMessage(new MessageBuilder().error().source(property).defaultText(msg).build());
		}
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
}
