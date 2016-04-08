package allow.simulator.entity;

import java.time.LocalTime;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;

import allow.simulator.flow.activity.Activity;
import allow.simulator.flow.activity.person.PlanJourney;
import allow.simulator.flow.activity.person.RegisterToFlexiBus;
import allow.simulator.util.Coordinate;
import allow.simulator.util.Geometry;
import allow.simulator.util.Pair;
import allow.simulator.world.StreetNode;
import allow.simulator.world.layer.Area;
import allow.simulator.world.layer.DistrictLayer;
import allow.simulator.world.layer.DistrictType;
import allow.simulator.world.layer.Layer;

public class PlanGenerator {

	private static final long SECONDS_TO_REGISTER_BEFORE_PLAN = 1800;

	public static void generateDayPlan(Person person) {

		switch (person.getProfile()) {
		case CHILD:
			generateChildDayPlan(person);
			break;

		case STUDENT:
			generateStudentDayPlan(person);
			break;

		case WORKER:
			generateWorkerDayPlan(person);
			break;

		case HOMEMAKER:
			generateHomemakerDayPlan(person);
			break;

		case RANDOM:
			generateDailyRoutine(person);
			break;

		default:
			throw new IllegalArgumentException("Error: Unknown person role "
					+ person.getProfile());

		}
	}

	private static Pair<LocalTime, Activity> createPlanJourney(Person p, TravelEvent t, long pOffsetSeconds) {
		LocalTime temp = t.getTime().minusSeconds(pOffsetSeconds).withSecond(0);
		return new Pair<LocalTime, Activity>(temp, new PlanJourney(p, t.getStartingPoint(), t.getDestination()));
	}
	
	private static Pair<LocalTime, Activity> createRegistertoFB(Person p, TravelEvent t, long pOffsetSeconds) {
		LocalTime pTemp = t.getTime().minusSeconds(pOffsetSeconds).withSecond(0);
		LocalTime rTemp = pTemp.minusSeconds(SECONDS_TO_REGISTER_BEFORE_PLAN).withSecond(0);
		return new Pair<LocalTime, Activity>(rTemp, new RegisterToFlexiBus(p, t.getStartingPoint(), t.getDestination(), pTemp));
	}
	
	private static void generateDailyRoutine(Person person) {
		int day = person.getContext().getTime().getCurrentDateTime().getDayOfWeek().getValue();
		List<TravelEvent> routine = person.getDailyRoutine().getDailyRoutine(1);
		Queue<Pair<LocalTime, Activity>> schedule = person.getScheduleQueue();

		// Add daily routine travel events.
		for (TravelEvent t : routine) {
			long pOffsetSeconds = t.arriveBy() ? (long) Geometry.haversine(t.getStartingPoint(), t.getDestination()) / 4 : 0;
			
			if (person.useFlexiBus()) {
				schedule.add(createRegistertoFB(person, t, pOffsetSeconds));
			}
			schedule.add(createPlanJourney(person, t, pOffsetSeconds));
		}
	}

	private static void generateChildDayPlan(Person person) {
		int day = person.getContext().getTime().getCurrentDateTime()
				.getDayOfWeek().getValue();
		List<TravelEvent> routine = person.getDailyRoutine().getDailyRoutine(1);
		Queue<Pair<LocalTime, Activity>> schedule = person.getScheduleQueue();

		// Add daily routine travel events.
		for (TravelEvent t : routine) {
			long pOffsetSeconds = t.arriveBy() ? (long) Geometry.haversine(t.getStartingPoint(), t.getDestination()) / 4 : 0;
			schedule.add(createPlanJourney(person, t, pOffsetSeconds));
			/*if (t.arriveBy()) {
				double estTimeToDest = Geometry.haversine(t.getStartingPoint(),
						t.getDestination()) / 4.0;

				if (person.useFlexiBus()) {
					schedule.add(new Pair<LocalTime, Activity>(
							t.getTime()
									.minusSeconds(
											(long) (estTimeToDest + SECONDS_TO_REGISTER_BEFORE_PLAN))
									.withSecond(0), new RegisterToFlexiBus(
									person, t.getStartingPoint(), t
											.getDestination(), t.getTime())));
				}
				schedule.add(new Pair<LocalTime, Activity>(t.getTime()
						.minusSeconds((long) estTimeToDest).withSecond(0),
						new PlanJourney(person, t.getStartingPoint(), t
								.getDestination())));

			} else {
				schedule.add(new Pair<LocalTime, Activity>(t.getTime(),
						new PlanJourney(person, t.getStartingPoint(), t
								.getDestination())));
			}*/
		}
	}

	private static int PROP_DEST_STUDENT[] = { 25, 5, 70, 0, 0, 0 };

	private static void generateStudentDayPlan(Person person) {
		DistrictLayer partitioning = (DistrictLayer) person.getContext().getWorld().getStreetMap().getLayer(Layer.Type.DISTRICTS);
		int day = person.getContext().getTime().getCurrentDateTime().getDayOfWeek().getValue();
		List<TravelEvent> routine = person.getDailyRoutine().getDailyRoutine(1);
		Queue<Pair<LocalTime, Activity>> schedule = person.getScheduleQueue();
		
		// 1. Home to work/university.
		TravelEvent homeToWork = routine.get(0);
		long pOffsetSeconds = (long) Geometry.haversine(homeToWork.getStartingPoint(), homeToWork.getDestination()) / 4;

		if (person.useFlexiBus()) {
			schedule.add(createRegistertoFB(person, homeToWork, pOffsetSeconds));
		}
		schedule.add(createPlanJourney(person, homeToWork, pOffsetSeconds));

		// 2. From work back home. 
		TravelEvent workToHome = routine.get(1);

		if (workToHome.getHour() < 16) {
			int rand = ThreadLocalRandom.current().nextInt(100);
			
			if (rand < 10) {
				// Intermediate journey then home (home - work - destination - work - home).
				Coordinate dest = newLocation(partitioning, PROP_DEST_STUDENT);
				schedule.add(new Pair<LocalTime, Activity>(
								workToHome.getTime(), new PlanJourney(person,
										workToHome.getStartingPoint(), dest)));

				LocalTime fromDest = workToHome.getTime();
				schedule.add(new Pair<LocalTime, Activity>(fromDest,
								new PlanJourney(person, dest, homeToWork
										.getDestination())));

						schedule.add(new Pair<LocalTime, Activity>(fromDest,
								new PlanJourney(person, workToHome.getStartingPoint(),
										workToHome.getDestination())));

			} else if (rand < 20) {
				// Home then another journey afterwards (home - work - home - destination - home).
				schedule.add(new Pair<LocalTime, Activity>(
								workToHome.getTime(), new PlanJourney(person,
										workToHome.getStartingPoint(), workToHome
												.getDestination())));

						Coordinate dest = newLocation(partitioning, PROP_DEST_STUDENT);
						LocalTime toDest = workToHome.getTime();
						schedule.add(new Pair<LocalTime, Activity>(toDest,
								new PlanJourney(person, workToHome.getDestination(),
										dest)));

						schedule.add(new Pair<LocalTime, Activity>(toDest,
								new PlanJourney(person, dest, workToHome
										.getDestination())));

			} else if (rand < 50) {
				// Triangular (home - work - destination - home).
				Coordinate dest = newLocation(partitioning, PROP_DEST_STUDENT);
				schedule.add(new Pair<LocalTime, Activity>(
						workToHome.getTime(), new PlanJourney(person,
						workToHome.getStartingPoint(), dest)));

				schedule.add(new Pair<LocalTime, Activity>(
						workToHome.getTime(), new PlanJourney(person, dest,
						homeToWork.getStartingPoint())));
			} else {
				// Straight home (home - work - home).
				if (person.useFlexiBus()) {
					schedule.add(createRegistertoFB(person, workToHome, 0));
				}
				schedule.add(createPlanJourney(person, workToHome, 0));
			}	
			
		} else {
			// Straight home (home - work - home).
			if (person.useFlexiBus()) {
				schedule.add(createRegistertoFB(person, workToHome, 0));
			}
			schedule.add(createPlanJourney(person, workToHome, 0));
		}	
		
		// 1. Home to work/university.
		/*TravelEvent homeToWork = routine.get(0);
		double estTimeToDest = Geometry.haversine(homeToWork.getStartingPoint(), homeToWork.getDestination()) / 4.0;

		if (person.useFlexiBus()) {
			schedule.add(new Pair<LocalTime, Activity>(
					homeToWork
							.getTime()
							.minusSeconds(
									(long) (estTimeToDest + SECONDS_TO_REGISTER_BEFORE_PLAN))
							.withSecond(0), new RegisterToFlexiBus(person,
							homeToWork.getStartingPoint(), homeToWork
									.getDestination(), homeToWork.getTime())));
		}
		schedule.add(new Pair<LocalTime, Activity>(homeToWork.getTime()
				.minusSeconds((long) estTimeToDest).withSecond(0),
				new PlanJourney(person, homeToWork.getStartingPoint(),
						homeToWork.getDestination())));

		TravelEvent workToHome = routine.get(1);

		if (workToHome.getHour() < 16) {
			int rand = ThreadLocalRandom.current().nextInt(100);
			if (rand < 10) {
				// Intermediate journey then home (home - work - destination -
				// work - home).
				Coordinate dest = newLocation(partitioning, PROP_DEST_STUDENT);
				schedule.add(new Pair<LocalTime, Activity>(
						workToHome.getTime(), new PlanJourney(person,
								workToHome.getStartingPoint(), dest)));

				LocalTime fromDest = workToHome.getTime();
				schedule.add(new Pair<LocalTime, Activity>(fromDest,
						new PlanJourney(person, dest, homeToWork
								.getDestination())));

				schedule.add(new Pair<LocalTime, Activity>(fromDest,
						new PlanJourney(person, workToHome.getStartingPoint(),
								dest)));

			} else if (rand < 20) {
				// Home then another journey afterwards (home - work - home -
				// destination - home).
				schedule.add(new Pair<LocalTime, Activity>(
						workToHome.getTime(), new PlanJourney(person,
								workToHome.getStartingPoint(), workToHome
										.getDestination())));

				Coordinate dest = newLocation(partitioning, PROP_DEST_STUDENT);
				LocalTime toDest = workToHome.getTime();
				schedule.add(new Pair<LocalTime, Activity>(toDest,
						new PlanJourney(person, workToHome.getDestination(),
								dest)));

				schedule.add(new Pair<LocalTime, Activity>(toDest,
						new PlanJourney(person, dest, workToHome
								.getDestination())));

			} else if (rand < 45) {
				// Triangular (home - work - destination - home).
				Coordinate dest = newLocation(partitioning, PROP_DEST_STUDENT);
				schedule.add(new Pair<LocalTime, Activity>(
						workToHome.getTime(), new PlanJourney(person,
								workToHome.getStartingPoint(), dest)));

				schedule.add(new Pair<LocalTime, Activity>(
						workToHome.getTime(), new PlanJourney(person, dest,
								homeToWork.getStartingPoint())));

			} else {
				// Straight home (home - work - home).
				schedule.add(new Pair<LocalTime, Activity>(
						workToHome.getTime(), new PlanJourney(person,
								workToHome.getStartingPoint(), workToHome
										.getDestination())));
			}
		}*/
	}

	private static int PROP_DEST_WORKER[] = { 15, 10, 75, 0, 0, 0 };

	private static void generateWorkerDayPlan(Person person) {
		DistrictLayer partitioning = (DistrictLayer) person.getContext().getWorld().getStreetMap().getLayer(Layer.Type.DISTRICTS);
		int day = person.getContext().getTime().getCurrentDateTime().getDayOfWeek().getValue();
		List<TravelEvent> routine = person.getDailyRoutine().getDailyRoutine(1);
		Queue<Pair<LocalTime, Activity>> schedule = person.getScheduleQueue();

		// 1. Going to work in the morning.
		TravelEvent homeToWork = routine.get(0);
		long pOffsetSeconds = (long) Geometry.haversine(homeToWork.getStartingPoint(), homeToWork.getDestination()) / 4;

		if (person.useFlexiBus()) {
			schedule.add(createRegistertoFB(person, homeToWork, pOffsetSeconds));
		}
		schedule.add(createPlanJourney(person, homeToWork, pOffsetSeconds));

		// 2. From work back home.
		TravelEvent workToHome = routine.get(1);
		int rand = ThreadLocalRandom.current().nextInt(100);
		
		if (rand < 10) {
			// Intermediate journey then home (home - work - destination - work - home).
			Coordinate dest = newLocation(partitioning, PROP_DEST_WORKER);
			schedule.add(new Pair<LocalTime, Activity>(workToHome.getTime(),
				new PlanJourney(person, workToHome.getStartingPoint(), dest)));

			LocalTime fromDest = workToHome.getTime();
			schedule.add(new Pair<LocalTime, Activity>(fromDest,
				new PlanJourney(person, dest, homeToWork.getDestination())));

			schedule.add(new Pair<LocalTime, Activity>(fromDest,
				new PlanJourney(person, workToHome.getStartingPoint(), workToHome.getDestination())));

		} else if (rand < 20) {
			// Home then another journey afterwards (home - work - home - destination - home).
			schedule.add(new Pair<LocalTime, Activity>(workToHome.getTime(),
				new PlanJourney(person, workToHome.getStartingPoint(), workToHome.getDestination())));

			Coordinate dest = newLocation(partitioning, PROP_DEST_WORKER);
			LocalTime toDest = workToHome.getTime();
			schedule.add(new Pair<LocalTime, Activity>(toDest,
					new PlanJourney(person, workToHome.getDestination(), dest)));

			schedule.add(new Pair<LocalTime, Activity>(toDest,
					new PlanJourney(person, dest, workToHome.getDestination())));

		} else if (rand < 45) {
			// Triangular (home - work - destination - home).
			Coordinate dest = newLocation(partitioning, PROP_DEST_WORKER);
			schedule.add(new Pair<LocalTime, Activity>(workToHome.getTime(),
					new PlanJourney(person, workToHome.getStartingPoint(), dest)));

			schedule.add(new Pair<LocalTime, Activity>(workToHome.getTime(),
					new PlanJourney(person, dest, homeToWork.getStartingPoint())));

		} else {
			// Straight home (home - work - home).
			if (person.useFlexiBus()) {
				schedule.add(createRegistertoFB(person, workToHome, 0));
			}
			schedule.add(createPlanJourney(person, workToHome, 0));
		}
				
		// 1. Going to work in the morning.
		/*TravelEvent homeToWork = routine.get(0);

		double estTimeSec = Geometry.haversine(homeToWork.getStartingPoint(),
				homeToWork.getDestination()) / 4.0;
		schedule.add(new Pair<LocalTime, Activity>(homeToWork.getTime()
				.minusSeconds((long) estTimeSec).withSecond(0),
				new PlanJourney(person, homeToWork.getStartingPoint(),
						homeToWork.getDestination())));

		TravelEvent workToHome = routine.get(1);

		int rand = ThreadLocalRandom.current().nextInt(100);
		if (rand < 10) {
			// Intermediate journey then home (home - work - destination - work
			// - home).
			Coordinate dest = newLocation(partitioning, PROP_DEST_WORKER);
			schedule.add(new Pair<LocalTime, Activity>(
					workToHome.getTime(),
					new PlanJourney(person, workToHome.getStartingPoint(), dest)));

			LocalTime fromDest = workToHome.getTime();
			schedule.add(new Pair<LocalTime, Activity>(fromDest,
					new PlanJourney(person, dest, homeToWork.getDestination())));

			schedule.add(new Pair<LocalTime, Activity>(
					fromDest,
					new PlanJourney(person, workToHome.getStartingPoint(), dest)));

		} else if (rand < 20) {
			// Home then another journey afterwards (home - work - home -
			// destination - home).
			schedule.add(new Pair<LocalTime, Activity>(workToHome.getTime(),
					new PlanJourney(person, workToHome.getStartingPoint(),
							workToHome.getDestination())));

			Coordinate dest = newLocation(partitioning, PROP_DEST_WORKER);
			LocalTime toDest = workToHome.getTime();
			schedule.add(new Pair<LocalTime, Activity>(toDest, new PlanJourney(
					person, workToHome.getDestination(), dest)));

			schedule.add(new Pair<LocalTime, Activity>(toDest, new PlanJourney(
					person, dest, workToHome.getDestination())));

		} else if (rand < 45) {
			// Triangular (home - work - destination - home).
			Coordinate dest = newLocation(partitioning, PROP_DEST_WORKER);
			schedule.add(new Pair<LocalTime, Activity>(
					workToHome.getTime(),
					new PlanJourney(person, workToHome.getStartingPoint(), dest)));

			schedule.add(new Pair<LocalTime, Activity>(
					workToHome.getTime(),
					new PlanJourney(person, dest, homeToWork.getStartingPoint())));

		} else {
			// Straight home (home - work - home).
			schedule.add(new Pair<LocalTime, Activity>(workToHome.getTime(),
					new PlanJourney(person, workToHome.getStartingPoint(),
							workToHome.getDestination())));
		}*/
	}

	private static int PROP_DEST_HOMEMAKER[] = { 30, 10, 60, 0, 0, 0 };

	private static void generateHomemakerDayPlan(Person person) {
		DistrictLayer partitioning = (DistrictLayer) person.getContext().getWorld().getStreetMap().getLayer(Layer.Type.DISTRICTS);
		Queue<Pair<LocalTime, Activity>> schedule = person.getScheduleQueue();

		// Journey in the morning?
		int rand = ThreadLocalRandom.current().nextInt(100);
		if (rand < 50) {
			// Random destination.
			Coordinate dest = newLocation(partitioning, PROP_DEST_HOMEMAKER);
			LocalTime tStart = gaussianPointInTime(600, 30);

			schedule.add(new Pair<LocalTime, Activity>(tStart, new PlanJourney(
					person, person.getHome(), dest)));

			schedule.add(new Pair<LocalTime, Activity>(
					tStart.plusMinutes(ThreadLocalRandom.current().nextInt(120,
							180)), new PlanJourney(person, dest, person
							.getHome())));
		}

		// Journey in the afternoon?
		rand = ThreadLocalRandom.current().nextInt(100);
		if (rand < 50) {
			// Random destination.
			Coordinate dest = newLocation(partitioning, PROP_DEST_HOMEMAKER);
			LocalTime tStart = gaussianPointInTime(960, 30);

			schedule.add(new Pair<LocalTime, Activity>(tStart, new PlanJourney(
					person, person.getHome(), dest)));

			schedule.add(new Pair<LocalTime, Activity>(
					tStart.plusMinutes(ThreadLocalRandom.current().nextInt(120,
							180)), new PlanJourney(person, dest, person
							.getHome())));
		}
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
		Area a = possibleAreas.get(ThreadLocalRandom.current().nextInt(
				possibleAreas.size()));
		List<StreetNode> temp = l.getPointsInArea(a);
		return temp.get(ThreadLocalRandom.current().nextInt(temp.size()))
				.getPosition();
	}

	private static LocalTime gaussianPointInTime(double mean, double std) {
		int t = (int) ((ThreadLocalRandom.current().nextGaussian() * std) + mean);
		int hour = t / 60;
		return LocalTime.of(hour, t - hour * 60);
	}

}
