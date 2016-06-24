package nl.jchmb.humor.main;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

import edu.mit.jwi.Dictionary;
import nl.jchmb.humor.classifier.ClassifierAdapter;
import nl.jchmb.humor.featurizer.Featurizer;
import nl.jchmb.humor.featurizer.Observation;
import nl.jchmb.humor.featurizer.ObservationFactory;
import nl.jchmb.humor.featurizer.feature.Feature;
import nl.jchmb.humor.featurizer.feature.sentiment.SentimentChangeFeature;
import nl.jchmb.humor.featurizer.source.ObservationSourceProvider;
import nl.jchmb.humor.sentiment.SentiWordNet;
import nl.jchmb.humor.sentiment.SentiWordNetFactory;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.LMT;
import weka.classifiers.trees.RandomForest;

class TestMain {
	public static void main(String[] args) {
		Dictionary dictionary = new Dictionary(new File("res/dict")); // TMP file here
		try {
			dictionary.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SentiWordNet sentimentDictionary = new SentiWordNetFactory(dictionary).produce();
		Featurizer featurizer = Featurizer.real(sentimentDictionary, dictionary);
		ObservationFactory factory = new ObservationFactory();
		//ClassifierAdapter classifier = new ClassifierAdapter(new NaiveBayes(), featurizer);
		J48 classifier = new J48();
		classifier.setBinarySplits(true);
		//Classifier classifier = new NaiveBayes();
		//SMO classifier = new SMO();
		//LMT tree = new LMT();
		//RandomForest forest = new RandomForest();
		//forest.set		
		ClassifierAdapter classifierAdapter = new ClassifierAdapter(
				classifier,
				featurizer
		);
		ObservationSourceProvider provider = new ObservationSourceProvider(factory);
//		provider.stream()
//			.collect(Collectors.groupingBy(observation -> observation.getCategory()))
//			.entrySet().stream()
//			.forEach(
//				entry -> System.out.println(
//						entry.getKey() + ": " +
//								entry.getValue().stream()
//										.mapToDouble(
//												observation -> observation.flatStream().count()
//										)
//										.average()
//						)
//			);
		System.out.println(
				classifierAdapter.evaluate(provider.stream())
					.toSummaryString()
		);
		//classifierAdapter.train(provider.stream());
		//classifierAdapter.printAverages(provider.stream());
		//SentimentChangeFeature feature = new SentimentChangeFeature(sentimentDictionary, 0);
//		provider.stream()
//				.limit(100)
//				.forEach(
//						observation -> {
//							double[] c = feature.linearSquares(observation.flatStream());
//							System.out.println(c[1] + "x " + c[0] + "::: " + observation.getQuote());
//						}
//				);
	}
}
