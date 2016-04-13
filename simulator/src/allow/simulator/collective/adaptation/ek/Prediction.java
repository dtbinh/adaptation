package allow.simulator.collective.adaptation.ek;

public class Prediction {

    int solverType;
    double estimatedSuccessRate;
    int accuracy;

    public Prediction(int solverType, double estimatedSuccessRate, int accuracy) {
	this.solverType = solverType;
	this.estimatedSuccessRate = estimatedSuccessRate;
	this.accuracy = accuracy;
    }

    public int getSolverType() {
	return solverType;
    }

    public double getEstimatedSuccessrate() {
	return estimatedSuccessRate;
    }

    public int getAccuracy() {
	return accuracy;
    }

    public String toString() {
	return "[solverType=" + solverType + ",utility=" + estimatedSuccessRate + ",accuracy=" + accuracy + "]";
    }
}
