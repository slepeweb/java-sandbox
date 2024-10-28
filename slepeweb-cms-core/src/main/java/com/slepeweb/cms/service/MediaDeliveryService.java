package com.slepeweb.cms.service;

import java.io.IOException;

import com.slepeweb.cms.bean.Item;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public interface MediaDeliveryService {
	void stream(Item item, HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException;
}
