package allow.simulator.entity.utility;

import java.util.concurrent.ThreadLocalRandom;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author UOC
 *
 */
public class Preferences {
	/*The total sum of the weights should be 1
	  ttweight + cweight + wdweight + ncweight = 1*/
	/*weight for travel time - Value range:[0,1]*/
	private double ttweight;
	
	/*weight for cost - Value range:[0,1]*/
	private double cweight;
	
	/*weight for walking distance - Value range:[0,1]*/
	private double wdweight;
	
	/*weight for number of changes - Value range:[0,1]*/
	private double ncweight;
	
	/*maximum of travel duration - in seconds*/
	private long tmax;
	
	/*maximum of cost*/
	private double cmax;
	
	/*maximum walking distance - in meters*/
	private double wmax;
	
	private double busPreference;
	
	private double carPreference;
	
	@JsonIgnore
	private int nBusPreferenceChanges;
	
	@JsonIgnore
	private int nCarPreferenceChanges; 
	
	@JsonIgnore
	private double lastExperiencedBusFillingLevel;
	
	@JsonIgnore
	private double busPenalty;
	
	@JsonIgnore
	private double carPenalty;
	
	@JsonCreator
	public Preferences(@JsonProperty("ttweight") double ttw, 
			@JsonProperty("cweight") double cw,
			@JsonProperty("wdweight") double wdw,
			@JsonProperty("ncweight") double ncw,
			@JsonProperty("tmax") long tmax,
			@JsonProperty("cmax") double cmax,
			@JsonProperty("wmax") double wmax,
			@JsonProperty("busPreference") double pBus,
			@JsonProperty("carPreference") double pCar){
		ttweight = ttw;
		cweight = cw;
		wdweight = wdw;
		ncweight = ncw;
		this.tmax = tmax;
		this.cmax = cmax;
		this.wmax = wmax;
		busPreference = pBus;
		carPreference = pCar;
		nBusPreferenceChanges = 0;
		nCarPreferenceChanges = 0;
	}
	
	public Preferences() {
		double vec[] = createNormVec();
		ttweight = vec[0];
		cweight = vec[1];
		wdweight = vec[2];
		ncweight = vec[3];
		tmax = 0;
		cmax = 0;
		wmax = 0;
		
		/*if (cweight > ttweight) {
			busPreference = ThreadLocalRandom.current().nextDouble(0.5, 1.0);
			carPreference = 1.0 - busPreference;
		} else {*/
			carPreference = ThreadLocalRandom.current().nextDouble(0, 0.7);
			busPreference = 1.0 - carPreference;
		//}
		nBusPreferenceChanges = 0;
		nCarPreferenceChanges = 0;
	}
	
	private static double[] createNormVec() {
		double v[] = { Math.abs(ThreadLocalRandom.current().nextGaussian()),
				Math.abs(ThreadLocalRandom.current().nextGaussian()),
				Math.abs(ThreadLocalRandom.current().nextGaussian()),
				Math.abs(ThreadLocalRandom.current().nextGaussian()) };
		double normInv = 1.0 / (v[0] + v[1] + v[2] + v[3]);
		
		for (int i = 0; i < v.length; i++) v[i] *= normInv;
		return v;
	}
	
	public double getTTweight() {
		return ttweight;
	}
	
	public double getCweight() {
		return cweight;
	}
	
	public double getWDweight() {
		return wdweight;
	}
	
	public double getNCweight() {
		return ncweight;
	}
	
	public long getTmax() {
		return tmax;
	}
	
	public double getCmax() {
		return cmax;
	}
	
	public double getWmax() {
		return wmax;
	}
	
	public void setTTweight(double weight){
		ttweight = weight;
	}
	
	public void setCweight(double weight){
		cweight = weight;
	}
	
	public void setWDweight(double weight){
		wdweight = weight;
	}
	
	public void setNCweight(double weight){
		ncweight = weight;
	}
	
	public void setTmax(long max){
		tmax = max;
	}
	
	public void setCmax(double max){
		cmax = max;
	}
	
	public void setWmax(double max){
		wmax = max;
	}
	
	public double getBusPreference() {
		return busPreference;
	}
	
	public double getCarPreference() {
		return carPreference;
	}

	@JsonIgnore
	public int getNBusPreferenceChanges() {
		return nBusPreferenceChanges;
	}
	
	@JsonIgnore
	public int getNCarPreferenceChanges() {
		return nCarPreferenceChanges;
	}
	
	public void setBusPreference(double newPreference) {
		busPreference = Math.max(Math.min(1, newPreference), 0);
		carPreference = 1.0 - busPreference;
		nBusPreferenceChanges++;
	}
	
	public void adjustBusPreference(double newPreference) {
		busPreference = Math.max(Math.min(1, newPreference), 0);
		carPreference = 1.0 - busPreference;
	}
	
	public void setCarPreference(double newPreference) {
		carPreference = Math.max(Math.min(1, newPreference), 0);
		busPreference = 1.0 - carPreference;
		nCarPreferenceChanges++;
	}
	
	public void adjustCarPreference(double newPreference) {
		carPreference = Math.max(Math.min(1, newPreference), 0);
		busPreference = 1.0 - carPreference;
	}
	
	public double getLastExperiencedBusFillingLevel() {
		return lastExperiencedBusFillingLevel;
	}
	
	public void setLastExperiencedBusFillingLevel(double level) {
		lastExperiencedBusFillingLevel = level;
	}
	
	public String toString() {
		return "[Preferences: " + ttweight + ", " + cweight + ", " + wdweight + ", " + ncweight + ", " + tmax + ", " + cmax + ", " + wmax + "]"; 
	}
}
