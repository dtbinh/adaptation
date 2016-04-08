package allow.simulator.statistics;

import java.util.Collection;

import allow.simulator.core.Context;
import allow.simulator.entity.Entity;
import allow.simulator.entity.utility.Preferences;

public class Statistics {
	// Sliding windows covering mean prior and posterior travel time for
	// different means of transportation.
	private SlidingWindow priorCarTravelTime;
	private SlidingWindow posteriorCarTravelTime;
	private SlidingWindow priorBusTravelTime;
	private SlidingWindow posteriorBusTravelTime;
	private SlidingWindow priorBikeTravelTime;
	private SlidingWindow posteriorBikeTravelTime;
	private SlidingWindow priorWalkTravelTime;
	private SlidingWindow posteriorWalkTravelTime;
	private SlidingWindow busFillingLevel;
	private SlidingWindow priorUtilityCar;
	private SlidingWindow posteriorUtilityCar;
	private SlidingWindow priorUtilityBus;
	private SlidingWindow posteriorUtilityBus;
	private SlidingWindow replaningWaitingTime;
	
	private double meanBusPreference;
	private double meanCarPreference;
	
	private long numberOfCarJourneys;
	private long numberOfTransitJourneys;
	private long numberOfBikeJourneys;
	private long numberOfWalkJourneys;
	private long numberOfTaxiJourneys;
	private long numberOfTaxiJourneysPerDay;
	private double carJourneysRatio;
	private double transitJourneysRatio;
	private double bikeJourneysRatio;
	private double walkJourneysRatio;
	private double taxiJourneyRatio;
	private int numberOfCongestedStreets;
	
	public Statistics(int windowSize) {
		priorCarTravelTime = new SlidingWindow(windowSize);
		posteriorCarTravelTime = new SlidingWindow(windowSize);
		
		priorBusTravelTime = new SlidingWindow(windowSize);
		posteriorBusTravelTime = new SlidingWindow(windowSize);
		
		priorBikeTravelTime = new SlidingWindow(windowSize);
		posteriorBikeTravelTime = new SlidingWindow(windowSize);
		
		priorWalkTravelTime = new SlidingWindow(windowSize);
		posteriorWalkTravelTime = new SlidingWindow(windowSize);
		
		busFillingLevel = new SlidingWindow(windowSize);
		
		priorUtilityCar = new SlidingWindow(windowSize);
		posteriorUtilityCar = new SlidingWindow(windowSize);
		
		priorUtilityBus = new SlidingWindow(windowSize);
		posteriorUtilityBus = new SlidingWindow(windowSize);
		
		replaningWaitingTime = new SlidingWindow(windowSize);
		
		meanBusPreference = 0.5;
		meanCarPreference = 0.5;
		numberOfCarJourneys = 0;
		numberOfTransitJourneys = 0;
		numberOfBikeJourneys = 0;
		numberOfWalkJourneys = 0;
		numberOfTaxiJourneys = 0;
		numberOfTaxiJourneysPerDay = 0;
		
		numberOfCongestedStreets = 0;
	}
	
	public void reset() {
		priorCarTravelTime.reset();
		posteriorCarTravelTime.reset();
		
		priorBusTravelTime.reset();
		posteriorBusTravelTime.reset();
		
		priorBikeTravelTime.reset();
		posteriorBikeTravelTime.reset();
		
		priorWalkTravelTime.reset();
		posteriorWalkTravelTime.reset();
		
		busFillingLevel.reset();
		
		priorUtilityCar.reset();
		posteriorUtilityCar.reset();
		
		priorUtilityBus.reset();
		posteriorUtilityBus.reset();
		
		numberOfTaxiJourneysPerDay = 0;
		
		numberOfCongestedStreets = 0;
	}
	
	public double getCarJourneyRatio() {
		return carJourneysRatio;
	}
	
	public double getTransitJourneyRatio() {
		return transitJourneysRatio;
	}
	
	public double getBikeJourneyRatio() {
		return bikeJourneysRatio;
	}
	
	public double getWalkJourneyRatio() {
		return walkJourneysRatio;
	}
	
	public double getTaxiJourneyRatio() {
		return taxiJourneyRatio;
	}
	
	public double getNumberOfTaxiJourneysPerDay() {
		return numberOfTaxiJourneysPerDay;
	}
	
	public double getMeanPriorCarTravelTime() {
		return priorCarTravelTime.getMean();
	}
	
	public double getMeanPosteriorCarTravelTime() {
		return posteriorCarTravelTime.getMean();
	}
	
	public double getMeanPriorBusTravelTime() {
		return priorBusTravelTime.getMean();
	}
	
	public double getMeanPosteriorBusTravelTime() {
		return posteriorBusTravelTime.getMean();
	}
	
	public double getMeanBusPreference() {
		return meanBusPreference;
	}
	
	public double getMeanCarPreference() {
		return meanCarPreference;
	}
	
	public double getMeanBusFillingLevel() {
		return busFillingLevel.getMean();
	}
	
	public double getMeanPriorUtilityCar() {
		return priorUtilityCar.getMean();
	}
	
	public double getMeanPosteriorUtilityCar() {
		return posteriorUtilityCar.getMean();
	}
	
	public double getMeanPriorUtilityBus() {
		return priorUtilityBus.getMean();
	}
	
	public double getMeanPosteriorUtilityBus() {
		return posteriorUtilityBus.getMean();
	}
	
	public double getMeanReplaningWaitingTime() {
		return replaningWaitingTime.getMean();
	}
	
	public int getNumberOfCongestedStreets() {
		return numberOfCongestedStreets;
	}
	
	public synchronized void reportCarJourney() {
		numberOfCarJourneys++;
	}
	
	public synchronized void reportTransitJourney() {
		numberOfTransitJourneys++;
	}
	
	public synchronized void reportBikeJourney() {
		numberOfBikeJourneys++;
	}
	
	public synchronized void reportWalkJourney() {
		numberOfWalkJourneys++;
	}
	
	public synchronized void reportTaxiJourney() {
		numberOfTaxiJourneys++;
		numberOfTaxiJourneysPerDay++;
	}
	
	public synchronized void reportCongestedStreet() {
		numberOfCongestedStreets++;
	}
	
	public void resetCongestedStreets() {
		numberOfCongestedStreets = 0;
	}
	
	public synchronized void reportPriorAndPosteriorCarTravelTimes(double priorToAdd, double posteriorToAdd) {
		priorCarTravelTime.addValue(priorToAdd);
		posteriorCarTravelTime.addValue(posteriorToAdd);
	}
	
	public synchronized void reportPriorAndPosteriorTransitTravelTimes(double priorToAdd, double posteriorToAdd) {
		priorBusTravelTime.addValue(priorToAdd);
		posteriorBusTravelTime.addValue(posteriorToAdd);
	}
	
	public synchronized void reportPriorAndPosteriorBikeTravelTimes(double priorToAdd, double posteriorToAdd) {
		priorBikeTravelTime.addValue(priorToAdd);
		posteriorBikeTravelTime.addValue(posteriorToAdd);
	}
	
	public synchronized void reportPriorAndPosteriorWalkTravelTimes(double priorToAdd, double posteriorToAdd) {
		priorWalkTravelTime.addValue(priorToAdd);
		posteriorWalkTravelTime.addValue(posteriorToAdd);
	}
	
	public synchronized void reportBusFillingLevel(double fillingLevel) {
		busFillingLevel.addValue(fillingLevel);
	}
	
	public synchronized void reportReplaningWaitingTime(double waitingTime) {
		replaningWaitingTime.addValue(waitingTime);
	}
	
	public synchronized void reportPriorAndPosteriorUtilityCar(double priorToAdd, double posteriorToAdd) {
		priorUtilityCar.addValue(priorToAdd);
		posteriorUtilityCar.addValue(posteriorToAdd);
	}
	
	public synchronized void reportPriorAndPosteriorUtilityBus(double priorToAdd, double posteriorToAdd) {
		priorUtilityBus.addValue(priorToAdd);
		posteriorUtilityBus.addValue(posteriorToAdd);
	}
	
	public synchronized void updateGlobalStatistics(Context simulationContext) {
		Collection<Entity> persons = simulationContext.getWorld().getEntitiesOfType(Entity.Type.PERSON);
		updateMeanTransportPreferences(persons);
		updateJourneyChoices();
	}
	
	private void updateMeanTransportPreferences(Collection<Entity> persons) {
		// int busPrefAcc = 0;
		// int carPrefAcc = 0;
		double busPrefAcc = 0.0;
		double carPrefAcc = 0.0;
		
		for (Entity person : persons) {
			Preferences p = person.getPreferences();
			
			/*if (p.getBusPreference() >= p.getCarPreference()) {
				busPrefAcc++;
			} else {
				carPrefAcc++;
			}*/
			busPrefAcc += p.getBusPreference();
			carPrefAcc += p.getCarPreference();
		}
		meanBusPreference = busPrefAcc / persons.size();
		meanCarPreference = carPrefAcc / persons.size();
	}
	
	private void updateJourneyChoices() {
		double totalNumberOfJourneys = numberOfCarJourneys + numberOfTransitJourneys 
				+ numberOfBikeJourneys + numberOfWalkJourneys + numberOfTaxiJourneys;
		
		if (totalNumberOfJourneys == 0) return;
		
		carJourneysRatio = (double) numberOfCarJourneys / totalNumberOfJourneys;
		transitJourneysRatio = (double) numberOfTransitJourneys / totalNumberOfJourneys;
		bikeJourneysRatio = (double) numberOfBikeJourneys / totalNumberOfJourneys;
		walkJourneysRatio = (double) numberOfWalkJourneys / totalNumberOfJourneys;
		taxiJourneyRatio = (double) numberOfTaxiJourneys / totalNumberOfJourneys;	
	}
}
