package com.slepeweb.cms.control;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.cms.bean.Host;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Media;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.StaticItem;
import com.slepeweb.cms.bean.Url;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.service.CmsService;
import com.slepeweb.cms.service.SiteService;
import com.slepeweb.common.bean.NameValuePair;
import com.slepeweb.common.service.HttpService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/static")
public class StaticPageController extends BaseController {
	
	private static Logger LOG = Logger.getLogger(StaticPageController.class);
	private static final Pattern HREF_PATTERN = Pattern.compile("href=\"(.*?)\"", 
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
	private static final Pattern SRC_PATTERN = Pattern.compile("src=\"(.*?)\"", 
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
	
	@Autowired private SiteService siteService;
	@Autowired private HttpService httpService;
	@Autowired private CmsService cmsService;
	
	@RequestMapping(value="/{siteShortname}")	
	public String main(@PathVariable String siteShortname, HttpServletRequest req, ModelMap model) {	
		
		Site s = this.siteService.getSiteByShortname(siteShortname);
		Map<String, StaticItem> urlMap = new HashMap<String, StaticItem>();
		
		if (s != null) {
			Host h = s.getDeliveryHost();

			if (h != null) {
				User u = getUser(req);
				NameValuePair authorisationHeader = this.httpService.getAuthorisationHeader(u.getEmail(), u.getPassword());
				NameValuePair staticDeliveryHeader = new NameValuePair("X-Static-Delivery", "1");
				List<NameValuePair> headers = Arrays.asList(new NameValuePair[] {authorisationHeader, staticDeliveryHeader});
				
				// Stage 1: Scrape content from CMS delivery server, and save html in temporary files
				if (s.isMultilingual()) {
					for (String language : s.getAllLanguages()) {
						crawlDynamicSite(new Url().setPath("/" + language), headers, s, h, u, urlMap);
					}
				}
				else {
					crawlDynamicSite(new Url().setPath("/"), headers, s, h, u, urlMap);
				}
				
				// Stage 2: Edit each temporary html file, replacing internal urls to match how the files were saved
				File root = new File(this.cmsService.getStaticSiteRoot() + "/" + siteShortname);
				for (File f : root.listFiles()) {
					if (! f.getName().equals("resources")) {
						crawlStaticSite(f, urlMap);	
					}
				}
			}
		}
		
		return "cms/crawlresults";
	}
	
	private void crawlDynamicSite(Url sourceUrl, List<NameValuePair> httpHeaders, Site s, Host h, User u, 
			Map<String, StaticItem> pathMap) {
		/*
		 * This url will represent any url that can be delivered by the cms, including:
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
		
		if (sourceUrl.getHostname() != null) {
			// All url's in CMS are relative; only external resources use full urls.
			// This must be an external url.
			return;
		}
		
		if (pathMap.containsKey(sourceUrl.getPathAndQuery())) {
			// This url has already been written to file - do not repeat.
			return;
		}

		sourceUrl.setProtocol(h.getProtocol());
		sourceUrl.setHostname(h.getNameAndPort());		
		
		// This is the default static path for non-cms items
		String itemPath = sourceUrl.getPath();
		Item i = null;
		StaticItem si = null;
		String html = null;
		
		if (! itemPath.startsWith("/resources/")) {
			if (s.isMultilingual() && sourceUrl.getPath().length() > 2) {
				itemPath = itemPath.substring(3);
				if (itemPath.equals("")) {
					itemPath = "/";
				}
			}
			
			i = s.getItem(itemPath);	
		}
		
		BufferedInputStream is = null;
		
		if (i != null) {
			i.setUser(u);
			
			// This url corresponds to a CMS item. What is it's mime type?
			String mimetype = i.getType().getMimeType();
			si = new StaticItem(sourceUrl, mimetype, i.getBindings().size() > 0);
			
			if (mimetype.equals("application/cms")) {
				html = this.httpService.get(sourceUrl.toString(), httpHeaders);
				if (StringUtils.isNotBlank(html)) {
					try {
						is = new BufferedInputStream(new ByteArrayInputStream(html.getBytes("utf-8")));
						writeToFile(is, toAbsoluteStaticPath(si.getTempStaticPath(), s.getShortname()));				
					} 
					catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
			else if (i.getType().isMedia()) {
				String view = sourceUrl.getQueryParam("view");				
				is = getMediaStream(i, mimetype.startsWith("image") && view != null && view.equals("thumbnail"));
				
				if (is != null) {
					writeToFile(is, toAbsoluteStaticPath(si.getStaticPath(), s.getShortname()));
				}
			}
		}
		else {
			// This must be a resource, given that it is being delivered by the cms,
			// yet is NOT an item.
			si = new StaticItem(sourceUrl, "not/classified", false);
			byte[] content = this.httpService.getBytes(sourceUrl.toString(), httpHeaders);
			is = new BufferedInputStream(new ByteArrayInputStream(content));
			writeToFile(is, toAbsoluteStaticPath(si.getStaticPath(), s.getShortname()));				
		}
		
		pathMap.put(sourceUrl.getPathAndQuery(), si);
		close(is);
		
		/* 
		 * IFF this is a page, ie if html has been retrieved,
		 * then identify all other links on this page, and crawl to them
		 */
		if (i != null && i.getType().getMimeType().equals("application/cms") && StringUtils.isNotBlank(html)) {
			Matcher m;
			String uri;
			
			for (Pattern p : new Pattern[] {HREF_PATTERN, SRC_PATTERN}) {
				m = p.matcher(html);
				
				while (m.find()) {
					uri = m.group(1);
					crawlDynamicSite(new Url().parse(uri), httpHeaders, s, h, u, pathMap);
				}
			}
		}
	}
	
	private void crawlStaticSite(File f, Map<String, StaticItem> pathMap) {
		if (f.isDirectory()) {
			for (File child : f.listFiles()) {
				crawlStaticSite(child, pathMap);
			}
		}
		else {
			if (f.getPath().endsWith(StaticItem.HTML_EXT + StaticItem.TEMP_EXT)) {
				replaceFile(f, pathMap);
			}
		}
	}
	
	private void replaceFile(File tempFile, Map<String, StaticItem> pathMap) {		
		String html = fileContents2String(tempFile);
		html = replaceUrls(html, HREF_PATTERN, "href", pathMap);
		html = replaceUrls(html, SRC_PATTERN, "src", pathMap);
		char[] chars = html.toCharArray();
		
		int len = tempFile.getPath().length();
		File replacement = new File(tempFile.getPath().substring(0, len - StaticItem.TEMP_EXT.length()));
		FileWriter fw = null;
		boolean ok = true;
		
		try {
			// Write contents out to new file
			fw = new FileWriter(replacement);
			fw.write(chars, 0, chars.length);		
			LOG.info(String.format("Replacement file created [%s]", replacement.getName()));
		}
		catch (Exception e) {
			LOG.error("Failed to replace file", e);
			ok = false;
		}
		finally {
			if (fw != null) {
				try {fw.close();}
				catch (Exception e) {}
			}
		}
		
		if (ok) {
			// Remove old file
			tempFile.delete();
			LOG.info(String.format("File deleted [%s]", tempFile.getName()));
		}
	}
	
	private String replaceUrls(String html, Pattern p, String attrib, Map<String, StaticItem> pathMap) {		
		Matcher m = p.matcher(html);
		StringBuffer sb = new StringBuffer();
		StaticItem si;		

		while (m.find()) {
			si = pathMap.get(m.group(1));

			if (si != null) {
				// Replace link ref with item path
				m.appendReplacement(sb, attrib + "=\"" + si.getStaticPath() + "\"");
			}			
		}
		
		m.appendTail(sb);
		return sb.toString();
	}
	
	private String fileContents2String(File f) {
		FileReader in = null;
		try {
			in = new FileReader(f);
			char[] chars = new char[1000];
			StringBuilder sb = new StringBuilder();
			int numChars = 0;
			while ((numChars = in.read(chars, 0, chars.length)) > -1) {
				sb.append(chars, 0, numChars);
			}
			
			return sb.toString();
		}
		catch (Exception e) {
			
		}
		finally {
			if (in != null) {
				try {in.close();}
				catch (Exception e) {}
			}
		}
		
		return null;
	}
	
	private void close(BufferedInputStream is) {
		try {
			if (is != null) {
				is.close();
			}
		}
		catch(IOException e) {}
	}
	
	private BufferedInputStream getMediaStream(Item i, boolean thumbnailReqd) {
		Media m = thumbnailReqd ? i.getThumbnail() : i.getMedia();
		if (m != null) {
			InputStream is = m.getDownloadStream();
			
			if (is != null) {
				try {					
					return new BufferedInputStream(is);
				} catch (Exception e) {
					LOG.error("Failed to get input stream for Media item", e);
				}
			}
		}
		
		LOG.error("Failed to get input stream for Media item");
		return null;
	}
	
	private String toAbsoluteStaticPath(String path, String siteShortname) {
		return new StringBuilder(this.cmsService.getStaticSiteRoot()).append("/").
				append(siteShortname).append(path).toString();
	}
	
	private boolean writeToFile(BufferedInputStream is, String staticPath) {
		FileOutputStream fos = null;
		
		try {
			// Create all necessary directories for this file
			mkdirs(staticPath);
			
			// Now create the file itself
			fos = new FileOutputStream(staticPath);
			int bufflen = 1000;
			byte[] buffer = new byte[bufflen];
			int numBytes;
			while ((numBytes = is.read(buffer, 0, bufflen)) > -1) {
				fos.write(buffer, 0, numBytes);
			}
			
			LOG.info(String.format("File written [%s]", staticPath));
			return true;
		}
		catch (Exception e) {
			LOG.warn(String.format("Error writing media out to file [%s]", staticPath), e);
		}
		finally {
			try {
				is.close();
				if (fos != null) fos.close();
			}
			catch (Exception e) {}
		}
		
		return false;
	}
	
	private void mkdirs(String staticPath) {
		int cursor = staticPath.lastIndexOf("/");
		String directoryPart = staticPath.substring(0, cursor);
		File directory = new File(directoryPart);
		directory.mkdirs();
	}
}
