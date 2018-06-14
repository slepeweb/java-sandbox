package com.slepeweb.money.service;

import java.sql.Timestamp;
import java.util.List;

import com.slepeweb.money.bean.Payment;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;


public interface PaymentService {
	Payment get(long id);
	Payment getByOrigId(long id);
	List<Payment> getPaymentsForAccount(long id);
	List<Payment> getPaymentsForAccount(long id, Timestamp from, Timestamp to);
	Payment save(Payment p) throws MissingDataException, DuplicateItemException;
}
