package allow.util.weatherservice.wunderground;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import allow.util.weatherservice.City;
import allow.util.weatherservice.IWeatherService;

/**
 * Implements a weather service based on the Wunderground Weather API
 * (http://www.wunderground.com/weather/api/).
 * 
 * Note: Using the Wunderground Weather API requires a valid API key.
 * Some licenses are restricted to a limited number of API calls per
 * minute or day. The client does NOT take care of this but will send
 * requests as they come. Responses may thus be error messages. 
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class WundergroundWeatherService implements IWeatherService {
	
	// Date format for requests.
	private static SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		
	// API specifics.
	private String url;
	
	// Client to send requests.
	private HttpClient client;
	
	/**
	 * Constructor.
	 * Creates a new instance of a Wunderground weather service specifying the
	 * API key to use.
	 * 
	 * @param key Key to use with the API.
	 */
	public WundergroundWeatherService(String key) {
		// Create new Http client.
		client = new DefaultHttpClient();
		
		// Prepare request url.
		url = "http://api.wunderground.com/api/" + key + "/history_";
	}
	
	@Override
	public String getWeather(Date date, City city) throws IOException {
		// Build request url adding countrycode and city name.
		StringBuilder uriBuilder = new StringBuilder();
		uriBuilder.append(url);
		uriBuilder.append(format.format(date));
		uriBuilder.append("/q/");
		uriBuilder.append(city.getCountryCode() + "/");
		uriBuilder.append(city.getName() + ".json");
		
		// Create get request and execute it.
		HttpGet request = new HttpGet(uriBuilder.toString());
		HttpResponse httpResponse = client.execute(request);
		
		// Get and return response.
		String res = EntityUtils.toString(httpResponse.getEntity());
		return res;
	}
}
