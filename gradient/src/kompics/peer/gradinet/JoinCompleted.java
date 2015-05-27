package kompics.peer.gradinet;

import kompics.peer.common.MSPeerAddress;
import se.sics.kompics.Event;

public class JoinCompleted extends Event {

	private final MSPeerAddress localPeer;

//-------------------------------------------------------------------
	public JoinCompleted(MSPeerAddress localPeer) {
		super();
		this.localPeer = localPeer;
	}

//-------------------------------------------------------------------
	public MSPeerAddress getLocalPeer() {
		return localPeer;
	}
}
