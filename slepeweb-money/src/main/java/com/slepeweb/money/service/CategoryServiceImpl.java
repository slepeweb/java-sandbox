package com.slepeweb.money.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.slepeweb.money.bean.Category;
import com.slepeweb.money.except.DataInconsistencyException;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;

@Service("categoryService")
public class CategoryServiceImpl extends BaseServiceImpl implements CategoryService {
	
	private static Logger LOG = Logger.getLogger(CategoryServiceImpl.class);
	@Autowired private SolrService solrService;
	
	public Category save(Category c) throws MissingDataException, DuplicateItemException, DataInconsistencyException {
		if (c.isDefined4Insert()) {
			if (c.isInDatabase()) {
				Category dbRecord = get(c.getId());		
				if (dbRecord != null) {
					update(dbRecord, c);
					return dbRecord;
				}
				else {
					throw new DataInconsistencyException(error(LOG, "Categroy does not exist in DB", c));
				}
			}
			else {
				insert(c);
			}
		}
		else {
			String t = "Category not saved - insufficient data";
			LOG.error(compose(t, c));
			throw new MissingDataException(t);
		}
		
		return c;
	}
	
	private Category insert(Category c) throws MissingDataException, DuplicateItemException {
		
		try {
			this.jdbcTemplate.update(
					"insert into category (origid, major, minor) values (?, ?, ?)", 
					c.getOrigId(), c.getMajor(), c.getMinor());
			
			c.setId(getLastInsertId());	
			
			LOG.info(compose("Added new category", c));		
			return c;
		}
		catch (DuplicateKeyException e) {
			throw new DuplicateItemException("Category already inserted");
		}
	}

	public Category update(Category dbRecord, Category c) {
		if (! dbRecord.equals(c)) {
			dbRecord.assimilate(c);
			
			this.jdbcTemplate.update(
					"update category set major = ?, minor = ? where id = ?", 
					dbRecord.getMajor(), dbRecord.getMinor(), dbRecord.getId());
			
			LOG.info(compose("Updated category", c));
		}
		else {
			LOG.debug(compose("Category not modified", c));
		}
		
		return dbRecord;
	}

	public Category get(String major, String minor) {
		// Form submission will offer a null value for minor if <select> hasn't been populated.
		if (minor == null) {
			minor = "";
		}
		
		Category c = get("select * from category where major = ? and minor = ?", new Object[]{major, minor});
		if (c != null) {
			return c;
		}
		else if (StringUtils.isNotBlank(major)) {		
			return getNoCategory();
		}
		
		return null;
	}
	
	public Category getNoCategory() {
		return get("", "");
	}

	public Category get(long id) {
		return get("select * from category where id = ?", new Object[]{id});
	}
	
	public Category getByOrigId(long id) {
		return get("select * from category where origid = ?", new Object[]{id});
	}
	
	private Category get(String sql, Object[] params) {
		return (Category) getFirstInList(this.jdbcTemplate.query(
			sql, params, new RowMapperUtil.CategoryMapper()));
	}

	public List<Category> getAll() {
		return this.jdbcTemplate.query(
			"select * from category order by major, minor", new RowMapperUtil.CategoryMapper());
	}
	
	public List<String> getAllMajorValues() {
		return this.jdbcTemplate.queryForList(
			"select distinct major from category order by major", java.lang.String.class);
	}

	public List<String> getAllMinorValues(String major) {
		return this.jdbcTemplate.queryForList(
			"select distinct minor from category where major = ? order by minor", new Object[]{major}, 
				java.lang.String.class);
	}

	public int delete(long id) {
		Category c = get(id);
		int num = this.jdbcTemplate.update("delete from category where id = ?", id);
		this.solrService.removeTransactionsByCategory(c.getMajor(), c.getMinor());
		return num;
	}	
}
