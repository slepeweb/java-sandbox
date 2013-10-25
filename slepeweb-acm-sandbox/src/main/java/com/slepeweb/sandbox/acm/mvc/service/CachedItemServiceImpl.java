package com.slepeweb.sandbox.acm.mvc.service;

import java.util.HashMap;
import java.util.Map;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mediasurface.client.IItem;
import com.mediasurface.client.ILink;
import com.mediasurface.datatypes.ItemKey;
import com.slepeweb.sandbox.acm.constants.FieldName;
import com.slepeweb.sandbox.acm.constants.ItemTypeName;
import com.slepeweb.sandbox.acm.navcache.CachedItem;
import com.slepeweb.sandbox.acm.navcache.CachedItemInline;

@Service("cachedItemService")
public class CachedItemServiceImpl implements CachedItemService {

	private static Logger LOG = Logger.getLogger( CachedItemServiceImpl.class );
	
	@Autowired
	private CacheManager acmCache;
	
	private long thumbnailRefreshCycle = 600000;

	private Boolean isLiveServer;

	public void setIsLiveServer( Boolean islive ) {
		this.isLiveServer = islive;
	}

	public Boolean getIsLiveServer() {
		return this.isLiveServer;
	}

	@SuppressWarnings("unused")
	private long getLongFromMinutes( int mins ) {
		long value = 0L;
		value = mins * 60 * 1000;
		return value;

	}

	public CachedItem getCachedItemClone( IItem item ) {
		return getCachedItemClone( item, false );
	}

	public CachedItem getCachedItemClone( IItem item, boolean getThumbnail ) {
		CachedItem cachedItem = getCachedItem( item, getThumbnail );

		if ( cachedItem != null ) {
			return ( CachedItem ) cachedItem.clone();
		}

		return cachedItem;
	}

	public CachedItem getCachedItem( IItem item ) {
		return getCachedItem( item, true );
	}

	public CachedItem getCachedItem( IItem iItem, boolean getThumbnail ) {
		ItemKey itemKey = null;
		CachedItem cachedItem = null;
		boolean wasCacheHit = false;
		boolean cacheNeedsRefreshing = false;

		try {
			itemKey = iItem.getKey();
		}
		catch ( Exception e ) {
			LOG.error( "Failed to retrieve item data", e );
		}

		StopWatch timer = new StopWatch();
		timer.start();

		if ( itemKey != null ) {
			cachedItem = get( itemKey );

			if ( cachedItem == null ) {
				cachedItem = new CachedItem( iItem );
				cacheNeedsRefreshing = true;
			}
			else {
				wasCacheHit = true;
			}

			if ( wasCacheHit ) {
				/*
				 * The cache has provided a navlink, but is it really fresh?
				 * 
				 * Most properties of a CachedItem will only change if an item is updated. In this case, a revision match will
				 * be adequate to check freshness.
				 */
				if ( ! cachedItem.isRevisionMatch( iItem ) ) {
					LOG.debug( String.format( "[%s] Item updated", cachedItem.getKey() ));
					cachedItem = new CachedItem( iItem );
					cacheNeedsRefreshing = true;
				}
				/*
				 * However, an item's path/url could have been changed by modifying the simplename of an ancestor item. If this
				 * happens, the navlink should be refreshed. So, here we do a path check. But first note:
				 * 
				 * For certain types, we substitute the item url for a field value. Examples of this are Quick Link and CTA
				 * Link. In these cases, we don't care if their paths have changed, because it is their URL field values that
				 * are significant, and that case would have been trapped by the revision-match check above.
				 */
				else if (
					// !navLink.getItemTypeName().equals(ItemTypeName.CTA_LINK)
					// && !navLink.getItemTypeName().equals(ItemTypeName.QUICK_LINK) &&
					! cachedItem.isPathMatch( iItem ) && cachedItem.getItemTypeName().indexOf( "LinkReference" ) == - 1 ) {

					// Yes, the path has changed.
					// Don't need to re-create the navLink entirely - just update its path and url
					cachedItem.setPath( iItem );

					// Persist change - path has changed
					LOG.debug( String.format( "[%s] Item path updated", cachedItem.getKey() ));
					cacheNeedsRefreshing = true;
				}
			}

			// Refresh thumbnail graphic if necessary
			if ( getThumbnail ) {
				long now = System.currentTimeMillis();
				long timeSinceLastRefresh = now - cachedItem.getLastRefreshedThumbnail();

				if ( timeSinceLastRefresh > getThumbRefreshCycle() ) {
					try {
						Map<String, CachedItemInline> thumbnails = refreshInlines( cachedItem, iItem );
						cachedItem.setInlines( thumbnails );
						cachedItem.setThumbnail( thumbnails.get( "thumbnail" ) );

						if ( thumbnails != null ) {
							cachedItem.setLastRefreshedThumbnail();
						}

						// Persist changes - a) time thumbnail last refreshed, b) new thumbnail url
						cacheNeedsRefreshing = true;
						LOG.debug( String.format( "[%s] Refreshed thumbnail [every %d mins]", cachedItem.getKey(), getThumbRefreshCycle() / 60000) );
					}
					catch ( Exception e ) {
						LOG.error( String.format( "[%s] Failed to refresh thumbnail", cachedItem.getKey() ), e );
					}
				}
				else {
					//LOG.debug( cachedItem.getPath() + ": Next thumbnail refresh in " + ( ( getThumbRefreshCycle() - timeSinceLastRefresh ) / 1000 ) + " secs" );
				}
			}

			StringBuilder sb = new StringBuilder( "[%s] ");

			if ( wasCacheHit ) {
				if ( ! cacheNeedsRefreshing ) {
					sb.append( "Fresh" );
				}
				else {
					putInNavCache( cachedItem );
					sb.append( "Refreshed" );
				}
			}
			else {
				putInNavCache( cachedItem );
				sb.append( "Missing/Stale" );
			}

			sb.append( " CachedItem retrieved [%d millis]" );
			LOG.debug( String.format( sb.toString(), cachedItem.getKey(), timer.getTime() ) );
		}

		return cachedItem;
	}

	public void setThumbRefreshCycle( long cycle ) {
		this.thumbnailRefreshCycle = cycle;
	}

	public long getThumbRefreshCycle() {
		return this.thumbnailRefreshCycle;
	}

	@SuppressWarnings("unused")
	private void replaceFromNavCache( CachedItem navLink ) {
		String cacheKey = navLink.getItemId();

		try {
			getItemCache().remove( cacheKey );
			LOG.debug( String.format( "[%s] Removed from cache", cacheKey ));
		}
		catch ( Exception e ) {
			LOG.error( String.format( "[%s] Failed to remove entry cache ", cacheKey), e );
		}

		putInNavCache( navLink );
	}

	private void putInNavCache( CachedItem navLink ) {
		String cacheKey = navLink.getItemId();

		try {
			getItemCache().put( new Element( cacheKey, navLink ) );
			LOG.debug( String.format( "[%s] Cache updated", cacheKey) );
		}
		catch ( Exception e ) {
			LOG.error( String.format( "[%s] Failed to update cache", cacheKey), e );
		}
	}

	private CachedItem get( ItemKey k ) {
		CachedItem cachedItem = null;
		String key = k.getKey();

		try {
			Element elem = getItemCache().get( key );
			cachedItem = elem != null ? ( CachedItem ) elem.getObjectValue() : null;
		}
		catch ( Exception e ) {
			if ( cachedItem == null ) {
				LOG.debug( String.format( "[%s] No entry in cache", key ));
			}
			else {
				LOG.debug( String.format( "[%s] Returning possibly stale entry in cache", key ));
			}
		}

		return cachedItem;
	}

	private Ehcache getItemCache() {
		return this.acmCache.getEhcache( "itemCache" );
	}

	public Map<String, CachedItemInline> refreshInlines( CachedItem cachedItem, IItem iitem ) throws Exception {
		CachedItem inlineNavLink;

		Map<String, CachedItemInline> inlines = new HashMap<String, CachedItemInline>();
		// Get inlines for given base IItem - ctx is the same that was used to
		// provide the IItem
		ILink[] inlineLinks = iitem.getInlineItems( null, null, false, null, null );

		if ( inlineLinks.length > 0 ) {
			// First, iterate through all inlines to identify one which has
			// purpose=Thumbnail
			String inlineType, purpose;
			IItem inlineItem = null;

			// Look for a GALLERY image, and assume that all size variants will
			// exist
			for ( ILink link : inlineLinks ) {
				inlineItem = link.getChildItem();
				inlineNavLink = getCachedItem( inlineItem, false );
				inlineType = inlineNavLink.getItemTypeName();

				if ( inlineType.startsWith( ItemTypeName.IMAGE_PREFIX ) ) {
					purpose = ( String ) inlineNavLink.getProperty( FieldName.PURPOSE );

					if ( purpose != null ) {
						inlines.put( purpose, new CachedItemInline( inlineNavLink, cachedItem.getMarket(), cachedItem.getLanguage() ) );
					}
				}
			}
		}
		return inlines;
	}

	public CachedItemInline refreshThumbnail( CachedItem cachedItem, IItem iitem ) throws Exception {
		CachedItem inlineCachedItem;

		// Get inlines for given base IItem - ctx is the same that was used to
		// provide the IItem
		ILink[] inlineLinks = iitem.getInlineItems( null, null, false, null, null );

		if ( inlineLinks.length > 0 ) {
			// First, iterate through all inlines to identify one which has
			// purpose=Thumbnail
			String inlineType, purpose;
			IItem inlineItem = null;

			// Look for a GALLERY image, and assume that all size variants will
			// exist
			for ( ILink link : inlineLinks ) {
				inlineItem = link.getChildItem();
				inlineCachedItem = getCachedItem( inlineItem, false );
				inlineType = inlineCachedItem.getItemTypeName();

				if ( inlineType.startsWith( ItemTypeName.IMAGE_PREFIX ) ) {
					purpose = ( String ) inlineCachedItem.getProperty( FieldName.PURPOSE );

					if ( purpose != null && CachedItemInline.ImagePurpose.valueOf( purpose.toUpperCase() ) == CachedItemInline.ImagePurpose.GALLERY ) {

						CachedItemInline thumbnail = new CachedItemInline( inlineCachedItem, cachedItem.getMarket(), cachedItem.getLanguage() );
						return thumbnail;
					}
				}
			}
		}
		return null;
	}
}