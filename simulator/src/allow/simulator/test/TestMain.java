package allow.simulator.test;

import java.io.FileNotFoundException;

import javax.naming.ConfigurationException;

import org.nlogo.app.App;

public class TestMain {
    private final static String PROP_PATH = "adaptation.properties";

    public static void main(String[] args) throws ConfigurationException, FileNotFoundException {
    	App.main(new String[0]);
        
    	try {
          java.awt.EventQueue.invokeAndWait(
        new Runnable() {
          public void run() {
            try {
              App.app().open("/Users/Andi/Documents/DFKI/Allow Ensembles/Simulator/Trento/Trento.nlogo");
            }
            catch(java.io.IOException ex) {
              ex.printStackTrace();
            }}});
        }
        catch(Exception ex) {
          ex.printStackTrace();
        }
      
    
		// Get String containing path to configuration file.
//		Path configPath = Paths.get(args[0]);
//
//		// Read configuration.
//		Configuration config = null;
//
//		try {
//			config = Configuration.fromJSON(configPath);
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		if (config == null)
//			return;
//		
//		// Setup simulator from configuration.
//		SimulationParameter params = new SimulationParameter();
//		params.BehaviourSpaceRunNumber = 0;
//		params.KnowledgeModel = "without";
//
//		try {
//			Simulator.Instance().setup(config, params, null);
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	try {
//	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//	} catch (Exception e) {
//
//	}
//
//	System.gc();
//	String propPath = PROP_PATH;
//	if (args.length > 0) {
//	    propPath = args[0];
//	}
//
//	// Ensemble instantiation
//	List<Entity> roles = new ArrayList<Entity>();
//
//	Coordinate c1 = new Coordinate(11.5, 34.4);
//	Coordinate c2 = new Coordinate(11.5, 34.4);
//	PublicTransportation busDriver = new PublicTransportation(1, null, null, null, 20);
//	Person passenger1 = new Person(2, Gender.MALE, Profile.WORKER, null, null, c1, true, true, true, null, null);
//	Person passenger2 = new Person(3, Gender.MALE, Profile.WORKER, null, null, c2, true, true, true, null, null);
//
//	roles.add(busDriver);
//	roles.add(passenger1);
//	roles.add(passenger2);
//
//	Ensemble en = new Ensemble("routeA", roles);
//	en.solveIssue("IntenseTraffic");

	// System.exit(1);

    }

}
