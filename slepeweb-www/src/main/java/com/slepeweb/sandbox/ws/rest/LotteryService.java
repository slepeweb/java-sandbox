package com.slepeweb.sandbox.ws.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("lottery")
public class LotteryService {

	@Path("numbers/{howmany}")
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
	public LotteryNumbersBean getNumbers(@PathParam("howmany") Integer howMany) {
		return new LotteryNumbersBean(howMany);
	}
}
