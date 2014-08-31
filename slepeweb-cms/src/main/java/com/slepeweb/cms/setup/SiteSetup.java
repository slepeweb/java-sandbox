package com.slepeweb.cms.setup;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slepeweb.cms.bean.CmsBeanFactory;
import com.slepeweb.cms.bean.Field;
import com.slepeweb.cms.bean.Field.FieldType;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.Template;
import com.slepeweb.cms.service.CmsService;
import com.slepeweb.cms.setup.SiteSetupStatistics.ResultType;
import com.slepeweb.cms.utils.LogUtil;

@Component
public class SiteSetup {
	private static Logger LOG = Logger.getLogger(SiteSetup.class);

	@Autowired private CmsService cmsService;

	public void load(String filePath) {
		LOG.info(LogUtil.compose("Setting up site", filePath));
		SiteSetupStatistics result = new SiteSetupStatistics();

		StopWatch stopwatch = new StopWatch();
		stopwatch.start();

		if (filePath != null) {
			LOG.info("Reading/validating spreadsheet");
			readCsv(filePath, result);
		}

		finish(result, stopwatch);
	}

	private void finish(SiteSetupStatistics result, StopWatch stopwatch) {
		stopwatch.stop();
		long sec = stopwatch.getTime() / 1000;

		LOG.info("Rows processed    :" + result.getNumRowsProcessed());
		LOG.info("XLS errors        :" + result.getNumXlsErrors());
		LOG.info("XLS warnings      :" + result.getNumXlsWarnings());
		LOG.info("Site updates      :" + result.getNumSiteUpdates());
		LOG.info("Field updates     :" + result.getNumFieldUpdates());
		LOG.info("Item type updates :" + result.getNumItemTypeUpdates());
		LOG.info("Template updates  :" + result.getNumTemplateUpdates());
		LOG.info("Took " + sec + " secs");
		LOG.info("Finished");
	}


	private void readCsv(String filePath, SiteSetupStatistics stats) {
		POIFSFileSystem fs;
		HSSFWorkbook wb;

		// open spreadsheet
		try {
			fs = new POIFSFileSystem(new FileInputStream(filePath));
		} catch (Exception e) {
			LOG.error("Failed to open spreadsheet", e);
			return;
		}

		// open workbook
		try {
			wb = new HSSFWorkbook(fs);
		} catch (Exception e) {
			LOG.error("Failed to open workbook", e);
			return;
		}

		createSite(wb.getSheetAt(0).rowIterator(), stats);
		createFields(wb.getSheetAt(1).rowIterator(), stats);
		createItemTypes(wb.getSheetAt(2).rowIterator(), stats);
		createTemplates(wb.getSheetAt(3).rowIterator(), stats);
	}

	private Site createSite(Iterator<Row> rowIter, SiteSetupStatistics stats) {
		Row row;
		Site s;
		String firstCell, name;
		boolean updateable = false;
		
		while (rowIter.hasNext()) {
			row = rowIter.next();
			firstCell = SiteSetupUtils.getString(row.getCell(0));

			if (firstCell.startsWith("###")) {
				break;
			} else if (firstCell.startsWith("#") || StringUtils.isBlank(firstCell)) {
				continue;
			} else {
				stats.inc(ResultType.ROWS_PROCESSED);
				updateable = firstCell.equals("1");
				name = SiteSetupUtils.getString(row.getCell(1));
				s = this.cmsService.getSiteService().getSite(name);
				
				if (s == null || updateable) {
					s = CmsBeanFactory.makeSite().
							setName(name).
							setHostname(SiteSetupUtils.getString(row.getCell(3))).
							save();
					
					if (s.getId() != null) {
						stats.inc(ResultType.SITE_UPDATED);
						return s;
					}
				}
				else {
					LOG.info(LogUtil.compose("Site is not updateable", s));
				}
			}
		}
		
		return null;
	}
	
	private void createFields(Iterator<Row> rowIter, SiteSetupStatistics stats) {
		Row row;
		Field f;
		String variable;
		boolean updateable = false;

		while (rowIter.hasNext()) {
			row = (Row) rowIter.next();
			String firstCell = SiteSetupUtils.getString(row.getCell(0));

			if (firstCell.startsWith("###")) {
				break;
			} else if (firstCell.startsWith("#") || StringUtils.isBlank(firstCell)) {
				continue;
			} else {
				stats.inc(ResultType.ROWS_PROCESSED);
				updateable = firstCell.equals("1");
				variable = SiteSetupUtils.getString(row.getCell(2));
				f = this.cmsService.getFieldService().getField(variable);
				if (f == null || updateable) {
					f = CmsBeanFactory.makeField().
							setName(SiteSetupUtils.getString(row.getCell(1))).
							setVariable(SiteSetupUtils.getString(row.getCell(2))).
							setType(FieldType.valueOf(SiteSetupUtils.getString(row.getCell(3)))).
							setSize(SiteSetupUtils.getInteger(row.getCell(4))).
							setHelp(SiteSetupUtils.getString(row.getCell(5))).
							setValidValues(SiteSetupUtils.getString(row.getCell(6))).
							setDefaultValue(SiteSetupUtils.getString(row.getCell(7))).
							save();
					
					stats.inc(ResultType.FIELD_UPDATED);
				}
				else {
					LOG.info(LogUtil.compose("Field is not updateable", f));
				}
			}
		}
	}

	private void createItemTypes(Iterator<Row> rowIter, SiteSetupStatistics stats) {
		Row row;
		ItemType it = null;
		boolean updateable = false;
		String name;
		Field f;
		Map<String, Field> fieldCache = new HashMap<String, Field>();

		while (rowIter.hasNext()) {
			row = rowIter.next();
			String firstCell = SiteSetupUtils.getString(row.getCell(0));

			if (firstCell.startsWith("###")) {
				break;
			} else if (firstCell.startsWith("#") || StringUtils.isBlank(firstCell)) {
				continue;
			} else {
				stats.inc(ResultType.ROWS_PROCESSED);
				updateable = firstCell.equals("1");
				name = SiteSetupUtils.getString(row.getCell(1));
				it = this.cmsService.getItemTypeService().getItemType(name);
				if (it == null || updateable) {
					it = CmsBeanFactory.makeItemType().
							setName(name).
							setMimeType(SiteSetupUtils.getString(row.getCell(2))).
							save();
					
					if (it.getId() != null) {
						stats.inc(ResultType.ITEMTYPE_UPDATED);
						
						// NOTE: This method does not remove fields from an item type - do this manually.
						long count = 0;
						for (String variable : SiteSetupUtils.getString(row.getCell(3)).split(", ")) {
							variable = variable.trim();
							f = fieldCache.get(variable);
							if (f == null) {
								f = this.cmsService.getFieldService().getField(variable);
								fieldCache.put(variable, f);
							}
							
							if (f != null) {
								CmsBeanFactory.makeFieldForType().
										setField(f).
										setTypeId(it.getId()).
										setOrdering(count++).
										save();
							}
						}
					}
				}
				else {
					LOG.info(LogUtil.compose("Item type is not updateable", it));
				}
			}
		}
	}

	private void createTemplates(Iterator<Row> rowIter, SiteSetupStatistics stats) {
		Row row;
		Template t = null;
		boolean updateable = false;
		String siteName, itemTypeName, templateName;
		Site s;
		ItemType it;
		Long siteId, itemTypeId;
		Map<String, Long> siteCache = new HashMap<String, Long>();
		Map<String, Long> itemTypeCache = new HashMap<String, Long>();

		while (rowIter.hasNext()) {
			row = rowIter.next();
			String firstCell = SiteSetupUtils.getString(row.getCell(0));

			if (firstCell.startsWith("###")) {
				break;
			} else if (firstCell.startsWith("#") || StringUtils.isBlank(firstCell)) {
				continue;
			} else {
				stats.inc(ResultType.ROWS_PROCESSED);
				updateable = firstCell.equals("1");
				
				siteName = SiteSetupUtils.getString(row.getCell(1));
				siteId = siteCache.get(siteName);
				if (siteId == null) {
					s = this.cmsService.getSiteService().getSite(siteName);
					if (s != null) {
						siteId = s.getId();
						siteCache.put(siteName, siteId);
					}
					else {
						LOG.error(LogUtil.compose("Failed to recognise site name; template ignored", siteName, row.getRowNum()));
						stats.inc(ResultType.XLS_ERROR);
						continue;
					}
				}
				
				itemTypeName = SiteSetupUtils.getString(row.getCell(2));
				itemTypeId = itemTypeCache.get(itemTypeName);
				if (itemTypeId == null) {
					it = this.cmsService.getItemTypeService().getItemType(itemTypeName);
					if (it != null) {
						itemTypeId = it.getId();
						itemTypeCache.put(itemTypeName, itemTypeId);
					}
					else {
						LOG.error(LogUtil.compose("Failed to recognise item type name; template ignored", itemTypeName, row.getRowNum()));
						stats.inc(ResultType.XLS_ERROR);
						continue;
					}
				}
				
				if (siteId != null && itemTypeId != null) {
					templateName = SiteSetupUtils.getString(row.getCell(3));
					t = this.cmsService.getTemplateService().getTemplate(siteId, templateName);
					if (t == null || updateable) {
						t = CmsBeanFactory.makeTemplate().
								setSiteId(siteId).
								setItemTypeId(itemTypeId).
								setName(templateName).
								setForward(SiteSetupUtils.getString(row.getCell(4))).
								save();
						
						stats.inc(ResultType.TEMPLATE_UPDATED);
					}
					else {
						LOG.info(LogUtil.compose("Template is not updateable", t));
					}
				}
			}
		}
	}
}
