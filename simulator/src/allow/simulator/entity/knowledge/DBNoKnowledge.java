package allow.simulator.entity.knowledge;

import java.util.List;

import allow.simulator.entity.Entity;

public class DBNoKnowledge implements DBKnowledgeModel {

	@Override
	public boolean addEntry(Entity agent, List<TravelExperience> prior, List<TravelExperience> experiences, String tablePrefix) {
		return true;
	}

	@Override
	public List<TravelExperience> getPredictedItinerary(Entity agent, List<TravelExperience> it, String tablePrefix) {
		return it;
	}
	
	@Override
	public void clean(Entity agent, String tablePrefix) {
		
	}

	@Override
	public boolean exchangeKnowledge(Entity agent1, Entity agent3, String tablePrefix) {
		return false;
	}
}
