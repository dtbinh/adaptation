package allow.simulator.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Describes the configuration of a service.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class Service {
	
	/**
	 * Service URL.
	 */
	private String url;
	
	/**
	 * Service port.
	 */
	private int port;
	
	/**
	 * Constructor.
	 * Creates a new service description specifying URL and port.
	 * 
	 * @param url URL of service.
	 * @param port Port to use.
	 */
	@JsonCreator
	public Service(@JsonProperty("url") String url, @JsonProperty("port") int port) {
		this.url = url;
		this.port = port;
	}
	
	
	/**
	 * Returns if service is available over network (i.e. port is not -1).
	 * 
	 * @return True, if service is a network service, false otherwise.
	 */
	public boolean isOnline() {
		return (port != -1);
	}
	
	/**
	 * Returns URL of service in case of web service or directory containing configuration for
	 * local (emulated) service.
	 * 
	 * @return URL of service or working directory for local service.
	 */
	public String getURL() {
		return url;
	}
	
	/**
	 * Returns port of service in case of web service or -1 for local (emulated) service.
	 * 
	 * @return Port of service or -1 for local service.
	 */
	public int getPort() {
		return port;
	}
}
