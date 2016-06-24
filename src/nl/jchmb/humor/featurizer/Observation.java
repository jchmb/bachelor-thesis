package nl.jchmb.humor.featurizer;

import java.io.StringReader;
import java.util.List;
import java.util.stream.Stream;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import nl.jchmb.humor.sentiment.SentiWordNet;
import nl.jchmb.humor.sentiment.SentiWordNetFactory;

public class Observation {
	private final String quote;
	private final Corpus corpus;
	private final List<List<TaggedWord>> tags;
	//private final SentiWordNet sentimentDictionary;
	private final Category category;
	
	// TODO: DO NOT misuse the Observation class to pass around things that are used to analyze it.
	// The Observation is supposed to be just that: an Observation, possibly supervised and processed.
	// But it should not have the tools to analyze it just attached to it.
	public Observation(String quote, Category category, Corpus corpus, MaxentTagger tagger) {
		this.quote = quote;
		this.category = category;
		this.corpus = corpus;
		tags = tagger.process(MaxentTagger.tokenizeText(new StringReader(quote)));
	}
	
	public int countSentences() {
		return tags.size();
	}
	
	public Category getCategory() {
		return category;
	}
	
	public String getQuote() {
		return quote;
	}
	
	public List<List<TaggedWord>> sentences() {
		return tags;
	}
	
	public Stream<TaggedWord> flatStream() {
		return tags.stream().flatMap(sentence -> sentence.stream());
	}
	
	public List<List<TaggedWord>> sentenceList() {
		return tags;
	}
	
	public List<List<TaggedWord>> getTags() {
		return tags;
	}
}
