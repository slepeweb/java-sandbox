package com.slepeweb.sandbox.spizza;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slepeweb.sandbox.spizza.bean.Customer;
import com.slepeweb.sandbox.spizza.bean.Order;
import com.slepeweb.sandbox.spizza.bean.Payment;
import com.slepeweb.sandbox.spizza.bean.PaymentDetails;
import com.slepeweb.sandbox.www.model.Page;
import com.slepeweb.sandbox.www.service.NavigationService;
import com.slepeweb.sandbox.www.service.PageService;

@Component("spizzaFlowActions")
public class SpizzaFlowActions {
	private static Logger LOG = Logger.getLogger(SpizzaFlowActions.class);
	
	@Autowired
	private PageService pageService;
	
	@Autowired
	private NavigationService navigationService;
	
	public void lookupCustomer(String phone) throws Exception {
		if (! phone.equals("123")) {
			throw new Exception("Custmer not recognized");
		}	
	}
	
	public boolean checkDeliveryArea(String postcode) {
		return postcode.equalsIgnoreCase("pe27");
	}
	
	public void addCustomer(Customer c) {
		LOG.info(String.format("Customer saved [%s]", c.getName()));
	}
	
	public Payment verifyPayment(PaymentDetails details) throws Exception {
		LOG.info("Verifying payment ...");
			return new Payment();
	}
	
	public boolean saveOrder(Order o) {
		LOG.info("Order saved.");
		return true;
	}
	
	public Page getPage() {
		Page page = this.pageService.getPage(PageService.SPIZZA);
		page.setTopNavigation(this.navigationService.getTopNavigation(page, null));
		page.getLeftSidebar().setNavigation(this.navigationService.getSandboxNavigation(page));
		return page;
	}
}
