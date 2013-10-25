package com.slepeweb.sandbox.acm.navcache;

import java.util.List;
import java.util.Properties;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.bootstrap.BootstrapCacheLoader;
import net.sf.ehcache.bootstrap.BootstrapCacheLoaderFactory;

import org.apache.log4j.Logger;

import com.slepeweb.sandbox.acm.mvc.service.CachedItemServiceImpl;

@SuppressWarnings("rawtypes")
public class AcmBootstrapCacheLoaderFactory extends BootstrapCacheLoaderFactory implements BootstrapCacheLoader {
	private static Logger LOG = Logger.getLogger(CachedItemServiceImpl.class);

	@Override
	public BootstrapCacheLoader createBootstrapCacheLoader(Properties properties) {
		return new AcmBootstrapCacheLoaderFactory();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	//@Override
	public boolean isAsynchronous() {
		return false;
	}

	//@Override
	public void load(Ehcache myCache) throws CacheException {
		LOG.info("loading navigation cache from disk....");
		List keys = myCache.getKeys();
		for (int i = 0; i < keys.size(); i++) {
			myCache.get((keys.get(i).toString()));
		}
		LOG.info("navigation cache loading complete :" + keys.size());
	}

}
