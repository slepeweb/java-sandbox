package com.slepeweb.sandbox.www.model;

import java.util.ArrayList;
import java.util.List;

public class SimpleBlock extends Component {
	private String heading, body;
	private List<Link> relatedPages;
	
	public SimpleBlock() {
		super();
		this.relatedPages = new ArrayList<Link>();
	}
	
	public String getHeading() {
		return heading;
	}
	
	public SimpleBlock setHeading(String heading) {
		this.heading = heading;
		return this;
	}
	
	public String getBody() {
		return body;
	}
	
	public SimpleBlock setBody(String body) {
		this.body = body;
		return this;
	}
	
	public List<Link> getRelatedPages() {
		return relatedPages;
	}
}
