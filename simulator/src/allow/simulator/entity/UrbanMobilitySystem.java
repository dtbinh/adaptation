package allow.simulator.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import allow.simulator.core.Context;
import allow.simulator.entity.utility.Preferences;
import allow.simulator.entity.utility.Utility;
import allow.simulator.mobility.data.TransportationRepository;
import allow.simulator.mobility.planner.FlexiBusPlanner;
import allow.simulator.mobility.planner.JourneyRequest;
import allow.simulator.mobility.planner.RequestBuffer;
import allow.simulator.util.Coordinate;
import allow.simulator.util.Pair;

/**
 * Represents a smart planner component persons send requests to to obtain
 * solutions for travelling.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public final class UrbanMobilitySystem extends Entity {
	// Request queue.
	private Queue<Pair<List<JourneyRequest>, RequestBuffer>> requests;
	private TransportationRepository transport;
	
	public UrbanMobilitySystem(long id, Utility utility, Preferences prefs, Context context) {
		super(id, Type.URBANMOBILITYSYSTEM, utility, prefs, context);
		requests = new ConcurrentLinkedDeque<Pair<List<JourneyRequest>, RequestBuffer>>();
	}

	public void setTransportationRepository(TransportationRepository repos) {
		this.transport = repos;
	}
	
	public TransportationRepository getTransportationRepository() {
		return transport;
	}
	
	public void register(Person p, Coordinate start, Coordinate dest, LocalDateTime startingTime) {
		FlexiBusPlanner planner = (FlexiBusPlanner) getContext().getFlexiBusPlannerService();
		planner.register(p, start, dest, startingTime);
		// System.out.println(p + " registered to FB service.");
	}
	
	public void unregister(Person p) {
		FlexiBusPlanner planner = (FlexiBusPlanner) getContext().getFlexiBusPlannerService();
		planner.unregister(p);
		// System.out.println(p + " unregistered from FB service.");
	}
	
	public void addRequest(JourneyRequest req, RequestBuffer buffer) {
		List<JourneyRequest> list = new ArrayList<JourneyRequest>(1);
		list.add(req);
		requests.add(new Pair<List<JourneyRequest>, RequestBuffer>(list, buffer));
	}
	
	public void addRequests(List<JourneyRequest> reqs, RequestBuffer buffer) {
		requests.add(new Pair<List<JourneyRequest>, RequestBuffer>(reqs, buffer));
	}
	
	public Queue<Pair<List<JourneyRequest>, RequestBuffer>> getRequestQueue() {
		return requests;
	}
	
	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public void exchangeKnowledge() {
		knowledge.exchangeKnowledge();
	}

}
