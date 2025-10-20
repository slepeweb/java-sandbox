package com.slepeweb.site.geo.control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.type.TypeReference;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.SiteConfigCache;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.constant.AttrName;
import com.slepeweb.cms.constant.SiteConfigKey;
import com.slepeweb.cms.service.QandAService;
import com.slepeweb.common.bean.MoneyDashboard;
import com.slepeweb.common.solr.bean.SolrConfig;
import com.slepeweb.common.util.HttpUtil;
import com.slepeweb.common.util.JsonUtil;
import com.slepeweb.site.bean.SolrParams4Site;
import com.slepeweb.site.control.BaseController;
import com.slepeweb.site.geo.bean.PasswordList;
import com.slepeweb.site.geo.bean.PasswordList.Account;
import com.slepeweb.site.geo.bean.PasswordList.Group;
import com.slepeweb.site.geo.bean.SectionMenu;
import com.slepeweb.site.geo.service.SolrService4Geo;
import com.slepeweb.site.model.LinkTarget;
import com.slepeweb.site.model.Page;
import com.slepeweb.site.service.MagicMarkupService;
import com.slepeweb.site.service.SiteCookieService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/spring/geo")
public class GeoPageController extends BaseController {
	
	public static final String HISTORY = "_history";
	public static final String MAGIC_MARKUP_SERVICE = "_magicMarkupService";
	public static final String LOCAL_HOSTNAME = "_localHostname";
	public static final String PASSKEY = "_passkey";
	
	@Autowired SolrService4Geo solrService4Geo;
	@Autowired SiteCookieService siteCookieService;
	@Autowired MagicMarkupService magicMarkupService;
	@Autowired QandAService qandAService;
	@Autowired SiteConfigCache siteConfigCache;
	
	@RequestMapping(value="/homepage")	
	public String homepage(
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			HttpServletRequest req,
			HttpServletResponse res,
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "homepage", model);
		addGeoExtras(i, req, res, model);
		return page.getView();
	}

	@RequestMapping(value="/searchresults")	
	public String searchResults(
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			HttpServletRequest req,
			HttpServletResponse res,
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "searchresults", model);
		
		String terms = req.getParameter("terms");
		SolrParams4Site params = new SolrParams4Site(i, new SolrConfig().setPageSize(20).setMaxPages(1));
		params.setSearchText(terms).setUser(i.getUser());
		model.addAttribute("_searchResponse", this.solrService4Geo.query(params));
		
		addGeoExtras(i, req, res, model);
		return page.getView();
	}

	@RequestMapping(value="/pagewide")	
	public String standardWide(
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			HttpServletRequest req,
			HttpServletResponse res,
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "standardWide", model);
		addGeoExtras(i, req, res, model);
		return page.getView();
	}

	@RequestMapping(value="/pagewide/pdf")	
	public String standardWidePdf(
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			HttpServletRequest req,
			HttpServletResponse res,
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "standardPdf", model);
		addPdfExtras(page, req, res, model);
		return page.getView();
	}

	@RequestMapping(value="/page3col")	
	public String standard3Col(
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			HttpServletRequest req,
			HttpServletResponse res,
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "standard3Col", model);
		addGeoExtras(i, req, res, model);
		return page.getView();
	}

	@RequestMapping(value="/page3col/pdf")	
	public String standard3ColPdf(
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			HttpServletRequest req,
			HttpServletResponse res,
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "standardPdf", model);
		addPdfExtras(page, req, res, model);
		return page.getView();
	}

	@RequestMapping(value="/topsecret")	
	public String topSecret(
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			HttpServletRequest req,
			HttpServletResponse res,
			ModelMap model) throws Exception {	
		
		// Does this user have top-secret access?
		if (! isSuperUser(req, res, i)) {
			return null;
		}
		
		Page page = getStandardPage(i, shortSitename, "standard3Col", model);
		addGeoExtras(i, req, res, model);
		return page.getView();
	}

	@RequestMapping(value="/remotepwg")	
	public String remotePwg(
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			HttpServletRequest req,
			HttpServletResponse res,
			ModelMap model) throws Exception {	
		
		if (! isSuperUser(req, res, i)) {
			return null;
		}
		
		if (req.getMethod().equalsIgnoreCase("get")) {
			Page page = getStandardPage(i, shortSitename, "pwForm", model);
			addGeoExtras(i, req, res, model);
			return page.getView();
		}
		
		// Dealing with form submission ...
		String alias = req.getParameter("alias");
		String key = req.getParameter("password");
		boolean important = req.getParameter("important") != null;
		
		Calendar now = Calendar.getInstance();
		String prefix = "" + zeroPad(now.get(Calendar.MINUTE)) + zeroPad(now.get(Calendar.HOUR_OF_DAY));
		String password = prefix + key + "^";
		String remotePath = "/list/passwords";		
		String remoteHost = this.siteConfigCache.getValue(i.getSite().getId(), SiteConfigKey.PWG_HOST, "http://localhost:8083");
		
		String json = httpPostMultipart(remoteHost + remotePath, alias, key, password);
		PasswordList pwl = json != null ? 
				JsonUtil.fromJson(new TypeReference<PasswordList>() {}, json) :
					new PasswordList().setError("Failed to process response from remote server");
		
		if (pwl == null || pwl.isError()) {
			res.sendRedirect(String.format("%s?error=%s", i.getPath(), 
					HttpUtil.encodeUrl(pwl != null ? pwl.getError() : "Un-specified error")));
			return null;
		}
		
		if (important) {
			Iterator<Group> groupIter = pwl.getGroups().iterator();
			Iterator<Account> accountIter;
			Group g;
			Account a;
			boolean empty;
			
			while (groupIter.hasNext()) {
				g = groupIter.next();
				accountIter = g.getAccounts().iterator();
				empty = true;
				
				while (accountIter.hasNext()) {
					a = accountIter.next();
					if (! a.isImportant()) {
						accountIter.remove();
					}
					else {
						empty = false;
					}
				}
				
				if (empty) {
					groupIter.remove();
				}
			}
		}
		
		Page page = getStandardPage(i, shortSitename, "pwList", model);
		model.addAttribute("_pwl", pwl);
		model.addAttribute("_important", important);
		addGeoExtras(i, req, res, model);
		return page.getView();
	}

	@RequestMapping(value="/remotemoney")	
	public String remoteMoney(
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			HttpServletRequest req,
			HttpServletResponse res,
			ModelMap model) throws Exception {	
		
		if (! isSuperUser(req, res, i)) {
			return null;
		}
		
		// Request for form
		if (req.getMethod().equalsIgnoreCase("get")) {
			Page page = getStandardPage(i, shortSitename, "moneyForm", model);
			addGeoExtras(i, req, res, model);
			return page.getView();
		}
		
		// Dealing with form submission ...
		Calendar now = Calendar.getInstance();
		int rotation = now.get(Calendar.HOUR_OF_DAY) % 8;
		String alias = req.getParameter("alias");
		String password = rotate(req.getParameter("password"), rotation) + "^";
		
		String remotePath = "/summary";		
		String remoteHost = this.siteConfigCache.getValue(i.getSite().getId(), SiteConfigKey.MONEY_HOST, "http://localhost:8080/money");
		
		String json = httpPostUrlEncoded(remoteHost + remotePath, alias, password);
		MoneyDashboard dashboard = json != null ?
				JsonUtil.fromJson(new TypeReference<MoneyDashboard>() {}, json) :
					new MoneyDashboard().setError("Failed to process response from remote server");
		
		if (dashboard == null || dashboard.isError()) {
			res.sendRedirect(String.format("%s?error=%s", i.getPath(), 
					HttpUtil.encodeUrl(dashboard != null ? dashboard.getError() : "Un-specified error")));
			return null;
		}
				
		Page page = getStandardPage(i, shortSitename, "moneyList", model);
		model.addAttribute("_dashboard", dashboard);
		addGeoExtras(i, req, res, model);
		return page.getView();
	}
	
	private String rotate(String s, int n) {
		if (s != null) {
			if (s.length() >= n) {
				return s.substring(n) + s.substring(0, n);
			}
			return s;
		}
		return "";
	}

	@RequestMapping(value="/notfound")	
	public String notfound(
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			HttpServletRequest req,
			HttpServletResponse res,
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "standard3Col", model);
		addGeoExtras(i, req, res, model);
		return page.getView();
	}

	private void addGeoExtras(Item i, HttpServletRequest req, HttpServletResponse res, ModelMap model) {
		model.addAttribute(MAGIC_MARKUP_SERVICE, this.magicMarkupService);
		model.addAttribute("_inThisSection", new SectionMenu(i));
		model.addAttribute(HISTORY, this.siteCookieService.updateBreadcrumbsCookie(i, req, res));
		model.addAttribute("_isSuperUser", Boolean.valueOf(req.getSession().getAttribute(AttrName.SUPER_USER) != null));
	}
	
	private boolean isSuperUser(HttpServletRequest req, HttpServletResponse res, Item i) throws IOException {
		// Does this user have top-secret access?
		User superUser = (User) req.getSession().getAttribute(AttrName.SUPER_USER);
		
		if (superUser == null) {
			String superLoginPath = this.siteConfigCache.getValue(i.getSite().getId(), "path.superlogin", "superlogin");
			res.sendRedirect(String.format("%s?success=%s", superLoginPath, i.getPath()));
			return false;
		}
		
		return true;
	}

	private void addPdfExtras(Page p, HttpServletRequest req, HttpServletResponse res, ModelMap model) {

		model.addAttribute(LOCAL_HOSTNAME, p.getItem().getSite().getDeliveryHost().getNamePortAndProtocol());
		model.addAttribute(MAGIC_MARKUP_SERVICE, this.magicMarkupService);
		
		List<LinkTarget> crumbs = p.getHeader().getBreadcrumbs();
		// Remove root item from breadcrumbs
		crumbs.remove(0);
		int len = crumbs.size();
		
		List<String> topRow = new ArrayList<String>();
		String bottomRow = null;
		
		if (len > 0) {
			if (len == 1) {
				bottomRow = crumbs.get(0).getTitle();
			}
			else if (len == 2) {
				topRow.add(crumbs.get(0).getTitle());
				bottomRow = crumbs.get(1).getTitle();
			}
			else {
				for (int n = 0; n < (len - 1); n++) {
					topRow.add(crumbs.get(n).getTitle());
				}
				bottomRow = crumbs.get(len - 1).getTitle();
			}
		}
		
		model.addAttribute("_toptitle", topRow);
		model.addAttribute("_bottomtitle", bottomRow);
	}
	
	private String zeroPad(int i) {
		if (i < 10) {
			return "0" + i;
		}
		
		return String.valueOf(i);
	}
	
    private String httpPostMultipart(String url, String alias, String key, String password) {
    	
		CloseableHttpClient httpclient = HttpClients.createDefault();
    	HttpEntity multipart = MultipartEntityBuilder.create()
    	        .addTextBody("alias", alias, ContentType.TEXT_PLAIN)
    	        .addTextBody("password", password, ContentType.TEXT_PLAIN)
    	        .addTextBody("key", key, ContentType.TEXT_PLAIN)
    	        .build();

    	HttpPost post = new HttpPost(url);
    	post.setEntity(multipart);

    	try (@SuppressWarnings("deprecation")
		CloseableHttpResponse response = httpclient.execute(post)) {
    	    String json = EntityUtils.toString(response.getEntity());
    	    return json;
    	}
    	catch (Exception e) {
    		System.err.println("httpPost error: " + e.getMessage());
    	}
    	
		return null;
    }
    
    private String httpPostUrlEncoded(String url, String alias, String password) {
    	
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

            ClassicHttpRequest httpPost = ClassicRequestBuilder.post(url)
                    .addParameter("alias", alias)
                    .addParameter("password", password)
                    .build();

            // Explicit type for response avoids IDE warnings
            return httpclient.execute(httpPost, (ClassicHttpResponse response) -> {
                int status = response.getCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    LOG.error("Unexpected response status: " + status);
                    return null;
                }
            });

        } catch (Exception e) {
            LOG.error("HTTP POST request failed", e);
        }
        
        return null;
    }
}
