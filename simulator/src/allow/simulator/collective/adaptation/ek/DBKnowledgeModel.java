package allow.simulator.collective.adaptation.ek;

import java.util.List;

public interface DBKnowledgeModel {

    /**
     * Adds a new entry to this EK database model.
     * 
     * @param agentId
     *            Id of the agent table
     * @param experiences
     *            List of experiences to add to the model instance
     * @param tablePrefix
     *            Additional table prefix
     * @return
     */
    boolean addEntry(String agentId, List<Experience> experiences, String tablePrefix);

    List<Prediction> getPredictedParameters(String agentId, Query query, String tablePrefix);

    void clean(String agentId, String tablePrefix);

}
