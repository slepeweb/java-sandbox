package com.slepeweb.cms.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class BadActorMonitor {
	
	private static Logger LOG = Logger.getLogger(BadActorMonitor.class);
	
	private static final int LOGIN_FAILURE = 0;
	private static final int PROBING_FAILURE = 1;
	
	private static long CLEANUP_INTERVAL = 10; // 10 minutes
	private static long CLEANUP_INTERVAL_MILLIS = CLEANUP_INTERVAL * 60 * 1000; // 10 minutes in milliseconds
	
	private Map<String, BadActorRecord> failures = new HashMap<String, BadActorRecord>();
	private long lastCleanup = System.currentTimeMillis();
	private long lastRegistration = 0L;
	
	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	
	@PostConstruct
	private void init() {
		Runnable task = () -> {
            try {
                removeStaleEntries();
            } catch (Exception e) {
                e.printStackTrace(); // Replace with proper logging
            }
        };
        
        this.scheduler.scheduleAtFixedRate(task, 0, CLEANUP_INTERVAL, TimeUnit.MINUTES);
	}
	
	public BadActorRecord getRecord(String ip) {
		return this.failures.get(ip);
	}
	
	public BadActorRecord registerLoginFailure(String ip) {
		return registerFailure(ip, LOGIN_FAILURE);
	}
	
	public BadActorRecord registerProbingFailure(String ip) {
		return registerFailure(ip, PROBING_FAILURE);
	}
	
	private BadActorRecord registerFailure(String ip, int type) {
		BadActorRecord f = getRecord(ip);
		if (f == null) {
			f = new BadActorRecord(ip, System.currentTimeMillis());
			this.failures.put(ip, f);
		}
		
		f.increment(type);
		this.lastRegistration = f.getWhen();
		return f;
	}
	
	public int removeStaleEntries() {
		long now = System.currentTimeMillis();
		if (
			this.lastRegistration < this.lastCleanup || 			// no dodgy activity since the last cleanup
			(now - this.lastCleanup) < CLEANUP_INTERVAL_MILLIS		// the last cleanup was within the allowed interval
			)  {
			
			// Zero records removed, if anyone's interested
			return 0;
		}
		
		List<String> removals = new ArrayList<String>();
		
		Iterator<BadActorRecord> iter = this.failures.values().iterator();
		BadActorRecord f;
		
		while (iter.hasNext()) {
			f = iter.next();
			if (f.isCleanupDue(now)) {
				removals.add(String.format("Stale entry: \n%s\t%d Logins\t%d Notfounds", f.getIp(), f.getCounters()[0], f.getCounters()[1]));
				iter.remove();
			}
		}
		
		for (String s : removals) {
			LOG.info(s);
		}
		
		this.lastCleanup = now;
		return removals.size();
	}

	public static class BadActorRecord {
			
		public static final int MAX_LOGIN_FAILURES = 3;
		public static final int MAX_PROBING_FAILURES = 5;
		
		private String ip;
		private long when;
		private int[] counters = {0 /* num login failures */, 0 /* num probing failures */};
		
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
		
		public int[] getCounters() {
			return counters;
		}

		public void increment(int index) {
			this.counters[index] += 1;
		}
		
		public boolean isCleanupDue(long now) {
			return now - this.getWhen() > BadActorMonitor.CLEANUP_INTERVAL;
		}
		
		public boolean isTooManyLoginFailures() {
			return this.counters[0] >= MAX_LOGIN_FAILURES;
		}

		public boolean isTooManyProbingFailures() {
			return this.counters[1] >= MAX_PROBING_FAILURES;
		}

	}

}
