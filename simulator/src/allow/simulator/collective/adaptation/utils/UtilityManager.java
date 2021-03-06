package allow.simulator.collective.adaptation.utils;

import java.util.ArrayList;
import java.util.List;

import isFeasible.Passenger;
import isFeasible.utility;

public class UtilityManager {

    public UtilityManager() {
	// super();
	// TODO Auto-generated constructor stub
    }

    public boolean checkFeasibility() {
	boolean result = false;
	/* Current passengers of FlexiBus 2 */
	List<Passenger> passengersN2 = new ArrayList<Passenger>();
	/* Current passengers of FlexiBus 3 */
	List<Passenger> passengersN3 = new ArrayList<Passenger>();
	/* Current passengers of FlexiBus 4 */
	List<Passenger> passengersN4 = new ArrayList<Passenger>();

	passengersN2.add(new Passenger(3, 720, 3, 0.4, 0.6, 1800, 5));
	passengersN2.add(new Passenger(4, 840, 3, 0.4, 0.6, 1800, 5));

	passengersN3.add(new Passenger(3, 1200, 4, 0.5, 0.5, 1500, 5));
	passengersN3.add(new Passenger(4, 840, 3, 0.5, 0.5, 1500, 5));
	passengersN3.add(new Passenger(5, 840, 3, 0.5, 0.5, 1500, 5));

	passengersN4.add(new Passenger(6, 600, 3, 0.3, 0.7, 900, 5));
	passengersN4.add(new Passenger(6, 600, 3, 0.3, 0.7, 900, 5));

	/*
	 * check solution for Flexibus 2 with 3 minutes delay (delay<traveltime)
	 */
	double delay = 180;
	System.out.print("The solution for FlexiBus 2 is ");
	System.out.println(utility.isFeasible(passengersN2, delay));
	/*
	 * check solution for Flexibus 3 with 5 minutes delay (delay<traveltime)
	 */
	delay = 300;
	System.out.print("The solution for FlexiBus 3 is ");
	System.out.println(utility.isFeasible(passengersN3, delay));
	/*
	 * check solution for Flexibus 2 with 10 minutes delay (delay ==
	 * traveltime)
	 */
	delay = 600;
	System.out.print("The solution for FlexiBus 4 is ");
	System.out.println(utility.isFeasible(passengersN4, delay));
	return result;

    }

}
