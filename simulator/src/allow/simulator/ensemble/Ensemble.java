package allow.simulator.ensemble;

import java.util.ArrayList;
import java.util.List;

import allow.simulator.collective.adaptation.DemonstratorAnalyzer;
import allow.simulator.collective.adaptation.DummyExecution;
import allow.simulator.collective.adaptation.api.CollectiveAdaptationEnsemble;
import allow.simulator.collective.adaptation.api.CollectiveAdaptationProblem;
import allow.simulator.collective.adaptation.api.CollectiveAdaptationRole;
import allow.simulator.entity.Entity;

public class Ensemble {

    private String id;
    private String ensembleName;

    private Entity creator;
    private List<Entity> participants;

    public Ensemble(String id, Entity creator) {
	this.id = id;
	this.creator = creator;
	participants = new ArrayList<Entity>();
    }

    public Ensemble(String ensembleName, List<Entity> roles) {
	super();
	ensembleName = ensembleName;
	participants = roles;
    }

    public void join(Entity newEntity) {
	// System.out.println(newEntity + " joined ensemble + " + id);
    }

    public void leave(Entity entity) {
	// System.out.println(entity + " left ensemble + " + id);
    }

    public int getNumberOfParticipants() {
	return participants.size();
    }

    public Entity getCreator() {
	return creator;
    }

    public String getID() {
	return id;
    }

    public String getEnsembleName() {
	return ensembleName;
    }

    public void setEnsembleName(String ensembleName) {
	this.ensembleName = ensembleName;
    }

    public void solveIssue(String IssueType) {
	DemonstratorAnalyzer demo = new DemonstratorAnalyzer();

	List<CollectiveAdaptationEnsemble> ensembles = new ArrayList<CollectiveAdaptationEnsemble>();
	List<CollectiveAdaptationRole> roles = new ArrayList<CollectiveAdaptationRole>();

	// creation of Entities

	CollectiveAdaptationEnsemble routeA = new CollectiveAdaptationEnsemble("RouteA", roles);
	ensembles.add(routeA);

	CollectiveAdaptationProblem cap = new CollectiveAdaptationProblem("CAP_1", ensembles, IssueType,
		"FlexibusDriver_13", ensembles.get(0).getEnsembleName(), "RoutePassenger_36");

	// CollectiveAdaptationSolution cas = demo.executeCap(cap, new
	// DummyExecution());
	demo.executeCap(cap, new DummyExecution());
    }

}
