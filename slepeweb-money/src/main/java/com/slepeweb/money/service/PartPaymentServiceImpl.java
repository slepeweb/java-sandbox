package com.slepeweb.money.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.slepeweb.money.bean.PartPayment;
import com.slepeweb.money.bean.Payment;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;

@Service("partPaymentService")
public class PartPaymentServiceImpl extends BaseServiceImpl implements PartPaymentService {
	
	private static Logger LOG = Logger.getLogger(PartPaymentServiceImpl.class);
	private static final String SELECT = 
			"select " +
					"ppt.paymentid, ppt.charge, ppt.memo" + 
					"c.id as categoryid, c.major, c.minor, " + 
			"from partpayment ppt " +
					"join category c on c.id = ppt.categoryid ";
	
	public Payment save(Payment pt) throws MissingDataException, DuplicateItemException {
		if (pt.isSplit()) {
			List<PartPayment> revisedList = pt.getPartPayments();
			
			// Delete existing part payments
			pt = delete(pt);
			
			// Insert latest part-payments
			for (PartPayment ppt : revisedList) {
				if (ppt.isDefined4Insert()) {
					insert(ppt);
				}
				else {
					String t = "Part-payments not saved - insufficient data";
					LOG.error(compose(t, pt));
					throw new MissingDataException(t);
				}
			}
		}
		
		return pt;
	}
	
	private PartPayment insert(PartPayment ppt) throws MissingDataException, DuplicateItemException {
		
		try {
			this.jdbcTemplate.update(
					"insert into partpayment (paymentid, categoryid, charge, memo) " +
					"values (?, ?, ?, ?)", 
					ppt.getPaymentId(), ppt.getCategory().getId(), ppt.getCharge(), ppt.getMemo());
			
			LOG.info(compose("Added new part-payment", ppt));		
			return ppt;
		}
		catch (DuplicateKeyException e) {
			throw new DuplicateItemException("Part-payment already inserted");
		}
	}

	public List<PartPayment> get(Payment pt) {
		return this.jdbcTemplate.query(
				SELECT + " where ppt.paymentid = ?", 
				new Object[]{pt.getId()}, 
				new RowMapperUtil.PartPaymentMapper());
	}

	public Payment delete(Payment pt) {
		if (this.jdbcTemplate.update("delete from partpayment where paymentid = ?", pt.getId()) > 0) {
			LOG.warn(compose("Deleted part-payments", pt.getId()));
			pt.getPartPayments().clear();
		}
		return pt;
	}	
}
