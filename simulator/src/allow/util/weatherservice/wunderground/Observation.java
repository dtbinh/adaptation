package allow.util.weatherservice.wunderground;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Utility class representing an observation of a Wunderground service response.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Observation {

	// Date of observation in respective timezone.
	private Date date;
	
	// UTC timezone date.
	private Date utcdate;
	
	// Weather observations.
	private double tempm;
	private String conds;
	private boolean fog;
	private boolean rain;
	private boolean snow;
	private boolean hail;
	private boolean thunder;
	private boolean tornado;
	
	/**
	 * Constructor.
	 * Creates a new instance of an observation of the Wunderground weather
	 * service.
	 * 
	 * @param tempm Mean temperature.
	 * @param conds String description of conditions.
	 * @param fog Flag indicating if there is fog.
	 * @param rain Flag indicating if there is rain.
	 * @param snow Flag indicating if there is snow.
	 * @param hail Flag indicating if there is hail.
	 * @param thunder Flag indicating if there is a thunderstorm.
	 * @param tornado Flag indicating if there is a tornado.
	 */
	@JsonCreator
	public Observation(@JsonProperty("tempm") String tempm,
			@JsonProperty("conds") String conds,
			@JsonProperty("fog") String fog,
			@JsonProperty("rain") String rain,
			@JsonProperty("snow") String snow,
			@JsonProperty("hail") String hail,
			@JsonProperty("thunder") String thunder,
			@JsonProperty("tornado") String tornado) {
		this.tempm = Double.parseDouble(tempm);
		this.conds = conds;
		this.fog = fog.equals("1");
		this.rain = rain.equals("1");
		this.snow = snow.equals("1");
		this.hail = hail.equals("1");
		this.thunder = thunder.equals("1");
		this.tornado = tornado.equals("1");
	}

	/**
	 * Returns date of the observation.
	 * 
	 * @return Date of observation.
	 */
	public Date getDate() {
		return date;
	}
	
	/**
	 * Returns UTC date of the observation.
	 * 
	 * @return UTC date of observation-
	 */
	public Date getUTCDate() {
		return utcdate;
	}
	
	/**
	 * Returns the mean temperature of the observation.
	 * 
	 * @return Mean temperature of the observation.
	 */
	public double getMeanTemperature() {
		return tempm;
	}
	
	/**
	 * Returns a description of the weather conditions of the observation.
	 * 
	 * @return Description of the weather conditions of the observation.
	 */
	public String getConditions() {
		return conds;
	}
	
	/**
	 * Returns if the weather is foggy in this observation.
	 * 
	 * @return True, if weather is foggy in this observation, false otherwise.
	 */
	public boolean getFog() {
		return fog;
	}
	
	/**
	 * Returns if the weather is rainy in this observation.
	 * 
	 * @return True, if weather is rainy in this observation, false otherwise.
	 */
	public boolean getRain() {
		return rain;
	}
	
	/**
	 * Returns if the weather is snowy in this observation.
	 * 
	 * @return True, if weather is snowy in this observation, false otherwise.
	 */
	public boolean getSnow() {
		return snow;
	}
	
	/**
	 * Returns if there is hail in this observation.
	 * 
	 * @return True, if there is hail in this observation, false otherwise.
	 */
	public boolean getHail() {
		return hail;
	}
	
	/**
	 * Returns if there is a thunderstorm in this observation.
	 * 
	 * @return True, if there is a thunderstorm in this observation, false otherwise.
	 */
	public boolean getThunder() {
		return thunder;
	}
	
	/**
	 * Returns if there is a tornado in this observation.
	 * 
	 * @return True, if there is a tornado in this observation, false otherwise.
	 */
	public boolean getTornado() {
		return tornado;
	}
}
