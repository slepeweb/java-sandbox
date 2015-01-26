package com.slepeweb.cms.component;

import org.springframework.stereotype.Component;

@Component
public class ServerConfig {
	private boolean liveDelivery = true;

	public boolean isLiveDelivery() {
		return liveDelivery;
	}

	public void setLiveDelivery(boolean liveDelivery) {
		this.liveDelivery = liveDelivery;
	}
}
