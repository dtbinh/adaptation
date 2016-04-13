package allow.simulator.collective.adaptation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.mxgraph.analysis.mxAnalysisGraph;
import com.mxgraph.analysis.mxTraversal;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraph.mxICellVisitor;

import allow.simulator.collective.adaptation.ahp.Ranking;
import allow.simulator.collective.adaptation.api.CollectiveAdaptationProblem;
import allow.simulator.collective.adaptation.api.CollectiveAdaptationSolution;
import allow.simulator.collective.adaptation.ek.EvoKnowledge;
import allow.simulator.collective.adaptation.ensemble.Analyzer;
import allow.simulator.collective.adaptation.ensemble.Ensemble;
import allow.simulator.collective.adaptation.ensemble.Role;
import allow.simulator.collective.adaptation.model.IssueCommunication;
import allow.simulator.collective.adaptation.model.IssueResolution;
import allow.simulator.collective.adaptation.presentation.CATree;
import allow.simulator.collective.adaptation.presentation.CAWindow;
import allow.simulator.collective.adaptation.presentation.RankedSolution;
import allow.simulator.collective.adaptation.utils.UtilityManager;

public class EnsembleManager {
    private static int distance = 0;
    private Ensemble ensemble;

    private EvoKnowledge ek;
    private CollectiveAdaptationSolution capSolution;

    public CollectiveAdaptationSolution getCapSolution() {
	return capSolution;
    }

    public void setCapSolution(CollectiveAdaptationSolution capSolution) {
	this.capSolution = capSolution;
    }

    private Ranking rk;
    private UtilityManager um;

    public EvoKnowledge getEk() {
	return ek;
    }

    public void setEk(EvoKnowledge ek) {
	this.ek = ek;
    }

    private int IssueResolutionCount = 0;

    public int getNumOfDecisionPoints() {
	return NumOfDecisionPoints;
    }

    private int NumOfDecisionPoints = 0;

    private ConcurrentHashMap<IssueResolution, List<IssueCommunication>> issueCommunications;

    private HashMap<String, List<RankedSolution>> decisionPointSolutions;

    // private Multimap<IssueResolution, List<IssueCommunication>>
    // issueCommunications;

    private HashMap<RoleManager, Preferences> RolePreferences;

    public HashMap<RoleManager, Preferences> getRolePreferences() {
	return RolePreferences;
    }

    public void setRolePreferences(HashMap<RoleManager, Preferences> rolePreferences) {
	RolePreferences = rolePreferences;
    }

    public HashMap<String, List<RankedSolution>> getDecisionPointSolutions() {
	return decisionPointSolutions;
    }

    private ArrayList<IssueResolution> activeIssueResolutions;

    private ArrayList<RoleManager> rolesManagers;

    private int abortedResolutions;

    // private HashMap<IssueResolution, List<IssueCommunication>>
    // issueCommunications;
    public void updateDecisionPoint() {
	this.NumOfDecisionPoints++;
    }

    public void addRolePreferences(RoleManager roleManager, Preferences preferences) {
	if (this.RolePreferences == null) {
	    this.RolePreferences = new HashMap<RoleManager, Preferences>();
	    this.RolePreferences.put(roleManager, preferences);
	} else {
	    this.RolePreferences.put(roleManager, preferences);
	}

    }

    public void AddRelations(IssueResolution ir, List<IssueCommunication> communications) {
	this.issueCommunications.put(ir, communications);
    }

    public ConcurrentHashMap<IssueResolution, List<IssueCommunication>> getIssueCommunications() {
	return issueCommunications;
    }

    public void setIssueResolutionsRelations(
	    ConcurrentHashMap<IssueResolution, List<IssueCommunication>> issueCommunications) {
	if (this.issueCommunications == null) {
	    this.issueCommunications = new ConcurrentHashMap<IssueResolution, List<IssueCommunication>>();
	} else {
	    this.issueCommunications = issueCommunications;
	}
    }

    public int getAbortedResolutions() {
	return abortedResolutions;
    }

    public void setAbortedResolutions(int abortedResolutions) {
	this.abortedResolutions = abortedResolutions;
    }

    public ArrayList<RoleManager> getRolesManagers() {
	return rolesManagers;
    }

    public void setRolesManagers(ArrayList<RoleManager> rolesManagers) {
	this.rolesManagers = rolesManagers;
    }

    public ArrayList<IssueResolution> getActiveIssueResolutions() {
	return activeIssueResolutions;
    }

    public Ensemble getEnsemble() {
	return ensemble;
    }

    public void setEnsemble(Ensemble ensemble) {
	this.ensemble = ensemble;
    }

    public void setActiveIssueResolutions(ArrayList<IssueResolution> activeIssueResolutions) {
	this.activeIssueResolutions = activeIssueResolutions;

	// associate each issue at the related RoleManager
	for (int i = 0; i < this.activeIssueResolutions.size(); i++) {
	    IssueResolution currentResolution = this.activeIssueResolutions.get(i);
	    RoleManager rm = currentResolution.getRoleCurrent();
	    rm.addIssueResolution(currentResolution);
	}
    }

    public EnsembleManager(Ensemble ensemble) {
	this.ensemble = ensemble;

	// create all the route managers
	for (int i = 0; i < ensemble.getRole().size(); i++) {
	    Role role = ensemble.getRole().get(i);
	    RoleManager manager = new RoleManager(role);
	    this.addRoleManager(manager);
	    // relate the manager to the specific ensemble
	    manager.setEnsemble(this);
	}

    }

    private HashMap<String, CATree> trees = new HashMap<String, CATree>();
    private HashMap<String, String> RoleNodeIssueRelations = new HashMap<String, String>();

    // private HashMultimap<Pair<String, RoleManager>, CATree> trees = new
    // HashMap<RoleManager, CATree>();

    public HashMap<String, String> getRoleNodeIssueRelations() {
	return RoleNodeIssueRelations;
    }

    public void setRoleNodeIssueRelations(HashMap<String, String> roleNodeIssueRelations) {

	if (RoleNodeIssueRelations == null) {
	    // System.out.println("trees must be not null");
	    return;
	}
	this.RoleNodeIssueRelations = roleNodeIssueRelations;
    }

    public void addDecisionPoints(String or, List<RankedSolution> predictions) {
	if (this.decisionPointSolutions == null) {
	    this.decisionPointSolutions = new HashMap<String, List<RankedSolution>>();
	    this.decisionPointSolutions.put(or, predictions);
	} else {
	    this.decisionPointSolutions.put(or, predictions);
	}
    }

    public void addRoleIssueRelation(String role, String IssueRow) {
	RoleNodeIssueRelations.put(role, IssueRow);
    }

    public HashMap<String, CATree> getTrees() {
	return trees;
    }

    public void setTrees(HashMap<String, CATree> trees) {
	if (trees == null) {
	    // System.out.println("trees must be not null");
	    return;
	}

	this.trees = trees;
    }

    public RoleManager getRolebyType(String type) {
	RoleManager role = null;
	for (int i = 0; i < this.getEnsemble().getRole().size(); i++) {
	    RoleManager current = this.getRolesManagers().get(i);
	    if (current.getRole().getType().equalsIgnoreCase(type)) {
		role = current;
		break;
	    }
	}
	return role;

    }

    public void updateNumOfIssueResolutions() {
	this.IssueResolutionCount++;
    }

    public void checkIssues(CollectiveAdaptationProblem cap, String capID, CAWindow window,
	    List<EnsembleManager> ensembles, CollectiveAdaptationSolution solution, int issueIndex,
	    CATree hierarchyTree) {

	if (this.trees == null) {
	    this.trees = new HashMap<String, CATree>();
	}

	if (AllChecked()) {
	    return;
	} else {

	    Iterator<IssueResolution> iterate = this.getIssueCommunications().keySet().iterator();

	    while (iterate.hasNext()) {
		IssueResolution ir = iterate.next();

		RoleManager currentRoleManager = ir.getRoleSource();
		CATree currentTree;

		if (this.getTrees().get(currentRoleManager) == null) {
		    currentTree = new CATree();
		} else {
		    currentTree = this.getTrees().get(currentRoleManager);

		}

		Analyzer an = currentRoleManager.getAnalyzer();
		an.resolveIssue(cap, capID, ir, this, window, currentTree, ensembles, solution, issueIndex,
			hierarchyTree);

	    }

	    this.checkIssues(cap, capID, window, ensembles, solution, issueIndex, hierarchyTree);
	}

	// System.out.println("FINE WHILE");

    }

    private boolean AllChecked() {
	boolean result = true;
	Set keySet = this.getIssueCommunications().keySet();
	Iterator keyIterator = keySet.iterator();
	while (keyIterator.hasNext()) {
	    IssueResolution ir = (IssueResolution) keyIterator.next();
	    if (!(ir.getStatus().equals("End"))) {

		result = false;
	    }

	}

	System.out.println("RESULT: " + result);
	return result;

    }

    public void addRoleManager(RoleManager manager) {

	if (this.rolesManagers == null) {
	    this.rolesManagers = new ArrayList<RoleManager>();
	    this.rolesManagers.add(manager);
	} else {
	    this.rolesManagers.add(manager);
	}

    }

    public void addIssueResolution(IssueResolution ir) {
	if (this.activeIssueResolutions == null) {
	    this.activeIssueResolutions = new ArrayList<IssueResolution>();
	    this.activeIssueResolutions.add(ir);
	} else {
	    this.activeIssueResolutions.add(ir);
	}
    }

    /* max depth */
    public int grapDepth(CATree cat) {
	// build graph
	distance = 0;
	mxGraph graph = new mxGraph();

	mxAnalysisGraph aGraph = new mxAnalysisGraph();
	aGraph.setGraph(cat);

	// apply dfs to find depth of a tree
	mxTraversal.dfs(aGraph, cat.getFirstNode(), new mxICellVisitor() {

	    @Override
	    public boolean visit(Object vertex, Object edge) {
		mxCell v = (mxCell) vertex;
		mxCell e = (mxCell) edge;
		String eVal = "N/A";

		if (e != null) {
		    if (e.getValue() == null) {
			eVal = "1.0";
		    } else {
			eVal = e.getValue().toString();
		    }
		}

		if (!eVal.equals("N/A")) {
		    distance = distance + 1;
		}

		// System.out.print("(v: " + v.getValue() + " e: " + eVal +
		// ")");

		return false;
	    }
	});
	return distance;

    }

    public void setCommunicationsRelations(IssueResolution res, List<IssueCommunication> communications) {

	if (this.issueCommunications == null) {
	    this.issueCommunications = new ConcurrentHashMap<IssueResolution, List<IssueCommunication>>();
	    this.issueCommunications.put(res, communications);
	} else {
	    this.issueCommunications.put(res, communications);
	}

    }

    public int getIssueResolutionCount() {
	return IssueResolutionCount;
    }

    public void setIssueResolutionCount(int issueResolutionCount) {
	IssueResolutionCount = issueResolutionCount;
    }

    /*
     * public RoleManager searchFather(IssueResolution res) { RoleManager father
     * = null;
     * 
     * System.out.println(this.getIssueCommunications().size()); Iterator it =
     * this.getIssueCommunications().entrySet().iterator(); while (it.hasNext())
     * { Map.Entry pair = (Map.Entry) it.next();
     * 
     * System.out.println(pair.getKey() + " = " + pair.getValue()); it.remove();
     * // avoids a ConcurrentModificationException } return father; }
     */

    public void updateRelations(IssueResolution res, ArrayList<IssueCommunication> communications) {
	if (this.getIssueCommunications() == null) {
	    ConcurrentHashMap<IssueResolution, List<IssueCommunication>> issueComs = new ConcurrentHashMap<IssueResolution, List<IssueCommunication>>();
	    issueComs.put(res, communications);
	} else {

	    Iterator<IssueResolution> iterate = this.getIssueCommunications().keySet().iterator();

	    Iterator it = this.getIssueCommunications().entrySet().iterator();
	    while (it.hasNext()) {
		ConcurrentHashMap.Entry pair = (ConcurrentHashMap.Entry) it.next();
		IssueResolution current = (IssueResolution) pair.getKey();
		System.out.println("Issue Relation ID: " + current.getIssueResolutionID());
		if (current.getIssueResolutionID() == res.getIssueResolutionID()) {

		    pair.setValue(communications);

		}
	    }
	}
    }

    public CollectiveAdaptationSolution getSolution() {
	// TODO Auto-generated method stub
	return null;
    }

}
