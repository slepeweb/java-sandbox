package com.slepeweb.money.service;

import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.slepeweb.money.bean.Payment;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;

@Service("paymentService")
public class PaymentServiceImpl extends BaseServiceImpl implements PaymentService {
	
	private static Logger LOG = Logger.getLogger(PaymentServiceImpl.class);
	private static final String SELECT = 
			"select " +
					"a.id as accountid, a.name as accountname, " + 
					"pe.id as payeeid, pe.name as payeename, " + 
					"c.id as categoryid, c.major, c.minor, " + 
					"pt.origid, pt.entered, pt.memo, pt.reference, pt.charge, pt.reconciled, " +
					"pt.transferid, at.name as transfername " +
			"from payment pt " +
					"join account a on a.id = pt.accountid " + 
					"left join account at on at.id = pt.transferid " + 
					"join payee pe on pe.id = pt.payeeid " +
					"join category c on c.id = pt.categoryid ";
	
	public Payment save(Payment pt) throws MissingDataException, DuplicateItemException {
		if (pt.isDefined4Insert()) {
			// Insert record, regardless of whether it has already been inserted.
			// (Take care with imports - should check whether already imported first!)
			insert(pt);
		}
		else {
			String t = "Payment not saved - insufficient data";
			LOG.error(compose(t, pt));
			throw new MissingDataException(t);
		}
		
		return pt;
	}
	
	private Payment insert(Payment pt) throws MissingDataException, DuplicateItemException {
		
		try {
			this.jdbcTemplate.update(
					"insert into payment (accountid, payeeid, categoryid, origid, entered, charge, " +
					"reconciled, transferid, reference, memo) " +
					"values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
					pt.getAccount().getId(), pt.getPayee().getId(), pt.getCategory().getId(), 
					pt.getOrigId(), pt.getEntered(), pt.getCharge(),
					pt.isReconciled(), pt.getTransferId(), pt.getReference(), pt.getMemo());
			
			pt.setId(getLastInsertId());	
			LOG.info(compose("Added new payment", pt));		
			return pt;
		}
		catch (DuplicateKeyException e) {
			throw new DuplicateItemException("Payment already inserted");
		}
	}

	public void update(Payment dbRecord, Payment p) {
		if (! dbRecord.equals(p)) {
			dbRecord.assimilate(p);
			
			try {
			this.jdbcTemplate.update(
					"update payment set charge = ?, reconciled = ?, transferid = ?, memo = ? " +
					"where entered = ? and accountid = ? and payeeid = ? and categoryid = ? and reference = ?", 
					dbRecord.getCharge(), dbRecord.isReconciled(), dbRecord.getTransferId(), dbRecord.getMemo(),
					dbRecord.getEntered(), dbRecord.getAccount().getId(), dbRecord.getPayee().getId(),
					dbRecord.getCategory().getId(), dbRecord.getReference());
			
			LOG.info(compose("Updated payment", p));
			}
			catch (DuplicateKeyException e) {
				LOG.error(compose("Duplicate key", p));
			}
		}
		else {
			LOG.info(compose("Payment not modified", p));
		}
	}

	public Payment get(long id) {
		return (Payment) getFirstInList(this.jdbcTemplate.query(
				SELECT + "where pt.id = ?", 
				new Object[]{id}, 
				new RowMapperUtil.PaymentMapper()));
	}

	public Payment getByOrigId(long id) {
		return (Payment) getFirstInList(this.jdbcTemplate.query(
				SELECT + "where pt.origid = ?", 
				new Object[]{id}, 
				new RowMapperUtil.PaymentMapper()));
	}

	public List<Payment> getPaymentsForAccount(long accountId) {
		return this.jdbcTemplate.query(
				SELECT + "where pt.accountid = ? order by pt.entered", 
				new Object[]{accountId}, 
				new RowMapperUtil.PaymentMapper());
	}
	
	public List<Payment> getPaymentsForAccount(long accountId, Timestamp from, Timestamp to) {
		return this.jdbcTemplate.query(
				SELECT + "where pt.accountid = ? and pt.entered >= ? and pt.entered <= ? order by pt.entered", 
				new Object[]{accountId, from, to}, 
				new RowMapperUtil.PaymentMapper());
	}
	
}