package com.slepeweb.cms.service;

import javax.crypto.SecretKey;

public interface CryptoService {
	SecretKey generateKey() throws Exception;
	String encrypt(String s) throws Exception;
	String decrypt(String s) throws Exception;
}
