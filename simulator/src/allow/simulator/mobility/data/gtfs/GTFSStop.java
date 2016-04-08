package allow.simulator.mobility.data.gtfs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)

/**
 * Utility class describing a stop of a means of public transportation.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class GTFSStop {
	// Id of this stop.
	private String id;
	
	// Name of this stop.
	private String name;
	
	// Latitude of this stop.
	private double lat;
	
	// Longitude of this stop.
	private double lon;
		
	/**
	 * Constructor.
	 * Creates a new stop info class describing a stop of means of public
	 * transportation.
	 * 
	 * @param id Id of the stop.
	 * @param name Name of the stop.
	 * @param lat Latitude of the stop.
	 * @param lon Longitude of the stop.
	 */
	@JsonCreator
	public GTFSStop(@JsonProperty("id") String id,
			@JsonProperty("name") String name,
			@JsonProperty("lat") double lat,
			@JsonProperty("lon") double lon) {
		this.id = id;
		this.name = name;
		this.lat = lat;
		this.lon = lon;
	}
	
	/**
	 * Returns Id of the stop.
	 * 
	 * @return Id of the stop.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Returns name of the stop.
	 * 
	 * @return Name of the stop.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns latitude of the stop.
	 * 
	 * @return Latitude of the stop.
	 */
	public double getLat() {
		return lat;
	}
	
	/**
	 * Returns longitude of the stop.
	 * 
	 * @return Longitude of the stop.
	 */
	public double getLon() {
		return lon;
	}
	
	private static int STOP_ID = 0;
	private static int STOP_NAME = 1;
	private static int STOP_LAT = 3;
	private static int STOP_LON = 4;
	
	/**
	 * Parse stop info from line in GTFS file.
	 * 
	 * @param line Line from GTFS file.
	 * @return Stop info parsed from GTFS file.
	 */
	public static GTFSStop fromGTFS(String line) {
		String tokens[] = line.split(",");
		return new GTFSStop(tokens[STOP_ID],
				tokens[STOP_NAME],
				Double.parseDouble(tokens[STOP_LAT]),
				Double.parseDouble(tokens[STOP_LON]));
	}
}
