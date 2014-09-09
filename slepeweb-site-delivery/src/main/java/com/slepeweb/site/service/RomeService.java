package com.slepeweb.site.service;

import java.util.List;

import com.slepeweb.site.model.LinkTarget;

public interface RomeService {
	List<LinkTarget> getFeed(String url);
}
