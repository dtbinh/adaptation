package allow.simulator.entity.knowledge;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import allow.simulator.core.EvoKnowledgeConfiguration;
import allow.simulator.entity.Entity;
import allow.simulator.entity.Person;
import allow.simulator.entity.PublicTransportation;
import allow.simulator.mobility.data.TType;
import allow.simulator.mobility.planner.Itinerary;
import allow.simulator.mobility.planner.Leg;
import allow.simulator.util.Pair;
import allow.simulator.world.Street;
import allow.simulator.world.StreetSegment;
import allow.simulator.world.Weather;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * Represents the knowledge of entities and implements functionalities of
 * Allow Ensembles Evolutionary Knowledge.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class EvoKnowledge {
	// Writer to log entity specific movement information.
	private static BufferedWriter loggerMovement;
	private static final String MOVEMENT_FORMAT = "LOG_TIMESTAMP,ENTITY,SEGMENT_ID,POSITION_START,POSITION_END,TIME_START,TIME_END,COSTS,TRAVEL_TIME,MODE,N_PEOPLE,WEATHER";
	
	// Writer to log information about entities in busses etc.
	private static BufferedWriter loggerStop;
	private static final String STOP_FORMAT = "LOG_TIMESTAMP,ENTITY,PASSENGERS,STOP_ID,STOP_POSITION,TIME_ARRIVAL,TIME_DEPARTURE,WEATHER";
	
	private static BufferedWriter loggerUtility;
	private static final String UTILITY_FORMAT = "LOG_TIMESTAMP,ENTITY,REQ_ID,REQ_NUMBER,ESTIMATED_UTILITY,ACTUAL_UTILITY";

	private static Queue<Pair<Entity, List<Itinerary>>> predictBuffer;
	private static List<Worker> tasks;
	private static WorkerPool workerPool;
	private static ExecutorService service;
	
	static {
		
		try {
			loggerMovement = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("movement_logs")));
			loggerStop = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("stop_logs")));
			loggerUtility = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("utility_logs")));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void initialize(EvoKnowledgeConfiguration config, String knowledgeModel, String prefix, ExecutorService service) {
		DBConnector.init(config, knowledgeModel, prefix);
		predictBuffer = new LinkedList<Pair<Entity, List<Itinerary>>>();
		tasks = new ArrayList<Worker>();
		workerPool = new WorkerPool(128);
		EvoKnowledge.service = service;
	}
	
	public static void setLoggerDirectory(Path file) throws IOException {
		loggerMovement = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Paths.get(file.toString(), "movement_logs").toFile())));
		loggerMovement.write(MOVEMENT_FORMAT);
		loggerMovement.newLine();
		loggerMovement.flush();
		loggerStop = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Paths.get(file.toString(), "stop_logs").toFile())));
		loggerStop.write(STOP_FORMAT);
		loggerStop.newLine();
		loggerStop.flush();
		loggerUtility = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Paths.get(file.toString(), "utility_logs").toFile())));
		loggerUtility.write(UTILITY_FORMAT);
		loggerUtility.newLine();
		loggerUtility.flush();
	}
	
	// Entity this knowledge instance belongs to.
	@JsonBackReference
	private Entity entity; 
	
	// Buffer to store experiences of entities for learning.
	private List<TravelExperience> travelExperienceBuffer;
	private List<StopExperience> stopExperienceBuffer;
	
	// Buffer holding entities to exchange knowledge with.
	private List<Entity> toExchangeBuffer;
	
	// Chain of handlers to execute knowledge exchange.
	private ExchangeHandler handlerChain;
	
	/**
	 * Constructor.
	 * Creates a new instance of EvoKnowledge associated with an entity.
	 * 
	 * @param entity Entity this instance of EvoKnowledge is associated with.
	 */
	public EvoKnowledge(Entity entity) {
		this.entity = entity;
		toExchangeBuffer = new ArrayList<Entity>();
		
		if (entity instanceof Person) {
			handlerChain = ExchangeHandler.StandardPersonChain;
			
		} else if (entity instanceof PublicTransportation) {
			handlerChain = ExchangeHandler.StandardBusChain;
		}
		travelExperienceBuffer = new ArrayList<TravelExperience>();
		stopExperienceBuffer = new ArrayList<StopExperience>();
	}
	
	/**
	 * Adds a new vector of observations to EvoKnowledge. To learn from a set of
	 * stored experiences call learn() method.
	 * 
	 * @param observation New experience.
	 */
	public void collect(Experience observation) {
		
		if (observation instanceof TravelExperience) {
			travelExperienceBuffer.add((TravelExperience) observation);
		} else if (observation instanceof StopExperience) {
			stopExperienceBuffer.add((StopExperience) observation);
		}
	}
	
	/*private static final double CAR_PREFERENCE_CHANGE_THRESHOLD1 = 600;
	private static final double CAR_PREFERENCE_CHANGE_STEP1 = 0.02;
	private static final double BUS_PREFERENCE_CHANGE_THRESHOLD = 1.0;
	private static final double BUS_PREFERENCE_CHANGE_STEP = 0.06;*/
	
	private static final double CAR_PREFERENCE_CHANGE_THRESHOLD1 = 600;
	private static final double CAR_PREFERENCE_CHANGE_THRESHOLD2 = 1200;
	private static final double CAR_PREFERENCE_CHANGE_THRESHOLD3 = 1800;
	private static final double CAR_PREFERENCE_CHANGE_STEP1 = 0.01;
	private static final double CAR_PREFERENCE_CHANGE_STEP2 = 0.03;
	private static final double CAR_PREFERENCE_CHANGE_STEP3 = 0.06;
	
	private static final double BUS_PREFERENCE_CHANGE_THRESHOLD1 = 0.7;
	private static final double BUS_PREFERENCE_CHANGE_THRESHOLD2 = 0.9;
	private static final double BUS_PREFERENCE_CHANGE_THRESHOLD3 = 1.0;
	private static final double BUS_PREFERENCE_CHANGE_STEP1 = 0.03;
	private static final double BUS_PREFERENCE_CHANGE_STEP2 = 0.05;
	private static final double BUS_PREFERENCE_CHANGE_STEP3 = 0.06;
	
	/**
	 * Update EvoKnowledge from the collected observations.
	 */
	public boolean learn() {
		// long currentTime = System.currentTimeMillis();

		// Handle statistics learning.
		if (entity instanceof Person) {
			Person p = (Person) entity;
			Itinerary it = p.getCurrentItinerary();
			DBConnector.addEntry(entity, it.priorSegmentation, travelExperienceBuffer);
			
			/*if (it.priorSegmentation.size() != travelExperienceBuffer.size()) {
				for (TravelExperience e : it.priorSegmentation) {
					if (!e.isTransient()) System.out.print(e.getSegmentId() + " ");
				}
				System.out.println();
				for (TravelExperience e : travelExperienceBuffer) {
					if (!e.isTransient()) System.out.print(e.getSegmentId() + " ");
				}
				System.out.println(it.priorSegmentation.size() + " " + travelExperienceBuffer.size());
			}*/
			ExperienceSummary summary = createSummary(p, it, travelExperienceBuffer);
			double estimatedTravelTime = it.duration + it.initialWaitingTime; // - p.getCurrentItinerary().waitingTime;

			switch (it.itineraryType) {
			
				case 0:
					double actualCarTravelTime = summary.travelTime + it.initialWaitingTime;
					p.getContext().getStatistics().reportPriorAndPosteriorCarTravelTimes(estimatedTravelTime, actualCarTravelTime);
					p.getContext().getStatistics().reportPriorAndPosteriorUtilityCar(it.utility, summary.utility);
					double carPreference = p.getPreferences().getCarPreference();
					double delay = actualCarTravelTime - estimatedTravelTime;

					if (delay > CAR_PREFERENCE_CHANGE_THRESHOLD3) {
						p.getPreferences().setCarPreference(carPreference - delay * CAR_PREFERENCE_CHANGE_STEP3 / CAR_PREFERENCE_CHANGE_THRESHOLD3);
					
					} else if (delay > CAR_PREFERENCE_CHANGE_THRESHOLD2) {
						p.getPreferences().setCarPreference(carPreference - CAR_PREFERENCE_CHANGE_STEP2 / CAR_PREFERENCE_CHANGE_THRESHOLD2);
					
					} else if (delay > CAR_PREFERENCE_CHANGE_THRESHOLD1) {
						p.getPreferences().setCarPreference(carPreference - CAR_PREFERENCE_CHANGE_STEP1 / CAR_PREFERENCE_CHANGE_THRESHOLD1);

					}
					
					/*if (delay > CAR_PREFERENCE_CHANGE_THRESHOLD1) {
						p.getPreferences().setCarPreference(carPreference - delay * CAR_PREFERENCE_CHANGE_STEP1 / CAR_PREFERENCE_CHANGE_THRESHOLD1);
					}*/
					// Reduce experienced bus filling level
					double prevFillingLevel = p.getPreferences().getLastExperiencedBusFillingLevel();
					p.getPreferences().setLastExperiencedBusFillingLevel(Math.max(prevFillingLevel - 0.2, 0));
					
					/*if (it.isTaxiItinerary) {
						p.getContext().getStatistics().reportTaxiJourney();
						
					} else {
						p.getContext().getStatistics().reportCarJourney();
					}*/
					break;
				
				case 1:
					double actualBusTravelTime = summary.travelTime + it.initialWaitingTime;
					p.getContext().getStatistics().reportPriorAndPosteriorTransitTravelTimes(estimatedTravelTime, actualBusTravelTime);
					p.getContext().getStatistics().reportPriorAndPosteriorUtilityBus(p.getCurrentItinerary().utility, summary.utility);

					double fillingLevel = getBusFillingLevel(travelExperienceBuffer);
					p.getContext().getStatistics().reportBusFillingLevel(fillingLevel);
					p.getPreferences().setLastExperiencedBusFillingLevel(fillingLevel);
					double busPreference = p.getPreferences().getBusPreference();
					
					if (fillingLevel >= BUS_PREFERENCE_CHANGE_THRESHOLD3) {
						p.getPreferences().setBusPreference(busPreference - fillingLevel * BUS_PREFERENCE_CHANGE_STEP3 / BUS_PREFERENCE_CHANGE_THRESHOLD3);
						
					} else if (fillingLevel >= BUS_PREFERENCE_CHANGE_THRESHOLD2) {
						p.getPreferences().setBusPreference(busPreference - BUS_PREFERENCE_CHANGE_STEP2 / BUS_PREFERENCE_CHANGE_THRESHOLD2);
						
					} else if (fillingLevel >= BUS_PREFERENCE_CHANGE_THRESHOLD1) {
						p.getPreferences().setBusPreference(busPreference - BUS_PREFERENCE_CHANGE_STEP1 / BUS_PREFERENCE_CHANGE_THRESHOLD1);
					}
					/*if (fillingLevel > BUS_PREFERENCE_CHANGE_THRESHOLD1) {
						p.getPreferences().setBusPreference(busPreference - fillingLevel * BUS_PREFERENCE_CHANGE_STEP1);
					}*/
					//p.getContext().getStatistics().reportTransitJourney();

					break;
					
				case 2:
					double actualBikeTravelTime = summary.travelTime;
					p.getContext().getStatistics().reportPriorAndPosteriorBikeTravelTimes(estimatedTravelTime, actualBikeTravelTime);
					
					// Reduce experienced bus filling level
					double prevFillingLevel2 = p.getPreferences().getLastExperiencedBusFillingLevel();
					p.getPreferences().setLastExperiencedBusFillingLevel(Math.max(prevFillingLevel2 - 0.2, 0));
					// p.getContext().getStatistics().reportBikeJourney();

					break;
					
				case 3:
					double actualWalkTravelTime = summary.travelTime;
					p.getContext().getStatistics().reportPriorAndPosteriorWalkTravelTimes(estimatedTravelTime, actualWalkTravelTime);
					
					// Reduce experienced bus filling level
					double prevFillingLevel3 = p.getPreferences().getLastExperiencedBusFillingLevel();
					p.getPreferences().setLastExperiencedBusFillingLevel(Math.max(prevFillingLevel3 - 0.2, 0));
					// p.getContext().getStatistics().reportWalkJourney();

					break;
					
				default:
					System.out.print("Unclassified journey: ");
						for (int i = 0; i < p.getCurrentItinerary().legs.size(); i++) {
							System.out.print(p.getCurrentItinerary().legs.get(i).mode + " ");
						}
						System.out.println();
			}

			// Compute and log posterior utility.
			/*summary.utility = p.getUtility().computeUtility(p.getPreferences(),
					summary.travelTime,
					summary.costs,
					summary.walkingDistance,
					summary.numberOfLegs);
			
			try {
				logUtility(currentTime, p, p.getCurrentItinerary(), summary);
				
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			p.setCurrentItinerary(null);*/
		}

		// Logging.
		/*try {

			for (TravelExperience experience : travelExperienceBuffer) {
				logMovement(currentTime, entity, experience);
			}
			
			for (StopExperience experience : stopExperienceBuffer) {
				logStopInformation(currentTime, entity, (StopExperience) experience);
			}
				
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		clear();
		return true;
	}
	
	/*public boolean learn() {
		long currentTime = System.currentTimeMillis();

		// Handle statistics learning.
		if (entity instanceof Person) {
			Person p = (Person) entity;
			ExperienceSummary summary = createSummary(p.getCurrentItinerary(), experienceBuffer);
			double estimatedTravelTime = p.getCurrentItinerary().duration - p.getCurrentItinerary().waitingTime;

			switch (p.getCurrentItinerary().itineraryType) {
			
				case 0:
					double actualCarTravelTime = summary.travelTime;
					p.getContext().getStatistics().reportPriorAndPosteriorCarTravelTimes(estimatedTravelTime, actualCarTravelTime);
					double carPreference = p.getPreferences().getCarPreference();
					double delay = actualCarTravelTime - estimatedTravelTime;
					
					if (delay > CAR_PREFERENCE_CHANGE_THRESHOLD) {
						p.getPreferences().setCarPreference(carPreference - CAR_PREFERENCE_CHANGE_STEP delay * CHANGE_PER_SECOND);
					}
					break;
				
				case 1:
					double actualBusTravelTime = summary.travelTime;
					p.getContext().getStatistics().reportPriorAndPosteriorTransitTravelTimes(estimatedTravelTime, actualBusTravelTime);
					double fillingLevel = getMaxBusFillingLevel(experienceBuffer);
					p.getContext().getStatistics().reportBusFillingLevel(fillingLevel);
					double busPreference = p.getPreferences().getBusPreference();
					
					if (fillingLevel > BUS_PREFERENCE_CHANGE_THRESHOLD) {
						p.getPreferences().setBusPreference(busPreference - BUS_PREFERENCE_CHANGE_STEP);

					}
					break;
					
				case 2:
					double actualBikeTravelTime = summary.travelTime;
					p.getContext().getStatistics().reportPriorAndPosteriorBikeTravelTimes(estimatedTravelTime, actualBikeTravelTime);
					break;
					
				case 3:
					double actualWalkTravelTime = summary.travelTime;
					p.getContext().getStatistics().reportPriorAndPosteriorWalkTravelTimes(estimatedTravelTime, actualWalkTravelTime);
					break;
					
				default:
					System.out.print("Unclassified journey: ");
						for (int i = 0; i < p.getCurrentItinerary().legs.size(); i++) {
							System.out.print(p.getCurrentItinerary().legs.get(i).mode + " ");
						}
						System.out.println();
				
			}
			
			// Compute and log posterior utility.
			summary.utility = p.getUtility().computeUtility(p.getPreferences(),
					summary.travelTime,
					summary.costs,
					summary.walkingDistance,
					summary.numberOfLegs);
			
			try {
				logUtility(currentTime, p, p.getCurrentItinerary(), summary);
				
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			p.setCurrentItinerary(null);
			return true;
		}

		// Logging.
		/*try {

			for (Experience experience : experienceBuffer) {
				switch (experience.getType()) {
				
				case TRAVEL:
					logMovement(currentTime, entity, (TravelExperience) experience);
					break;
					
				case STOP:
					logStopInformation(currentTime, entity, (StopExperience) experience);
					break;
					
				default:
					throw new IllegalArgumentException("Error: Unknown experience type " + experience.getType());
				}
			}
				
		} catch (IOException e) {
			e.printStackTrace();
		}
		experienceBuffer.clear();
		return true;
	}*/
	
	private static ExperienceSummary createSummary(Entity e, Itinerary itinerary, List<TravelExperience> experiences) {
		ExperienceSummary summary = new ExperienceSummary();
		summary.transfers = itinerary.transfers;
		
		switch (itinerary.itineraryType) {
		
		case 0:
			summary.type = 0;
			summary.travelTime = (long) summarizeTravelTime(experiences);
			summary.costs = summarizeCosts(experiences);
			summary.numberOfLegs = itinerary.legs.size();
			summary.walkingDistance = 0.0;
			break;
		
		case 1:
			summary.type = 1;
			summary.travelTime = (long) summarizeTravelTime(experiences);
			summary.costs = 1.2;
			summary.numberOfLegs = itinerary.legs.size();
			summary.walkingDistance = summarizeWalkingDistance(experiences);
			summary.maxBusFillingLevel = getBusFillingLevel(experiences);
			break;
			
		case 2:
			summary.type = 2;
			summary.travelTime = (long) summarizeTravelTime(experiences);
			summary.costs = summarizeCosts(experiences);
			summary.numberOfLegs = itinerary.legs.size();
			summary.walkingDistance = summarizeWalkingDistance(experiences);
			break;
			
		case 3:
			summary.type = 3;
			summary.travelTime = (long) summarizeTravelTime(experiences);
			summary.costs = 0;
			summary.numberOfLegs = itinerary.legs.size();
			summary.walkingDistance = summarizeWalkingDistance(experiences);
			break;
			
		default:
			throw new IllegalArgumentException("Illegal journey type.");
		}
		summary.utility = e.getUtility().computeUtility(summary.travelTime, summary.costs,
				summary.walkingDistance, summary.maxBusFillingLevel, summary.transfers, e.getPreferences());
		return summary;
		
	}
	
	private static double summarizeTravelTime(List<TravelExperience> experiences) {
		double actualTravelTime = 0.0;
		int experiencesSize = experiences.size();
		
		for (int i = 0; i < experiencesSize; i++) {
			actualTravelTime += experiences.get(i).getTravelTime();
		}
		return actualTravelTime;
	}
	
	private static double summarizeWalkingDistance(List<TravelExperience> experiences) {
		double walkingDistance = 0.0;
		int experiencesSize = experiences.size();
		
		for (int i = 0; i < experiencesSize; i++) {
			TravelExperience ex = experiences.get(i);
			
			if (ex.getTransportationMean() == TType.WALK) {
				walkingDistance += ex.getSegmentLength();
			}
		}
		return walkingDistance;
	}
	
	private static double summarizeCosts(List<TravelExperience> experiences) {
		double actualCosts = 0.0;
		int experiencesSize = experiences.size();
		
		for (int i = 0; i < experiencesSize; i++) {
			actualCosts += experiences.get(i).getCosts();
		}
		return actualCosts;
	}
	
	private static double getBusFillingLevel(List<TravelExperience> experiences) {
		double max = 0.0;
		int experiencesSize = experiences.size();
		
		for (int i = 0; i < experiencesSize; i++) {
			TravelExperience ex = (TravelExperience) experiences.get(i);
			
			if (ex.getTransportationMean() == TType.BUS) {
				max = Math.max(max, ex.getPublicTransportationFillingLevel());
			}
		}
		return max;
	}
	
	/*private static synchronized void logMovement(long timestamp, Entity entity, TravelExperience ex) throws IOException {
		loggerMovement.write(timestamp + "," + entity.toString() + ",");
		loggerMovement.write(ex.getSegmentId() + ",");
		loggerMovement.write(ex.getStartPosition() + ",");
		loggerMovement.write(ex.getEndPosition() + ",");
		loggerMovement.write(ex.getStartingTime() + ",");
		loggerMovement.write(ex.getEndTime() + ",");
		loggerMovement.write(ex.getCosts() + ",");
		loggerMovement.write(ex.getTravelTime() + ",");
		loggerMovement.write(ex.getTransportationMean() + ",");
		loggerMovement.write(ex.getNumberOfPeopleOnSegment() + ",");
		loggerMovement.write(ex.getWeather() + "");
		loggerMovement.newLine();
		loggerMovement.flush();
	}
	
	private static synchronized void logStopInformation(long timestamp, Entity entity, StopExperience ex) throws IOException {
		loggerStop.write(timestamp + "," + entity.toString() + ",");
		loggerStop.write(ex.getStopId() + ",");
		loggerStop.write(ex.getStopPosition() + ",");
		loggerStop.write(ex.getPassengers() + ",");
		loggerStop.write(ex.getTimeArrival() + ",");
		loggerStop.write(ex.getTimeDeparture() + ",");
		loggerStop.write(ex.getWeather() + "");
		loggerStop.newLine();
		loggerStop.flush();
	}
	
	private static synchronized void logUtility(long timestamp, Entity entity, Itinerary it, ExperienceSummary sum) throws IOException {
		loggerUtility.write(timestamp + "," + entity.getId() + ",");
		loggerUtility.write(it.reqId + "," + it.reqNumber + ",");
		loggerUtility.write(it.utility + ",");
		loggerUtility.write(sum.utility + "");
		loggerUtility.newLine();
		loggerUtility.flush();
	}*/
	
	/**
	 * Predicts the values of a set of parameters given a set of observations.
	 * 
	 * @param fromPlanner Itinerary as returned by the planner.
	 * @return Itinerary updated by EvoKnowledge.
	 */
	public void predict(List<Itinerary> fromPlanner) {
		predictBuffer.add(new Pair<Entity, List<Itinerary>>(entity, fromPlanner));
	}
	
	public static void cleanModel() {
		DBConnector.cleanModel(null);
	}
	
	public static void invokeRequest() {
		if (predictBuffer.size() == 0) {
			return;
		}
		
		CountDownLatch latch = new CountDownLatch(predictBuffer.size());
		
		while (predictBuffer.size() > 0) {
			Pair<Entity, List<Itinerary>> request = predictBuffer.poll();
			Worker w = workerPool.pop();
			w.prepare(request.first, request.second, latch);
			tasks.add(w);
		}
		
		try {
			service.invokeAll(tasks);
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		for (int j = 0; j < tasks.size(); j++) {
			Worker w = tasks.get(j);
			w.reset();
			workerPool.put(w);
		}
		tasks.clear();
	}
	
	public static List<TravelExperience> itineraryToTravelExperience(Entity e, Itinerary it) {
		List<TravelExperience> ret = new ArrayList<TravelExperience>();
		Weather.State currentWeather = e.getContext().getWeather().getCurrentState();
		//System.out.println(it.legs.size());
		
		for (Leg l : it.legs) {
			//System.out.println("  " + l.mode + " " + l.startTime + " " + l.endTime + " " + l.segments.size());
			long tStart = l.startTime;
			long tEnd = 0;
			
			if (l.streets.size() == 0) {
				double v = (l.mode == TType.WALK) 
						? StreetSegment.WALKING_SPEED 
								: ((l.mode == TType.BICYCLE) ? StreetSegment.CYCLING_SPEED : StreetSegment.DEFAULT_DRIVING_SPEED);
				tEnd = (long) (tStart + l.distance / v);
				ret.add(new TravelExperience(tEnd - tStart, 0.0, l.mode, tStart, tEnd, -1, -1, null, currentWeather));
				continue;
			}
			
			for (Street street : l.streets) {
				
				//for (StreetSegment s : street.getSubSegments()) {
					double v = (l.mode == TType.WALK) ? street.getSubSegments().get(0).getWalkingSpeed() 
							: ((l.mode == TType.BICYCLE) ? street.getSubSegments().get(0).getCyclingSpeed() : street.getSubSegments().get(0).getMaxSpeed());
					double travelTime = street.getLength() / v;
					double costs = l.costs * (street.getLength() / l.distance);
					tEnd = (long) (tStart + travelTime * 1000);
					TravelExperience t  = new TravelExperience(street, travelTime, costs, l.mode, tStart, tEnd, -1, -1, l.tripId, currentWeather);
					ret.add(t);
					//System.out.println(t.getSegmentId() + " " + t.getStartingTime() + " " + t.getEndTime() + " " + travelTime);
					tStart = tEnd;
				//}
			}
		}
		return ret;
	}
	
	public static void updateItineraryFromTravelExperience(Itinerary it, List<TravelExperience> ex) {
		if (ex.size() == 0) {
			return;
		}
		it.waitingTime = 0;
		int exIndex = 0;
		long legStartTime = 0;
		long legEndTime = 0;
		boolean first = true;
	
		for (Leg l : it.legs) {
			
			if (first || l.mode == TType.BUS || l.mode == TType.CABLE_CAR) {
				legStartTime = l.startTime;
				legEndTime = legStartTime;

			} else {
				legStartTime = legEndTime;
			}
			
			int added = 0;
			for (int i = exIndex; i < ex.size(); i++) {
				TravelExperience e = ex.get(i);
				
				if (e.isTransient())
					break;
				
				// In case transportation means changes.
				if ((e.getTransportationMean() != l.mode)) {
					break;
				}
				
				// In case these is an intermediate bus change.
				if ((l.mode == TType.BUS || l.mode == TType.CABLE_CAR) && !l.tripId.equals(e.getPublicTransportationTripId())) {
					break;
				}
				double duration = e.getTravelTime() * 1000;
				it.maxFillingLevel = Math.max(e.getPublicTransportationFillingLevel(), it.maxFillingLevel);
				legEndTime += duration;
				added++;
			}
			exIndex += added;
			
			if (added == 0) {
				legEndTime += ex.get(exIndex++).getTravelTime() * 1000;
			}
			first = false;
			l.startTime = legStartTime;
			l.endTime = legEndTime;
		}
		
		// Update itinerary time.
		it.startTime = it.legs.get(0).startTime;
		it.endTime = it.startTime;
		
		for (Leg l : it.legs) {
			long duration = (l.endTime - l.startTime);
			it.endTime += duration;
		}
		it.duration = ((it.endTime - it.startTime) / 1000);
		// Compute waiting time.
		for (int i = 0; i < it.legs.size() - 1; i++) {
			it.waitingTime += ((it.legs.get(i + 1).startTime - it.legs.get(i).endTime) / 1000);
		}
		it.duration += (it.waitingTime / 1000);
	}
	
	public boolean exchangeKnowledge(Entity other) {
		return DBConnector.exchangeKnowledge(entity, other);
	}
	
	/**
	 * Executes knowledge exchange based on relations of the associated entity.
	 */
	public void exchangeKnowledge() {
		// Get relations.
		entity.getRelations().updateRelations(toExchangeBuffer);
		// Execute knowledge exchange.
		if (toExchangeBuffer.size() > 0) {

			for (Entity other : toExchangeBuffer) {
				handlerChain.exchange(entity, other);
				entity.getRelations().addToBlackList(other);
			}
		}
		// Clear relations buffer.
		toExchangeBuffer.clear();
	}
	
	public void clear() {
		travelExperienceBuffer.clear();
		stopExperienceBuffer.clear();
	}
}
