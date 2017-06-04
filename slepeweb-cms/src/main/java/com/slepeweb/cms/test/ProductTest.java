package com.slepeweb.cms.test;

import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.service.TagService;

@Service
public class ProductTest extends BaseTest {
	
	private static Logger LOG = Logger.getLogger(ProductTest.class);
	
	@Autowired TagService itemService;
		
	public TestResultSet execute() {
		
		TestResult r;
		TestResultSet trs = new TestResultSet("Item testbed").
				register(9010, "Create a product in the storefront");
		
		
		try {
			Site site = getTestSite();
			
			if (site == null) {
				LOG.warn("Failed to retrieve test site");
				return trs;
			}
			
			Item storefrontItem = site.getItem("/storefront");
			
			if (storefrontItem == null) {
				LOG.warn("Failed to retrieve storefront item");
				return trs;
			}
			
			Timestamp now = new Timestamp(System.currentTimeMillis());
			ItemType productType = this.cmsService.getItemTypeService().getItemType(PRODUCT_TYPE_NAME);
			if (productType == null) {
				LOG.warn("Failed to retrieve Product type");
				return trs;
			}
			
			r = trs.execute(9010);
			addItem(storefrontItem, "Product A", "product-a", now, now, site, productType, null);
			Item i = site.getItem("/storefront/product-a");
			r.failIf(i == null);
		}
		catch (Exception e) {
			LOG.error("Unexpected exception", e);
		}
				
		return trs;
	}
}
