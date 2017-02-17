package com.slepeweb.cms.service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Media;
import com.slepeweb.cms.except.MissingDataException;


public interface MediaService {
	void delete(Long id);
	Media getMedia(Long id);
	void writeMedia(Long id, String outputFilePath);
	Media save(Media m) throws MissingDataException;
	boolean hasMedia(Item i);
	int getCount();
}
