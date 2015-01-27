package kompics.simulator.snapshot;


import java.util.ArrayList;

import kompics.peer.common.MSPeerAddress;

public class PeerInfo {
	private long birthTime;
	private int utilityValue;
	private ArrayList<MSPeerAddress> similarView;
	private ArrayList<MSPeerAddress> randomView;

//-------------------------------------------------------------------
	public PeerInfo(int utilityValue, long birthTime) {
		this.birthTime = birthTime;
		this.utilityValue = utilityValue;
	}

//-------------------------------------------------------------------
	public void updateSimilarView(ArrayList<MSPeerAddress> similarView) {
		this.similarView = similarView;
	}

//-------------------------------------------------------------------
	public void updateRandomView(ArrayList<MSPeerAddress> randomView) {
		this.randomView = randomView;
	}

//-------------------------------------------------------------------
	public ArrayList<MSPeerAddress> getSimilarView() {
		return this.similarView;
	}

//-------------------------------------------------------------------
	public ArrayList<MSPeerAddress> getRandomView() {
		return this.randomView;
	}
	
//-------------------------------------------------------------------
	public long getBirthTime() {
		return this.birthTime;
	}

//-------------------------------------------------------------------
	public int getUtilityValue() {
		return this.utilityValue;
	}
}
