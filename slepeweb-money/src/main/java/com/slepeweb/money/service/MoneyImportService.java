package com.slepeweb.money.service;

import java.io.BufferedReader;

import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.Payment;

public interface MoneyImportService {
	Account identifyAccount(String account);
	Payment createPayment(Account a, BufferedReader inf);
	Payment savePayment(Payment pt);
}
