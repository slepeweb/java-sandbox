package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.LoggerBean;


public interface LoglevelService {
	List<LoggerBean> getAllLoggers();
	LoggerBean save(LoggerBean b);

}
