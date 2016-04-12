package allow.simulator.ensemble;

import java.util.HashMap;
import java.util.Map;

import allow.simulator.entity.Entity;

public class EnsembleManager {

	public static final String FLEXIBUS_AGENCY_ENSEMBLE = "FlexiBusAgencyEnsemble";
	public static final String CARPOOLING_AGENCY_ENSEMBLE = "CarPoolingAgencyEnsemble";
	
	private Map<String, Ensemble> ensembles;
	
	public EnsembleManager() {
		ensembles = new HashMap<String, Ensemble>();
	}
	
	public Ensemble getEnsemble(String id) {
		return ensembles.get(id);
	}
	
	public Ensemble createEnsemble(String id, Entity creator) {
		Ensemble ensemble = ensembles.get(id);
		
		if (ensemble == null) {
			ensemble = new Ensemble(id, creator);
			ensembles.put(id, ensemble);
		}
		//System.out.println("Created ensemble \"" + id + "\"");
		return ensemble;
	}
	
	public void destroyEnsemble(String id) {
		ensembles.remove(id);
		//System.out.println("Destroyed ensemble \"" + id + "\"");
	}
}
