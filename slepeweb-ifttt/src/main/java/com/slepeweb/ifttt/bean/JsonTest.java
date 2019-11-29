package com.slepeweb.ifttt.bean;

public class JsonTest {

	public static void main(String[] args) {
		JsonObj top = JsonObj.createStruc().
				put("data", JsonObj.createStruc().
					put("samples", JsonObj.createStruc().
						put("triggers", JsonObj.createStruc().
							put("hello_world", JsonObj.createStruc().
								put("message", JsonObj.create("Hello Wolrd"))))).
					put("actions", JsonObj.createStruc().
							put("get_password", JsonObj.createStruc().
								put("party", JsonObj.create("Halifax")))));
		
		pr(top.stringify());
		
		String name = "samples.triggers";
		JsonObj o = top.find(name);
		if (o != null) {
			pr(String.format("Found [%s]: %s", name, o));
		}
		else {
			pr("Failed to find property");
		}
	}
	
	public static void pr(String s) {
		System.out.println(s);
	}
}
