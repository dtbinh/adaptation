package allow.simulator.collective.adaptation.presentation;

import allow.simulator.collective.adaptation.ek.Prediction;

public class RankedSolution {

    private String issueType;

    public String getIssueType() {
	return issueType;
    }

    public void setIssueType(String issueType) {
	this.issueType = issueType;
    }

    public String getSolution() {
	return solution;
    }

    public void setSolution(String solution) {
	this.solution = solution;
    }

    public Prediction getPrediction() {
	return prediction;
    }

    public void setPrediction(Prediction prediction) {
	this.prediction = prediction;
    }

    public String getFeasible() {
	return feasible;
    }

    public void setFeasible(String feasible) {
	this.feasible = feasible;
    }

    public String getRanking() {
	return ranking;
    }

    public void setRanking(String ranking) {
	this.ranking = ranking;
    }

    private String solution;
    private Prediction prediction;
    private String feasible;
    private String ranking;

    public RankedSolution(String issueType, String solution, Prediction prediction, String feasible, String ranking) {

	this.issueType = issueType;
	this.solution = solution;
	this.prediction = prediction;
	this.feasible = feasible;
	this.ranking = ranking;
    }

}
