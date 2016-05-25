package allow.simulator.flow.activity.ums;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import allow.simulator.core.Context;
import allow.simulator.entity.Entity;
import allow.simulator.entity.UrbanMobilitySystem;
import allow.simulator.flow.activity.Activity;
import allow.simulator.mobility.planner.JourneyRequest;
import allow.simulator.mobility.planner.RequestBuffer;
import allow.simulator.util.Pair;

/**
 * Activity realizing parallel requests to 
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class QueryJourneyPlanner extends Activity {
		
	private List<Worker> tasks;
	private WorkerPool workerPool;
	private ExecutorService service;

	public QueryJourneyPlanner(Entity entity, boolean allowParallelClientRequests, ExecutorService service) {
		super(Type.QUERY_JOURNEY_PLANNER, entity);
		workerPool = new WorkerPool(128);
		tasks = new ArrayList<Worker>();
		this.service = service;
	}

	@Override
	public double execute(double deltaT) {
		// Get planner entity.
		UrbanMobilitySystem planner = (UrbanMobilitySystem) entity;
		Queue<Pair<List<JourneyRequest>, RequestBuffer>> requests = planner.getRequestQueue();
		CountDownLatch latch = new CountDownLatch(requests.size());
		Context context = planner.getContext();

		int i = 0;
		while(requests.size() > 0) {
			Pair<List<JourneyRequest>, RequestBuffer> request = requests.poll();
			Worker w = workerPool.pop();
			w.prepare(request.first, request.second, context.getPlannerServices().get(i), context.getFlexiBusPlannerService(),
					context.getBikeRentalPlannerService(), context.getTaxiPlannerService(), latch);
			tasks.add(w);
			i = (i + 1) % planner.getContext().getPlannerServices().size();
		}
		
		try {
			service.invokeAll(tasks);
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		for (int j = 0; j < tasks.size(); j++) {
			Worker w = tasks.get(j);
			w.reset();
			workerPool.put(w);
		}
		tasks.clear();
		return deltaT;
	}
}
