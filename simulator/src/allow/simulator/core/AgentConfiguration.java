package allow.simulator.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AgentConfiguration {
	// Path to configuration file.
	private String url;
	
	@JsonCreator
	public AgentConfiguration(@JsonProperty("url") String url) {
		this.url = url;
	}
	
	public String getAgentConfigurationFile() {
		return url;
	}
}
