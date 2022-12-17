package com.slepeweb.cms.test;

import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.CmsBeanFactory;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.service.TagService;
import com.slepeweb.commerce.bean.Axis;
import com.slepeweb.commerce.bean.AxisValue;
import com.slepeweb.commerce.bean.Product;
import com.slepeweb.commerce.bean.Variant;

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
				register(9040, "", "Item should be an instance of Product").
				register(9050, "Create some variants, on colour", "4 variants should be created").
				register(9060, "Copy a product", "Should be 1 new row in product table").
				register(9070, "", "Should be 4 new rows in variant table").
				register(9080, "Trash the copy", "Should be no change in rows in item table").
				register(9090, "", "... and no change in rows in product table").
				register(9100, "Deleted the trashed item", "Should be one less row in item table").
				register(9110, "", "Should be one less row in product table").
				register(9120, "", "Should be 4 less rows in variant table");		
		
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
			
			// Create an axis
			Axis a = CmsBeanFactory.makeAxis().setShortname("color").setLabel("Colour");
			a = a.save();
			
			// Create values for this axis
			AxisValue av;
			String[] values = new String[] {"Red", "Green", "Blue", "Yellow"};
			int j = 0;
			
			for (String s : values) {
				av = CmsBeanFactory.makeAxisValue().
						setAxisId(a.getId()).
						setValue(s).
						setOrdering(j++);
				av.save();
			}
			
			r = trs.execute(9010);
			String path = "/storefront/product-a";
			long count = this.cmsService.getProductService().count();
			Item productItem = site.getItem(path);
			if (productItem != null) {
				productItem.delete();
				long count2 = this.cmsService.getProductService().count();
				r.failIf((count2 - count) != -1);
				r.setNotes(String.format("Product already existed; table count is now %d",  count2));
			}
			else {
				r.setNotes(String.format("Product does not exist; table count is %d",  count));
			}
			
			r = trs.execute(9020);
			count = this.cmsService.getProductService().count();
			addProduct(storefrontItem, "Product A", "product-a", now, now, site, productType, null, "part-no-123", 50L, 121L, a.getId(), null);
			productItem = site.getItem(path);
			if (productItem == null) {
				r.fail().setNotes(String.format("Product item couldn't be found", path));
			}
			else {
				r.setNotes(String.format("Product item found [%s]", path));
			
				r = trs.execute(9030);
				long count2 = this.cmsService.getProductService().count();
				r.failIf((count2 - count) != 1);
				r.setNotes(String.format("Table count is now %d",  count2));
	
				r = trs.execute(9040);
				r.failIf(! productItem.isProduct());
				r.setNotes(String.format("Item %s Product",  productItem.isProduct() ? "IS" : "is NOT"));
			}
			
			// Create variants for this product
			r = trs.execute(9050);
			count = this.cmsService.getVariantService().count();
			Variant v;
			if (productItem.isProduct()) {
				Product p = (Product) productItem;
				List<AxisValue> avs = this.cmsService.getAxisValueService().getAll(a.getId());
				for (AxisValue avx : avs) {
					v = CmsBeanFactory.makeVariant().
							setAlphaAxisValueId(avx.getId()).
							setOrigItemId(p.getOrigId()).
							setStock(33L).
							setQualifier(p.getPartNum() + "-" + avx.getValue().toLowerCase());
					v.save();
				}
				
				long count2 = this.cmsService.getVariantService().count();
				r.failIf(count2 - count != avs.size()).
					setNotes(String.format("Count has increased by %d", count2 - count));
				
				r = trs.execute(9060);
				long prodCount = this.cmsService.getProductService().count();
				long varCount = this.cmsService.getVariantService().count();
				p.copy();
				r.failIf(this.cmsService.getProductService().count() - prodCount != 1);
				
				r = trs.execute(9070);
				r.failIf(this.cmsService.getVariantService().count() - varCount != avs.size());
				
				long itemCount = this.cmsService.getItemService().getCount(site.getId());
				prodCount = this.cmsService.getProductService().count();
				varCount = this.cmsService.getVariantService().count();
				productItem = site.getItem(path + "-copy-1");
				if (productItem != null && productItem.isProduct()) {
					r = trs.execute(9080);
					p = (Product) productItem;
					p.trash();
					r.failIf(this.cmsService.getItemService().getCount(site.getId()) != itemCount);
					
					r = trs.execute(9090);
					r.failIf(this.cmsService.getProductService().count() != prodCount);
					
					r = trs.execute(9100);
					p.delete();
					r.failIf(this.cmsService.getItemService().getCount(site.getId()) - itemCount != -1);
					
					r = trs.execute(9110);
					r.failIf(this.cmsService.getProductService().count() - prodCount != -1);
					
					r = trs.execute(9120);
					r.failIf(this.cmsService.getVariantService().count() - varCount != -avs.size());
				}
				
			}
			else {
				r.setNotes("Item is NOT a Product");
			}
		}
		catch (Exception e) {
			LOG.error("Unexpected exception", e);
		}
				
		return trs;
	}
}
