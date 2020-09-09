package com.slepeweb.common.service;

public interface SendMailService {

	boolean sendMail(String from, String to, String name, String subject, String message);
}
