package allow.simulator.mobility.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import allow.simulator.mobility.data.gtfs.GTFSService;
import allow.simulator.mobility.data.gtfs.GTFSStopTimes;

public class TimeTable {

	private static class StopTimesComparator implements Comparator<GTFSStopTimes> {

		@Override
		public int compare(GTFSStopTimes trip1, GTFSStopTimes trip2) {
			return trip1.getStartingTime().compareTo(trip2.getStartingTime());
		}

	}
	
	private static Comparator<GTFSStopTimes> comp = new StopTimesComparator();

	private String routeId;
	private int maxNumberOfTrips;
	
	private List<List<GTFSStopTimes>> schedule;
	
	public enum Day {
		
		 MONDAY,
		 
		 TUESDAY,
		 
		 WEDNESDAY,
		 
		 THURSDAY,
		 
		 FRIDAY,
		 
		 SATURDAY,
		 
		 SUNDAY
		 
	}
	
	public TimeTable(String routeId, List<GTFSStopTimes> trips, List<GTFSService> services) {
		// Route id.
		this.routeId = routeId;
		
		// Create schedule data structure.
		schedule = new ArrayList<List<GTFSStopTimes>>(7);
		
		// Add new list for every day of week.
		for (int i = 0; i < 7; i++) {
			schedule.add(new ArrayList<GTFSStopTimes>());
		}
		
		for (int i = 0; i < trips.size(); i++) {
			boolean serviceDays[] = services.get(i).getDays();
			GTFSStopTimes current = trips.get(i);
			
			for (int j = 0; j < 7; j++) {
				if (serviceDays[j]) schedule.get(j).add(current);
			}
		}
		int maxNumber = 0;
		
		for (int i = 0; i < 7; i++) {
			Collections.sort(schedule.get(i), comp);
			
			if (schedule.get(0).size() > maxNumber) {
				maxNumber = schedule.get(0).size();
			}
		}
		maxNumberOfTrips = maxNumber;

	}
	
	public int getMaximalNumberOfTrips() {
		return maxNumberOfTrips;
	}
	
	public List<GTFSStopTimes> getTripsOfDay(Day weekday) {
		return schedule.get(weekday.ordinal());
	}
	
	public String toString() {
		StringBuilder bldr = new StringBuilder();
		bldr.append(routeId + "\n");
		
		for (int i = 0; i < 7; i++) {
			bldr.append(schedule.get(i) + "\n");
		}
		return bldr.toString();
	}
}
