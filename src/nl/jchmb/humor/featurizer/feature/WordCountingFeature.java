package nl.jchmb.humor.featurizer.feature;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import edu.stanford.nlp.ling.TaggedWord;
import nl.jchmb.humor.featurizer.Observation;

public class WordCountingFeature implements Feature {
	private List<String> words;
	private Predicate<String> totalPredicate;
	private Function<TaggedWord, String> tokenSelector;

	public WordCountingFeature(Function<TaggedWord, String> tokenSelector, String... words) {
		this(tokenSelector, word -> true, words);
	}

	public WordCountingFeature(Function<TaggedWord, String> tokenSelector, Predicate<String> totalPredicate, String... words) {
		this.words = Arrays.asList(words);
		this.tokenSelector = tokenSelector;
		this.totalPredicate = totalPredicate;
	}

	public double compute(Observation observation) {
		double totalCount = (double) observation.sentences().stream()
			.flatMap(sentence -> sentence.stream())
			.map(tokenSelector)
			.filter(totalPredicate)
			.count();
		double count = (double) observation.sentences().stream()
			.flatMap(sentence -> sentence.stream())
			.map(tokenSelector)
			.filter(word -> words.contains(word))
			.count();
		return count / totalCount;
	}

	public static Feature thirdPersonPronounsCount() {
		return new WordCountingFeature(
			tag -> tag.word(),
			"he",
			"she",
			"they",
			"them",
			"their",
			"that",
			"this",
			"these",
			"those"
		);
	}

	public static Feature indefiniteCount() {
		List<String> determiners = Arrays.asList("a", "an", "the");
		return new WordCountingFeature(
			tag -> tag.word(),
			word -> determiners.contains(word),
			"a",
			"an"
		);
	}

	public static Feature pastTenseCount() {
		return new WordCountingFeature(
			tag -> tag.tag(),
			tag -> tag.startsWith("VB"),
			"VBD",
			"VBN"
		);
	}
}