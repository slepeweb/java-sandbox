package com.slepeweb.cms.service;

import java.io.InputStream;
import java.sql.Blob;

import com.slepeweb.cms.bean.Item;


public interface MediaService {
	void deleteMedia(Long id);
	Blob getMedia(Long id);
	void writeMedia(Long id, String outputFilePath);
	void save(Long id, InputStream is);
	boolean hasMedia(Item i);
	int getCount();
}
