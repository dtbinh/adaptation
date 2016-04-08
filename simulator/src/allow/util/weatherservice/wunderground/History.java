package allow.util.weatherservice.wunderground;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class History {

	// Date historical weather data was recorded.
	private Date date;
	
	// List of observations.
	private List<Observation> observations;
	
	// List of summaries.
	public List<DailySummary> summaries;
	
	@JsonCreator
	public History(@JsonProperty("Date") Date date,
			@JsonProperty("observations") List<Observation> observations,
			@JsonProperty("dailysummary") List<DailySummary> summary) {
		this.date = date;
		this.observations = observations;
		this.summaries = summary;
	}
	
	public Date getDate() {
		return date;
	}
	
	public List<Observation> getObservations() {
		return observations;
	}
	
	public List<DailySummary> getDailySummaries() {
		return summaries;
	}
}
