package kompics.simulator.core;

import se.sics.kompics.PortType;
import se.sics.kompics.p2p.experiment.dsl.events.TerminateExperiment;

public class MSSimulatorPort extends PortType {{
	positive(MSPeerJoin.class);
	positive(MSPeerFail.class);
	negative(TerminateExperiment.class);
}}
