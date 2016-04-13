package allow.simulator.collective.adaptation.ek;

public class Experience {
    // Type of issue
    private int issueType;

    // Type of solver
    private int solverType;

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

    // Actual utility
    private double actualUtility;

    /**
     * Creates a new instance of a EvoKnowledge query to predict the best solver
     * for a certain type of issue occurring in a certain context.
     *
     * @param issueType
     *            Type of issue
     * @param solverType
     *            Type of solver
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
    public Experience(int issueType, int solverType, int numberOfPassangersWaiting, int numberOfPassengers,
	    /* int weekday, int timeOfDay, int modality, */double actualUtility) {
	this.issueType = issueType;
	this.numberOfPassengersWaiting = numberOfPassangersWaiting;
	this.numberOfPassengers = numberOfPassengers;
	// this.weekday = weekday;
	// this.timeOfDay = timeOfDay;
	// this.modality = modality;
	this.actualUtility = actualUtility;
	this.solverType = solverType;
    }

    /**
     * Returns the type of solver used to solve an issue.
     *
     * @return Type of solver used to solve an issue
     */
    public int getSolverType() {
	return solverType;
    }

    /**
     * Returns the type of issue which was solved.
     *
     * @return Type of issue which was solved
     */
    public int getIssueType() {
	return issueType;
    }

    /**
     * Returns the number of waiting passengers when issue was triggered.
     *
     * @return Number of passengers when issue was triggered.
     */
    public int getNumberOfPassengersWaiting() {
	return numberOfPassengersWaiting;
    }

    /**
     * Returns the number of passengers using the modality when issue was
     * triggered.
     *
     * @return Number of passengers using the modality when issue was triggered
     */
    public int getNumberOfPassenger() {
	return numberOfPassengers;
    }

    /**
     * Returns the day of week when the issue was solved.
     *
     * @return Day of week when issue was solved
     */
    /*
     * public int getWeekday() { return weekday; }
     */

    /**
     * Returns the time of day when the issue was solved.
     *
     * @return Time of day when issue was solved
     */
    /*
     * public int getTimeOfDay() { return timeOfDay; }
     */

    /**
     * Returns the modality the issue occurred/was solved.
     *
     * @return Modality the issue occurred/was solved
     */
    /*
     * public int getModality() { return modality; }
     */

    /**
     * Returns the experienced utility when solving the issue with the given
     * type of solver.
     * 
     * @return Experienced utility when solving the issue with the given type of
     *         solver
     */
    public double getActualUtility() {
	return actualUtility;
    }
}
