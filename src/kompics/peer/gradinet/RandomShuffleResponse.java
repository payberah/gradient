package kompics.peer.gradinet;


import java.util.UUID;

import kompics.peer.common.MSMessage;
import kompics.peer.common.MSPeerAddress;


public class RandomShuffleResponse extends MSMessage {

	private static final long serialVersionUID = -5022051054665787770L;
	private final UUID requestId;
	private final DescriptorBuffer randomBuffer;

//-------------------------------------------------------------------
	public RandomShuffleResponse(UUID requestId, DescriptorBuffer randomBuffer, MSPeerAddress source, MSPeerAddress destination) {
		super(source, destination);
		this.requestId = requestId;
		this.randomBuffer = randomBuffer;
	}

//-------------------------------------------------------------------
	public UUID getRequestId() {
		return requestId;
	}

//-------------------------------------------------------------------
	public DescriptorBuffer getRandomBuffer() {
		return randomBuffer;
	}
	
//-------------------------------------------------------------------
	public int getSize() {
		return 0;
	}
}
