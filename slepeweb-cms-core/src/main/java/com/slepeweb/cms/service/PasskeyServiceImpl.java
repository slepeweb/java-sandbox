package com.slepeweb.cms.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.component.Passkey;
import com.slepeweb.cms.component.PasskeyModel;

@Service("passkeyService")
public class PasskeyServiceImpl implements PasskeyService {
	
	private Map<String, PasskeyModel> models = new HashMap<String, PasskeyModel>();
	
	public PasskeyServiceImpl() {
		// Currently, only the LONG_TTL model is in use. AT the time of writing, I thought
		// we needed two models - oh how complicated this app has become!
		this.models.put(PasskeyModel.SHORT_TTL, new PasskeyModel(PasskeyModel.SHORT_TTL, 2));
		this.models.put(PasskeyModel.LONG_TTL, new PasskeyModel(PasskeyModel.LONG_TTL, 60));
	}
	
	public Passkey issueKey(String id, User u) {
		// Only logged-in users can obtain genuine passkeys
		if (u == null) {
			return new Passkey(id, "passkey-for-anonymous-user");
		}
		
		PasskeyModel model = getModel(id);
		if (model == null) {
			return new Passkey(id, "unknown-passkey-model");
		}
		
		return model.issueKey(u);
	}
	
	public boolean validateKey(Passkey pkey) {
		PasskeyModel model = getModel(pkey.getId());
		if (model == null) {
			return false;
		}
		
		return model.validateKey(pkey.getKey());
	}
	
	private PasskeyModel getModel(String id) {
		return this.models.get(id);
	}
}
