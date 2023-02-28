package com.slepeweb.cms.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.FileMetadata;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Media;
import com.slepeweb.common.util.IOUtil;

@Repository
public class MediaFileServiceImpl extends BaseServiceImpl implements MediaFileService {
	
	private static Logger LOG = Logger.getLogger(MediaFileServiceImpl.class);
	public static final int BIN_CAPACITY = 200;
	public static final int MAX_TEMP_FILES = 4; // This should be 2 * ItemUpdateHistory.MAX_SIZE + 2
	public static final String TMP_FOLDER = "/tmp";
	public static final String TMP_FILE_PREFIX = "pho-";
	
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
	public FileMetadata writeMediaToRepository(Media m) {
		
		// If this is a new Media record, write it to the 'current' folder in the repository
		String binIdentifier = this.currentBin = identifyBinWithSpace(this.currentBin);

		// Otherwise, if an existing Media record is being updated, use the same folder
		if (StringUtils.isNotBlank(m.getFolder())) {
			binIdentifier = m.getFolder();
		}
		
		String outputFilePath = getRepositoryFilePath(binIdentifier, m.getRepositoryFileName());
		File f = new File(outputFilePath);
		boolean outputFileAlreadyExists = f.exists();
		
		Long bytesWritten = writeMedia(m.getUploadStream(), outputFilePath);
		
		if (bytesWritten != null) {
			if (! outputFileAlreadyExists) {
				// Increment file count for this bin.
				increaseCount();
			}
			
			return new FileMetadata().
					setName(m.getRepositoryFileName()).
					setBin(binIdentifier).
					setPath(outputFilePath).
					setSize(bytesWritten);
		}

		return null;
	}	
	
	private int increaseCount() {
		return adjustCount(1);
	}
	
	private int decreaseCount() {
		return adjustCount(-1);
	}
	
	private int adjustCount(int k) {
		int n = this.fileCount.get(this.currentBin) + k;
		this.fileCount.put(this.currentBin, n);
		return n;
	}

	public boolean wipeBinaryContent(Item i) {
		boolean ok = true;
		
		if (i.getType().isMedia()) {
			for (Media m : i.getAllMedia()) {
				ok = wipeBinaryContent(m) && ok;
			}
		}
		
		return ok;
	}	

	public boolean wipeBinaryContent(Media m) {
		if (m.isBinaryContentLoaded()) {
			String filePath = getRepositoryFilePath(m.getFolder(), m.getRepositoryFileName());
			File f = new File(filePath);
			
			if (f.exists() && f.delete()) {
				int n = decreaseCount();
				LOG.info(String.format("Successfully deleted media [%s]; bin count (%s) is %d", filePath, this.currentBin, n));
				return true;
			}
			else {
				LOG.warn(String.format("Failed to delete media from file system [%s]", filePath));
			}
		}
		
		return false;
	}	

	/*
	 * After consuming the input stream, this method closes the same.
	 */
	public Long writeMedia(InputStream is, String outputFilePath) {
		return IOUtil.writeStreamToFile(is, outputFilePath);
	}	

	/*
	 * Identify the next bin in sequence that has space.
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
			File f = new File(getRepositoryFilePath(bin, filename));
			if (f.exists() && f.canRead()) {
				return new FileInputStream(f);
			}
		}
		catch (Exception e) {
			LOG.error("Open file error", e);
		}
		
		return null;
	}

	public boolean saveTempFile(Item i, Media m) {
		return saveTempFile(i, m.getDownloadStream(), m.isThumbnail());
	}
	
	public boolean saveTempFile(Item i, InputStream is, boolean isThumbnail) {
		// First, trim the number of files in the temp folder
		deleteExcessTempFiles();
		
		String tempFilename = getTempMediaFilepath(i, isThumbnail);
		File f = new File(tempFilename);
		
		if (! f.exists() && is != null) {
			LOG.debug(String.format("Saving temporary media file [%s]", tempFilename));
			return IOUtil.writeStreamToFile(new BufferedInputStream(is), tempFilename) != null;
		}
		
		return false;
	}	

	public String getTempMediaFilepath(Item i, boolean isThumbnail) {
		return 
			TMP_FOLDER + 
			"/" +
			TMP_FILE_PREFIX +
			i.getDateUpdated().getTime() + 
			"-" + 
			i.getId() +
			(isThumbnail ? "t" : "");
	}
	
	private int deleteExcessTempFiles() {
		int count = 0;
		int maxFiles = MAX_TEMP_FILES;
		
		File f = new File(TMP_FOLDER);
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(TMP_FILE_PREFIX);
			}			
		};
		
		File[] existing = f.listFiles(filter);
		if (existing.length > maxFiles) {
			// sort files in reverse date order, then trim the excess
			Arrays.sort(existing, new Comparator<File>() {
				@Override
				public int compare(File f1, File f2) {
                    return f2.getName().compareTo(f1.getName());
                }
			});
			
			for (int n = existing.length; n > MAX_TEMP_FILES; n--) {
				existing[n - 1].delete();
				count ++;
			}
			
			LOG.info(String.format("Deleted %d temporary media files", count));
		}
		
		return count;
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
