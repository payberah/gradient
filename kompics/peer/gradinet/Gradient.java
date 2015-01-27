package kompics.peer.gradinet;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

import kompics.peer.common.MSPeerAddress;
import kompics.peer.common.MSPeerDescriptor;
import kompics.peer.mspeers.MessagePort;
import kompics.simulator.snapshot.Snapshot;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.timer.CancelTimeout;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;

public final class Gradient extends ComponentDefinition {

	Negative<PeerSamplingPort> psPort = negative(PeerSamplingPort.class);
	Positive<MessagePort> messagePort = positive(MessagePort.class);
	Positive<Timer> timerPort = positive(Timer.class);

	private MSPeerAddress self;

	private int shuffleLength;
	private long shufflePeriod;
	private long shuffleTimeout;

	private RandomView randomView;
	private SimilarView similarView;
	private Fingers fingers;
	
	private int utilityValue;
	
	private boolean joining;

	private HashMap<UUID, MSPeerAddress> outstandingRandomShuffles;
	private HashMap<UUID, MSPeerAddress> outstandingSimilarShuffles;

//-------------------------------------------------------------------	
	public Gradient() {
		outstandingRandomShuffles = new HashMap<UUID, MSPeerAddress>();
		outstandingSimilarShuffles = new HashMap<UUID, MSPeerAddress>();

		subscribe(handleInit, control);
		subscribe(handleJoin, psPort);
		subscribe(handleInitiateShuffle, timerPort);
		subscribe(handleRandomShuffleTimeout, timerPort);
		subscribe(handleSimilarShuffleTimeout, timerPort);
		subscribe(handleRandomShuffleRequest, messagePort);
		subscribe(handleRandomShuffleResponse, messagePort);
		subscribe(handleSimilarShuffleRequest, messagePort);
		subscribe(handleSimilarShuffleResponse, messagePort);
	}

//-------------------------------------------------------------------	
	Handler<GradientInit> handleInit = new Handler<GradientInit>() {
		public void handle(GradientInit init) {
			shuffleLength = init.getConfiguration().getShuffleLength();
			shufflePeriod = init.getConfiguration().getShufflePeriod();
			shuffleTimeout = init.getConfiguration().getShuffleTimeout();
			randomView = init.getRandomView();
			similarView = init.getSimilarView();
			fingers = init.getFingers();
			utilityValue = init.getUtilityValue();
		}
	};

//-------------------------------------------------------------------	
	Handler<Join> handleJoin = new Handler<Join>() {
		public void handle(Join event) {
			self = event.getSelf();

			LinkedList<MSPeerAddress> insiders = event.getCyclonInsiders();

			if (insiders.size() == 0) {
				// I am the first peer
				trigger(new JoinCompleted(self), psPort);

				// schedule shuffling
				SchedulePeriodicTimeout spt = new SchedulePeriodicTimeout(shufflePeriod, shufflePeriod);
				spt.setTimeoutEvent(new InitiateShuffle(spt));
				trigger(spt, timerPort);
				return;
			}

			MSPeerAddress peer = insiders.poll();
			initiateShuffle(1, peer, peer);
			joining = true;
		}
	};

//-------------------------------------------------------------------	
	private void initiateShuffle(int shuffleSize, MSPeerAddress randomPeer, MSPeerAddress similarPeer) {
		// send the random view to a random peer
		ArrayList<MSPeerDescriptor> randomDescriptors = randomView.selectToSendAtActive(shuffleSize - 1, randomPeer);
		randomDescriptors.add(new MSPeerDescriptor(self, utilityValue));
		DescriptorBuffer randomBuffer = new DescriptorBuffer(self, randomDescriptors);
		
		ScheduleTimeout rst = new ScheduleTimeout(shuffleTimeout);
		rst.setTimeoutEvent(new RandomShuffleTimeout(rst, randomPeer));
		UUID rTimeoutId = rst.getTimeoutEvent().getTimeoutId();

		outstandingRandomShuffles.put(rTimeoutId, randomPeer);
		RandomShuffleRequest rRequest = new RandomShuffleRequest(rTimeoutId, randomBuffer, self, randomPeer);

		trigger(rst, timerPort);
		trigger(rRequest, messagePort);

		// send the similar view to a similar peer
		if (similarView.getAll().size() == 0 && randomView.getAll().size() > 0) {
			similarView.selectToKeep(randomView.getAll());
		}
		
		if (similarPeer != null) {
			ArrayList<MSPeerDescriptor> similarDescriptors = similarView.selectToSendAtActive(shuffleSize - 1, similarPeer);
			similarDescriptors.add(new MSPeerDescriptor(self, utilityValue));		
			DescriptorBuffer similarBuffer = new DescriptorBuffer(self, similarDescriptors);
	
			ScheduleTimeout sst = new ScheduleTimeout(shuffleTimeout);
			sst.setTimeoutEvent(new SimilarShuffleTimeout(sst, similarPeer));
			UUID sTimeoutId = sst.getTimeoutEvent().getTimeoutId();
		
			outstandingSimilarShuffles.put(sTimeoutId, similarPeer);
			SimilarShuffleRequest sRequest = new SimilarShuffleRequest(sTimeoutId, similarBuffer, self, similarPeer);
		
			trigger(sst, timerPort);
			trigger(sRequest, messagePort);
		}		
		
		fingers.updateFingers(randomDescriptors);
	}

//-------------------------------------------------------------------	
	Handler<InitiateShuffle> handleInitiateShuffle = new Handler<InitiateShuffle>() {
		public void handle(InitiateShuffle event) {
			randomView.incrementDescriptorAges();
			similarView.incrementDescriptorAges();
			
			MSPeerAddress randomPeer = randomView.selectPeerToShuffleWith();
			MSPeerAddress similarPeer = similarView.selectPeerToShuffleWith();
			
			if (randomPeer != null)
				initiateShuffle(shuffleLength, randomPeer, similarPeer);
			
			Snapshot.updateViews(self, similarView.getAllPeers(), randomView.getAllPeers());
		}
	};

//-------------------------------------------------------------------	
	Handler<RandomShuffleRequest> handleRandomShuffleRequest = new Handler<RandomShuffleRequest>() {
		public void handle(RandomShuffleRequest event) {
			
			MSPeerAddress peer = event.getMSPeerSource();
			DescriptorBuffer receivedRandomBuffer = event.getRandomBuffer();
			DescriptorBuffer toSendRandomBuffer = new DescriptorBuffer(self, randomView.selectToSendAtPassive(receivedRandomBuffer.getSize(), peer));
			randomView.selectToKeep(peer, receivedRandomBuffer.getDescriptors());

			RandomShuffleResponse response = new RandomShuffleResponse(event.getRequestId(), toSendRandomBuffer, self, peer);
			trigger(response, messagePort);
		}
	};

//-------------------------------------------------------------------	
	Handler<SimilarShuffleRequest> handleSimilarShuffleRequest = new Handler<SimilarShuffleRequest>() {
		public void handle(SimilarShuffleRequest event) {

			MSPeerAddress peer = event.getMSPeerSource();
			DescriptorBuffer receivedSimilarBuffer = event.getSimilarBuffer();			
			DescriptorBuffer toSendSimilarBuffer = new DescriptorBuffer(self, similarView.selectToSendAtPassive(receivedSimilarBuffer.getSize(), peer));
			similarView.selectToKeep(peer, randomView.getAll(), receivedSimilarBuffer.getDescriptors());

			SimilarShuffleResponse response = new SimilarShuffleResponse(event.getRequestId(), toSendSimilarBuffer, self, peer);
			trigger(response, messagePort);
		}
	};

	
//-------------------------------------------------------------------	
	Handler<RandomShuffleResponse> handleRandomShuffleResponse = new Handler<RandomShuffleResponse>() {
		public void handle(RandomShuffleResponse event) {
			if (joining) {
				joining = false;
				trigger(new JoinCompleted(self), psPort);

				// schedule shuffling
				SchedulePeriodicTimeout spt = new SchedulePeriodicTimeout(shufflePeriod, shufflePeriod);
				spt.setTimeoutEvent(new InitiateShuffle(spt));
				trigger(spt, timerPort);
			}

			// cancel shuffle timeout
			UUID shuffleId = event.getRequestId();
			if (outstandingRandomShuffles.containsKey(shuffleId)) {
				outstandingRandomShuffles.remove(shuffleId);
				CancelTimeout ct = new CancelTimeout(shuffleId);
				trigger(ct, timerPort);
			}

			MSPeerAddress peer = event.getMSPeerSource();
			DescriptorBuffer receivedRandomBuffer = event.getRandomBuffer();
			randomView.selectToKeep(peer, receivedRandomBuffer.getDescriptors());
		}
	};

//-------------------------------------------------------------------	
	Handler<SimilarShuffleResponse> handleSimilarShuffleResponse = new Handler<SimilarShuffleResponse>() {
		public void handle(SimilarShuffleResponse event) {
			// cancel shuffle timeout
			UUID shuffleId = event.getRequestId();
			if (outstandingSimilarShuffles.containsKey(shuffleId)) {
				outstandingSimilarShuffles.remove(shuffleId);
				CancelTimeout ct = new CancelTimeout(shuffleId);
				trigger(ct, timerPort);
			}

			MSPeerAddress peer = event.getMSPeerSource();
			DescriptorBuffer receivedSimilarBuffer = event.getSimilarBuffer();
			similarView.selectToKeep(peer, randomView.getAll(), receivedSimilarBuffer.getDescriptors());
		}
	};
	
//-------------------------------------------------------------------	
	Handler<RandomShuffleTimeout> handleRandomShuffleTimeout = new Handler<RandomShuffleTimeout>() {
		public void handle(RandomShuffleTimeout event) {
			//logger.warn("SHUFFLE TIMED OUT");
		}
	};

//-------------------------------------------------------------------	
	Handler<SimilarShuffleTimeout> handleSimilarShuffleTimeout = new Handler<SimilarShuffleTimeout>() {
		public void handle(SimilarShuffleTimeout event) {
			//logger.warn("SHUFFLE TIMED OUT");
		}
	};
}
