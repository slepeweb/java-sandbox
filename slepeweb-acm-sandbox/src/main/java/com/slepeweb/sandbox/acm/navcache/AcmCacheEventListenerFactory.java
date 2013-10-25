package com.slepeweb.sandbox.acm.navcache;

import java.util.Properties;

import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.CacheEventListenerFactory;

public class AcmCacheEventListenerFactory extends CacheEventListenerFactory {

	@Override
	public CacheEventListener createCacheEventListener( Properties p ) {
		return new AcmCacheEventListenerImpl();
	}

}
