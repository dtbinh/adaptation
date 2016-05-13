package allow.simulator.netlogo.commands;

import org.nlogo.api.DefaultClassManager;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.PrimitiveManager;


public class ExtensionManager extends DefaultClassManager {

	public static final String EXTENSION_NAME = "allow";
	
	@Override
	public void load(PrimitiveManager primitiveManager) throws ExtensionException {
		primitiveManager.addPrimitive("create-persons", new CreatePersons());
		primitiveManager.addPrimitive("execute-person", new ExecutePerson());
		primitiveManager.addPrimitive("exchange-knowledge", new ExchangeKnowledge());
		primitiveManager.addPrimitive("update-grid", new UpdateGrid());
		primitiveManager.addPrimitive("execute-bus", new ExecuteBus());
		primitiveManager.addPrimitive("show-activity", new ShowActivity());
		primitiveManager.addPrimitive("show-segments", new ShowSegments());
		primitiveManager.addPrimitive("showNotAtHome", new ShowNotAtHome());
		primitiveManager.addPrimitive("execute-transport-agency", new ExecuteTransportAgencies());
		primitiveManager.addPrimitive("setup-simulator", new SetupSimulator());
		primitiveManager.addPrimitive("shutdown-simulator", new ShutDownSimulator());
		primitiveManager.addPrimitive("tick", new Tick());
		primitiveManager.addPrimitive("get-regions", new GetRegions());
		primitiveManager.addPrimitive("get-region", new GetRegion());
		primitiveManager.addPrimitive("get-person-roles", new GetPersonRoles());
		primitiveManager.addPrimitive("get-person-role", new GetPersonRole());
		primitiveManager.addPrimitive("test-taxi", new TestTaxi());
		primitiveManager.addPrimitive("test-shared-taxi", new TestSharedTaxi());
	}
	
	@Override
    public java.util.List<String> additionalJars() {
		return java.util.Arrays.asList( new String[] {
            "httpclient-4.3.2.jar",
            "httpclient-cache-4.3.2.jar",
            "httpcore-4.3.1.jar"
			});
    }

}
