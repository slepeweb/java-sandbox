package com.slepeweb.site.sws.spizza;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;
import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.service.CmsService;
import com.slepeweb.site.constant.FieldName;
import com.slepeweb.site.model.Page;
import com.slepeweb.site.sws.spizza.bean.Customer;
import com.slepeweb.site.sws.spizza.bean.LoginForm;
import com.slepeweb.site.sws.spizza.bean.Order;
import com.slepeweb.site.sws.spizza.bean.Payment;
import com.slepeweb.site.sws.spizza.bean.Payment.CardType;
import com.slepeweb.site.sws.spizza.bean.PaymentForm;
import com.slepeweb.site.sws.spizza.bean.Pizza;
import com.slepeweb.site.sws.spizza.bean.PizzaFactory;
import com.slepeweb.site.sws.spizza.bean.PizzaForm;

@Component("spizzaFlowActions")
public class SpizzaFlowActions extends MultiAction {
	private static Logger LOG = Logger.getLogger(SpizzaFlowActions.class);
	private static Pattern DATA_PATTERN = Pattern.compile("^\\[(\\w+)\\]\\:(.*)$", Pattern.DOTALL | Pattern.MULTILINE);
	
	@Autowired private CmsService cmsService;	
	
	/*
	 * This pulls the Page object from the native request. The main flow definition calls this method,
	 * and places the result in conversationScope (ie. available throughout the main flow).
	 * Tag <sw:spizzaLayout> assigns the _page attribute to the object in conversationScope, thereby
	 * allowing the tags like <sw:head>, <sw:navigation-left> and <sw:navigation-top> to do their jobs.
	 */
	public Page getPageFromRequest(RequestContext ctx) {
		HttpServletRequest req = (HttpServletRequest) ctx.getExternalContext().getNativeRequest();
	    Page p = (Page) req.getAttribute("_page");
	    
	    /* 
	     * When the main flow reaches the end-state, it seems to re-call the first page in the flow,
	     * using the /webflow url, and NOT the /sandbox url. This means that the CmsDeliveryServlet
	     * doesn't get a chance to put the _page attribute into the native request. So, we have to do
	     * that here ...
	     */
	    if (p == null) {
	    	Site s = this.cmsService.getSiteService().getSite("Slepeweb");
	    	Item i = s.getItem("/sandbox/spizza");
			p = new Page().
					setTitle(i.getFieldValue("title")).
					setHeading(i.getFieldValue("title")).
					setBody(i.getFieldValue("bodytext", "")).
					setItem(i);
			
			p.setLeftNavigation();
	    }
	    
    	p.getItem().setCmsService(this.cmsService);
	    return p;
	}
	
	public Map<String, String> parseMainContent(RequestContext ctx) {
		Map<String, String> map = parseContent(FieldName.BODYTEXT, ctx);		
		String replacementHref = ctx.getFlowExecutionUrl() + "&_eventId=register";
		String replacement;
		
		for (Map.Entry<String, String> entry : map.entrySet()) {
			replacement = entry.getValue().replaceAll("\\[register\\.href\\]", replacementHref);
			entry.setValue(replacement);
		}
				
		return map;
	}
	
	public Map<String, String> parseContent(String fieldName, RequestContext ctx) {
		Map<String, String> map = new HashMap<String, String>();
		Page p = (Page) ctx.getConversationScope().get("page");
		String fieldValue = p.getItem().getFieldValue(fieldName, "");
		Matcher m;
		
		for (String part : fieldValue.split("\\|")) {
			m = DATA_PATTERN.matcher(part.trim());
			if (m.matches()) {
				map.put(m.group(1).trim(), m.group(2).trim());
			}
		}
				
		return map;
	}
	
	public Event identifyCustomer(RequestContext ctx) throws Exception {
		LoginForm form = (LoginForm) ctx.getFlowScope().get("loginForm");

		if (form.getEmail().equals("fred@flintstone.com") && form.getPassword().equals("rubble")) {
			// Create a full customer record
			Customer c = (Customer) ctx.getFlowScope().get("customer");
			c.setName("Fred Flintstone");
			c.setAddress("The Qurry, Bedrock, Springfield");
			c.setZipCode("CB2 2DT");
			c.setPhoneNumber("0755 777 0817");
			c.setEmail("fred@flintstone.com");
			return success();
		}
		else {
			MessageContext mctx = ctx.getMessageContext();
			MessageBuilder mb = new MessageBuilder().error().source("email").
					code("loginForm.email.no.such.account").
					defaultText("These account details are not recognised");
			mctx.addMessage(mb.build());
			return error();
		}	
	}
	
	public Event validateRegistration(RequestContext ctx) {
		Customer c = (Customer) ctx.getFlowScope().get("customer");
		if (! c.getPassword().equals(c.getConfirmPassword())) {
			MessageContext mctx = ctx.getMessageContext();
			MessageBuilder mb = new MessageBuilder().error().source("password").
					code("customer.password.nomatch");
			mctx.addMessage(mb.build());
			return error();
		}
		
		return success();
	}
	
	public Event checkDeliveryArea(RequestContext ctx) {
		Customer c = (Customer) ctx.getFlowScope().get("customer");
		if (c.getZipCode().toLowerCase().startsWith("pe27")) {
			LOG.info(String.format("We do deliver to this area [%s]", c.getZipCode()));
			return success();
		}
		else {
			LOG.info(String.format("We do NOT deliver to this area [%s]", c.getZipCode()));
			return error();
		}
	}
	
	public Event addCustomer(RequestContext ctx) {		
		Customer c = (Customer) ctx.getFlowScope().get("customer");
		LOG.info(String.format("Customer saved [%s]", c.getName()));
		return success();
	}
	
	public Event verifyPayment(RequestContext ctx) throws Exception {
		LOG.info("Verifying payment ...");
		return success();
	}
	
	public void updateOrderWithPayment(Order o, PaymentForm form) throws Exception {
		Payment p = new Payment();
		p.setAccepted(true);
		p.setCardNumber(form.getCardNumber());
		p.setCardOwner(form.getCardOwner());
		p.setCardType(CardType.valueOf(form.getCardType()));
		p.setCcvCode(form.getCcvCode());
		p.setExpiryDate(form.getExpiryDate());
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MINUTE, 30);
		o.setDeliveryEta(String.format("%1$tH:%1$tM", c.getTime()));
		o.setPayment(p);
	}
	
	public Event saveOrder(RequestContext ctx) {
		LOG.info("Order saved.");
		return success();
	}
	
	public Event removePizza(RequestContext ctx) {
		Order o = (Order) ctx.getFlowScope().get("order");
		String idStr = ctx.getExternalContext().getRequestParameterMap().get("id");

		if (StringUtils.isNumeric(idStr)) {
			int id = Integer.parseInt(idStr) - 1;
			
			if (id < o.getPizzas().size()) {
				Pizza p = o.getPizzas().remove(id);
				LOG.info(String.format("Pizza removed [%s]", p));
				return success();
			}
		}
		
		return error();
	}
	
	public Order newOrder() {
		return Order.getInstance();
	}
	
	public PizzaForm initPizzaForm() {
		PizzaForm p = new PizzaForm();
		p.setBase(PizzaFactory.Base.Margherita.name());
		p.setSize(PizzaFactory.Size.Large.name());
		return p;
	}
	
	public PaymentForm initPaymentForm() {
		PaymentForm p = new PaymentForm();
		p.setCardNumber("4111111111111111");
		p.setCardType("Visa");
		return p;
	}
}
