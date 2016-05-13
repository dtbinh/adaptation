package allow.simulator.mobility.data;

import java.time.LocalTime;
import java.util.List;

import allow.simulator.world.Street;

public abstract class Trip {
	// Id of trip.
	protected final String tripId;
	
	// List of stops of a trip.
	protected final List<Stop> stops;
		
	// List of stop times of a trip.
	protected final List<LocalTime> stopTimes;
	
	// Trace of this trip.
	protected final List<List<Street>> trace;
	
	/**
	 * Creates a new trip instance with given Id, stops, and trace.
	 * 
	 * @param tripId Id of the trip.
	 * @param schedule Schedule of the trip including stop and stop times.
	 * @param trace Trace between stops.
	 */
	protected Trip(String tripId,
			List<Stop> stops,
			List<LocalTime> stopTimes,
			List<List<Street>> trace) {
		this.tripId = tripId;
		this.stops = stops;
		this.stopTimes = stopTimes;
		this.trace = trace;
	}
	
	/**
	 * Returns Id of this trip.
	 * 
	 * @return Id of this trip
	 */
	public String getTripId() {
		return tripId;
	}
	
	/**
	 * Returns the traces (sequence of geographical points between two stops)
	 * of this trip.
	 * 
	 * @return Trace of this trip
	 */
	public List<List<Street>> getTraces() {
		return trace;
	}
	
	/**
	 * Returns the starting time of this trip.
	 * 
	 * @return Starting time of this trip
	 */
	public LocalTime getStartingTime() {
		return stopTimes.get(0);
	}
	
	/**
	 * Returns the stops of this trip.
	 * 
	 * @return Stops if this trip
	 */
	public List<LocalTime> getStopTimes() {
		return stopTimes;
	}
	
	public String toString() {
		return "[Trip " + tripId + "]";
	}
}
