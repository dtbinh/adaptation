package allow.simulator.entity;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import allow.simulator.entity.Person.Gender;
import allow.simulator.entity.Person.Profile;
import allow.simulator.entity.utility.Preferences;
import allow.simulator.entity.utility.UtilityWithoutPreferences;
import allow.simulator.mobility.data.RType;
import allow.simulator.mobility.data.TType;
import allow.simulator.mobility.planner.IPlannerService;
import allow.simulator.mobility.planner.Itinerary;
import allow.simulator.mobility.planner.JourneyRequest;
import allow.simulator.mobility.planner.OnlineJourneyPlanner;
import allow.simulator.util.Coordinate;
import allow.simulator.util.Geometry;
import allow.simulator.world.StreetMap;
import allow.simulator.world.StreetNode;
import allow.simulator.world.layer.Area;
import allow.simulator.world.layer.DistrictLayer;
import allow.simulator.world.layer.DistrictType;
import allow.simulator.world.layer.Layer;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Main {
		
	public static class Worker implements Runnable {
		
		private Person buffer[];
		private int idxStart;
		private int idxEnd;
		private StreetMap map;
		private CountDownLatch latch;
		private IPlannerService service;
		
		public Worker(Person buffer[], int idxStart, int idxEnd, StreetMap map, CountDownLatch latch) {
			this.buffer = buffer;
			this.idxStart = idxStart;
			this.idxEnd = idxEnd;
			this.map = map;
			this.latch = latch;
			service = new OnlineJourneyPlanner("localhost", 8020, Paths.get("temp"));
		}

		@Override
		public void run() {
			
			for (int i = idxStart; i < idxEnd; i++) {
				
				int r = ThreadLocalRandom.current().nextInt(100);
				try {
				if (r < 25) {
					System.out.println(Thread.currentThread().getId() + " generating student.");
					buffer[i] = createStudent(i, map, service);
					
				} else if (r < 40) {
					System.out.println(Thread.currentThread().getId() + " generating homemaker.");
					buffer[i] = createHomemaker(i, map, service);

				} else if (r < 50) {
					System.out.println(Thread.currentThread().getId() + " generating child.");
					buffer[i] = createChild(i, map, service);
					
				} else if (r < 60) {
					System.out.println(Thread.currentThread().getId() + " generating worker.");
					buffer[i] = createWorker(i, map, service);
					
				} else {
					System.out.println(Thread.currentThread().getId() + " generating worker.");
					buffer[i] = createWorker(i, map, service);
				}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			latch.countDown();
		}
	}
	
	public static void main(String args[]) throws IOException {
		// Determine home location.
		StreetMap map = new StreetMap(Paths.get("/Users/Andi/Documents/DFKI/Allow Ensembles/Repository/repos/Software/DFKI Simulator/NetLogo/data/world/trento.world"));
		map.addLayer(Layer.Type.DISTRICTS, Paths.get("/Users/Andi/Documents/DFKI/Allow Ensembles/Repository/repos/Software/DFKI Simulator/NetLogo/data/world/partitioning.layer"));
		
		long t1 = System.nanoTime();
		int numberToGenerate = 5000;
		Person buffer[] = new Person[numberToGenerate];
		
		int nThreads = Math.min(Runtime.getRuntime().availableProcessors(), numberToGenerate);
		ExecutorService service = Executors.newFixedThreadPool(nThreads);
		CountDownLatch latch = new CountDownLatch(nThreads);
		
		int numberPerThread = numberToGenerate / nThreads;

		int currentIdx = 0;
		for (int i = 0; i < nThreads; i++) {
			int endIdx = currentIdx + numberPerThread;
			if (i == nThreads - 1) endIdx = Math.max(endIdx, numberToGenerate - 1);
			Worker worker = new Worker(buffer, currentIdx, endIdx, map, latch);
			currentIdx += numberPerThread;
			service.submit(worker);
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		service.shutdown();
		System.out.println("Ellapsed time: " + (System.nanoTime() - t1));
		
		// Write results.
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		BufferedWriter wr = Files.newBufferedWriter(Paths.get("trento_small.agent"), Charset.defaultCharset());
		
		for (int i = 0; i < buffer.length; i++) {
			wr.write(mapper.writeValueAsString(buffer[i]) + "\n");
		}
		
		wr.close();
	}
	
	private static final TType transitJourney[] = new TType[] { TType.TRANSIT, TType.WALK };
	private static final TType carJourney[] = new TType[] { TType.CAR, TType.WALK };
	private static final TType walkingJourney[] = new TType[] { TType.WALK };
	private static final TType bikeJourney[] = new TType[] { TType.BICYCLE };
	
	private static final int DAYS_OF_WEEK[] = new int[] { 
		DayOfWeek.MONDAY.getValue(),
		DayOfWeek.TUESDAY.getValue(),
		DayOfWeek.WEDNESDAY.getValue(),
		DayOfWeek.THURSDAY.getValue(),
		DayOfWeek.FRIDAY.getValue(),
		DayOfWeek.SATURDAY.getValue(),
		DayOfWeek.SUNDAY.getValue()
	};
	
	private static DateTimeFormatter format = DateTimeFormatter.ofPattern("hh:mma");
	
	private static boolean validateParameters(IPlannerService planner, TravelEvent event, boolean hasCar, boolean hasBike, StreetMap map) {
		List<Itinerary> it = new ArrayList<Itinerary>();
		// Request transit journeys.
		JourneyRequest s = createRequest(event.getStartingPoint(), event.getDestination(), event.getTime(),
				event.arriveBy(), transitJourney, 500);
		List<Itinerary> temp = planner.requestSingleJourney(s);
		if (temp != null) it.addAll(temp);

		// Add walking journeys
		s.TransportTypes = walkingJourney;
		s.MaximumWalkDistance = 1500;
		temp = planner.requestSingleJourney(s);
		if (temp != null) it.addAll(temp);
		int nMeans = 2;

		// Add car journeys if person has car.
		if (hasCar) {
			s.TransportTypes = carJourney;
			s.MaximumWalkDistance = 500;
			temp = planner.requestSingleJourney(s);
			if (temp != null) it.addAll(temp);
			nMeans++;
		}
		
		// Add bike journeys if person has bike.
		if (hasBike) {
			s.TransportTypes = bikeJourney;
			s.MaximumWalkDistance = 500;
			temp = planner.requestSingleJourney(s);
			if (temp != null) it.addAll(temp);
			nMeans++;
		}
		//System.out.println(nMeans + " " + it.size());
		return it.size() >= nMeans;
		/*if (it.size() == 0) {
			return false;
		} else if (hasCar && hasBike) {
			return 
		}*/
		
		// Itinerary i = it.get(0);
		//return LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(i.startTime)), ZoneId.of("UTC")).getHour() <= event.getTime().getHour();
	}
	
	private static JourneyRequest createRequest(Coordinate from,
			Coordinate to, 
			LocalTime time,
			boolean arriveBy,
			TType modes[],
			int maxWalk) {
		JourneyRequest s = new JourneyRequest();
		s.Date = "08/25/2014";
		
		if (arriveBy) {
			s.ArrivalTime = time.format(format);
		} else {
			s.DepartureTime = time.format(format);
		}

		// Set starting position and destination.
		s.From.x = from.x;
		s.From.y = from.y;
		s.To.x = to.x;
		s.To.y = to.y;

		// Set random route type.
		s.RouteType = RType.QUICK;

		// Set predefined choice of means of transportation.
		s.TransportTypes = modes;
		s.ResultsNumber = 1;
		s.MaximumCosts = 25;
		s.MaximumWalkDistance = maxWalk;
		return s;
	}
	
	private static Coordinate newLocation(DistrictLayer l, int distribution[]) {
		int r1 = ThreadLocalRandom.current().nextInt(100);
		DistrictType types[] = DistrictType.values();
		DistrictType t = types[0];
		int acc = 0;
		
		for (int i = 0; i < types.length; i++) {
			acc += distribution[i];
			
			if (r1 < acc) {
				t = types[i];
				break;
			}
		}
		List<Area> possibleAreas = l.getAreasOfType(t);
		Area a = possibleAreas.get(ThreadLocalRandom.current().nextInt(possibleAreas.size()));
		List<StreetNode> temp = l.getPointsInArea(a);
		return temp.get(ThreadLocalRandom.current().nextInt(temp.size())).getPosition();
	}
	
	private static LocalTime gaussianPointInTime(double mean, double std) {
		int t = (int) ((ThreadLocalRandom.current().nextGaussian() * std) + mean);
		int hour = t / 60;
		return LocalTime.of(hour, t - hour * 60);
	}
	
	private static LocalTime fullHourPointInTime(int min, int max) {
		int t = ThreadLocalRandom.current().nextInt(max - min + 1);
		return LocalTime.of(min + t, 0);
	}
	
	private static int PROP_HOME_CHILD[] = { 75, 10, 10, 0, 0, 5 };
	//private static int PROP_HOME_CHILD[] = { 91, 0, 3, 0, 0, 6 };
	//private static int PROP_HOME_CHILD[] = { 85, 5, 5, 5, 0, 0 };
	
	private static Area getNearestSchool(List<Area> schools, Coordinate p) {
		Area a = schools.get(0);
		double minDist = Geometry.haversine(p, a.getBoundary().get(0));
		int minIndex = 0;
		
		for (int i = 1; i < schools.size(); i++) {
			a = schools.get(i);
			double dist = Geometry.haversine(p, a.getBoundary().get(0));
			
			if (dist < minDist) {
				minDist = dist;
				minIndex = i;
			}
		}
		return schools.get(minIndex);
	}
	
	private static Person createChild(int id, StreetMap map, IPlannerService planner) {
		DistrictLayer l = (DistrictLayer) map.getLayer(Layer.Type.DISTRICTS);
		List<Area> schools = l.getAreasOfType(DistrictType.SCHOOL);
		Coordinate home = null, school = null;
		TravelEvent homeToWork = null, workToHome = null;
		boolean valid = false;
		boolean hasBike = ThreadLocalRandom.current().nextBoolean();
		
		while (!valid) {
			home = newLocation(l, PROP_HOME_CHILD);
			Area a = getNearestSchool(schools, home);
			List<StreetNode> temp = l.getPointsInArea(a);
			school = temp.get(0).getPosition();
			homeToWork = new TravelEvent(LocalTime.of(8, 0), home, school, true);
			workToHome = new TravelEvent(LocalTime.of(13, 0), school, home, false);
			valid = validateParameters(planner, homeToWork, false, hasBike, map) && validateParameters(planner, workToHome, false, hasBike, map);
		}
		DailyRoutine r = new DailyRoutine();
		
		for (int i = 0; i < 5; i++) {
			r.addToDailyRoutine(homeToWork, DAYS_OF_WEEK[i]);
			r.addToDailyRoutine(workToHome, DAYS_OF_WEEK[i]);
		}
		Gender gender = (ThreadLocalRandom.current().nextInt(100) < 50) ? Gender.MALE : Gender.FEMALE;
		
		double[] weights = createNormVec();
		double cweight = weights[0]; // ThreadLocalRandom.current().nextDouble(0.3, 1.0);
		double ttweight = weights[1]; // 1.0 - cweight;
		// double wweight = weights[2];
		// double tweight = weights[3];
		double carPreference = ThreadLocalRandom.current().nextDouble(0.5);
		double busPreference = 1.0 - carPreference;
		Preferences prefs = new Preferences(ttweight, cweight, 0.0, 0.0, 0, 0, 0, busPreference, carPreference);
		//Preferences prefs = new Preferences(ttweight, cweight, wweight, tweight, 0, 0, 0, busPreference, carPreference);
		Person newChild = new Person(id, gender, Profile.CHILD, new UtilityWithoutPreferences(), prefs, home, false, hasBike, false, r);
		return newChild;
	}
	
	//private static int PROP_HOME_HOMEMAKER[] = { 87, 3, 3, 3, 0, 6 };
	private static int PROP_HOME_HOMEMAKER[] = { 85, 5, 5, 5, 0, 0 };
	private static int PROP_WORK_HOMEMAKER[] = { 0, 0, 90, 0, 0, 10 };
	private static int PROP_CAR_HOMEMAKER = 80;
	
	private static Person createHomemaker(int id, StreetMap map, IPlannerService planner) {
		DistrictLayer l = (DistrictLayer) map.getLayer(Layer.Type.DISTRICTS);
		Coordinate home = null, second = null;
		
		//if (ThreadLocalRandom.current().nextInt(100) < 70) {
			// Homemaker with no explicit daily routine.
			TravelEvent homeToWork = null, workToHome = null;
			boolean hasCar = (ThreadLocalRandom.current().nextInt(100) < PROP_CAR_HOMEMAKER);
			boolean hasBike = ThreadLocalRandom.current().nextBoolean();
			boolean valid = false;
			
			while (!valid) {
				home = newLocation(l, PROP_HOME_HOMEMAKER);
				second = newLocation(l, PROP_WORK_HOMEMAKER);
				homeToWork = new TravelEvent(gaussianPointInTime(840, 120), home, second, true);
				int hour = homeToWork.getTime().getHour() + 1;
				if (hour == 24) hour = 0;
				workToHome = new TravelEvent(LocalTime.of(hour, homeToWork.getTime().getMinute()), second, home, false);
				valid = validateParameters(planner, homeToWork, hasCar, hasBike, map) && validateParameters(planner, workToHome, hasCar, hasBike, map);
			}
			Gender gender = (ThreadLocalRandom.current().nextInt(100) < 50) ? Gender.MALE : Gender.FEMALE;
			boolean useFlexiBus = false; //ThreadLocalRandom.current().nextBoolean();
			
			double[] weights = createNormVec();
			double cweight = weights[0]; // ThreadLocalRandom.current().nextDouble(0.3, 1.0);
			double ttweight = weights[1]; // 1.0 - cweight;
			// double wweight = weights[2];
			// double tweight = weights[3];
			double carPreference = ThreadLocalRandom.current().nextDouble(0.5);
			double busPreference = 1.0 - carPreference;
			Preferences prefs = new Preferences(ttweight, cweight, 0.0, 0.0, 0, 0, 0, busPreference, carPreference);
			// Preferences prefs = new Preferences(ttweight, cweight, wweight, tweight, 0, 0, 0, busPreference, carPreference);
			Person newHomemaker = new Person(id, gender, Profile.HOMEMAKER, new UtilityWithoutPreferences(), prefs, home, hasCar, hasBike, useFlexiBus, new DailyRoutine());
			return newHomemaker;
			
		/*} /*else {
			List<Area> schools = l.getAreaOfType(AreaType.SCHOOL);
			TravelEvent homeToWork = null, workToHome = null;
			boolean valid = false;
		
			while (!valid) {
				home = newLocation(l, PROP_HOME_CHILD);
				Area a = getNearestSchool(schools, home);
				List<StreetNode> temp = l.getPointsInArea(a);
				second = temp.get(0).getPosition();
				homeToWork = new TravelEvent(LocalTime.of(8, 0), home, second, true);
				workToHome = new TravelEvent(LocalTime.of(13, 0), second, home, false);
				valid = validateParameters(planner, homeToWork, true, false) && validateParameters(planner, workToHome, true, false);
			}
			DailyRoutine r = new DailyRoutine();
		
			for (int i = 0; i < 5; i++) {
				r.addToDailyRoutine(homeToWork, DAYS_OF_WEEK[i]);
				r.addToDailyRoutine(workToHome, DAYS_OF_WEEK[i]);
			}
			Person newHomemaker = new Person(id, Profile.HOMEMAKER, new Utility(), new Preferences(), home, true, (ThreadLocalRandom.current().nextInt(100) > 90), r);
			return newHomemaker;
		}*/
	}
	
	//private static int PROP_HOME_STUDENT[] = { 86, 2, 3, 3, 0, 6 };
	private static int PROP_HOME_STUDENT[] = { 65, 10, 10, 15, 0, 0 };

	private static int PROP_WORK_STUDENT[] = { 0, 0, 0, 100, 0, 0 };
	private static int PROP_CAR_STUDENT = 60;
	private static int PROP_BIKE_STUDENT = 80;

	private static Person createStudent(int id, StreetMap map, IPlannerService planner) {
		DistrictLayer l = (DistrictLayer) map.getLayer(Layer.Type.DISTRICTS);
		boolean hasCar = (ThreadLocalRandom.current().nextInt(100) < PROP_CAR_STUDENT);
		boolean hasBike = (ThreadLocalRandom.current().nextInt(100) < PROP_BIKE_STUDENT);
		Coordinate home = null, university = null;
		boolean valid = false;
		List<TravelEvent> events = new ArrayList<TravelEvent>(10);
		
		while (!valid) {
			home = newLocation(l, PROP_HOME_STUDENT);
			university = newLocation(l, PROP_WORK_STUDENT);
			valid = true;
			
			for (int i = 0; i < 5 && valid; i++) {
				TravelEvent homeToWork = new TravelEvent(fullHourPointInTime(8, 13), home, university, true);
				TravelEvent workToHome = new TravelEvent(
						LocalTime.of(homeToWork.getTime().getHour() + ThreadLocalRandom.current().nextInt(6) + 2, 
								homeToWork.getTime().getMinute()), university, home, false);
				valid = validateParameters(planner, homeToWork, hasCar, hasBike, map) && validateParameters(planner, workToHome, hasCar, hasBike, map);
				events.add(homeToWork);
				events.add(workToHome);
			}
			
			if (!valid) events.clear();
		}
		DailyRoutine r = new DailyRoutine();
		
		for (int i = 0, j = 0; i < 5; i++, j += 2) {
			r.addToDailyRoutine(events.get(j), DAYS_OF_WEEK[i]);
			r.addToDailyRoutine(events.get(j + 1), DAYS_OF_WEEK[i]);
		}
		Gender gender = (ThreadLocalRandom.current().nextInt(100) < 50) ? Gender.MALE : Gender.FEMALE;
		double[] weights = createNormVec();
		double cweight = weights[0]; // ThreadLocalRandom.current().nextDouble(0.3, 1.0);
		double ttweight = weights[1]; // 1.0 - cweight;
		//double wweight = weights[2];
		//double tweight = weights[3];
		double busPreference = 0.0;
		double carPreference = 0.0;
		
		//if (cweight > ttweight) {
			//busPreference = ThreadLocalRandom.current().nextDouble(0.5, 1.0);
			//carPreference = 1.0 - busPreference;
		// } else {
		carPreference = ThreadLocalRandom.current().nextDouble(0.5);
		busPreference = 1.0 - carPreference;
		//}
		Preferences prefs = new Preferences(ttweight, cweight, 0.0, 0.0, 0, 0, 0, busPreference, carPreference);
		// Preferences prefs = new Preferences(ttweight, cweight, wweight, tweight, 0, 0, 0, busPreference, carPreference);
		boolean useFlexiBus = false; // ThreadLocalRandom.current().nextBoolean();
		Person newStudent = new Person(id, gender, Profile.STUDENT, new UtilityWithoutPreferences(), prefs, home, hasCar, hasBike, useFlexiBus, r);
		return newStudent;
	}
	
	// Probability distribution of home location of workers.
	//private static int PROP_HOME_WORKER[] = { 82, 5, 3, 3, 0, 7 };
	private static int PROP_HOME_WORKER[] = { 65, 15, 15, 5, 0, 7 };

	// Probability distribution of working location of workers.
	private static int PROP_WORK_WORKER[] = { 0, 80, 10, 0, 0, 10 };
	
	// Probability distribution of workers having a car.
	private static int PROP_CAR_WORKER = 90;
	
	private static int PROP_BIKE_WORKER = 30;
	
	private static Person createWorker(int id, StreetMap map, IPlannerService planner) {
		// Get layer.
		DistrictLayer l = (DistrictLayer) map.getLayer(Layer.Type.DISTRICTS);
		
		// Determine car availability.
		boolean hasCar = (ThreadLocalRandom.current().nextInt(100) < PROP_CAR_WORKER);
		boolean hasBike = (ThreadLocalRandom.current().nextInt(100) < PROP_BIKE_WORKER);
		
		// Determine home and working location.
		Coordinate home = null, work = null;
		TravelEvent homeToWork = null, workToHome = null;
		boolean valid = false;
		
		while (!valid) {
			home = newLocation(l, PROP_HOME_WORKER);
			work = newLocation(l, PROP_WORK_WORKER);
			homeToWork = new TravelEvent(gaussianPointInTime(450, 25), home, work, true);
			workToHome = new TravelEvent(LocalTime.of(homeToWork.getTime().getHour() + 8, homeToWork.getTime().getMinute()), work, home, false);
			valid = validateParameters(planner, homeToWork, hasCar, hasBike, map) && validateParameters(planner, workToHome, hasCar, hasBike, map);
		}
		DailyRoutine r = new DailyRoutine();
		
		for (int i = 0; i < 5; i++) {
			r.addToDailyRoutine(homeToWork, DAYS_OF_WEEK[i]);
			r.addToDailyRoutine(workToHome, DAYS_OF_WEEK[i]);
		}
		Gender gender = (ThreadLocalRandom.current().nextInt(100) < 50) ? Gender.MALE : Gender.FEMALE;
		double[] weights = createNormVec();
		double ttweight = weights[0];//ThreadLocalRandom.current().nextDouble(0.3, 1.0);
		double cweight = weights[1];// 1.0 - ttweight;
		//double wweight = weights[2];
		//double tweight = weights[3];
		double busPreference = 0.0;
		double carPreference = 0.0;
		
		//if (cweight > ttweight) {
		//	busPreference = ThreadLocalRandom.current().nextDouble(0.5, 1.0);
		//	carPreference = 1.0 - busPreference;
		//} else {
		carPreference = ThreadLocalRandom.current().nextDouble(0.5);
			busPreference = 1.0 - carPreference;
		//}
		Preferences prefs = new Preferences(ttweight, cweight, 0.0, 0.0, 0, 0, 0, busPreference, carPreference);
		//Preferences prefs = new Preferences(ttweight, cweight, wweight, tweight, 0, 0, 0, busPreference, carPreference);
		boolean useFlexiBus = false; // ThreadLocalRandom.current().nextBoolean();
		Person newWorker = new Person(id, gender, Profile.WORKER, new UtilityWithoutPreferences(), prefs, home, hasCar, hasBike, useFlexiBus, r);
		return newWorker;
	}
	
	private static double[] createNormVec() {
		double v[] = { Math.abs(ThreadLocalRandom.current().nextGaussian()),
				Math.abs(ThreadLocalRandom.current().nextGaussian()) };
				/*Math.abs(ThreadLocalRandom.current().nextGaussian()),
				Math.abs(ThreadLocalRandom.current().nextGaussian()) };*/
		double normInv = 1.0 / (v[0] + v[1]/* + v[2] + v[3]*/);
		
		for (int i = 0; i < v.length; i++) v[i] *= normInv;
		return v;
	}
}
