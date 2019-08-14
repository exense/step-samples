package ch.exense.step.examples.http;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;

import javax.json.JsonObject;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HttpRequest extends HttpEntityEnclosingRequestBase {
	private static final Logger logger = LoggerFactory.getLogger(HttpRequest.class);
	String method = HttpGet.METHOD_NAME;
	String body="";
	
	public HttpRequest(String uri, String method) {
		this.method = method;
		setURI(URI.create(uri));
		
	}

	@Override
	public String getMethod() {
		return method;
	}
	
	public HttpRequest appendHeader(String key, String value){
		this.addHeader(key, value);
		return this;
	}
	
	public HttpRequest setHeaders(JsonObject headers){
		for (String key: headers.keySet()) {
			this.appendHeader(key, headers.getString(key));
		}
		return this;
	}
	
	public HttpRequest setParams(List<NameValuePair> params) throws UnsupportedEncodingException{
		setEntity(new UrlEncodedFormEntity(params));
		return this;
	}
	
	public HttpRequest setRowPayload(String payload) throws UnsupportedEncodingException{
		body=payload;
		setEntity(new StringEntity(payload,ContentType.create("text/plain", "UTF-8")));
		return this;
	}
	
	protected void logDebugInfo(){
		logger.debug("Request URI: " + (this.getURI()));
		logger.debug("Request headers: " + this.getAllHeaders());
		logger.debug("Request method: " + this.getMethod());
		if (method.equals(HttpPost.METHOD_NAME)) {
			logger.debug("Request body: " + body);
		}
	}
	
}
