package com.slepeweb.site.bean;

import com.slepeweb.cms.bean.Item;

public class StaticItem {

	public static final String TEMP_EXT = ".tmp";
	public static final String HTML_EXT = ".html";
	
	private UriSupport urlSupport;
	private String staticPath, mimetype;
	private boolean directory;
	
	public StaticItem(Item i, UriSupport parser) {
		this(parser, i.getType().getMimeType(), i.getBindings().size() > 0);
	}
	
	public StaticItem(UriSupport parser, String mimetype, boolean isDirectory) {
		this.urlSupport = parser;
		this.mimetype = mimetype;
		this.directory = isDirectory;
	}
	
	@Override
	public String toString() {
		return String.format("%s -> %s", this.urlSupport.getPathAndQuery(), this.staticPath);
	}
	
	public void setStaticPath4Resource() {
		this.staticPath = this.urlSupport.getPath();
	}
	
	public void setStaticPath4Page() {
		
		StringBuilder sb = new StringBuilder(this.urlSupport.getPath());
		
		if (this.directory) {
			if (! this.urlSupport.getPath().equals("/")) {
				sb.append("/");
			}
			
			sb.append("index");
		}
		
		if (this.urlSupport.isQueryPresent()) {
			sb.append("_").append(this.urlSupport.getQueryStr().replaceAll("[=&/]", "_"));
		}
		
		this.staticPath = sb.append(HTML_EXT).toString();
	}
		
	public void setStaticPath4Media(Item i) {
		
		StringBuilder sb = new StringBuilder("/media/").append(i.getOrigId());
		String view = this.urlSupport.getQueryParam("view");
		String ext = "";
		
		if (view != null && view.equals("thumbnail")) {
			sb.append("t");
		}
				
		if (this.mimetype.startsWith("image")) {

			if (this.mimetype.equals("image/gif")) {
				ext = ".gif";
			}
			else if (this.mimetype.equals("image/jpg")) {
				ext = ".jpg";
			}
			else if (this.mimetype.equals("image/jpeg")) {
				ext = ".jpeg";
			}
			else if (this.mimetype.equals("image/png")) {
				ext = ".png";
			}
		}
		else {
			if (this.mimetype.equals("text/javascript")) {
				ext = ".js";
			}
			else if (this.mimetype.equals("text/css")) {
				ext = ".css";
			}
			else if (this.mimetype.equals("application/pdf")) {
				ext = ".pdf";
			}
		}
		
		if (! sb.toString().endsWith(ext)) {
			sb.append(ext);
		}
		
		this.staticPath = sb.toString();
	}
		

	public static void main(String[] args) {
		System.out.println(new StaticItem(new UriSupport("/a/b/c"), "application/cms", false).getStaticPath());
		System.out.println(new StaticItem(new UriSupport("/a/b/c"), "application/cms", true).getStaticPath());
		System.out.println(new StaticItem(new UriSupport("/a/b/c?view=gallery/1234"), "application/cms", false).getStaticPath());
		System.out.println(new StaticItem(new UriSupport("/a/b/c?view=gallery/1234"), "application/cms", true).getStaticPath());
		System.out.println(new StaticItem(new UriSupport("/a/b/c/d"), "image/jpg", false).getStaticPath());
		System.out.println(new StaticItem(new UriSupport("/a/b/c/d.jpg"), "image/jpg", false).getStaticPath());
		System.out.println(new StaticItem(new UriSupport("/a/b/c/d?view=thumbnail"), "image/jpg", false).getStaticPath());
		System.out.println(new StaticItem(new UriSupport("/a/b/c/d.jpg?view=thumbnail"), "image/jpg", false).getStaticPath());
		System.out.println(new StaticItem(new UriSupport("/a/b/c/d.jpg?view=thumbnail"), "image/jpeg", false).getStaticPath());
		System.out.println(new StaticItem(new UriSupport("/a/b/c/d.jpeg?view=thumbnail"), "image/jpeg", false).getStaticPath());
	}
	
	public String getStaticPath() {
		return staticPath;
	}

	public String getTempStaticPath() {
		return this.staticPath + TEMP_EXT;
	}

	public UriSupport getUriSupport() {
		return this.urlSupport;
	}

	public String getMimetype() {
		return this.mimetype;
	}

	public boolean isDirectory() {
		return directory;
	}

	public void setDirectory(boolean directory) {
		this.directory = directory;
	}

}
