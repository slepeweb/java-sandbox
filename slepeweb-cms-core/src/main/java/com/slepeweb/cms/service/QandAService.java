package com.slepeweb.cms.service;

import com.slepeweb.cms.bean.QandAList;
import com.slepeweb.cms.bean.User;

public interface QandAService {
	void update (User u, QandAList list) throws Exception;
	boolean validate(User u, QandAList list) throws Exception;
	QandAList getQandAList(User u) throws Exception;
}
