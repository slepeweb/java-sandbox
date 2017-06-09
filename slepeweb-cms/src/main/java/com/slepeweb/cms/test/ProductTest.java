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
		TestResultSet trs = new TestResultSet("Product testbed").
				register(9010, "Delete Product-A, if it exists", "Should be one less row in Product table").
				register(9020, "Re-create Product-A", "New item should be retrievable").
				register(9030, "", "One more row should be added to Product table").
				register(9040, "", "Item should be an instance of Product");
		
		
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
			String path = "/storefront/product-a";
			long count = this.cmsService.getProductService().count();
			Item product = site.getItem(path);
			if (product != null) {
				product.delete();
				long count2 = this.cmsService.getProductService().count();
				r.failIf((count2 - count) != -1);
				r.setNotes(String.format("Product already existed; table count is now %d",  count2));
			}
			else {
				r.setNotes(String.format("Product does not exist; table count is %d",  count));
			}
			
			r = trs.execute(9020);
			count = this.cmsService.getProductService().count();
			addProduct(storefrontItem, "Product A", "product-a", now, now, site, productType, null, "part-no-123", 50L, 121L, null, null);
			product = site.getItem(path);
			if (product == null) {
				r.fail().setNotes(String.format("Product item couldn't be found", path));
			}
			else {
				r.setNotes(String.format("Product item found [%s]", path));
			
				r = trs.execute(9030);
				long count2 = this.cmsService.getProductService().count();
				r.failIf((count2 - count) != 1);
				r.setNotes(String.format("Table count is now %d",  count2));
	
				r = trs.execute(9040);
				r.failIf(! product.isProduct());
				r.setNotes(String.format("Item %s Product",  product.isProduct() ? "IS" : "is NOT"));
			}
		}
		catch (Exception e) {
			LOG.error("Unexpected exception", e);
		}
				
		return trs;
	}
}
