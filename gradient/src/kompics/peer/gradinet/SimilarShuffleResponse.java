package kompics.peer.gradinet;

import java.util.UUID;

import kompics.peer.common.MSMessage;
import kompics.peer.common.MSPeerAddress;

public class SimilarShuffleResponse extends MSMessage {

	private static final long serialVersionUID = -5022051054665787770L;
	private final UUID requestId;
	private final DescriptorBuffer similarBuffer;

//-------------------------------------------------------------------
	public SimilarShuffleResponse(UUID requestId, DescriptorBuffer similarBuffer, MSPeerAddress source, MSPeerAddress destination) {
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
