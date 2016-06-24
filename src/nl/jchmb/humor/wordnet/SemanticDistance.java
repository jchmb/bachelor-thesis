package nl.jchmb.humor.wordnet;

import java.util.List;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.Pointer;

public class SemanticDistance {
	public static double compute(IDictionary dictionary, ISynset synset, ISynset hypernym) {
		if (synset.equals(hypernym)) {
			return Double.POSITIVE_INFINITY;
		}
		List<ISynsetID> ids = synset.getRelatedSynsets(Pointer.HYPERNYM);
		if (ids.size() == 0) {
			return 0.0d;
		}
		return 1.0d + compute(dictionary, dictionary.getSynset(ids.get(0)), hypernym);
	}
}
