package kompics.peer.gradinet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

import kompics.peer.common.MSPeerAddress;
import kompics.peer.common.MSPeerDescriptor;
import kompics.simulator.scenario.UniformDistribution;

public class SimilarView {

	private final int size;
	private final MSPeerAddress self;
	private final int utilityLevel;
	private ArrayList<ViewEntry> entries;
	private HashMap<MSPeerAddress, ViewEntry> d2e;
	private Random random = new Random();

//-------------------------------------------------------------------
	private Comparator<ViewEntry> comparatorByAge = new Comparator<ViewEntry>() {
		public int compare(ViewEntry o1, ViewEntry o2) {
			if (o1.getDescriptor().getAge() > o2.getDescriptor().getAge())
				return 1;
			else if (o1.getDescriptor().getAge() < o2.getDescriptor().getAge())
				return -1;
			else
				return 0;			
		}
	};

//-------------------------------------------------------------------
	public SimilarView(int size, MSPeerAddress self, int utilityValue) {
		super();
		this.self = self;
		this.size = size;
		this.entries = new ArrayList<ViewEntry>();
		this.d2e = new HashMap<MSPeerAddress, ViewEntry>();

		utilityLevel = UniformDistribution.getUtilityLevel(utilityValue);
	}

//-------------------------------------------------------------------
	public synchronized void incrementDescriptorAges() {
		for (ViewEntry entry : entries) {
			entry.getDescriptor().incrementAndGetAge();
		}
	}

//-------------------------------------------------------------------
	/**
	 * called at shuffle initiator upon initiating shuffle request
	 */
	public synchronized MSPeerAddress selectPeerToShuffleWith() {
		if (entries.isEmpty())
			return null;
		
		ViewEntry oldestEntry = Collections.max(entries, comparatorByAge);
		removeEntry(oldestEntry);
		return oldestEntry.getDescriptor().getMSPeerAddress();
	}

//-------------------------------------------------------------------
	/**
	 * called at shuffle initiator upon initiating shuffle request
	 */
	public synchronized ArrayList<MSPeerDescriptor> selectToSendAtActive(int count, MSPeerAddress destinationPeer) {
		ArrayList<ViewEntry> randomEntries = generateRandomSample(count);
		ArrayList<MSPeerDescriptor> descriptors = new ArrayList<MSPeerDescriptor>();

		for (ViewEntry cacheEntry : randomEntries) {
			cacheEntry.sentTo(destinationPeer);
			descriptors.add(cacheEntry.getDescriptor());
		}
		
		return descriptors;
	}

//-------------------------------------------------------------------
	/**
	 * called at shuffle receiver upon receiving shuffle request
	 */
	public synchronized ArrayList<MSPeerDescriptor> selectToSendAtPassive(int count, MSPeerAddress destinationPeer) {
		ArrayList<ViewEntry> randomEntries = generateRandomSample(count);
		ArrayList<MSPeerDescriptor> descriptors = new ArrayList<MSPeerDescriptor>();
		
		for (ViewEntry cacheEntry : randomEntries) {
			cacheEntry.sentTo(destinationPeer);
			descriptors.add(cacheEntry.getDescriptor());
		}
		
		return descriptors;
	}

//-------------------------------------------------------------------
	/**
	 * called at shuffle receiver upon receiving shuffle request called at
	 * shuffle initiator upon receiving shuffle response
	 */
	public synchronized void selectToKeep(MSPeerAddress from, ArrayList<MSPeerDescriptor> randomDescriptors, ArrayList<MSPeerDescriptor> similarDescriptors) {
		LinkedList<ViewEntry> entriesSentToThisPeer = new LinkedList<ViewEntry>();

		for (ViewEntry cacheEntry : entries) {
			if (cacheEntry.wasSentTo(from))
				entriesSentToThisPeer.add(cacheEntry);			
		}
		
		MSPeerDescriptor worstSimilarDescriptor;
		
		for (MSPeerDescriptor descriptor : similarDescriptors) {

			// do not keep descriptor of self
			if (self.equals(descriptor.getMSPeerAddress()))
				continue;

			// do not keep the lower utility peers
			if (descriptor.getUtilityLevel() == 0 || descriptor.getUtilityLevel() < this.utilityLevel || descriptor.getUtilityLevel() > this.utilityLevel + 1)
				continue;
			
			if (d2e.containsKey(descriptor.getMSPeerAddress())) {
				// we already have an entry for this peer. keep the youngest one
				ViewEntry entry = d2e.get(descriptor.getMSPeerAddress());
				if (entry.getDescriptor().getAge() > descriptor.getAge()) {
					// we keep the lowest age descriptor
					removeEntry(entry);
					addEntry(new ViewEntry(descriptor));
					continue;
				} else {
					continue;
				}
			}
			
			if (entries.size() < size) {
				// fill an empty slot
				addEntry(new ViewEntry(descriptor));
				continue;
			}
			
			// replace one slot out of those sent to this peer
			ViewEntry sentEntry = entriesSentToThisPeer.poll();
			if (sentEntry != null) {
				removeEntry(sentEntry);
				addEntry(new ViewEntry(descriptor));
			}
		}
		
		// update the similar view using the random view
		for (MSPeerDescriptor descriptor : randomDescriptors) {
			// do not keep descriptor of self
			if (self.equals(descriptor.getMSPeerAddress()))
				continue;

			// do not keep the lower utility peers
			if (descriptor.getUtilityLevel() == 0 || descriptor.getUtilityLevel() < this.utilityLevel || descriptor.getUtilityLevel() > this.utilityLevel + 1)
				continue;
			
			if (d2e.containsKey(descriptor.getMSPeerAddress())) {
				// we already have an entry for this peer. keep the youngest one
				ViewEntry entry = d2e.get(descriptor.getMSPeerAddress());
				if (entry.getDescriptor().getAge() > descriptor.getAge()) {
					// we keep the lowest age descriptor
					removeEntry(entry);
					addEntry(new ViewEntry(descriptor));
					continue;
				} else {
					continue;
				}
			}

			if (entries.size() < size) {
				// fill an empty slot
				addEntry(new ViewEntry(descriptor));
				continue;
			} 
			
			worstSimilarDescriptor = getWorstSimilar(similarDescriptors);
//			if (isBetter(descriptor, worstSimilarDescriptor)) {
			if (worstSimilarDescriptor != null) {
				removeEntry(new ViewEntry(worstSimilarDescriptor));
				addEntry(new ViewEntry(descriptor));
			}
		}
	}

	
//-------------------------------------------------------------------
	/**
	 * called at shuffle receiver upon receiving shuffle request called at
	 * shuffle initiator upon receiving shuffle response
	 */
	public synchronized void selectToKeep(ArrayList<MSPeerDescriptor> randomDescriptors) {
		MSPeerDescriptor worstSimilarDescriptor;
		
		// update the similar view using the random view
		for (MSPeerDescriptor descriptor : randomDescriptors) {
			if (self.equals(descriptor.getMSPeerAddress()))
				continue;

			if (this.utilityLevel > 0) {
				if (descriptor.getUtilityLevel() < utilityLevel || descriptor.getUtilityLevel() == 0)
					continue;
			}
			
			if (d2e.containsKey(descriptor.getMSPeerAddress())) {
				// we already have an entry for this peer. keep the youngest one
				ViewEntry entry = d2e.get(descriptor.getMSPeerAddress());
				if (entry.getDescriptor().getAge() > descriptor.getAge()) {
					// we keep the lowest age descriptor
					removeEntry(entry);
					addEntry(new ViewEntry(descriptor));
					continue;
				} else {
					continue;
				}
			}

			if (entries.size() < size) {
				// fill an empty slot
				addEntry(new ViewEntry(descriptor));
				continue;
			} 
			
			worstSimilarDescriptor = getWorstSimilar(null);
			if (isBetter(descriptor, worstSimilarDescriptor)) {
				removeEntry(new ViewEntry(worstSimilarDescriptor));
				addEntry(new ViewEntry(descriptor));
			}
		}
	}

//-------------------------------------------------------------------
	/**
	 * @return all peers from the cache.
	 */
	public final synchronized ArrayList<MSPeerDescriptor> getAll() {
		ArrayList<MSPeerDescriptor> descriptors = new ArrayList<MSPeerDescriptor>();
		for (ViewEntry cacheEntry : entries)
			descriptors.add(cacheEntry.getDescriptor());
		
		return descriptors;
	}

//-------------------------------------------------------------------
	/**
	 * Generates a list of random peers from the cache.
	 * 
	 * @param count
	 *            how many peers to generate.
	 * @return the list of random peers.
	 */
	public final synchronized List<MSPeerAddress> getRandomPeers(int count) {
		ArrayList<ViewEntry> randomEntries = generateRandomSample(count);
		LinkedList<MSPeerAddress> randomPeers = new LinkedList<MSPeerAddress>();

		for (ViewEntry cacheEntry : randomEntries)
			randomPeers.add(cacheEntry.getDescriptor().getMSPeerAddress());		

		return randomPeers;
	}
	
//-------------------------------------------------------------------
	public final synchronized ArrayList<MSPeerAddress> getAllPeers() {
		ArrayList<MSPeerAddress> peers = new ArrayList<MSPeerAddress>();

		for (ViewEntry cacheEntry : entries)
			peers.add(cacheEntry.getDescriptor().getMSPeerAddress());
		
		return peers;
	}

//-------------------------------------------------------------------
	private final synchronized ArrayList<ViewEntry> generateRandomSample(int n) {
		ArrayList<ViewEntry> randomEntries;
		if (n >= entries.size()) {
			// return all entries
			randomEntries = new ArrayList<ViewEntry>(entries);
		} else {
			// return count random entries
			randomEntries = new ArrayList<ViewEntry>();
			// Don Knuth, The Art of Computer Programming, Algorithm S(3.4.2)
			int t = 0, m = 0, N = entries.size();
			while (m < n) {
				int x = random.nextInt(N - t);
				if (x < n - m) {
					randomEntries.add(entries.get(t));
					m += 1;
					t += 1;
				} else {
					t += 1;
				}
			}
		}
		
		return randomEntries;
	}

//-------------------------------------------------------------------
	private synchronized void addEntry(ViewEntry entry) {
		entries.add(entry);
		d2e.put(entry.getDescriptor().getMSPeerAddress(), entry);
		checkSize();
	}

//-------------------------------------------------------------------
	private synchronized void removeEntry(ViewEntry entry) {
		entries.remove(entry);
		d2e.remove(entry.getDescriptor().getMSPeerAddress());
		checkSize();
	}

//-------------------------------------------------------------------
	private synchronized void checkSize() {
		if (entries.size() != d2e.size())
			throw new RuntimeException("WHD " + entries.size() + " <> "
					+ d2e.size());
	}

//-------------------------------------------------------------------
	private synchronized MSPeerDescriptor getWorstSimilar(ArrayList<MSPeerDescriptor> similarDescriptors) {
		MSPeerDescriptor descriptor = null;
		TreeMap<Integer, MSPeerDescriptor> lowerValues = new TreeMap<Integer, MSPeerDescriptor>();
		TreeMap<Integer, MSPeerDescriptor> higherValues = new TreeMap<Integer, MSPeerDescriptor>();
		TreeMap<Integer, MSPeerDescriptor> slightlyHigherValues = new TreeMap<Integer, MSPeerDescriptor>();

		for (ViewEntry cacheEntry : entries) {
			if (cacheEntry.getDescriptor().getUtilityLevel() < utilityLevel)
				lowerValues.put(cacheEntry.getDescriptor().getUtilityLevel(), cacheEntry.getDescriptor());
			else if (cacheEntry.getDescriptor().getUtilityValue() > utilityLevel + 1)
				higherValues.put(cacheEntry.getDescriptor().getUtilityValue(), cacheEntry.getDescriptor());
			else if (similarDescriptors == null || !similarDescriptors.contains(cacheEntry.getDescriptor()))
				slightlyHigherValues.put(cacheEntry.getDescriptor().getUtilityValue(), cacheEntry.getDescriptor());
		}

		if (lowerValues.size() > 0)
			descriptor = lowerValues.firstEntry().getValue();
		else if (higherValues.size() > 0)
			descriptor = higherValues.lastEntry().getValue();
		else if (slightlyHigherValues.size() > 0)
			descriptor = slightlyHigherValues.lastEntry().getValue();
		
		return descriptor;
	}
	
//-------------------------------------------------------------------
	private synchronized boolean isBetter(MSPeerDescriptor goodPeer, MSPeerDescriptor badPeer) {
		boolean result = false;
		if (badPeer == null)
			return false;
		
		int goodPeerLevel = goodPeer.getUtilityLevel();
		int badPeerLevel = badPeer.getUtilityLevel();
		
		if (goodPeerLevel == utilityLevel && badPeerLevel != utilityLevel)
			result = true;
		else if (goodPeerLevel != utilityLevel && badPeerLevel == utilityLevel)
			result = false;
		else if (goodPeerLevel < utilityLevel && badPeerLevel < utilityLevel) {
			if (goodPeerLevel < badPeerLevel)
				result = false;
			else
				result = true;
		} else if (goodPeerLevel > utilityLevel && badPeerLevel > utilityLevel) {
			if (goodPeerLevel < badPeerLevel)
				result = true;
			else
				result = false;
		} else if (goodPeerLevel > utilityLevel && badPeerLevel < utilityLevel)
			result = true;
		else if (goodPeerLevel < utilityLevel && badPeerLevel > utilityLevel)
			result = false;
		
		return result;
	}
}
