package nl.jchmb.humor.sentiment;

import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import Jama.Matrix;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IndexWord;
import edu.mit.jwi.item.POS;
import edu.stanford.nlp.ling.TaggedWord;
import nl.jchmb.humor.wordnet.POSMapping;
import nl.jchmb.utils.tuple.Tuple2;

public class SentiWordNet {
	// ADJ, ADJ_SAT, ADV, NOUN, VERB = 'a', 's', 'r', 'n', 'v'
	
	private Map<ISynsetID, Sentiment> sentiments;
	private IDictionary dictionary;

	public SentiWordNet(IDictionary dictionary, Map<ISynsetID, Sentiment> sentiments) {
		this.dictionary = dictionary;
		this.sentiments = sentiments;
	}

	public Sentiment sentiment(ISynsetID id) {
		return sentiments.get(id);
	}
	
	public Sentiment sentiment(TaggedWord tag) {
		if (!POSMapping.contains(tag.tag())) {
			return null;
		}
		return sentiment(
				tag.word(),
				POSMapping.get(tag.tag())
		);
	}

	public Sentiment sentiment(String word, POS pos) {
		IIndexWord indexWord = dictionary.getIndexWord(word, pos);
		if (indexWord == null) {
			return null;
		}
		return SentimentMerger.average(
				indexWord.getWordIDs().stream()
						.map(wordID -> sentiment(wordID.getSynsetID()))
		);
	}
}