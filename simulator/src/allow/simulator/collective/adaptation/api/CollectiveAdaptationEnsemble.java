package allow.simulator.collective.adaptation.api;

import java.util.List;

public class CollectiveAdaptationEnsemble {

    private String EnsembleName;
    private List<CollectiveAdaptationRole> Roles;

    public List<CollectiveAdaptationRole> getRoles() {
	return Roles;
    }

    public void setRoles(List<CollectiveAdaptationRole> roles) {
	Roles = roles;
    }

    public String getEnsembleName() {
	return EnsembleName;
    }

    public void setEnsembleName(String ensembleName) {
	EnsembleName = ensembleName;
    }

    public CollectiveAdaptationEnsemble(String ensembleName, List<CollectiveAdaptationRole> roles) {
	super();
	EnsembleName = ensembleName;
	Roles = roles;
    }

}
