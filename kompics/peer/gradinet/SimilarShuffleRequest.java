package kompics.peer.gradinet;

import java.util.UUID;

import kompics.peer.common.MSMessage;
import kompics.peer.common.MSPeerAddress;

public class SimilarShuffleRequest extends MSMessage {

	private static final long serialVersionUID = 8493601671018888143L;
	private final UUID requestId;
	private final DescriptorBuffer similarBuffer;

//-------------------------------------------------------------------
	public SimilarShuffleRequest(UUID requestId, DescriptorBuffer similarBuffer, MSPeerAddress source, MSPeerAddress destination) {
		super(source, destination);
		this.requestId = requestId;
		this.similarBuffer = similarBuffer;
	}

//-------------------------------------------------------------------
	public UUID getRequestId() {
		return requestId;
	}

//-------------------------------------------------------------------
	public DescriptorBuffer getSimilarBuffer() {
		return similarBuffer;
	}
	
//-------------------------------------------------------------------
	public int getSize() {
		return 0;
	}
}
