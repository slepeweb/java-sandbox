package com.slepeweb.cms.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.slepeweb.cms.bean.QandAList;
import com.slepeweb.cms.bean.User;
import com.slepeweb.common.util.JsonUtil;

/*
 * TODO: TEMPORARILY using 'secret' column in User table, as anc site is using it for a different purpose.
 */

@Service
public class QandAServiceImpl implements QandAService {
	
	@Autowired private CryptoService cryptoService;
	
	public void update(User u, QandAList list) throws Exception {
		String json = JsonUtil.toJson(list);
		String encrypted = this.cryptoService.encrypt(json);
		u.setSecret(encrypted);
	}
	
	public boolean validate(User u, QandAList list) throws Exception {
		String json = JsonUtil.toJson(list);
		String encrypted = this.cryptoService.encrypt(json);
		return StringUtils.isNotBlank(u.getSecret()) && u.getSecret().equals(encrypted);
	}
	
	public QandAList getQandAList(User u) throws Exception {
		if (StringUtils.isNotBlank(u.getSecret())) {
			String json = this.cryptoService.decrypt(u.getSecret());
			return JsonUtil.fromJson(new TypeReference<QandAList>() {}, json);
		}
		
		return new QandAList();
	}
	
}
