package com.slepeweb.site.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.Host;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.Media;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.constant.ItemTypeName;
import com.slepeweb.cms.service.CmsService;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.cms.service.MediaFileService;
import com.slepeweb.common.bean.NameValuePair;
import com.slepeweb.common.service.HttpService;
import com.slepeweb.site.bean.StaticItem;
import com.slepeweb.site.bean.UriSupport;

@Service
public class StaticSiteServiceImpl implements StaticSiteService {
	
	private static Logger LOG = Logger.getLogger(StaticSiteServiceImpl.class);
	private static final Pattern MINIPATH_PATTERN = Pattern.compile("^(/(\\w\\w))?/\\$_(\\d+)(\\?.*)?$");
	private static final Set<PosixFilePermission> MEDIA_PERMISSIONS =  PosixFilePermissions.fromString("rw-r--r--");
	private static final String STATIC_DELIVERY_PARAM = "staticd";
	private static final String RESOURCES_FILENAME = "resources";
	private static final String RESOURCES_PATH = "/" + RESOURCES_FILENAME;
	private static final String MEDIA_PATH = "/media";

	
	@Autowired private ItemService itemService;
	@Autowired private HttpService httpService;
	@Autowired private MediaFileService mediaFileService;
	@Autowired private CmsService cmsService;
	
	private String rootFilePath;
	
	public record Wallet(Site site, Host host, User user, CloseableHttpClient httpClient, List<NameValuePair> headers) {}

	public void build(Item i, String sessionId) throws Exception {
		
		Map<String, StaticItem> urlMap = new HashMap<String, StaticItem>();
		
		Site s = i.getSite();
		User u = i.getUser();
		Host h = s.getDeliveryHost();
		
		if (this.rootFilePath == null) {
			this.rootFilePath = new StringBuilder(this.cmsService.getStaticSiteRoot()).
				append("/").
				append(s.getShortname()).
				toString();
		}

		// Create required directories on the file system
		Files.createDirectories(Path.of(toAbsoluteStaticPath(MEDIA_PATH)));
		Files.createDirectories(Path.of(toAbsoluteStaticPath(RESOURCES_PATH + "/" + s.getShortname())));
		
		List<NameValuePair> headers = new ArrayList<NameValuePair>();
		headers.add(new NameValuePair("X-Static-Delivery", "1"));
		headers.add(new NameValuePair("Cookie", "JSESSIONID=" + sessionId));
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		Wallet wallet = new Wallet(s, h, u, httpclient, headers);		
		
		// Stage 1: Scrape content from CMS delivery server, and save html in temporary files
		if (s.isMultilingual()) {
			for (String language : s.getAllLanguages()) {
				crawlDynamicSite(wallet, new UriSupport("/" + language), urlMap);
			}
		}
		else {
			crawlDynamicSite(wallet, new UriSupport("/"), urlMap);
		}
		
		// Stage 2: Edit each temporary html file, replacing internal urls to match the static files created
		File root = new File(this.cmsService.getStaticSiteRoot() + "/" + s.getShortname());
		for (File f : root.listFiles()) {
			if (! f.getName().equals("resources")) {
				crawlStaticSite(f, s, urlMap);	
			}
		}
	}
	
	private void crawlDynamicSite(Wallet wallet, UriSupport usupp, Map<String, StaticItem> pathMap) {
		/*
		 * sourceUrl represents any url that can be delivered by the cms, including:
		 * - stylesheet
		 * - javascript
		 * - image
		 * - pdf
		 * - page
		 * 
		 * All other url's can be ignored.
		 * 
		 * Note that files below /resources can also be delivered by the CMS, but they are NOT cms items.
		 */
		
		if (! preReqsSatisfied(wallet.host(), usupp, pathMap)) {
			// All url's in CMS are relative; only external resources use full urls.
			// This must be an external url.
			return;
		}
				
		/*
		 * If when identifyItem finds that a) a minipath has been used in the sourceUrl,
		 * and b) the item belongs to THIS site, then the sourceUrl's path is expanded
		 */
		Item i = identifyItem(wallet.site(), usupp);
		StaticItem si = null;
		String html = null;
				
		if (i != null) {
			i.setUser(wallet.user());
			
			// We have navigated to an item, either a Page or media
			// Have we already processed it?
			if (pathMap.containsKey(usupp.getPathAndQuery())) {
				return;
			}
			
			// Do we want it displayed on the site?
			if (
					! i.isRoot() && (
							i.isHiddenFromNav() ||
							i.getType().getName().equals(ItemTypeName.CONTENT_FOLDER) ||
							! i.isAccessible())) {
				
				return;
			}

			// This url corresponds to a CMS item. What is it's mime type?
			si = new StaticItem(i, usupp);
			
			if (i.isPage()) {
				html = renderPage(i, si, wallet);
			}
			else if (i.getType().isMedia()) {
				copyMedia(i, si);
			}
			
			doubleMapStaticItem(si, pathMap);
			
			/* 
			 * IFF:
			 * - this is a page, ie if html has been retrieved
			 * - we got here by drilling DOWN the hierarchy
			 * 
			 * then identify all other links/inlines on this page, and crawl to them
			 * and map them, BUT DO NOT continue further from there
			 */
			if (i.isPage() && StringUtils.isNotBlank(html)) {
				Document doc = createJsoupDocument(html);
				followInlines(doc, pathMap, wallet);
				
				// Deal with related media items (eg. pdfs)
				followRelatedMedia(i, pathMap, wallet);
				
				// Finally, continue crawling DOWN the site hierarchy 
				followBindings(i, pathMap, wallet);
			}
		}
		else {
			// This must be a resource, given that it is being delivered by the cms,urlSupport
			// yet is NOT an item.			
			if (pathMap.containsKey(usupp.getPathAndQuery())) {
				return;
			}
			
			if (renderResource(usupp, wallet)) {
				String key = usupp.getPathAndQuery();
				if (! pathMap.containsKey(key)) {
					pathMap.put(key, si);
				}
			}
		}
		
	}
	
	private String renderPage(Item i, StaticItem si, Wallet wallet) {
		UriSupport usupp = si.getUriSupport();
		si.setStaticPath4Page();
		usupp.addQueryParam(STATIC_DELIVERY_PARAM, "1");
		String html = this.httpService.get(wallet.httpClient(), usupp.toString(), wallet.headers());
		usupp.removeQueryParam(STATIC_DELIVERY_PARAM);
		
		if (StringUtils.isNotBlank(html)) {
			writeToFile(i, html, si.getTempStaticPath());
		}
		
		return html;
	}
	
	private void copyMedia(Item i, StaticItem si) {
		UriSupport usupp = si.getUriSupport();
		si.setStaticPath4Media(i);
		String view = usupp.getQueryParam("view");
		Media m = view != null && view.equals("thumbnail") ? i.getThumbnail() : i.getMedia();
		
		if (m != null) {
			String mediaFilePath = this.mediaFileService.getRepositoryFilePath(m.getFolder(), m.getRepositoryFileName());
			String targetPath = toAbsoluteStaticPath(si.getStaticPath());
			copyFile(mediaFilePath, targetPath);
		}
	}
	
	private boolean renderResource(UriSupport usupp, Wallet wallet) {
		StaticItem si = new StaticItem(usupp, "not/classified", false);
		si.setStaticPath4Resource();
		String html = this.httpService.get(wallet.httpClient(), usupp.toString(), wallet.headers());
		return writeToFile(null, html, si.getStaticPath());
	}
	
	private void followInlines(Document doc, Map<String, StaticItem> pathMap, Wallet wallet) {
		
		followInlines(doc, "img", "src", pathMap, wallet);
		followInlines(doc, "script", "src", pathMap, wallet);
		followInlines(doc, "link", "href", pathMap, wallet);
	}

	private void followInlines(Document doc, String tag, String attr, Map<String, StaticItem> pathMap, Wallet wallet) {
		
		String url;
		UriSupport urlSupport;
		
		for (Element ele : doc.getElementsByTag(tag)) {
			url = ele.attr(attr);
			if (! (url.startsWith("#") || StringUtils.isBlank(url))) {
				urlSupport = new UriSupport(url);
				crawlDynamicSite(wallet, urlSupport, pathMap);
			}
		}
	}
	
	private void followBindings(Item i, Map<String, StaticItem> pathMap, Wallet wallet) {
		
		Item child;
		for (Link l : i.getBindings()) {
			child = l.getChild();
			if (child.isPage()) {
				crawlDynamicSite(wallet, new UriSupport(child.getPath()), pathMap);
			}
		}
	}
	
	private void followRelatedMedia(Item i, Map<String, StaticItem> pathMap, Wallet wallet) {
		
		Item child;
		for (Link l : i.getRelations()) {
			child = l.getChild();
			if (child.getType().isMedia()) {
				crawlDynamicSite(wallet, new UriSupport(child.getPath()), pathMap);
			}
		}
	}
	
	private boolean preReqsSatisfied(Host h, UriSupport urlSupport, Map<String, StaticItem> pathMap) {
		
		if (StringUtils.isBlank(urlSupport.getPath())) {
			// Typically, this happens when a 'src' or 'href' attribute is blank
			return false;
		}
		
		if (urlSupport.getHost() != null) {
			// All url's in CMS are relative; only external resources use full urls.
			// This must be an external url.
			return false;
		}
		
		urlSupport.setProtocol(h.getProtocol());
		urlSupport.setHost(h.getNameAndPort());
		return true;
	}
	
	private Item identifyItem(Site s, UriSupport urlSupport) {
		String itemPath = urlSupport.getPath();
		Item i = null;
		
		if (itemPath == null) {
			return null;
		}
		
		if (! itemPath.startsWith("/resources/")) {
			
			if (s.isMultilingual() && urlSupport.getPath().length() > 2) {
				itemPath = itemPath.substring(3);
				if (itemPath.equals("")) {
					itemPath = "/";
				}
			}
			
			Matcher m = MINIPATH_PATTERN.matcher(itemPath);
			
			if (m.matches()) {
				i = this.itemService.getItemByOriginalId(Long.valueOf(m.group(3)));
				if (i != null && i.getSite().getId().equals(s.getId())) {
					// So, path WAS a minipath - NOW change it to a true path
					urlSupport.setPath(i.getPath());
				}
				else {
					/* The minipath relates to an item on a different site. This typically is
					 * the case for image items stored in the pho site. Leave urlSupport as-is.
					 */
				}
			}
			else {
				i = s.getItem(itemPath);
			}
			
		}
		
		if (i != null) {
			// All items have a minipath representation
			urlSupport.setMinipath(i.getOrigId());
		}
		return i;
	}
	
	private void doubleMapStaticItem(StaticItem si, Map<String, StaticItem> map) {
		/*
		 * Double up on entries to the map, since an item can be identified by both
		 * its path and its origId.
		 */
		String key = si.getUriSupport().getPathAndQuery();
		if (! map.containsKey(key)) {
			map.put(key, si);
		}
		
		key = si.getUriSupport().getMinipathAndQuery();
		if (! map.containsKey(key)) {
			map.put(key, si);
		}
	}
	
	private void crawlStaticSite(File f, Site s, Map<String, StaticItem> pathMap) {
		if (f.isDirectory()) {
			for (File child : f.listFiles()) {
				crawlStaticSite(child, s, pathMap);
			}
		}
		else {
			if (f.getPath().endsWith(StaticItem.HTML_EXT + StaticItem.TEMP_EXT)) {
				updateLinksInFile(f.getPath(), s, pathMap);
			}
		}
	}
	
	private void updateLinksInFile(String tempFilePath, Site s, Map<String, StaticItem> pathMap) {		
		String html = readFile(tempFilePath);
		Document doc = createJsoupDocument(html);

		replaceUrls(doc, "a", "href", s, pathMap);
		replaceUrls(doc, "img", "src", s, pathMap);
		html = doc.html();

		int len = tempFilePath.length();
		String staticPath = tempFilePath.substring(0, len - StaticItem.TEMP_EXT.length());
		boolean ok = writeToFile(html, staticPath);
		
		if (ok) {
			// Remove old file
			deleteFile(tempFilePath);
		}
	}
	
	private Document createJsoupDocument(String html) {
    	Document doc = Jsoup.parse(html);
    	doc.outputSettings()
	 	   .syntax(Document.OutputSettings.Syntax.xml) // ← forces XHTML output
	 	   .escapeMode(Entities.EscapeMode.xhtml)
	 	   .charset(StandardCharsets.UTF_8);
    	
    	return doc;
	}

	private Document replaceUrls(Document doc, String tag, String attr, Site s, Map<String, StaticItem> pathMap) {
		
    	Elements eles = doc.getElementsByTag(tag);
    	String uri;
		StaticItem si;
		UriSupport urlSupport;
    	 
    	for (Element ele : eles) {
    		uri = ele.attr(attr);

    		if (uri.startsWith("#") || StringUtils.isBlank(uri)) {
    			continue;
    		}
    		
    		urlSupport = new UriSupport(uri);
    		si = pathMap.get(urlSupport.getPathAndQuery());
    		
    		if (si != null) {
    			ele.attr(attr, si.getStaticPath());
    		}
    		else {
    			LOG.warn(String.format("No replacement found for url [%s] on '%s' attribute of '%s' tag)", urlSupport.getPathAndQuery(), attr, tag));
    		}
    	}
    	
		return doc;
	}
	
	private String readFile(String filePath) {
		try {
			Path path = Path.of(filePath);
        	return Files.readString(path, StandardCharsets.UTF_8);
		}
		catch (IOException e) {
			LOG.warn(String.format("Failed to read file [%s]: %s", filePath, e.getMessage()));
		}
		
		return null;
	}
	
	private String toAbsoluteStaticPath(String path) {
		return new StringBuilder(this.rootFilePath).append(path).toString();
	}

	private boolean writeToFile(Item i, String html, String path) {
		String absPath = toAbsoluteStaticPath(path);
		
		if (! writeToFile(html, absPath)) {
			LOG.warn(String.format("Failed write: item=[%s], path=[%s]", i, absPath));
			return false;
		}
		
		return true;
	}
	
	private boolean writeToFile(String html, String filePath) {
		
		try {
			// Create all necessary directories for this file
			mkdirs(filePath);
			
			// Now create the file itself
			Path path = Path.of(filePath);
			Files.writeString(path, html);	
			LOG.info(String.format("File written [%s]", filePath));
			return true;
		}
		catch (Exception e) {
			LOG.warn(String.format("Error writing to file [%s]: %s", filePath, e.getMessage()));
		}
		
		return false;
	}
	
	private boolean deleteFile(String path) {
		try {
			Files.delete(Path.of(path));
			return true;
		}
		catch (IOException e) {
			LOG.warn(String.format("Error deleting file [%s]: %s", path, e.getMessage()));
		}
		
		return false;
	}
	
	private boolean copyFile(String from, String to) {
        try {
        	Path source = Path.of(from);
        	Path target = Path.of(to);
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            Files.setPosixFilePermissions(target, MEDIA_PERMISSIONS);
			LOG.info(String.format("Copied media item to [%s]", to));
            return true;
        } catch (Exception e) {
			LOG.warn(String.format("Error copying file [%s] to [%s]: %s", from, to, e.getMessage()));
        }
		return false;
	}
	
	// Code courtesy of ChatGPT!
	@SuppressWarnings("unused")
	private void copyDirectory(String from, String to) throws Exception {
		Path source = Path.of(from);
		Path target = Path.of(to);
		
		try (var paths = Files.walk(source)) {
		    paths.forEach(path -> {
		        Path targetPath = target.resolve(source.relativize(path));
		        try {
		            if (Files.isDirectory(path)) {
		                Files.createDirectories(targetPath);
		            } else {
		                Files.copy(path, targetPath, StandardCopyOption.REPLACE_EXISTING);
		            }
		        } catch (IOException e) {
		            throw new RuntimeException(e);
		        }
		    });
		}
	}

	private void mkdirs(String staticPath) {
		int cursor = staticPath.lastIndexOf("/");
		String directoryPart = staticPath.substring(0, cursor);
		File directory = new File(directoryPart);
		directory.mkdirs();
	}
}
