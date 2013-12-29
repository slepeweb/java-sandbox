package com.slepeweb.sandbox.spizza;

import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;
import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import com.slepeweb.sandbox.spizza.bean.Customer;
import com.slepeweb.sandbox.spizza.bean.LoginForm;
import com.slepeweb.sandbox.spizza.bean.Order;
import com.slepeweb.sandbox.spizza.bean.Payment;
import com.slepeweb.sandbox.spizza.bean.PaymentForm;
import com.slepeweb.sandbox.spizza.bean.Pizza;
import com.slepeweb.sandbox.spizza.bean.PizzaFactory;
import com.slepeweb.sandbox.spizza.bean.PizzaForm;
import com.slepeweb.sandbox.spizza.bean.Payment.CardType;
import com.slepeweb.sandbox.www.model.Page;
import com.slepeweb.sandbox.www.service.NavigationService;
import com.slepeweb.sandbox.www.service.PageService;

@Component("spizzaFlowActions")
public class SpizzaFlowActions extends MultiAction {
	private static Logger LOG = Logger.getLogger(SpizzaFlowActions.class);
	
	@Autowired
	private PageService pageService;
	
	@Autowired
	private NavigationService navigationService;
	
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
	
	public Page getPage() {
		Page page = this.pageService.getPage(PageService.SPIZZA);
		page.setTopNavigation(this.navigationService.getTopNavigation(page, null));
		page.getLeftSidebar().setNavigation(this.navigationService.getSandboxNavigation(page));
		return page;
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
