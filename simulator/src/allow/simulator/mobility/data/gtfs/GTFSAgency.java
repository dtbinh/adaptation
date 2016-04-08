package allow.simulator.mobility.data.gtfs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)

/**
 * Utility class describing a public transportation agency from GTFS.
 * (https://developers.google.com/transit/gtfs/reference)
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public final class GTFSAgency {
	// Id of agency.
	private String id;
	
	// Name of agency.
	private String name;

	/**
	 * Constructor.
	 * Creates a new agency info class describing a transportation agency from GTFS.
	 * 
	 * @param id Id of the agency.
	 * @param name Name of the agency.
	 */
	@JsonCreator
	public GTFSAgency(@JsonProperty("id") String id,
			@JsonProperty("name") String name) {
		this.id = id;
		this.name = name;
	}
	
	/**
	 * Returns the Id of the agency.
	 * 
	 * @return Id of the agency.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Returns the name of the agency.
	 * 
	 * @return Name of the agency.
	 */
	public String getName() {
		return name;
	}
	
	// Constants when agency is read from a GTFS file directly.
	private static int AGENCY_ID = 0;
	private static int AGENCY_NAME = 1;

	/**
	 * Parses a GTFSAgency from a line in the corresponding GTFS file (agency.txt).
	 * 
	 * @param line Line from GTFS file (agency.txt).
	 * @return GTFSAgency parsed from GTFS file.
	 */
	public static GTFSAgency fromGTFS(String line) {
		String tokens[] = line.split(",");
		return new GTFSAgency(tokens[AGENCY_ID], tokens[AGENCY_NAME]);
	}
}
