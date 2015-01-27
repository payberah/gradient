package kompics.peer.gradinet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import kompics.peer.common.MSPeerAddress;
import kompics.peer.common.MSPeerDescriptor;
import kompics.simulator.scenario.UniformDistribution;

public class Fingers {
	private final MSPeerAddress self;
	
	private final int utilityLevel;

	private HashMap<Integer, MSPeerDescriptor> fingers;

//-------------------------------------------------------------------
	public Fingers(MSPeerAddress self, int utilityValue) {
		this.self = self;
		this.utilityLevel = UniformDistribution.getUtilityLevel(utilityValue);

		this.fingers = new HashMap<Integer, MSPeerDescriptor>();
		
		int highestLevel = UniformDistribution.getHighestUtilityLevel();
		
		for (int i = this.utilityLevel + 1; i <= highestLevel; i++)
			this.fingers.put(i, null);
		
	}

//-------------------------------------------------------------------
	public synchronized void updateFingers(ArrayList<MSPeerDescriptor> randomDescriptors) {
		ArrayList<MSPeerDescriptor> peerLevelList = new ArrayList<MSPeerDescriptor>();
		
		for (Integer level : this.fingers.keySet()) {
			for (MSPeerDescriptor descriptor : randomDescriptors) {			
				if (self.equals(descriptor.getMSPeerAddress()) || 
					descriptor.getUtilityLevel() == 0 || 
					descriptor.getUtilityLevel() != level)
					continue;
				
				peerLevelList.add(descriptor);
			}
			
			if (peerLevelList.size() > 0) {
				Collections.shuffle(peerLevelList);
				this.fingers.put(level, peerLevelList.get(0));
				peerLevelList.clear();
			}

		}
	}

//-------------------------------------------------------------------
	public synchronized void removeFinger(int utilityLevel) {
		this.fingers.put(utilityLevel, null);
	}

//-------------------------------------------------------------------
	public final synchronized HashMap<Integer, MSPeerDescriptor> getLevelFingers() {
		return this.fingers;
	}

//-------------------------------------------------------------------
	public final synchronized ArrayList<MSPeerDescriptor> getFingers() {
		return new ArrayList<MSPeerDescriptor>(this.fingers.values());
	}

//-------------------------------------------------------------------
	public final synchronized MSPeerDescriptor getFinger(int utilityLevel) {
		return this.fingers.get(utilityLevel);
	}
}
