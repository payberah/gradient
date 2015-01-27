package kompics.simulator.scenario;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

import se.sics.kompics.p2p.experiment.dsl.distribution.Distribution;

public class UniformDistribution extends Distribution<BigInteger> {
	private Random random;
	private static final long serialVersionUID = 6853092446046319743L;

	public UniformDistribution(Random random) {
		super(Type.OTHER, BigInteger.class);
		this.random = random;
	}

//-------------------------------------------------------------------
	public static int getUtilityLevel(int utilityValue) {
		int utilityLevel;
				
		if (utilityValue == 1)
			utilityLevel = 1;
		else if (utilityValue == 2)
			utilityLevel = 2;
		else if (utilityValue == 3)
			utilityLevel = 3;
		else if (utilityValue == 4)
			utilityLevel = 4;
		else if (utilityValue == 5)
			utilityLevel = 5;
		else if (utilityValue == 6)
			utilityLevel = 6;
		else if (utilityValue == 7)
			utilityLevel = 7;
		else if (utilityValue == 8)
			utilityLevel = 8;
		else if (utilityValue == 9)
			utilityLevel = 9;
		else if (utilityValue == 10)
			utilityLevel = 10;
		else 
			utilityLevel = 11;
		
		return utilityLevel;
	}
	
//-------------------------------------------------------------------
	public static int getHighestUtilityLevel() {
		return 11;
	}
	
//-------------------------------------------------------------------
	@Override
	public final BigInteger draw() {
		int num;
		double r = random.nextDouble();
		
		if (r < 0.1)
			num = 1;
		else if (r < 0.2)
			num = 2;
		else if (r < 0.3)
			num = 3;
		else if (r < 0.4)
			num = 4;
		else if (r < 0.5)
			num = 5;
		else if (r < 0.6)
			num = 6;
		else if (r < 0.7)
			num = 7;
		else if (r < 0.8)
			num = 8;
		else if (r < 0.9)
			num = 9;
		else 
			num = 10;
	
		return new BigDecimal(num).toBigInteger();		
	}
}
