package allow.simulator.entity.knowledge;

public class EvoEncoding {

	private static final byte NIGHT = 0;
	private static final byte EARLY_MORNING = 1;
	private static final byte MORNING = 2;
	private static final byte EARLY_AFTERNOON = 3;
	private static final byte AFTERNOON = 4;
	private static final byte EVENING = 5;
	
	private static final byte TIME_OF_DAY[] = new byte[] {
		NIGHT,
		NIGHT,
		NIGHT,
		NIGHT,
		NIGHT,
		EARLY_MORNING,
		EARLY_MORNING,
		EARLY_MORNING,
		EARLY_MORNING,
		MORNING,
		MORNING,
		MORNING,
		EARLY_AFTERNOON,
		EARLY_AFTERNOON,
		EARLY_AFTERNOON,
		AFTERNOON,
		AFTERNOON,
		AFTERNOON,
		AFTERNOON,
		EVENING,
		EVENING,
		EVENING,
		NIGHT,
		NIGHT
	};
	
	public static byte getTimeOfDay(int hourOfDay) {
		return TIME_OF_DAY[hourOfDay];
	}
}
