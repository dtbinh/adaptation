package allow.simulator.flow.activity.person;

import java.util.ArrayList;
import java.util.List;

import allow.simulator.entity.Person;
import allow.simulator.flow.activity.Activity;
import allow.simulator.mobility.planner.Itinerary;

public final class FilterAlternatives extends Activity {

	private List<Itinerary> toFilter;
	
	public FilterAlternatives(Person person, List<Itinerary> it) {
		super(Activity.Type.FILTER_ALTERNATIVES, person);
		toFilter = it;
	}

	@Override
	public double execute(double deltaT) {
		Person person = (Person) entity;
		List<Itinerary> filtered = new ArrayList<Itinerary>(toFilter.size());
		//SafetyLayer safetyLayer = (SafetyLayer) person.getContext().getWorld().getStreetMap().getLayer(Layer.Type.SAFETY);
		/* 
		 * Do filtering here 
		 */
		
		/*
		 * Remove this line after adding filtering algorithm.
		 */
		filtered.addAll(toFilter);
		entity.getFlow().addActivity(new RankAlternatives(person, filtered));
		setFinished();
		return 0;
	}

	public String toString() {
		return "FilterAlternatives " + entity;
	}
}
