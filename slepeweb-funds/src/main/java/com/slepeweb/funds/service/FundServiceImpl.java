package com.slepeweb.funds.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.slepeweb.funds.bean.Fund;
import com.slepeweb.funds.except.DuplicateItemException;
import com.slepeweb.funds.except.MissingDataException;

@Service("fundService")
public class FundServiceImpl extends BaseServiceImpl implements FundService {
	
	private static Logger LOG = Logger.getLogger(FundServiceImpl.class);
	
	public Fund save(Fund f) throws MissingDataException, DuplicateItemException {
		if (f.isDefined4Insert()) {
			Fund dbRecord = getFund(f.getName());		
			if (dbRecord != null) {
				updateFund(dbRecord, f);
				return dbRecord;
			}
			else {
				insertFund(f);
			}
		}
		else {
			String t = "Fund not saved - insufficient data";
			LOG.error(compose(t, f));
			throw new MissingDataException(t);
		}
		
		return f;
	}
	
	private Fund insertFund(Fund f) throws MissingDataException, DuplicateItemException {
		
		try {
			this.jdbcTemplate.update(
					"insert into fund (name, alias, units) values (?, ?, ?)", 
					f.getName(), f.getAlias(), f.getUnits());
			
			f.setId(getLastInsertId());	
			
			LOG.info(compose("Added new fund", f));		
			return f;
		}
		catch (DuplicateKeyException e) {
			throw new DuplicateItemException("Fund already inserted");
		}
	}

	private void updateFund(Fund dbRecord, Fund f) {
		if (! dbRecord.equals(f)) {
			dbRecord.assimilate(f);
			
			this.jdbcTemplate.update(
					"update fund set name = ?, alias = ?, units = ? where id = ?", 
					dbRecord.getName(), dbRecord.getAlias(), dbRecord.getUnits(), dbRecord.getId());
			
			LOG.info(compose("Updated fund", f));
		}
		else {
			LOG.info(compose("Fund not modified", f));
		}
	}

	public Fund getFund(String name) {
		return getFund("select * from fund where name = ?", new Object[]{name});
	}

	public Fund getFund(long id) {
		return getFund("select * from fund where id = ?", new Object[]{id});
	}
	
	private Fund getFund(String sql, Object[] params) {
		return (Fund) getFirstInList(this.jdbcTemplate.query(
			sql, params, new RowMapperUtil.FundMapper()));
	}

	public List<Fund> getAllFunds() {
		return this.jdbcTemplate.query(
			"select * from fund order by ordering", new RowMapperUtil.FundMapper());
	}
}
