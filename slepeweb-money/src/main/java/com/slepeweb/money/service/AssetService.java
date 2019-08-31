package com.slepeweb.money.service;

import java.sql.Timestamp;
import java.util.List;

import com.slepeweb.money.bean.Transaction;


public interface AssetService {
	List<Transaction> get(Timestamp from, Timestamp to);
}