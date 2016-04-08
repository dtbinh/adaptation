package allow.simulator.entity;

import java.time.LocalTime;

import allow.simulator.util.Coordinate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class TravelEvent {

	private int hour;
	private int minute;
	@JsonIgnore
	private LocalTime time;
	private Coordinate start;
	private Coordinate dest;
	private boolean arriveBy;
	
	@JsonCreator
	public TravelEvent(@JsonProperty("hour") int hour,
			@JsonProperty("minute") int minute,
			@JsonProperty("start") Coordinate start,
			@JsonProperty("dest") Coordinate dest,
			@JsonProperty("arriveby") boolean arriveBy) {
		time = LocalTime.of(hour, minute);
		this.start = start;
		this.dest = dest;
		this.arriveBy = arriveBy;
	}
	
	public TravelEvent(LocalTime time, Coordinate start,
			Coordinate dest, boolean arriveBy) {
		this.time = time;
		hour = time.getHour();
		minute = time.getMinute();
		this.start = start;
		this.dest = dest;
		this.arriveBy = arriveBy;
	}
	
	@JsonIgnore
	public LocalTime getTime() {
		return time;
	}
	
	@JsonGetter("minute")
	public int getMinute() {
		return minute;
	}
	
	@JsonGetter("hour")
	public int getHour() {
		return hour;
	}
	
	@JsonGetter("start")
	public Coordinate getStartingPoint() {
		return start;
	}
	
	@JsonGetter("dest")
	public Coordinate getDestination() {
		return dest;
	}
	
	public boolean arriveBy() {
		return arriveBy;
	}
	
	public String toString() {
		return "[" + time.toString() + "]";
	}
	
}
