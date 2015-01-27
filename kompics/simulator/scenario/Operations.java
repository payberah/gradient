package kompics.simulator.scenario;


import java.math.BigInteger;
import java.util.Random;

import kompics.simulator.core.MSPeerFail;
import kompics.simulator.core.MSPeerJoin;

import se.sics.kompics.p2p.experiment.dsl.adaptor.Operation1;
import se.sics.kompics.p2p.experiment.dsl.adaptor.Operation2;

@SuppressWarnings("serial")
public class Operations {

//-------------------------------------------------------------------
	static Operation1<MSPeerJoin, BigInteger> msJoin(final Random random, final int max, final int downloadSlots) {
		return new Operation1<MSPeerJoin, BigInteger>() {
			public MSPeerJoin generate(BigInteger id) {
				int num = random.nextInt(max) + 1;
				return new MSPeerJoin(id, num, downloadSlots);
			}
		};
	}

//-------------------------------------------------------------------
	static Operation2<MSPeerJoin, BigInteger, BigInteger> msJoin(final int downloadSlots) {
		return new Operation2<MSPeerJoin, BigInteger, BigInteger>() {
			public MSPeerJoin generate(BigInteger num, BigInteger id) {
				return new MSPeerJoin(id, num.intValue(), downloadSlots);
			}
		};
	}
	
//-------------------------------------------------------------------
	static Operation1<MSPeerFail, BigInteger> msFail = new Operation1<MSPeerFail, BigInteger>() {
		public MSPeerFail generate(BigInteger id) {
			return new MSPeerFail(id);
		}
	};
}
