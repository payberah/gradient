package kompics.peer.mspeers;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

public final class MSConfiguration {
	private final int snapshotPeriod;

//-------------------------------------------------------------------	
	public MSConfiguration(int snapshotPeriod) {
		super();
		this.snapshotPeriod = snapshotPeriod;
	}

//-------------------------------------------------------------------	
	public int getSnapshotPeriod() {
		return this.snapshotPeriod;
	}

//-------------------------------------------------------------------	
	public void store(String file) throws IOException {
		Properties p = new Properties();
		p.setProperty("snapshot.period", "" + this.snapshotPeriod);
		
		Writer writer = new FileWriter(file);
		p.store(writer, "se.sics.kompics.p2p.ms");
	}

//-------------------------------------------------------------------	
	public static MSConfiguration load(String file) throws IOException {
		Properties p = new Properties();
		Reader reader = new FileReader(file);
		p.load(reader);

		int snapshotPeriod = Integer.parseInt(p.getProperty("snapshot.period"));
		
		return new MSConfiguration(snapshotPeriod);
	}
}
