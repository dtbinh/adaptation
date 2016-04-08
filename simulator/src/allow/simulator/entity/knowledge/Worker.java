package allow.simulator.entity.knowledge;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import allow.simulator.entity.Entity;
import allow.simulator.mobility.planner.Itinerary;

public class Worker implements Callable<List<Itinerary>> {
	// Requests to send to the planner.
	public List<Itinerary> toUpdate;
	public Entity entity;
	
	// Latch to count down for thread synchronization.
	public CountDownLatch latch;

	public void prepare(Entity entity, List<Itinerary> toUpdate, CountDownLatch latch) {
		this.entity = entity;
		this.toUpdate = toUpdate;
		this.latch = latch;
	}
	
	public void reset() {
		toUpdate = null;
		latch = null;
		entity = null;
	}
	
	@Override
	public List<Itinerary> call() throws Exception {
		
		for (Itinerary it : toUpdate) {
			List<TravelExperience> ex = EvoKnowledge.itineraryToTravelExperience(entity, it);
			
			try {
				DBConnector.getPredictedItinerary(entity, ex);
				
				it.priorSegmentation = new ArrayList<TravelExperience>(ex.size());
				
				for (TravelExperience e : ex) {
					it.priorSegmentation.add(e.clone());
				}
				EvoKnowledge.updateItineraryFromTravelExperience(it, ex);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		latch.countDown();
		return toUpdate;
	}

}
