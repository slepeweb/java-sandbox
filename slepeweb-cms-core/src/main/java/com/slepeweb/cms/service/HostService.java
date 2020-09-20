package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.Host;
import com.slepeweb.cms.bean.Host.HostType;


public interface HostService {
	void deleteHost(Host h);
	Host getHost(String name);
	Host getHost(Long id);
	List<Host> getAllHosts(Long siteId);
	List<Host> getHosts(Long siteId, HostType type);
	Host save(Host s);
}
