package com.slepeweb.sandbox.acm.mvc.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mediasurface.client.IHost;
import com.mediasurface.client.IItem;
import com.mediasurface.client.ILink;
import com.mediasurface.client.IType;
import com.mediasurface.client.Mediasurface;
import com.mediasurface.datatypes.SecurityContextHandle;
import com.mediasurface.general.AuthorizationException;
import com.mediasurface.general.LinkSortOrder;
import com.mediasurface.general.ResourceException;
import com.slepeweb.sandbox.acm.navcache.CachedItem;

public class AcmObject implements Serializable {
	private static final long serialVersionUID = 1L;

	private final IItem requestItem;
	private final CachedItem cachedRequestItem;
	private final IType requestType;
	private IHost host;
	private IItem siteHomePageItem;
	private CachedItem cachedSiteHomepage;
	transient private final SecurityContextHandle securityContextHandle;
	transient private final Mediasurface mediasurface;
	private List<ILink> relatedLinks;
	private Boolean cacheableRequest;

	public AcmObject( IItem requestItem, CachedItem cachedRequestItem, IType requestType, SecurityContextHandle securityContextHandle,
			Mediasurface mediasurface ) {
		this.requestItem = requestItem;
		this.requestType = requestType;
		this.securityContextHandle = securityContextHandle;
		this.mediasurface = mediasurface;
		this.cachedRequestItem = cachedRequestItem;

	}

	public Boolean isCacheableRequest() {
		return cacheableRequest;
	}

	public void setCacheableRequest( Boolean cacheableRequest ) {
		this.cacheableRequest = cacheableRequest;
	}

	public IItem getRequestItem() {
		return requestItem;
	}

	public CachedItem getCachedRequestItem() {
		return cachedRequestItem;
	}

	public IType getRequestType() {
		return requestType;
	}

	public IHost getHost() throws AuthorizationException, ResourceException {
		if ( host == null ) {
			host = requestItem.getHost();
		}
		return host;
	}

	public SecurityContextHandle getSecurityContextHandle() {
		return securityContextHandle;
	}

	public Mediasurface getMediasurface() {
		return mediasurface;
	}

	public ArrayList<ILink> getRelatedLinks( final String typeName, final String viewName ) {

		ArrayList<ILink> linkArrLst = new ArrayList<ILink>();

		for ( ILink link : getRelatedLinks() ) {
			if ( this.linkHasView( link, viewName ) && this.linkHasType( link, typeName ) ) {
				linkArrLst.add( link );
			}
		}

		return linkArrLst;
	}

	public List<ILink> getRelatedLinksWithView( final String viewName ) {

		List<ILink> linkArrLst = new ArrayList<ILink>();

		for ( ILink link : getRelatedLinks() ) {
			if ( linkHasView(link, viewName ) ) {
				linkArrLst.add( link );
			}
		}

		return linkArrLst;
	}

	public ArrayList<ILink> getRelatedLinksWithType( final String typeName ) {

		ArrayList<ILink> linkArrLst = new ArrayList<ILink>();

		for ( ILink link : getRelatedLinks() ) {
			if ( linkHasType( link, typeName ) ) {
				linkArrLst.add( link );
			}
		}

		return linkArrLst;
	}

	private List<ILink> getRelatedLinks() {

		if ( this.relatedLinks == null ) {
			try {
				ILink[] relLnksArr = this.requestItem.getRelatedItems( null, LinkSortOrder.LINKSORT_ORDERING, false, null, null );
				this.relatedLinks = Arrays.asList( relLnksArr );
			}
			catch ( Exception e ) {
				e.printStackTrace();
			}
		}
		
		return this.relatedLinks;
	}

	private boolean linkHasView( final ILink link, final String viewName ) {
		try {
			return link.getViewName().equalsIgnoreCase( viewName );
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}

		return false;
	}

	private boolean linkHasType( final ILink link, final String typeName ) {
		try {
			return link.getChildItem().getType().getName().equalsIgnoreCase( typeName );
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}

		return false;
	}

	public IItem getSiteHomePageItem() {
		return siteHomePageItem;
	}

	public void setSiteHomePageItem( IItem siteHomePageItem ) {
		this.siteHomePageItem = siteHomePageItem;
	}

	public CachedItem getCachedSiteHomepage() {
		return cachedSiteHomepage;
	}

	public void setCachedSiteHomepage( CachedItem cachedSiteHomepage ) {
		this.cachedSiteHomepage = cachedSiteHomepage;
	}

}
