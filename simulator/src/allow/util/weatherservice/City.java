package allow.util.weatherservice;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Container representing a city to request weather data for.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class City {

	// Name of the city.
	private String name;
	
	// Country code of the city.
	private String countryCode;
	
	/**
	 * Constructor.
	 * Creates a new instance of a city container.
	 * 
	 * @param name Name of the city.
	 * @param countryCode Country code of the city.
	 */
	@JsonCreator
	public City(@JsonProperty("Name") String name, @JsonProperty("CountryCode") String countryCode) {
		this.name = name;
		this.countryCode = countryCode;
	}
	
	/**
	 * Returns the name of the city.
	 * 
	 * @return Name of the city.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the country code of the city.
	 * 
	 * @return Country code of the city.
	 */
	public String getCountryCode() {
		return countryCode;
	}
}
