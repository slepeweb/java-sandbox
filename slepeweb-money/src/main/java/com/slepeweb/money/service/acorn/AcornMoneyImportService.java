package com.slepeweb.money.service.acorn;

import java.io.IOException;

import com.slepeweb.money.bean.Transaction;

public interface AcornMoneyImportService {
	void init() throws IOException;
	Transaction importTransaction();
	Transaction saveTransaction(Transaction pt);
}
