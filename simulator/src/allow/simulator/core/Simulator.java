package allow.simulator.core;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.nlogo.agent.World;

import allow.simulator.ensemble.Ensemble;
import allow.simulator.ensemble.EnsembleManager;
import allow.simulator.entity.Entity;
import allow.simulator.entity.Entity.Type;
import allow.simulator.entity.FlexiBusAgency;
import allow.simulator.entity.Person;
import allow.simulator.entity.PlanGenerator;
import allow.simulator.entity.PublicTransportation;
import allow.simulator.entity.PublicTransportationAgency;
import allow.simulator.entity.Taxi;
import allow.simulator.entity.TaxiAgency;
import allow.simulator.entity.TransportationAgency;
import allow.simulator.entity.UrbanMobilitySystem;
import allow.simulator.entity.knowledge.EvoKnowledge;
import allow.simulator.entity.utility.Preferences;
import allow.simulator.entity.utility.Utility;
import allow.simulator.flow.activity.ums.QueryJourneyPlanner;
import allow.simulator.mobility.data.IDataService;
import allow.simulator.mobility.data.MobilityRepository;
import allow.simulator.mobility.data.OfflineDataService;
import allow.simulator.mobility.data.OnlineDataService;
import allow.simulator.mobility.data.TransportationRepository;
import allow.simulator.mobility.planner.BikeRentalPlanner;
import allow.simulator.mobility.planner.FlexiBusPlanner;
import allow.simulator.mobility.planner.IPlannerService;
import allow.simulator.mobility.planner.JourneyRepository;
import allow.simulator.mobility.planner.OfflineJourneyPlanner;
import allow.simulator.mobility.planner.OnlineJourneyPlanner;
import allow.simulator.mobility.planner.TaxiPlanner;
import allow.simulator.statistics.Statistics;
import allow.simulator.util.Coordinate;
import allow.simulator.world.IWorld;
import allow.simulator.world.NetLogoWorld;
import allow.simulator.world.Weather;
import allow.simulator.world.layer.Layer;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Main class of Allow Ensembles urban traffic simulation.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class Simulator {
	// Instance of the simulator which can be accessed using getInstance() method.
	private static Simulator instance;
		
	// Simulation context.
	private Context context;
	
	// Id counter for entities.
	private long ids;
	
	private ExecutorService plannerThreadPool;
	private ExecutorService knowlegdeThreadPool;
	
	public static final String LAYER_DISTRICTS = "partitioning";
	public static final String LAYER_SAFTEY = "safety";
	
	/**
	 * Creates a new instance of the simulator.
	 * @throws IOException 
	 */
	public void setup(Configuration config, 
			SimulationParameter params,
			World netLogoWorld) throws IOException {
		// Reset Id counter.
		ids = 0;
	
		// Setup world.
		System.out.println("Loading world...");
		NetLogoWorld world = new NetLogoWorld(netLogoWorld, config.getMapPath());
		
		System.out.println("  Adding layer \"" + LAYER_DISTRICTS + "\"...");
		Path l = config.getLayerPath(LAYER_DISTRICTS);
		if (l == null) throw new IllegalStateException("Error: Missing layer with key \"" + LAYER_DISTRICTS + "\".");
		world.getStreetMap().addLayer(Layer.Type.DISTRICTS, l);
		
		System.out.println("  Adding layer \"" + LAYER_SAFTEY + "\"...");
		l = config.getLayerPath(LAYER_SAFTEY);
		if (l == null) throw new IllegalStateException("Error: Missing layer with key \"" + LAYER_SAFTEY + "\".");
		world.getStreetMap().addLayer(Layer.Type.SAFETY, config.getLayerPath(LAYER_SAFTEY));

		// Create data services.
		System.out.println("Creating data services...");
		List<IDataService> dataServices = new ArrayList<IDataService>();
		List<Service> dataConfigs = config.getDataServiceConfiguration();
		
		for (Service dataConfig : dataConfigs) {

			if (dataConfig.isOnline()) {
				// For online queries create online data services. 
				dataServices.add(new OnlineDataService(dataConfig.getURL(), dataConfig.getPort()));
		
			} else {
				// For offline queries create mobility repository and offline service.
				MobilityRepository repos = new MobilityRepository(Paths.get(dataConfig.getURL()), world.getStreetMap());
				dataServices.add(new OfflineDataService(repos));
			}
		}
		
		// Create time and weather.
		Time time = new Time(config.getStartingDate(), 5);
				
		// Create planner services.
		System.out.println("Creating planner services...");
		List<IPlannerService> plannerServices = new ArrayList<IPlannerService>();
		List<Service> plannerConfigs = config.getPlannerServiceConfiguration();
		int nClients = config.allowParallelClientRequests() ? (Runtime.getRuntime().availableProcessors() * 8) : plannerConfigs.size();
		
		for (int i = 0; i < nClients; i++) {
			Service plannerConfig = plannerConfigs.get(i % plannerConfigs.size());

			if (plannerConfig.isOnline()) {
				// For online queries create online planner service. 
				plannerServices.add(new OnlineJourneyPlanner(plannerConfig.getURL(), plannerConfig.getPort(), config.getTracesOutputPath()));
			
			} else {
				// For offline queries create journey repository and offline services.
				JourneyRepository journeyRepository = new JourneyRepository(plannerConfig.getURL());
				plannerServices.add(new OfflineJourneyPlanner(journeyRepository, config.getTracesOutputPath()));
			}
		}		
		
		// Create taxi planner service
		Coordinate taxiRank = new Coordinate(11.1198448, 46.0719489);
		TaxiPlanner taxiPlannerService = new TaxiPlanner(plannerServices, time, taxiRank);
		
		// Create bike rental service
		Coordinate bikeRentalStation = new Coordinate(11.1248895,46.0711398);
		BikeRentalPlanner bikeRentalPlanner = new BikeRentalPlanner(plannerServices, time, bikeRentalStation);
		System.out.println("Loading weather model...");
		Weather weather = new Weather(config.getWeatherPath(), time);
				
		// Create global context from world, time, planner and data services, and weather.
		context = new Context(world, time, dataServices, plannerServices, new FlexiBusPlanner(),
				taxiPlannerService, bikeRentalPlanner, weather, new Statistics(800),
				new EnsembleManager(), params);
		
		// Setup entities.
		System.out.println("Loading entities from file...");
		loadEntitiesFromFile(config.getAgentConfigurationPath(), params.KnowledgeModel);
		
		// Create public transportation.
		System.out.println("Creating public transportation system...");
		TransportationRepository repos = TransportationRepository.loadPublicTransportation(this);
		
		// Create urban mobility system entity.
		UrbanMobilitySystem ums = (UrbanMobilitySystem) addEntity(Entity.Type.URBANMOBILITYSYSTEM);
		ums.setTransportationRepository(repos);
		int nThreads = config.allowParallelClientRequests() ? (Runtime.getRuntime().availableProcessors() * 8) : plannerServices.size();
		plannerThreadPool = Executors.newFixedThreadPool(nThreads);
		ums.getFlow().addActivity(new QueryJourneyPlanner(ums, config.allowParallelClientRequests(), plannerThreadPool));
		
		// Create basic ensemble structure.
		setupEnsembles();
		
		// Initialize EvoKnowlegde and setup logger.
		knowlegdeThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 4);
		EvoKnowledge.initialize(config.getEvoKnowledgeConfiguration(), params.KnowledgeModel, "evo_" + params.BehaviourSpaceRunNumber, knowlegdeThreadPool);
		EvoKnowledge.setLoggerDirectory(config.getLoggingOutputPath());
		
		// Update world grid.
		world.updateGrid();
	}
	
	private void setupEnsembles() {
		EnsembleManager ensembles = context.getEnsembleManager();
		// Prepare basic ensemble structure.
		Entity flexiBusAgency = TransportationRepository.Instance().getFlexiBusAgency();
		Ensemble flexiBusEnsemble = ensembles.createEnsemble(EnsembleManager.FLEXIBUS_AGENCY_ENSEMBLE, flexiBusAgency);
		
		Map<String, PublicTransportationAgency> gtfsAgencies = TransportationRepository.Instance().getGTFSTransportAgencies();
		
		for (TransportationAgency agency : gtfsAgencies.values()) {
			ensembles.createEnsemble("TransportAgency" + agency.getAgencyId() + "Ensemble", agency);
		}
	}
	
	private void loadEntitiesFromFile(Path config, String knowledgeModel) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		
		List<String> lines = Files.readAllLines(config, Charset.defaultCharset());
		
		for (String line : lines) {
			Person p = mapper.readValue(line, Person.class);
			p.setContext(context);
			
			/*if (knowledgeModel.equals("local") || knowledgeModel.equals("global (temporally restricted)")) {
				p.setUtility(new UtilityWithoutPreferences());
				
			} else if (knowledgeModel.equals("without")) {

			} else {
				throw new IllegalArgumentException("Error: Unknown knowledge model");
			}*/
			PlanGenerator.generateDayPlan(p);
			context.getWorld().addEntity(p);
			ids = Math.max(ids, p.getId() + 1);
		}
	}
	
	/**
	 * Get current instance of simulator parameters. 
	 * Instance must be created with createInstance(...) before calling the function.
	 * Otherwise an exception is thrown.
	 * 
	 * @return The current simulator parameters.
	 */
	public static Simulator Instance() {
		if (instance == null) instance = new Simulator();
		return instance;
	}

	/**
	 * Adds a new entity of given type to the simulation.
	 * 
	 * @param e Type of entity.
	 * @return Instance of new entity of given type.
	 */
	public Entity addEntity(Entity.Type e) {
		// Entity to add.
		Entity newEntity = null;
		
		switch (e) {
			case BUS:
				newEntity = new PublicTransportation(ids++, new Utility(), new Preferences(), context, 25);
				break;
				
			case TAXI:
				newEntity = new Taxi(ids++, new Utility(), new Preferences(), context, 3);
				break;
				
			case PUBLICTRANSPORTAGENCY:
				newEntity = new PublicTransportationAgency(ids++, new Utility(), new Preferences(), context);
				break;
			
			case FLEXIBUSAGENCY:
				newEntity = new FlexiBusAgency(ids++, new Utility(), new Preferences(), context);
				break;
				
			case TAXIAGENCY:
				newEntity = new TaxiAgency(ids++, new Utility(), new Preferences(), context);
				break;
				
			case URBANMOBILITYSYSTEM:
				newEntity = new UrbanMobilitySystem(ids++, new Utility(), new Preferences(), context);
				break;
				
			default:
				throw new IllegalArgumentException("Error: Unknown entity type.");
		}
		context.getWorld().addEntity(newEntity);
		return newEntity;
	}
	
	/**
	 * Advances the simulation by one step.
	 * 
	 * @param deltaT Time interval for this step.
	 */
	public void tick(int deltaT) {
		// Save current day to trigger routine scheduling.
		int days = context.getTime().getDays();
		
		// Update time.
		context.getTime().tick(deltaT);
		
		// Update street network.
		context.getWorld().getStreetMap().updateStreetSegments();
		
		// Trigger routine scheduling.
		if (days != context.getTime().getDays()) {
			Collection<Entity> persons = context.getWorld().getEntitiesOfType(Type.PERSON);

			for (Entity p : persons) {
				PlanGenerator.generateDayPlan((Person) p);
				p.getRelations().resetBlackList();
			}
		}
		
		if (context.getTime().getCurrentTime().getHour() == 3
				&& context.getTime().getCurrentTime().getMinute() == 0
				&& context.getTime().getCurrentTime().getSecond() == 0) {
			context.getStatistics().reset();
		}
		//context.getWorld().getStreetMap().getNBusiestStreets(20);
		EvoKnowledge.invokeRequest();
		EvoKnowledge.cleanModel();
	}
	
	/**
	 * Removes an entity from the simulation given its Id.
	 * 
	 * @param entityId Id of the entity.
	 */
	public void removeEntity(long entityId) {
		context.getWorld().removeEntity(entityId);
	}

	/**
	 * Returns an entity of the simulation given its Id.
	 * 
	 * @param entityId Id of the entity.
	 * @return Entity of simulation with given Id.
	 */
	public Entity getEntity(long entityId) {
		return context.getWorld().getEntityById(entityId);	
	}
	
	/**
	 * Returns the current time of day of the simulation.
	 * 
	 * @return Current time of day of the simulation.
	 */
	public Time getTime() {
		return context.getTime();
	}
	
	/**
	 * Returns the simulated world of the simulation.
	 * 
	 * @return Simulated world.
	 */
	public IWorld getWorld() {
		return context.getWorld();
	}
	
	/**
	 * Returns the data services to use in the simulation.
	 * 
	 * @return Data services of the simulation.
	 */
	public List<IDataService> getDataService() {
		return context.getDataServices();
	}
	
	/**
	 * Returns the planner services to use in the simulation.
	 * 
	 * @return Planner services of the simulation.
	 */
	public List<IPlannerService> getPlannerService() {
		return context.getPlannerServices();
	}
	
	/**
	 * Returns the weather of the simulation.
	 * 
	 * @return Weather of the simulation.
	 */
	public Weather getWeather() {
		return context.getWeather();
	}
	
	public Context getContext() {
		return context;
	}
	
	public void finish() {
		plannerThreadPool.shutdown();
		knowlegdeThreadPool.shutdown();
	}
}
