package nl.jchmb.humor.featurizer.feature.sentiment;

import java.util.function.Function;

import edu.stanford.nlp.ling.TaggedWord;
import nl.jchmb.humor.featurizer.Observation;
import nl.jchmb.humor.featurizer.feature.Feature;
import nl.jchmb.humor.ngram.NGram;
import nl.jchmb.humor.ngram.NGramStream;
import nl.jchmb.humor.sentiment.SentiWordNet;
import nl.jchmb.humor.sentiment.Sentiment;

public class SentimentValuationCountFeature implements Feature {
	private SentiWordNet sentimentDictionary;
	private final double threshold = 0.5d;
	private final NGram<SentimentValuation> ngram;

	public SentimentValuationCountFeature(
		SentiWordNet sentimentDictionary,
		NGram<SentimentValuation> ngram
	) {
		this.sentimentDictionary = sentimentDictionary;
		this.ngram = ngram;
	}
	
	private SentimentValuation chooseValuation(Sentiment sentiment) {
		return sentiment.subjectivity() < threshold ?
				SentimentValuation.NEUTRAL :
				(sentiment.positivity() >= threshold ?
					SentimentValuation.POSITIVE :
					SentimentValuation.NEGATIVE);
	}

	public double compute(Observation observation) {
		return (double) NGramStream.of(
				observation.flatStream()
					.map(word -> sentimentDictionary.sentiment(word))
					.filter(sentiment -> sentiment != null)
					.map(
						this::chooseValuation
					),
				ngram.length()
			)
			.filter(ngram -> ngram.equals(this.ngram))
			.count();
	}

	public static Feature positiveToNegative(SentiWordNet sentimentDictionary) {
		return new SentimentValuationCountFeature(
			sentimentDictionary,
			NGram.of(SentimentValuation.POSITIVE, SentimentValuation.NEGATIVE)
		);
	}

	public static Feature negativeToPositive(SentiWordNet sentimentDictionary) {
		return new SentimentValuationCountFeature(
			sentimentDictionary,
			NGram.of(SentimentValuation.NEGATIVE, SentimentValuation.POSITIVE)
		);
	}
}