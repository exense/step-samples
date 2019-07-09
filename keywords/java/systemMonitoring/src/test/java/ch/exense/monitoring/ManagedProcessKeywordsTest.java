package ch.exense.monitoring;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import javax.json.Json;
import javax.json.JsonObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.exense.monitoring.managedprocess.JavaManagedProcessKeywords;
import ch.exense.monitoring.managedprocess.ManagedProcessKeywords;
import ch.exense.monitoring.managedprocess.TypePerfManagedProcessKeywords;
import step.commons.processmanager.ManagedProcess;
import step.functions.io.Output;
import step.handlers.javahandler.KeywordRunner;
import step.handlers.javahandler.KeywordRunner.ExecutionContext;



public class ManagedProcessKeywordsTest {
	
	ExecutionContext ctx;
	@Before
	public void setUp() {
		ctx = KeywordRunner.getExecutionContext(ManagedProcessKeywords.class, JavaManagedProcessKeywords.class,TypePerfManagedProcessKeywords.class);	
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
	            .add("maxOutputPayloadSize", "10000000000000000")
	            .add("maxOutputAttachmentSize", "0")
	            .add("executablePath", "powershell")
	            .add("args", " Get-Service -DisplayName 'DHCP Client' | Format-List Status")
	            .build();
		try {
			output = ctx.run("ManagedProcessKeyword", input.toString());
			System.out.println(output.getPayload());
			assertTrue(output.getPayload().getString("stdout").contains("Running"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//@Test
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
	
	//@Test
	public void testManagedProcessKW() {
		JsonObject input;
		Output<JsonObject> output;
		
		input = Json.createObjectBuilder()
	            .add("timeoutInMillis", "100000")
	            .add("maxOutputPayloadSize", "10000000000000000")
	            .add("maxOutputAttachmentSize", "0")
	            .add("executablePath", "typeperf")
	            .add("args", "\"\\Processor(_Total)\\% Idle Time\" \"\\Memory\\Available MBytes\" -sc 1 -s DESKTOP-UD66RM4")
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

	//@Test
	public void testSystemMonitoring() {
		
		String[] keys = {"CPU_idle(%)","MemoryAvailableMB"};
		String hostname ="DESKTOP-UD66RM4";
		String cmd = "typeperf \"\\Processor(_Total)\\% Idle Time\" \"\\Memory\\Available MBytes\" -sc 1 -s " + hostname;
		//String cmd = "java -version";
		try (ManagedProcess process = new ManagedProcess(cmd, "typeperf")){
			process.start();	
			process.waitFor(5000);
			List<String> output = Files.readAllLines(process.getProcessOutputLog().toPath(), Charset.defaultCharset());
			System.out.println("Stde: " + new String(Files.readAllBytes(process.getProcessErrorLog().toPath()), Charset.defaultCharset()));
			if (!output.isEmpty() && output.get(4).contentEquals("The command completed successfully.")) {
				Map<String,String> values = processTypePerfOutput(output,keys);
				System.out.println("Values: " + values);
				
			}
		} catch (TimeoutException e) {
			System.out.println("Timeout while waiting for process termination.");
		} catch (Exception e) {
			System.out.println("Error while running process");
			e.printStackTrace();
		}	
	}

	private Map<String,String> processTypePerfOutput(List<String> output, String[] keys) {
		Map<String,String> values = new HashMap<String,String> ();
		String[] fields = output.get(2).split(",");
		for (int i=1; i < fields.length; i++) {
			values.put(keys[i-1], fields[i]);
		}
		
		return values;
	}
}
