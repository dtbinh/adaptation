package allow.simulator.collective.adaptation.ek;

import java.util.List;

/**
 * Implements learning and prediction functionalities of Allow Ensembles
 * Evolutionary Knowledge.
 *
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class EvoKnowledge {

    /**
     * Initializes the underlying database for EvoKnowledge.
     *
     * @param config
     *            Configuration specifying the database to use and possibly
     *            necessary credentials
     * @param prefix
     *            Configurable additional prefix for tables which will be
     *            created during the learning process. Can be null or empty.
     */
    public static void initialize(EKConfiguration config, String prefix, boolean deletePrevious) {
	if (config == null)
	    throw new IllegalArgumentException("Error: config must not be null.");

	if (prefix == null)
	    prefix = "";

	DBConnector.init(config, prefix, deletePrevious);
    }

    /**
     * Returns a new instance of EvoKnowledge.Make sure to call initialize
     * method before creating an EvoKnowledge instance. Otherwise an exception
     * is thrown.
     */
    public static EvoKnowledge createEvoKnowledge() {
	if (!DBConnector.isInitialized())
	    throw new IllegalStateException("Error: EvoKnowledge is not initialized.");

	return new EvoKnowledge();
    }

    /**
     * Updates the EvoKnowledge instance given a new set of experiences.
     *
     * @return True, if learning was successful, false otherwise.
     */
    public boolean learn(List<Experience> parameter) {
	if (parameter == null)
	    throw new IllegalArgumentException("Error: List of parameters must not be null.");

	return DBConnector.addEntry("", parameter);
    }

    /**
     * Predicts the values for a certain query.
     *
     * @param parameter
     *            Set of observed (context) parameters
     * @return Predicted parameters
     */
    public List<Prediction> predict(Query parameter) {
	if (parameter == null)
	    throw new IllegalArgumentException("Error: Parameters must not be null.");

	return DBConnector.getPredictedParameters("", parameter);
    }

    /**
     * Cleans the current model removing all available experiences and
     * knowledge.
     */
    public void cleanModel() {
	DBConnector.cleanModel("");
    }
}
