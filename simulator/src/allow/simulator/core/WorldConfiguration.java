package allow.simulator.core;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Utility class to load/store configuration aspects of the simulated world.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class WorldConfiguration {
	// Path to world map. 
	private String map;
	
	// List of layers to be added to the map.
	private Map<String, String> layer;
	
	// Weather model.
	private String weather;
	
	/**
	 * Constructor.
	 * Creates a new world configuration providing paths to the map to use,
	 * the layers to add to the map, and the weather model.
	 * 
	 * @param mapPath Path to map to use.
	 * @param layerPaths Layers to be added to the map.
	 * @param weatherPath Weather model to simulate.
	 */
	@JsonCreator
	public WorldConfiguration(@JsonProperty("map") String mapPath,
			@JsonProperty("layer") Map<String, String> layerPaths,
			@JsonProperty("weather") String weatherPath) {
		map = mapPath;
		layer = layerPaths;
		weather = weatherPath;
	}
	
	/**
	 * Returns the path to the file containing the street map of the
	 * simulation.
	 * 
	 * @return Path to file containing the street map of the simulation.
	 */
	public String getMapFile() {
		return map;
	}
	
	/**
	 * Returns path to a file describing the layer specified by parameter key to
	 * be add to the map e.g. partitioning of map into residential and working
	 * areas.
	 * 
	 * @return Path to file containing layer associated with given key, null
	 * otherwise.
	 */
	public String getLayerPath(String key) {
		return layer.containsKey(key) ? layer.get(key) : null;
	}
	
	/**
	 * Returns the path to the weather model of the simulation.
	 * 
	 * @return Path to weather model of the simulation.
	 */
	public String getWeatherFiles() {
		return weather;
	}
}
