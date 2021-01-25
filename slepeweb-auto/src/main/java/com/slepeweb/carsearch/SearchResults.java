package com.slepeweb.carsearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchResults implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<VehicleBean> vehicles = new ArrayList<VehicleBean>();
	private int numPages;
	
	private transient Map<String, VehicleBean> vehicleById = new HashMap<String, VehicleBean>(61);
	private transient int numProcessed = 0, numNew = 0, numUpdated = 0, numUnchanged = 0, numExpired = 0;
	
	public void mapVehicles() {
		this.vehicleById = new HashMap<String, VehicleBean>(61);
		for (VehicleBean v : this.vehicles) {
			this.vehicleById.put(v.getId(), v);
		}
	}
	
	public void addVehicle(VehicleBean v) {
		this.vehicles.add(v);
		this.vehicleById.put(v.getId(), v);
	}
	
	public List<VehicleBean> getVehicles() {
		return vehicles;
	}
	
	public void setVehicles(List<VehicleBean> titanium) {
		this.vehicles = titanium;
	}
	
	public Map<String, VehicleBean> getVehicleById() {
		return vehicleById;
	}
	
	public void setVehicleById(Map<String, VehicleBean> vehicleById) {
		this.vehicleById = vehicleById;
	}

	public int getNumProcessed() {
		return numProcessed;
	}

	public void incNumProcessed() {
		this.numProcessed++;
	}

	public int getNumNew() {
		return numNew;
	}

	public void incNumNew() {
		this.numNew++;
	}

	public int getNumUpdated() {
		return numUpdated;
	}

	public void incNumUpdated() {
		this.numUpdated++;
	}

	public int getNumUnchanged() {
		return numUnchanged;
	}

	public void incNumUnchanged() {
		this.numUnchanged++;
	}

	public int getNumExpired() {
		return numExpired;
	}

	public void incNumExpired() {
		this.numExpired++;
	}

	public int getNumPages() {
		return numPages;
	}

	public void setNumPages(int numPages) {
		this.numPages = numPages;
	}

}
