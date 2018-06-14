package com.slepeweb.money.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public interface MSAccessService {
	ResultSet getNextTransaction() throws SQLException;
	String getAccount() throws SQLException;
	String getPayee() throws SQLException;
	Timestamp getDate() throws SQLException;
	String getMemo() throws SQLException;
	long getAmount() throws SQLException;
	long getOrigId() throws SQLException;
}
