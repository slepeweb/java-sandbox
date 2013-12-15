package com.slepeweb.sandbox.spizza.bean;

import java.util.Arrays;
import java.util.List;

public enum Topping {
	ANCHOVIES, CHILLIES, SAUSSAGE;
	
	public static List<Topping> asList() {
		Topping[] all = Topping.values();
		return Arrays.asList(all);
	}

	@Override
	public String toString() {
		return name().replace('_', ' ');
	}

}
