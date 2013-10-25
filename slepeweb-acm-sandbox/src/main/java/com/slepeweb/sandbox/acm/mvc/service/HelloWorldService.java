package com.slepeweb.sandbox.acm.mvc.service;

import java.util.List;

import com.slepeweb.sandbox.acm.mvc.annotation.AcmObjectAnno;
import com.slepeweb.sandbox.acm.navcache.CachedItem;

public interface HelloWorldService {
	List<CachedItem> getLevelOneItems( @AcmObjectAnno Object acmObject );
}
