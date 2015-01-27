package kompics.simulator.snapshot;

import java.util.ArrayList;
import java.util.HashMap;

import kompics.peer.common.MSPeerAddress;

public class Snapshot {
	private static HashMap<MSPeerAddress, PeerInfo> peers = new HashMap<MSPeerAddress, PeerInfo>();
	private static int counter = 0;
	private static String FILENAME = "log.out";

//-------------------------------------------------------------------
	public static void init() {
		FileIO.write("", FILENAME);
	}

//-------------------------------------------------------------------
	public static void addPeer(MSPeerAddress address, int utilityValue, int bw, long birthTime) {
		peers.put(address, new PeerInfo(utilityValue, birthTime));
	}
	
//-------------------------------------------------------------------
	public static void removePeer(MSPeerAddress address) {
		peers.remove(address);
	}
	
//-------------------------------------------------------------------
	public static void updateViews(MSPeerAddress address, ArrayList<MSPeerAddress> similarView, ArrayList<MSPeerAddress> randomView) {
		PeerInfo peerInfo = peers.get(address);
		
		if (peerInfo == null)
			return;
		
		peerInfo.updateSimilarView(similarView);
		peerInfo.updateRandomView(randomView);
	}

//-------------------------------------------------------------------
	public static void report() {
		String str = new String();
		str += "current time: " + counter++ + "\n";
		str += reportNetworkState();
		str += reportDetailes();
		str += "###\n";
		
		System.out.println(str);
		
		FileIO.append(str, FILENAME);
	}

//-------------------------------------------------------------------
	private static String reportNetworkState() {
		PeerInfo peerInfo;
		String str = new String("---\n");
		int totalNumOfPeers = peers.size() - 1;
		int utilityValue = 0;
		
		for (MSPeerAddress peer : peers.keySet()) {
			peerInfo = peers.get(peer);
			utilityValue += peerInfo.getUtilityValue();
		}
			
		str += "total number of peers: " + totalNumOfPeers + "\n";
		str += "total utility value: " + utilityValue + "\n";
				
		return str;		
	}
	
//-------------------------------------------------------------------
	private static String reportDetailes() {
		PeerInfo peerInfo;
		String str = new String("---\n");

		for (MSPeerAddress peer : peers.keySet()) {
			peerInfo = peers.get(peer);
		
			str += "peer: " + peer;
			str += ", utility value: " + peerInfo.getUtilityValue();
			str += ", similar view: " + peerInfo.getSimilarView();
			str += ", random view: " + peerInfo.getRandomView();
			str += "\n";
		}
		
		return str;
	}

}
