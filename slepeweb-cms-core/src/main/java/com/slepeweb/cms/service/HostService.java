package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.Host;
import com.slepeweb.cms.bean.Host.HostType;


public interface HostService {
	void deleteHost(Host h);
	Host getHost(Long id);
	Host getHost(String name, int port, HostType t);
	Host getHost(String name, int port);
	Host getHost(String name);
	Host getHostByPublicName(String name);
	Host getHost(Long siteId, HostType t);
	List<Host> getHosts(Long siteId);
	Host save(Host s);
}
