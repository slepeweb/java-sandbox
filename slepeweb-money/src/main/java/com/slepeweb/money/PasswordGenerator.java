package com.slepeweb.money;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

public class PasswordGenerator {

	public static void main(String[] args) {
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("passwordGeneratorContext.xml");		
		StandardPasswordEncoder encoder = (StandardPasswordEncoder) context.getBean("passwordEncoder");		
		String readable = "giga8yte";
		System.out.println(String.format("User [%s] has password [%s]", readable, encoder.encode(readable)));

	}
}
