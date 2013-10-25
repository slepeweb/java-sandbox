package com.slepeweb.sandbox.acm.mvc.service;

import java.util.Map;

import com.mediasurface.client.IItem;
import com.slepeweb.sandbox.acm.navcache.CachedItem;
import com.slepeweb.sandbox.acm.navcache.CachedItemInline;

public interface CachedItemService {
	CachedItem getCachedItem( IItem item );
	CachedItem getCachedItem( IItem iItem, boolean getThumbnail );
	CachedItem getCachedItemClone( IItem item );
	CachedItem getCachedItemClone( IItem item, boolean getThumbnail );
	void setThumbRefreshCycle( long cycle );
	Map<String, CachedItemInline> refreshInlines( CachedItem navLink, IItem navItem ) throws Exception;
	CachedItemInline refreshThumbnail( CachedItem navLink, IItem navItem ) throws Exception;
}
