package com.slepeweb.site.pho.control;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.slepeweb.cms.bean.Dateish;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Media;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.SolrDocument4Cms;
import com.slepeweb.cms.bean.TagList;
import com.slepeweb.cms.constant.AttrName;
import com.slepeweb.cms.constant.FieldName;
import com.slepeweb.cms.constant.ItemTypeName;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.cms.service.TagService;
import com.slepeweb.common.solr.bean.SolrConfig;
import com.slepeweb.common.solr.bean.SolrPager;
import com.slepeweb.common.solr.bean.SolrResponse;
import com.slepeweb.site.control.BaseController;
import com.slepeweb.site.model.Page;
import com.slepeweb.site.pho.bean.PhoCookieValues;
import com.slepeweb.site.pho.bean.PhoMetadata;
import com.slepeweb.site.pho.bean.SolrParams4Pho;
import com.slepeweb.site.pho.service.PhoCookieService;
import com.slepeweb.site.pho.service.SolrService4Photos;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/spring/pho")
public class PhotosPageController extends BaseController {
	
	@Autowired private SolrService4Photos solrService4Photos;
	@Autowired private PhoCookieService phoCookieService;
	@Autowired private TagService tagService;
	@Autowired private ItemService itemService;
	
	@RequestMapping(value="/homepage")	
	public String homepage(
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			@ModelAttribute(AttrName.SITE) Site site, 
			HttpServletRequest req,
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "homepage", model);
		model.addAttribute("_latestCookieValues", this.phoCookieService.getCookieValues(req));
		
		TagList tagList = this.tagService.getTagCount4Site(i.getSite().getId(), 50);
		tagList.analyze();
		model.addAttribute("_toptags", tagList);
		model.addAttribute("_nowYear", Calendar.getInstance().get(Calendar.YEAR));

		return page.getView();
	}

	@RequestMapping(value="/collage")	
	public String collage(
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			@ModelAttribute(AttrName.SITE) Site site, 
			HttpServletRequest req,
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "collage", model);
		model.addAttribute("_photoList", new int[][] {
			
			// David
			/*
				{4206, 4002, 3339, 2265, 3994, 2264}, 
				{3338, 2086, 2263, 2323, 2111}, 
				{3326, 2528, 2485, 2255, 2526}, 
				{4187, 2459, 2324, 3967, 2356, 2454},
				{4208, 2080, 4005, 2455, 2468},
				{3328, 0, 2246, 3351}
				*/
				
			// John
			{3315, 2132, 3254, 3371},
			{3258, 3391, 3368, 3966},
			{3253, 3260, 3314, 4354, 3255},
			{2475, 3257, 4324, 2270},
			{2242, 0, 3382}
		});
		
		model.addAttribute("_photoMargin", new int[] {45, 75, 0, 30, 200, 0});
		return page.getView();
	}

	@RequestMapping(value="/search", method=RequestMethod.POST)	
	public String searchPost(
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			HttpServletRequest request,
			HttpServletResponse response,
			ModelMap model) {	
				
		return search(i, shortSitename, request, response, model);
	}
	
	@RequestMapping(value="/search/get", method=RequestMethod.GET)	
	public String searchGet(
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			HttpServletRequest request,
			HttpServletResponse response,
			ModelMap model) {	

		return search(i, i.getSite().getShortname(), request, response, model);
	}
	
	@RequestMapping(value="/search/related", method=RequestMethod.GET)	
	public String related(
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			@RequestParam(value="id", required=true) Long id,
			HttpServletRequest request,
			ModelMap model) {	
		
		Item parent = this.itemService.getItem(id);
		List<SolrDocument4Cms> docs = new ArrayList<SolrDocument4Cms>();
		
		if (parent != null) {
			List<String> targets = Arrays.asList(new String[] {
					ItemTypeName.PHOTO_JPG, ItemTypeName.MOVIE_MP4
			});
			
			for (Item r : parent.getRelatedItems()) {
				if (targets.contains(r.getType().getName())) {
					docs.add(new SolrDocument4Cms(r));
				}
			}
		}

		Page page = getStandardPage(i, shortSitename, "relations", model);
		page.setTitle(i.getName());
		
		SolrResponse<SolrDocument4Cms> response = new SolrResponse<SolrDocument4Cms>();
		response.setResults(docs);
		response.setTotalHits(docs.size());
			
		response.setPager(new SolrPager<SolrDocument4Cms>(
		response.getTotalHits(), 10, 1));		
			
		model.addAttribute("_search", response);
		return page.getView();
	}
	
	@RequestMapping(value="/export/csv", method=RequestMethod.GET)	
	public String export2csv (
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			HttpServletRequest req,
			ModelMap model) {
		
		String attr = "_csv";
		@SuppressWarnings("unchecked")
		List<PhoMetadata> csvTable = (List<PhoMetadata>) req.getSession().getAttribute(attr);
		String refresh = req.getParameter("refresh");
		
		if (csvTable == null || refresh != null) {
			
			csvTable = new ArrayList<PhoMetadata>(1313);
			
			try {
				csvTable.addAll(toCsv(this.itemService.getItemsByType(i.getSite().getId(), ItemTypeName.PHOTO_JPG)));
				csvTable.addAll(toCsv(this.itemService.getItemsByType(i.getSite().getId(), ItemTypeName.MOVIE_MP4)));
				
				Collections.sort(csvTable, new Comparator<PhoMetadata>() {
					@Override
					public int compare(PhoMetadata o1, PhoMetadata o2) {
						Dateish d1 = new Dateish(o1.getDateishStr());
						Dateish d2 = new Dateish(o2.getDateishStr());
						return d2.toSortableString().compareTo(d1.toSortableString());
					}				
				});
				
				PrintWriter pw = new PrintWriter(new FileWriter("/tmp/photos.csv"));
				pw.println(StringUtils.join(PhoMetadata.getCsvHeader()));
				
				for (PhoMetadata d : csvTable) {
					pw.println(d.getCsvRow());
				}
				
				req.getSession().setAttribute(attr, csvTable);
				pw.close();
			}
			catch (IOException e) {
				System.out.print("CSV export failed: " + e.getMessage());
			}
		}
		
		Page page = getStandardPage(i, shortSitename, "csvExport", model);
		model.addAttribute(attr, csvTable);
		model.addAttribute("_header", PhoMetadata.getHeaderArray());
		return page.getView();
	}
	
	private List<PhoMetadata> toCsv(List<Item> list) throws IOException {
		List<PhoMetadata> csvTable = new ArrayList<PhoMetadata>();
		PhoMetadata meta;
		
		Media m;
		for (Item i : list) {
			m = i.getMedia();
			if (m == null) {
				continue;
			}
			
			meta = new PhoMetadata().
					setItemPath(i.getPath()).
					setMediaFilePath(String.format("%s/%s", m.getFolder(), m.getRepositoryFileName())).
					setMediaType(i.getType().isImage() ? "Image" : "Video").
					setTags(i.getTagsAsString()).
					setTitle(i.getTitle()).
					setTeaser(i.getFieldValue(FieldName.TEASER)).
					setDateishStr(i.getFieldValue(FieldName.DATEISH));

			csvTable.add(meta);
		}
		
		return csvTable;
	}
	
	private String search(
			Item i, 
			String shortSitename,
			HttpServletRequest request,
			HttpServletResponse response,
			ModelMap model) {
		
		Page page = getStandardPage(i, shortSitename, "search", model);
		page.setTitle(i.getName());
		
		PhoCookieValues formData = new PhoCookieValues(request);
		this.phoCookieService.saveCookie(formData, response);
		
		SolrParams4Pho params = new SolrParams4Pho(i, new SolrConfig());		
		params.setSearchText(formData.getText());
		params.
			setFrom(formData.getFrom()).
			setTo(formData.getTo());
		
		params.
			setPageSize(24).
			setPageNum(request.getParameter("page"));
		
		model.addAttribute("_params", params);
		model.addAttribute("_search", this.solrService4Photos.query(params));
		
		return page.getView();
	}
}
