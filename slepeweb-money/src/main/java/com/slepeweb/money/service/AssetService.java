package com.slepeweb.money.service;

import java.sql.Timestamp;
import java.util.List;

import com.slepeweb.money.bean.NakedTransaction;


public interface AssetService {
	List<NakedTransaction> get(Timestamp from, Timestamp to);
}