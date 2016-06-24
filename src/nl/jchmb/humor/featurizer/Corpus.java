package nl.jchmb.humor.featurizer;

import java.util.ArrayList;
import java.util.Collection;

public class Corpus {
	private Collection<Observation> baseline;
	
	public Corpus() {
		baseline = new ArrayList<>();
	}
	
	public void add(Observation observation) {
//		if (observation.getCategory().equals(Category.OTHER)) {
//			baseline.add(observation);
//		}
	}
}
