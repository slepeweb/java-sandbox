package com.slepeweb.sandbox.spizza.bean;

import java.util.Arrays;
import java.util.List;

public enum PaymentType {
	CASH, CHECK, CREDIT_CARD;
	
	public static List<PaymentType> asList() {
		PaymentType[] all = PaymentType.values();
		return Arrays.asList(all);
	}

	@Override
	public String toString() {
		return name().replace('_', ' ');
	}

}
