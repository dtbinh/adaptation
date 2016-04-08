package allow.util.weatherservice.wunderground;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a response of the Wunderground weather API for historical
 * weather data.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class Response {

	// Response header.
	private Header header;
	
	// List of historical weather observations of that day.
	private History history;

	/**
	 * Constructor.
	 * Creates a new instance of a response of the Wunderground weather API for
	 * historical weather data.
	 * 
	 * @param header Response header.
	 * @param history Weather history.
	 * @param summary Daily summary.
	 */
	@JsonCreator
	public Response(@JsonProperty("response") Header header,
			@JsonProperty("history") History history) {
		this.header = header;
		this.history = history;
	}
	
	/**
	 * Returns the header of this service response.
	 * 
	 * @return Header of this service response.
	 */
	public Header getHeader() {
		return header;
	}
	
	/**
	 * Returns the weather history of this response.
	 * 
	 * @return Weather history of this response.
	 */
	public History getHistory() {
		return history;
	}
}
