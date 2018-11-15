package com.slepeweb.money.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;

@Service("payeeService")
public class PayeeServiceImpl extends BaseServiceImpl implements PayeeService {
	
	private static Logger LOG = Logger.getLogger(PayeeServiceImpl.class);
	
	public Payee save(Payee pe) throws MissingDataException, DuplicateItemException {
		if (pe.isDefined4Insert()) {
			Payee dbRecord = get(pe.getName());		
			if (dbRecord != null) {
				update(dbRecord, pe);
				return dbRecord;
			}
			else {
				insert(pe);
			}
		}
		else {
			String t = "Payee not saved - insufficient data";
			LOG.error(compose(t, pe));
			throw new MissingDataException(t);
		}
		
		return pe;
	}
	
	private Payee insert(Payee pe) throws MissingDataException, DuplicateItemException {
		
		try {
			this.jdbcTemplate.update(
					"insert into payee (name) values (?)", 
					pe.getName());
			
			pe.setId(getLastInsertId());	
			
			LOG.info(compose("Added new payee", pe));		
			return pe;
		}
		catch (DuplicateKeyException e) {
			throw new DuplicateItemException("Payee already inserted");
		}
	}

	private void update(Payee dbRecord, Payee pe) {
		if (! dbRecord.equals(pe)) {
			dbRecord.assimilate(pe);
			
			this.jdbcTemplate.update(
					"update payee set name = ? where id = ?", 
					dbRecord.getName(), dbRecord.getId());
			
			LOG.info(compose("Updated payee", pe));
		}
		else {
			LOG.debug(compose("Payee not modified", pe));
		}
	}

	public Payee get(String name) {
		return get("select * from payee where name = ?", new Object[]{name});
	}

	public Payee get(long id) {
		return get("select * from payee where id = ?", new Object[]{id});
	}
	
	private Payee get(String sql, Object[] params) {
		return (Payee) getFirstInList(this.jdbcTemplate.query(
			sql, params, new RowMapperUtil.PayeeMapper()));
	}

	public List<Payee> getAll() {
		return this.jdbcTemplate.query(
			"select * from payee order by name", new RowMapperUtil.PayeeMapper());
	}
}
