package nl.jchmb.humor.classifier;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import nl.jchmb.humor.featurizer.Category;
import nl.jchmb.humor.featurizer.Featurizer;
import nl.jchmb.humor.featurizer.Observation;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class ClassifierAdapter {
	private Featurizer featurizer;
	private Classifier classifier;
	private Instances instances;
	
	public ClassifierAdapter(Classifier classifier, Featurizer featurizer) {
		this.classifier = classifier;
		this.featurizer = featurizer;
		this.instances = featurizer.instances();
	}
	
	public void printAverages(Stream<Observation> stream) {
		Map<Category, Integer> counts = new HashMap<>();
		counts.put(Category.HUMOROUS, 1185);
		counts.put(Category.MEMORABLE, 1588);
		Map<String, Map<Category, Double>> calc = new HashMap<>();
		featurizer.features().keySet().stream()
			.forEach(
					key -> {
						calc.put(key, new HashMap<>());
						calc.get(key).put(Category.HUMOROUS, 0.0d);
						calc.get(key).put(Category.MEMORABLE, 0.0d);
					}
			);
		stream.forEach(
				observation -> classify(observation).forEach(
						(category, weight) -> featurizer.features().forEach(
								(key, feature) -> calc.get(key).put(
										category,
										calc.get(key).get(category) + weight * feature.compute(observation)
								)
						)
				)
		);
		featurizer.features().keySet().stream()
			.forEach(
					key -> calc.get(key).forEach(
						(category, total) -> System.out.println(
								category + ": " + ((total) / ((double) counts.get(category)))
						)
					)
			);
	}
	
	public Map<Category, Double> classify(Observation observation) {
		double[] classification = classifyFeatures(
				featurizer.computeValues(observation)
		);
		return Arrays.asList(Category.values()).stream()
				.collect(
						Collectors.toMap(
								Function.identity(),
								category -> classification[category.ordinal()]
						)
				);
	}
	
	public double[] classifyFeatures(double[] features) {
		Instances tempInstances = new Instances(instances);
		Instance instance = instantiate(
				tempInstances,
				features
		);
		tempInstances.add(instance);
		try {
			return classifier.distributionForInstance(instance);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private Instance instantiateSupervised(Instances instances, double[] features, Category category) {
		Instance instance = instantiate(instances, features);
		instance.setValue((Attribute) instances.attribute(features.length), category.toString());
		return instance;
	}
	
	private Instance instantiate(Instances instances, double[] features) {
		Instance instance = new DenseInstance(features.length + 1);
		IntStream.range(0, features.length)
			.forEach(
				i -> instance.setValue(
						(Attribute) instances.attribute(i),
						features[i]
					)
			);
		return instance;
	}
	
	public Evaluation evaluate(Stream<Observation> stream) {
		Instances instances = new Instances(this.instances);
		stream.map(
				observation -> instantiateSupervised(
						instances,
						featurizer.computeValues(observation),
						observation.getCategory()
				)
			)
			.forEach(instances::add);
		
		Random rand = new Random(1);  // using seed = 1
		int folds = 10;
		try {
			Evaluation eval = new Evaluation(instances);
			eval.crossValidateModel(classifier, instances, folds, rand);
			return eval;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void train(Stream<Observation> stream) {
		Instances trainingSet = new Instances(instances);
		stream.map(
				observation -> instantiate(
						trainingSet,
						featurizer.computeValues(observation)
				)
			)
			.forEach(trainingSet::add);
		try {
			classifier.buildClassifier(trainingSet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void trainFeatures(Instances instances, double[] features, Category category) {
		Instance instance = new DenseInstance(features.length + 1);
		IntStream.range(0, features.length)
			.forEach(
					i -> instance.setValue(
							(Attribute) instances.attribute(i),
							features[i]
						)
			);
		instance.setClassValue(category.toString());
		instances.add(instance);
	}
	
	public static ClassifierAdapter naiveBayes(Featurizer featurizer) {
		return new ClassifierAdapter(new NaiveBayes(), featurizer);
	}
}
