package com.slepeweb.carsearch;

public class StatisticBean {
	private String registration;
	private int numVehicles;
	private int totalPrice;
	
	public void inc(int price) {
		this.numVehicles++;
		this.totalPrice += price;
	}
	
	public int getAveragePrice() {
		return totalPrice / numVehicles;
	}
	
	public String getRegistration() {
		return registration;
	}
	
	public void setRegistration(String registration) {
		this.registration = registration;
	}
	
	public int getNumVehicles() {
		return numVehicles;
	}
	
	public void setNumVehicles(int numVehicles) {
		this.numVehicles = numVehicles;
	}
	
	public int getTotalPrice() {
		return totalPrice;
	}
	
	public void setTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice;
	}
}
