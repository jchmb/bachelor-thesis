package nl.jchmb.humor.main;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.RAMDictionary;
import nl.jchmb.humor.classifier.ClassifierAdapter;
import nl.jchmb.humor.featurizer.Category;
import nl.jchmb.humor.featurizer.Featurizer;
import nl.jchmb.humor.featurizer.Observation;
import nl.jchmb.humor.featurizer.ObservationFactory;
import nl.jchmb.humor.featurizer.feature.Feature;
import nl.jchmb.humor.featurizer.feature.sentiment.SentimentChangeFeature;
import nl.jchmb.humor.featurizer.source.ObservationSourceProvider;
import nl.jchmb.humor.sentiment.SentiWordNet;
import nl.jchmb.humor.sentiment.SentiWordNetFactory;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.LMT;
import weka.classifiers.trees.RandomForest;

class AverageFeaturesMain {
	public static void main(String[] args) {
		URL url = null;
		try {
			url = new URL("file", null, "/home/jochem/workspace/java8-humor/res/dict");
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		double baseline = 0.5729d;
		IDictionary dictionary = new Dictionary(url); // TMP file here
		try {
			dictionary.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
		SentiWordNet sentimentDictionary = new SentiWordNetFactory(dictionary).produce();
		//SentiWordNet sentimentDictionary = new SentiWordNetFactory(dictionary).produce();
		//Featurizer featurizer = Featurizer.real(sentimentDictionary);
		Featurizer featurizer = Featurizer.real(sentimentDictionary, dictionary);
		Map<String, Feature> features = featurizer.features();
		System.out.println("\\begin{tabular}{| l | l | l |}");
		System.out.println("\\hline");
		System.out.println("\\textbf{Feature} & \\textbf{Humorous} & \\textbf{Memorable} & \\textbf{Common language} \\\\");
		ObservationFactory factory = new ObservationFactory();
		ObservationSourceProvider provider = new ObservationSourceProvider(factory);
		Featurizer individualFeaturizer;
		int i = 0;
		for (Map.Entry<String, Feature> entry : featurizer.features().entrySet()) {
			Feature f = entry.getValue();
			Map<Category, List<Observation>> map = provider.stream()
				.collect(
					Collectors.groupingBy(observation -> observation.getCategory())
				);
			Map<Category, Double> values = map.keySet().stream()
				.collect(
					Collectors.toMap(
							k -> k,
							k -> map.get(k).stream()
									.mapToDouble(
											o -> f.compute(o)
									)
									.average()
									.getAsDouble()
					)
				);
				System.out.format(
						"%s & %.2f & %.2f & %.2f \\\\ \n",
						entry.getKey(),
						values.get(Category.HUMOROUS),
						values.get(Category.MEMORABLE),
						values.get(Category.OTHER)
				);
		}
		System.out.println("\\end{tabular}");
	}
}
