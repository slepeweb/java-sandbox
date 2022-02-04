package com.slepeweb.cms.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.FileMetadata;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Media;
import com.slepeweb.common.util.NumberUtil;

@Repository
public class MediaFileServiceImpl extends BaseServiceImpl implements MediaFileService {
	
	private static Logger LOG = Logger.getLogger(MediaFileServiceImpl.class);
	public static final int BIN_CAPACITY = 200;
	
	private String repository = "/home/photos";
	private Map<String, Integer> fileCount = new HashMap<String, Integer>();
	private String currentBin;
	
	@PostConstruct
	private void init() throws Exception {
		File root = new File(repository);
		
		// Make sure administrator has created a file repository.
		if (! (root.exists() && root.isDirectory())) {
			throw new Exception(String.format("File storage directory does not exist [%s]", repository));
		}
		
		// Count the number of files in each bin
		for (File child : root.listFiles()) {
			this.fileCount.put(child.getName(), child.list().length);
			LOG.info(String.format("File storage bin %s has %d entries", child.getName(), child.list().length));
		}
		
		// Is the repository empty?
		String firstBin = "aa";
		
		if (this.fileCount.size() == 0) {
			createBin(firstBin);
		}
		
		// Identify the next available bin in the file store
		this.currentBin = identifyBinWithSpace(firstBin);
	}

	public String getCurrentBin() {
		return this.currentBin;
	}
	
	/*
	 * This writes the file to a bin in the repository, and returns the bin identifier.
	 * Should the write fail, then null is returned.
	 */
	public FileMetadata writeMediaToRepository(BufferedInputStream is, String filename) {
		
		this.currentBin = identifyBinWithSpace(this.currentBin);
		String outputFilePath = getRepositoryFilePath(this.currentBin, filename);
		Long bytesWritten = writeMedia(is, outputFilePath);
		
		if (bytesWritten != null) {
			// Increment file count for this bin.
			increment();
			
			return new FileMetadata().
					setName(filename).
					setBin(this.currentBin).
					setPath(outputFilePath).
					setSize(bytesWritten);
		}

		return null;
	}	
	
	private int increment() {
		int n = this.fileCount.get(this.currentBin) + 1;
		this.fileCount.put(this.currentBin, n);
		return n;
	}

	public boolean delete(Item i) {
		if (i.getType().isMedia()) {
			for (Media m : i.getAllMedia()) {
				if (m.isFileStored()) {
					String filePath = getRepositoryFilePath(m.getFolder(), m.getRepositoryFileName());
					File f = new File(filePath);
					
					if (f.delete()) {
						LOG.info(String.format("Successfully deleted media from file system [%s]", filePath));
					}
					else {
						LOG.warn(String.format("Failed to delete media from file system [%s]", filePath));
					}
				}
			}
		}
		return false;
	}	

	/*
	 * After consuming the input stream, this method closes the same.
	 */
	public Long writeMedia(BufferedInputStream is, String outputFilePath) {
		
		FileOutputStream fos = null;
		
		try {
			long totalBytesWritten = 0;
			fos = new FileOutputStream(outputFilePath);
			int bufflen = 1000;
			byte[] buffer = new byte[bufflen];
			int numBytes;
			while ((numBytes = is.read(buffer, 0, bufflen)) > -1) {
				fos.write(buffer, 0, numBytes);
				totalBytesWritten += numBytes;
			}
			
			LOG.info(String.format("File written [%s, %s]", outputFilePath, 
					NumberUtil.formatBytes(totalBytesWritten)));
			
			return totalBytesWritten;
		}
		catch (Exception e) {
			LOG.warn(compose("Error writing media out to file", outputFilePath), e);
			return null;
		}
		finally {
			try {
				if (fos != null) fos.close();
				if (is != null) is.close();
			}
			catch (Exception e) {
				
			}
		}
	}	

	/*
	 * Identify the next bin insequence that has space.
	 * Create a new bin if necessary.
	 */
	private String identifyBinWithSpace(String testCase) {
		Integer count = this.fileCount.get(testCase);
		
		if (count == null) {
			return createBin(testCase) ? testCase : null;
		}
		else if (count >= BIN_CAPACITY) {
			return identifyBinWithSpace(nextBin(testCase));
		}
		else {			
			LOG.info(String.format("Using bin %s, which has %d files", testCase, count));
			return testCase;
		}
	}
	
	/*
	 * Work out the next bin in sequence.
	 */
	private String nextBin(String current) {
		char[] chars = current.toCharArray();
		
		if (chars[1] < 'z') {
			chars[1] += 1; 
		}
		else {
			if (chars[0] < 'z') {
				chars[0] += 1; 
				chars[1] = 'a';
			}
			else {
				// LOG IT
			}
		}
		
		return new String(chars);
	}
	
	public void setRepository(String repository) {
		this.repository = repository;
	}
	
	public String getRepositoryFilePath(String bin, String filename) {
		return String.format("%s/%s/%s", this.repository, bin, filename);
	}
	
	public InputStream getInputStream(String bin, String filename) {
		try {
			return new FileInputStream(getRepositoryFilePath(bin, filename));
		}
		catch (Exception e) {
			LOG.error("Open file error", e);
		}
		
		return null;
	}

	/*
	 * Create a directory, and map it.
	 */
	private boolean createBin(String id) {
		File f = new File(repository + "/" + id);
		if (f.mkdir()) {
			this.fileCount.put(id, 0);
			LOG.info(String.format("Created new bin [%s]", id));
			return true;
		}
		else {
			LOG.error(String.format("Failed to create bin [%s]", id));
		}
		
		return false;
	}
	
	private static String nextBinStat(String current) {
		char[] chars = current.toCharArray();
		
		if (chars[1] < 'z') {
			chars[1] += 1; 
		}
		else {
			if (chars[0] < 'z') {
				chars[0] += 1; 
				chars[1] = 'a';
			}
			else {
				// LOG IT
			}
		}
		
		return new String(chars);
	}
	
	public static void main(String[] args) {
		String bin = "aa";
		for (int i = 0; i < 200; i++) {
			bin = nextBinStat(bin);
			System.out.print(", " + bin);
		}
	}
}
