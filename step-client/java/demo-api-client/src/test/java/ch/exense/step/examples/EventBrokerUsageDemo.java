package ch.exense.step.examples;

import org.junit.Test;

import junit.framework.Assert;
import step.client.StepClient;
import step.client.eventbroker.RemoteEventBrokerClient;
import step.plugins.events.Event;

public class EventBrokerUsageDemo {

	@Test
	public void eventBrokerDemo() throws Exception {
		try(StepClient sclient = new StepClient("https://step-public-demo.stepcloud.ch", "admin", "public")) {

			RemoteEventBrokerClient client = sclient.getEventBrokerClient();
			
			//ID based
			Event sentEvent = new Event().setId("a_unique_id");client.putEvent(sentEvent);
			
			client.peekEvent("a_unique_id");
			Event readEvent = client.consumeEvent("a_unique_id");
			System.out.println(readEvent);
			
			Assert.assertEquals("a_unique_id", readEvent.getId());
			
			//Group based
			Event sentGEvent1 = new Event().setGroup("myGroup").setName("myEventName1");
			Event sentGEvent2 = new Event().setGroup("myGroup").setName("myEventName2");
			
			client.putEvent(sentGEvent1);
			client.putEvent(sentGEvent2);
			
			Event readGEvent = client.consumeEventByGroupAndName("myGroup", null); // event 2
			readGEvent = client.consumeEventByGroupAndName("myGroup", null); // event 1
			System.out.println(readGEvent);
			
			Assert.assertEquals(0, client.getGroupSize("myGroup")); //All events are consumed
			
			client.close();
			sclient.close();
			
		}
	}
}