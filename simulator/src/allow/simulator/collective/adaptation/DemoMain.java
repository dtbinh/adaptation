package allow.simulator.collective.adaptation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.naming.ConfigurationException;

import allow.simulator.collective.adaptation.ensemble.Ensemble;
import allow.simulator.collective.adaptation.ensemble.Issue;
import allow.simulator.collective.adaptation.model.IssueResolution;
import allow.simulator.collective.adaptation.presentation.CAWindow;

/**
 * this is an entry point of the application
 * 
 * @author Antonio
 * 
 */
public class DemoMain {
    private final static String PROP_PATH = "adaptation.properties";

    public static void main(String[] args) throws ConfigurationException, FileNotFoundException {

	// reading property file
	String propPath = PROP_PATH;
	if (args.length > 0) {
	    propPath = args[0];
	}
	Properties props = new Properties();
	try {
	    props.load(new FileInputStream(propPath));
	} catch (IOException e) {
	    System.err.println("Cannot read configuration file: " + e.getMessage());
	}
	// setting up repository path
	if (props.getProperty("repo_path") == null) {
	    throw new ConfigurationException("repo_path property is not found in the config file: execution aborted");
	}
	;
	// demo management system construction

	DemoManagementSystem dms = DemoManagementSystem.initializeSystem(props.getProperty("repo_path"));

	// Ensemble Creation - Instance of Ensemble 1
	Ensemble E1 = dms.getEnsemble("E2", null);
	EnsembleManager route1Manager = new EnsembleManager(E1);

	// create all the Route Managers inside an Ensemble

	// Roles Creation of the Ensemble
	RoleManager FBDriver = route1Manager.getRolebyType("H1");

	Issue issue1 = new Issue();
	issue1.setIssueType("I5");
	IssueResolution resolution1 = new IssueResolution(1, "ISSUE_TRIGGERED", FBDriver, FBDriver, issue1, null);
	FBDriver.addIssueResolution(resolution1);

	ArrayList<IssueResolution> resolutions = new ArrayList<IssueResolution>();
	resolutions.add(resolution1);
	route1Manager.setActiveIssueResolutions(resolutions);

	ArrayList<EnsembleManager> ensembles = new ArrayList<EnsembleManager>();
	ensembles.add(route1Manager);

	// ArrayList<Role> runningRoles = dms.getRoleInstances();

	// CAWindow window = new CAWindow(ensembles);
	CAWindow window = new CAWindow(ensembles, null, 0, null);
	// window.loadActiveIssueResolutionsTable(route1Manager.getActiveIssueResolutions(),
	// route1Manager);
	// window.loadMonitoringTable(route1.getRoles().get(0).getMonitors(),
	// route1);
	// window.loadAnalyzerFrame(route1.getRoles().get(0).getMonitors(),
	// route1);
	// window.loadExecuteFrame(route1.getRoles().get(0).getMonitors(),
	// route1);
	// window.loadPlannerFrame(route1.getRoles().get(0).getMonitors(),
	// route1);
	// window.loadTreeFrame();

	// route1Manager.checkRoles(window);

	// System.out.println("END SIMULATION");

    }
}
