package com.slepeweb.site.service;

import java.util.List;

import com.slepeweb.site.bean.DatedLinkTarget;

public interface RomeService {
	List<DatedLinkTarget> getFeed(String url);
}
