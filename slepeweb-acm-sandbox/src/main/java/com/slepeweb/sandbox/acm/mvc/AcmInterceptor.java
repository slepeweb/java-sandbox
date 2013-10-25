package com.slepeweb.sandbox.acm.mvc;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.mediasurface.client.IItem;
import com.mediasurface.client.IType;
import com.mediasurface.client.Mediasurface;
import com.mediasurface.datatypes.SecurityContextHandle;
import com.mediasurface.general.AuthorizationException;
import com.mediasurface.general.ResourceException;
import com.slepeweb.sandbox.acm.constants.ItemTypeName;
import com.slepeweb.sandbox.acm.constants.RequestAttribute;
import com.slepeweb.sandbox.acm.mvc.bean.AcmObject;
import com.slepeweb.sandbox.acm.mvc.service.CachedItemService;
import com.slepeweb.sandbox.acm.navcache.CachedItem;

public class AcmInterceptor extends HandlerInterceptorAdapter implements ServletContextAware {
	private static Logger LOG = Logger.getLogger( AcmInterceptor.class );

	private ServletContext servletContext;

	@Autowired
	private CachedItemService cachedItemService;

	/**
	 * We want to be able to efficiently obtain the IItem that MediaSurface returned, but we also want out Controllers to
	 * be independant of ACM so we can do "dummy" code in order to speed up development and to make the Controller layer
	 * ACM-agnostic. We also want the service method parameters to be serialisable for cache purposes.
	 * 
	 * IItem does not appear to be serializable.
	 * 
	 * So to do this we put the item ID (url) into request scope.
	 * 
	 * We also add the IItem to a cache in ServletContext to improve performance in the Service layer (so it doesn't ask
	 * for the IItem a second time)
	 */
	@Override
	public boolean preHandle( HttpServletRequest req, HttpServletResponse response, Object handler ) throws Exception {
		storeUrlAndAcmObject( req );
		return super.preHandle( req, response, handler );
	}

	private void storeUrlAndAcmObject( HttpServletRequest req ) throws AuthorizationException, ResourceException {
		IItem item = getItem( req );
		if ( item != null ) {
			String url = getUrl( item );
			req.setAttribute( RequestAttribute.REQUEST_URL, url );

			IType type = getType( req );
			SecurityContextHandle sctx = ( SecurityContextHandle ) getSessionAttribute( req, RequestAttribute.SECURITY_CONTEXT );
			Mediasurface ms = getMediasurface();

			CachedItem cachedRequestItem = cachedItemService.getCachedItem( item );
			AcmObject acmObject = new AcmObject( item, cachedRequestItem, type, sctx, ms );
			
			// only required for site planner item
			if ( ! cachedRequestItem.getPath().startsWith( "/content/" ) ) {
				IItem homeItem = getSiteHomePage( acmObject.getRequestItem(), acmObject.getCachedRequestItem() );

				if ( homeItem != null ) {
					acmObject.setSiteHomePageItem( homeItem );
					acmObject.setCachedSiteHomepage( cachedItemService.getCachedItem( homeItem ) );
					//LOG.debug( "sitehome page :" + acmObject.getCachedSiteHomepage().getPath() );

				}
				else {
					LOG.warn( "Home page item is null" );
				}

			}

			Boolean isLiveServer = ( Boolean ) servletContext.getAttribute( RequestAttribute.IS_LIVE_SERVER_ATT_NAME );
			Boolean isMorelloRequest = ( Boolean ) req.getSession().getAttribute( RequestAttribute.IS_MORELLO_REQUEST );

			acmObject.setCacheableRequest( isLiveServer && ! isMorelloRequest );
			req.setAttribute( RequestAttribute.ACM_OBJECT, acmObject );
		}
	}

	private IItem getSiteHomePage( IItem item, CachedItem link ) throws AuthorizationException, ResourceException {
		while ( link != null && ! link.getPath().equals( "/" ) && ! link.getItemTypeName().equals( ItemTypeName.HOMEPAGE ) ) {
			item = item.getParent();
			link = cachedItemService.getCachedItem( item );

			if ( link != null ) {
//				LOG.debug( link.getPath() + ":" + link.getItemTypeName() + ":"
//						+ ( ! link.getPath().equals( "/" ) || ! link.getItemTypeName().equals( ItemTypeName.HOMEPAGE ) ) );
			}
			else {
				// Request item may be in the content store, eg. for admin page.
				// In these cases, site-homepage will be null.
			}
		}

		return item;
	}

	private String getUrl( IItem item ) {
		String url = "";
		try {
			url = item.getUrl();
		}
		catch ( AuthorizationException e ) {
		}
		catch ( ResourceException e ) {
		}
		return url;
	}

	private IItem getItem( HttpServletRequest request ) {
		Object obj = request.getAttribute( RequestAttribute.REQUEST_ITEM );
		IItem item = null;

		if ( obj != null && obj instanceof IItem ) {
			item = ( IItem ) obj;
		}

		return item;
	}

	private IType getType( HttpServletRequest request ) {
		Object obj = request.getAttribute( RequestAttribute.REQUEST_ITEM_TYPE );
		IType type = null;

		if ( obj != null && obj instanceof IType ) {
			type = ( IType ) obj;
		}

		return type;
	}

	//@Override
	public void setServletContext( ServletContext servletContext ) {
		this.servletContext = servletContext;
	}

	private Object getSessionAttribute( HttpServletRequest request, String attributeName ) {
		Object result = null;
		if ( request != null ) {
			HttpSession session = request.getSession( false );
			if ( session != null ) {
				result = session.getAttribute( attributeName );
			}
		}
		return result;
	}

	private Mediasurface getMediasurface() {
		return ( Mediasurface ) servletContext.getAttribute( RequestAttribute.MEDIASURFACE );
	}
}
