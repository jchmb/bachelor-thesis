package nl.jchmb.humor.featurizer.feature;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import nl.jchmb.humor.featurizer.Observation;
import nl.jchmb.humor.wordnet.POSMapping;

public class AmbiguityFeature implements Feature {
	private IDictionary dictionary;
	
	public AmbiguityFeature(IDictionary dictionary) {
		this.dictionary = dictionary;
	}
	
	@Override
	public double compute(Observation observation) {
		return observation.flatStream()
				.map(
						word -> dictionary.getIndexWord(
								word.word(),
								POSMapping.get(word.tag())
						)
				)
				.filter(word -> word != null)
				.mapToInt(
						word -> word.getWordIDs().size()
				)
				.filter(i -> i >= 2)
				.map(i -> i - 1)
				.count();
	}
}