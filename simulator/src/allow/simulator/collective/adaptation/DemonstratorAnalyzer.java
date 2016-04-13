package allow.simulator.collective.adaptation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import allow.simulator.collective.adaptation.api.CollectiveAdaptationCommandExecution;
import allow.simulator.collective.adaptation.api.CollectiveAdaptationEnsemble;
import allow.simulator.collective.adaptation.api.CollectiveAdaptationInterface;
import allow.simulator.collective.adaptation.api.CollectiveAdaptationProblem;
import allow.simulator.collective.adaptation.api.CollectiveAdaptationSolution;
import allow.simulator.collective.adaptation.api.RoleCommand;
import allow.simulator.collective.adaptation.ek.EKConfiguration;
import allow.simulator.collective.adaptation.ek.EvoKnowledge;
import allow.simulator.collective.adaptation.ek.Experience;
import allow.simulator.collective.adaptation.ensemble.Ensemble;
import allow.simulator.collective.adaptation.ensemble.Issue;
import allow.simulator.collective.adaptation.model.IssueCommunication;
import allow.simulator.collective.adaptation.model.IssueResolution;
import allow.simulator.collective.adaptation.presentation.CATree;
import allow.simulator.collective.adaptation.presentation.CAWindow;

public class DemonstratorAnalyzer implements CollectiveAdaptationInterface {

    private final static String PROP_PATH = "adaptation.properties";
    private static final String STYLE_INIT = "verticalAlign=middle;dashed=false;dashPattern=5;rounded=true;fillColor=white;size=2";
    private static final String STYLE_ROLE = "verticalAlign=middle;dashed=false;dashPattern=5;rounded=true;align=center;fontSize=9;";

    private static final String STYLE_ISSUE_EDGE = "fontColor=#FF0000;fontSize=8;endArrow=classic;html=1;fontFamily=Helvetica;align=left;";

    // private final static String PreferencesDir =
    // "scenario/ALLOWEnsembles/Preferences/";

    private CollectiveAdaptationCommandExecution executor;

    @Override
    public void executeCap(CollectiveAdaptationProblem cap, CollectiveAdaptationCommandExecution executor) {

	// call the analyzer with the specific CAP

	// reading property file
	// String propPath = PROP_PATH;

	this.executor = executor;

	Properties props = new Properties();
	try {
	    props.load(getClass().getClassLoader().getResourceAsStream(PROP_PATH));
	} catch (FileNotFoundException e) {
	    System.out.println("Error loading file " + e.getMessage());

	    throw new NullPointerException(e.getMessage());

	} catch (IOException e) {
	    System.out.println("Error loading file " + e.getMessage());

	    throw new NullPointerException(e.getMessage());

	}

	// ///// Evoknowledge SETUP/////////
	// String modelPath = "jdbc:postgresql://localhost:5432/";
	String modelPath = "jdbc:mysql://localhost:3306/";
	String modelName = "antonioek";
	String userName = "root";
	String password = "";

	// Create EKConfiguration or read from file
	EKConfiguration config = new EKConfiguration(modelPath, modelName, userName, password);

	// Initialize EvoKnowledge
	EvoKnowledge.initialize(config, "ek", true);

	// Create a new instance of EvoKnowledge
	EvoKnowledge ek = EvoKnowledge.createEvoKnowledge();
	// ////////////////////////////
	/* CREATE THE LEARNING BASE - EXPERIENCE IN THE DB */
	List<Experience> experiences = new ArrayList<Experience>();

	List<String> issueTypes = new ArrayList<String>();
	issueTypes.add("IntenseTraffic");
	issueTypes.add("AccidentDetected");
	issueTypes.add("FlexiBusBroken");
	issueTypes.add("StrikeWarning");
	issueTypes.add("FlexiBusDelay");
	issueTypes.add("RouteBlocked");

	List<String> solverTypes = new ArrayList<String>();
	solverTypes.add("DeleteRoute");
	solverTypes.add("UpdateRoute");
	solverTypes.add("AskHelpCarPool");
	solverTypes.add("ChangePathDropPP");
	solverTypes.add("ChangePathAddPP");
	solverTypes.add("AskHelpOtherRoute");
	solverTypes.add("AssignPassengerOfB");
	solverTypes.add("ExitAndChangeRoutePP");

	HashMap<String, Integer> issueTypesMap = new HashMap<String, Integer>();

	issueTypesMap.put("IntenseTraffic", 1);
	issueTypesMap.put("AccidentDetected", 2);
	issueTypesMap.put("FlexiBusBroken", 3);
	issueTypesMap.put("StrikeWarning", 4);
	issueTypesMap.put("FlexiBusDelay", 5);
	issueTypesMap.put("RouteBlocked", 6);

	HashMap<String, Integer> solverTypesMap = new HashMap<String, Integer>();

	solverTypesMap.put("DeleteRoute", 1);
	solverTypesMap.put("UpdateRoute", 2);
	solverTypesMap.put("AskHelpCarPool", 3);
	solverTypesMap.put("ChangePathDropPP", 4);
	solverTypesMap.put("ChangePathAddPP", 5);
	solverTypesMap.put("AskHelpOtherRoute", 6);
	solverTypesMap.put("AssignPassengerOfB", 7);
	solverTypesMap.put("ExitAndChangeRoutePP", 8);

	// FILL THE DB CONTENT WITH RANDOM EXPERIENCES
	System.out.println("SIZE BEFORE: " + experiences.size());
	for (int i = 0; i < 5000; i++) {
	    experiences = AddExperience(experiences, issueTypes, solverTypes, issueTypesMap, solverTypesMap);
	}
	System.out.println("SIZE AFTER: " + experiences.size());
	ek.learn(experiences);
	// ////////////////////////////
	/* END LEARNING BASE CREATED */

	// ///////////////////////

	// demo management system construction

	DemoManagementSystem dms = null;
	try {
	    dms = DemoManagementSystem.initializeSystem("scenario/ensembles/");
	} catch (FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	// creation of ensembles
	List<EnsembleManager> ensembles = new ArrayList<EnsembleManager>();
	for (int i = 0; i < cap.getEnsembles().size(); i++) {
	    CollectiveAdaptationEnsemble ensemble = cap.getEnsembles().get(i);
	    String EnsembleName = ensemble.getEnsembleName();
	    Ensemble e = dms.getEnsemble(EnsembleName, cap);
	    EnsembleManager manager = new EnsembleManager(e);

	    // set the Evoknowledge of the Ensemble
	    manager.setEk(ek);

	    // add the ensemble to the list
	    ensembles.add(manager);

	}

	// retrieve the Issue Index
	int IssueIndex = issueTypesMap.get(cap.getIssue());

	// run the Analyzer

	CAWindow window = new CAWindow(ensembles, cap, IssueIndex, executor);
	// CollectiveAdaptationSolution solution = this.run(cap, ensembles,
	// window, cap.getIssue(), cap.getCapID(),
	// cap.getStartingRole(), IssueIndex);

	// return solution;

    }

    public CollectiveAdaptationSolution run(CollectiveAdaptationProblem cap, List<EnsembleManager> ensembles,
	    CAWindow window, String issueName, String capID, String startingRole, int issueIndex) {

	CollectiveAdaptationSolution solution = new CollectiveAdaptationSolution(capID, null);
	Issue issue = new Issue();
	issue.setIssueType(issueName);

	// search the role that can trigger the specific issue
	EnsembleManager en = ensembles.stream()
		.filter(e -> e.getEnsemble().getName().equals(cap.getStartingRoleEnsemble())).findFirst().get();

	RoleManager r = en.getRolebyType(startingRole);
	System.out.println("ISSUE TRIGGERED: " + issue.getIssueType());

	IssueResolution resolution1 = new IssueResolution(1, "ISSUE_TRIGGERED", r, r, issue, null);
	resolution1.setRoot(true);
	r.addIssueResolution(resolution1);

	EnsembleManager em = null;

	// add the issueresolution to the right Ensemble
	for (int i = 0; i < ensembles.size(); i++) {
	    for (int j = 0; j < ensembles.get(i).getRolesManagers().size(); j++) {
		RoleManager currentManager = ensembles.get(i).getRolesManagers().get(j);
		if (currentManager.getRole().getType().equalsIgnoreCase(r.getRole().getType())) {
		    ArrayList<IssueResolution> resolutions = new ArrayList<IssueResolution>();

		    em = ensembles.get(i);
		    if (em.getIssueCommunications() != null) {
			em.getIssueCommunications().clear();
		    }

		    solution.setCapID(capID);
		    HashMap<String, List<RoleCommand>> ensembleCommands = new HashMap<String, List<RoleCommand>>();
		    List<RoleCommand> commands = new ArrayList<RoleCommand>();
		    ensembleCommands.put(em.getEnsemble().getName(), commands);

		    solution.setEnsembleCommands(ensembleCommands);

		    // update id of the issue resolution
		    em.setIssueResolutionCount(1);
		    resolution1.setIssueResolutionID(em.getIssueResolutionCount());

		    resolutions.add(resolution1);
		    em.setActiveIssueResolutions(resolutions);

		    List<IssueCommunication> relatedComs = new ArrayList<IssueCommunication>();

		    em.setCommunicationsRelations(resolution1, relatedComs);

		    CATree hierarchyTree = createHierarchyTree(ensembles);

		    window.updateHierarchy(hierarchyTree);

		    em.checkIssues(cap, capID, window, ensembles, solution, issueIndex, hierarchyTree);

		    break;
		}
	    }
	}
	// retrieve the final solution for the ensemble
	List<RoleCommand> roleCommands = new ArrayList<RoleCommand>();
	solution.setCapID(capID);
	for (int i = 0; i < em.getRolesManagers().size(); i++) {
	    RoleManager rm = em.getRolesManagers().get(i);
	    // System.out.println("ROLE: " + rm.getRole().getType());
	    RoleCommand command = rm.getRoleCommands();

	    roleCommands.add(command);

	}

	HashMap<String, List<RoleCommand>> ensembleCommands = new HashMap<String, List<RoleCommand>>();
	ensembleCommands.put(em.getEnsemble().getName(), roleCommands);
	solution.setEnsembleCommands(ensembleCommands);
	return solution;

    }

    private CATree createHierarchyTree(List<EnsembleManager> ensembles) {
	CATree hierarchyTree = new CATree();

	// CREATE FIRST PART OF THE HIERARCHY TREE

	Object root1 = hierarchyTree.insertNodeHierarchy(hierarchyTree.getDefaultParent(), null, "UMS", STYLE_INIT);

	Object v1 = hierarchyTree.insertNodeHierarchy(hierarchyTree.getDefaultParent(), null, "FBC", STYLE_INIT);
	hierarchyTree.insertEdge(hierarchyTree.getDefaultParent(), "", "", root1, v1, STYLE_ISSUE_EDGE);

	for (int k = 0; k < ensembles.size(); k++) {
	    EnsembleManager e = ensembles.get(k);
	    if (!(e.getEnsemble().getName().contains("Flexi"))) {
		List<RoleManager> roles = e.getRolesManagers();
		for (int m = 0; m < roles.size(); m++) {
		    RoleManager role = roles.get(m);
		    if (role.getRole().getType().contains("RouteManagement")) {
			Object v = hierarchyTree.insertNodeHierarchy(hierarchyTree.getDefaultParent(), null,
				role.getRole().getType(), STYLE_INIT);
			hierarchyTree.insertEdge(hierarchyTree.getDefaultParent(), "", "", v1, v, STYLE_ISSUE_EDGE);
			for (int n = 0; n < roles.size(); n++) {
			    RoleManager role1 = roles.get(n);
			    if (role1.getRole().getType().contains("RoutePassenger")) {
				Object v2 = hierarchyTree.insertNodeHierarchy(hierarchyTree.getDefaultParent(), null,
					role1.getRole().getType(), STYLE_INIT);
				hierarchyTree.insertEdge(hierarchyTree.getDefaultParent(), "", "", v, v2,
					STYLE_ISSUE_EDGE);
			    } else if (role1.getRole().getType().contains("FlexibusDriver")) {
				Object v2 = hierarchyTree.insertNodeHierarchy(hierarchyTree.getDefaultParent(), null,
					role1.getRole().getType(), STYLE_INIT);
				hierarchyTree.insertEdge(hierarchyTree.getDefaultParent(), "", "", v, v2,
					STYLE_ISSUE_EDGE);
			    }

			}
		    }

		}

	    }

	}
	return hierarchyTree;
    }

    private static List<Experience> AddExperience(List<Experience> experiences, List<String> issueTypes,
	    List<String> solverTypes, HashMap<String, Integer> issueTypesMap, HashMap<String, Integer> solverTypesMap) {
	// select Issue Type Randomly
	int randomIssue = new Random().nextInt(issueTypes.size());
	String issueType = issueTypes.get(randomIssue);
	// System.out.println("Issue Type Seleceted: " + issueType);

	int randomSolver = 0;

	// solverTypesMap.put("DeleteRoute", 1);
	// solverTypesMap.put("UpdateRoute", 2);
	// solverTypesMap.put("AskHelpCarPool", 3);

	if (issueType.equals("IntenseTraffic")) {
	    // Possible Solvers : AskHelpOtherRoute (6), ChangePathDropPP
	    // (4),ChangePathAddPP (5)
	    Random rand = new Random();
	    randomSolver = rand.nextInt(5);

	} else if (issueType.equals("AccidentDetected")) {
	    randomSolver = 1;

	} else if (issueType.equals("FlexiBusBroken")) {
	    randomSolver = 2;

	} else if (issueType.equals("StrikeWarning")) {
	    randomSolver = 0;

	} else if (issueType.equals("FlexiBusDelay")) {
	    randomSolver = 1;
	} else if (issueType.equals("RouteBlocked")) {
	    List<Integer> numbers = Arrays.asList(3, 6, 7);
	    int index = randomThree(numbers);
	    randomSolver = numbers.get(index);
	}

	String solverType = solverTypes.get(randomSolver);

	// select Solver Type Randomly
	// int randomSolver = new Random().nextInt(issueTypes.size());
	// String solverType = solverTypes.get(randomSolver);
	// System.out.println("Solver Type Seleceted: " + solverType);

	int issueTypeSelected = issueTypesMap.get(issueType);
	int solverTypeSelected = solverTypesMap.get(solverType);

	Random rand1 = new Random();
	int numberOfPassengersWaiting = rand1.nextInt((5 - 1) + 1) + 1;

	Random rand2 = new Random();
	int numberOfPassengers = rand2.nextInt((5 - 1) + 1) + 1;

	Random r = new Random();
	double utilityFirst = 0.4 + (0.99 - 0.4) * r.nextDouble();

	String sValue = String.format("%.2f", utilityFirst);
	// System.out.println(sValue);
	String newValue = sValue.replace(',', '.');
	double utility = Double.parseDouble(newValue);
	// System.out.println("RANDOM UTILITY: " + utility);

	Experience experience = new Experience(issueTypeSelected, solverTypeSelected, numberOfPassengersWaiting,
		numberOfPassengers, utility);
	experiences.add(experience);
	return experiences;
    }

    private static int randomThree(List<Integer> numbers) {
	Random rand = new Random();
	return (rand.nextInt(numbers.size()));
    }

}
