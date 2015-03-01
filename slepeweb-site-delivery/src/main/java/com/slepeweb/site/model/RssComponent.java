package com.slepeweb.site.model;

import java.util.List;

import com.slepeweb.cms.bean.Link;
import com.slepeweb.site.bean.DatedLinkTarget;

public class RssComponent extends SimpleComponent {
	private static final long serialVersionUID = 1L;
	//private static Logger LOG = Logger.getLogger(RssComponent.class);

	private List<DatedLinkTarget> targets;

	public RssComponent setup(Link l) {
		super.setup(l);		
		return this;
	}
	
	@Override
	public String toString() {
		return String.format("RssComponent (%s): %s", getType(), getHeading());
	}

	public List<DatedLinkTarget> getTargets() {
		return targets;
	}

	public RssComponent setTargets(List<DatedLinkTarget> targets) {
		this.targets = targets;
		return this;
	}	
}
