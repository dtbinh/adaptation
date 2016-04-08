package allow.simulator.mobility.planner;

import java.util.ArrayList;
import java.util.List;

public class RequestBuffer {
	public boolean processed = false;
	public List<Itinerary> buffer = new ArrayList<Itinerary>();
	
	public void reset() {
		processed = false;
		buffer.clear();
	}
}
