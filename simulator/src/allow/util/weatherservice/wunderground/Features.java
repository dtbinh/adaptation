package allow.util.weatherservice.wunderground;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Utility class representing a features entry of a Wunderground service response.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Features {

	// History feature.
	private boolean history;
	
	/**
	 * Constructor.
	 * Creates a new instance of feature entries of a Wunderground service
	 * response.
	 * 
	 * @param history Indicates, if response contains history features.
	 */
	@JsonCreator
	public Features(@JsonProperty("history") String history) {
		this.history = Boolean.parseBoolean(history);
	}
	
	/**
	 * Returns true if response contains history features.
	 * 
	 * @return True if response contains history features, false otherwise.
	 */
	public boolean getHistory() {
		return history;
	}
}
