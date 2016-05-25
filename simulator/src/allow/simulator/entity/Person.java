package allow.simulator.entity;

import java.time.LocalTime;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import allow.simulator.core.Context;
import allow.simulator.entity.utility.IUtility;
import allow.simulator.entity.utility.Preferences;
import allow.simulator.entity.utility.UtilityWithoutPreferences;
import allow.simulator.flow.activity.Activity;
import allow.simulator.mobility.planner.Itinerary;
import allow.simulator.mobility.planner.RequestBuffer;
import allow.simulator.util.Coordinate;
import allow.simulator.util.Pair;
import allow.simulator.world.layer.Area;
import allow.simulator.world.layer.Layer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a person entity performing journeys within the simulated world
 * using car, bike, and the public transportation network.
 * 
 * Persons follow a certain profile (e.g. student, worker, child,...) determining
 * their behaviour in more detail.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public final class Person extends Entity {

	/**
	 * Identifies a profile which suggest a person's behaviour/daily routine
	 * in the simulation. Workers for example may go to work in the morning and
	 * back in the evening. 
	 * 
	 * @author Andreas Poxrucker (DFKI)
	 *
	 */
	public enum Profile {
		
		/**
		 * Students have a schedule from Monday to Friday arriving randomly at
		 * university at full hour from 8 am to noon and going back randomly
		 * from noon to 8 pm at full hour.
		 */
		STUDENT("Student"),
		
		/**
		 * Workers arrive at work every morning between 5 am and 9 am and go in
		 * the afternoon/evening (eight hours later).
		 */
		WORKER("Worker"),
		
		/**
		 * Homemakers perform random journeys focusing on shopping areas during
		 * the morning and afternoon and purely random journeys during the whole
		 * day.
		 */
		HOMEMAKER("Homemaker"),
		
		/**
		 * Children go to school arriving at 8 am in the morning and back at
		 * 1 pm.
		 */
		CHILD("Child"),
		
		/**
		 * Persons with the random profile perform random journeys during the 
		 * whole day.
		 */
		RANDOM("Random");
		
		// String describing the role for output.
		private String prettyPrint;
		
		private Profile(String name) {
			prettyPrint = name;
		}
		
		/**
		 * Returns a String description of the role for output.
		 * 
		 * @return String description of role for output.
		 */
		public String toString() {
			return prettyPrint;
		}	
	}
	
	/**
	 * Identifies the gender of a person.
	 * 
	 * @author Andreas Poxrucker (DFKI)
	 *
	 */
	public enum Gender {
		
		FEMALE,

		MALE
		
	}
	
	// Gender of a person.
	private Gender gender;
	
	// Profile suggesting a person's behaviour.
	private Profile profile;
	
	// Location a person lives at.
	private Coordinate home;
	
	// True if person has a car, false otherwise.
	private boolean hasCar;
	
	// True, if person has a bike, false otherwise.
	private boolean hasBike;
	
	// True, if person will send requests to the FlexiBus planner, false otherwise.
	private boolean useFlexiBus;
	
	// Request buffer for more efficient journey planning.
	private RequestBuffer requestBuffer;
	
	// Daily routine of this person, i.e. set of travelling events which are
	// executed regularly on specific days, e.g. going to work on back from 
	// Mo to Fri.
	private DailyRoutine dailyRoutine;
	
	// Schedule containing starting times of activities.
	@JsonIgnore
	private Queue<Pair<LocalTime, Activity>> schedule;
	
	// Current destination of a person.
	@JsonIgnore
	private Itinerary currentItinerary;
	
	// Determines if person is replanning.
	@JsonIgnore
	private boolean isReplanning;
	
	// Indicates whether a person used her car during the current travelling
	// cycle which forbids replanning a journey with own car.
	private boolean usedCar;
	
	// Name of home area
	private String homeAreaName;
	
	/**
	 * Constructor.
	 * Creates new instance of a person.
	 * 
	 * @param id Id of this person.
	 * @param gender Gender of this person.
	 * @param profile Profile of this person.
	 * @param utility Utility function of this person.
	 * @param homeLocation Location on the map that is defined to be the home
	 *        of the person.
	 * @param hasCar Determines if this person has a car for travelling.
	 * @param hasBike Determines if this person has a bike for travelling.
	 * @param useFlexiBus Determines if this person uses FlexiBus for travelling.
	 * @param dailyRoutine Daily routine of this person, e.g. going to work in
	 *        the morning and back in the afternoon for workers.
	 * @param context Context of this person.
	 */
	public Person(long id,
			Gender gender,
			Profile profile,
			IUtility utility,
			Preferences prefs,
			Coordinate homeLocation,
			boolean hasCar,
			boolean hasBike,
			boolean useFlexiBus,
			DailyRoutine dailyRoutine,
			Context context) {
		super(id, Type.PERSON, utility, prefs, context);
		this.gender = gender;
		this.profile = profile;
		this.hasCar = hasCar;
		this.hasBike = hasBike;
		this.useFlexiBus = useFlexiBus;
		this.dailyRoutine = dailyRoutine;
		home = homeLocation;
		setPosition(homeLocation);
		schedule = new ArrayDeque<Pair<LocalTime, Activity>>();
		requestBuffer = new RequestBuffer();
		currentItinerary = null;
		usedCar = false;
		isReplanning = false;
	}
	
	/**
	 * Constructor.
	 * Creates new instance of a person.
	 * 
	 * @param id Id of this person.
	 * @param gender Gender of this person.
	 * @param role Role of this person.
	 * @param utility Utility function of this person.
	 * @param homeLocation Location on the map that is defined to be the home
	 *        of the person.
	 * @param hasCar Determines if this person has a car for travelling.
	 * @param hasBike Determines if this person has a bike for travelling.
	 * @param willUseFelxiBus Determines if this person uses FlexiBus for travelling.
	 * @param dailyRoutine Daily routine of this person, e.g. going to work in
	 *        the morning and back in the afternoon for workers.
	 */
	@JsonCreator
	public Person(@JsonProperty("id") long id,
			@JsonProperty("gender") Gender gender,
			@JsonProperty("role") Profile role,
			@JsonProperty("utility") UtilityWithoutPreferences utility,
			@JsonProperty("preferences") Preferences prefs,
			@JsonProperty("home") Coordinate homeLocation,
			@JsonProperty("hasCar") boolean hasCar,
			@JsonProperty("hasBike") boolean hasBike,
			@JsonProperty("useFlexiBus") boolean useFlexiBus,
			@JsonProperty("dailyRoutine") DailyRoutine dailyRoutine) {
		super(id, Type.PERSON, utility, prefs);
		this.gender = gender;
		this.profile = role;
		this.hasCar = hasCar;
		this.hasBike = hasBike;
		this.useFlexiBus = useFlexiBus;
		this.dailyRoutine = dailyRoutine;
		home = homeLocation;
		setPosition(homeLocation);
		schedule = new ArrayDeque<Pair<LocalTime, Activity>>();
		requestBuffer = new RequestBuffer();
		currentItinerary = null;
		usedCar = false;
		isReplanning = false;
	}
	
	/**
	 * Specifies the context the entity is used in.
	 * 
	 * @param context Context the entity is used in.
	 */
	@Override
	public void setContext(Context context) {
		super.setContext(context);
		Layer districts = context.getWorld().getStreetMap().getLayer(Layer.Type.DISTRICTS);
		List<Area> areas = districts.getAreasContainingPoint(home);
		Area temp = null;
		
		for (int i = 0; i< areas.size(); i++) {
			temp = areas.get(i);
			
			if (!temp.getName().equals("default")) {
				homeAreaName = temp.getName().replace(" ", "");
				break;
			}
		}
		
		if (homeAreaName == null) {
			homeAreaName = "default";
		}
	}
	
	/**
	 * Returns the gender of the person.
	 * 
	 * @return Gender of the person.
	 */
	public Gender getGender() {
		return gender;
	}
	
	/**
	 * Returns the role of the person in the simulation determining its
	 * behaviour by following a certain daily routine.
	 * 
	 * @return Role of the person in the simulation.
	 */
	public Profile getProfile() {
		return profile;
	}
	
	/**
	 * Returns the location (coordinates) that is defined to be the home of a
	 * person entity.
	 * 
	 * @return Coordinates of home location of a person.
	 */
	public Coordinate getHome() {
		return home;
	}
	
	public String getHomeArea() {
		return homeAreaName;
	}
	
	/**
	 * Sets the current itinerary to be executed by this person. 
	 * 
	 * @param itinerary Current itinerary to be executed.
	 */
	public void setCurrentItinerary(Itinerary itinerary) {
		currentItinerary = itinerary;
	}
	
	/**
	 * Returns the current itinerary to be executed by this person.
	 * 
	 * @return Current itinerary to be executed or null in case there is no
	 *         itinerary to execute.
	 */
	@JsonIgnore
	public Itinerary getCurrentItinerary() {
		return currentItinerary;
	}
	
	/**
	 * Returns true, if this person has a car for travelling, false otherwise.
	 * 
	 * @return True, if person has a car for travelling, false otherwise.
	 */
	public boolean hasCar() {
		return hasCar;
	}
	
	/**
	 * Returns true, if this person has a bike for travelling, false otherwise.
	 * 
	 * @return True, if person has a bike for travelling, false otherwise.
	 */
	public boolean hasBike() {
		return hasBike;
	}
	
	/**
	 * Returns true, if this person uses FlexiBus for travelling.
	 * 
	 * @return True, if person uses FlexiBus for travelling, false otherwise.
	 */
	public boolean useFlexiBus() {
		return useFlexiBus;
	}
	
	/**
	 * Determine whether this person uses FlexiBus for travelling.
	 * 
	 * @param useFlexiBus True, if person should use FlexiBus, false otherwise.
	 */
	public void setUseFlexiBus(boolean useFlexiBus) {
		this.useFlexiBus = useFlexiBus;
	}
	
	/**
	 * Returns true, if this person has used her car for travelling, false otherwise.
	 * 
	 * @return True, if person has used her car for travelling, false otherwise.
	 */
	@JsonIgnore
	public boolean hasUsedCar() {
		return usedCar;
	}
	
	/**
	 * Determine whether this person has used her car for travelling.
	 * 
	 * @param usedCar True, if person has used her car for travelling.
	 */
	public void setUsedCar(boolean usedCar) {
		this.usedCar = usedCar;
	}
	
	/**
	 * Returns true, if this person is replanning, false otherwise.
	 * 
	 * @return True, if person is replanning, false otherwise.
	 */
	@JsonIgnore
	public boolean isReplanning() {
		return isReplanning;
	}
	
	/**
	 * Determine whether this person is currently replanning a journey.
	 * 
	 * @param usedCar True, if person is currently replanning a journey.
	 */
	public void setReplanning(boolean isReplanning) {
		this.isReplanning = isReplanning;
	}
	
	/**
	 * Returns the daily routine of this person, i.e. set of travelling events
	 * which are executed regularly on specific days, e.g. going to work on back
	 * from Mo to Fri.
	 * 
	 * @return Daily routine of this person.
	 */
	public DailyRoutine getDailyRoutine() {
		return dailyRoutine;
	}
	
	/**
	 * Specifies the daily routine of the person.
	 * 
	 * @param dailyRoutine Daily routine this person should have.
	 */
	public void setDailyRoutine(DailyRoutine dailyRoutine) {
		this.dailyRoutine = dailyRoutine;
	}
	
	/**
	 * Returns the scheduling queue of the person defining the points in time
	 * when a person should become active and which activity should be started.
	 * 
	 * @return Scheduling queue of the person.
	 */
	@JsonIgnore
	public Queue<Pair<LocalTime, Activity>> getScheduleQueue() {
		return schedule;
	}
	
	/**
	 * Returns true if person is currently at home and false otherwise.
	 * 
	 * @return True if person is at home, false otherwise.
	 */
	@JsonIgnore
	public boolean isAtHome() {
		return home.equals(position);
	}
	
	@JsonIgnore
	public RequestBuffer getRequestBuffer() {
		return requestBuffer;
	}
	
	public String toString() {
		return "[" + profile.prettyPrint + id + "]";
	}

	@Override
	public boolean isActive() {
		return true;
	}
}
