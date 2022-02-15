package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Media;
import com.slepeweb.cms.except.ResourceException;


public interface MediaService {
	void delete(Long id);
	Media getMedia(Long id);
	Media getMedia(Long id, boolean b);
	List<Media> getAllMedia(Long id);
	void writeMedia(Long id, String outputFilePath);
	Media save(Media m) throws ResourceException;
	boolean hasMedia(Item i);
	boolean hasThumbnail(Item i);
	int getCount();
}
