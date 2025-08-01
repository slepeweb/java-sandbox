package com.slepeweb.cms.service;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.User;

/*
 * DO NOT CONFUSE THIS CLASS WITH PasskeyService, which is required by . 
 * PdfService produces a PDF from given html input, and requires public access to images,
 * which it can achieve using passkeys issued by PasskeyService.
 * 
 * This class (XPasskeyServiceImpl) encrypts the user's password, to be used as a passkey
 * that can be used to log into the editorial app (from a delivery app) without having 
 * to re-login. The passkey is a combination of hex characters (0-9, a-f).
 * 
 * The X (first char) in XPasskeyService is for 'Cross'. That is , we use this service 
 * to cross-login, from a delivery app to it's editorial equivalent.
 * 
 */

@Service("xPasskeyService")
public class XPasskeyServiceImpl implements XPasskeyService {
	//private static Logger LOG = Logger.getLogger(XPasskeyServiceImpl.class);
	
	@Autowired private UserService userService;
	private final int digitBump = 17, bumperBase = 16;
		
	public String issueKey(User u) {
		return transform(u.getPassword(), getInterval());
	}
	
	public User identifyUser(String key) {
		int offs = getInterval();
		
		User u = this.userService.getByPassword(undoTransform(key, offs));
		
		// In case the second-hand has just gone past 0 ...
		if (u == null) {
			// In case the minute-hand has rolled over the hour ...
			offs = offs == 0 ? 59 : offs - 1;
			u = this.userService.getByPassword(undoTransform(key, offs));
		}
		
		return u;
	}
	
	private String transform(String str, int value) {
		char[] chars = str.toCharArray();
		return new String(mirror(rotate(bump(chars), value)));
	}
	
	private String undoTransform(String str, int value) {
		char[] in = str.toCharArray();
		return new String(undoBump(rotate(mirror(in), -value)));
	}
	
	private int getInterval() {
		Calendar now = Calendar.getInstance();
		return now.get(Calendar.MINUTE);
	}
	
	// An inverse rotation is catered for by using a negative value
	private char[] rotate(char[] in, int value) {
		
		int len = in.length;
		char[] out = new char[len];
		
		int direction = 1;
		if (value < 0) {
			direction = -1;
			value = -value;
		}
		
		value = value % len;
		int i = 0;
		
		// Shift characters in two chunks ...
		
		// ... from middle to end
		for (int j = value; j < len; j++) {
			rotate(in, out, i, j, direction);
			i++;
		}
		
		// ... then from beginning to middle
		for (int j = 0; j < value; j++) {
			rotate(in, out, i, j, direction);
			i++;
		}
		
		return out;
	}
	
	private void rotate(char[] in, char[] out, int i, int j, int direction) {
		if (direction > 0) {
			out[i] =  in[j];
		}
		else {
			out[j] =  in[i];
		}			
	}
	
	// An inverse mirror action is the same as 2 repeated mirrors on the same string
	private char[] mirror(char[] in) {
		int len = in.length;
		char head;
		int j;
		
		for (int i = 0; i < (len / 2); i++) {
			j = len - i - 1;
			head = in[i];
			in[i] = in[j];
			in[j] = head;
		}
		
		return in;
	}
	
	
	/*
	 * We will bump digits by 17, so that 1 -> A, 2 -> B, etc.
	 * Lowercase letters do not need to be bumped at this stage.
	 * Then, we bump both sets of characters, by a value in the range 0 -> 19.
	 */
	private char[] bump(char[] in) {
		int offset;
		
		for (int i = 0; i < in.length; i++) {
			// All digits will be converted to uppercase letters
			offset = isNumeric(in[i]) ? this.digitBump : 0;
			offset += i % this.bumperBase;
			in[i] += offset;
		}
		
		return in;
	}
	
	private char[] undoBump(char[] in) {
		int offset;
		
		for (int i = 0; i < in.length; i++) {
			// All digits will be converted to uppercase letters
			offset = isUppercaseLetter(in[i]) ? this.digitBump : 0;
			offset += i % this.bumperBase;
			in[i] -= offset;
		}
		
		return in;
	}
	
	private boolean isNumeric(char c) {
		return c >= 48 && c <= 57;
	}
	
	
	private boolean isUppercaseLetter(char c) {
		return c >= 65 && c <= 90;
	}
	
	
	// For test purposes
	public static void main(String[] args) {
		XPasskeyServiceImpl svc = new XPasskeyServiceImpl();
		String inStr = "13138321e66c2e6d1defa39a26949937a7644ce26b0a1862eaf523d940b845a76105cd94e8bb97f9";
		int offs = 3;
		String outStr = output(inStr, offs, svc.transform(inStr, offs));
		output(outStr, offs, svc.undoTransform(outStr, offs));

		offs = 13;
		outStr = output(inStr, offs, svc.transform(inStr, offs));
		String cmp = output(outStr, offs, svc.undoTransform(outStr, offs));
		
		System.out.println(String.format("Strings %s identical", cmp.equals(inStr) ? "ARE" : "ARE NOT"));
	}
	
	private static String output(String in, int val, String out) {
		String s = String.format("\nTransform with value %d => \n%s\n%s", val, in, out);
		System.out.println(s);
		return out;
	}
}
