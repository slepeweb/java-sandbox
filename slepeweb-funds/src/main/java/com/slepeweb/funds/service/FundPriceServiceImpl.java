package com.slepeweb.funds.service;

import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.slepeweb.funds.bean.FundPrice;
import com.slepeweb.funds.except.DuplicateItemException;
import com.slepeweb.funds.except.MissingDataException;

@Service("fundPriceService")
public class FundPriceServiceImpl extends BaseServiceImpl implements FundPriceService {
	
	private static Logger LOG = Logger.getLogger(FundPriceServiceImpl.class);
	private static final String SELECT = 
			"select f.id as fundid, f.name, f.alias, f.units, p.entered, p.value " + 
			"from fund f " + 
			"join price p on f.id = p.fundid ";
	
	public FundPrice save(FundPrice fp) throws MissingDataException, DuplicateItemException {
		if (fp.isDefined4Insert()) {
			FundPrice dbRecord = getFundPrice(fp.getFund().getId(), fp.getEntered());		
			if (dbRecord != null) {
				updateFundPrice(dbRecord, fp);
				return dbRecord;
			}
			else {
				insertFundPrice(fp);
			}
		}
		else {
			String t = "Fund price not saved - insufficient data";
			LOG.error(compose(t, fp));
			throw new MissingDataException(t);
		}
		
		return fp;
	}
	
	private FundPrice insertFundPrice(FundPrice fp) throws MissingDataException, DuplicateItemException {
		
		try {
			this.jdbcTemplate.update(
					"insert into price (fundid, entered, value) values (?, ?, ?)", 
					fp.getFund().getId(), fp.getEntered(), fp.getValue());
			
			LOG.info(compose("Added new fund price", fp));		
			return fp;
		}
		catch (DuplicateKeyException e) {
			throw new DuplicateItemException("Fund price for this date already inserted");
		}
	}

	private void updateFundPrice(FundPrice dbRecord, FundPrice fp) {
		if (! dbRecord.equals(fp)) {
			dbRecord.assimilate(fp);
			
			this.jdbcTemplate.update(
					"update price set value = ? where fundid = ? and entered = ?", 
					dbRecord.getValue(), dbRecord.getFund().getId(), dbRecord.getEntered());
			
			LOG.info(compose("Updated fund price", fp));
		}
		else {
			LOG.info(compose("Fund price not modified", fp));
		}
	}

	public FundPrice getFundPrice(long fundId, Timestamp ts) {
		return (FundPrice) getFirstInList(this.jdbcTemplate.query(
				SELECT + "where f.id = ? and p.entered = ?", 
				new Object[]{fundId, ts}, 
				new RowMapperUtil.FundPriceMapper()));
	}

	public List<FundPrice> getPricesForFund(long fundId) {
		return this.jdbcTemplate.query(
				SELECT + "where p.fundid = ? order by p.entered", 
				new Object[]{fundId}, 
				new RowMapperUtil.FundPriceMapper());
	}
	
	public List<FundPrice> getPricesForFund(long fundId, Timestamp from, Timestamp to) {
		return this.jdbcTemplate.query(
				SELECT + "where p.fundid = ? and p.entered >= ? and p.entered <= ? order by p.entered", 
				new Object[]{fundId, from, to}, 
				new RowMapperUtil.FundPriceMapper());
	}
	
	public List<FundPrice> getPricesForDate(Timestamp ts) {
		return this.jdbcTemplate.query(
				SELECT + "where p.entered = ? order by f.ordering", 
				new Object[]{ts}, 
				new RowMapperUtil.FundPriceMapper());
	}
	
	public List<Timestamp> getDistinctDates(Timestamp from, Timestamp to) {
		return this.jdbcTemplate.query(
				"select distinct entered from price where entered >= ? and entered <= ? order by entered", 
				new Object[]{}, 
				new RowMapperUtil.DistinctTimestampMapper());
	}
	
	public List<FundPrice> getFundPrices(Timestamp ts) {
		return this.jdbcTemplate.query(
				SELECT + "where p.entered = ? order by f.alias", 
				new Object[]{ts}, 
				new RowMapperUtil.FundPriceMapper());
	}

	public List<FundPrice> getAllPrices(Timestamp from, Timestamp to) {
		return this.jdbcTemplate.query(
				SELECT + "where p.entered >= ? and p.entered <= ? order by p.entered, f.ordering", 
				new Object[]{from, to}, 
				new RowMapperUtil.FundPriceMapper());
	}
	
}
