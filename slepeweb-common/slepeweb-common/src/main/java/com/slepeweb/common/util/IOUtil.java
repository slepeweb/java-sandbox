package com.slepeweb.common.util;

import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;

public class IOUtil {
	
	private static Logger LOG = Logger.getLogger(IOUtil.class);
	
	public static Long writeStreamToFile(InputStream is, String outputFilePath) {
		
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
			LOG.warn(String.format("Error writing media out to file [%s]", outputFilePath), e);
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
}
