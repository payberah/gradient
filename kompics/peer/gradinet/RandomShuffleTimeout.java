package kompics.peer.gradinet;

import kompics.peer.common.MSPeerAddress;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

public class RandomShuffleTimeout extends Timeout {

	private final MSPeerAddress peer;

//-------------------------------------------------------------------
	public RandomShuffleTimeout(ScheduleTimeout request, MSPeerAddress peer) {
		super(request);
		this.peer = peer;
	}

//-------------------------------------------------------------------
	public MSPeerAddress getPeer() {
		return peer;
	}
}
