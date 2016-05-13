package allow.simulator.mobility.data;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import allow.simulator.world.Street;

public class TaxiTrip extends Trip {

	public TaxiTrip(String tripId, List<TaxiStop> stops, List<LocalTime> stopTimes, List<List<Street>> traces) {
		super(tripId, new ArrayList<Stop>(stops), stopTimes, traces);
		
	}
	
	public List<TaxiStop> getTaxiStops() {
		List<TaxiStop> ret = new ArrayList<TaxiStop>(stops.size());
		
		for (Stop s : stops) {
			ret.add((TaxiStop) s);
		}
		return ret;
	}
	
	public List<List<Street>> getTraces() {
		return trace;
	}
}
