package com.slepeweb.ifttt.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

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
		
		JsonObj data = JsonObj.createList();
		top = JsonObj.createStruc().put("data", data);
		Date created = new Date();
		String id = String.valueOf(created.getTime());
		SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

		data.add(JsonObj.createStruc().
				put("party", JsonObj.create("Halifax")).
				put("password", JsonObj.create("dummyPassword")).
				put("created_at", JsonObj.create(SDF.format(created))).
				put("meta", JsonObj.createStruc().
					put("id", JsonObj.create(created.getTime())).
					put("timestamp", JsonObj.create(id))));	
		
		pr(top.stringify());
	}
	
	public static void pr(String s) {
		System.out.println(s);
	}
}
