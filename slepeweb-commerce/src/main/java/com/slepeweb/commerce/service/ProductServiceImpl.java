package com.slepeweb.commerce.service;

import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.except.DuplicateItemException;
import com.slepeweb.cms.except.MissingDataException;
import com.slepeweb.cms.service.BaseServiceImpl;
import com.slepeweb.commerce.bean.Product;
import com.slepeweb.commerce.util.CommerceRowMapper;

@Repository
public class ProductServiceImpl extends BaseServiceImpl implements ProductService {
	
	private static Logger LOG = Logger.getLogger(ProductServiceImpl.class);
	
	public Product save(Product p) throws MissingDataException, DuplicateItemException {
		if (! p.isDefined4Insert()) {
			throw new MissingDataException("Product data not sufficient for db insert");
		}
		
		Product dbRecord = get(p.getOrigItemId());		
		if (dbRecord != null) {
			update(dbRecord, p);
		}
		else {
			insert(p);
		}
		
		return p;
	}
	
	private void insert(Product p) throws MissingDataException, DuplicateItemException {
		try {
			this.jdbcTemplate.update(
					"insert into product (origitemid, partnum, stock, price, alphaid, betaid) " +
					"values (?, ?, ?, ?, ?, ?)",
					p.getOrigItemId(), p.getPartNum(), p.getStock(), p.getPrice(), p.getAlphaAxisId(), p.getBetaAxisId());				
		}
		catch (DuplicateKeyException e) {
			throw new DuplicateItemException("Product already exists");
		}
		
		LOG.info(compose("Added new product", p));		
	}

	private void update(Product dbRecord, Product p) {
		if (! dbRecord.equals(p)) {
			dbRecord.assimilate(p);
			
			this.jdbcTemplate.update(
					"update product set partnum = ?, stock = ?, price = ?, alphaid = ?, betaid = ? where origitemid = ?",
					dbRecord.getPartNum(), dbRecord.getStock(), dbRecord.getPrice(), 
					dbRecord.getAlphaAxisId(), dbRecord.getBetaAxisId(), p.getOrigItemId());
			
			LOG.info(compose("Updated product", p));
			
		}
		else {
			LOG.info(compose("Product not modified", p));
		}
		
	}
	
	public Product get(Long origItemId) {
		return (Product) getLastInList(this.jdbcTemplate.query(
			"select * from product where origitemid = ?", 
			new Object[] {origItemId}, 
			new CommerceRowMapper.ProductMapper()));
	}
	
	public void delete(Long origItemId) {
		if (this.jdbcTemplate.update("delete from product where origitemid = ?", origItemId) > 0) {
			LOG.warn(compose("Deleted product", String.valueOf(origItemId)));
		}
	}
}
