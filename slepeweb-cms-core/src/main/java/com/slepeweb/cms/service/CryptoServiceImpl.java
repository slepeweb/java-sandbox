package com.slepeweb.cms.service;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CryptoServiceImpl implements CryptoService {

	private static final int AES_KEY_SIZE = 256; // 128 or 256
	private static final int GCM_TAG_LENGTH = 128; // bits
	private static final int IV_SIZE = 12; // bytes

	@Autowired private SecretKey aesKey;

	// Generate a secure random AES key, and store in specified file.
	public SecretKey generateKey() throws Exception {
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(AES_KEY_SIZE);
		return keyGen.generateKey();
	}
	
	// Encrypt a string
	public String encrypt(String plainText) throws Exception {
		byte[] iv = new byte[IV_SIZE];
		SecureRandom random = new SecureRandom();
		random.nextBytes(iv);

		Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, iv);
		byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

		// Concatenate IV + ciphertext and Base64 encode
		byte[] encrypted = new byte[iv.length + cipherText.length];
		System.arraycopy(iv, 0, encrypted, 0, iv.length);
		System.arraycopy(cipherText, 0, encrypted, iv.length, cipherText.length);

		return Base64.getEncoder().encodeToString(encrypted);
	}

	// Decrypt a string
	public String decrypt(String encryptedText) throws Exception {
		if (this.aesKey == null) {
			throw new IllegalStateException("aesKey is null");
		}
		
		byte[] decoded = Base64.getDecoder().decode(encryptedText);
		byte[] iv = new byte[IV_SIZE];
		byte[] cipherText = new byte[decoded.length - IV_SIZE];

		System.arraycopy(decoded, 0, iv, 0, IV_SIZE);
		System.arraycopy(decoded, IV_SIZE, cipherText, 0, cipherText.length);

		Cipher cipher = getCipher(Cipher.DECRYPT_MODE, iv);

		byte[] plainText = cipher.doFinal(cipherText);
		return new String(plainText, StandardCharsets.UTF_8);
	}
	
	private Cipher getCipher(int mode, byte[] iv) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
		GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
		cipher.init(mode, this.aesKey, gcmSpec);
		return cipher;
	}

}
