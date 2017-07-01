package com.slepeweb.commerce.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.CmsBeanFactory;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.except.DuplicateItemException;
import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.cms.service.ItemServiceImpl;
import com.slepeweb.commerce.bean.Product;
import com.slepeweb.commerce.bean.Variant;
import com.slepeweb.commerce.util.CommerceRowMapper;

@Repository(value="productService")
public class ProductServiceImpl extends ItemServiceImpl implements ProductService {
	
	private static Logger LOG = Logger.getLogger(ProductServiceImpl.class);
	
	@Autowired ItemService itemService;
	
	public Product save(Product p) throws ResourceException {
		return save(p, false);
	}
	
	public Product save(Product p, boolean extendedSave) throws ResourceException {
		// First save the item data, ie insert/update row in Item
		Item i = super.save(p, extendedSave);
		
		// Now save the insert/update row in Product
		Product dbRecord = get(p.getOrigId());		
		if (dbRecord != null) {
			update(dbRecord, p);
		}
		else {
			insert(p);
		}
		
		return get(i.getOrigId());
	}
	
	/*
	 * This method inserts one row in Product - doesn't need to touch the Item table
	 */
	private void insert(Product p) throws ResourceException {
		try {
			this.jdbcTemplate.update(
					"insert into product (origitemid, partnum, stock, price, alphaaxisid, betaaxisid) " +
					"values (?, ?, ?, ?, ?, ?)",
					p.getOrigId(), p.getPartNum(), p.getStock(), p.getPrice(), p.getAlphaAxisId(), p.getBetaAxisId());				
		}
		catch (DuplicateKeyException e) {
			throw new DuplicateItemException("Product already exists");
		}
		
		LOG.info(compose("Added new product", p));		
	}

	/*
	 * This method updates one row in Product - doesn't need to touch the Item table
	 */
	private void update(Product dbRecord, Product p) {
		if (! dbRecord.equalsProduct(p)) {
			dbRecord.assimilate(p);
			
			this.jdbcTemplate.update(
					"update product set partnum = ?, stock = ?, price = ?, alphaaxisid = ?, betaaxisid = ? where origitemid = ?",
					dbRecord.getPartNum(), dbRecord.getStock(), dbRecord.getPrice(), 
					dbRecord.getAlphaAxisId(), dbRecord.getBetaAxisId(), p.getOrigId());
			
			LOG.info(compose("Updated product", p));
			
		}
		else {
			LOG.info(compose("Product not modified", p));
		}
		
	}
	
	/*
	 * Gets row from Product, and DOES NOT mashes in row from Item
	 */
	public Product get(Long origItemId) {
		return (Product) getLastInList(this.jdbcTemplate.query(
			"select * from product where origitemid = ?", 
			new Object[] {origItemId}, 
			new CommerceRowMapper.ProductMapper()));		
	}
	
	public Product get(String partNum) {
		return (Product) getLastInList(this.jdbcTemplate.query(
			"select * from product where partnum = ?", 
			new Object[] {partNum}, 
			new CommerceRowMapper.ProductMapper()));		
	}
	
	/*
	 * This method deletes one or more rows in Item table, and one row in Product table.
	 */
	@Override
	public void deleteAllVersions(Long origItemId) {
		// First delete row(s) in Item
		super.deleteAllVersions(origItemId);
		
		// Now delete row in Product
		if (this.jdbcTemplate.update("delete from product where origitemid = ?", origItemId) > 0) {
			LOG.warn(compose("Deleted product", String.valueOf(origItemId)));
		}
	}
		
	@SuppressWarnings("deprecation")
	public long count() {
		return this.jdbcTemplate.queryForInt("select count(*) from product");
	}
	
	public Product copy(Product source, String name, String simplename, String partNum, Integer copyId) 
			throws ResourceException {
		
		List<Variant>  sourceVariants = source.getVariants();
		
		// Manipulate source, and use it to save a new copy
		Item i = source;
		i.setName(name).setSimpleName(simplename).setId(-1L);
		source.setPartNum(partNum);
		
		Product p = save(source);
		Variant nv;
		
		// Now copy the original (source) product's variants
		for (Variant v : sourceVariants) {
			nv = CmsBeanFactory.makeVariant();
			nv.assimilate(v);
			nv.setOrigItemId(p.getOrigId()).setQualifier(String.format("%s%s%d", v.getQualifier(), Item.SIMPLENAME_COPY_EXT, copyId)).save();
		}
		
		return p.setVariants(null);
	}
}
