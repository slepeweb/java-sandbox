package com.slepeweb.money.service;

import java.sql.Date;
import java.util.List;

import com.slepeweb.money.bean.NakedTransaction;


public interface AssetService {
	List<NakedTransaction> get(Date from, Date to);
}