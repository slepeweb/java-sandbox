package com.slepeweb.cms.component;

public class ServerConfig {
	private boolean liveDelivery = true;
	private boolean commerceEnabled = true;

	public boolean isLiveDelivery() {
		return liveDelivery;
	}

	public void setLiveDelivery(boolean liveDelivery) {
		this.liveDelivery = liveDelivery;
	}

	public boolean isCommerceEnabled() {
		return commerceEnabled;
	}

	public void setCommerceEnabled(boolean commerceEnabled) {
		this.commerceEnabled = commerceEnabled;
	}
}
