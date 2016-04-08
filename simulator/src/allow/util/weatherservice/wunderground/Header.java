package allow.util.weatherservice.wunderground;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Utility class representing a header of a Wunderground service response.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class Header {

	// API version.
	private String version;
	
	/**
	 * Constructor.
	 * Creates a new instance of a header for the Wunderground
	 * weather service API.
	 * 
	 * @param version API version.
	 * @param termsofService Not used.
	 * @param features Not used.
	 */
	@JsonCreator
	public Header(@JsonProperty("version") String version,
			@JsonProperty("termsofService") String termsofService,
			@JsonProperty("features") Features features) {
		this.version = version;
	}
	
	/**
	 * Returns the API version of the response.
	 * 
	 * @return API version of the response.
	 */
	public String getVersion() {
		return version;
	}
	
}
