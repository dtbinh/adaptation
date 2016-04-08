package allow.simulator.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EvoKnowledgeConfiguration {

	private String modelPath;
	private String modelName;
	private String user;
	private String password;
	
	@JsonCreator
	public EvoKnowledgeConfiguration(@JsonProperty("modelPath") String modelPath,
			@JsonProperty("modelName") String modelName,
			@JsonProperty("user") String user,
			@JsonProperty("password") String password) {
		this.modelPath = modelPath;
		this.modelName = modelName;
		this.user = user;
		this.password = password;
	}
	
	public String getModelPath() {
		return modelPath;
	}

	public String getModelName() {
		return modelName;
	}
	
	public String getUser() {
		return user;
	}
	
	public String getPassword() {
		return password;
	}
}
