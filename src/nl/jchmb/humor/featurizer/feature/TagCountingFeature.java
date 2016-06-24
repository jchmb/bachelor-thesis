package nl.jchmb.humor.featurizer.feature;

import java.util.function.Predicate;
import java.util.stream.Stream;

import edu.stanford.nlp.ling.TaggedWord;
import nl.jchmb.humor.featurizer.Observation;
import nl.jchmb.utils.stream.aggregate.Proportion;
import nl.jchmb.utils.tuple.Tuple2;

public class TagCountingFeature implements Feature {
	private Predicate<TaggedWord> predicate;
	
	public TagCountingFeature(Predicate<TaggedWord> predicate) {
		this.predicate = predicate;
	}
	
	@Override
	public double compute(Observation observation) {
//		return observation.getTags().stream()
//				.flatMap(list -> list.stream())
//				.filter(predicate)
//				.count();
		return Proportion.measure(
				observation.flatStream(),
				predicate
		);
	}
	
	public static Feature of(String tag) {
		return new TagCountingFeature(word -> word.tag().equals(tag));
	}

	public static Stream<Tuple2<String, Feature>> of(String... tags) {
		return Stream.of(tags)
				.map(
						tag -> new Tuple2<String, Feature>(
										"count_" + tag,
										of(tag)
								)
				);
	}
	
	public static Stream<Tuple2<String, Feature>> startsWith(String... tags) {
		return Stream.of(tags)
				.map(
						tag -> new Tuple2<String, Feature>(
										"count_" + tag,
										startsWith(tag)
								)
				);
	}
	
	public static TagCountingFeature startsWith(String tag) {
		return new TagCountingFeature(word -> word.tag().startsWith(tag));
	}
	
}
