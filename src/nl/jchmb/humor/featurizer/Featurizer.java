package nl.jchmb.humor.featurizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.mit.jwi.IDictionary;
import nl.jchmb.humor.featurizer.feature.AbstractionFeature;
import nl.jchmb.humor.featurizer.feature.AmbiguityFeature;
import nl.jchmb.humor.featurizer.feature.Feature;
import nl.jchmb.humor.featurizer.feature.TagCountingFeature;
import nl.jchmb.humor.featurizer.feature.TagDetectionFeature;
import nl.jchmb.humor.featurizer.feature.WordCountingFeature;
import nl.jchmb.humor.featurizer.feature.sentiment.SentimentChangeFeature;
import nl.jchmb.humor.featurizer.feature.sentiment.SentimentFeature;
import nl.jchmb.humor.featurizer.feature.sentiment.SentimentValuationCountFeature;
import nl.jchmb.humor.sentiment.SentiWordNet;
import nl.jchmb.utils.tuple.Tuple2;
import weka.core.Attribute;
import weka.core.Instances;

public class Featurizer {
	private Map<String, Feature> features;
	
	public Featurizer() {
		this(new HashMap<>());
	}
	
	public Featurizer(Map<String, Feature> features) {
		this.features = features;
	}
	
	public Featurizer add(String key, Feature feature) {
		features.put(key, feature);
		return this;
	}
	
	public Featurizer add(Stream<Tuple2<String, Feature>> featureStream) {
		featureStream.forEach(tuple -> add(tuple.get1(), tuple.get2()));
		return this;
	}
	
	public Map<String, Feature> features() {
		return features;
	}
	
	public Instances instances() {
		List<Attribute> attributes = attributes();
		Instances instances = new Instances(
				"Rel",
				new ArrayList<>(attributes),
				attributes.size()
		);
		instances.setClassIndex(attributes.size() - 1);
		return instances;
	}
	
	private List<Attribute> attributes() {
		return Stream.concat(
				features.entrySet().stream()
					.map(entry -> new Attribute(entry.getKey())),
				Stream.of(
					new Attribute(
							"category",
							Arrays.asList(Category.values()).stream()
								.map(category -> category.toString())
								.collect(Collectors.toList())
					)	
				)
			)
			.collect(Collectors.toList());	
	}
	
	public double[] computeValues(Observation observation) {
		return compute(observation).values().stream()
				.mapToDouble(Double::doubleValue)
				.toArray();
	}
	
	public Map<String, Double> compute(Observation observation) {
		return features.entrySet().stream()
				.collect(
						Collectors.toMap(
								entry -> entry.getKey(),
								entry -> entry.getValue().compute(observation)
						)
				);
	}
	
	public static Featurizer dummy() {
		return new Featurizer()
				.add("noun_count", Feature.tagCounting("NN"))
				.add("length", Feature.sentenceLength());
	}
	
	public static Featurizer experimental(IDictionary dictionary) {
		return new Featurizer()
				//.add("noun_count", Feature.tagCounting("NN"))
				.add(
						TagCountingFeature.of(
								"NN",
								"JJ",
								"VB"
						)
				)
				.add("abstraction_score", new AbstractionFeature(dictionary));
	}
	
	// Here is where the tools should be provided.
	public static Featurizer real(SentiWordNet sentimentDictionary, IDictionary dictionary) {
		return new Featurizer()
				.add(
						TagCountingFeature.of(
								"NN",
								"JJ"
						)
				)
				.add("abstraction_score", new AbstractionFeature(dictionary))
				//.add("ambiguity", new AmbiguityFeature(dictionary))
				//.add("length", Feature.sentenceLength())
				.add("indefinite", WordCountingFeature.indefiniteCount())
				.add("third_person", WordCountingFeature.thirdPersonPronounsCount())
				.add("past_tense", WordCountingFeature.pastTenseCount())
		
				.add("sentiment_positivity", SentimentFeature.positivity(sentimentDictionary))
				.add("sentiment_negativity", SentimentFeature.negativity(sentimentDictionary))
				.add("sentiment_objectivity", SentimentFeature.objectivity(sentimentDictionary))
				//.add("sentiment_min_positivity", SentimentFeature.minimumPositivity(sentimentDictionary))
				//.add("sentiment_min_negativity", SentimentFeature.minimumNegativity(sentimentDictionary))
				//.add("sentiment_max_positivity", SentimentFeature.maximumPositivity(sentimentDictionary))
				//.add("sentiment_max_negativity", SentimentFeature.maximumNegativity(sentimentDictionary))
				
				.add("sentiment_potential_offset", SentimentChangeFeature.potentialOffset(sentimentDictionary))
				.add("sentiment_potential_slope", SentimentChangeFeature.potentialSlope(sentimentDictionary))
				
				.add(TagDetectionFeature.tagsDetectors("NN", "VB", "JJ"))
				.add("neg_to_pos", SentimentValuationCountFeature.negativeToPositive(sentimentDictionary))
				.add("pos_to_neg", SentimentValuationCountFeature.positiveToNegative(sentimentDictionary))
				
				.add("ambiguity", new AmbiguityFeature(dictionary))
				
				;
	}
}
