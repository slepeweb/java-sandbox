package com.slepeweb.cms.bean;

public class FileMetadata {

	private String name, bin, path;	
	private Long size;
	
	public String getName() {
		return name;
	}
	
	public FileMetadata setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getBin() {
		return bin;
	}
	
	public FileMetadata setBin(String bin) {
		this.bin = bin;
		return this;
	}
	
	public String getPath() {
		return path;
	}
	
	public FileMetadata setPath(String path) {
		this.path = path;
		return this;
	}
	
	public Long getSize() {
		return size;
	}
	
	public FileMetadata setSize(Long size) {
		this.size = size;
		return this;
	}
}
