package allow.simulator.mobility.data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import allow.simulator.mobility.data.gtfs.GTFSServiceException;
import allow.simulator.world.Street;

public class Trip {
	// Id of trip.
	private String tripId;
	private LocalDate startingDate;
	private LocalDate endingDate;
	private List<GTFSServiceException> exceptions;
	
	// List of stops of a trip.
	private List<Stop> stops;
		
	// List of stop times of a trip.
	private List<LocalTime> stopTimes;
	
	// Trace of this trip.
	private List<List<Street>> trace;
	
	/**
	 * Constructor.
	 * Creates a new trip with given Id, stops, and trace.
	 * 
	 * @param tripId Id of the trip.
	 * @param schedule Schedule of the trip including stop and stop times.
	 * @param trace Trace between stops.
	 */
	public Trip(String tripId, LocalDate starting, LocalDate ending,
			List<GTFSServiceException> exceptions, 
			List<Stop> stops,
			List<LocalTime> stopTimes,
			List<List<Street>> trace) {
		this.tripId = tripId;
		this.startingDate = starting;
		this.endingDate = ending;
		this.exceptions = exceptions;
		this.stops = stops;
		this.stopTimes = stopTimes;
		this.trace = trace;
	}
	
	/**
	 * Returns Id of this trip.
	 * 
	 * @return Id of this trip.
	 */
	public String getTripId() {
		return tripId;
	}
	
	/**
	 * Returns the traces (sequence of geographical points between two stops)
	 * of this trip.
	 * 
	 * @return Trace of this trip.
	 */
	public List<List<Street>> getTraces() {
		return trace;
	}
	
	/**
	 * Returns the starting time of this trip.
	 * 
	 * @return Starting time of this trip.
	 */
	public LocalTime getStartingTime() {
		return stopTimes.get(0);
	}
	
	public List<Stop> getStops() {
		return stops;
	}
	
	public List<LocalTime> getStopTimes() {
		return stopTimes;
	}
	
	public boolean isValidThisDay(LocalDate day) {
		boolean isValid = day.compareTo(startingDate) >= 0 && day.compareTo(endingDate) <= 0;
		
		for (int i = 0; i < exceptions.size(); i++) {
			isValid = isValid && day.compareTo(exceptions.get(i).getDate()) != 0;
		}
		return isValid;
	}
	
	public String toString() {
		return tripId + " " + startingDate + " " + endingDate;
	}
}
