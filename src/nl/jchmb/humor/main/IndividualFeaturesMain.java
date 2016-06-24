package nl.jchmb.humor.main;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.stream.Collectors;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.RAMDictionary;
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
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.LMT;
import weka.classifiers.trees.RandomForest;

class IndividualFeaturesMain {
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
		//SentiWordNet sentimentDictionary = new SentiWordNetFactory(dictionary).produce();
		//Featurizer featurizer = Featurizer.real(sentimentDictionary);
		Featurizer featurizer = Featurizer.experimental(dictionary);
		Map<String, Feature> features = featurizer.features();
		Featurizer individualFeaturizer;
		System.out.println("\\begin{tabular}{| l | l | l |}");
		System.out.println("\\hline");
		System.out.println("\\textbf{Feature} & \\textbf{Accuracy} & \\textbf{Gain} \\\\");
		ObservationFactory factory = new ObservationFactory();
		ObservationSourceProvider provider = new ObservationSourceProvider(factory);
		int i = 0;
		for (Map.Entry<String, Feature> entry : features.entrySet()) {
			System.out.println("\\hline");
			individualFeaturizer = new Featurizer();
			individualFeaturizer.add(entry.getKey(), entry.getValue());
			
			J48 classifier = new J48();
			classifier.setBinarySplits(true);
			ClassifierAdapter classifierAdapter = new ClassifierAdapter(
				classifier,
				individualFeaturizer
			);	
			Evaluation evaluation = classifierAdapter.evaluate(provider.stream());
			double accuracy = evaluation.pctCorrect();
			double gain = accuracy - (baseline * 100.0d);
			System.out.format(
					"%s & %.2f & %.2f \\\\ \n",
					entry.getKey()
						.replace("_", "\\_"),
					accuracy,
					gain
			);
		}
		System.out.println("\\hline");
		System.out.println("\\end{tabular}");
	}
}
