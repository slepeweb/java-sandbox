package com.slepeweb.cms.setup;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slepeweb.cms.bean.CmsBeanFactory;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.constant.FieldName;
import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.cms.service.CmsService;
import com.slepeweb.cms.setup.SiteSetupStatistics.ResultType;
import com.slepeweb.cms.utils.LogUtil;
import com.slepeweb.commerce.bean.Axis;
import com.slepeweb.commerce.bean.Product;
import com.slepeweb.commerce.service.ProductXlsRow;

@Component
public class CommerceSetup {
	private static Logger LOG = Logger.getLogger(CommerceSetup.class);
	private static Pattern CURRENCY_PATTERN = Pattern.compile("(\\d+)\\.(\\d{2})");

	@Autowired private CmsService cmsService;

	public void load(String siteName, String filePath) throws ResourceException {
		
		LOG.info(LogUtil.compose("Setting up storefront", filePath));
		SiteSetupStatistics result = new SiteSetupStatistics();
		Site site = this.cmsService.getSiteService().getSite(siteName);

		StopWatch stopwatch = new StopWatch();
		stopwatch.start();

		if (site != null && filePath != null) {
			LOG.info("Reading/validating spreadsheet");
			processCsv(readCsv(filePath, result), site, result);
		}

		finish(result, stopwatch);
	}

	private void finish(SiteSetupStatistics result, StopWatch stopwatch) {
		stopwatch.stop();
		long sec = stopwatch.getTime() / 1000;

		LOG.info("Rows processed    :" + result.getNumRowsProcessed());
		LOG.info("XLS errors        :" + result.getNumXlsErrors());
		LOG.info("XLS warnings      :" + result.getNumXlsWarnings());
		LOG.info("Product updates  :" + result.getNumProductUpdates());
		LOG.info("Took " + sec + " secs");
		LOG.info("Finished");
	}


	private HSSFWorkbook readCsv(String filePath, SiteSetupStatistics stats) {
		InputStream is = null;
		POIFSFileSystem fs;

		// open spreadsheet
		try {
			is = new FileInputStream(filePath);
			fs = new POIFSFileSystem(is);
		} catch (Exception e) {
			LOG.error("Failed to open spreadsheet", e);
			return null;
		}

		// open workbook
		try {
			return new HSSFWorkbook(fs);
		} catch (Exception e) {
			LOG.error("Failed to open workbook", e);
			return null;
		}
		finally {
			// close spreadsheet
			try {
				is.close();
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}		
	}

	private void processCsv(HSSFWorkbook wb, Site site, SiteSetupStatistics stats) throws ResourceException {
		if (wb != null) {
			createProducts(wb.getSheetAt(0).rowIterator(), site, stats);
		}
	}

	private void createProducts(Iterator<Row> rowIter, Site site, SiteSetupStatistics stats) throws ResourceException {		
		Row row;
		Item i;
		Product p;
		String path;
		boolean updateable = false, exists;
		Long oldAlphaAxisId, oldBetaAxisId;
		ProductXlsRow currentRow = new ProductXlsRow();
		ItemType productType = this.cmsService.getItemTypeService().getItemType("Product");
		Axis alpha, beta;
		
		if (productType == null) {
			stats.inc(ResultType.XLS_ERROR);
			LOG.error("Failed to find item type Product in the database");
			return;
		}

		while (rowIter.hasNext()) {
			row = (Row) rowIter.next();
			update(currentRow, row);

			if (currentRow.getUpdate().startsWith("###")) {
				break;
			} else if (currentRow.getUpdate().startsWith("#") || StringUtils.isBlank(currentRow.getUpdate())) {
				continue;
			} else {
				stats.inc(ResultType.ROWS_PROCESSED);
				
				updateable = currentRow.getUpdate().equals("1");
				path = String.format("%s/%s", currentRow.getSection(), currentRow.getPartNum());
				i = this.cmsService.getItemService().getItem(site.getId(), path);
				exists = i != null;
				oldAlphaAxisId = oldBetaAxisId = null;
				
				if (! exists) {
					i = CmsBeanFactory.makeProduct();
					i.setSite(site);
					i.setType(productType);
					i.setPath(path);
					i.setName(StringUtils.isBlank(currentRow.getName()) ? 
							currentRow.getPartNum() : currentRow.getName());
					i.setSimpleName(currentRow.getPartNum());
					i.setFieldValue(FieldName.TITLE, currentRow.getPartNum());
					LOG.info(String.format("Creating new product [%s]", currentRow.getPartNum()));
				}
				else {
					// Determine whether axes have changes
					if (i.isProduct()) {
						p = (Product) i;
						oldAlphaAxisId = p.getAlphaAxisId();
						oldBetaAxisId = p.getBetaAxisId();
					}
				}
				
				if (! exists || updateable) {
					if (i.isProduct()) {
						p = (Product) i;
						p.setPartNum(currentRow.getPartNum()).
						setStock(Long.valueOf(currentRow.getStock())).
						setPrice(pounds2pence(currentRow.getPrice()));
						
						// Identify axes
						alpha = this.cmsService.getAxisService().get(currentRow.getAlpha());
						if (alpha == null && StringUtils.isNotBlank(currentRow.getAlpha())) {
							LOG.warn(String.format("Un-recognized alpha axis [%s]", currentRow.getAlpha()));
						}
						p.setAlphaAxisId(alpha == null ? -1L : alpha.getId());
						
						beta = this.cmsService.getAxisService().get(currentRow.getBeta());
						if (beta == null && StringUtils.isNotBlank(currentRow.getBeta())) {
							LOG.warn(String.format("Un-recognized beta axis [%s]", currentRow.getBeta()));
						}
						p.setBetaAxisId(beta == null ? -1L : beta.getId());
						
						// Delete all variants of this product IFF axes have changed
						if (
								(oldAlphaAxisId != null && ! oldAlphaAxisId.equals(p.getAlphaAxisId())) || 
								(oldBetaAxisId != null  && ! oldBetaAxisId.equals(p.getBetaAxisId()))) {
							
							this.cmsService.getVariantService().deleteMany(p.getOrigId());
							LOG.warn(String.format("Deleted all product variants [%s]", currentRow.getPartNum()));
						}
						
						// Save product
						p.save();
						LOG.info(String.format("Product saved [%s]", currentRow.getPartNum()));
						stats.inc(ResultType.PRODUCT_UPDATED);
					}
				}
				else {
					LOG.debug(LogUtil.compose("Product is not updateable", i));
				}
			}
		}
	}
	
	private void update(ProductXlsRow currentRow, Row row) {
		currentRow.setUpdate(stringValueParser(currentRow.getUpdate(), 
				SiteSetupUtils.getString(row.getCell(0))));
		currentRow.setName(stringValueParser(currentRow.getName(), 
				SiteSetupUtils.getString(row.getCell(1))));
		currentRow.setSection(stringValueParser(currentRow.getSection(), 
				SiteSetupUtils.getString(row.getCell(2))));
		currentRow.setPartNum(stringValueParser(currentRow.getPartNum(), 
				SiteSetupUtils.getString(row.getCell(3))));
		currentRow.setStock(stringValueParser(currentRow.getStock(), 
				SiteSetupUtils.getString(row.getCell(4))));
		currentRow.setPrice(stringValueParser(currentRow.getPrice(), 
				SiteSetupUtils.getString(row.getCell(5))));
		currentRow.setAlpha(stringValueParser(currentRow.getAlpha(), 
				SiteSetupUtils.getString(row.getCell(6))));
		currentRow.setBeta(stringValueParser(currentRow.getBeta(), 
				SiteSetupUtils.getString(row.getCell(7))));
	}
	
	private String stringValueParser(String orig, String replacement) {
		if (StringUtils.isBlank(replacement)) {
			return orig;
		}
		else if (replacement.equals("-")) {
			return null;
		}
		return replacement;
	}
	
	private Long pounds2pence(String poundsStr) {
		Matcher m = CURRENCY_PATTERN.matcher(poundsStr);
		if (m.matches()) {
			return Long.valueOf(m.group(1) + m.group(2));
		}
		return -1L;
	}
}
