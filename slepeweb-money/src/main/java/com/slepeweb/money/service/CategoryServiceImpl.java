package com.slepeweb.money.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.slepeweb.money.bean.Category;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;

@Service("categoryService")
public class CategoryServiceImpl extends BaseServiceImpl implements CategoryService {
	
	private static Logger LOG = Logger.getLogger(CategoryServiceImpl.class);
	
	public Category save(Category c) throws MissingDataException, DuplicateItemException {
		if (c.isDefined4Insert()) {
			Category dbRecord = get(c.getMajor(), c.getMinor());		
			if (dbRecord != null) {
				update(dbRecord, c);
				return dbRecord;
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
					"insert into category (major, minor) values (?, ?)", 
					c.getMajor(), c.getMinor());
			
			c.setId(getLastInsertId());	
			
			LOG.info(compose("Added new category", c));		
			return c;
		}
		catch (DuplicateKeyException e) {
			throw new DuplicateItemException("Category already inserted");
		}
	}

	private void update(Category dbRecord, Category c) {
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
	}

	public Category get(String major, String minor) {
		return get("select * from category where major = ? and minor = ?", new Object[]{major, minor});
	}

	public Category get(long id) {
		return get("select * from category where id = ?", new Object[]{id});
	}
	
	private Category get(String sql, Object[] params) {
		return (Category) getFirstInList(this.jdbcTemplate.query(
			sql, params, new RowMapperUtil.CategoryMapper()));
	}

	public List<Category> getAll() {
		return this.jdbcTemplate.query(
			"select * from category order by major, minor", new RowMapperUtil.CategoryMapper());
	}
}
