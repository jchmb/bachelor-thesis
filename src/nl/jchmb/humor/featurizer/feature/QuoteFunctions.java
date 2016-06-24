package nl.jchmb.humor.featurizer.feature;

import java.util.List;
import java.util.function.Function;

import edu.stanford.nlp.ling.TaggedWord;
import nl.jchmb.humor.featurizer.Observation;

public class QuoteFunctions {
	public static Function<Observation, List<TaggedWord>> firstSentence() {
		return observation -> observation.sentenceList().get(0);
	}

	public static Function<Observation, List<TaggedWord>> lastSentence() {
		return observation -> observation.sentenceList().get(observation.countSentences() - 1);
	}

	public static Function<List<TaggedWord>, TaggedWord> firstWord() {
		return sentence -> sentence.get(0);
	}

	public static Function<List<TaggedWord>, TaggedWord> lastWord() {
		return sentence -> sentence.get(sentence.size() - 1);
	}

	public static Function<TaggedWord, String> tag() {
		return taggedWord -> taggedWord.tag();
	}

	public static Function<Observation, TaggedWord> firstSentenceFirstWord() {
		return firstSentence().andThen(firstWord());
	}

	public static Function<Observation, TaggedWord> firstSentenceLastWord() {
		return firstSentence().andThen(lastWord());
	}

	public static Function<Observation, TaggedWord> lastSentenceFirstWord() {
		return lastSentence().andThen(firstWord());
	}

	public static Function<Observation, TaggedWord> lastSentenceLastWord() {
		return lastSentence().andThen(lastWord());
	}
}