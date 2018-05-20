package com.slepeweb.money.service;

import java.io.BufferedReader;

import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.Category;
import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.bean.Payment;

public interface MoneyImportService {
	Account identifyAccount(String account);
	Payee identifyNoPayee();
	Category identifyNoCategory();
	Payment createPayment(Account a, Payee p, Category c, BufferedReader inf);
	Payment savePayment(Payment pt);
	Payment savePartPayments(Payment pt);
	Account resetAccountBalance(Account a);
}
