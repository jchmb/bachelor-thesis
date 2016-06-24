package nl.jchmb.humor.sentiment;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import Jama.Matrix;
import edu.stanford.nlp.ling.TaggedWord;

public class SentimentMerger {
	public static final SentimentMerger ZERO = new SentimentMerger(Sentiment.NEUTRAL, 0);
	
	private double positivity;
	private double negativity;
	private int n;
	
	public SentimentMerger(Sentiment sentiment) {
		this(sentiment, 1);
	}
	
	public SentimentMerger(Sentiment sentiment, int n) {
		this(sentiment.positivity(), sentiment.negativity(), n);
	}
	
	public SentimentMerger(double positivity, double negativity, int n) {
		this.positivity = positivity;
		this.negativity = negativity;
		this.n = n;
	}

	public SentimentMerger merge(SentimentMerger that) {
		return new SentimentMerger(
				this.positivity + that.positivity,
				this.negativity + that.negativity,
				this.n + that.n
		);
	}
	
	public Sentiment reduce() {
		return n < 1 ?
			Sentiment.NEUTRAL :
			new Sentiment(
				positivity / ((double) n),
				negativity / ((double) n)
			);
	}
	
	public static Sentiment maximum(Stream<Sentiment> sentiments) {
		return sentiments
				.filter(sentiment -> sentiment != null)
				.reduce(
						new Sentiment(0.0d, 0.0d),
						(s1, s2) -> new Sentiment(
								Math.max(s1.positivity(), s2.positivity()),
								Math.max(s1.negativity(), s2.negativity())
						)
				);
	}
	
	public static Sentiment minimum(Stream<Sentiment> sentiments) {
		return sentiments
				.filter(sentiment -> sentiment != null)
				.reduce(
						new Sentiment(1.0d, 1.0d),
						(s1, s2) -> new Sentiment(
								Math.min(s1.positivity(), s2.positivity()),
								Math.min(s1.negativity(), s2.negativity())
						)
				);
	}
	
	public static Sentiment average(Stream<Sentiment> sentiments) {
		return sentiments
				.filter(sentiment -> sentiment != null)
				.map(sentiment -> new SentimentMerger(sentiment))
				.collect(
					Collectors.collectingAndThen(
						Collectors.reducing(
							SentimentMerger.ZERO,
							(s1, s2) -> s1.merge(s2)
						),
						merger -> merger.reduce()
					)
				);
	}
}
