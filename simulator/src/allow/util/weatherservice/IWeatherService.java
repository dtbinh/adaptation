package allow.util.weatherservice;

import java.io.IOException;
import java.util.Date;

/**
 * Interface for a service providing (historical) weather information.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public interface IWeatherService {

	/**
	 * Returns a String representation of the weather of a given city at a 
	 * given date.
	 * 
	 * @param date Date to return the weather at.
	 * @param city City to return the weather of of.
	 * @return String description of the weather.
	 */
	public String getWeather(Date date, City city) throws IOException;
	
}
