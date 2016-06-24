package nl.jchmb.humor.wordnet;

import java.util.Map;
import java.util.stream.Stream;

import edu.mit.jwi.item.POS;
import nl.jchmb.utils.tuple.Tuple2;

public class POSMapping {
	private static final Map<String, POS> mapping = Tuple2.toMap(
			Stream.of(
					new Tuple2<>("JJ", POS.ADJECTIVE),
					new Tuple2<>("JJR", POS.ADJECTIVE),
					new Tuple2<>("JJS", POS.ADJECTIVE),
					new Tuple2<>("NN", POS.NOUN),
					new Tuple2<>("NNS", POS.NOUN),
					new Tuple2<>("NNP", POS.NOUN),
					new Tuple2<>("NNPS", POS.NOUN),
					new Tuple2<>("VB", POS.VERB),
					new Tuple2<>("VBD", POS.VERB),
					new Tuple2<>("VBG", POS.VERB),
					new Tuple2<>("VBN", POS.VERB),
					new Tuple2<>("VBP", POS.VERB),
					new Tuple2<>("VBZ", POS.VERB),
					new Tuple2<>("RB", POS.ADVERB),
					new Tuple2<>("RBR", POS.ADVERB),
					new Tuple2<>("RBS", POS.ADVERB)
			)
	);
	
	public static boolean contains(String tag) {
		return mapping.containsKey(tag);
	}
	
	public static POS get(String tag) {
		return mapping.getOrDefault(tag, POS.NOUN);
	}
}
