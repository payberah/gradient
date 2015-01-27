package kompics.simulator.core;

import java.math.BigInteger;

import se.sics.kompics.Event;

public final class MSPeerJoin extends Event {

	private final int uploadSlots;
	private final int downloadSlots;
	private final BigInteger msPeerId;

//-------------------------------------------------------------------	
	public MSPeerJoin(BigInteger msPeerId, int uploadSlots, int downloadSlots) {
		this.msPeerId = msPeerId;
		this.uploadSlots = uploadSlots;
		this.downloadSlots = downloadSlots;
	}

//-------------------------------------------------------------------	
	public BigInteger getMSPeerId() {
		return msPeerId;
	}

//-------------------------------------------------------------------	
	public int getUploadSlots() {
		return this.uploadSlots;
	}

//-------------------------------------------------------------------	
	public int getDownloadSlots() {
		return this.downloadSlots;
	}
	
//-------------------------------------------------------------------	
	@Override
	public String toString() {
		return "Join@" + this.msPeerId + "(" + this.downloadSlots +", " + this.uploadSlots + ")";
	}
}
