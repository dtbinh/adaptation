package allow.simulator.flow.activity.ums;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import allow.simulator.mobility.data.TType;
import allow.simulator.mobility.planner.IPlannerService;
import allow.simulator.mobility.planner.Itinerary;
import allow.simulator.mobility.planner.JourneyRequest;
import allow.simulator.mobility.planner.RequestBuffer;

public class Worker implements Callable<RequestBuffer> {

	private static final int MAX_NUMBER_OF_ATTEMPTS = 2;

	// Requests to send to the planner.
	public List<JourneyRequest> requests;
	
	// Buffer to add planner responses to.
	public RequestBuffer responseBuffer;
	
	// Planner to use.
	public IPlannerService regularPlanner;
	public IPlannerService flexiBusPlanner;
	
	// Latch to count down for thread synchronization.
	public CountDownLatch latch;

	public void prepare(List<JourneyRequest> requests, RequestBuffer responseBuffer,
			IPlannerService regularPlanner, IPlannerService flexiBusPlanner, CountDownLatch latch) {
		this.requests = requests;
		this.responseBuffer = responseBuffer;
		this.regularPlanner = regularPlanner;
		this.flexiBusPlanner = flexiBusPlanner;
		this.latch = latch;
	}
	
	public void reset() {
		requests = null;
		responseBuffer = null;
		regularPlanner = null;
		latch = null;
	}
	
	@Override
	public RequestBuffer call() throws Exception {
		responseBuffer.processed = false;
		responseBuffer.buffer.clear();

		for (JourneyRequest req : requests) {
			
			if (req.TransportTypes[0] == TType.FLEXIBUS) {
				flexiBusPlanner.requestSingleJourney(req, responseBuffer.buffer);
				
			} else {
				int i = 0;

				while (i < MAX_NUMBER_OF_ATTEMPTS) {
					try {
						List<Itinerary> itineraries = regularPlanner.requestSingleJourney(req, responseBuffer.buffer);

						if (itineraries != null) {
							break;
						}
						i++;
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
		responseBuffer.processed = true;
		latch.countDown();
		return responseBuffer;
	}

}
