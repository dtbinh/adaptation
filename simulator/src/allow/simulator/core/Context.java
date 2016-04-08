package allow.simulator.core;

import java.util.List;

import allow.simulator.ensemble.EnsembleManager;
import allow.simulator.mobility.data.IDataService;
import allow.simulator.mobility.planner.IPlannerService;
import allow.simulator.statistics.Statistics;
import allow.simulator.world.IWorld;
import allow.simulator.world.Weather;

/**
 * Represents context (i.e. world, time, a.s.o. shared by all entities) of 
 * the simulation.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class Context {
	// World including the StreetMap.
	private IWorld world;
		
	// Simulated time.
	private Time time;
	
	// Weather.
	private Weather weather;
	
	// Instances of available data services (for information about transportation network).
	private List<IDataService> dataServices;
	
	// Instances of available planner services (for journey planning).
	private List<IPlannerService> plannerServices;
	
	// Instance of FlexiBus planner service.
	private IPlannerService flexiBusService;
	
	// Statistics.
	private Statistics stats;
	
	// Ensembles.
	private EnsembleManager ensembleManager;
	
	// (Externally) provided simulation parameters.
	private SimulationParameter params;
	
	/**
	 * Constructor.
	 * Creates new context.
	 * 
	 * @param world World of this context.
	 * @param time Time of this context.
	 * @param dataServices Data services to be used.
	 * @param plannerService Planner services to be used.
	 * @param weather Current weather.
	 */
	public Context(IWorld world,
			Time time,
			List<IDataService> dataServices,
			List<IPlannerService> plannerServices,
			IPlannerService flexiBusService,
			Weather weather,
			Statistics stats,
			EnsembleManager ensembleManager,
			SimulationParameter params) {
		this.world = world;
		this.time = time;
		this.dataServices = dataServices;
		this.plannerServices = plannerServices;
		this.flexiBusService = flexiBusService;
		this.weather = weather;
		this.stats = stats;
		this.ensembleManager = ensembleManager;
		this.params = params;
	}
	
	/**
	 * Returns the world of the simulation.
	 * 
	 * @return World of the simulation.
	 */
	public IWorld getWorld() {
		return world;
	}
	
	/**
	 * Returns the time of the simulation.
	 * 
	 * @return Time of the simulation.
	 */
	public Time getTime() {
		return time;
	}
	
	/**
	 * Returns the data service instances used in the simulation.
	 * 
	 * @return Data service instance to be used in the simulation.
	 */
	public List<IDataService> getDataServices() {
		return dataServices;
	}
	
	/**
	 * Returns the planner service instances used in the simulation. 
	 * 
	 * @return Planner service instances to be used in the simulation.
	 */
	public List<IPlannerService> getPlannerServices() {
		return plannerServices;
	}
	
	/**
	 * Returns the FlexiBus planner service instance.
	 * 
	 * @return FlexiBus planner service instance.
	 */
	public IPlannerService getFlexiBusPlannerService() {
		return flexiBusService;
	}
	
	/**
	 * Returns the weather of the simulation.
	 * 
	 * @return Weather of the simulation.
	 */
	public Weather getWeather() {
		return weather;
	}
	
	/**
	 * Returns the statistics collection of the simulation.
	 *  
	 * @return Statistics collection of the simulation.
	 */
	public Statistics getStatistics() {
		return stats;
	}
	
	/**
	 * Returns the ensemble manager tracking existing ensembles.
	 * 
	 * @return Ensemble manager tracking existing ensembles in the simulation.
	 */
	public EnsembleManager getEnsembleManager() {
		return ensembleManager;
	}
	
	/**
	 * Returns collection of simulation parameters coming from the external
	 * simulation environment.
	 * 
	 * @return Simulation parameters.
	 */
	public SimulationParameter getSimulationParameters() {
		return params;
	}
}
