package nl.jchmb.humor.featurizer.feature;

import java.util.List;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;
import edu.mit.jwi.item.Word;
import nl.jchmb.humor.featurizer.Observation;
import nl.jchmb.humor.wordnet.POSMapping;
import nl.jchmb.humor.wordnet.SemanticDistance;

public class AbstractionFeature implements Feature {
	private IDictionary dictionary;
	
	public AbstractionFeature(IDictionary dictionary) {
		this.dictionary = dictionary;
	}
	
	@Override
	public double compute(Observation observation) {
		List<IWordID> wordIds = dictionary.getIndexWord(
				"abstraction", POS.NOUN
		).getWordIDs();
		IWordID abstractionId = wordIds.get(wordIds.size() - 1);
		ISynsetID synsetId = abstractionId.getSynsetID();
		ISynset abstraction = dictionary.getSynset(synsetId);
		double score = observation.flatStream()
			.map(
					e -> dictionary.getIndexWord(e.word(), POSMapping.get(e.tag()))
			)
			.filter(e -> e != null)
			.map(e -> e.getWordIDs())
			.map(
					ids -> ids.stream()
									.mapToDouble(
											w -> SemanticDistance.compute(
													dictionary,
													dictionary.getSynset(w.getSynsetID()),
													abstraction
												)
									)
									.filter(distance -> distance < Double.POSITIVE_INFINITY)
									.map(distance -> (1.0d / (distance + 0.01d)))
									.average()
			)
			.filter(distance -> distance.isPresent())
			.mapToDouble(distance -> distance.getAsDouble())
			.sum();
		//System.out.println(score);
		return score;
	}
	
}
