package com.slepeweb.money.service;

import java.util.List;

import com.slepeweb.money.bean.PartPayment;
import com.slepeweb.money.bean.Payment;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;


public interface PartPaymentService {
	List<PartPayment> get(Payment pt);
	Payment save(Payment pt) throws MissingDataException, DuplicateItemException;
	Payment delete(Payment pt);
}
