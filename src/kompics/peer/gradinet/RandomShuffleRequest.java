package kompics.peer.gradinet;


import java.util.UUID;

import kompics.peer.common.MSMessage;
import kompics.peer.common.MSPeerAddress;


public class RandomShuffleRequest extends MSMessage {

	private static final long serialVersionUID = 8493601671018888143L;
	private final UUID requestId;
	private final DescriptorBuffer randomBuffer;

//-------------------------------------------------------------------
	public RandomShuffleRequest(UUID requestId, DescriptorBuffer randomBuffer, MSPeerAddress source, MSPeerAddress destination) {
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
