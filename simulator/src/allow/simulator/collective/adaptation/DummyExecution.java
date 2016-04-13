package allow.simulator.collective.adaptation;

import allow.simulator.collective.adaptation.api.CollectiveAdaptationCommandExecution;
import allow.simulator.collective.adaptation.api.RoleCommand;

public class DummyExecution implements CollectiveAdaptationCommandExecution {

    @Override
    public void applyCommand(String ensemble, RoleCommand command) {
	// TODO Auto-generated method stub
	System.out.println(command);

    }

    @Override
    public void endCommand() {
	// TODO Auto-generated method stub
	System.out.println("End Command");

    }

}
