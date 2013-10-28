package com.slepeweb.sandbox.www.service;

import java.util.List;

import com.slepeweb.sandbox.www.model.Link;

public interface RomeService {
	List<Link> getFeed(String url);
}
