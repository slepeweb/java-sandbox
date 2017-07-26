package com.slepeweb.site.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.commerce.bean.Product;

public class TldFunction {

	public static String formatUKDate(Date d, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, new Locale("en-GB"));
		return sdf.format(d).replaceFirst("AM$", "am").replaceFirst("PM$", "pm");
	}
	
	public static Item getMatchingHifiImage(Product product, Item testImg) {
		return product.getMatchingHifiImage(testImg);
	}
	
	public static void main(String[] args) {
		System.out.println(formatUKDate(new Date(), "MMMM d, HH:mm z"));
	}
}
