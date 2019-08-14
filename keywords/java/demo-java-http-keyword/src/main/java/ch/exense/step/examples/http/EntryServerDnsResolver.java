package ch.exense.step.examples.http;

import java.net.InetAddress;

import java.net.UnknownHostException;

import org.apache.http.impl.conn.SystemDefaultDnsResolver;

/**
 * 
 * Custom DNS resolver to be used with the http client
 * 
 * Allows to resolve given host name to a specific IP for simulating load
 * balancing
 * 
 * across multiple entry servers
 * 
 * @author stephanda
 *
 * 
 * 
 */
public class EntryServerDnsResolver extends SystemDefaultDnsResolver {

	private String hostWithCustomDns;
	private InetAddress[] targetIP;

	public EntryServerDnsResolver(String targetIP, String hostWithCustomDns) throws UnknownHostException {
		this.targetIP = new InetAddress[] { InetAddress.getByName(targetIP) };
		this.hostWithCustomDns = hostWithCustomDns.replaceFirst("https://", "").replaceFirst("http://", "");
	}

	@Override
	public InetAddress[] resolve(String host) throws UnknownHostException {
		// If hostname is the one to be resolved to specific IP
		if (host.contains(hostWithCustomDns)) {
			return targetIP;
			// else fallback to default DNS resolver
		} else {
			return super.resolve(host);
		}
	}

}