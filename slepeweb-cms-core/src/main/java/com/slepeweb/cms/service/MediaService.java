package com.slepeweb.cms.service;

import java.io.InputStream;
import java.util.List;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Media;
import com.slepeweb.cms.except.ResourceException;


public interface MediaService {
	boolean delete(Long id);
	boolean delete(Long id, boolean thumbnail);
	boolean delete(Media m);
	Media getMedia(Long id);
	Media getMedia(Long id, boolean b);
	List<Media> getAllMedia(Long id);
	void writeMedia(Long id, String outputFilePath);
	Media save(Media m) throws ResourceException;
	Media save(Long itemId, InputStream is, boolean isThumbnail) throws ResourceException;
	Media make(Long itemId, InputStream is, boolean isThumbnail);
	boolean hasMedia(Item i);
	boolean hasThumbnail(Item i);
	int getCount();
	void wipeBinaryContent(Media m);
}
