package allow.util.weatherservice;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Configuration {

	// Date format to parse starting and ending date.
	private static SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
	
	// Day to start requests.
	private Date startingDate;
	
	// Day to end requests.
	private Date endingDate;
	
	// Key to use API.
	private String apiKey;
	
	// City to request weather data from.
	private City city;
	
	/**
	 * Constructor.
	 * Creates a new configuration for the Allow Ensembles simulator.
	 * 
	 * @param startingDate Starting date of the simulation.
	 * @param plannerService Planner service configuration.
	 * @param dataService Data service configuration.
	 * @param tracesOutputPath Path to file to write traces to.
	 * @param worldPath Path containing simulated world.
	 * @throws ParseException 
	 */
	@JsonCreator
	public Configuration(@JsonProperty("StartingDate") String startingDate, 
			@JsonProperty("EndingDate") String endingDate,
			@JsonProperty("APIKey") String apiKey,
			@JsonProperty("City") City city) throws ParseException {
		this.startingDate = format.parse(startingDate);
		this.endingDate = format.parse(endingDate);
		
		if (this.startingDate.after(this.endingDate)) {
			throw new IllegalArgumentException("Error: Starting date is after ending date.");
		}
		this.apiKey = apiKey;
		this.city = city;
	}
	
	/**
	 * Returns the starting date, i.e. the first day a request should be made for.
	 * 
	 * @return Starting date.
	 */
	public Date getStartingDate() {
		return startingDate;
	}
	
	/**
	 * Returns the ending date, i.e. the last day a request should be made for.
	 * 
	 * @return Ending date.
	 */
	public Date getEndingDate() {
		return endingDate;
	}
	
	/**
	 * Returns the specified API key to query the weather API.
	 * 
	 * @return API key to query the weather API.
	 */
	public String getAPIKey() {
		return apiKey;
	}
	
	/**
	 * Returns the city to query the weather for in the specified period of time.
	 * 
	 * @return City to query the weather for.
	 */
	public City getCity() {
		return city;
	}
	
	/**
	 * Returns a new configuration from JSON file.
	 * 
	 * @param filePath File containing JSON description of configuration.
	 * @return Configuration read from JSON file.
	 * @throws IOException
	 */
	public static Configuration fromJSON(File filePath) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(filePath, Configuration.class);
	}
}
