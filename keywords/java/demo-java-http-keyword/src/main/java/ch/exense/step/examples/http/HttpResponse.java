package ch.exense.step.examples.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import step.functions.io.OutputBuilder;

public class HttpResponse {
	private static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);
	
	private String responsePayload;
	private List<BasicNameValuePair> responseHeaders;
	private List<String> cookies;
	private int status;
	
	public HttpResponse(String responsePayload, List<BasicNameValuePair> responseHeaders, int status) throws Exception {
		super();
		this.responsePayload = responsePayload;
		this.responseHeaders = responseHeaders;
		this.cookies = buildCookies(responseHeaders);
		this.status = status;
	}

	public String getResponsePayload() {
		return responsePayload;
	}

	public List<BasicNameValuePair> getResponseHeaders() {
		return responseHeaders;
	}

	public List<String> getCookies() {
		return cookies;
	}
	
	public void setCookies(List<String> cookies) {
		this.cookies = cookies;
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	protected List<String> buildCookies(List<BasicNameValuePair> headers) throws Exception{
		if(headers == null)
			throw new Exception("Header list not initialized. Did you execute a (successful) request?");
		List<String> cookies = new ArrayList<String>();
		for(BasicNameValuePair nvp : headers){
			if(nvp.getName().trim().contains("Set-Cookie"))
				cookies.add(nvp.getValue());
		}
		return cookies;
	}
	
	public void logDebugInfo() {
<<<<<<< HEAD
		if (logger.isDebugEnabled()) {
			logger.debug("Http response code: " + status);
			logger.debug("Http Response header: " + getResponseHeaders().toString());
			logger.debug("Cookies: " + getCookies().toString());
	//		logger.debug("Response Payload: " + getResponsePayload());
		}
=======

		logger.debug("Http response code: " + status);
		logger.debug("Http Response header: " + getResponseHeaders().toString());
		logger.debug("Cookies: " + getCookies().toString());
//		logger.debug("Response Payload: " + getResponsePayload());
>>>>>>> refs/remotes/origin/master
	
	}

	public void extractAllInfo(OutputBuilder output, boolean returnResponsePayload) {
		output.add("httpStatusCode", status);
		output.add("headers", getResponseHeaders().toString());
		output.add("cookies", getCookies().toString());
		if (returnResponsePayload) {
			output.add("payload", getResponsePayload());
		}
	}
}
