package com.slepeweb.money.bean.solr;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonSetter;

import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Category;

@JsonIgnoreProperties({"start", "hrefBase", "accountIdStr", "payeeIdStr", "categoryIdStr"})
public class SolrParams {

	public static final String START_OF_DAY = "T00:00:00Z";
	public static final String END_OF_DAY = "T23:59:59Z";
	
	private SolrConfig config;
	private String memo, majorCategory, minorCategory, payeeName;
	private List<Category> categories;
	private Long accountId, payeeId, categoryId;
	private int pageNum, pageSize;
	private Date from, to;
	private Long fromAmount, toAmount;
	private boolean debit;

	// For Jackson
	public SolrParams() {}
	
	public SolrParams(SolrConfig config) {
		this.config = config;
	}
	
	public Date getFrom() {
		return from;
	}

	public SolrParams setFrom(String from) {
		if (StringUtils.isNotBlank(from)) {
			setFrom((Date) Util.parseSolrDate(from + START_OF_DAY));
		}
		return this;
	}

	public SolrParams setFrom(Date from) {
		this.from = from;
		return this;
	}

	@JsonSetter("from")
	public SolrParams setFrom(long from) {
		if (from > 0L) {
			this.from = new Date(from);
		}
		return this;
	}

	public Date getTo() {
		return to;
	}

	public SolrParams setTo(String to) {
		if (StringUtils.isNotBlank(to)) {
			setTo((Date) Util.parseSolrDate(to + END_OF_DAY));
		}
		return this;
	}

	public SolrParams setTo(Date to) {
		this.to = to;
		return this;
	}

	@JsonSetter("to")
	public SolrParams setTo(long to) {
		if (to > 0L) {
			this.to = new Date(to);
		}
		return this;
	}

	public String getPayeeName() {
		return payeeName;
	}

	public SolrParams setPayeeName(String payeeName) {
		if (StringUtils.isNotBlank(payeeName)) {
			this.payeeName = payeeName;
		}
		return this;
	}

	public String getMemo() {
		return this.memo == null ? "" : this.memo;
	}

	public SolrParams setMemo(String memo) {
		this.memo = memo;
		return this;
	}

	public String getMajorCategory() {
		return this.majorCategory == null ? "" : this.majorCategory;
	}

	public SolrParams setMajorCategory(String majorCategory) {
		this.majorCategory = majorCategory;
		return this;
	}

	public String getMinorCategory() {
		return minorCategory;
	}

	public SolrParams setMinorCategory(String minorCategory) {
		this.minorCategory = minorCategory;
		return this;
	}

	public Long getPayeeId() {
		return payeeId;
	}

	public String getPayeeIdStr() {
		return getPayeeId() == null ? "" : String.valueOf(getPayeeId());
	}

	@JsonSetter("payeeId")
	public SolrParams setPayeeId(Long payeeId) {
		this.payeeId = payeeId;
		return this;
	}

	public SolrParams setPayeeId(String payeeId) {
		this.payeeId = Util.toLong(payeeId, null);
		return this;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public String getCategoryIdStr() {
		return getCategoryId() == null ? "" : String.valueOf(getCategoryId());
	}

	@JsonSetter("categoryId")
	public SolrParams setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
		return this;
	}

	public SolrParams setCategoryId(String categoryId) {
		this.categoryId = Util.toLong(categoryId, null);
		return this;
	}

	public Long getAccountId() {
		return accountId;
	}

	public String getAccountIdStr() {
		return getAccountId() == null ? "" : String.valueOf(getAccountId());
	}

	@JsonSetter("accountId")
	public SolrParams setAccountId(Long accountId) {
		this.accountId = accountId;
		return this;
	}

	public SolrParams setAccountId(String accountId) {
		this.accountId = Util.toLong(accountId, null);		
		return this;
	}

	public int getPageSize() {
		if (this.pageSize == 0) {
			this.pageSize = this.config.getPageSize();
		}
		return this.pageSize;
	}

	@JsonSetter("pageSize")
	public SolrParams setPageSize(int pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	public SolrParams setPageSize(String s) {
		this.pageSize = Util.toInteger(s, this.config.getPageSize());
		return this;
	}

	public int getPageNum() {
		if ( this.pageNum == 0) {
			this.pageNum = 1;
		}
		return this.pageNum;
	}
	
	public int getStart() {
		return (getPageNum() - 1) * getPageSize();
	}

	@JsonSetter("pageNum")
	public SolrParams setPageNum(int pageNum) {
		this.pageNum = pageNum;
		return this;
	}

	public SolrParams setPageNum(String s) {
		this.pageNum = Util.toInteger(s, 1);
		return this;
	}

	public String getHrefBase() {
		StringBuilder sb = new StringBuilder("/search");
		//sb.append("?").append("searchText=").append(clean(getSearchText()));
		return sb.toString();
	}
	
	/*
	private String clean(String s) {
		String cleaned = s.replaceAll("[<>]", " ");
		try {
			return URLEncoder.encode(cleaned, "utf-8");
		}
		catch (UnsupportedEncodingException e) {}
		return cleaned;
	}
	*/

	public SolrConfig getConfig() {
		return config;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public SolrParams setCategories(List<Category> categories) {
		this.categories = categories;
		return this;
	}

	public Long getFromAmount() {
		return fromAmount;
	}

	@JsonSetter("fromAmount")
	public SolrParams setFromAmount(Long fromAmount) {
		this.fromAmount = fromAmount;
		return this;
	}

	public SolrParams setFromAmount(String s) {
		this.fromAmount = setAmount(s);
		return this;
	}

	public Long getToAmount() {
		return toAmount;
	}

	@JsonSetter("toAmount")
	public SolrParams setToAmount(Long toAmount) {
		this.toAmount = toAmount;
		return this;
	}

	public SolrParams setToAmount(String s) {
		this.toAmount = setAmount(s);
		return this;
	}
	
	private Long setAmount(String s) {
		if (StringUtils.isNotBlank(s)) {
			long pennies = Util.parsePounds(s);
			if (isDebit()) {
				pennies = -pennies;
			}
			return new Long(pennies);
		}
		return null;
	}

	public boolean isDebit() {
		return debit;
	}

	@JsonSetter("debit")
	public SolrParams setDebit(boolean debit) {
		this.debit = debit;
		return this;
	}

	public SolrParams setDebit(String s) {
		if (StringUtils.isNotBlank(s)) {
			this.debit = s.equals("-1");
		}
		else {
			this.debit = false;
		}
		return this;
	}
}
