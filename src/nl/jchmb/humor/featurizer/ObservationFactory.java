package nl.jchmb.humor.featurizer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class ObservationFactory {
	private final Corpus corpus;
	private final MaxentTagger tagger;
	
	public ObservationFactory() {
		this(
				new MaxentTagger("res/pos/english-bidirectional-distsim.tagger"),
				new Dictionary(new File("res/dict"))
		);
	}
	
	public ObservationFactory(MaxentTagger tagger, IDictionary dictionary) {
		this.corpus = new Corpus();
		this.tagger = tagger;
	}
	
	public Observation produce(String quote, Category category) {
		Observation observation = new Observation(quote, category, corpus, tagger);
		return observation;
	}
	
	public static void main(String[] args) throws IOException {
		ObservationFactory factory = new ObservationFactory();
	}
}
