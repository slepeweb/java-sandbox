package com.slepeweb.site.util;

public class QuickTest {
	
	public static void main(String[] args) {
	}
	
	
	protected static void trace(String s) {
		System.out.println(s);
	}
	
	protected static void trace(String s, Exception e) {
		System.out.println(s + ": " + e.getMessage());
	}
}
