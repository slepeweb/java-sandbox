package com.slepeweb.cms.cache;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import com.slepeweb.cms.bean.CmsBean;
import com.slepeweb.cms.bean.Field;
import com.slepeweb.cms.bean.FieldForType;
import com.slepeweb.cms.bean.Host;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.LinkName;
import com.slepeweb.cms.bean.LinkType;
import com.slepeweb.cms.bean.Media;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.SiteConfig;
import com.slepeweb.cms.bean.Tag;
import com.slepeweb.cms.bean.Template;
import com.slepeweb.cms.utils.LogUtil;

@Component
public class CacheEvictor {
	private static Logger LOG = Logger.getLogger(CacheEvictor.class);
	
	@Autowired private CacheManager cacheManager;
	private net.sf.ehcache.Cache ehCache;
	
	public void init() {
		Cache springCache = this.cacheManager.getCache("serviceCache");
		if (springCache != null) {
			if (springCache.getNativeCache() instanceof net.sf.ehcache.Cache) {
				this.ehCache = (net.sf.ehcache.Cache) springCache.getNativeCache();
			}
		}
		if (this.ehCache == null) {
			throw new RuntimeException("Failed to setup CacheEvictor");
		}
	}
	
	private void broadcast(CmsBean bean) {
		@SuppressWarnings("unused")
		String url;
		for (String hostname : new String[] {}) {
			url = String.format("http://%s/cache/evict/%s/%d", hostname, bean.getClass().getSimpleName(), bean.getId());
			// TODO: send out an http request to this url
			// TODO: setup a cache controller to respond to these requests
		}
	}
	
	public void evict(Field f) {
		evict(
				compose("getField", f.getVariable()), 
				compose("getField", f.getId()));
		
		broadcast(f);
	}
	

	public void evict(FieldForType fft) {
		evict(
				compose("getFieldForType", fft.getField().getId(), fft.getTypeId()),
				compose("getFieldsForType", fft.getTypeId()));
		
		broadcast(fft);
	}

	public void evict(ItemType it) {
		evict(
				compose("getItemType", it.getName()), 
				compose("getItemType", it.getId()), 
				compose("getAvailableItemTypes"));
		
		broadcast(it);
	}

	public void evict(LinkName ln) {
		evict(
				compose("getLinkName", ln.getSiteId(), ln.getLinkTypeId(), ln.getName()),
				compose("getLinkNames", ln.getSiteId(), ln.getLinkTypeId()));		 
		
		broadcast(ln);
	}
	
	public void evict(LinkType lt) {
		evict(
				compose("getLinkType", lt.getName()));		 
		
		broadcast(lt);
	}

	public void evict(SiteConfig sc) {
		evict(
				compose("getSiteConfig", sc.getSiteId(), sc.getName()),
				compose("getSiteConfigs", sc.getSiteId()));
		
		broadcast(sc);
	}

	public void evict(Host h) {
		evict(compose("getHost", h.getName()));		
		broadcast(h);
	}
	
	public void evict(Site s) {
		evict(
				compose("getSite", s.getName()),
				compose("getSite", s.getId()));
		
		broadcast(s);
	}
	
	public void evict(Template t) {
		evict(
				compose("getTemplate", t.getId()), 
				compose("getTemplate", t.getSiteId(), t.getName()), 
				compose("getAvailableTemplates", t.getSiteId()));
		
		broadcast(t);
	}

	public void evict(Media t) {
		// TODO: Implement
	}

	public void evict(Tag t) {
		evict(
				compose("getTaggedItem", t.getItem().getSite().getId(), t.getValue()));
		
		broadcast(t);
	}

	private String compose(Object ... parts) {
		if (parts.length == 1) {
			return parts[0].toString();
		}
		else if (parts.length > 1) {
			StringBuilder sb = new StringBuilder();
			for (Object o : parts) {
				if (sb.length() > 0) {
					sb.append("-");
				}
				sb.append(o.toString());
			}
			return sb.toString();
		}
		return  null;
	}
	
	private boolean evict(String ... keys) {
		boolean result = true;
		
		for (String key : keys) {
			if (key != null) {
				result = result && ehCache.remove(key);
			}
		}
		
		if (! result) {
			LOG.debug(LogUtil.compose("Failed to evict all items from cache", ((Object[]) keys)));
		}
		
		return result;
	}
	
	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}
}
