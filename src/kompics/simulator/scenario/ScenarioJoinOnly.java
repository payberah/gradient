package kompics.simulator.scenario;


import java.util.Random;

import se.sics.kompics.p2p.experiment.dsl.SimulationScenario;

@SuppressWarnings("serial")
public class ScenarioJoinOnly extends Scenario {
	private static SimulationScenario scenario = new SimulationScenario() {{
		final Random random = getRandom();
		final UniformDistribution distro = new UniformDistribution(random);

		StochasticProcess process1 = new StochasticProcess() {{
			eventInterArrivalTime(exponential(10));
			raise(1, Operations.msJoin(10), distro, uniform(13));
		}};
		
		StochasticProcess process2 = new StochasticProcess() {{
			eventInterArrivalTime(exponential(10));
			raise(50, Operations.msJoin(10), distro, uniform(13));
		}};

		process1.start();
		process2.startAfterTerminationOf(2000, process1);
	}};
	
//-------------------------------------------------------------------
	public ScenarioJoinOnly() {
		super(scenario);
	} 
}
