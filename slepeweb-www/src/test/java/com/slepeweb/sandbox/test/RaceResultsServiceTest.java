package com.slepeweb.sandbox.test;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RaceResultsServiceTest {

	@Test
	public void subscribedClientShouldReceiveMessage() {
		RaceResultsService raceResults = new RaceResultsService();
		Client client = mock(Client.class);
		Message message = mock(Message.class);
		
		raceResults.addSubscriber(client);
		raceResults.send(message);
		
		verify(client).receive(message);
	}

	@Test
	public void messageShouldBeSent2AllSubscribers() {
		RaceResultsService raceResults = new RaceResultsService();
		Client clientA = mock(Client.class, "clientA");
		Client clientB = mock(Client.class, "clientB");
		Message message = mock(Message.class);
		
		raceResults.addSubscriber(clientA);
		raceResults.addSubscriber(clientB);
		raceResults.send(message);
		
		verify(clientA).receive(message);
		verify(clientB).receive(message);
	}
}
