package com.slepeweb.sandbox.acm.navcache;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

import org.apache.log4j.Logger;

public class AcmCacheEventListenerImpl implements CacheEventListener {
	private static Logger LOG = Logger.getLogger( AcmCacheEventListenerImpl.class );

	public void notifyElementRemoved( Ehcache cache, Element element ) throws CacheException {
		// TODO Auto-generated method stub
		
	}

	public void notifyElementPut( Ehcache cache, Element element ) throws CacheException {
		LOG.debug( String.format( "PUT [%s]", element.getObjectKey() ) );		
	}

	public void notifyElementUpdated( Ehcache cache, Element element ) throws CacheException {
		LOG.debug( String.format( "UPD [%s]", element.getObjectKey() ) );		
	}

	public void notifyElementExpired( Ehcache cache, Element element ) {
		LOG.debug( String.format( "EXP [%s]", element.getObjectKey() ) );		
	}

	public void notifyElementEvicted( Ehcache cache, Element element ) {
		LOG.debug( String.format( "EVC [%s]", element.getObjectKey() ) );		
	}

	public void notifyRemoveAll( Ehcache cache ) {
		LOG.warn( "REMOVE ALL" );		
	}

	public void dispose() {
		LOG.warn( "DISPOSE" );		
	}

  public Object clone() throws CloneNotSupportedException {
  	throw new CloneNotSupportedException();
  }
}
