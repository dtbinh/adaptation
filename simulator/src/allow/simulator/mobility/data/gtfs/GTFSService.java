package allow.simulator.mobility.data.gtfs;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Utility class containing properties of a GTFS service.
 * (https://developers.google.com/transit/gtfs/reference)
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public final class GTFSService {
	// Format of dates in GTFS files.
	private static final DateTimeFormatter gtfsDate = DateTimeFormatter.ofPattern("yyyyMMdd");
	
	// Id of service.
	private String serviceId;
	
	// Weekdays service is valid.
	private boolean days[] = new boolean[7];
	
	// Start and ending date of service.
	private LocalDate startDate;
	private LocalDate endDate;
	
	/**
	 * Returns the service id of the service.
	 * 
	 * @return Service id of the service.
	 */
	public String getServiceId() {
		return serviceId;
	}
	
	/**
	 * Returns the days the service is valid on.
	 * 
	 * @return Days the service is valid on.
	 */
	public boolean[] getDays() {
		return days;
	}
	
	/**
	 * Returns the date at which the service becomes valid.
	 * 
	 * @return Date the service becomes valid.
	 */
	public LocalDate startDate() {
		return startDate;
	}
	
	/**
	 * Returns the date after which the service becomes invalid.
	 * 
	 * @return Date after which the service becomes invalid.
	 */
	public LocalDate endDate() {
		return endDate;
	}
	
	private static int SERVICE_ID = 0;
	private static int SERVICE_DAYS_START = 1;
	private static int SERVICE_DAYS_END = 7;
	private static int SERVICE_STARTDATE = 8;
	private static int SERVICE_ENDDATE = 9;
	
	public static GTFSService fromGTFS(String line) throws ParseException {
		GTFSService i = new GTFSService();
		String tokens[] = line.split(",");
		i.serviceId = tokens[SERVICE_ID];
		
		for (int j = SERVICE_DAYS_START; j <= SERVICE_DAYS_END; j++) {
			i.days[j - SERVICE_DAYS_START] = (tokens[j].equals("1"));
		}
		i.startDate = LocalDate.parse(tokens[SERVICE_STARTDATE], gtfsDate);
		i.endDate = LocalDate.parse(tokens[SERVICE_ENDDATE], gtfsDate);
		return i;
	}
}
