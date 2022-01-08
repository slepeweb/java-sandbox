package com.slepeweb.cms.service;

import java.io.BufferedInputStream;
import java.io.InputStream;

import com.slepeweb.cms.bean.FileMetadata;
import com.slepeweb.cms.bean.Item;

public interface MediaFileService {
	String getCurrentBin();
	String getRepositoryFilePath(String bin, String filename);
	InputStream getInputStream(String bin, String filename);
	FileMetadata writeMediaToRepository(BufferedInputStream is, String filename);
	Long writeMedia(BufferedInputStream is, String filepath);
	boolean delete(Item i);
}
