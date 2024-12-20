package com.slepeweb.cms.setup;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
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
import com.slepeweb.cms.bean.SiteType;
import com.slepeweb.cms.bean.Template;
import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.cms.service.CmsService;
import com.slepeweb.cms.service.SiteTypeService;
import com.slepeweb.cms.setup.SiteSetupStatistics.ResultType;
import com.slepeweb.cms.utils.LogUtil;

@Component
public class SiteSetup {
	private static Logger LOG = Logger.getLogger(SiteSetup.class);

	@Autowired private CmsService cmsService;
	private Map<String, Site> siteCache = new HashMap<String, Site>();

	public void load(String filePath) throws ResourceException {
		LOG.info(LogUtil.compose("Setting up site", filePath));
		SiteSetupStatistics result = new SiteSetupStatistics();

		StopWatch stopwatch = new StopWatch();
		stopwatch.start();

		if (filePath != null) {
			LOG.info("Reading/validating spreadsheet");
			processCsv(readCsv(filePath, result), result);
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

	private void processCsv(HSSFWorkbook wb, SiteSetupStatistics stats) throws ResourceException {
		if (wb != null) {
			HSSFSheet siteSheet = wb.getSheetAt(0);
			createSites(siteSheet.rowIterator(), stats);
			createFields(wb.getSheetAt(1).rowIterator(), stats);
			createItemTypes(wb.getSheetAt(2).rowIterator(), stats);
			createSiteTypes(siteSheet.rowIterator(), stats);
			
			if (this.siteCache.size() > 0) {
				createLinkNames(wb.getSheetAt(3).rowIterator(), stats);
				createTemplates(wb.getSheetAt(4).rowIterator(), stats);
			}
		}
	}

	/*
	 * TODO: NOTE: host column in spreadsheet is currently being ignored!
	 */
	private void createSites(Iterator<Row> rowIter, SiteSetupStatistics stats) throws ResourceException {
		Row row;
		Site s = null;
		String firstCell, name;
		boolean updateable = false, exists;
		
		while (rowIter.hasNext()) {
			row = rowIter.next();
			firstCell = SiteSetupUtils.getStringIgnoreDecimal(row.getCell(0));

			if (firstCell.startsWith("###")) {
				break;
			} else if (firstCell.startsWith("#") || StringUtils.isBlank(firstCell)) {
				continue;
			} else {
				stats.inc(ResultType.ROWS_PROCESSED);
				updateable = firstCell.equals("1");
				name = SiteSetupUtils.getStringIgnoreDecimal(row.getCell(1));
				s = this.cmsService.getSiteService().getSite(name);
				exists = s != null;
				
				if (! exists) {
					s = CmsBeanFactory.makeSite();
					LOG.info(String.format("Creating new site [%s]", name));
				}
				
				if (! exists || updateable) {
					s.
						setName(name).
						setShortname(SiteSetupUtils.getStringIgnoreDecimal(row.getCell(2))).
						setLanguage(SiteSetupUtils.getStringIgnoreDecimal(row.getCell(4))).
						setExtraLanguages(SiteSetupUtils.getStringIgnoreDecimal(row.getCell(5))).
						save();
					
					if (s.getId() != null) {
						LOG.info(String.format("Updated site [%s]", name));
						stats.inc(ResultType.SITE_UPDATED);
						this.siteCache.put(s.getShortname(), s);
					}
				}
				else {
					LOG.debug(LogUtil.compose("Site is not updateable", s));
				}
			}
		}
	}
	
	private void createSiteTypes(Iterator<Row> rowIter, SiteSetupStatistics stats) throws ResourceException {
		Row row;
		Site s = null;
		SiteType st;
		String firstCell, name, typeList;
		ItemType itype;
		boolean updateable = false;
		
		while (rowIter.hasNext()) {
			row = rowIter.next();
			firstCell = SiteSetupUtils.getStringIgnoreDecimal(row.getCell(0));

			if (firstCell.startsWith("###")) {
				break;
			} else if (firstCell.startsWith("#") || StringUtils.isBlank(firstCell)) {
				continue;
			} else {
				//stats.inc(ResultType.ROWS_PROCESSED);
				updateable = firstCell.equals("1");
				name = SiteSetupUtils.getStringIgnoreDecimal(row.getCell(1));
				typeList = SiteSetupUtils.getStringIgnoreDecimal(row.getCell(6));
				
				if (StringUtils.isNotBlank(typeList)) {
					s = this.cmsService.getSiteService().getSite(name);
					
					if (s != null && updateable) {
						SiteTypeService sts = this.cmsService.getSiteTypeService();
						
						// Delete all existing SiteType records from the db
						sts.delete(s.getId());
						
						// Add current SiteType records back in
						for (String type : typeList.split(",")) {
							itype = this.cmsService.getItemTypeService().getItemType(type.trim());
							
							if (itype != null) {
								st = CmsBeanFactory.makeSiteType().setSiteId(s.getId()).setType(itype).save();
							}
						}
					}
					else {
						LOG.debug(LogUtil.compose("Sitetype is not updateable", s));
					}
				}
			}
		}
	}
	
	private void createFields(Iterator<Row> rowIter, SiteSetupStatistics stats) {
		Row row;
		Field f;
		String variable;
		boolean updateable = false, exists;

		while (rowIter.hasNext()) {
			row = (Row) rowIter.next();
			String firstCell = SiteSetupUtils.getStringIgnoreDecimal(row.getCell(0));

			if (firstCell.startsWith("###")) {
				break;
			} else if (firstCell.startsWith("#") || StringUtils.isBlank(firstCell)) {
				continue;
			} else {
				stats.inc(ResultType.ROWS_PROCESSED);
				updateable = firstCell.equals("1");
				variable = SiteSetupUtils.getStringIgnoreDecimal(row.getCell(2));
				f = this.cmsService.getFieldService().getField(variable);
				exists = f != null;
				
				if (! exists) {
					f = CmsBeanFactory.makeField();
					LOG.info(String.format("Creating new field [%s]", variable));
				}
				
				if (! exists || updateable) {					
					f.
						setName(SiteSetupUtils.getStringIgnoreDecimal(row.getCell(1))).
						setVariable(SiteSetupUtils.getStringIgnoreDecimal(row.getCell(2))).
						setMultilingual(SiteSetupUtils.getStringIgnoreDecimal(row.getCell(3)).equals("1")).
						setType(FieldType.valueOf(SiteSetupUtils.getStringIgnoreDecimal(row.getCell(4)))).
						setSize(SiteSetupUtils.getInteger(row.getCell(5))).
						setHelp(SiteSetupUtils.getStringIgnoreDecimal(row.getCell(6))).
						setValidValues(SiteSetupUtils.getStringIgnoreDecimal(row.getCell(7))).
						setDefaultValue(SiteSetupUtils.getStringIgnoreDecimal(row.getCell(8))).
						save();
					
					LOG.info(String.format("Field saved [%s]", variable));
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
		boolean updateable = false, exists;
		String name;
		Field f;
		Map<String, Field> fieldCache = new HashMap<String, Field>();

		while (rowIter.hasNext()) {
			row = rowIter.next();
			String firstCell = SiteSetupUtils.getStringIgnoreDecimal(row.getCell(0));

			if (firstCell.startsWith("###")) {
				break;
			} else if (firstCell.startsWith("#") || StringUtils.isBlank(firstCell)) {
				continue;
			} else {
				stats.inc(ResultType.ROWS_PROCESSED);
				updateable = firstCell.equals("1");
				name = SiteSetupUtils.getStringIgnoreDecimal(row.getCell(1));
				it = this.cmsService.getItemTypeService().getItemType(name);
				exists = it != null;
				
				if (! exists) {
					it = CmsBeanFactory.makeItemType();
					LOG.info(String.format("Creating new item type [%s]", name));
				}
				
				if (! exists || updateable) {
					it.
						setName(name).
						setMimeType(SiteSetupUtils.getStringIgnoreDecimal(row.getCell(2))).
						setPrivateCache(SiteSetupUtils.getLong(row.getCell(4))).
						setPublicCache(SiteSetupUtils.getLong(row.getCell(5))).
						save();
					
					if (it.getId() != null) {
						LOG.info(String.format("Item type saved [%s]", name));
						stats.inc(ResultType.ITEMTYPE_UPDATED);
					}
				}
						
				if (it.getId() != null) {
					
					// NOTE: This method does not remove fields from an item type - do this manually.
					long count = 0;
					for (String variable : SiteSetupUtils.getStringIgnoreDecimal(row.getCell(3)).split(", ")) {
						if (StringUtils.isBlank(variable)) {
							continue;
						}
						
						variable = variable.trim();
						f = fieldCache.get(variable);
						if (f == null) {
							f = this.cmsService.getFieldService().getField(variable);
							if (f != null) {
								fieldCache.put(variable, f);
							}
							else {
								LOG.error(LogUtil.compose("Field does not exist", variable));
							}
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
				else {
					LOG.debug(LogUtil.compose("Item type not saved", it));
				}
			}
		}
	}

	private void createLinkNames(Iterator<Row> rowIter, SiteSetupStatistics stats) {
		Row row;
		LinkType lt;
		LinkName ln;
		String firstCell, linkType, linkNameStr;
		Site s;
		boolean updateable = false, exists;
		
		while (rowIter.hasNext()) {
			row = rowIter.next();
			firstCell = SiteSetupUtils.getStringIgnoreDecimal(row.getCell(0));

			if (firstCell.startsWith("###")) {
				break;
			} else if (firstCell.startsWith("#") || StringUtils.isBlank(firstCell)) {
				continue;
			} else {
				stats.inc(ResultType.ROWS_PROCESSED);
				updateable = firstCell.equals("1");
				linkType = SiteSetupUtils.getStringIgnoreDecimal(row.getCell(1));
				linkNameStr = SiteSetupUtils.getStringIgnoreDecimal(row.getCell(2));				
				lt = this.cmsService.getLinkTypeService().getLinkType(linkType);
				s = this.siteCache.get(SiteSetupUtils.getStringIgnoreDecimal(row.getCell(3)));
				
				if (s != null && lt != null) {
					for (String linkName : linkNameStr.split(", ")) {
						
						ln = this.cmsService.getLinkNameService().getLinkName(s.getId(), lt.getId(), linkName);
						exists = ln != null;
						
						if (! exists) {
							ln = CmsBeanFactory.makeLinkName();
							LOG.info(String.format("Creating new linkname [%s]", linkName));
						}
						
						if (! exists || updateable) {
							ln.
								setSiteId(s.getId()).
								setLinkTypeId(lt.getId()).
								setName(linkName).
								save();
							
							if (ln.getId() != null) {
								LOG.info(String.format("Updated linkname [%s]", linkName));
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
	}
	
	private void createTemplates(Iterator<Row> rowIter, SiteSetupStatistics stats) {
		Row row;
		Template t = null;
		Site s;
		boolean updateable = false, exists;
		String sitename, templateName, itemTypeName;
		ItemType it;

		while (rowIter.hasNext()) {
			row = rowIter.next();
			String firstCell = SiteSetupUtils.getStringIgnoreDecimal(row.getCell(0));

			if (firstCell.startsWith("###")) {
				break;
			} else if (firstCell.startsWith("#") || StringUtils.isBlank(firstCell)) {
				continue;
			} else {
				stats.inc(ResultType.ROWS_PROCESSED);
				updateable = firstCell.equals("1");
				itemTypeName = SiteSetupUtils.getStringIgnoreDecimal(row.getCell(1));
				it = this.cmsService.getItemTypeService().getItemType(itemTypeName);
				
				if (it != null) {
					templateName = SiteSetupUtils.getStringIgnoreDecimal(row.getCell(2));
					sitename = SiteSetupUtils.getStringIgnoreDecimal(row.getCell(4));
					s = this.siteCache.get(sitename);
					
					if (s != null) {
						t = this.cmsService.getTemplateService().getTemplate(s.getId(), templateName);
						exists = t != null;
						
						if (! exists) {
							t = CmsBeanFactory.makeTemplate();
							LOG.info(String.format("Creating new template [%s]", templateName));
						}
						
						if (! exists || updateable) {
							t.
								setSiteId(s.getId()).
								setItemTypeId(it.getId()).
								setName(templateName).
								setController(SiteSetupUtils.getStringIgnoreDecimal(row.getCell(3))).
								save();
							
							LOG.info(String.format("Updated template [%s]", templateName));
							stats.inc(ResultType.TEMPLATE_UPDATED);
						}
						else {
							LOG.debug(LogUtil.compose("Template is not updateable", t));
						}
					}
					else {
						LOG.debug(LogUtil.compose("Site shortname is not valid", sitename));
					}
				}
				else {
					LOG.warn(LogUtil.compose("No such item type", itemTypeName));
				}
			}
		}
	}
}
