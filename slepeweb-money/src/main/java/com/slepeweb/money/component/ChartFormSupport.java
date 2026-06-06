package com.slepeweb.money.component;

import java.awt.geom.Rectangle2D;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.slepeweb.common.util.JsonUtil;
import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Chart;
import com.slepeweb.money.bean.ChartData;
import com.slepeweb.money.bean.FlatTransaction;
import com.slepeweb.money.bean.Property;
import com.slepeweb.money.bean.SavedSearch;
import com.slepeweb.money.bean.SavedSearchOption;
import com.slepeweb.money.bean.solr.SolrParams;
import com.slepeweb.money.bean.solr.SolrResponse;
import com.slepeweb.money.service.SavedSearchService;
import com.slepeweb.money.service.SolrService4Money;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class ChartFormSupport {

	public static final String YEAR_RANGE_ATTR = "_yearRange";	
	public static final String FORM_VIEW = "chartForm";
	public static final String LIST_VIEW = "chartList";
	public static final String RESULTS_VIEW = "chartResults";	
	public static final int NUM_EMPTY_GROUPS = 2;
	
	private static Logger LOG = Logger.getLogger(ChartFormSupport.class);	
	
	@Autowired private FormSupport formSupport;
	@Autowired private SearchFormSupport searchFormSupport;
	@Autowired private SolrService4Money solrService4Money;
	@Autowired private SavedSearchService savedSearchService;

	private List<Integer> yearRange;
	
	private record ChartDataSupport(DefaultCategoryDataset ds, Map<String, ChartData> chartDataByLabel, 
			List<String> labels) {}
	
	public String executeSearches(Chart ch, HttpServletRequest req, ModelMap model) {
		model.addAttribute(YEAR_RANGE_ATTR, this.formSupport.getYearRange());
		
		// from Jan 1 in the current year to Dec 31
		LocalDate from = Util.today().withMonth(1).withDayOfMonth(1);
		LocalDate to = Util.today().withMonth(12).withDayOfMonth(31);
		int currentYear = from.getYear();
		
		DefaultCategoryDataset ds = new DefaultCategoryDataset();				
		List<Integer> years = new ArrayList<Integer>();
	    model.addAttribute("_years", years);
	    for (int i = ch.getFromYear(); i < (ch.getFromYear() + ch.getToYear()) && i <= currentYear; i++) {
	    	years.add(i);
	    }

	    List<String> labels = new ArrayList<String>();
	    model.addAttribute("_chartLabels", labels);
		Map<String, ChartData> chartDataByLabel = new HashMap<String, ChartData>();
	    model.addAttribute("_chartDataMap", chartDataByLabel);
	    ChartDataSupport supp = new ChartDataSupport(ds, chartDataByLabel, labels);
	    SavedSearch ss;
	    boolean missingEntity;
	    
		for (int year = ch.getFromYear(); year <= ch.getToYear() && year <= currentYear; year++) {
			from = from.withYear(year);
			to = to.withYear(year);
			
			for (Long ssid : ch.getSearchIdsAsList()) {
				ss = this.savedSearchService.get(ssid);
				if (ss == null) {
					LOG.error("Saved search id not recognised");
					continue;
				}
				
				SolrParams params = JsonUtil.fromJson(new TypeReference<SolrParams>() {}, ss.getJson());
				
				// Adjust parameters TODO: will need to account for even larger numbers of transactions
				params.setPageSize(2048);
				params.setFrom(Util.formatSimple(from));
				params.setTo(Util.formatSimple(to));
				missingEntity = this.searchFormSupport.convertId2Name(params);
				
				if (missingEntity) {
					model.addAttribute("_flasher", "failure|Check search definition for deleted data");
				}
				
				queryThenAggregateData(params, ss.getName(), year, supp);
			}
		}
	 
		JFreeChart chart = ChartFactory.createBarChart(
		         ch.getName(), "Years", "Amounts (£)",
		         ds,
		         PlotOrientation.VERTICAL, true, true, false);
		
		SVGGraphics2D svg2d = new SVGGraphics2D(1000, 600);
	    chart.draw(svg2d,new Rectangle2D.Double(0, 0, 1000, 600));
	    model.addAttribute("_chartSVG", svg2d.getSVGElement());
		
		return RESULTS_VIEW; 
	}
	
	private void queryThenAggregateData(SolrParams p, String label, int year, ChartDataSupport supp) {
		SolrResponse<FlatTransaction> resp = this.solrService4Money.query(p);
		long amount = 0;
		
		if ( ! supp.labels.contains(label)) {
			supp.labels.add(label);
		}
		
		ChartData chartData = supp.chartDataByLabel.get(label);
		
		if (chartData == null) {
			chartData = new ChartData().setLabel(label);
			supp.chartDataByLabel.put(label, chartData);
		}
		
		for (FlatTransaction ft : resp.getResults()) {
			amount += ft.getAmount();
		}
		
		supp.ds.addValue(Util.toPounds(amount), label, Integer.valueOf(year));
		chartData.getData().put(year, amount);
	}

	
	public int getYear(HttpServletRequest req, String formElementName, int dflt) {
		String yearStr = req.getParameter(formElementName);
		
		if (StringUtils.isNumeric(yearStr)) {
			return Integer.valueOf(yearStr).intValue();
		}
		
		return dflt;
	}
	
	public List<Integer> getYearRange() {
		if (this.yearRange != null) {
			return this.yearRange;
		}
		
		int thisYear = Util.today().getYear();
		this.yearRange = new ArrayList<Integer>();
		for (int i = 1995; i <= thisYear; i++) {
			this.yearRange.add(i);
		}
		
		return this.yearRange;
	}
	
	public String toJson(List<SavedSearchOption> options) {
		List<Property> props = new ArrayList<Property>(options.size());
		
		for (SavedSearchOption sso : options) {
			props.add(new Property(String.valueOf(sso.getSavedSearch().getId()), sso.getSavedSearch().getName()));
		}
		
		StringBuilder sb = new StringBuilder("[");
		for (Property p : props) {
			sb.append(String.format("{label:'%s', value:'%s'},", p.getValue(), p.getKey()));
		}
		sb.append("]");
		
		return sb.toString();
	}
	

}
