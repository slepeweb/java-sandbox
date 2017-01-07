package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.Host;


public interface HostService {
	void deleteHost(Host h);
	Host getHost(String name);
	Host getHost(Long id);
	List<Host> getAllHosts(Long siteId);
	Host save(Host s);
}
