package com.slepeweb.cms.setup;

public class SiteSetupStatistics {
	private int numRowsProcessed, numXlsErrors, numXlsWarnings;
	private int numSiteUpdates, numItemTypeUpdates, numFieldUpdates, numTemplateUpdates;

	public enum ResultType {
		ROWS_PROCESSED, XLS_ERROR, XLS_WARNING, 
		SITE_UPDATED, FIELD_UPDATED, ITEMTYPE_UPDATED, TEMPLATE_UPDATED
	}

	public void inc(ResultType type) {
		if (type == ResultType.ROWS_PROCESSED) {
			this.numRowsProcessed++;
		} else if (type == ResultType.XLS_ERROR) {
			this.numXlsErrors++;
		} else if (type == ResultType.XLS_WARNING) {
			this.numXlsWarnings++;
		} else if (type == ResultType.SITE_UPDATED) {
			this.numSiteUpdates++;
		} else if (type == ResultType.ITEMTYPE_UPDATED) {
			this.numItemTypeUpdates++;
		} else if (type == ResultType.FIELD_UPDATED) {
			this.numFieldUpdates++;
		} else if (type == ResultType.TEMPLATE_UPDATED) {
			this.numTemplateUpdates++;
		}
	}

	public int getNumRowsProcessed() {
		return numRowsProcessed;
	}

	public int getNumXlsErrors() {
		return numXlsErrors;
	}

	public int getNumXlsWarnings() {
		return numXlsWarnings;
	}

	public int getNumSiteUpdates() {
		return numSiteUpdates;
	}

	public int getNumItemTypeUpdates() {
		return numItemTypeUpdates;
	}

	public int getNumFieldUpdates() {
		return numFieldUpdates;
	}

	public int getNumTemplateUpdates() {
		return numTemplateUpdates;
	}
}