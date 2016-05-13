package allow.simulator.entity.knowledge;

import java.util.List;

import allow.simulator.entity.Entity;
import allow.simulator.mobility.data.PublicTransportationStop;
import allow.simulator.util.Coordinate;
import allow.simulator.world.Weather;

public final class StopExperience extends Experience {

	private PublicTransportationStop stop;
	private List<Entity> passengers;
	private long timeArrival;
	private long timeDeparture;
	private Weather.State weather;
	
	public StopExperience(PublicTransportationStop stop,
			List<Entity> passengers,
			long timeArrival,
			long timeDeparture,
			Weather.State weather) {
		super(Experience.Type.STOP);
		this.stop = stop;
		this.passengers = passengers;
		this.timeArrival = timeArrival;
		this.timeDeparture = timeDeparture;
		this.weather = weather;
	}
	
	public String getStopId() {
		return stop.getStopId();
	}
	
	public Coordinate getStopPosition() {
		return stop.getPosition();
	}
	
	public List<Entity> getPassengers() {
		return passengers;
	}
	
	public long getTimeArrival() {
		return timeArrival;
	}
	
	public long getTimeDeparture() {
		return timeDeparture;
	}
	
	public Weather.State getWeather() {
		return weather;
	}

}
