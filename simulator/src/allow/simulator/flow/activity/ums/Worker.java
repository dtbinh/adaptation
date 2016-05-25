package allow.simulator.flow.activity.ums;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import allow.simulator.mobility.data.TType;
import allow.simulator.mobility.planner.IPlannerService;
import allow.simulator.mobility.planner.JourneyRequest;
import allow.simulator.mobility.planner.RequestBuffer;

public class Worker implements Callable<RequestBuffer> {

	private static final int MAX_NUMBER_OF_ATTEMPTS = 2;

	// Requests to send to the planner.
	private List<JourneyRequest> requests;
	
	// Buffer to add planner responses to.
	private RequestBuffer responseBuffer;
	
	// Planner to use.
	private IPlannerService regularPlanner;
	private IPlannerService flexiBusPlanner;
	private IPlannerService bikeRentalPlanner;
	private IPlannerService taxiPlannerService;
	
	// Latch to count down for thread synchronization.
	private CountDownLatch latch;

	public void prepare(List<JourneyRequest> requests, RequestBuffer responseBuffer,
			IPlannerService regularPlanner, IPlannerService flexiBusPlanner,
			IPlannerService taxiPlanner, IPlannerService bikeRentalPlanner,
			CountDownLatch latch) {
		this.requests = requests;
		this.responseBuffer = responseBuffer;
		this.regularPlanner = regularPlanner;
		this.flexiBusPlanner = flexiBusPlanner;
		this.taxiPlannerService = taxiPlanner;
		this.bikeRentalPlanner = bikeRentalPlanner;
		this.latch = latch;
	}
	
	public void reset() {
		requests = null;
		latch = null;
	}
	
	@Override
	public RequestBuffer call() throws Exception {
		responseBuffer.processed = false;
		responseBuffer.buffer.clear();

		for (JourneyRequest req : requests) {
			
			if (req.TransportTypes[0] == TType.FLEXIBUS) {
				flexiBusPlanner.requestSingleJourney(req, responseBuffer.buffer);
			
			} else if (req.TransportTypes[0] == TType.SHARED_BICYCLE) {
				bikeRentalPlanner.requestSingleJourney(req, responseBuffer.buffer);
				
			} else if ((req.TransportTypes[0] == TType.TAXI) || (req.TransportTypes[0] == TType.SHARED_TAXI)) {
				taxiPlannerService.requestSingleJourney(req, responseBuffer.buffer);
				
			} else {
				int i = 0;

				while (i < MAX_NUMBER_OF_ATTEMPTS) {
					try {
						boolean success = regularPlanner.requestSingleJourney(req, responseBuffer.buffer);

						if (success) {
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
