package nl.jchmb.humor.featurizer.feature;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.stanford.nlp.ling.TaggedWord;
import nl.jchmb.humor.featurizer.Observation;
import nl.jchmb.utils.tuple.Tuple2;

public class TagDetectionFeature implements Feature {
	private Function<Observation, String> selector;
	private String tag;

	public TagDetectionFeature(Function<Observation, TaggedWord> selector, String tag) {
		this.selector = selector.andThen(QuoteFunctions.tag());
		this.tag = tag;
	}
	
	public double compute(Observation observation) {
		return selector.apply(observation).startsWith(tag) ? 1.0d : 0.0d;
	}

	public static Feature firstSentenceFirstTag(String tag) {
		return new TagDetectionFeature(
			QuoteFunctions.firstSentenceFirstWord(),
			tag
		);
	}

	public static Feature firstSentenceLastTag(String tag) {
		return new TagDetectionFeature(
			QuoteFunctions.firstSentenceLastWord(),
			tag
		);
	}

	public static Feature lastSentenceFirstTag(String tag) {
		return new TagDetectionFeature(
			QuoteFunctions.lastSentenceFirstWord(),
			tag
		);
	}

	public static Feature lastSentenceLastTag(String tag) {
		return new TagDetectionFeature(
			QuoteFunctions.lastSentenceLastWord(),
			tag
		);
	}

	public static Stream<Tuple2<String, Feature>> tagDetectors(String tag) {
		String prefix = "detection_" + tag + "_";
		return Stream.of(
			new Tuple2<>(prefix + "f_f", firstSentenceFirstTag(tag)),
			new Tuple2<>(prefix + "f_l", firstSentenceLastTag(tag)),
			new Tuple2<>(prefix + "l_f", lastSentenceFirstTag(tag)),
			new Tuple2<>(prefix + "l_l", lastSentenceLastTag(tag))
		);
	}

	public static Stream<Tuple2<String, Feature>> tagsDetectors(String... tags) {
		return Stream.of(tags)
			.flatMap(tag -> tagDetectors(tag));
	}
}