package allow.simulator.mobility.data.gtfs;

import java.util.List;

public class GTFSStopTimes {

	private String tripId;
	private String arrivalTimes[] = new String[0];
	private String departureTimes[] = new String[0];
	private String stopIds[] = new String[0];
	
	public String getTripId() {
		return tripId;
	}
	
	public String[] getArrivalTimes() {
		return arrivalTimes;
	}
	
	public String[] getDepartureTimes() {
		return departureTimes;
	}
	
	public String[] getStopIds() {
		return stopIds;
	}
	
	public String getStartingTime() {
		return arrivalTimes[0];
	}
	
	public String getEndingTime() {
		return arrivalTimes[arrivalTimes.length - 1];
	}
	
	private static int STOPTIMES_TRIP_ID = 0;
	private static int STOPTIMES_ARRIVAL_TIME = 1;
	private static int STOPTIMES_DEPARTURE_TIME = 2;
	private static int STOPTIMES_STOP_ID = 3;
	
	public static GTFSStopTimes fromGTFS(List<String> sequence) {
		GTFSStopTimes i = new GTFSStopTimes();
		i.arrivalTimes = new String[sequence.size()];
		i.departureTimes = new String[sequence.size()];
		i.stopIds = new String[sequence.size()];

		for (int j = 0; j < sequence.size(); j++) {
			String tokens[] = sequence.get(j).split(",");
			i.tripId = tokens[STOPTIMES_TRIP_ID];
			i.arrivalTimes[j] = tokens[STOPTIMES_ARRIVAL_TIME];
			i.departureTimes[j] = tokens[STOPTIMES_DEPARTURE_TIME];
			i.stopIds[j] = tokens[STOPTIMES_STOP_ID];	
		}
		return i;
	}
	
	public String toString() {
		StringBuilder bldr = new StringBuilder();
		bldr.append(tripId + " ");
		bldr.append("[");
		
		for (int i = 0; i < arrivalTimes.length - 1; i++) {
			bldr.append(arrivalTimes[i] + ", ");
		}
		bldr.append(arrivalTimes[arrivalTimes.length - 1]);
		bldr.append("] ");
		bldr.append("[");
		
		for (int i = 0; i < stopIds.length - 1; i++) {
			bldr.append(stopIds[i] + ", ");
		}
		bldr.append(stopIds[stopIds.length - 1]);
		bldr.append("] ");
		return bldr.toString();
	}
}
