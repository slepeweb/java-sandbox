package com.slepeweb.common.solr.service;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

public class SolrServiceBase {
	
	private static Logger LOG = Logger.getLogger(SolrServiceBase.class);
		
	private String serverUrl;
	private SolrClient client;
	private ConcurrentUpdateSolrClient batchingClient;


	protected SolrClient getClient() {
		if (this.client == null) {
			this.client = new HttpSolrClient(this.serverUrl);
			LOG.info(String.format("Initialised solr server [%s]", this.serverUrl));
		}
		return this.client;
	}
	
	protected ConcurrentUpdateSolrClient getBatchingClient() {
		if (this.batchingClient == null) {
			try {
				this.batchingClient = new ConcurrentUpdateSolrClient(getServerUrl(), 100, 5);
				this.batchingClient.ping();
				LOG.info(String.format("Solr server is available for bulk updates [%s]", getServerUrl()));
			}
			catch (Exception e) {
				LOG.error(String.format("Solr server is NOT available for bulk updates [%s]: %s", getServerUrl(), e.getMessage()));
			}
		}

		return this.batchingClient;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}
	
	public String getServerUrl() {
		return this.serverUrl;
	}
}
