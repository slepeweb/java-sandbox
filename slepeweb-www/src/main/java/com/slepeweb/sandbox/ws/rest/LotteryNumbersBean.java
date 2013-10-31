package com.slepeweb.sandbox.ws.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="lottery")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "lottery", namespace = "com.slepeweb.sandbox.ws.rest.lottery", propOrder = {"date", "lines"})
public class LotteryNumbersBean {
	private static Random RANDOMIZER = new Random(new Date().getTime());
	protected List<String> lines = new ArrayList<String>();
	protected Date date;

	public LotteryNumbersBean(int num) {
		setDate(new Date());
		while( getLines().size() < num) {
			getLines().add(getRandomLine());
		}
	}
	
	private String getRandomLine() {
		List<Integer> arr = new ArrayList<Integer>(6);
		Integer i;

		while ( arr.size() < 6 )
		{
			i = new Integer( RANDOMIZER.nextInt( 49 ) + 1 );
			if (! arr.contains(i)) {
				arr.add(i);
			}
		}
		
		Collections.sort(arr);
		StringBuilder sb = new StringBuilder();
		for (Integer j : arr) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(j);
		}
		return sb.toString();
	}
	
	public LotteryNumbersBean() {		
	}
	
	public List<String> getLines() {
		return lines;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
