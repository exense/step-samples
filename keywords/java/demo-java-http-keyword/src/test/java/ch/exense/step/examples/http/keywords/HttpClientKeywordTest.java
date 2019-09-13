package ch.exense.step.examples.http.keywords;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.http.HttpHeaders;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import ch.qos.logback.classic.Level;
import step.functions.io.Output;
import step.handlers.javahandler.KeywordRunner;
import step.handlers.javahandler.KeywordRunner.ExecutionContext;

public class HttpClientKeywordTest {

	private static String VERSION = "3.11.0";

	ExecutionContext ctx = KeywordRunner.getExecutionContext(HttpClientKeyword.class);
	private Output<JsonObject> output;
	
	private JsonBuilderFactory factory = Json.createBuilderFactory(null);
	private JsonObjectBuilder builder;

	@Before
	public void initTest() {
	}

	//@Test
	public void stepLoginTest() throws Exception {
		
		Logger LOG = (Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		((ch.qos.logback.classic.Logger) LOG).setLevel(Level.DEBUG);
		

		// Open http client
		System.out.println("Init client");
		output = ctx.run("InitHttpClientKW", "{}");
		assertEquals("true", output.getPayload().getString("success"));

		// Navigate to login page
		System.out.println("Go_to_Step");
		
		builder = factory.createObjectBuilder();
		builder.add("protocol", "http://")
			.add("host", "localhost")
			.add("port", "8080")
			.add("uri", "")
			.add("method", "GET")
			.add("header_Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
			.add("header_Accept-Encoding", "gzip, deflate")
			.add("header_User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36")
			.add("check_title", "<title>STEP</title>")
			.add("extract_version", "STEP ([^ ]*) - Copyright ");

		String input = builder.build().toString();

		System.out.println(input);
		output = ctx.run("HttpRequestKW", input);
		System.out.println(output.getPayload().toString());
		assertEquals(output.getPayload().getInt("httpStatusCode"),200);
		assertTrue(output.getPayload().getBoolean("check_title"));
		assertEquals(output.getPayload().getString("extract_version"),VERSION);
		
		//login
		System.out.println("Login");
		builder = factory.createObjectBuilder();
		builder.add("protocol", "http://")
			.add("host", "localhost")
			.add("port", "8080")
			.add("uri", "/rest/access/login")
			.add("method", "POST")
			.add("header_Accept","application/json, text/plain, */*")
			.add("header_Content-Type", "application/json;charset=UTF-8")
			.add("header_User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36")
			.add("payload", "{\"username\": \"admin\", \"password\": \"init\"}")
			.add("extract_role", "\"role\":\"([^\"]*)\"");

		input = builder.build().toString();
		System.out.println(input);
		output = ctx.run("HttpRequestKW", input);
		System.out.println(output.getPayload().toString());
		assertEquals(output.getPayload().getInt("httpStatusCode"),200);
		assertEquals(output.getPayload().getString("extract_role"),"admin");
		
		output = ctx.run("CloseHttpClientKW", "{}");
		
	}
	
	//@Test
	public void tomcatManagerTest() throws Exception {
		Logger LOG = (Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		((ch.qos.logback.classic.Logger) LOG).setLevel(Level.DEBUG);

		// Open http client
		System.out.println("Init client");
		builder = factory.createObjectBuilder();
		builder.add("basic_auth_user", "tomcat")
			.add("basic_auth_password","tomcat")
			.add("basic_auth_host","localhost")
			.add("basic_auth_host_scheme","http")
			.add("basic_auth_port",8088)
			.add("targetIP", "127.0.0.1")
			.add("hostWithCustomDns", "localhost");
		
		
		output = ctx.run("InitHttpClientKW", builder.build().toString());
		assertEquals("true", output.getPayload().getString("success"));

		// Navigate to google search page
		System.out.println("Go_to_TomcatManager");
		
		/*Basic auth can be used directly in each http request header as follow
		 * Currenlty the basic auth is set in the http client
		String auth = "tomcat" + ":" + "tomcat";
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.ISO_8859_1));
		String authHeader = "Basic " + new String(encodedAuth);*/
		
		builder = factory.createObjectBuilder();
		builder.add("protocol", "http://")
			.add("host", "localhost")
			.add("port", "8088")
			.add("uri", "/manager/html")
			.add("method", "GET")
			.add("header_Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
			.add("header_Accept-Encoding", "gzip, deflate")
			.add("header_User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36")
	//		.add("header_"+HttpHeaders.AUTHORIZATION, authHeader)
			.add("check_title", ">Tomcat Web Application Manager</font>")
			.add("extract_CSRF_NONCE", "filters.CSRF_NONCE=([^\"]*)\">");
		
		String input = builder.build().toString();

		System.out.println(input);
		output = ctx.run("HttpRequestKW", input);
		System.out.println(output.getPayload().toString());
		assertEquals(output.getPayload().getInt("httpStatusCode"),200);
		assertTrue(output.getPayload().getBoolean("check_title"));
		
		String CSRF_NONCE=output.getPayload().getString("extract_CSRF_NONCE");
		
		output = ctx.run("GetCookiesKW", "{}");
		System.out.println(output.getPayload().toString());
		
		builder = factory.createObjectBuilder()
			.add("protocol", "http://")
			.add("host", "localhost")
			.add("port", "8088")
			.add("uri", "/manager/html/stop")
			.add("method", "POST")
			.add("header_Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
			.add("header_Accept-Encoding", "gzip, deflate")
			.add("header_User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36")
		//	.add("header_"+HttpHeaders.AUTHORIZATION, authHeader)
			.add("param_org.apache.catalina.filters.CSRF_NONCE", CSRF_NONCE)
			.add("param_path", "/docs")
			.add("check_title", ">Tomcat Web Application Manager</font>")
			.add("extract_message", "<td class=\"row-left\"><pre>(.*)</pre>");
		
		
		input = builder.build().toString();
		System.out.println(input);
		output = ctx.run("HttpRequestKW", input);
		System.out.println(output.getPayload().toString());
		assertEquals(output.getPayload().getInt("httpStatusCode"),200);
		assertTrue(output.getPayload().getBoolean("check_title"));
		assertTrue(output.getPayload().getString("extract_message").startsWith("OK - Stopped application at context path"));
		
		output = ctx.run("CloseHttpClientKW", "{}");
	}
	
	
	//@Test
	public void googleSearchTest() throws Exception {
		Logger LOG = (Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		((ch.qos.logback.classic.Logger) LOG).setLevel(Level.DEBUG);

		// Open http client
		System.out.println("Init client");
		output = ctx.run("InitHttpClientKW", "{}");
		assertEquals("true", output.getPayload().getString("success"));

		// Navigate to google search page
		System.out.println("Go_to_Google");
		
		builder = factory.createObjectBuilder();
		builder.add("protocol", "https://")
			.add("host", "www.google.com")
			.add("uri", "")
			.add("method", "GET")
			.add("header_Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
			.add("header_Accept-Encoding", "gzip, deflate")
			.add("header_User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36")
			.add("check_title", "<title>Google</title>");

		String input = builder.build().toString();

		System.out.println(input);
		output = ctx.run("HttpRequestKW", input);
		System.out.println(output.getPayload().toString());
		assertEquals(output.getPayload().getInt("httpStatusCode"),200);
		assertTrue(output.getPayload().getBoolean("check_title"));
		
		//search
		System.out.println("Search");
		builder = factory.createObjectBuilder();
		builder.add("protocol", "https://")
			.add("host", "www.google.com")
			.add("uri", "/search?q=exense&oq=exense")
			.add("method", "GET")
			.add("header_Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
			.add("header_Accept-Encoding", "gzip, deflate")
			.add("header_User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36")
			.add("extract_URL", "href=\"https://www.exense.ch/\"");

		input = builder.build().toString();
		System.out.println(input);
		output = ctx.run("HttpRequestKW", input);
		System.out.println(output.getPayload().toString());
		assertEquals(output.getPayload().getInt("httpStatusCode"),200);
		assertEquals(output.getPayload().getString("extract_URL"),"href=\"https://www.exense.ch/\"");
		
		output = ctx.run("CloseHttpClientKW", "{}");
	}

}
