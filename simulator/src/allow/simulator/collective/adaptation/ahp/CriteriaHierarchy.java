package allow.simulator.collective.adaptation.ahp;

import java.util.ArrayList;

public class CriteriaHierarchy extends Comparison {
    Comparison instanceImportanceComparison;
    Comparison[] criteriaImportanceComparison;
    int totalCriteriaNumber;

    public CriteriaHierarchy() {
	// TODO Auto-generated constructor stub
    }

    public CriteriaHierarchy(int instanceNumber, ArrayList<ArrayList<Double>> instanceImportance,
	    ArrayList<ArrayList<ArrayList<Double>>> criteriaImportance) {
	// TODO Auto-generated constructor stub
	instanceImportanceComparison = new Comparison(instanceImportance);
	totalCriteriaNumber = 0;
	criteriaImportanceComparison = new Comparison[instanceNumber];
	for (int i = 0; i < instanceNumber; i++) {
	    criteriaImportanceComparison[i] = new Comparison(criteriaImportance.get(i));
	    totalCriteriaNumber += criteriaImportanceComparison[i].weightVector.length;
	    criteriaImportanceComparison[i].scaleWeightVector(instanceImportanceComparison.getWeightVector()[i]);
	}
	generateWeightVector();
    }

    public CriteriaHierarchy(int instanceNumber, String instanceImportanceFile, String[] criteriaImportanceFiles) {
	// TODO Auto-generated constructor stub
	instanceImportanceComparison = new Comparison(
		FileOperations.fileToArrayListOfArrayLists(instanceImportanceFile));
	totalCriteriaNumber = 0;
	criteriaImportanceComparison = new Comparison[instanceNumber];
	for (int i = 0; i < instanceNumber; i++) {
	    criteriaImportanceComparison[i] = new Comparison(
		    FileOperations.fileToArrayListOfArrayLists(criteriaImportanceFiles[i]));
	    totalCriteriaNumber += criteriaImportanceComparison[i].weightVector.length;
	    criteriaImportanceComparison[i].scaleWeightVector(instanceImportanceComparison.getWeightVector()[i]);
	}
	generateWeightVector();
    }

    private void generateWeightVector() {
	weightVector = new double[totalCriteriaNumber];
	int k = 0;
	System.out.println();
	for (int i = 0; i < criteriaImportanceComparison.length; i++) {
	    for (int j = 0; j < criteriaImportanceComparison[i].weightVector.length; j++) {
		weightVector[k++] = criteriaImportanceComparison[i].weightVector[j];
		System.out.print(weightVector[k - 1] + ",");
		// System.out.println(weightVector[k-1]);

	    }

	}
    }

    public static void main(String[] args) {
	// TODO Auto-generated method stub
	// String [] files = new
	// String[]{"data/criteriaComparison3.csv","data/criteriaComparison3.csv"};
	// CriteriaHierarchy hierarchy = new
	// CriteriaHierarchy(2,"data/criteriaComparison2.csv", files);
	// String [] files = new
	// String[]{"data/SASO/criteriaComparison_A.csv","data/SASO/criteriaComparison_B.csv","data/SASO/criteriaComparison_FB.csv"};
	// CriteriaHierarchy hierarchy = new
	// CriteriaHierarchy(3,"data/SASO/criteriaComparison.csv", files);

	// criteria.calculateNormalisedMatrix();
	// criteria.getWeightVector();

	/*
	 * String [] files = new
	 * String[]{"data/UMS/alternativesValuesUtility.csv",
	 * "data/UMS/alternativesValuesPreferences.csv",
	 * "data/UMS/alternativesValuesData.csv"}; int [] options = new int []
	 * {1, 0, 0}; Ranking ranking = new Ranking
	 * ("data/UMS/criteriaComparison_case8.csv", files, options);
	 * ranking.getRanking();
	 */

    }

}
