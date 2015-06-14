package com.slepeweb.site.util;

import org.springframework.security.crypto.password.StandardPasswordEncoder;

public class PasswordGenerator {

	public static void main(String[] args) {
		StandardPasswordEncoder encoder = new StandardPasswordEncoder();
		System.out.println(String.format("%s: [%s]", args[0], encoder.encode(args[0])));
	}

}
