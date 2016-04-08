package allow.util.weatherservice.wunderground;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Utility class representing a date of a Wunderground service response.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Date {
	
	// Date members.
	private int year;
	private int mon;
	private int mday;
	private int hour;
	private int min;
	
	/**
	 * Constructor.
	 * Creates a new instance of a observation Date for the Wunderground
	 * weather service API.
	 * 
	 * @param pretty Not used.
	 * @param year Year of the observation.
	 * @param mon Month of the observation.
	 * @param mday Day of the observation.
	 * @param hour Hour of the observation.
	 * @param min Minute of the observation.
	 * @param tzname Not used.
	 */
	public Date(@JsonProperty("year") String year,
			@JsonProperty("mon") String mon,
			@JsonProperty("mday") String mday,
			@JsonProperty("hour") String hour,
			@JsonProperty("min") String min) {
		this.year = Integer.parseInt(year);
		this.mon = Integer.parseInt(mon);
		this.mday = Integer.parseInt(mday);
		this.hour = Integer.parseInt(hour);
		this.min = Integer.parseInt(min);
	}
	
	/**
	 * Returns the year of this date.
	 * 
	 * @return Year of this date.
	 */
	public int getYear() {
		return year;
	}
	
	/**
	 * Returns the month of this date.
	 * 
	 * @return Month of this date.
	 */
	public int getMonth() {
		return mon;
	}
	
	/**
	 * Returns the day of this date.
	 * 
	 * @return Day of this date.
	 */
	public int getDay() {
		return mday;
	}
	
	/**
	 * Returns the hour of this date.
	 * 
	 * @return Hour of this date.
	 */
	public int getHour() {
		return hour;
	}
	
	/**
	 * Returns the minute of this date.
	 * 
	 * @return Minute of this date.
	 */
	public int getMinute() {
		return min;
	}
}