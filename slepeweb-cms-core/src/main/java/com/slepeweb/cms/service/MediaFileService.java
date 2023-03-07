package com.slepeweb.cms.service;

import java.io.InputStream;
import java.util.Map;

import com.slepeweb.cms.bean.FileMetadata;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Media;

public interface MediaFileService {
	String getCurrentBin();
	String getRepositoryFilePath(String bin, String filename);
	InputStream getInputStream(String bin, String filename);
	FileMetadata writeMediaToRepository(Media m);
	Long writeMedia(InputStream is, String filepath);
	boolean wipeBinaryContent(Item i);
	boolean wipeBinaryContent(Media m);
	boolean saveTempFile(Item i, Media m);
	boolean saveTempFile(Item i, InputStream is, boolean isThumbnail);
	String getTempMediaFilepath(Item i, boolean isThumbnail);
	void setBinCapacity(int binCapacity);
	void setMaxTempFiles(int maxTempFiles);
	void setTempFolder(String tempFolder);
	void setTempFilePrefix(String tempFilePrefix);
	void setFileCount(Map<String, Integer> fileCount);
	void setCurrentBin(String currentBin);
	void setRepository(String repository);
}
