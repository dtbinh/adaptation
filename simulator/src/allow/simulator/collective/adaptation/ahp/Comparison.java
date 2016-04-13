package allow.simulator.collective.adaptation.ahp;

import java.util.ArrayList;

public class Comparison {
    // ArrayList <ArrayList<Double>> matrix = new ArrayList
    // <ArrayList<Double>>();
    // double [][] matrix1;
    // double [][] normalisedMatrix;
    double[] weightVector;

    public Comparison() {
	// TODO Auto-generated constructor stub
    }

    /*
     * public Comparison(String file) { // reads comparison matrix from a file
     * //ArrayList <ArrayList<Double>> matrix = new ArrayList
     * <ArrayList<Double>>(); //calculateNormalisedMatrix(FileToMatrix(file));
     * calculateWeightVector(generateNormalisedMatrix(FileOperations.
     * fileToArrayListOfArrayLists(file))); }
     */
    public Comparison(ArrayList<ArrayList<Double>> matrix) {
	// TODO Auto-generated constructor stub
	// calculateNormalisedMatrix(matrix);
	calculateWeightVector(generateNormalisedMatrix(matrix));

    }

    /*
     * public Comparison(String file, int option) { //generates pairwise
     * comparison matrix for alternatives based on values of the criterion // if
     * option = 0 - minimizing criterion, 1 - maximizing ArrayList <Double>
     * values = FileOperations.fileToArrayList(file);
     * 
     * // generate comparison matrix based on the absolute values and create a
     * weight vector
     * //calculateNormalisedMatrix(valuesToComparisonMatrix(values, option));
     * calculateWeightVector(generateNormalisedMatrix(valuesToComparisonMatrix(
     * values, option))); }
     */
    public Comparison(ArrayList<Double> values, int option) {
	// generates pairwise comparison matrix for alternatives based on values
	// of the criterion
	// if option = 0 - minimizing criterion, 1 - maximizing
	calculateWeightVector(generateNormalisedMatrix(valuesToComparisonMatrix(values, option)));
	/*
	 * System.out.println(); weightVector = new double[values.size()]; if
	 * (option == 1){ double sum = 0; for (int i = 0; i < values.size();
	 * i++){ sum += values.get(i); } for (int i = 0; i < values.size();
	 * i++){ weightVector[i] = values.get(i)/sum;
	 * System.out.print(weightVector[i] + ","); } }
	 * 
	 * else if (option == 0){ double sum = 0; for (int i = 0; i <
	 * values.size(); i++){ sum += (1/values.get(i)); } for (int i = 0; i <
	 * values.size(); i++){ weightVector[i] = 1/(values.get(i)*sum);
	 * System.out.print(weightVector[i] + ","); } } System.out.println();
	 */

    }

    private double[][] generateNormalisedMatrix(ArrayList<ArrayList<Double>> matrix) {
	double[][] normalisedMatrix = new double[matrix.size()][matrix.get(0).size()];
	for (int j = 0; j < matrix.get(0).size(); j++) {// for each column
	    double sum = 0;
	    for (int i = 0; i < matrix.size(); i++) {
		sum += matrix.get(i).get(j);
	    }
	    for (int i = 0; i < matrix.size(); i++) {
		normalisedMatrix[i][j] = matrix.get(i).get(j) / sum;
	    }
	}
	return normalisedMatrix;
    }

    private double[][] generateNormalisedMatrix(double[][] matrix) {
	double[][] normalisedMatrix = new double[matrix.length][matrix[0].length];
	for (int j = 0; j < matrix[0].length; j++) {// for each column
	    double sum = 0;
	    for (int i = 0; i < matrix.length; i++) {
		sum += matrix[i][j];
	    }
	    for (int i = 0; i < matrix.length; i++) {
		normalisedMatrix[i][j] = matrix[i][j] / sum;
	    }
	}
	return normalisedMatrix;
    }

    public void calculateWeightVector(double[][] normalisedMatrix) {
	weightVector = new double[normalisedMatrix.length];
	// System.out.println();
	System.out.println(" weightVector:    ,");

	for (int i = 0; i < normalisedMatrix.length; i++) {
	    double sum = 0;
	    for (int j = 0; j < normalisedMatrix[i].length; j++) {
		sum += normalisedMatrix[i][j];
	    }
	    weightVector[i] = sum / normalisedMatrix[i].length;
	    System.out.print(weightVector[i] + ",");
	}
	System.out.println();
    }

    public void scaleWeightVector(double scale) {
	System.out.println();
	System.out.println(" modified weightVector    x" + scale + ":    ,");

	for (int i = 0; i < weightVector.length; i++) {
	    weightVector[i] *= scale;
	    System.out.print(weightVector[i] + ",");
	}
	System.out.println();
    }

    public double[] getWeightVector() {
	return weightVector;
    }

    public int getWeightVectorLength() {
	return weightVector.length;
    }

    public double[][] valuesToComparisonMatrix(ArrayList<Double> values, int option) {
	double max = -Double.MAX_VALUE;
	double min = Double.MAX_VALUE;
	double[][] matrix = new double[values.size()][values.size()];

	for (int i = 0; i < values.size(); i++) {
	    if (values.get(i) > max)
		max = values.get(i);
	    if (values.get(i) < min)
		min = values.get(i);
	}

	if (max == min) {
	    for (int i = 0; i < values.size(); i++) {
		for (int h = 0; h < values.size(); h++) {
		    matrix[i][h] = 1;
		}

	    }

	    return matrix;
	}

	for (int i = 0; i < values.size(); i++) {
	    for (int h = 0; h < values.size(); h++) {
		if (i == h) {
		    matrix[i][h] = 1;
		} else // if option = 0 - the smaller the better - minimising
		       // objective, 1 - the bigger the better
		if (option == 0) {
		    if (values.get(i) <= values.get(h)) {
			matrix[i][h] = 8. * (values.get(h) - values.get(i)) / (max - min) + 1.;
			matrix[h][i] = 1. / matrix[i][h];
		    }
		} else if (option == 1) {
		    if (values.get(i) >= values.get(h)) {
			matrix[i][h] = 8. * (values.get(i) - values.get(h)) / (max - min) + 1.;
			matrix[h][i] = 1. / matrix[i][h];
		    }
		}
	    }
	}
	for (int i = 0; i < matrix.length; i++) {
	    for (int j = 0; j < matrix[i].length; j++)
		System.out.print(matrix[i][j] + "  ");
	    System.out.println();
	}

	return matrix;
    }

    public static void main(String[] args) {
	// TODO Auto-generated method stub
	Comparison criteria = new Comparison(
		FileOperations.fileToArrayListOfArrayLists("data/SASO/FBCFindNew/utilityComparison_FBC.csv"));
	// criteria.calculateNormalisedMatrix();
	// criteria.getWeightVector();

    }

}
