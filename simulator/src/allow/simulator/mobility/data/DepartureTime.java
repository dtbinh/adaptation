package allow.simulator.mobility.data;

public class DepartureTime {
	public int hour;
	public int minute;
	
	public DepartureTime(String time) {
		String tokens[] = time.split(":");
		hour = Integer.parseInt(tokens[0]);
		minute = Integer.parseInt(tokens[1]);
	}
}
