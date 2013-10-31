package com.slepeweb.sandbox.ws.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "line", namespace = "com.slepeweb.sandbox.ws.rest.lottery", propOrder = {"line"})
public class LotteryLineBean {
	private static Random RANDOMIZER = new Random(new Date().getTime());
	protected List<Integer> line;
	
	public LotteryLineBean() {
		setLine(getRandomLine());
	}

	public List<Integer> getLine() {
		return this.line;
	}

	public void setLine(List<Integer> line) {
		this.line = line;
	}

	private List<Integer> getRandomLine() {
		List<Integer> randomLine = new ArrayList<Integer>(6);
		Integer i;

		while ( randomLine.size() < 6 )
		{
			i = new Integer( RANDOMIZER.nextInt( 49 ) + 1 );
			if (! randomLine.contains(i)) {
				randomLine.add(i);
			}
		}
		
		Collections.sort(randomLine);
		return randomLine;
	}
}
