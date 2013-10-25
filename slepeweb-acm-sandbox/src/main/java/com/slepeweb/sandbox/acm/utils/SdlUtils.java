package com.slepeweb.sandbox.acm.utils;

import org.apache.commons.lang3.StringUtils;

public class SdlUtils {

	/**
	 * Takes a full url, and returns the path part.
	 * 
	 * @param url
	 * @return
	 */
	public static String getPathPart(String url) {
		if (url != null) {
			int cursor = url.indexOf("//");
			if (cursor > -1) {
				String intermediate = url.substring(cursor + 2);
				cursor = intermediate.indexOf("/");
				if (cursor > -1) {
					return intermediate.substring(cursor);
				}
				return intermediate;
			}
		}

		return url;
	}

	public static String getPathPartIfSameDomain(String baseUrl, String comparedUrl) {
		if (StringUtils.isEmpty(comparedUrl) || comparedUrl.equals("#")) {
			return comparedUrl;
		}

		if (!comparedUrl.startsWith("http")) {
			return comparedUrl;
		}

		String relativeUrl = comparedUrl;
		String baseDomain = StringUtils.substringBetween(baseUrl, "http://", "/");
		String comparedDomain = StringUtils.substringBetween(comparedUrl, "http://", "/");

		if (comparedUrl.contains("/globalcontent/")) {
			relativeUrl = StringUtils.substringAfter(comparedUrl, comparedDomain);
		} else if (baseDomain.equals(comparedDomain)) {
			relativeUrl = StringUtils.substringAfter(comparedUrl, comparedDomain);
		}

		return relativeUrl;
	}
}
