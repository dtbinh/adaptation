package allow.simulator.collective.adaptation.ahp;

import java.util.ArrayList;

public class Ranking {
    Alternatives alternatives;
    Comparison criteria;
    double[] rankings;

    // for 1-tier criteria hierarchy (criteria/preferences of one agent)
    public Ranking(ArrayList<ArrayList<Double>> Criteria, ArrayList<ArrayList<Double>> Alternatives,
	    int[] minmaxCriteriaOptions) {
	// Criteria - pairwise comparisons of importance of criteria used (size
	// m*m, where m - a number of criteria)
	// Alternatives - values of criteria for all alternatives (size m *n,
	// where m is a total number of criteria, n - a number of alternatives)
	// minmaxCriteriaOptions - array of m integer values {0,1},
	// minmaxCriteriaOptions[i] = 0 - we are minimizing i-th criterion,
	// minmaxCriteriaOptions[i] = 1 - we are maximizing i-th criterion
	criteria = new Comparison(Criteria);
	alternatives = new Alternatives(Alternatives, minmaxCriteriaOptions);

    }

    // for 2-tier criteria hierarchy (preferences of several agents)
    public Ranking(int nrAgents, ArrayList<ArrayList<Double>> agentImportance,
	    ArrayList<ArrayList<ArrayList<Double>>> criteriaImportance, ArrayList<ArrayList<Double>> alternativeValues,
	    int[] minmaxCriteriaOptions) {
	// nrAgents - number of agents whose utilities have to be considered
	// agentImportance - matrix of pairwise comparisons of utility
	// importance set by the current resolver (size nrAgents*nrAgents)
	// criteriaImportance - arraylist that contains matrices of pairwise
	// comparisons of criteria importance
	// alternativeValues - values of alternatives with respect to each
	// criterion, size m *n where m is total number of criteria of all
	// agents, n - number of alternatives
	// minmaxCriteriaOptions - n array of flags indicating whether the
	// criteria must be minimized (0) or maximized (1)
	criteria = new CriteriaHierarchy(nrAgents, agentImportance, criteriaImportance);
	alternatives = new Alternatives(alternativeValues, minmaxCriteriaOptions);
	if (criteria.weightVector.length != alternatives.scores[0].length)
	    System.out.println("ERROR in sizes" + criteria.weightVector.length + " vs " + alternatives.scores.length);
	else
	    getRanking();

    }

    public Ranking(int nrAgents, String agentImportanceFile, String[] criteriaImportanceFiles,
	    String[] filesAlternatives, int[] minmaxCriteriaOptions) {
	criteria = new CriteriaHierarchy(nrAgents, FileOperations.fileToArrayListOfArrayLists(agentImportanceFile),
		FileOperations.fileArrayTo3DArrayList(criteriaImportanceFiles));
	alternatives = new Alternatives(FileOperations.fileArrayTo2DArrayList(filesAlternatives),
		minmaxCriteriaOptions);
	if (criteria.weightVector.length != alternatives.scores[0].length)
	    System.out.println("ERROR in sizes" + criteria.weightVector.length + " vs " + alternatives.scores.length);
	else
	    getRanking();

    }

    public double[] getRanking() {
	// returns final rankings of alternatives
	int aRows = alternatives.scores.length;
	int aColumns = alternatives.scores[0].length;
	int bRows = criteria.weightVector.length;
	int bColumns = 1;

	if (aColumns != bRows) {
	    throw new IllegalArgumentException(
		    "alternatives.scores[0].length (number of criterion scores for each alternative) and criteria.weightVector.length (number of criteria) must be the same length");
	}

	rankings = new double[aRows];
	System.out.print("final scores:    ,");
	for (int i = 0; i < aRows; i++) { // aRow
	    rankings[i] = 0.0;
	    for (int j = 0; j < bRows; j++) { // bColumn
		rankings[i] += alternatives.scores[i][j] * criteria.weightVector[j];
	    }
	    System.out.print(rankings[i] + ",");
	}
	return rankings;

    }

    public int returnBestAlternative() {
	int index = 0;
	double best = rankings[0];
	for (int i = 1; i < rankings.length; i++) {
	    if (rankings[i] > best) {
		index = i;
		best = rankings[i];
	    }
	}
	return index;
    }

    public static void main(String[] args) {
	// TODO Auto-generated method stub
	// String [] files = new
	// String[]{"data/SASO/alternativesValuesUtility.csv"};//,
	// "data/SASO/alternativesValuesPreferences.csv",
	// "data/SASO/alternativesValuesData.csv"};
	// int [] options = new int [] {1};//, 0, 0};
	// CriteriaHierarchy hierarchy = new CriteriaHierarchy(2,"data/SASO/",
	// criteriaImportanceFiles);

	/*
	 * String [] criteriaImportanceFiles = new
	 * String[]{"data/SASO/criteriaComparison_A.csv",
	 * "data/SASO/criteriaComparison_B.csv",
	 * "data/SASO/criteriaComparison_C.csv",
	 * "data/SASO/criteriaComparison_D.csv"}; String [] filesAlternatives =
	 * new String[]{"data/SASO/FB2ChangeRoute/alternativesPA_time.csv",
	 * "data/SASO/FB2ChangeRoute/alternativesPA_cost.csv",
	 * "data/SASO/FB2ChangeRoute/alternativesPA_walking.csv",
	 * "data/SASO/FB2ChangeRoute/alternativesPB_time.csv",
	 * "data/SASO/FB2ChangeRoute/alternativesPB_cost.csv",
	 * "data/SASO/FB2ChangeRoute/alternativesPC_time.csv",
	 * "data/SASO/FB2ChangeRoute/alternativesPD_cost.csv",
	 * "data/SASO/FB2ChangeRoute/alternativesPC_walking.csv",
	 * "data/SASO/FB2ChangeRoute/alternativesPD_cost.csv",
	 * "data/SASO/FB2ChangeRoute/alternativesPD_walking.csv", }; int []
	 * options = new int [] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; Ranking ranking
	 * = new Ranking
	 * (4,"data/SASO/FB2ChangeRoute/utilityComparison_FB2.csv",
	 * criteriaImportanceFiles, filesAlternatives, options);
	 */
	/*
	 * String [] criteriaImportanceFiles = new
	 * String[]{"data/SASO/criteriaComparison_A.csv",
	 * "data/SASO/criteriaComparison_B.csv",
	 * "data/SASO/criteriaComparison_E.csv"}; String [] filesAlternatives =
	 * new String[]{"data/SASO/FB3ChangeRoute/alternativesPA_time.csv",
	 * "data/SASO/FB3ChangeRoute/alternativesPA_cost.csv",
	 * "data/SASO/FB3ChangeRoute/alternativesPA_walking.csv",
	 * "data/SASO/FB3ChangeRoute/alternativesPB_time.csv",
	 * "data/SASO/FB3ChangeRoute/alternativesPB_cost.csv",
	 * "data/SASO/FB3ChangeRoute/alternativesPE_time.csv",
	 * "data/SASO/FB3ChangeRoute/alternativesPE_cost.csv",
	 * "data/SASO/FB3ChangeRoute/alternativesPE_walking.csv", }; int []
	 * options = new int [] {0, 0, 0, 0, 0, 0, 0, 0}; Ranking ranking = new
	 * Ranking (3,"data/SASO/FB3ChangeRoute/utilityComparison_FB3.csv",
	 * criteriaImportanceFiles, filesAlternatives, options);
	 */

	String[] criteriaImportanceFiles = new String[] { "data/SASO/criteriaComparison_A.csv",
		"data/SASO/criteriaComparison_B.csv", "data/SASO/criteriaComparison_FBC.csv" };
	String[] filesAlternatives = new String[] { "data/SASO/FBCFindNew/alternativesPA_time.csv",
		"data/SASO/FBCFindNew/alternativesPA_cost.csv", "data/SASO/FBCFindNew/alternativesPA_walking.csv",
		"data/SASO/FBCFindNew/alternativesPB_time.csv", "data/SASO/FBCFindNew/alternativesPB_cost.csv",
		"data/SASO/FBCFindNew/alternativesFBC_cost.csv", };
	int[] options = new int[] { 0, 0, 0, 0, 0, 0 };
	Ranking ranking = new Ranking(3, "data/SASO/FBCFindNew/utilityComparison_FBC.csv", criteriaImportanceFiles,
		filesAlternatives, options);

	// Alternatives alternatives = new Alternatives
	// (FileOperations.fileArrayToArrayList(filesAlternatives), options);
	// ranking.getRanking();

    }

}
