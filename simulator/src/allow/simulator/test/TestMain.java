package allow.simulator.test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.ConfigurationException;
import javax.swing.UIManager;

import allow.simulator.ensemble.Ensemble;
import allow.simulator.entity.Bus;
import allow.simulator.entity.Entity;
import allow.simulator.entity.Person;
import allow.simulator.entity.Person.Gender;
import allow.simulator.entity.Person.Profile;
import allow.simulator.util.Coordinate;

public class TestMain {
    private final static String PROP_PATH = "adaptation.properties";

    public static void main(String[] args) throws ConfigurationException, FileNotFoundException {

	try {
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (Exception e) {

	}

	System.gc();
	String propPath = PROP_PATH;
	if (args.length > 0) {
	    propPath = args[0];
	}

	// Ensemble instantiation
	List<Entity> roles = new ArrayList<Entity>();

	Coordinate c1 = new Coordinate(11.5, 34.4);
	Coordinate c2 = new Coordinate(11.5, 34.4);
	Bus busDriver = new Bus(1, null, null, null, 20);
	Person passenger1 = new Person(2, Gender.MALE, Profile.WORKER, null, null, c1, true, true, true, null, null);
	Person passenger2 = new Person(3, Gender.MALE, Profile.WORKER, null, null, c2, true, true, true, null, null);

	roles.add(busDriver);
	roles.add(passenger1);
	roles.add(passenger2);

	Ensemble en = new Ensemble("routeA", roles);
	en.solveIssue("IntenseTraffic");

	// System.exit(1);

    }

}
