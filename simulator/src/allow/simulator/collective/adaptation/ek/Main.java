package allow.simulator.collective.adaptation.ek;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String args[]) {
	String modelPath = "jdbc:mysql://localhost:3306/";
	String modelName = "ek_adaptation";
	String userName = "root";
	String password = "";

	// Create EKConfiguration or read from file
	EKConfiguration config = new EKConfiguration(modelPath, modelName, userName, password);

	// Initialize EvoKnowledge
	EvoKnowledge.initialize(config, "ek", true);

	// Create a new instance of EvoKnowledge
	EvoKnowledge ek = EvoKnowledge.createEvoKnowledge();

	List<Experience> experiences = new ArrayList<Experience>();

	List<String> issueTypes = new ArrayList<String>();
	issueTypes.add("TrafficDetected");
	issueTypes.add("AccidentDetected");
	issueTypes.add("FlexiBusBroken");
	issueTypes.add("StrikeWarning");
	issueTypes.add("FlexiBusDelay");

	List<String> solverTypes = new ArrayList<String>();
	solverTypes.add("DeleteRoute");
	solverTypes.add("UpdateRoute");
	solverTypes.add("AskHelpCarPool");
	solverTypes.add("ChangePathDropPP");
	solverTypes.add("ChangePathAddPP");
	solverTypes.add("AskHelpOtherRoute");

	HashMap<String, Integer> issueTypesMap = new HashMap<String, Integer>();

	issueTypesMap.put("TrafficDetected", 1);
	issueTypesMap.put("AccidentDetected", 2);
	issueTypesMap.put("FlexiBusBroken", 3);
	issueTypesMap.put("StrikeWarning", 4);
	issueTypesMap.put("FlexiBusDelay", 5);

	HashMap<String, Integer> solverTypesMap = new HashMap<String, Integer>();

	solverTypesMap.put("DeleteRoute", 1);
	solverTypesMap.put("UpdateRoute", 2);
	solverTypesMap.put("AskHelpCarPool", 3);
	solverTypesMap.put("ChangePathDropPP", 4);
	solverTypesMap.put("ChangePathAddPP", 5);
	solverTypesMap.put("AskHelpOtherRoute", 6);

	// FILL THE DB CONTENT WITH RANDOM EXPERIENCES
	System.out.println("SIZE BEFORE: " + experiences.size());
	for (int i = 0; i < 5000; i++) {
	    experiences = AddExperience(experiences, issueTypes, solverTypes, issueTypesMap, solverTypesMap);
	}
	System.out.println("SIZE AFTER: " + experiences.size());
	ek.learn(experiences);

	// Predict parameters
	int issueTypePred = 1;
	int numberOfPassengersWaitingPred = 1;
	int numberOfPassengersPred = 2;
	Query query = new Query(issueTypePred, numberOfPassengersWaitingPred, numberOfPassengersPred);
	List<Prediction> predicted = ek.predict(query);
	System.out.println(predicted);

	// Clean model
	// ek.cleanModel();

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

	if (issueType.equals("TrafficDetected")) {
	    // Possible Solvers : AskHelpOtherRoute (6), ChangePathDropPP
	    // (4),ChangePathAddPP (5)
	    Random rand = new Random();
	    randomSolver = rand.nextInt((5 - 3) + 1) + 3;

	} else if (issueType.equals("AccidentDetected")) {
	    randomSolver = 1;

	} else if (issueType.equals("FlexiBusBroken")) {
	    randomSolver = 2;

	} else if (issueType.equals("StrikeWarning")) {
	    randomSolver = 0;

	} else if (issueTypes.equals("FlexiBusDelay")) {
	    randomSolver = 1;
	}

	String solverType = solverTypes.get(randomSolver);

	// select Solver Type Randomly
	// int randomSolver = new Random().nextInt(issueTypes.size());
	// String solverType = solverTypes.get(randomSolver);
	System.out.println("Solver Type Seleceted: " + solverType);

	int issueTypeSelected = issueTypesMap.get(issueType);
	int solverTypeSelected = solverTypesMap.get(solverType);

	Random rand1 = new Random();
	int numberOfPassengersWaiting = rand1.nextInt((5 - 1) + 1) + 1;

	Random rand2 = new Random();
	int numberOfPassengers = rand2.nextInt((5 - 1) + 1) + 1;

	Random r = new Random();
	double utilityFirst = 0.4 + (0.99 - 0.4) * r.nextDouble();

	String sValue = (String) String.format("%.2f", utilityFirst);
	// System.out.println(sValue);
	String newValue = sValue.replace(',', '.');
	double utility = Double.parseDouble(newValue);
	// System.out.println("RANDOM UTILITY: " + utility);

	Experience experience = new Experience(issueTypeSelected, solverTypeSelected, numberOfPassengersWaiting,
		numberOfPassengers, utility);
	experiences.add(experience);
	return experiences;
    }

}
