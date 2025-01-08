package com.slepeweb.money.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.except.DataInconsistencyException;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;

@Service("payeeService")
public class PayeeServiceImpl extends BaseServiceImpl implements PayeeService {
	
	private static Logger LOG = Logger.getLogger(PayeeServiceImpl.class);
	
	@Autowired private TransactionService transactionService;
	@Autowired private SolrService4Money solrService4Money;
	
	public Payee save(Payee pe) throws MissingDataException, DuplicateItemException, DataInconsistencyException {
		if (pe.isDefined4Insert()) {
			if (pe.isInDatabase()) {
				Payee dbRecord = get(pe.getId());	
				if (dbRecord != null) {
					update(dbRecord, pe);
					return dbRecord;
				}
				else {
					throw new DataInconsistencyException(error(LOG, "Payee does not exist in DB", pe));
				}
			}
			else {
				return insert(pe);
			}
		}
		else {
			throw new MissingDataException(error(LOG, "Payee not saved - insufficient data", pe));
		}
	}
	
	private Payee insert(Payee pe) throws MissingDataException, DuplicateItemException {
		
		try {
			this.jdbcTemplate.update(
					"insert into payee (origid, name) values (?, ?)", 
					pe.getOrigId(), pe.getName());
			
			pe.setId(getLastInsertId());	
			
			LOG.info(compose("Added new payee", pe));		
			return pe;
		}
		catch (DuplicateKeyException e) {
			throw new DuplicateItemException("Payee already inserted");
		}
	}

	public Payee update(Payee dbRecord, Payee pe) {
		if (! dbRecord.equals(pe)) {
			dbRecord.assimilate(pe);
			
			this.jdbcTemplate.update(
					"update payee set name = ? where id = ?", 
					dbRecord.getName(), dbRecord.getId());
			
			// Update transaction documents in solr, which store payee name, NOT id.
			this.solrService4Money.save(this.transactionService.getTransactionsForPayee(dbRecord.getId()));
			
			LOG.info(compose("Updated payee", pe));
		}
		else {
			LOG.debug(compose("Payee not modified", pe));
		}
		
		return dbRecord;
	}

	public Payee get(String name) {
		Payee p = get("select * from payee where name = ?", new Object[]{name});
		if (p != null) {
			return p;
		}
		else if (StringUtils.isNotBlank(name)) {		
			return getNoPayee();
		}
		
		return null;
	}
	
	public Payee getNoPayee() {
		return get("");
	}

	public Payee get(long id) {
		return get("select * from payee where id = ?", id);
	}
	
	public Payee getByOrigId(long id) {
		return get("select * from payee where origid = ?", id);
	}
	
	private Payee get(String sql, Object... params) {
		try {
		return this.jdbcTemplate.queryForObject(
			sql, new RowMapperUtil.PayeeMapper(), params);
		}
		catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<Payee> getAll() {
		return this.jdbcTemplate.query(
			"select * from payee order by name", new RowMapperUtil.PayeeMapper());
	}
	
	/*
	 * It's only possible to delete a payee if there are ZERO transactions for the payee.
	 */
	public int delete(long id) {
		if (this.transactionService.getNumTransactionsForPayee(id) > 0) {
			return 0;
		}
		
		return this.jdbcTemplate.update("delete from payee where id = ?", id);
	}	
}
