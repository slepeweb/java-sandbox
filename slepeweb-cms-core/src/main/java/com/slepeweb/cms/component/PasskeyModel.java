package com.slepeweb.cms.component;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.slepeweb.cms.bean.User;

public class PasskeyModel {
	private static Logger LOG = Logger.getLogger(PasskeyModel.class);
	public static final String SHORT_TTL = "xitem";
	public static final String LONG_TTL = "topdf";
	
	
	private static int NUM_COLS = 10, NUM_ROWS = 10;
	private static int GRIDSIZE = NUM_COLS * NUM_ROWS;
	private static int MAX_ISSUES = 2;
	
	// Identifier
	private String id;
	
	// Time to live
	private int ttl = 30;
	
	// This array contains all the numbers from 0 to 99, in random order.
	private int[] model;
	
	// The model is rebuild every TTL seconds. We keep the last two models in this list.
	private List<int[]> issues;
	
	// This is when the model was last re-built.
	private long lastIssuedMillis;
	
	// This is the last key issued
	private Passkey lastKeyIssued;
	
	public PasskeyModel(String id, int ttl) {
		this.id = id;
		this.ttl = ttl;
		this.issues = new ArrayList<int[]>();
		this.issues.add(this.model = build());
		this.lastIssuedMillis = System.currentTimeMillis();
	}
	
	private long toSeconds(long millis) {
		return millis / 1000;
	}
	
	private int[] build() {
		int[] model = new int[GRIDSIZE];
		
		for (int i = 0; i < GRIDSIZE; i++) {
			model[i] = i;
		}
		
		// Shuffle model twice, for good measure!
		LOG.info("Built data model for TTL = " + this.ttl);
		return shuffle(shuffle(model));
	}
	
	private void rebuild() {
		this.issues.add(this.model = build());
		this.lastIssuedMillis = System.currentTimeMillis();
		LOG.info("Re-built data model");
		
		if (this.issues.size() > MAX_ISSUES) {
			this.issues.remove(0);
			LOG.info(String.format("Old data model removed, leaving %d behind", this.issues.size()));
		}
	}
	
	private int[] shuffle(int[] model) {
		// Randomly shuffle array
		int a, b;
		int z;
		
		for (int i = 0; i < GRIDSIZE; i++) {
			a = getRandom();
			b = getRandom();
			z = model[a];
			model[a] = model[b];
			model[b] = z;
		}
		
		return model;
	}
	
	// Get random number between 0 and 99
	private int getRandom() {
		return (int) (Math.random() * GRIDSIZE);

	}
	
	// Treats the model as a matrix, with 10 rows and 10 columns.
	// Returns a value from the model, corresponding to row and col, which are zero based.
	private int getModelEntry(int col, int row) {
		int cursor = (col * NUM_ROWS) + row;
		return this.model[cursor];
	}
	
	// Given a value from the model, which is known to come from a given column,
	// find the corresponding row value.
	private int getRowIndex(int col, int value, int[] model) {
		int cursor = col * NUM_ROWS;
		for (int i = 0; i < NUM_ROWS; i++) {
			if (model[cursor + i] == value) {
				return i;
			}
		}
		
		return 0;
	}
	
	public synchronized Passkey issueKey(User u) {
		// Only logged-in users can obtain genuine passkeys
		if (u == null) {
			return toPasskey("no-alias", "passkey-for-anonymous-user");
		}
		
		// For the next 50 years or so, nowMillis will always be 1 10-digit number.
		// For example, let's say nowMillis = 1732489259
		long nowMillis = System.currentTimeMillis();
		long difference = nowMillis - this.lastIssuedMillis;
		long ttlMillis = ttl * 1000;
		if (difference < ttlMillis && this.lastKeyIssued != null) {
			return this.lastKeyIssued;
		}
		else if (difference > ttlMillis) {
			rebuild();
		}
		
		String keyStr = String.valueOf(toSeconds(nowMillis));
		char[] timeStr = keyStr.toCharArray();	
		int strlen = timeStr.length;
		
		StringBuffer sb = new StringBuffer();
		int row;
		
		// Say timeStr is 1723456985.
		for (int col = strlen - 1; col >= 0; col--) {
			// On first loop, col = 9 (base 0)
			// The ninth digit (base 0) in timeStr is 5
			// row becomes 5
			row = Integer.parseInt(String.valueOf(timeStr[col]));
			// Extract the model entry for this col/row, and
			// append it as a 2-character string value (zero padded if necessary) to the output key.
			sb.append(zeropad(String.valueOf(getModelEntry(col, row))));
		}
		
		// So, the mapping might be:
		// 1 7 2 3 4 5 6 9 8 5  -> 40 03 31 80 64 57 20 55 42 25
		//                   |______|
		//                 |___________|
		//               |________________| etc.
		//
		// Expanding further, the 8th digit (base 0) on timeStr is 8.
		// At col 8, row 8 in the model, the value is 03.
		String s = sb.toString();
		LOG.info(String.format("%s encoded to passkey %s", keyStr, s));
		this.lastKeyIssued = toPasskey(u.getAlias(), s);
		return this.lastKeyIssued;
	}
	
	public synchronized boolean validateKey(Passkey passkey) {
		boolean isValid = false;
		
		if (! passkey.isReady()) {
			return false;
		}
		
		String key = passkey.getKey();
		
		// First, try latest model data
		int[] testAgainstModel = this.model;
		long nowSecs = toSeconds(System.currentTimeMillis());

		LOG.trace("First validation ...");
		String decodedStr = decodePasskey(key, testAgainstModel);
		
		if (! (isValid = validateKey(decodedStr, nowSecs)) && this.issues.size() > 1) {
			LOG.info("First validation ***failed***, now trying second ...");
			// Now try earlier set of chars
			testAgainstModel = this.issues.get(0);
			decodedStr = decodePasskey(key, testAgainstModel);
			isValid = validateKey(decodedStr, nowSecs);
		}
		
		return isValid;
	}
	
	private boolean validateKey(String decodedStr, long nowcompare2Seconds) {
		long decodedSeconds = Long.parseLong(decodedStr);		
		long discrepancy = Math.abs(nowcompare2Seconds - decodedSeconds);
		boolean r = discrepancy < ttl;
		
		if (r) {
			LOG.trace("Key is valid!");
		}
		else {
			LOG.info(String.format("Key NOT valid, discrepancy = %d secs", discrepancy));
		}

		return r;
	}
	
	// key will be a 20-character string like 40 03 31 80 64 57 20 55 42 25
	private String decodePasskey(String key, int[] model) {
		int strlen = key.length();
		if (strlen != 20) {
			return "0";
		}
		
		StringBuffer sb = new StringBuffer();
		int start, end;
		String pair;
		
		for (int col = 0; col < NUM_COLS; col++) {
			start = (NUM_COLS - col - 1) * 2;
			end = start + 2;
			pair = key.substring(start, end);				
			sb.append(getRowIndex(col, Integer.parseInt(pair), model));
		}
		
		String decodedStr = sb.toString();
		String.format("Key %s decoded to %s", key, decodedStr);
		return decodedStr;
	}
	
	private String zeropad(String numeric) {
		if (numeric.length() == 1) {
			return "0" + numeric;
		}
		
		return numeric;
	}
	
	private Passkey toPasskey(String alias, String s) {
		return new Passkey(this.id, alias, s);
	}
	
	
	public static void main(String[] args) {
		PasskeyModel c = new PasskeyModel("identifier", 1);

		try {
			Passkey pkey;
			for (int i = 0; i < 10; i++) {
				pkey = c.issueKey(null);
				c.validateKey(pkey);
				Thread.sleep(6000);
			}
		}
		catch (Exception e) {
			LOG.error(e);
		}

	}
}
