package allow.simulator.core;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Represents a setup configuration for the Allow Ensembles simulator.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class Configuration {
	// Starting date of the simulation.
	private LocalDateTime startingDate;
	
	// Configuration of planner service.
	private List<Service> plannerServiceConfiguration;
	private boolean allowParallelClientRequests;
	
	// Configuration of data service.
	private List<Service> dataServiceConfiguration;
	
	// Path to world file.
	private WorldConfiguration worldConfiguration;
		
	// Path to agent configuration file.
	private AgentConfiguration agentConfiguration;
	
	// EvoKnowledge configuration information.
	private EvoKnowledgeConfiguration evoConfiguration;
	
	// Path to logging output.
	private String loggingPath;
	
	// Path to input data sources.
	private String dataPath;
	
	/**
	 * Constructor.
	 * Creates a new configuration for the Allow Ensembles simulator.
	 * 
	 * @param dataPath Path to input data sources of simulation.
	 * @param startingDate Starting date of the simulation.
	 * @param plannerService Planner service configuration.
	 * @param dataService Data service configuration.
	 * @param loggingPath Path to file to write logging output to.
	 * @param worldPath Path containing simulated world.
	 * @throws ParseException 
	 */
	@JsonCreator
	public Configuration(@JsonProperty("datapath") String dataPath,
			@JsonProperty("startingdate") String startingDate, 
			@JsonProperty("plannerservice") List<Service> plannerServices,
			@JsonProperty("allowParallelClientRequests") boolean allowParallelClientRequests,
			@JsonProperty("dataservice") List<Service> dataServices,
			@JsonProperty("world") WorldConfiguration worldConfig,
			@JsonProperty("agents") AgentConfiguration agentConfig,
			@JsonProperty("evoknowledge") EvoKnowledgeConfiguration evoConfig,
			@JsonProperty("loggingpath") String loggingPath) throws ParseException {
		this.dataPath = dataPath;
		this.startingDate = LocalDateTime.parse(startingDate, DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm:ss", Locale.ITALY));
		this.plannerServiceConfiguration = plannerServices;
		this.allowParallelClientRequests = allowParallelClientRequests;
		this.dataServiceConfiguration = dataServices;
		this.worldConfiguration = worldConfig;
		this.agentConfiguration = agentConfig;
		this.evoConfiguration = evoConfig;
		this.loggingPath = loggingPath;
	}
	
	/**
	 * Returns the path to map file to use.
	 * 
	 * @return Path to map file to use.
	 */
	public Path getMapPath() {
		return Paths.get(dataPath, worldConfiguration.getMapFile());
	}
	
	public Path getLayerPath(String key) {
		String path = worldConfiguration.getLayerPath(key);
		return (path != null) ? Paths.get(dataPath, path) : null;
	}
	
	public Path getWeatherPath() {
		return Paths.get(dataPath, worldConfiguration.getWeatherFiles());
	}
	
	public Path getAgentConfigurationPath() {
		return Paths.get(dataPath, agentConfiguration.getAgentConfigurationFile());
	}
	
	public EvoKnowledgeConfiguration getEvoKnowledgeConfiguration() {
		return evoConfiguration;
	}
	
	public Path getTracesOutputPath() {
		return Paths.get(loggingPath, "traces");
	}
	
	/**
	 * Returns the starting date of the simulated world.
	 * 
	 * @return Starting date of the simulated world.
	 */
	public LocalDateTime getStartingDate() {
		return startingDate;
	}
	
	/**
	 * Returns the planner service configuration.
	 * 
	 * @return Planner service configuration.
	 */
	public List<Service> getPlannerServiceConfiguration() {
		return plannerServiceConfiguration;
	}
	
	/**
	 * Returns whether parallel client requests to the planner instances are
	 * possible or not. This flag is used for backwards compatibility of old
	 * OTP versions (< 0.11) which allow only one client to query a planner
	 * instance at a time.
	 * 
	 * @return True, if parallel client requests are possible, false otherwise.
	 */
	public boolean allowParallelClientRequests() {
		return allowParallelClientRequests;
	}
	
	/**
	 * Returns the data service configuration.
	 * 
	 * @return Data service configuration.
	 */
	public List<Service> getDataServiceConfiguration() {
		return dataServiceConfiguration;
	}
	
	/**
	 * Returns path to file to store logging output.
	 * 
	 * @return Path to file to store logging output.
	 */
	public Path getLoggingOutputPath() {
		return Paths.get(loggingPath);
	}
	
	/**
	 * Returns a new configuration from JSON file.
	 * 
	 * @param filePath File containing JSON description of configuration.
	 * @return Configuration read from JSON file.
	 * @throws IOException
	 */
	public static Configuration fromJSON(Path filePath) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(filePath.toFile(), Configuration.class);
	}
}
