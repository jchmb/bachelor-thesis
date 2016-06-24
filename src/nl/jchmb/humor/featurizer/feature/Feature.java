package nl.jchmb.humor.featurizer.feature;

import java.util.stream.Stream;

import nl.jchmb.humor.featurizer.Observation;

@FunctionalInterface
public interface Feature {
	public double compute(Observation observation);
	
	public static TagCountingFeature tagCounting(String tag) {
		return new TagCountingFeature(word -> word.tag().equals(tag));
	}
	
	public static TagCountingFeature tagCounting(String... tags) {
		return new TagCountingFeature(
				word -> Stream.of(tags).anyMatch(tag -> tag.equals(word.tag()))
		);
	}
	
	public static Feature sentenceLength() {
		return observation -> observation.getQuote().length();
	}
}
