package com.slepeweb.site.anc.bean;

import java.util.ArrayList;
import java.util.List;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.LinkFilter;
import com.slepeweb.site.model.Image;

public class Document {

	private Item item;
	private String heading, teaser, body;
	private List<Image> images;
	private List<Document> subSections;
	
	public Document(Item i) {
		this.item = i;
		this.heading = i.getFieldValue("heading");
		this.teaser = i.getFieldValue("teaser");
		this.body = i.getFieldValue("body");
	}
	
	public Item getItem() {
		return item;
	}

	public String getHeading() {
		return heading;
	}

	public String getTeaser() {
		return teaser;
	}

	public String getBody() {
		return body;
	}

	public List<Image> getImages() {
		if (this.images == null) {
			this.images = new ArrayList<Image>();
			LinkFilter f = new LinkFilter().setLinkNames(new String[] {"std", "passport_photo"});
			for (Link l : f.filter(getItem().getInlines())) {
				this.images.add(new Image(l));
			}
		}
		
		return this.images;
	}

	public List<Document> getSubsections() {
		if (this.subSections == null) {
			this.subSections = new ArrayList<Document>();
			Document d;
			LinkFilter f = new LinkFilter().setItemType("Document");
			
			for (Item i : f.filterItems(this.item.getBindings())) {
				d = new Document(i);
				this.subSections.add(d);
			}
		}
		
		return this.subSections;
	}
}
