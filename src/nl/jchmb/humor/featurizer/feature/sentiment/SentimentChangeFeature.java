package nl.jchmb.humor.featurizer.feature.sentiment;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import Jama.Matrix;
import edu.stanford.nlp.ling.TaggedWord;
import nl.jchmb.humor.featurizer.Observation;
import nl.jchmb.humor.featurizer.feature.Feature;
import nl.jchmb.humor.sentiment.SentiWordNet;
import nl.jchmb.humor.sentiment.Sentiment;
import nl.jchmb.utils.tuple.Tuple2;

public class SentimentChangeFeature implements Feature {
	private static final int INDEX_OFFSET = 0;
	private static final int INDEX_SLOPE = 1;
	private SentiWordNet sentimentDictionary;
	
	private int index;
	
	public SentimentChangeFeature(SentiWordNet sentimentDictionary, int index) {
		this.sentimentDictionary = sentimentDictionary;
		this.index = index;
	}
	
	public double[] linearSquares(Stream<TaggedWord> tags) {
		List<Sentiment> sentiments = tags
									.map(tag -> sentimentDictionary.sentiment(tag))
									.collect(Collectors.toList());
		List<Tuple2<Integer, Sentiment>> actualSentiments = IntStream.range(0, sentiments.size())
				.filter(i -> sentiments.get(i) != null)
				.boxed()
				.map(i -> new Tuple2<>(i, sentiments.get(i)))
				.collect(
						Collectors.toList()
				);
		if (actualSentiments.size() < 2) {
			return new double[]{0.0d, 0.0d};
		}
		double[][] aValues = new double[actualSentiments.size()][2];
		double[][] bValues = new double[actualSentiments.size()][1];
		IntStream.range(0, actualSentiments.size())
				.forEach(i -> {
					aValues[i][0] = 1.0d;
					aValues[i][1] = actualSentiments.get(i).get1();
					bValues[i][0] = actualSentiments.get(i).get2().potential();
				});
		Matrix b = new Matrix(bValues);
		Matrix a = new Matrix(aValues);
		Matrix x = a.solve(b);
		return new double[]{x.get(0, 0), x.get(1, 0)};
	}

	@Override
	public double compute(Observation observation) {
		double value = linearSquares(observation.flatStream())[index];
		
		return value == 0 ? 0 : Math.log(value);
	}
	
	public static Feature potentialOffset(SentiWordNet sentimentDictionary) {
		return new SentimentChangeFeature(sentimentDictionary, INDEX_OFFSET);
	}
	
	public static Feature potentialSlope(SentiWordNet sentimentDictionary) {
		return new SentimentChangeFeature(sentimentDictionary, INDEX_SLOPE);
	}

}
