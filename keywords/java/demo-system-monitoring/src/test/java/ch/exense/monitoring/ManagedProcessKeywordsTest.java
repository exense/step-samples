package ch.exense.monitoring;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.json.Json;
import javax.json.JsonObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.exense.monitoring.managedprocess.JavaManagedProcessKeywords;
import ch.exense.monitoring.managedprocess.ManagedProcessKeywords;
import ch.exense.monitoring.managedprocess.TypePerfManagedProcessKeywords;
import ch.exense.monitoring.managedprocess.WindowsServiceStatusKeywords;
import step.functions.io.Output;
import step.handlers.javahandler.KeywordRunner;
import step.handlers.javahandler.KeywordRunner.ExecutionContext;

public class ManagedProcessKeywordsTest {
	
	ExecutionContext ctx;
	@Before
	public void setUp() {
		ctx = KeywordRunner.getExecutionContext(ManagedProcessKeywords.class, JavaManagedProcessKeywords.class,TypePerfManagedProcessKeywords.class, WindowsServiceStatusKeywords.class);	
	}

	@After
	public void tearDown() {
		ctx.close();
	}
	
	@Test
	public void testPowershellServiceStatusKeyword() {
		JsonObject input;
		Output<JsonObject> output;
		
		input = Json.createObjectBuilder()
	            .add("timeoutInMillis", "100000")
	            .add("maxOutputPayloadSize", "10000000000000")
	            .add("maxOutputAttachmentSize", "0")
	            .add("executablePath", "powershell")
	            .add("serviceDisplayName", "DHCP Client")
	            .build();
		try {
			output = ctx.run("WindowsServiceStatusKeyword", input.toString());
			System.out.println(output.getPayload());
			assertTrue(output.getPayload().getString("status").contains("running"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testTypePerfManagedProcessKeyword() {
		JsonObject input;
		Output<JsonObject> output;
		
		input = Json.createObjectBuilder()
	            .add("timeoutInMillis", "100000")
	            .add("maxOutputPayloadSize", "10000000000000000")
	            .add("maxOutputAttachmentSize", "0")
	            .add("executablePath", "typeperf")
	            .build();
		try {
			output = ctx.run("TypePerfManagedProcessKeyword", input.toString());
			System.out.println(output.getPayload());
			assertTrue(output.getPayload().getString("stdout").contains("The command completed successfully"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testManagedProcessKW() {
		JsonObject input;
		Output<JsonObject> output;
		
		input = Json.createObjectBuilder()
	            .add("timeoutInMillis", "100000")
	            .add("maxOutputPayloadSize", "10000000000000000")
	            .add("maxOutputAttachmentSize", "0")
	            .add("executablePath", "typeperf")
	            .add("args", "\"\\Processor(_Total)\\% Idle Time\" \"\\Memory\\Available MBytes\" -sc 1")
	            .build();
		try {
			output = ctx.run("ManagedProcessKeyword", input.toString());
			System.out.println(output.getPayload());
			assertTrue(output.getPayload().getString("stdout").contains("The command completed successfully"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//@Test
	public void testJavaManagedProcessKW() {
		JsonObject input;
		Output<JsonObject> output;
		
		input = Json.createObjectBuilder()
	            .add("timeoutInMillis", "100000")
	            .add("maxOutputPayloadSize", "10000000000000000")
	            .add("maxOutputAttachmentSize", "0")
	            .add("executablePath", "java")
	            .add("mainClassOrJar", "ch.exense.monitoring.HelloWorld")
	            .add("classPath", "../target/classes")
	            .add("vmArgs", "-Xms32m -DMySysProp=test")
	            .add("programArgs", "David")
	            .build();
		try {
			output = ctx.run("JavaManagedProcessKeyword", input.toString());
			System.out.println(output.getPayload());
			assertEquals(output.getPayload().getString("stdout"),"Hello world to David\nMy system property value is test\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
