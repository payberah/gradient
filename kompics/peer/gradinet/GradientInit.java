package kompics.peer.gradinet;

import se.sics.kompics.Init;

public final class GradientInit extends Init {

	private final GradientConfiguration configuration;
	private final RandomView randomView;
	private final SimilarView similarView;
	private final Fingers fingers;
	private final int utilityValue;

//-------------------------------------------------------------------
	public GradientInit(GradientConfiguration configuration, RandomView randomView, SimilarView similarView, Fingers fingers, int utilityValue) {
		super();
		this.configuration = configuration;
		this.randomView = randomView;
		this.similarView = similarView;
		this.fingers = fingers;
		this.utilityValue = utilityValue;
	}

//-------------------------------------------------------------------
	public GradientConfiguration getConfiguration() {
		return configuration;
	}

//-------------------------------------------------------------------
	public RandomView getRandomView() {
		return randomView;
	}

//-------------------------------------------------------------------
	public SimilarView getSimilarView() {
		return similarView;
	}

//-------------------------------------------------------------------
	public Fingers getFingers() {
		return fingers;
	}

//-------------------------------------------------------------------
	public int getUtilityValue() {
		return utilityValue;
	}
}