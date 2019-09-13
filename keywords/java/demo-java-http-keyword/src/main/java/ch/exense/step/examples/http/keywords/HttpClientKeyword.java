package ch.exense.step.examples.http.keywords;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import ch.exense.step.examples.common.helper.AbstractEnhancedKeyword;
import ch.exense.step.examples.http.HttpClient;
import ch.exense.step.examples.http.HttpRequest;
import ch.exense.step.examples.http.HttpResponse;
import step.handlers.javahandler.Keyword;

public class HttpClientKeyword extends AbstractEnhancedKeyword {
	
	private boolean returnResponsePayload=true;
	public static final String HEADER_PREFIX="header_";
	public static final String PARAM_PREFIX="param_";
	public static final String EXTRACT_PREFIX="extract_";
	public static final String CHECK_PREFIX="check_";

	/**
	 * step Keyword to init an Apache HTTP clients
	 * the client will be placed in the current step session
	 * 
	 * Keyword inputs
	 *   keyStorePath (optional)
	 *   keyStorePassword (mandatory is keyStorePath is provided)
	 *   targetIP: enable a custom DNS resolver, requires hostWithCustomDns
	 *   hostWithCustomDns: requests to this host will be resolved to the "targetIP"
	 *   basic_auth_user: set basic authenticate user name (require password) 
	 *   basic_auth_password: set basic authentication password
	 *   basic_auth_host: enable preemptive authentication (require the 5 bascic_auth fields)
	 *   basic_auth_host_scheme: target host scheme (i.e. https)
	 *   basic_auth_port: target host port number
	 *   
	 */
	@Keyword
	public void InitHttpClientKW() {	
		try {
			HttpClient httpClient=null;
			String keyStorePath = null;
			String keyStorePassword = null;
			String targetIP = null;
			String hostWithCustomDns = null;
			String basic_auth_host_scheme = null;
			String basic_auth_host = null;
			int basic_auth_port = 0;
			String basic_auth_user = null;
			String basic_auth_password = null;
			
			if(input.containsKey("keyStorePath")) {
				if (!input.containsKey("keyStorePassword")) {
					failWithErrorMessage("keyStorePath provided without password.");
				} else {
					keyStorePath = input.getString("keyStorePath");
					keyStorePassword = input.getString("keyStorePassword");
				}
			} 
			if (input.containsKey("targetIP")) {
				if (!input.containsKey("hostWithCustomDns")) {
					failWithErrorMessage("targetIP provided wihtout 'hostWithCustomDns'.");
				} else {
					targetIP = input.getString("targetIP");
					hostWithCustomDns = input.getString("hostWithCustomDns");
				}
			}
			if (input.containsKey("basic_auth_user")) {
				if (!input.containsKey("basic_auth_password")) {
					failWithErrorMessage("basic_auth_user provided wihtout 'basic_auth_password'.");
				} else {
					basic_auth_user = input.getString("basic_auth_user");
					basic_auth_password = input.getString("basic_auth_password");
					if (input.containsKey("basic_auth_host")) {
						if (!input.containsKey("basic_auth_port") || !input.containsKey("basic_auth_host_scheme")) {
							failWithErrorMessage("basic_auth_host provided wihtout 'basic_auth_port' or 'basic_auth_host_scheme'.");
						} else {
							basic_auth_host = input.getString("basic_auth_host");
							basic_auth_host_scheme = input.getString("basic_auth_host_scheme");
							basic_auth_port = input.getInt("basic_auth_port");
						}
					}
				}
			}
			httpClient = new HttpClient(keyStorePath, keyStorePassword, targetIP, hostWithCustomDns, 
					basic_auth_host_scheme, basic_auth_host, basic_auth_port, basic_auth_user, basic_auth_password);

			getSession().put("httpClient", httpClient);
			setSuccess();
			
			
       } catch(Exception e) {
    	   failWithException(e);
        }
	}

	/**
	 * step Keyword to close the Apache HTTP clients stored in the step session
	 */
	@Keyword
	public void CloseHttpClientKW() {	
		HttpClient httpClient = (HttpClient) getSession().get("httpClient");
		httpClient.close();
		setSuccess();
	}
	
	
	
	/**
	 * step Keyword to execute one HTTP request
	 * 
	 * Keyword inputs:
	 *   name (optional): name of the request used for RTM measurements (default: defaultName) 
	 *   protocol: (i.e. http://,https://...)
	 *   host: hostname or ip address 
	 *   port (optional): (i.e. 8080
	 *   uri: the remaining part of the URL
	 *   method: GET, POST (other methods might be supported)
	 *   header_* (optional): naming convention to pass header parameters, ex:
	 *     key: header_accept, value: application/json, text/plain
	 *     key: header_accept-language, value: en-US,en;q=0.9,fr;q=0.8,de;q=0.7
	 *   payload (optional): body payload as string (used only for Post method and only if param_* are not set)
	 *   param_* (optional): naming convention to pass request parameters, ex:
	 *     key: param_user, value: myname
	 *     key: param_password, value: mypassword
	 *   extract_*: naming convention to pass content check strings, ex:
	 *     key: extract_myId, value: regexp as string with one group
	 *   check_* (optional): naming convention to pass content check strings, ex:
	 *     key: check_pageTitle, value: 'my web site title'
	 *   returnResponsePayload: default true
	 *   
	 *  
	 *  Keyword output
	 *    success: Keyword status (false only in case of exceptions)
	 *    httpStatusCode: Request status code
	 *    headers: headers
	 *    cookies: cookies set in this response's header
	 *    payload: response payload (depends on input returnResponsePayload, default true)
	 *    extract_*: extracted fields
	 *    check_*: content checks result (true if found)
	 *    Error: Error message if any
	 *    
	 * @throws Exception
	 */
	@Keyword
	public void HttpRequestKW() throws Exception {	
		checkMandatoryInputs("protocol","host", "uri","method");
		String requestName = (input.containsKey("name")) ? input.getString("name") : "defaultName"; 
		output.startMeasure(requestName + "_AGENT");
		HttpClient httpClient = (HttpClient) getSession().get("httpClient");
		String protocol = input.getString("protocol");
		String host = input.getString("host");
		String port = (input.containsKey("port")) ? ":" + input.getString("port") : "";
		String uri = input.getString("uri");
		String method = input.getString("method");
		
		boolean addResponsePayload = (input.containsKey("returnResponsePayload")) ? input.getBoolean("returnResponsePayload") : returnResponsePayload;
		
		//Init request
		HttpRequest request = new HttpRequest(protocol + host + port + uri, method);
		
		//Extract all dynamic and optional inputs
		String payload = "";
		List<NameValuePair> params = new ArrayList<NameValuePair> ();
		Map<String,Pattern> extractRegexp = new HashMap <String,Pattern>();
		Map<String,String> textChecks = new HashMap <String,String>();
		for (String key: input.keySet()) {
			if (key.startsWith(HEADER_PREFIX)) {
				String name = key.substring(HEADER_PREFIX.length());
				request.appendHeader(name, input.getString(key));
			} else if (key.startsWith(PARAM_PREFIX)) {
				String name = key.substring(PARAM_PREFIX.length());
				params.add(new BasicNameValuePair(name, input.getString(key)));
			} else if (key.startsWith(EXTRACT_PREFIX)) {
				String name = key.substring(EXTRACT_PREFIX.length());
				extractRegexp.put(name, Pattern.compile(input.getString(key),Pattern.DOTALL));
			} else if (key.startsWith(CHECK_PREFIX)) {
				String name = key.substring(CHECK_PREFIX.length());
				textChecks.put(name, input.getString(key));
			} else if (key.equals("payload")) {
				payload = input.getString("payload");
			}
		}
		if (!params.isEmpty()) {
			request.setParams(params);
		} else if (!payload.isEmpty()) {
			request.setRowPayload(payload);
		}
		
		Map<String, Object> networkExceptionMap = new HashMap<>();
		networkExceptionMap.put("DestIP", httpClient.getTargetIP());
		try {
			output.startMeasure(requestName + "_NETWORK");
			HttpResponse httpResponse = httpClient.executeRequestInContext(request);
			output.stopMeasure(networkExceptionMap);
			
			httpResponse.logDebugInfo();
			httpResponse.extractAllInfo(output, addResponsePayload);
			//extract all fields
			for (String key: extractRegexp.keySet()) {
				String value = "";
				Matcher m = extractRegexp.get(key).matcher(httpResponse.getResponsePayload());
				if (m.find()) {
					//return 1st group if defined or full match
					try {
						value = m.group(1);
					} catch (Exception e) {
						value = m.group(0);
					}
				}
				output.add(EXTRACT_PREFIX.concat(key), value);
			}
			
			//do all checks
			for (String key: textChecks.keySet()) {
				output.add(CHECK_PREFIX.concat(key), httpResponse.getResponsePayload().contains(textChecks.get(key)));
			}
			setSuccess();
			
		} catch (Exception e) {
			//stop network measure
			networkExceptionMap.put("ExceptionType", e.getClass().toString());
			networkExceptionMap.put("ExceptionMessage", (e.getMessage() == null) ? "null" : e.getMessage());
			output.stopMeasure(networkExceptionMap);
			failWithException(e);
		}
		output.stopMeasure(networkExceptionMap);
	}
	
	@Keyword
	public void GetCookiesKW() throws Exception {
		HttpClient httpClient = (HttpClient) getSession().get("httpClient");
		output.add("cookies", httpClient.getCookiesFromStore().toString());
	}
	
	@Keyword
	public void AddCookiesKW() {	
		Arrays.asList("cookies")
			.forEach(in -> { if(!input.containsKey(in)) failWithErrorMessage("Input " + in + " is null"); } );
		HttpClient httpClient = (HttpClient) getSession().get("httpClient");
		String cookiesStr = input.getString("cookies");
		List<String> cookiesList = new ArrayList<String>(Arrays.asList(cookiesStr.substring(1, cookiesStr.length()-1).split(",")));
		httpClient.setCookiesToStore(cookiesList);
		output.add("cookies", httpClient.getCookiesFromStore().toString());
	}
	
	@Keyword
	public void EnableProxyKW() {
		
		
		System.setProperty("http.proxyHost", input.getString("proxyHost"));
	    System.setProperty("http.proxyPort", input.getString("proxyPort"));
	    System.setProperty("http.nonProxyHosts", "*.pfin.ch|*.post.ch|*.pnet.ch|*.postfinance.ch");
	    System.setProperty("https.proxyHost",  input.getString("proxyHost"));
	    System.setProperty("https.proxyPort", input.getString("proxyPort"));
	    System.setProperty("https.nonProxyHosts", "*.pfin.ch|*.post.ch|*.pnet.ch|*.postfinance.ch");
	    System.setProperty("java.net.preferIPv4Stack", "true");
	    
	}
	
	
	@Keyword
	public void DisableProxyKW() {
		System.setProperty("http.proxyHost", "");
	    System.setProperty("http.proxyPort", "");
	    System.setProperty("http.nonProxyHosts", "");
	    System.setProperty("https.proxyHost", "");
	    System.setProperty("https.proxyPort", "");
	    System.setProperty("https.nonProxyHosts", "");
	    System.setProperty("java.net.preferIPv4Stack", "false");
	    
	}
	
	@Keyword
	public void ShowProxySettingKW() {
		output.add("http.proxyHost", System.getProperty("http.proxyHost"));
		output.add("http.proxyPort", System.getProperty("http.proxyPort"));
		output.add("https.proxyHost", System.getProperty("https.proxyHost"));
		output.add("https.proxyPort", System.getProperty("https.proxyPort"));
		output.add("java.net.preferIPv4Stack", System.getProperty("java.net.preferIPv4Stack"));
		
	}
}
