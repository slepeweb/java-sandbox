package com.slepeweb.common.solr.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;

public abstract class SolrService4CmsBase extends SolrServiceBase {
	
	private static Logger LOG = Logger.getLogger(SolrServiceBase.class);
		
	protected Object getDocument(long itemId, String language, Class<?> clazz) {
		try {
			org.apache.solr.common.SolrDocument doc = getClient().getById(String.format("%d-%s", itemId, language));
			DocumentObjectBinder binder = new DocumentObjectBinder();
			return binder.getBean(clazz, doc);
		}
		catch (Exception e) {
			LOG.error("Failed to retrieve Solr document", e);
			return null;
		}
	}
	
	/*
	 * Object i should be an Item instance. The Object class is used in the method signature,
	 * so that this 'common' class can be independant of the cms project.
	 */
	protected abstract List<Object> makeDocuments(Object i);
	
	protected boolean save(long siteId, Object i) {
		List<Object> docs = makeDocuments(i);
		try {
			/*UpdateResponse resp = */ getBatchingClient().addBeans(docs);
			getClient().commit();
			LOG.debug("Item successfully indexed by Solr");
			return true;
		}
		catch (Exception e) {
			LOG.error("Solr failed to index item", e);
			return false;
		}
	}
	
	protected boolean remove(long siteId, long origId) {
		try {
			/*UpdateResponse resp = */ getClient().deleteById(String.valueOf(origId));
			getClient().commit();
			LOG.debug("Item successfully removed from Solr index");
			return true;
		}
		catch (Exception e) {
			LOG.error("Solr failed to remove item from Solr index", e);
			return false;
		}
	}
	
	protected boolean remove(long siteId) {
		try {
			/*UpdateResponse resp = */ getClient().deleteByQuery(String.format("siteid:%d", siteId));
			getClient().commit();
			LOG.debug("Items successfully removed from Solr index");
			return true;
		}
		catch (Exception e) {
			LOG.error("Solr failed to remove items from Solr index", e);
			return false;
		}
	}
}
