package allow.simulator.collective.adaptation.ek;

public class Query {
    // Type of issue
    private int issueType;

    // Number of passengers waiting for bus bus when issue is triggered
    private int numberOfPassengersWaiting;

    // Number of passengers on the already using the modality when issue
    // is triggered
    private int numberOfPassengers;

    // Weekday
    // private int weekday;

    // Time of day
    // private int timeOfDay;

    // Used modality
    // private int modality;

    /**
     * Creates a new instance of a EvoKnowledge query to predict the best solver
     * for a certain type of issue occurring in a certain context.
     *
     * @param issueType
     *            Type of issue
     * @param numberOfPassangersWaiting
     *            Number of passengers waiting for bus bus when issue is
     *            triggered
     * @param numberOfPassengers
     *            Number of passengers on the already using the modality when
     *            issue is triggered
     * @param weekday
     *            Weekday
     * @param timeOfDay
     *            Time of day
     * @param modality
     *            Used modality
     */
    public Query(int issueType, int numberOfPassangersWaiting,
	    int numberOfPassengers/*
				   * , int weekday, int timeOfDay, int modality
				   */) {
	this.issueType = issueType;
	this.numberOfPassengersWaiting = numberOfPassangersWaiting;
	this.numberOfPassengers = numberOfPassengers;
	// this.weekday = weekday;
	// this.timeOfDay = timeOfDay;
	// this.modality = modality;
    }

    public int getIssueType() {
	return issueType;
    }

    public int getNumberOfPassengersWaiting() {
	return numberOfPassengersWaiting;
    }

    public int getNumberOfPassengers() {
	return numberOfPassengers;
    }

    /*
     * public int getWeekday() { return weekday; }
     */

    /*
     * public int getTimeOfDay() { return timeOfDay; }
     */

    /*
     * public int getModality() { return modality; }
     */
}
