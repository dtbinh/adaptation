package allow.simulator.entity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class DailyRoutine {
	
	private List<List<TravelEvent>> weeklyRoutine;
	
	private static Comparator<TravelEvent> comp = new Comparator<TravelEvent>() {

		@Override
		public int compare(TravelEvent o1, TravelEvent o2) {
			return o1.getTime().compareTo(o2.getTime());
		}
	};
	
	public String toString() {
		return weeklyRoutine.toString();
	}
	
	@JsonCreator
	public DailyRoutine(@JsonProperty("weeklyRoutine") List<List<TravelEvent>> weeklyRoutine) {
		this.weeklyRoutine = weeklyRoutine;
	}
	
	public DailyRoutine() {
		weeklyRoutine = new ArrayList<List<TravelEvent>>(7);
		
		for (int i = 0; i < 7; i++) {
			weeklyRoutine.add(new ArrayList<TravelEvent>());
		}
	}
	
	public void addToDailyRoutine(TravelEvent toDo, Calendar c) {
		List<TravelEvent> dailyRoutine = weeklyRoutine.get(c.get(Calendar.DAY_OF_WEEK));
		dailyRoutine.add(toDo);
		Collections.sort(dailyRoutine, comp);
	}
	
	public void addToDailyRoutine(TravelEvent toDo, int dayOfWeek) {
		List<TravelEvent> dailyRoutine = weeklyRoutine.get(dayOfWeek - 1);
		dailyRoutine.add(toDo);
		Collections.sort(dailyRoutine, comp);
	}
	
	public List<TravelEvent> getDailyRoutine(Calendar c) {
		return weeklyRoutine.get(c.get(Calendar.DAY_OF_WEEK));
	}
	
	public List<TravelEvent> getDailyRoutine(int dayOfWeek) {
		return weeklyRoutine.get(dayOfWeek - 1);
	}
}
