package nl.jchmb.humor.featurizer.feature.sentiment;

import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import Jama.Matrix;
import edu.stanford.nlp.ling.TaggedWord;
import nl.jchmb.humor.featurizer.Observation;
import nl.jchmb.humor.featurizer.feature.Feature;
import nl.jchmb.humor.sentiment.SentiWordNet;
import nl.jchmb.humor.sentiment.Sentiment;
import nl.jchmb.humor.sentiment.SentimentMerger;

public class SentimentFeature implements Feature {
	private SentiWordNet sentimentDictionary;
	private ToDoubleFunction<Sentiment> f;
	private Function<Stream<Sentiment>, Sentiment> reducer;
	
	public SentimentFeature(SentiWordNet sentimentDictionary, ToDoubleFunction<Sentiment> f, Function<Stream<Sentiment>, Sentiment> reducer) {
		this.sentimentDictionary = sentimentDictionary;
		this.f = f;
		this.reducer = reducer;
	}
	
	public SentimentFeature(SentiWordNet sentimentDictionary, ToDoubleFunction<Sentiment> f) {
		this(sentimentDictionary, f, SentimentMerger::average);
	}

	@Override
	public double compute(Observation observation) {
		return f.applyAsDouble(
				reducer.apply(
						observation.flatStream()
								.map(tag -> sentimentDictionary.sentiment(tag))
								.filter(sentiment -> sentiment != null)
				)
		);
	}
	
	public static Feature minimumPositivity(SentiWordNet sentimentDictionary) {
		return new SentimentFeature(
				sentimentDictionary,
				sentiment -> sentiment.positivity(),
				SentimentMerger::minimum
		);
	}
	
	public static Feature minimumNegativity(SentiWordNet sentimentDictionary) {
		return new SentimentFeature(
				sentimentDictionary,
				sentiment -> sentiment.negativity(),
				SentimentMerger::minimum
		);
	}
	
	public static Feature maximumPositivity(SentiWordNet sentimentDictionary) {
		return new SentimentFeature(
				sentimentDictionary,
				sentiment -> sentiment.positivity(),
				SentimentMerger::maximum
		);
	}
	
	public static Feature maximumNegativity(SentiWordNet sentimentDictionary) {
		return new SentimentFeature(
				sentimentDictionary,
				sentiment -> sentiment.negativity(),
				SentimentMerger::maximum
		);
	}
	
	public static Feature positivity(SentiWordNet sentimentDictionary) {
		return new SentimentFeature(
				sentimentDictionary,
				sentiment -> sentiment.positivity()
		);
	}
	
	public static Feature negativity(SentiWordNet sentimentDictionary) {
		return new SentimentFeature(
				sentimentDictionary,
				sentiment -> sentiment.negativity()
		);
	}
	
	/**
	 * ObjScore = 1 - (PosScore + NegScore)
	 * 
	 * @param sentimentDictionary
	 * @return
	 */
	public static Feature objectivity(SentiWordNet sentimentDictionary) {
		return new SentimentFeature(
				sentimentDictionary,
				sentiment -> sentiment.objectivity()
		);
	}
}
