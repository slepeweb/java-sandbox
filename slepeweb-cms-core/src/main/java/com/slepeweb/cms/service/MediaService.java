package com.slepeweb.cms.service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Media;


public interface MediaService {
	void delete(Long id);
	Media getMedia(Long id);
	void writeMedia(Long id, String outputFilePath);
	Media save(Media m);
	boolean hasMedia(Item i);
	int getCount();
}
