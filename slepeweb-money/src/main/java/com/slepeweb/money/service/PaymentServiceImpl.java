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
					"pt.entered, pt.memo, pt.reference, pt.charge, pt.reconciled, pt.transfer " +
			"from payment pt " +
					"join account a on a.id = pt.accountid " + 
					"join payee pe on pe.id = pt.payeeid " +
					"join category c on c.id = pt.categoryid ";
	
	public Payment save(Payment pt) throws MissingDataException, DuplicateItemException {
		if (pt.isDefined4Insert()) {
			Payment dbRecord = get(pt);		
			if (dbRecord != null) {
				update(dbRecord, pt);
				return dbRecord;
			}
			else {
				insert(pt);
			}
		}
		else {
			String t = "Payment not saved - insufficient data";
			LOG.error(compose(t, pt));
			throw new MissingDataException(t);
		}
		
		return pt;
	}
	
	private Payment insert(Payment p) throws MissingDataException, DuplicateItemException {
		
		try {
			this.jdbcTemplate.update(
					"insert into payment (accountid, payeeid, categoryid, entered, charge, reconciled, transfer, reference, memo) " +
					"values (?, ?, ?, ?, ?, ?, ?, ?, ?)", 
					p.getAccount().getId(), p.getPayee().getId(), p.getCategory().getId(), p.getEntered(), p.getCharge(),
					p.isReconciled(), p.isTransfer(), p.getReference(), p.getMemo());
			
			p.setId(getLastInsertId());	
			LOG.info(compose("Added new payment", p));		
			return p;
		}
		catch (DuplicateKeyException e) {
			throw new DuplicateItemException("Payment already inserted");
		}
	}

	private void update(Payment dbRecord, Payment p) {
		if (! dbRecord.equals(p)) {
			dbRecord.assimilate(p);
			
			try {
			this.jdbcTemplate.update(
					"update payment set charge = ?, reconciled = ?, transfer = ?, memo = ? " +
					"where entered = ? and accountid = ? and payeeid = ? and categoryid = ? and reference = ?", 
					dbRecord.getCharge(), dbRecord.isReconciled(), dbRecord.isTransfer(), dbRecord.getMemo(),
					dbRecord.getEntered(), dbRecord.getAccount().getId(), dbRecord.getPayee().getId(),
					dbRecord.getCategory().getId(), dbRecord.getReference());
			
			LOG.info(compose("Updated payment", p));
			}
			catch (DuplicateKeyException e) {
				LOG.error("Set breakpoint here");
			}
		}
		else {
			LOG.info(compose("Payment not modified", p));
		}
	}

	public Payment get(Payment bones) {
		return (Payment) getFirstInList(this.jdbcTemplate.query(
				SELECT + "where pt.entered = ? and pt.accountid = ? and pt.payeeid = ? and pt.categoryid = ? and pt.reference = ?", 
				new Object[]{
						bones.getEntered(), bones.getAccount().getId(), bones.getPayee().getId(),
						bones.getCategory().getId(), bones.getReference()}, 
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
