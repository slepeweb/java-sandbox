package com.slepeweb.site.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slepeweb.cms.bean.Dateish;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.SolrDocument4Cms;
import com.slepeweb.cms.bean.StringWrapper;
import com.slepeweb.commerce.bean.Product;

public class TldFunction {

	public static String formatUKDate(Date d, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, new Locale("en-GB"));
		return sdf.format(d).replaceFirst("AM$", "am").replaceFirst("PM$", "pm");
	}
	
	public static Item getMatchingHifiImage(Product product, Item testImg) {
		return product.getMatchingHifiImage(testImg);
	}
	
	public static String resolveMarkupFieldValue(Item i, String variable, String dflt) {
		return i.getFieldValueResolved(variable, new StringWrapper(dflt));
	}
	
	public static Item resolveOrderItemThumbnail(Product p, String qualifier) {
		return p.getOrderItemThumbnail(qualifier);
	}
	
	public static void main(String[] args) {
		System.out.println(formatUKDate(new Date(), "MMMM d, HH:mm z"));
	}
	
	public static Dateish toDateish(String s) {
		return new Dateish(s);
	}
	
	public static List<SolrDocument4Cms> json2SolrCmsDoc(String json) {
		try {
			TypeReference<List<SolrDocument4Cms>> type = new TypeReference<List<SolrDocument4Cms>>() {};
			List<SolrDocument4Cms> list = new ObjectMapper().readValue(json, type);
			return list;
		} catch (Exception e) {
			// Handle the problem
		}
		return null;
	}
	
	public static String compress(String s) {
		return com.slepeweb.common.util.StringUtil.compress(s);
	}
}
