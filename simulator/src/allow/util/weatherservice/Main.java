package allow.util.weatherservice;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import allow.util.weatherservice.wunderground.WundergroundWeatherService;

public class Main {
	
	public static void main(String args[]) throws InterruptedException, IOException {
	
		// Check command line arguments for configuration file.
		if (args.length != 2) {
			System.out.println("Error: Invalid number of arguments. Argument -c [Path to configuration file] expected.");
			return;
		
		} else if (!args[0].equals("-c")) {
			System.out.println("Error: Unknown argument. Argument -c [Path to configuration file] expected.");
			return;
		}
		
		// Create file and check existance.
		File configFile = new File(args[1]);
		
		if (!configFile.exists()) {
			System.out.println("Error: Illegal path to configuration file.");
			return;
		}
		
		// Load configuration.
		Configuration config = Configuration.fromJSON(configFile);
		
		// Create weather service.
		IWeatherService s = new WundergroundWeatherService(config.getAPIKey());
		
		// Prepare calendar.
		Calendar c = Calendar.getInstance();
		c.setTime(config.getStartingDate());
		
		// Prepare writer.
		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(config.getCity().getName() + ".weather")));
		wr.write(Integer.toString(c.get(Calendar.YEAR)));
		int i = 1;
		
		while (c.getTime().equals(config.getEndingDate()) || c.getTime().before(config.getEndingDate())) {
			// Get weather from service.
			String json = s.getWeather(c.getTime(), config.getCity());
			
			// Write weather.
			wr.write(json);
			
			if (i % 10 == 0) {
				System.out.println(i + " requests processed. Sleeping for one minute.");
				TimeUnit.MINUTES.sleep(1);
			}
			c.add(Calendar.DAY_OF_MONTH, 1);
			i++;
		}
	
		// Close writer.
		wr.close();
	}
}
