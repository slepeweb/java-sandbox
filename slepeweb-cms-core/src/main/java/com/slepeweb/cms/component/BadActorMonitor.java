package com.slepeweb.cms.component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class BadActorMonitor {
	
	private static long CLEANUP_INTERVAL = 5 * 60 * 1000; // 5 minutes in milliseconds
	private Map<String, BadActorRecord> failures = new HashMap<String, BadActorRecord>();
	private long lastCleanup = System.currentTimeMillis();
	
	public BadActorRecord getRecord(String ip) {
		return this.failures.get(ip);
	}
	
	public void registerFailure(String ip) {
		if (ip != null) {
			BadActorRecord f = getRecord(ip);
			if (f == null) {
				f = new BadActorRecord(ip, System.currentTimeMillis());
				this.failures.put(ip, f);
			}
			
			f.increment();
		}
	}
	
	public boolean isBad(String ip) {
		// Remove any stale entries
		cleanupIf();
		
		// Test whether this IP has an failure record with too many counts
		BadActorRecord f = getRecord(ip);
		return f != null && f.isTooMany();
	}
	
	private int cleanupIf() {
		long now = System.currentTimeMillis();
		if (now - this.lastCleanup < CLEANUP_INTERVAL) {
			return 0;
		}
		
		Iterator<BadActorRecord> iter = this.failures.values().iterator();
		int count = 0;
		BadActorRecord f;
		
		while (iter.hasNext()) {
			f = iter.next();
			if (f.isCleanupDue(now)) {
				iter.remove();
				count++;
			}
		}
		
		this.lastCleanup = System.currentTimeMillis();
		return count;
	}

	public static class BadActorRecord {
			
		private static final int MAX_FAILURES = 4;
		private static final int RESET_INTERVAL = 10 * 60 * 1000; // 10 minutes in milliseconds
		
		private String ip;
		private long when;
		private int count = 0;
		
		public BadActorRecord(String ip, long when) {
			this.ip = ip;
			this.when = when;
		}

		public String getIp() {
			return ip;
		}

		public long getWhen() {
			return when;
		}
		
		public int getCount() {
			return count;
		}

		public int increment() {
			return ++this.count;
		}
		
		public boolean isCleanupDue(long now) {
			return now - this.getWhen() > RESET_INTERVAL;
		}
		
		public boolean isTooMany() {
			return this.count >= MAX_FAILURES;
		}

	}

}
