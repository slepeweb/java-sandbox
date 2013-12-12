package com.slepeweb.sandbox.acm.mvc.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mediasurface.client.ILink;
import com.mediasurface.datatypes.ItemFilter;
import com.mediasurface.general.LinkSortOrder;
import com.slepeweb.sandbox.acm.mvc.annotation.AcmObjectAnno;
import com.slepeweb.sandbox.acm.mvc.bean.AcmObject;
import com.slepeweb.sandbox.acm.navcache.CachedItem;

@Service( "helloWorldService" )
public class HelloWorldServiceImpl implements HelloWorldService {
	
	@Autowired
	private CachedItemService cachedItemService;

	public List<CachedItem> getLevelOneItems( @AcmObjectAnno Object acmObject ) {
		AcmObject acm = ( AcmObject ) acmObject;
		ItemFilter filter = null;
		List<CachedItem> list = new ArrayList<CachedItem> ();
		
		try {
			for ( ILink ilink : acm.getRequestItem().getBoundItems( LinkSortOrder.LINKSORT_ORDERING, false, null, filter, null ) ) {
				list.add( this.cachedItemService.getCachedItem( ilink.getChildItem() ) );
			}
		}
		catch ( Exception e ) {
			
		}
		
		return list;
	}
}
