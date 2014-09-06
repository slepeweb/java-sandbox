package com.slepeweb.cms.setup;

import java.io.FileInputStream;
import java.io.InputStream;
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
import com.slepeweb.cms.bean.LinkName;
import com.slepeweb.cms.bean.LinkType;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.Template;
import com.slepeweb.cms.service.CmsService;
import com.slepeweb.cms.setup.SiteSetupStatistics.ResultType;
import com.slepeweb.cms.utils.LogUtil;

@Component
public class SiteSetup {
	private static Logger LOG = Logger.getLogger(SiteSetup.class);

	@Autowired private CmsService cmsService;
	private Site site;

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
		LOG.info("Link name updates :" + result.getNumLinkNameUpdates());
		LOG.info("Template updates  :" + result.getNumTemplateUpdates());
		LOG.info("Took " + sec + " secs");
		LOG.info("Finished");
	}


	private void readCsv(String filePath, SiteSetupStatistics stats) {
		InputStream is = null;
		POIFSFileSystem fs;
		HSSFWorkbook wb;

		// open spreadsheet
		try {
			is = new FileInputStream(filePath);
			fs = new POIFSFileSystem(is);
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

		if ((this.site = createSite(wb.getSheetAt(0).rowIterator(), stats)) != null) {
			createFields(wb.getSheetAt(1).rowIterator(), stats);
			createItemTypes(wb.getSheetAt(2).rowIterator(), stats);
			createLinkNames(wb.getSheetAt(3).rowIterator(), stats);
			createTemplates(wb.getSheetAt(4).rowIterator(), stats);
		}
		
		// close spreadsheet
		try {
			is.close();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Site createSite(Iterator<Row> rowIter, SiteSetupStatistics stats) {
		Row row;
		Site s = null;
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
							setShortname(SiteSetupUtils.getString(row.getCell(2))).
							setHostname(SiteSetupUtils.getString(row.getCell(3))).
							save();
					
					if (s.getId() != null) {
						stats.inc(ResultType.SITE_UPDATED);
						return s;
					}
				}
				else {
					LOG.debug(LogUtil.compose("Site is not updateable", s));
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
					LOG.debug(LogUtil.compose("Field is not updateable", f));
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
					LOG.debug(LogUtil.compose("Item type is not updateable", it));
				}
			}
		}
	}

	private void createLinkNames(Iterator<Row> rowIter, SiteSetupStatistics stats) {
		Row row;
		LinkType lt;
		LinkName ln;
		String firstCell, linkType, linkName;
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
				linkType = SiteSetupUtils.getString(row.getCell(1));
				linkName = SiteSetupUtils.getString(row.getCell(2));				
				lt = this.cmsService.getLinkTypeService().getLinkType(linkType);
				
				if (lt != null) {
					ln = this.cmsService.getLinkNameService().getLinkName(this.site.getId(), lt.getId(), linkName);
					
					if (ln == null || updateable) {
						ln = CmsBeanFactory.makeLinkName().
								setSiteId(this.site.getId()).
								setLinkTypeId(lt.getId()).
								setName(linkName).
								save();
						
						if (ln.getId() != null) {
							stats.inc(ResultType.LINKNAME_UPDATED);
						}
					}
					else {
						LOG.debug(LogUtil.compose("LinkName is not updateable", ln));
					}
				}
			}
		}
	}
	
	private void createTemplates(Iterator<Row> rowIter, SiteSetupStatistics stats) {
		Row row;
		Template t = null;
		boolean updateable = false;
		String templateName;
		ItemType it;

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
				it = this.cmsService.getItemTypeService().getItemType(SiteSetupUtils.getString(row.getCell(1)));
				
				if (it != null) {
					templateName = SiteSetupUtils.getString(row.getCell(2));
					t = this.cmsService.getTemplateService().getTemplate(this.site.getId(), templateName);
					if (t == null || updateable) {
						t = CmsBeanFactory.makeTemplate().
								setSiteId(this.site.getId()).
								setItemTypeId(it.getId()).
								setName(templateName).
								setForward(SiteSetupUtils.getString(row.getCell(3))).
								save();
						
						stats.inc(ResultType.TEMPLATE_UPDATED);
					}
					else {
						LOG.debug(LogUtil.compose("Template is not updateable", t));
					}
				}
			}
		}
	}
}
