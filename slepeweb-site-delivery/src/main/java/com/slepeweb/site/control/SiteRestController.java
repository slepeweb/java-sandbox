package com.slepeweb.site.control;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.cms.service.SiteService;
import com.slepeweb.cms.service.TagService;
import com.slepeweb.commerce.bean.AxisValue;
import com.slepeweb.commerce.bean.AxisValueSelector;
import com.slepeweb.commerce.bean.Basket;
import com.slepeweb.commerce.bean.OrderItem;
import com.slepeweb.commerce.bean.Product;
import com.slepeweb.commerce.bean.Variant;
import com.slepeweb.commerce.service.ProductService;
import com.slepeweb.commerce.service.VariantService;
import com.slepeweb.site.model.LinkTarget;
import com.slepeweb.site.service.NavigationService;

@Controller
@RequestMapping("/rest")
public class SiteRestController extends BaseController {
	//private static Logger LOG = Logger.getLogger(SiteRestController.class);
	public static final String SLEPEWEB_SITENAME = "Slepeweb";
	public static final String NOCRAWL = "nocrawl";
	private static final String BASKET_COOKIE = "_basket";
	
	@Autowired private SiteService siteService;
	@Autowired private NavigationService navigationService;
	@Autowired private TagService tagService;
	@Autowired private ItemService itemService;
	@Autowired private ProductService productService;
	@Autowired private VariantService variantService;
	
	@RequestMapping(value="/sitemap/sws.txt", method=RequestMethod.GET, produces="text/plain")
	@ResponseBody
	public String swsSitemap() {	
		
		Site s = this.siteService.getSite(SLEPEWEB_SITENAME);
		if (s == null) {
			return "";
		}
		
		// Get all pages on the SWS site
		LinkTarget root = this.navigationService.drillDown(s.getItem("/"), 4, null);
		
		// Put the page URLs into a Set
		Set<String> set = new HashSet<String>(37);
		this.drillDown(root, set);
		
		// Remove the homepage url, since it forwards to /about
		set.remove("/");
		
		// Remove any pages tagged with 'nocrawl'
		for (Item i : this.tagService.getTaggedItems(s.getId(), NOCRAWL)) {
			set.remove(i.getPath());
		}
		
		// Put all remaining urls into a single string
		StringBuilder sb = new StringBuilder();
		String prefix = "http://www.slepeweb.com";
		Iterator<String> iter = set.iterator();
		while (iter.hasNext()) {
			if (sb.length() > 0) {
				sb.append("\n");
			}
			sb.append(prefix).append(iter.next());
		}		

		return sb.toString();		
	}
	
	@RequestMapping(value="/robots/sws.txt", method=RequestMethod.GET, produces="text/plain")
	@ResponseBody
	public String swsRobots() {
		Site s = this.siteService.getSite(SLEPEWEB_SITENAME);
		if (s == null) {
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("Sitemap: http://www.slepeweb.com/rest/sitemap/sws.txt\n");
		sb.append("User-agent: *\n");
		sb.append("Disallow: /proxy\n");
		
		for (Item i : this.tagService.getTaggedItems(s.getId(), NOCRAWL)) {
			sb.append(String.format("Disallow: %s\n", i.getPath()));
		}
		
		return sb.toString();		
	}
	
	private void drillDown(LinkTarget node, Set<String> set) {
		set.add(node.getHref());
		for (LinkTarget lt : node.getChildren()) {
			drillDown(lt, set);
		}
	}
	
	@RequestMapping(value="/product/{itemId}/variants/{alphaValueId}", 
			method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public AxisValueSelector getManyWithStock(@PathVariable long itemId, @PathVariable long alphaValueId, ModelMap model) {	
		List<Variant> variants = this.variantService.getVariantsWithBetaAxis(itemId, alphaValueId);
		AxisValueSelector selector = new AxisValueSelector();
		AxisValueSelector.Option option;
		AxisValue av;
		
		for (Variant v : variants) {
			av = v.getBetaAxisValue();
			option = new AxisValueSelector.Option().
					setBody(av.getValue()).
					setValue(av.getId()).
					setStock(v.getStock());
			
			selector.getOptions().add(option);
		}
		
		return selector;
	}

	@RequestMapping(value="/product/{itemId}/has-hifi", method=RequestMethod.POST, produces="text/plain")
	@ResponseBody
	public String getHifiImagePath(@PathVariable long itemId, @RequestParam String baseImagePath, ModelMap model) {	
		Item product = this.itemService.getItem(itemId);
		if (product != null) {
			Item hifiImage = this.itemService.getItem(product.getSite().getId(), Product.getHifiImagePath(baseImagePath));
			if (hifiImage != null) {
				return hifiImage.getPath();
			}
		}
		return null;
	}
	
	@RequestMapping(value="/product/basket/add/{origItemId}", method=RequestMethod.POST, produces="text/plain")
	@ResponseBody
	public String add2basket(@PathVariable long origItemId, HttpServletRequest req, HttpServletResponse res) {
		String alphaAxisIdStr = req.getParameter("alphavalueid");
		String betaAxisIdStr = req.getParameter("betavalueid");
		
		Cookie c = getBasketCookie(req.getCookies(), BASKET_COOKIE);
		if (c == null) {
			c = new Cookie(BASKET_COOKIE, "");
		}
		
		Basket b = Basket.parseCookieStringValue(c.getValue());
		OrderItem oi = new OrderItem(1, origItemId, null);
		
		if (alphaAxisIdStr == null && betaAxisIdStr == null) {
			// Adding a product
			Product p = this.productService.get(origItemId);
			if (p != null) {
				b.add(oi);
			}
			else {
				return "Product not identified";
			}
		}
		else {
			// Adding a variant
			Long alphaAxisId = StringUtils.isNumeric(alphaAxisIdStr) ? Long.valueOf(alphaAxisIdStr) : -1;
			Long betaAxisId = StringUtils.isNumeric(betaAxisIdStr) ? Long.valueOf(betaAxisIdStr) : -1;
			Variant v = this.variantService.get(origItemId, alphaAxisId, betaAxisId);
			
			if (v != null) {
				oi.setQualifier(v.getQualifier());
				b.add(oi);
			}
			else {
				return "Product variant not identified";
			}
		}
		
		c.setValue(b.formatCookieStringValue());
		c.setMaxAge(3 * 24 * 3600); // 3 days
		c.setPath("/rest/product/");
		res.addCookie(c);
		
		return String.format("Basket contains %d item(s)", b.getSize());
	}
	
	private Cookie getBasketCookie(Cookie[] arr, String name) {
		for (Cookie c : arr) {
			if (c.getName().equals(name)) {
				return c;
			}
		}
		return null;
	}
}
