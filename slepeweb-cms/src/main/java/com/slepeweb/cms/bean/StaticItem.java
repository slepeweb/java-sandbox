package com.slepeweb.cms.bean;

public class StaticItem {

	public static final String TEMP_EXT = ".tmp";
	public static final String HTML_EXT = ".html";
	public static final String THUMB_EXT = "_thumb";
	
	private Url sourceUrl;
	private String staticPath, mimetype;
	
	public StaticItem(Url url, String mimetype, boolean isDirectory) {
		this.sourceUrl = url;
		this.mimetype = mimetype;
		setStaticPath(url, isDirectory);
	}
	
	private StaticItem setStaticPath(Url url, boolean isDirectory) {
		this.staticPath = url.getPath();
		
		// Update default static path according to mimetype, and whether static item is a directory or not.
		if (this.mimetype.equals("application/cms")) {
			if (url.getQuery() != null) {
				this.staticPath += "_" + url.getQuery().replaceAll("[=&/]", "_");
				
				// Requests involving query parameters can be treated as files
				isDirectory = false;
			}
			
			if (isDirectory) {
				this.staticPath += "/index" + HTML_EXT;
			}
			else {
				this.staticPath = setStaticPathExtension(this.staticPath, HTML_EXT);
			}
		}
		else {
			if (this.mimetype.startsWith("image")) {
				String view = url.getQueryParam("view");
				if (view != null && view.equals("thumbnail")) {
					setStaticThumbnailPath();
				}
				
				if (this.mimetype.equals("image/gif")) {
					this.staticPath = setStaticPathExtension(this.staticPath, ".gif");
				}
				else if (this.mimetype.equals("image/jpg")) {
					this.staticPath = setStaticPathExtension(this.staticPath, ".jpg");
				}
				else if (this.mimetype.equals("image/jpeg")) {
					this.staticPath = setStaticPathExtension(this.staticPath, ".jpeg");
				}
				else if (this.mimetype.equals("image/png")) {
					this.staticPath = setStaticPathExtension(this.staticPath, ".png");
				}
			}
			else {
				if (this.mimetype.equals("text/javascript")) {
					this.staticPath = setStaticPathExtension(this.staticPath, ".js");
				}
				else if (this.mimetype.equals("text/css")) {
					this.staticPath = setStaticPathExtension(this.staticPath, ".css");
				}
				else if (this.mimetype.equals("application/pdf")) {
					this.staticPath = setStaticPathExtension(this.staticPath, ".pdf");
				}
			}
		}
		
		return this;
	}
		
	private String setStaticPathExtension(String path, String ext) {
		if (path.endsWith(ext)) {
			return path;
		}
		return path + ext;
	}
	
	public static void main(String[] args) {
		System.out.println(new StaticItem(new Url().parse("/a/b/c"), "application/cms", false).getStaticPath());
		System.out.println(new StaticItem(new Url().parse("/a/b/c"), "application/cms", true).getStaticPath());
		System.out.println(new StaticItem(new Url().parse("/a/b/c?view=gallery/1234"), "application/cms", false).getStaticPath());
		System.out.println(new StaticItem(new Url().parse("/a/b/c?view=gallery/1234"), "application/cms", true).getStaticPath());
		System.out.println(new StaticItem(new Url().parse("/a/b/c/d"), "image/jpg", false).getStaticPath());
		System.out.println(new StaticItem(new Url().parse("/a/b/c/d.jpg"), "image/jpg", false).getStaticPath());
		System.out.println(new StaticItem(new Url().parse("/a/b/c/d?view=thumbnail"), "image/jpg", false).getStaticPath());
		System.out.println(new StaticItem(new Url().parse("/a/b/c/d.jpg?view=thumbnail"), "image/jpg", false).getStaticPath());
		System.out.println(new StaticItem(new Url().parse("/a/b/c/d.jpg?view=thumbnail"), "image/jpeg", false).getStaticPath());
		System.out.println(new StaticItem(new Url().parse("/a/b/c/d.jpeg?view=thumbnail"), "image/jpeg", false).getStaticPath());
	}
	
	public String getStaticPath() {
		return staticPath;
	}

	public String getTempStaticPath() {
		return this.staticPath + TEMP_EXT;
	}

	public void setStaticPath(String staticUrl) {
		this.staticPath = staticUrl;
	}

	public Url getSourceUrl() {
		return this.sourceUrl;
	}

	private void setStaticThumbnailPath() {
		if (this.staticPath.matches("^.*?\\.(jpg|jpeg|gif|png)$")) {
			int cursor = this.staticPath.lastIndexOf(".");
			this.staticPath =  this.staticPath.substring(0, cursor) + "_thumb" + this.staticPath.substring(cursor);
		}
		else {
			this.staticPath =  this.staticPath + "_thumb";
		}
	}

	public String getMimetype() {
		return this.mimetype;
	}
}
