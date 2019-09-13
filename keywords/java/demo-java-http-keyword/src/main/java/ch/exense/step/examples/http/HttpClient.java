package ch.exense.step.examples.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.json.JsonObject;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

public class HttpClient {

	private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

	protected CloseableHttpClient client;
	protected List<BasicNameValuePair> lastResponseHeaders;
	protected HttpRequestBase request = null;
	protected HttpClientContext context = null;
	protected String targetIP = "";
				 
	/**
	* Create a client and its context with provided SSL information, auth cache
	* and optionally a custom DNS resolved for load balancing
	* @param jksPath
	* @param password
	* @param targetIP
	* @param hostWithCustomDns: resolved to the target IP if provided
	* @throws KeyStoreException
	* @throws NoSuchAlgorithmException
	* @throws CertificateException
	* @throws IOException
	* @throws UnrecoverableKeyException
	* @throws KeyManagementException
	*/
	public HttpClient(String jksPath, String password, String targetIP, String hostWithCustomDns, String basic_auth_host_scheme,
			String basic_auth_host, int basic_auth_port, String basic_auth_user, String basic_auth_password )
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException,
			UnrecoverableKeyException, KeyManagementException {
		
		//TODO remove all code from init
		//initClient();
		Logger LOG = (Logger) org.slf4j.LoggerFactory.getLogger("org.apache.http");
		((ch.qos.logback.classic.Logger) LOG).setLevel(Level.DEBUG);
		
		
		
		HttpClientBuilder httpClientBuilder = HttpClients.custom();
		if (jksPath != null && password != null) {
			SSLContext sc = setSSLContext(jksPath, password);
			httpClientBuilder.setSSLContext(sc);		
		}
		
		// If provided add a custom DNS resolver which will resolve the
		// 'hostWithCustomDns' to the provided 'targetIP'
		if (targetIP != null && !targetIP.isEmpty()) {
			this.targetIP = targetIP;
			httpClientBuilder.setDnsResolver(new EntryServerDnsResolver(targetIP, hostWithCustomDns));
		}
		
		// Create the http context and init Auth cache
		context = HttpClientContext.create();
		AuthCache authCache = new BasicAuthCache();
		
		/*basic auth (auth provided upon challenge) */
		 CredentialsProvider provider = null;
		 if (basic_auth_user != null && basic_auth_password != null) {
			 provider = new BasicCredentialsProvider();
			 UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(basic_auth_user, basic_auth_password);
			 provider.setCredentials(AuthScope.ANY, credentials);
		 }
		 
		/*if Preemptive basic auth set it in the http context*/
		if (basic_auth_host_scheme != null && basic_auth_host != null && basic_auth_port > 0 && provider != null) {
			HttpHost targetHost = new HttpHost(basic_auth_host, basic_auth_port, basic_auth_host_scheme);
			authCache.put(targetHost, new BasicScheme());
			context.setCredentialsProvider(provider);
		//else if basic authentication set it as defeult in the client
		} else if (provider != null) {
			httpClientBuilder.setDefaultCredentialsProvider(provider);
		}
		
		//Build the client
		this.client = httpClientBuilder.build();
		  		
		// context.setCredentialsProvider(credsProvider);
		// Add AuthCache to the execution context
		context.setAuthCache(authCache);
	}
	
	private SSLContext setSSLContext(String jksPath, String password) throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException, IOException, UnrecoverableKeyException, KeyManagementException {
		FileInputStream instream = new FileInputStream(new File(jksPath));
		KeyStore keyStore = KeyStore.getInstance("jks");
		keyStore.load((InputStream) instream, password.toCharArray());

		KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		keyFactory.init(keyStore, password.toCharArray());
		KeyManager[] keyManagers = keyFactory.getKeyManagers();

		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };

		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(keyManagers, trustAllCerts, new SecureRandom());

		return sc;
		//this.client = HttpClients.custom().setSSLContext(sc).build();
	}

	protected String readResponse(CloseableHttpResponse response) throws UnsupportedOperationException, IOException {
		this.lastResponseHeaders = toNameValues(Arrays.asList(response.getAllHeaders()));
		if (response.getEntity() != null) {
			return EntityUtils.toString(response.getEntity());
		} else {
			return "";
		}

	}

	private static List<BasicNameValuePair> toNameValues(List<Header> headerList) {
		List<BasicNameValuePair> basicnvp = new ArrayList<>();
		for (Header h : headerList)
			basicnvp.add(new BasicNameValuePair(h.getName(), h.getValue()));
		return basicnvp;
	}

	protected List<BasicNameValuePair> getResponseHeaders() throws Exception {
		if (this.lastResponseHeaders == null)
			throw new Exception("Header list not initialized. Did you execute a (successful) request?");
		return this.lastResponseHeaders;
	}

	public HttpResponse executeRequestInContext(HttpRequest request)
			throws ClientProtocolException, IOException, Exception {
		request.logDebugInfo();
		CloseableHttpResponse httpResponse = this.client.execute(request, context);
		String response = readResponse(httpResponse);
		int status = httpResponse.getStatusLine().getStatusCode();
		return new HttpResponse(response, this.lastResponseHeaders, status);
	}

	public HttpResponse executeJsonRequest(String host, JsonObject requestData)
			throws ClientProtocolException, IOException, Exception {
		String uri = requestData.get("uri").toString();
		JsonObject details = requestData.getJsonObject("details");
		String method = details.getString("method");
		HttpRequest request = new HttpRequest(host + uri, method).setHeaders(details.getJsonObject("headers"));
		if (method.equals(HttpPost.METHOD_NAME)) {
			request.setRowPayload(details.getString("body").toString());
		}

		return executeRequestInContext(request);
	}

	public void close() {
		try {
			if (this.client != null)
				this.client.close();
		} catch (IOException e) {
			logger.error("Could not close http client.", e);
		}
	}

	protected static String prettyPrintHeaders(Header[] allHeaders) {
		StringBuilder sb = new StringBuilder();
		for (Header h : allHeaders)
			sb.append(h.getName()).append("=").append(h.getValue()).append(";");
		return sb.toString();
	}

	/**
	* Extract the list of all cookies in the current client store
	*/
	public List<String> getCookiesFromStore() {
		List<String> cookieNames = new ArrayList<String>();
		for (Cookie cookie : context.getCookieStore().getCookies()) {

			cookieNames.add(cookie.getName() + "=" + cookie.getValue() + "; Domain=" + cookie.getDomain() + "; Path="
					+ cookie.getPath());
		}
		return cookieNames;
	}
	
	public void setCookiesToStore (List<String> cookies) {
		//Cookie store only created after first request
		if (context.getCookieStore() == null) {
			context.setCookieStore(new BasicCookieStore());
		}
		for (String cookieStr: cookies) {
			String[] cookieArray = cookieStr.split(";");
			String[] nameValuePair = cookieArray[0].trim().split("=");
			BasicClientCookie cookie = new BasicClientCookie(nameValuePair[0].trim(), nameValuePair[1].trim());
			nameValuePair = cookieArray[1].trim().split("=");
			cookie.setDomain(nameValuePair[1].trim());
			nameValuePair = cookieArray[2].trim().split("=");
			cookie.setPath(nameValuePair[1].trim());	
			context.getCookieStore().addCookie(cookie);
		}
	}

	public String getTargetIP() {
		return this.targetIP;
	}													
}
