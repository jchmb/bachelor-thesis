package nl.jchmb.humor.featurizer.source;

import java.util.stream.Stream;

import nl.jchmb.humor.featurizer.Category;
import nl.jchmb.humor.featurizer.Observation;

@FunctionalInterface
public interface ObservationSource {
	public Stream<Observation> stream(Category category);
}
