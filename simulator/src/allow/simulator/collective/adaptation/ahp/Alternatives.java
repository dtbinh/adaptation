package allow.simulator.collective.adaptation.ahp;

import java.util.ArrayList;

public class Alternatives {
    // Comparison [] optionComparisons; // Comparisons of alternative solutions
    // for each criteria
    double[][] scores; // m x n matrix of scores of alternative solutions for
		       // each criteria, where n is number of criteria, m is
		       // number of alternatives

    public Alternatives() {

	// TODO Auto-generated constructor stub
    }
    /*
     * public Alternatives(String [] files, int [] minmaxFlags) { Comparison []
     * optionComparisons; // TODO Auto-generated constructor stub
     * optionComparisons = new Comparison [files.length]; for (int j = 0; j <
     * files.length; j++) { System.out.println(j); optionComparisons[j] = new
     * Comparison (files[j], minmaxFlags[j]); double [] weights =
     * optionComparisons[j].getWeightVector(); if (j == 0) { scores = new double
     * [weights.length][files.length]; } for (int i = 0; i < weights.length; i
     * ++) { scores[i][j] = weights[i]; } }
     * 
     * }
     */

    public Alternatives(ArrayList<ArrayList<Double>> alternatives, int[] minmaxFlags) {
	Comparison[] optionComparisons;
	// TODO Auto-generated constructor stub
	optionComparisons = new Comparison[alternatives.size()];
	for (int j = 0; j < alternatives.size(); j++) {
	    optionComparisons[j] = new Comparison(alternatives.get(j), minmaxFlags[j]);
	    double[] weights = optionComparisons[j].getWeightVector();
	    if (j == 0) {
		scores = new double[weights.length][alternatives.size()];
	    }
	    for (int i = 0; i < weights.length; i++) {
		scores[i][j] = weights[i];
	    }
	}

    }

    public static void main(String[] args) {

    }
}
