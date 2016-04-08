package allow.simulator.core;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents time of the simulation.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class Time {
	// Output format.	
	private static DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM. HH:mm:ss");
	
	// Current date and time.
	private LocalDateTime currentDateTime;
	private LocalTime currentTime;
	private long timestamp;
	
	// Time interval per tick.
	private int deltaT;

	// Number of simulated days.
	private int days;
	
	public Time(LocalDateTime startingDate, int deltaT) {
		// Setup calendar.
		currentDateTime = startingDate;
		currentTime = currentDateTime.toLocalTime();
		timestamp = Timestamp.valueOf(currentDateTime).getTime();

		// Setup time step interval.
		this.deltaT = deltaT;
		days = 0;
	}

	/**
	 * Get current time of simulator.
	 * 
	 * @return Current time of the simulator.
	 */
	public LocalTime getCurrentTime() {
		return currentTime;
	}

	/**
	 * Get current time of simulator.
	 * 
	 * @return Current time of the simulator.
	 */
	public LocalDateTime getCurrentDateTime() {
		return currentDateTime;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	/**
	 * Increases current time by deltaT seconds.
	 */
	public void tick(int deltaT) {
		this.deltaT = deltaT;
		DayOfWeek tempDay = currentDateTime.getDayOfWeek();
		int tempYear = currentDateTime.getYear();
		currentDateTime = currentDateTime.plusSeconds(deltaT);
		
		// Register changes of day.
		if (tempDay != currentDateTime.getDayOfWeek()) {
			days++;
		}
		
		// Disable changes of year to assure consistency with transit and
		// weather data.
		if (tempYear != currentDateTime.getYear()) {
			currentDateTime = currentDateTime.withYear(tempYear);
		}
		currentTime = currentDateTime.toLocalTime();
		timestamp = Timestamp.valueOf(currentDateTime).getTime();
	}

	/**
	 * Returns time in seconds current time is increased per tick.
	 * 
	 * @return Time interval time is increased per tick.
	 */
	public int getDeltaT() {
		return deltaT;
	}
	
	/**
	 * Returns the number of already simulated days.
	 * 
	 * @return Number of simulated days.
	 */
	public int getDays() {
		return days;
	}
	
	/**
	 * Returns a string representation of the current time.
	 * 
	 * @return String representation of current time.
	 */
	public String toString() {
		return format.format(currentDateTime) + " (Day " + days + ")";
	}
	
}
