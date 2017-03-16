package com.slepeweb.funds.service;

import java.sql.Timestamp;
import java.util.List;

import com.slepeweb.funds.bean.FundPrice;
import com.slepeweb.funds.except.DuplicateItemException;
import com.slepeweb.funds.except.MissingDataException;


public interface FundPriceService {
	FundPrice getFundPrice(long fundId, Timestamp ts);
	List<FundPrice> getPricesForFund(long fundId);
	List<FundPrice> getPricesForDate(Timestamp ts);
	List<FundPrice> getPricesForFund(long fundId, Timestamp from, Timestamp to);
	FundPrice save(FundPrice fp) throws MissingDataException, DuplicateItemException;
	List<Timestamp> getDistinctDates(Timestamp from, Timestamp to);
	List<FundPrice> getFundPrices(Timestamp ts);
	List<FundPrice> getAllPrices(Timestamp from, Timestamp to);
}
