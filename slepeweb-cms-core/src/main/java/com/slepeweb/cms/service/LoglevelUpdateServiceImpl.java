package com.slepeweb.cms.service;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.NullEnumeration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.LoggerBean;
import com.slepeweb.cms.service.LoglevelService;

@Service("loglevelService")
public class LoglevelUpdateServiceImpl implements LoglevelUpdateService {
	private static Logger LOG = Logger.getLogger(LoglevelUpdateServiceImpl.class);
	
	@Autowired LoglevelService loglevelService;

	public Map<String, LoggerBean> getLoggersOfInterest() {
		List<LoggerBean> loggers = this.loglevelService.getAllLoggers();
		Map<String, LoggerBean> map = new HashMap<String, LoggerBean>(loggers.size());
		
		for(LoggerBean logger : loggers) {
			map.put(logger.getPackag(), logger);
		}
		
		return map;
	}
	
	public void updateLoglevels() {
		String pkg, level, currentLevel, msg;
		Logger logger;
		boolean changesMade = false;
		
		@SuppressWarnings("rawtypes")
		Enumeration allAppenders;
		
		String knownPackage = "com.slepeweb";
		Logger knownLogger = Logger.getLogger(knownPackage);
		Appender logfileAppender = knownLogger.getAppender("LOGFILE");
		if (logfileAppender == null) {
			msg = String.format("LOGFILE appender couldn't be found for [%s]", knownPackage);
			LOG.warn(msg);
		}
		
		for (Map.Entry<String, LoggerBean> entry : getLoggersOfInterest().entrySet()) {
			pkg = entry.getKey();
			level = entry.getValue().getLevel();				
			logger = Logger.getLogger(pkg);
			
			if (logger != null) {
				if (logger.getLevel() == null) {
					currentLevel = "";
				} else {
					currentLevel = logger.getLevel().toString();
				}
				
				if (! level.equals(currentLevel)) {
					logger.setLevel(Level.toLevel(level));
					logger.setAdditivity(false);
					msg = String.format("Log-level set to %s [%s]", level, pkg);
					changesMade = true;
					LOG.info(msg);
				}
				else {
					msg = String.format("Log-level unchanged [%s]", pkg);
					LOG.info(msg);
				}
				
				allAppenders = logger.getAllAppenders();
				if (allAppenders instanceof NullEnumeration && logfileAppender != null) {
					logger.addAppender(logfileAppender);
					msg = String.format("Added LOGFILE appender [%s]", pkg);
					logger.info(msg);
				}
			}
			else {
				msg = String.format("No logger available [%s]", pkg);
				LOG.info(msg);
			}
		}
		
		if ( ! changesMade) {
			msg = "No log levels changed";
			LOG.info(msg);
		}
	}	
}
