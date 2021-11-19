package com.slepeweb.cms.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.slepeweb.cms.bean.Item;


public interface MediaDeliveryService {
	void stream(Item item, HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException;
}
