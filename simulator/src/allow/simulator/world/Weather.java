package allow.simulator.world;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.time.Year;

import allow.simulator.core.Time;
import allow.util.weatherservice.wunderground.Observation;
import allow.util.weatherservice.wunderground.Response;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Simulates weather as a context factor in the Allow Ensembles simulator.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class Weather {

	/**
	 * Represents the state of the weather.
	 * 
	 * @author Andreas Poxrucker (DFKI)
	 *
	 */
	public static class State {
	
		// Speed reduction factor.
		private double speedFactor;
		
		// Textual description.
		private String description;
		
		// Weather flags.
		private boolean flags[];
		
		// Encoding of the flags;
		private byte encoding;
		
		/**
		 * Constructor.
		 * Creates a new weather state of the enum.
		 * 
		 * @param speedFactor Speed reduction factor.
		 * @param description Textual description.
		 */
		protected State(String description, boolean fog, boolean rain, boolean snow, boolean thunderstorm) {
			this.description = description;
			flags = new boolean[] { fog, rain, snow, thunderstorm };
			encoding = 0;
			
			if (fog) encoding |= 1;
			if (rain) encoding |= 2;
			if (snow) encoding |= 4;
			if (thunderstorm) encoding |= 8;
			
			speedFactor = 1.0;
			
			/*if (rain) speedFactor = 0.85;
			
			if (thunderstorm) speedFactor = 0.70;
			
			if (fog) speedFactor = 0.70;

			if (snow) speedFactor = 0.40;*/
		}
		
		/**
		 * Returns the speed reduction factor  of the state. Factor is
		 * between 1.0 and 0.0 with 1.0 meaning no reduction.
		 * 
		 * @return Speed reduction factor.
		 */
		public double getSpeedReductionFactor() {
			return speedFactor;
		}
		
		/**
		 * Returns the textual description of the state.
		 * 
		 * @return Textual description of the state.
		 */
		public String getDescription() {
			return description;
		}
		
		/**
		 * Returns if the weather is foggy in this observation.
		 * 
		 * @return True, if weather is foggy in this observation, false otherwise.
		 */
		public boolean getFog() {
			return flags[0];
		}
		
		/**
		 * Returns if the weather is rainy in this observation.
		 * 
		 * @return True, if weather is rainy in this observation, false otherwise.
		 */
		public boolean getRain() {
			return flags[1];
		}
		
		/**
		 * Returns if the weather is snowy in this observation.
		 * 
		 * @return True, if weather is snowy in this observation, false otherwise.
		 */
		public boolean getSnow() {
			return flags[2];
		}
		
		/**
		 * Returns if there is a thunderstorm in this observation.
		 * 
		 * @return True, if there is a thunderstorm in this observation, false otherwise.
		 */
		public boolean getThunderstorm() {
			return flags[3];
		}
		
		/**
		 * Returns the weather state in an encoded form:
		 * For fog the first bit is set.
		 * For rain the second bit is set.
		 * For snow the third bit is set.
		 * For thunderstorm the fourth bit is set.
		 * Combinations are possible, too.
		 * 
		 * @return Encoded weather state.
		 */
		public byte getEncoding() {
			return encoding;
		}
		
		public String toString() {
			return description;
		}
	}
	
	// Current time.
	private Time time;
	
	// Weather model.
	private State model[][];
	
	/**
	 * Constructor.
	 * Creates a new instance of a weather model.
	 * 
	 * @param path Path to file containing weather model.
	 * @param time Time of the simulation to simulate changes.
	 * @throws IOException 
	 */
	public Weather(Path path, Time time) throws IOException {
		this.time = time;
		loadModel(path);
	}
	
	/**
	 * Returns the current state of the weather.
	 * 
	 * @return Current state of the weather.
	 */
	public State getCurrentState() {
		return model[time.getCurrentDateTime().getDayOfYear() - 1][time.getCurrentTime().getHour()];
	}

	/**
	 * Loads a weather model from a file filling the internal weather state array.
	 * 
	 * @param path Path to weather model file.
	 * @throws IOException
	 */
	private void loadModel(Path path) throws IOException {
		// Open weather model file and create Json object mapper.
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path.toFile())));
		ObjectMapper mapper = new ObjectMapper();
		
		// Allocate model ([number of days of year][24 observations per day])
		model = new State[Year.of(time.getCurrentDateTime().getYear()).isLeap() ? 366 : 365][24];
		
		// Json string read line by line from file.
		StringBuilder json = new StringBuilder();

		for (int i = 0; i < model.length; i++) {
			json.delete(0, json.length());
			String line = reader.readLine();
			
			while ((line != null) && !line.equals("")) {
				json.append(line);
				line = reader.readLine();
			}
			
			// Parse response from read Json string.
			Response res = mapper.readValue(json.toString(), Response.class);
			int index = 0;
			
			for (Observation ob : res.getHistory().getObservations()) {
				// If observation conditions equal empty string (i.e. nothing
				// to display), continue with next observation.
				if (ob.getConditions().equals("")) {
					continue;
				}
				
				// Get hour of observation.
				int hour = ob.getDate().getHour();
				
				if (hour == index) {
					// If hour equals currently expected index create state and increase index.
					model[i][index] = new State(ob.getConditions(), ob.getFog(), ob.getRain(), ob.getSnow(), ob.getThunder());
					index++;
					
				} else if (hour > index) {
					// If hour is greater than currently expected index (i.e. for a certain index there
					// is no observation), copy current observation to all indices up to hour.
					int diff = hour - index;
					State newState = new State(ob.getConditions(), ob.getFog(), ob.getRain(), ob.getSnow(), ob.getThunder());

					for (int j = 0; j < diff; j++) {
						model[i][index] = newState;
						index++;
					}
				} 
			}
			// If index is less than the expected number of observation (i.e.
			// there are too few observations), fill remaining states with
			// last known state.
			for (int j = index; j < model[i].length; j++) {
				model[i][j] = model[i][j - 1];
			}
		}
		// Close reader.
		reader.close();
	}
}
