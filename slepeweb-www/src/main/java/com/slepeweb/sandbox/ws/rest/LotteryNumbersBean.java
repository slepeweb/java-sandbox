package com.slepeweb.sandbox.ws.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="lottery")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "lottery", namespace = "com.slepeweb.sandbox.ws.rest.lottery", propOrder = {"date", "lines"})
public class LotteryNumbersBean {
	protected List<LotteryLineBean> lines = new ArrayList<LotteryLineBean>();
	protected Date date;

	public LotteryNumbersBean(int num) {
		setDate(new Date());
		while( getLines().size() < num) {
			getLines().add(new LotteryLineBean());
		}
	}
	
	public LotteryNumbersBean() {		
	}
	
	public List<LotteryLineBean> getLines() {
		return lines;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
