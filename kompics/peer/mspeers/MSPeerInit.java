package kompics.peer.mspeers;

import kompics.peer.common.MSPeerAddress;
import kompics.peer.gradinet.GradientConfiguration;

import se.sics.kompics.Init;
import se.sics.kompics.p2p.bootstrap.BootstrapConfiguration;
import se.sics.kompics.p2p.fd.ping.PingFailureDetectorConfiguration;

public final class MSPeerInit extends Init {

	private final MSPeerAddress msPeerSelf;
	private final int uploadSlots;
	private final int downloadSlots;
	private final BootstrapConfiguration bootstrapConfiguration;
	private final GradientConfiguration gradientConfiguration;
	private final MSConfiguration msConfiguration;
	private final PingFailureDetectorConfiguration fdConfiguration;

//-------------------------------------------------------------------	
	public MSPeerInit(MSPeerAddress msPeerSelf, int uploadSlots, int downloadSlots,
			MSConfiguration msConfiguration,
			BootstrapConfiguration bootstrapConfiguration,
			GradientConfiguration gradientConfiguration,
			PingFailureDetectorConfiguration fdConfiguration) {
		super();
		this.uploadSlots = uploadSlots;
		this.downloadSlots = downloadSlots;
		this.msPeerSelf = msPeerSelf;
		this.bootstrapConfiguration = bootstrapConfiguration;
		this.gradientConfiguration = gradientConfiguration;
		this.msConfiguration = msConfiguration;
		this.fdConfiguration = fdConfiguration;
	}

//-------------------------------------------------------------------	
	public MSPeerAddress getMSPeerSelf() {
		return msPeerSelf;
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
	public BootstrapConfiguration getBootstrapConfiguration() {
		return bootstrapConfiguration;
	}

//-------------------------------------------------------------------	
	public GradientConfiguration getGradientConfiguration() {
		return gradientConfiguration;
	}

//-------------------------------------------------------------------	
	public MSConfiguration getMSConfiguration() {
		return msConfiguration; 
	}
	
//-------------------------------------------------------------------	
	public PingFailureDetectorConfiguration getFdConfiguration() {
		return fdConfiguration;
	}
}
