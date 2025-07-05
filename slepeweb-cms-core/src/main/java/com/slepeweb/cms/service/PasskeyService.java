package com.slepeweb.cms.service;

import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.component.Passkey;

public interface PasskeyService {
	Passkey issueKey(String modelId, User u);
	boolean validateKey(Passkey key);
}
