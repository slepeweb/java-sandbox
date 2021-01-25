package com.slepeweb.carsearch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.slepeweb.carsearch.VehicleBean.Status;

public class Searcher {
	private static Logger LOG = Logger.getLogger(Searcher.class);
	private static int MILLIS_IN_DAY = 24 * 60 * 60000;
	private static String LOG_FOLDER = "/home/george/cars";
	
	private static Pattern REG_PATTERN = Pattern.compile("(\\d{4}) \\((\\d{2}) reg\\)");
	private static Pattern BODYSTYLE_PATTERN = Pattern.compile("(Saloon|Hatchback|Estate)", 
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern MILES_PATTERN = Pattern.compile("(\\d{1,3},)?(\\d{2,3}) miles");
	private static Pattern TRANSMISSION_PATTERN = Pattern.compile("(Manual|Automatic)");
	private static Pattern ENGINE_PATTERN = Pattern.compile("(\\d\\.\\d)L");
	private static Pattern POWER_PATTERN = Pattern.compile("(\\d{2,3})PS");
	private static Pattern PRICE_PATTERN = Pattern.compile("£(\\d+,\\d{3})");
	private static Pattern FUEL_PATTERN = Pattern.compile("(Petrol|Diesel|Hybrid - Petrol/Electric)");
	private static Pattern HREF_PATTERN = Pattern.compile("/classified/advert/(\\d+)\\?.*");
	private static Pattern TOTAL_FOUND_PATTERN = Pattern.compile("^(.*?) cars found");
	private static Pattern SELLER_PATTERN = Pattern.compile("^(\\w+) seller .*");
	
	private SearchAgent[] agents;
	
	public static void main(String[] args) {
		LOG.info("\n\n\n");

		SearchAgent avensis = new SearchAgent("Avensis", "",
		"https://www.autotrader.co.uk/car-search?postcode=e63qj&radius=50&make=TOYOTA&model=AVENSIS&include-delivery-option=on&fuel-type=Diesel&body-type=Saloon",
		"avensis-");		
		LOG.info(String.format("Search url: [%s]", avensis.getCriteria().getUrl()));
		
		SearchAgent i30 = new SearchAgent("Hyundai", "SE,DCT",
		"https://www.autotrader.co.uk/car-search?sort=distance&postcode=ex109nn&radius=100&make=HYUNDAI&model=IONIQ&include-delivery-option=on&fuel-type=Hybrid%20%E2%80%93%20Petrol%2FElectric",
		"i30-");
		LOG.info(String.format("Search url: [%s]", i30.getCriteria().getUrl()));
		
		new Searcher( new SearchAgent[] {avensis}).execute();
		
		LOG.info("Finished!\n\n\n");
	}
	
	public Searcher(SearchAgent[] agents) {
		this.agents = agents;
	}
	
	private void execute() {
		for (SearchAgent agent : this.agents) {
			// Load previous search results from file
			agent.setPrevious(getSerializedModel(agent.getSerializedFilePrefix()));
			
			if (agent.getPrevious() == null) {
				agent.setPrevious(new SearchResults());
			}
			
			// Re-build data map from old results
			agent.getPrevious().mapVehicles();
			
			Date now = new Date();
			
			try {
				// Scrape the first page
				scrape(agent, 1, 0, now);
				int numPages = agent.getResults().getNumPages();
				
				// Scrape remaining pages
				for (int i = 2; i < numPages; i++) {
					scrape(agent, i, numPages, now);
				}
				
				// Carry forward old search results
				retainExpiredVehicles(agent);
				
				// Save combined results to file
				serializeModel(agent, now);
				
				// Output combined results to CSV (tab-delimited)
				report(agent, now);
			}
			catch (Exception e) {
				LOG.error("Search failure", e);
			}
		}
	}
	
	private SearchResults getSerializedModel(final String filePrefix) {
		try {
			File tmp = new File(LOG_FOLDER);
			File[] files = tmp.listFiles(new FilenameFilter(){
				public boolean accept(File dir, String name) {
					return name.startsWith(filePrefix) && name.endsWith(".ser");
				}});
			
			if (files.length > 0) {
				File target = files[files.length - 1];
				FileInputStream fileIn = new FileInputStream(target);
				ObjectInputStream in = new ObjectInputStream(fileIn);
				SearchResults results = (SearchResults) in.readObject();
				in.close();
				fileIn.close();
				LOG.info(String.format("Model successfully de-serialized [%s]", target.getAbsolutePath()));
				return results;
			}
			else {
				LOG.info("No previous model data available");
			}
		} catch (Exception c) {
			LOG.error("Model not found", c);
		}
		
		return null;
	}
	
	private void serializeModel(SearchAgent agent, Date now) {
		try {
			String fileName = new StringBuilder(
					LOG_FOLDER).append("/").
					append(agent.getSerializedFilePrefix()).
					append(now.getTime()).
					append(".ser").
					toString();
			
			FileOutputStream fileOut = new FileOutputStream(fileName);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(agent.getResults());
			out.close();
			fileOut.close();
			LOG.info(String.format("Serialized data is saved [%s]", fileName));
		} catch (IOException i) {
			LOG.error("Failed to serialize the model", i);
		}
	}
	
	private void retainExpiredVehicles(SearchAgent agent) {
		SearchResults newModel = agent.getResults();
		List<VehicleBean> oldList = agent.getPrevious().getVehicles();
		List<VehicleBean> newList = agent.getResults().getVehicles();
		
		for (VehicleBean v : oldList) {
			if (v.getStatus() == Status.EXPIRED) {
				newList.add(v);
			}
			else if (newModel.getVehicleById().get(v.getId()) == null) {
				v.setStatus(Status.EXPIRED);
				newList.add(v);
				newModel.incNumExpired();
			}
		}
	}
	
	private void scrape(SearchAgent agent, int pageNum, int numPages, Date now) throws Exception {
		
		LOG.debug(String.format("Getting page [%d]", pageNum));
		String baseUrl = agent.getCriteria().getUrl(); 
		SearchResults oldModel = agent.getPrevious();
		SearchResults newModel = agent.getResults();
		
		String url = baseUrl;
		if (pageNum > 1) {
			url += "&page=" + pageNum;
		}
		
		Document doc = getDocument(url);
		VehicleBean v;
		Element anchor;
		
		if (doc != null) {
			for (Element searchResult : doc.getElementsByClass("search-page__result")) {
				newModel.incNumProcessed();
				v = new VehicleBean();
				
				v.setId(searchResult.attr("id"));
				if (StringUtils.isBlank(v.getId())) {
					LOG.warn("No ID for vehicle - probably an advert");
					continue;
				}
				
				if (agent.getResults().getVehicleById().get(v.getId()) != null) {
					LOG.debug("Duplicate vehicle id");
					continue;
				}
				
				anchor = searchResult.select("a").last();
				if (anchor != null) {
					v.setHref(anchor.attr("href"));
				}
				
				if (! scrapeProductCard(v, searchResult)) {
					LOG.warn(String.format("Vehicle information not available [%s]", v));
					continue;
				}
				
				if (! scrapePrice(v, searchResult)) {
					LOG.warn(String.format("Vehicle price not available [%s]", v));
					continue;
				}
				
				scrapeSellerInfo(v, searchResult);
								
				newModel.addVehicle(v);
				setVehicleStatus(v, now, oldModel, newModel);
			}
			
			// How many pages can we scrape?
			if (pageNum == 1) {
				newModel.setNumPages(getNumPages(doc));
			}
		}
	}
	
	private boolean scrapeProductCard(VehicleBean v, Element searchResult) {
		Element productCardContent = searchResult.select("div.product-card-content").first();
		
		if (productCardContent == null) {
			LOG.warn(String.format("Missing section (section.product-card-content) [id=%s]", v.getId()));
			return false;
		}
		
		Element productCardDetails = productCardContent.select("section.product-card-details").first();
		
		if (productCardDetails == null) {
			LOG.warn(String.format("Missing section (section.product-card-details) [id=%s]", v.getId()));
			return false;
		}
		
		Element ele = productCardDetails.select("h3").first();
		
		if (ele == null) {
			LOG.warn(String.format("Missing vehicle description [id=%s]", v.getId()));
			return false;
		}
		
		//v.setHref(anchor.attr("href"));
		v.setTitle(ele.ownText());
		LOG.debug(v.getTitle());
		
		ele = productCardDetails.select("p.product-card-details__attention-grabber").first();
		
		if (ele != null) {
			v.setFeatures(ele.ownText());
		}
		
		/*
		 * TODO: Is this functionality actioned elsewhere?
		 * 
		if (! agent.titleMatchesTrimFilter(v.getTitle().toLowerCase())) {
			LOG.info(String.format("No trim match on vehicle heading [%s]", v.getTitle()));
			continue;
		}
		*/
		
		scrapeKeySpecs(v, productCardDetails);		
		return true;
	}
	
	private void scrapeKeySpecs(VehicleBean v, Element productCardDetails) {
		Matcher m;
		boolean doRegistration, doBodyStyle, doMileage, doTransmission, doEngineSize, doPower, doFuel;
		Element specs = productCardDetails.select("ul.listing-key-specs").first();
		
		if (specs != null) {
			doRegistration = doMileage = doBodyStyle = doTransmission = 
					doEngineSize = doPower = doFuel = true;
			
			for (Element spec : specs.select("li")) {
				if (doRegistration) {
					m = REG_PATTERN.matcher(spec.ownText());
					if (m.matches()) {
						v.setYear(m.group(1));
						v.setRegistration(m.group(2));
						doRegistration = false;
						continue;
					}
				}
				
				if (doBodyStyle) {
					m = BODYSTYLE_PATTERN.matcher(spec.ownText());
					if (m.matches()) {
						v.setBodystyle(m.group(1));
						doBodyStyle = false;
						continue;
					}
				}

				if (doMileage) {
					m = MILES_PATTERN.matcher(spec.ownText().trim());
					if (m.matches()) {
						if (m.group(1) != null && ! m.group(1).equals("null")) {
							v.setMileage((m.group(1) + m.group(2)).replaceAll(",", ""));
						}
						else {
							v.setMileage(m.group(2));
						}
						doMileage = false;
						continue;
					}
				}
				
				if (doTransmission) {
					m = TRANSMISSION_PATTERN.matcher(spec.ownText());
					if (m.matches()) {
						v.setTransmission(m.group(1));
						doTransmission = false;
						continue;
					}
				}
				
				if (doEngineSize) {
					m = ENGINE_PATTERN.matcher(spec.ownText());
					if (m.matches()) {
						v.setEngine(m.group(1));
						doEngineSize = false;
						continue;
					}
				}
				
				if (doPower) {
					m = POWER_PATTERN.matcher(spec.ownText());
					if (m.matches()) {
						v.setPower(m.group(1));
						doPower = false;
						continue;
					}
				}
				
				if (doFuel) {
					m = FUEL_PATTERN.matcher(spec.ownText());
					if (m.matches()) {
						v.setFuel(m.group(1));
						doFuel = false;
						continue;
					}
				}
			}
		}
	}
	
	private void scrapeSellerInfo(VehicleBean v, Element searchResult) {
		Element sellerInfo = searchResult.select("div.product-card-seller__info").first();
		if (sellerInfo != null) {
			Element ele = sellerInfo.select("div.product-card-seller__seller-type").first();
			if (ele != null) {
				Matcher m = SELLER_PATTERN.matcher(ele.ownText().trim());
				if (m.matches()) {
					v.setSellerType(m.group(1));
				}
			}
			
			ele = sellerInfo.select("span.seller-town").first();
			if (ele != null) {
				v.setLocation(ele.ownText());
			}
		}
	}
	
	private boolean scrapePrice(VehicleBean v, Element searchResult) {
		Element priceContainer = searchResult.select("div.product-card-pricing__price span").first();
		if (priceContainer != null) {
			Matcher m = PRICE_PATTERN.matcher(priceContainer.ownText());
			if (m.matches()) {
				v.setPrice(m.group(1).replace(",", ""));
				return true;
			}
		}
		return false;
	}
	
	private void setVehicleStatus(VehicleBean v, Date now, SearchResults oldModel, SearchResults newModel) {
		// Do we already know about this vehicle?
		VehicleBean target = oldModel.getVehicleById().get(v.getId());				
		
		if (target == null) {
			// This is the first time we've seen this vehicle
			v.setDateCreated(now);
			v.setDateUpdated(now);
			newModel.incNumNew();
			v.setStatus(VehicleBean.Status.NEW);
		}
		else {
			v.setDateCreated(target.getDateCreated());
			
			// Both vehicles have the same ID, but have their key properties changed?
			if (v.getId().equals(target.getId())) {
				if (! v.equals(target)) {
					// We've seen this vehicle before, but some key properties have changed
					v.setStatus(VehicleBean.Status.UPDATED);
					newModel.incNumUpdated();
					v.setDateUpdated(new Date());
				}
				else {
					// We've seen this vehicle before, AND NOTHING has changed
					newModel.incNumUnchanged();
					v.setStatus(VehicleBean.Status.UNCHANGED);
				}
			}
			else {
				// Not sure how we could get to this point in the code ...
				newModel.incNumUnchanged();
				v.setStatus(VehicleBean.Status.UNCHANGED);
			}
		}
	}
	
	private int getNumPages(Document doc) {
		int numPages = 1;
		Element ele = doc.select("h1.search-form__count").first();
		
		if (ele != null) {
			Matcher m = TOTAL_FOUND_PATTERN.matcher(ele.ownText());
			if (m.matches()) {
				String s = m.group(1).replace(",", "");
				if (StringUtils.isNumeric(s)) {
					int numResults = Integer.parseInt(s);
					LOG.info(String.format("Search found %d results", numResults));
					
					numPages = numResults / 12;
					if (numPages > 50) {
						numPages = 50;
					}
				}
			}
		}
		
		return numPages;
	}
	
	private Document getDocument(String url) {
		try {
			return Jsoup.connect(url).
					data("query", "Java").
					userAgent("Mozilla").
					cookie("auth", "token").
					timeout(3000).
					get();
		}
		catch(IOException e) {
			LOG.error("Search error", e);
		}
		
		return null;
	}
	
	private void report(SearchAgent agent, Date now) throws Exception {
		
		PrintWriter pw = new PrintWriter(
			new StringBuilder(LOG_FOLDER).
				append("/").
				append(agent.getSerializedFilePrefix()).
				append(now.getTime()).
				append(".csv").
				toString());
		
		String[] header = {
				"Age (Years)",
				"Status",
				"Year",
				"Registration",
				"Mileage",
				"High?",
				"Price (£)",
				"Bodystyle",
				"Fuel",
				"Engine size",
				"Power (PS)",
				"Transmission",
				"Location",
				"Seller type",
				"Vehicle",
				"Features",
				"Link"
			};

		StringBuilder sb = new StringBuilder();
		
		for (String part : header) {
			if (sb.length() > 0) {
				sb.append("\t");
			}
			
			sb.append(part);
		}
		
		pw.println(sb.toString());
		
		String[] ordering = { 
				"51", "02", "52", "03", "53", "04", "54", "05", "55",
				"06", "56", "07", "57", "08", "58", "09", "59",
				"10", "60", "11", "61", "12", "62", "13", "63", "14", "64", "15", "65",
				"16", "66", "17", "67", "18", "68", "19", "69", "20", "70", "21"
				};
		
		SearchResults results = agent.getResults();
		sortAndPrint(results.getVehicles(), ordering, "21", pw);
		pw.close();
		
		LOG.info(String.format("\n*** Statistics (%s):", agent.getCriteria().getHeading()));
		LOG.info(String.format(" Num processed: %d", results.getNumProcessed()));
		LOG.info(String.format("    Total cars: %d", results.getVehicles().size()));
		LOG.info(String.format("      New cars: %d", results.getNumNew()));
		LOG.info(String.format("  Updated cars: %d", results.getNumUpdated()));
		LOG.info(String.format("Unchanged cars: %d", results.getNumUnchanged()));
		LOG.info(String.format("  Expired cars: %d", results.getNumExpired()));
		
		// Average prices per registration
		reportAveragePrices(agent, ordering);
		LOG.info("\n\n\n");
	}
	
	private void reportAveragePrices(SearchAgent agent, String[] ordering) {
		String heading = agent.getCriteria().getHeading();
		List<VehicleBean> vehicles = agent.getResults().getVehicles();
		Map<String, StatisticBean> averagePrice = new HashMap<String, StatisticBean>();
		StatisticBean stat;
		for (VehicleBean v : vehicles) {
			stat = averagePrice.get(v.getRegistration());
			if (stat == null) {
				stat = new StatisticBean();
				averagePrice.put(v.getRegistration(), stat);
			}
			stat.inc(Integer.parseInt(v.getPrice()));
		}
		
		LOG.info(String.format("\n*** Average prices (%s)", heading));
		for (String reg : ordering) {
			stat = averagePrice.get(reg);
			if (stat != null) {
				LOG.info(String.format("  Reg %s: %d (*%d)", reg, stat.getAveragePrice(), stat.getNumVehicles()));
			}
		}
	}
	
	private void sortAndPrint(List<VehicleBean> vehicles, final String[] ordering, String zeroYear, PrintWriter pw) {
		Collections.sort(vehicles, new Comparator<VehicleBean>() {

			public int compare(VehicleBean o1, VehicleBean o2) {
				return indexOf(o1.getRegistration(), ordering).compareTo(indexOf(o2.getRegistration(), ordering));
			}});
		
		int origin = indexOf(zeroYear, ordering);
		int vehicleAgeCursor;
		float vehicleAge, milesPerYear;
		
		for (VehicleBean v : vehicles) {
			vehicleAgeCursor = indexOf(v.getRegistration(), ordering);
			vehicleAge = Float.parseFloat(getAgeInYears(origin, vehicleAgeCursor));
			if (StringUtils.isNumeric(v.getMileage())) {
				milesPerYear = Float.parseFloat(v.getMileage()) / vehicleAge;
				if (milesPerYear > 15000.0) {
					v.setMileageRating("HIGH");
				}
				else if (milesPerYear < 5000.0) {
					v.setMileageRating("LOW");
				}
			}
			pw.println(vehicleAge + "\t" + v.toCsv());
		}
	}
	
	private String getAgeInYears(int origin, int vehicleAge) {
		float diff = origin - vehicleAge;
		diff /= 2;
		DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
		df.applyPattern("0.0");
		return df.format(diff);
	}
	
	@SuppressWarnings("unused")
	private int getAgeInDays(long now, long then) {
		return (int) (now - then)/(MILLIS_IN_DAY);
	}
	
	private Integer indexOf(String target, String[] ordering) {
		int index = 1;
		
		for (String reg : ordering) {
			if (reg.equals(target)) {
				return index;
			}
			index++;
		}
		
		return -1;
	}
	
	@SuppressWarnings("unused")
	private String getIdFromHref(String href) {
		Matcher m = HREF_PATTERN.matcher(href);
		if (m.matches()) {
			return m.group(1);
		}
		return "";
	}
}
