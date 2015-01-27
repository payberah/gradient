package kompics.main;

import kompics.simulator.scenario.Scenario;
import kompics.simulator.scenario.ScenarioJoinOnly;

public class Main {
	public static void main(String[] args) throws Throwable {
		Configuration configuration = new Configuration();
		configuration.set();
		
		Scenario scenario = new ScenarioJoinOnly();
		scenario.setSeed(0);
		scenario.simulate();
	}
}
