package allow.simulator.collective.adaptation;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.ConfigurationException;
import javax.swing.UIManager;

import allow.simulator.collective.adaptation.api.CollectiveAdaptationEnsemble;
import allow.simulator.collective.adaptation.api.CollectiveAdaptationProblem;
import allow.simulator.collective.adaptation.api.CollectiveAdaptationRole;

public class Collective2Main {
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

	// try {
	// System.out.println("Experiment starting in 5 seconds...");
	// Thread.sleep(5000);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }

	// ROUTE A

	List<CollectiveAdaptationRole> rolesRouteA = new ArrayList<CollectiveAdaptationRole>();

	CollectiveAdaptationRole p1 = new CollectiveAdaptationRole();
	p1.setRole("RoutePassenger_33");
	p1.setId(33);
	p1.setTravel_time(35);// inventato
	p1.setCost(10); // inventato
	p1.setWeightTravelTime(0.2);
	p1.setWeightCost(0.3);
	p1.setMaxTravelTime(40);
	p1.setMaxCost(10.0);

	CollectiveAdaptationRole p2 = new CollectiveAdaptationRole();
	p2.setRole("RoutePassenger_30");
	p2.setId(32);
	p2.setTravel_time(35); // inventato
	p2.setCost(10); // inventato
	p2.setWeightTravelTime(0.7);
	p2.setWeightCost(0.9);
	p2.setMaxTravelTime(40);
	p2.setMaxCost(10.0);

	CollectiveAdaptationRole p3 = new CollectiveAdaptationRole();
	p3.setRole("RoutePassenger_36");
	p3.setId(36);
	p3.setTravel_time(35); // inventato
	p3.setCost(10); // inventato
	p3.setWeightTravelTime(0.7);
	p3.setWeightCost(0.9);
	p3.setMaxTravelTime(40);
	p3.setMaxCost(100);

	rolesRouteA.add(p1);
	rolesRouteA.add(p2);
	rolesRouteA.add(p3);

	// ROUTE B
	List<CollectiveAdaptationRole> rolesRouteB = new ArrayList<CollectiveAdaptationRole>();
	CollectiveAdaptationRole p4 = new CollectiveAdaptationRole();
	p4.setRole("RoutePassenger_64");
	p4.setId(64);
	p4.setTravel_time(20);// inventato
	p4.setCost(10); // inventato
	p4.setWeightTravelTime(0.2);
	p4.setWeightCost(0.2);
	p4.setMaxTravelTime(40);
	p4.setMaxCost(3);

	CollectiveAdaptationRole p4Bis = new CollectiveAdaptationRole();
	p4Bis.setRole("RoutePassenger_69");
	p4Bis.setId(69);
	p4Bis.setTravel_time(20); // inventato
	p4Bis.setCost(10); // inventato
	p4Bis.setWeightTravelTime(0.2);
	p4Bis.setWeightCost(0.1);
	p4Bis.setMaxTravelTime(30);
	p4Bis.setMaxCost(10.0);

	CollectiveAdaptationRole p6 = new CollectiveAdaptationRole();
	p6.setRole("RoutePassenger_73");
	p6.setId(73);
	p6.setTravel_time(25); // inventato
	p6.setCost(0); // inventato
	p6.setWeightTravelTime(0.7);
	p6.setWeightCost(0.9);
	p6.setMaxTravelTime(40);
	p6.setMaxCost(10);

	rolesRouteB.add(p4);
	rolesRouteB.add(p4Bis);
	rolesRouteB.add(p6);

	DemonstratorAnalyzer demo = new DemonstratorAnalyzer();

	List<CollectiveAdaptationEnsemble> ensembles = new ArrayList<CollectiveAdaptationEnsemble>();
	ensembles.add(new CollectiveAdaptationEnsemble("RouteA", rolesRouteA));
	ensembles.add(new CollectiveAdaptationEnsemble("RouteB", rolesRouteB));
	ensembles.add(new CollectiveAdaptationEnsemble("FlexiBusMngmt", null));

	CollectiveAdaptationProblem cap = new CollectiveAdaptationProblem("CAP_1", ensembles, "RouteBlocked",
		"FlexibusDriver_28", ensembles.get(1).getEnsembleName(), "RoutePassenger_73");

	// CollectiveAdaptationSolution cas = demo.executeCap(cap);
	demo.executeCap(cap, new DummyExecution());

	// System.out.println("SOLUTION ID: " + cas.getCapID());
	//
	// for (HashMap.Entry<String, List<RoleCommand>> entry :
	// cas.getEnsembleCommands().entrySet()) {
	// System.out.println(entry.getKey() + " : " + entry.getValue());
	// List<RoleCommand> commands = entry.getValue();
	//
	// for (int i = 0; i < commands.size(); i++) {
	// if (commands.get(i) != null) {
	// RoleCommand current = commands.get(i);
	// System.out.println("ROLE: " + current.getRole());
	// System.out.println("COMMAND: " + current.getCommands().get(0));
	// }
	//
	// }
	// }

	System.out.println("END SIMULATION");
	// System.exit(1);

    }

}
