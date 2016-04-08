package allow.util.weatherservice.wunderground;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Utility class representing a daily summary of a Wunderground service response.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DailySummary {

	// Summary report members.
	private Date date;
	private boolean fog;
	private boolean rain;
	private boolean snow;
	private boolean hail;
	private boolean thunder;
	private boolean tornado;
	private double meantempm;
	
	/**
	 * Constructor.
	 * Creates a new instance of a daily summary of the Wunderground service
	 * API response for historical weather data.
	 * 
	 * @param date Summary date.
	 * @param fog Flag indicating if there was fog.
	 * @param rain Flag indicating if there was rain.
	 * @param snow Flag indicating if there was snow.
	 * @param hail Flag indicating if there was hail.
	 * @param thunder Flag indicating if there was a thunderstorm.
	 * @param tornado Flag indicating if there was a tornado.
	 * @param meantempm Mean temperature of the day.
	 */
	@JsonCreator
	public DailySummary(@JsonProperty("date") Date date,
			@JsonProperty("fog") String fog,
			@JsonProperty("rain") String rain,
			@JsonProperty("snow") String snow,
			@JsonProperty("hail") String hail,
			@JsonProperty("thunder") String thunder,
			@JsonProperty("tornado") String tornado,
			@JsonProperty("meantempm") String meantempm) {
		this.date = date;
		this.fog = Boolean.parseBoolean(fog);
		this.rain = Boolean.parseBoolean(rain);
		this.snow = Boolean.parseBoolean(snow);
		this.hail = Boolean.parseBoolean(hail);
		this.thunder = Boolean.parseBoolean(thunder);
		this.tornado = Boolean.parseBoolean(tornado);
		this.meantempm = Double.parseDouble(meantempm);
	}
	
	/**
	 * Returns date of the summary.
	 * 
	 * @return Date of summary.
	 */
	public Date getDate() {
		return date;
	}
	
	/**
	 * Returns the mean temperature of the summary.
	 * 
	 * @return Mean temperature of the summary.
	 */
	public double getMeanTemperature() {
		return meantempm;
	}
	
	/**
	 * Returns if the weather is foggy in this summary.
	 * 
	 * @return True, if weather is foggy in this summary, false otherwise.
	 */
	public boolean getFog() {
		return fog;
	}
	
	/**
	 * Returns if the weather is rainy in this summary.
	 * 
	 * @return True, if weather is rainy in this summary, false otherwise.
	 */
	public boolean getRain() {
		return rain;
	}
	
	/**
	 * Returns if the weather is snowy in this summary.
	 * 
	 * @return True, if weather is snowy in this summary, false otherwise.
	 */
	public boolean getSnow() {
		return snow;
	}
	
	/**
	 * Returns if there is hail in this summary.
	 * 
	 * @return True, if there is hail in this summary, false otherwise.
	 */
	public boolean getHail() {
		return hail;
	}
	
	/**
	 * Returns if there is a thunderstorm in this summary.
	 * 
	 * @return True, if there is a thunderstorm in this summary, false otherwise.
	 */
	public boolean getThunder() {
		return thunder;
	}
	
	/**
	 * Returns if there is a tornado in this summary.
	 * 
	 * @return True, if there is a tornado in this summary, false otherwise.
	 */
	public boolean getTornado() {
		return tornado;
	}
}
