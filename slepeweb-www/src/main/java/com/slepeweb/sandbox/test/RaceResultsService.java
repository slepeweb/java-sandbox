package com.slepeweb.sandbox.test;

import java.util.ArrayList;
import java.util.Collection;

public class RaceResultsService {

	private Collection<Client> clients = new ArrayList<Client>();
	
	public void addSubscriber(Client client) {
		this.clients.add(client);
	}

	public void send(Message message) {
		for (Client client : this.clients) {
			client.receive(message);
		}
	}

}
