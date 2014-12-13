package com.slepeweb.site.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.slepeweb.cms.bean.Item;


public class Page implements Serializable, NestableComponent {
	private static final long serialVersionUID = 1L;
	//private static Logger LOG = Logger.getLogger(Page.class);
	private String href;
	private Header header;
	private Footer footer;
	private Sidebar leftSidebar, rightSidebar;
	private String title, heading, body, view;
	private List<SimpleComponent> components;
	private List<String> roles;
	private Item item;
	
	public Page() {
		this.header = new Header(this);
		this.footer = new Footer();
		this.leftSidebar = new Sidebar(this, Sidebar.Type.left);
		this.rightSidebar = new Sidebar(this, Sidebar.Type.right);
		this.components = new ArrayList<SimpleComponent>();
	}
	
	public void setLeftNavigation() {
		List<LinkTarget> nav = new ArrayList<LinkTarget>();
		
		if (getHeader().getBreadcrumbs().size() > 1) {
			
			Item requestItem = getItem();
			Item levelOneItem = getHeader().getBreadcrumbItems().get(1);
			List<Item> levelOneBindings = levelOneItem.getBoundItems();
			
			if (levelOneBindings.size() > 0) {
				nav = new ArrayList<LinkTarget>();
				LinkTarget levelOneTarget = new LinkTarget(levelOneItem).setSelected(true);
				LinkTarget levelTwoTarget, levelThreeTarget;
				nav.add(levelOneTarget);
				
				for (Item levelTwoItem : levelOneBindings) {
					levelTwoTarget = new LinkTarget(levelTwoItem);
					levelTwoTarget.setSelected(requestItem.getPath().startsWith(levelTwoItem.getPath()));
					levelOneTarget.getChildren().add(levelTwoTarget);
					for (Item levelThreeItem : levelTwoItem.getBoundItems()) {
						levelThreeTarget = new LinkTarget(levelThreeItem);
						levelTwoTarget.getChildren().add(levelThreeTarget);
						levelThreeTarget.setSelected(requestItem.getPath().startsWith(levelThreeItem.getPath()));
					}
				}
			}
		}
		
		getLeftSidebar().setNavigation(nav);
	}
	
	public Page addRole(String r) {
		if (getRoles() == null) {
			setRoles(new ArrayList<String>());
		}
		getRoles().add(r);
		return this;
	}
	
	public Page addStylesheet(String path) {
		getHeader().getStylesheets().add(path);
		return this;
	}
	
	public Page addJavascript(String path) {
		getHeader().getJavascripts().add(path);
		return this;
	}
	
	public Header getHeader() {
		return header;
	}
	
	public Page setHeader(Header header) {
		this.header = header;
		return this;
	}
	
	public Footer getFooter() {
		return footer;
	}
	
	public Page setFooter(Footer footer) {
		this.footer = footer;
		return this;
	}
	
	public Sidebar getLeftSidebar() {
		return leftSidebar;
	}
	
	public Page setLeftSidebar(Sidebar leftSidebar) {
		this.leftSidebar = leftSidebar;
		return this;
	}
	
	public Sidebar getRightSidebar() {
		return rightSidebar;
	}
	
	public Page setRightSidebar(Sidebar rightSidebar) {
		this.rightSidebar = rightSidebar;
		return this;
	}
	
	public String getHeading() {
		return heading;
	}
	
	public Page setHeading(String heading) {
		this.heading = heading;
		return this;
	}
	
	public String getBody() {
		return body;
	}
	
	public Page setBody(String body) {
		this.body = body;
		return this;
	}
	
	public String getView() {
		return view;
	}
	
	public Page setView(String view) {
		this.view = view;
		return this;
	}
	
	public List<SimpleComponent> getComponents() {
		return components;
	}

	public void setComponents(List<SimpleComponent> components) {
		this.components = components;
	}

	public String getHref() {
		return href;
	}

	public Page setHref(String href) {
		this.href = href;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public Page setTitle(String title) {
		this.title = title;
		return this;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public Item getItem() {
		return item;
	}

	public Page setItem(Item item) {
		this.item = item;
		return this;
	}

}
