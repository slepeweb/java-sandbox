package com.slepeweb.cms.setup;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
import com.slepeweb.cms.bean.Template;
import com.slepeweb.cms.constant.FieldName;
import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.cms.service.CmsService;
import com.slepeweb.cms.setup.SiteSetupStatistics.ResultType;
import com.slepeweb.cms.utils.LogUtil;
import com.slepeweb.commerce.bean.Axis;
import com.slepeweb.commerce.bean.AxisValue;
import com.slepeweb.commerce.bean.Product;
import com.slepeweb.commerce.bean.Variant;
import com.slepeweb.commerce.xls.AxisValueXlsRow;
import com.slepeweb.commerce.xls.AxisXlsRow;
import com.slepeweb.commerce.xls.ProductXlsRow;
import com.slepeweb.commerce.xls.VariantXlsRow;

@Component
public class CommerceSetup {
	private static Logger LOG = Logger.getLogger(CommerceSetup.class);
	private static Pattern CURRENCY_PATTERN = Pattern.compile("(\\d+)(\\.(\\d*))?");

	@Autowired private CmsService cmsService;
	private Template productTemplate, categoryTemplate;

	public void load(String siteName, String filePath, String productTemplateName, String categoryTemplateName) {
		
		LOG.info(LogUtil.compose("Setting up storefront", filePath, productTemplateName, categoryTemplateName));
		SiteSetupStatistics result = new SiteSetupStatistics();
		Site site = this.cmsService.getSiteService().getSite(siteName);

		StopWatch stopwatch = new StopWatch();
		stopwatch.start();

		if (site != null && 
				StringUtils.isNotBlank(filePath) && 
				StringUtils.isNotBlank(productTemplateName) && 
				StringUtils.isNotBlank(categoryTemplateName)) {
			
			this.productTemplate = this.cmsService.getTemplateService().getTemplate(site.getId(), productTemplateName);
			this.categoryTemplate = this.cmsService.getTemplateService().getTemplate(site.getId(), categoryTemplateName);
			
			if (this.productTemplate != null && this.categoryTemplate != null) {
				LOG.info("Reading/validating spreadsheet");
				processCsv(readCsv(filePath, result), site, result);
			}
			else {
				LOG.error("Check templates exist");
			}
		}

		finish(result, stopwatch);
	}

	private void finish(SiteSetupStatistics result, StopWatch stopwatch) {
		stopwatch.stop();
		long sec = stopwatch.getTime() / 1000;

		LOG.info("Rows processed       :" + result.getNumRowsProcessed());
		LOG.info("XLS errors           :" + result.getNumXlsErrors());
		LOG.info("XLS warnings         :" + result.getNumXlsWarnings());
		LOG.info("Axes processed       :" + result.getNumAxisUpdates());
		LOG.info("AxisValues processed :" + result.getNumAxisValueUpdates());
		LOG.info("Products processed   :" + result.getNumProductUpdates());
		LOG.info("Variants processed   :" + result.getNumVariantUpdates());
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

	private void processCsv(HSSFWorkbook wb, Site site, SiteSetupStatistics stats) {
		if (wb != null) {
			if (createAxes(wb.getSheetAt(0).rowIterator(), stats)) {
				if (createAxisValues(wb.getSheetAt(1).rowIterator(), stats)) {
					if (createProducts(wb.getSheetAt(2).rowIterator(), site, stats)) {
						createVariants(wb.getSheetAt(3).rowIterator(), site, stats);
					}
				}
			}
		}
	}

	private boolean createAxes(Iterator<Row> rowIter, SiteSetupStatistics stats) {		
		Row row;
		Axis a;
		AxisXlsRow currentRow = new AxisXlsRow();
		boolean ok = true;
		
		while (rowIter.hasNext()) {
			row = (Row) rowIter.next();
			update(currentRow, row);

			if (currentRow.getUpdate().startsWith("###")) {
				break;
			} else if (currentRow.getUpdate().startsWith("#") || StringUtils.isBlank(currentRow.getUpdate())) {
				continue;
			} else {
				stats.inc(ResultType.ROWS_PROCESSED);
				
				if (! currentRow.getUpdate().equals("1")) {
					LOG.debug(LogUtil.compose("Axis is not updateable", currentRow.getShortname()));
					continue;
				}
				
				if (StringUtils.isBlank(currentRow.getShortname())) {
					LOG.error(LogUtil.compose("Missing shortname in XLS"));
					stats.inc(ResultType.XLS_ERROR);
					ok = false;
					continue;
				}
				
				a = this.cmsService.getAxisService().get(currentRow.getShortname());
				if (a == null) {
					a = CmsBeanFactory.makeAxis().
							setShortname(currentRow.getShortname());
				}
								
				a.
					setLabel(currentRow.getLabel()).
					setUnits(currentRow.getUnits()).
					setDescription(currentRow.getDescription());
				
					
				// Save variant
				try {
					a.save();
					stats.inc(ResultType.AXIS_UPDATED);
				}
				catch (ResourceException e) {
					LOG.error(LogUtil.compose("Failed to save axis", a.getShortname()));
					ok = false;
				}
			}
		}
		
		return ok;
	}
	
	private boolean createAxisValues(Iterator<Row> rowIter, SiteSetupStatistics stats) {		
		Row row;
		Axis a;
		AxisValue av;
		AxisValueXlsRow currentRow = new AxisValueXlsRow();
		String lastAxisShortname = null;
		int ordering = 10;
		boolean ok = true;
		
		while (rowIter.hasNext()) {
			row = (Row) rowIter.next();
			update(currentRow, row);
			if (lastAxisShortname == null || ! lastAxisShortname.equals(currentRow.getAxis())) {
				lastAxisShortname = currentRow.getAxis();
				ordering = 10;
			}

			if (currentRow.getUpdate().startsWith("###")) {
				break;
			} else if (currentRow.getUpdate().startsWith("#") || StringUtils.isBlank(currentRow.getUpdate())) {
				continue;
			} else {
				stats.inc(ResultType.ROWS_PROCESSED);
				
				if (! currentRow.getUpdate().equals("1")) {
					LOG.debug(LogUtil.compose("AxisValue is not updateable", currentRow.getAxis(), currentRow.getAxis()));
					continue;
				}
				
				if (StringUtils.isBlank(currentRow.getValue())) {
					LOG.error(LogUtil.compose("Missing value in XLS"));
					stats.inc(ResultType.XLS_ERROR);
					ok = false;
					continue;
				}
				
				a = this.cmsService.getAxisService().get(currentRow.getAxis());
				if (a == null) {
					LOG.error(LogUtil.compose("No such axis in DB", currentRow.getAxis()));
					stats.inc(ResultType.XLS_ERROR);
					ok = false;
					continue;
				}
				
				av = this.cmsService.getAxisValueService().get(a.getId(), currentRow.getValue());
				if (av == null) {
					av = CmsBeanFactory.makeAxisValue().
							setAxisId(a.getId()).
							setValue(currentRow.getValue());
				}
								
				av.setOrdering(ordering);				
				ordering += 10;
									
				// Save axis value
				try {
					av.save();
					stats.inc(ResultType.AXISVALUE_UPDATED);
				}
				catch (ResourceException e) {
					LOG.error(LogUtil.compose("Failed to save axis value", a.getShortname(), av.getValue()));
					ok = false;
				}
			}
		}
		
		return ok;
	}
	
	private boolean createProducts(Iterator<Row> rowIter, Site site, SiteSetupStatistics stats) {		
		Row row;
		Item i, categoryItem;
		Product p;
		String productPath, categoryPath;
		Long oldAlphaAxisId, oldBetaAxisId;
		ProductXlsRow currentRow = new ProductXlsRow();
		ItemType productType = this.productTemplate.getItemType();
		ItemType categoryType = this.categoryTemplate.getItemType();
		Axis alpha, beta;
		boolean ok = true;
		
		while (rowIter.hasNext()) {
			row = (Row) rowIter.next();
			update(currentRow, row);

			if (currentRow.getUpdate().startsWith("###")) {
				break;
			} else if (currentRow.getUpdate().startsWith("#") || StringUtils.isBlank(currentRow.getUpdate())) {
				continue;
			} else {
				stats.inc(ResultType.ROWS_PROCESSED);
				
				if (! currentRow.getUpdate().equals("1")) {
					LOG.debug(LogUtil.compose("Product is not updateable", currentRow.getPartNum()));
					continue;
				}
				
				categoryPath = currentRow.getSection();
				categoryItem = this.cmsService.getItemService().getItem(site.getId(), categoryPath);
				if (categoryItem == null) {
					createCategory(site, categoryType, categoryPath);
				}
				
				productPath = String.format("%s/%s", categoryPath, currentRow.getPartNum());
				i = this.cmsService.getItemService().getItem(site.getId(), productPath);
				oldAlphaAxisId = oldBetaAxisId = null;
				
				if (i == null) {
					i = CmsBeanFactory.makeProduct();
					i.setSite(site);
					i.setType(productType);
					i.setPath(productPath);
					i.setTemplate(productTemplate);
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
				
				i.
					setName(StringUtils.isBlank(currentRow.getName()) ? currentRow.getPartNum() : currentRow.getName()).
					setSimpleName(currentRow.getPartNum()).
					setFieldValue(FieldName.TITLE, currentRow.getPartNum());
				
				if (i.isProduct()) {
					p = (Product) i;
					p.
						setPartNum(currentRow.getPartNum()).
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
					try {
						p.save();
						stats.inc(ResultType.PRODUCT_UPDATED);
					}
					catch (ResourceException e) {
						LOG.error(LogUtil.compose("Failed to save product", p.getPartNum(), e.getMessage()));
						ok = false;
					}
				}
			}
		}
		
		return ok;
	}
	
	private Item createCategory(Site site, ItemType category, String path) {		
		Item i = CmsBeanFactory.makeItem(category.getName()).
				setSite(site).
				setType(category).
				setPath(path).
				setTemplate(this.categoryTemplate).
				setName("New category");
		
		try {
			LOG.info(String.format("Creating new category [%s]", category.getName()));
			return i.save();
		}
		catch (ResourceException e) {
			LOG.error(String.format("Failed to create category item: %s", e.getMessage()));
		}
		
		return null;
	}
	
	private void createVariants(Iterator<Row> rowIter, Site site, SiteSetupStatistics stats) {		
		Row row;
		Product p;
		Variant v;
		VariantXlsRow currentRow = new VariantXlsRow();
		AxisValue alphaValue, betaValue;
		Map<String, Product> productCache = new HashMap<String, Product>();
		Map<String, AxisValue> axisValueCache = new HashMap<String, AxisValue>();
		
		while (rowIter.hasNext()) {
			row = (Row) rowIter.next();
			update(currentRow, row);

			if (currentRow.getUpdate().startsWith("###")) {
				break;
			} else if (currentRow.getUpdate().startsWith("#") || StringUtils.isBlank(currentRow.getUpdate())) {
				continue;
			} else {
				stats.inc(ResultType.ROWS_PROCESSED);
				
				if (! currentRow.getUpdate().equals("1")) {
					LOG.debug(LogUtil.compose("Variant is not updateable", currentRow.getPartNum()));
					continue;
				}
				
				if (StringUtils.isBlank(currentRow.getPartNum())) {
					LOG.error(LogUtil.compose("Missing part number in XLS", currentRow.getPartNum()));
					stats.inc(ResultType.XLS_ERROR);
					continue;
				}
				
				String[] partNumberArr = currentRow.getPartNum().split(",");
				
				for (String partNumber : partNumberArr) {
					partNumber = partNumber.trim();
					
					if (StringUtils.isBlank(partNumber)) {
						continue;
					}
					
					p = productCache.get(partNumber);
					if (p == null) {
						p = this.cmsService.getProductService().get(site.getId(), partNumber);
						if (p != null) {
							productCache.put(partNumber, p);
						}
					}
					
					if (p == null) {
						LOG.error(LogUtil.compose("Product not in DB", partNumber));
						stats.inc(ResultType.XLS_ERROR);
						continue;
					}
					
					// The alpha axis must have a value
					alphaValue = getAxisValue(axisValueCache, p.getAlphaAxisId(), currentRow.getAlpha());
					if (alphaValue == null) {
						LOG.error(LogUtil.compose("Alpha axis value not in DB", currentRow.getAlpha()));
						stats.inc(ResultType.XLS_ERROR);
						continue;
					}
					
					// The beta axis is not mandatory
					betaValue = p.getBetaAxisId() > 0L ?
							getAxisValue(axisValueCache, p.getBetaAxisId(), currentRow.getBeta()) :
							null;				
					
					v = this.cmsService.getVariantService().get(p.getOrigId(), alphaValue.getId(), 
							betaValue != null ? betaValue.getId() : -1L);
					
					if (v == null) {
						v = CmsBeanFactory.makeVariant();
						v.setOrigItemId(p.getOrigId()).
						setAlphaAxisValueId(alphaValue.getId()).
						setBetaAxisValueId(betaValue != null ? betaValue.getId() : -1L);
					}
					
					v.
						setQualifier(currentRow.getQualifier()).
						setStock(Long.valueOf(currentRow.getStock())).
						setPrice(pounds2pence(currentRow.getPrice()));
					
						
					// Save variant
					try {
						v.save();
						stats.inc(ResultType.VARIANT_UPDATED);
					}
					catch (ResourceException e) {
						LOG.error(LogUtil.compose("Failed to save variant", e.getMessage(), p.getPartNum(), v.getQualifier()));
					}
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private void update(AxisXlsRow currentRow, Row row) {
		int cell = 0;
		currentRow.setUpdate(stringValueParser(currentRow.getUpdate(), 
				SiteSetupUtils.getStringIgnoreDecimal(row.getCell(cell++))));
		currentRow.setShortname(stringValueParser(currentRow.getShortname(), 
				SiteSetupUtils.getString(row.getCell(cell++))));
		currentRow.setLabel(stringValueParser(currentRow.getLabel(), 
				SiteSetupUtils.getString(row.getCell(cell++))));
		currentRow.setUnits(stringValueParser(currentRow.getUnits(), 
				SiteSetupUtils.getString(row.getCell(cell++))));
		currentRow.setDescription(stringValueParser(currentRow.getDescription(), 
				SiteSetupUtils.getString(row.getCell(cell++))));
	}
	
	
	@SuppressWarnings("deprecation")
	private void update(AxisValueXlsRow currentRow, Row row) {
		int cell = 0;
		currentRow.setUpdate(stringValueParser(currentRow.getUpdate(), 
				SiteSetupUtils.getStringIgnoreDecimal(row.getCell(cell++))));
		currentRow.setAxis(stringValueParser(currentRow.getAxis(), 
				SiteSetupUtils.getString(row.getCell(cell++))));
		currentRow.setValue(stringValueParser(currentRow.getValue(), 
				SiteSetupUtils.getString(row.getCell(cell++))));
	}
	
	
	@SuppressWarnings("deprecation")
	private void update(ProductXlsRow currentRow, Row row) {
		int cell = 0;
		currentRow.setUpdate(stringValueParser(currentRow.getUpdate(), 
				SiteSetupUtils.getStringIgnoreDecimal(row.getCell(cell++))));
		currentRow.setName(stringValueParser(currentRow.getName(), 
				SiteSetupUtils.getString(row.getCell(cell++))));
		currentRow.setSection(stringValueParser(currentRow.getSection(), 
				SiteSetupUtils.getString(row.getCell(cell++))));
		currentRow.setPartNum(stringValueParser(currentRow.getPartNum(), 
				SiteSetupUtils.getString(row.getCell(cell++))));
		currentRow.setStock(stringValueParser(currentRow.getStock(), 
				SiteSetupUtils.getString(row.getCell(cell++))));
		currentRow.setPrice(stringValueParser(currentRow.getPrice(), 
				SiteSetupUtils.getString(row.getCell(cell++))));
		currentRow.setAlpha(stringValueParser(currentRow.getAlpha(), 
				SiteSetupUtils.getString(row.getCell(cell++))));
		currentRow.setBeta(stringValueParser(currentRow.getBeta(), 
				SiteSetupUtils.getString(row.getCell(cell++))));
	}
	
	@SuppressWarnings("deprecation")
	private void update(VariantXlsRow currentRow, Row row) {
		int cell = 0;
		currentRow.setUpdate(stringValueParser(currentRow.getUpdate(), 
				SiteSetupUtils.getStringIgnoreDecimal(row.getCell(cell++))));
		currentRow.setPartNum(stringValueParser(currentRow.getPartNum(), 
				SiteSetupUtils.getString(row.getCell(cell++))));
		currentRow.setStock(stringValueParser(currentRow.getStock(), 
				SiteSetupUtils.getString(row.getCell(cell++))));
		currentRow.setPrice(stringValueParser(currentRow.getPrice(), 
				SiteSetupUtils.getString(row.getCell(cell++))));
		currentRow.setAlpha(stringValueParser(currentRow.getAlpha(), 
				SiteSetupUtils.getString(row.getCell(cell++))));
		currentRow.setBeta(stringValueParser(currentRow.getBeta(), 
				SiteSetupUtils.getString(row.getCell(cell++))));
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
	
	private AxisValue getAxisValue(Map<String, AxisValue> cache, Long axisId, String value) {
		String axisValueKey = String.format("%s-%s", axisId, value);
		AxisValue av = cache.get(axisValueKey);
		if (av == null) {
			av = this.cmsService.getAxisValueService().get(axisId, value);
			if (av != null) {
				cache.put(axisValueKey, av);
			}
		}
		
		return av;
	}
	
	private Long pounds2pence(String poundsStr) {
		if (StringUtils.isNotBlank(poundsStr)) {
			Matcher m = CURRENCY_PATTERN.matcher(poundsStr);
			if (m.matches()) {
				Float f = Float.valueOf(poundsStr) * 100.0F;
				return f.longValue();
			}
		}
		return -1L;
	}
	
	private void tout(String t) {
		System.out.println(String.format("%s => %d", t, pounds2pence(t)));
	}
	
	public static void main(String[] args) {
		CommerceSetup c = new CommerceSetup();
		c.tout("25");
		c.tout("25.");
		c.tout("25.1");
		c.tout("25.12");
		c.tout("25.123");
		c.tout("25.126");
	}
}
