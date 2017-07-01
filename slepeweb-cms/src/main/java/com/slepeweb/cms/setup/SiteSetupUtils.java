package com.slepeweb.cms.setup;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;

public class SiteSetupUtils {
	private static Logger LOG = Logger.getLogger(SiteSetupUtils.class);

	public static final String VIEW_KEY_PREFIX = "ms-view:";
	public static final String CONTENT_REF_LINK_NAME = "ms:contentRef";

	public static String format(int handle, String message) {
		return MessageFormat.format("{0,number,#}: {1}", handle, message);
	}

	public static String format(int fromHandle, String linkType, int toHandle, String message, String itemPath,
			String itemType) {
		return MessageFormat.format("{0,number,#}-{1}-{2,number,#}: {3} [{5}: {4}]", fromHandle, linkType, toHandle,
				message, itemPath, itemType);
	}

	public static String format(int handle, String message, String itemPath, String itemType) {
		return MessageFormat.format("{0,number,#}: {1} [{3}: {2}]", handle, message, itemPath, itemType);
	}

	public static String format(String message, String itemPath, String itemType) {
		return MessageFormat.format("{0} [{2}: {1}]", message, itemPath, itemType);
	}

	/**
	 * Get a date value from a spreadsheet cell, converting it from a string value if necessary. Returns null only if cell
	 * has error.
	 * 
	 * @param cell
	 * @return
	 */
	public static Date getDate(Cell cell, Date dflt) {
		if (cell != null) {
			int type = cell.getCellType();

			if (type != Cell.CELL_TYPE_ERROR) {
				if (type == Cell.CELL_TYPE_STRING) {
					String strValue = cell.getStringCellValue().trim();
					if (!StringUtils.isBlank(strValue)) {
						return getDate(strValue);
					} else {
						return new Date(0);
					}
				} else if (type == Cell.CELL_TYPE_NUMERIC || type == Cell.CELL_TYPE_FORMULA) {
					return cell.getDateCellValue();
				} else if (type == Cell.CELL_TYPE_BLANK) {
					return new Date(0);
				}
			}
		}

		return dflt;
	}

	public static Date getDate(Cell cell) {
		return getDate(cell, null);
	}

	/**
	 * Convert string such as '2005-12-21 10:24:00.0' into a Date object. Returns null if format is incorrect.
	 * 
	 * @param s
	 * @return
	 */
	public static Date getDate(String dateStr) {
		int year, month, day, hour, minute, second;
		StringTokenizer tok;

		if (dateStr != null) {
			tok = new StringTokenizer(dateStr);

			if (tok.countTokens() > 0) {
				String dayPart, timePart;
				dayPart = tok.nextToken();
				timePart = null;

				if (tok.hasMoreTokens()) {
					timePart = tok.nextToken();
				}

				tok = new StringTokenizer(dayPart, "-/");

				if (tok.countTokens() == 3) {
					year = Integer.parseInt(tok.nextToken());
					month = Integer.parseInt(tok.nextToken()) - 1;
					day = Integer.parseInt(tok.nextToken());

					if (timePart != null) {
						tok = new StringTokenizer(timePart, ":");

						if (tok.countTokens() == 3) {
							hour = Integer.parseInt(tok.nextToken());
							minute = Integer.parseInt(tok.nextToken());
							second = 0;

							Calendar cal = Calendar.getInstance();
							cal.set(year, month, day, hour, minute, second);
							cal.set(Calendar.MILLISECOND, 0);
							return cal.getTime();
						} else {
							hour = minute = second = 0;
						}
					}
				} else {
					return null;
				}
			}
		}

		return null;
	}

	/**
	 * Get an long value from a spreadsheet cell, converting it from a string value if necessary.
	 * 
	 * @param cell
	 * @return
	 */
	public static Long getLong(Cell cell, Long dflt) {
		if (cell != null) {
			int type = cell.getCellType();

			if (type != Cell.CELL_TYPE_ERROR) {
				if (type == Cell.CELL_TYPE_STRING) {
					return Long.valueOf(cell.getStringCellValue());
				} else if (type == Cell.CELL_TYPE_NUMERIC) {
					return new Long(new Double(cell.getNumericCellValue()).longValue());
				}
			}
		}
		if (dflt != null) {
			return dflt;
		}

		return new Long(-1);
	}

	public static Long getLong(Cell cell) {
		return getLong(cell, null);
	}

	/**
	 * Get an integer value from a spreadsheet cell, converting it from a string value if necessary.
	 * 
	 * @param cell
	 * @return
	 */
	public static Integer getInteger(Cell cell, Integer dflt) {
		if (cell != null) {
			int type = cell.getCellType();

			if (type != Cell.CELL_TYPE_ERROR) {
				if (type == Cell.CELL_TYPE_STRING) {
					return Integer.valueOf(cell.getStringCellValue());
				} else if (type == Cell.CELL_TYPE_NUMERIC) {
					return new Integer(new Double(cell.getNumericCellValue()).intValue());
				}
			}
		}
		if (dflt != null) {
			return dflt;
		}

		return new Integer(-1);
	}

	public static Integer getInteger(Cell cell) {
		return getInteger(cell, null);
	}

	/**
	 * Get a string value from a spreadsheet cell, converting it from a numeric value if necessary. Never null.
	 * 
	 * @param cell
	 * @return
	 */
	public static String getStringIgnoreDecimal(Cell cell, String dflt) {
		if (cell != null) {
			int type = cell.getCellType();
			if (type != Cell.CELL_TYPE_ERROR) {
				if (type == Cell.CELL_TYPE_STRING || type == Cell.CELL_TYPE_FORMULA) {
					return cell.getStringCellValue().trim();
				} else if (type == Cell.CELL_TYPE_NUMERIC) {
					String value = String.valueOf(cell.getNumericCellValue());

					// Strip off decimal part from double value
					int cursor = value.indexOf('.');
					if (cursor > -1) {
						return value.substring(0, cursor);
					}

					return value;
				}
			}
		}

		if (dflt != null) {
			return dflt;
		}

		return "";
	}

	public static String getString(Cell cell) {
		return getString(cell, null);
	}
	
	public static String getString(Cell cell, String dflt) {
		if (cell != null) {
			int type = cell.getCellType();
			if (type != Cell.CELL_TYPE_ERROR) {
				if (type == Cell.CELL_TYPE_STRING || type == Cell.CELL_TYPE_FORMULA) {
					return cell.getStringCellValue().trim();
				} else if (type == Cell.CELL_TYPE_NUMERIC) {
					return String.valueOf(cell.getNumericCellValue());
				}
			}
		}

		if (dflt != null) {
			return dflt;
		}

		return "";
	}

	public static String getStringIgnoreDecimal(Cell cell) {
		return getStringIgnoreDecimal(cell, null);
	}

	public static String diffStr(String subject, Object destination, Object source) {
		return String.format("%s:Dest-[%s] cf. Src-[%s]", subject, destination, source);
	}

	static final int BUFFER = 2048;

	public static void zipMedia(InputStream media, File outputFile) {
		BufferedInputStream origin = null;
		ZipOutputStream out = null;

		try {
			out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));

			// out.setMethod(ZipOutputStream.DEFLATED);
			byte data[] = new byte[BUFFER];

			// FileInputStream fi = new FileInputStream( files[ i ] );
			origin = new BufferedInputStream(media, BUFFER);
			ZipEntry entry = new ZipEntry("media");
			out.putNextEntry(entry);
			int count;

			while ((count = origin.read(data, 0, BUFFER)) != -1) {
				out.write(data, 0, count);
			}
		} catch (Exception e) {
			LOG.error("Failed to write zipped media content to file", e);
		} finally {
			try {
				if (origin != null) {
					origin.close();
				}
				if (origin != null) {
					out.close();
				}
			} catch (IOException e) {
				LOG.error("Failed to close IO resources", e);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public static InputStream unzipMedia(File inputFile) {
		BufferedOutputStream dest = null;
		BufferedInputStream is = null;
		ZipFile zipfile = null;

		try {
			ZipEntry entry;
			zipfile = new ZipFile(inputFile);
			Enumeration e = zipfile.entries();

			if (e.hasMoreElements()) {
				entry = (ZipEntry) e.nextElement();
				return new BufferedInputStream(zipfile.getInputStream(entry));
			}
		} catch (Exception e) {
			LOG.error("Failed to unzip media content from file", e);
		} finally {
			try {
				if (dest != null) {
					dest.flush();
					dest.close();
				}

				if (is != null) {
					is.close();
				}

				if (zipfile != null) {
					zipfile.close();
				}
			} catch (IOException e) {
				LOG.error("Failed to close IO resources", e);
			}
		}

		return null;
	}
}
