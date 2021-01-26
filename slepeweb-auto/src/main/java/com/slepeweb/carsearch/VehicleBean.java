package com.slepeweb.carsearch;

import java.io.Serializable;
import java.util.Date;

import com.sun.xml.internal.ws.util.StringUtils;

public class VehicleBean implements Serializable {
	private static final long serialVersionUID = 1L;
	//private static NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();
		
	private String id, title = "", href, location, sellerType, year, registration, bodystyle;
	private String price, mileage, mileageRating = "", transmission, engine, fuel, description, power;
	private String features;
	private Date dateCreated;
	
	public boolean isExpired(Date d) {
		long now = d.getTime();
		long then = this.dateCreated.getTime();
		long threshold = 30L * 24L * 60L * 60L * 1000L;
		return (now - then) > threshold;
	}
	
	public String getMileageRating() {
		return mileageRating;
	}

	public void setMileageRating(String mileageRating) {
		this.mileageRating = mileageRating;
	}

	public String getPower() {
		return power;
	}

	public void setPower(String power) {
		this.power = power;
	}

	@Override
	public String toString() {
		return String.format("%s / %s / %s / %s / %s", getTitle(), getRegistration(), getLocation(), getMileage(), getPrice());
	}
	
	public String toCsv() {
		String[] parts = {
			getYear(),
			String.format("\"%s\"", getRegistration()),
			getMileage(),
			getMileageRating(),
			getPrice(),
			getBodystyle(),
			getFuel(),
			String.format("%sL", getEngine() == null ? "?" : getEngine()),
			getPower(),
			getTransmission(),
			getLocation(),
			getSellerType(),
			getTitle(),
			getFeatures(),
			getHref()
		};
		
		StringBuilder sb = new StringBuilder();
		
		for (String part : parts) {
			if (sb.length() > 0) {
				sb.append("\t");
			}
			
			sb.append(part == null ? "" : part);
		}
		
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		return getId().hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof VehicleBean) {
			return getId().equals(((VehicleBean)other).getId());
		}
		return false;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String pageHref) {
		this.href = pageHref;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String s) {
		this.location = StringUtils.capitalize(s);
	}

	public String getSellerType() {
		return sellerType;
	}

	public void setSellerType(String sellerType) {
		this.sellerType = sellerType;
	}

	public String getRegistration() {
		return registration;
	}

	public void setRegistration(String registration) {
		this.registration = registration;
	}

	public String getBodystyle() {
		return bodystyle;
	}

	public void setBodystyle(String bodystyle) {
		this.bodystyle = bodystyle;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getMileage() {
		return mileage;
	}

	public void setMileage(String mileage) {
		this.mileage = mileage;
	}

	public String getTransmission() {
		return transmission;
	}

	public void setTransmission(String transmission) {
		this.transmission = transmission;
	}

	public String getEngine() {
		return engine;
	}

	public void setEngine(String engine) {
		this.engine = engine;
	}

	public String getFuel() {
		return fuel;
	}

	public void setFuel(String fuel) {
		this.fuel = fuel;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getFeatures() {
		return features;
	}

	public void setFeatures(String features) {
		this.features = features;
	}
}
