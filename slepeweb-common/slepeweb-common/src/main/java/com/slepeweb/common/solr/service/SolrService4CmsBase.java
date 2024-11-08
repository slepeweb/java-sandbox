package com.slepeweb.common.solr.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.response.UpdateResponse;

public abstract class SolrService4CmsBase extends SolrServiceBase {
	
	private static Logger LOG = Logger.getLogger(SolrService4CmsBase.class);
		
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
	
	protected boolean saveItem(Object i) {
		List<Object> docs = makeDocuments(i);
		try {
			getBatchingClient().addBeans(docs);
			UpdateResponse resp = getBatchingClient().commit();
			LOG.info(String.format("Item successfully indexed by Solr, status [%d]", resp.getStatus()));
			return true;
		}
		catch (Exception e) {
			LOG.error("Solr failed to index item", e);
			return false;
		}
	}
	
	protected boolean removeItems(List<String> keys) {
		try {
			/*UpdateResponse resp = */ getClient().deleteById(keys);
			getClient().commit();
			LOG.debug("Item successfully removed from Solr index");
			return true;
		}
		catch (Exception e) {
			LOG.error("Solr failed to remove item from Solr index", e);
			return false;
		}
	}
	
	protected boolean removeItemBySiteId(long siteId) {
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
	
	protected boolean removeSectionByPath(long siteId, String path) {
		try {
			/*UpdateResponse resp = */ getClient().deleteByQuery(String.format("siteid:%d AND path_tokens:\"%s\"", siteId, path));
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
