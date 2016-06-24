package nl.jchmb.humor.featurizer.feature;

import java.util.List;
import java.util.function.Function;

import edu.stanford.nlp.ling.TaggedWord;
import nl.jchmb.humor.featurizer.Observation;

public class Distinctiveness implements Feature {
	private List<MarkovModel<String>> markovModels;
	private Function<TaggedWord, String> f;

	public Distinctiveness(List<MarkovModel<String>> markovModels, Function<TaggedWord, String> f) {
		this.markovModels = markovModels;
		this.f = f;
	}

	public double compute(Observation observation) {
		return observation.sentences().stream()
				.mapToDouble(this::compute)
				.average();
	}

	private double compute(List<TaggedWord> sentence) {
		return markovModels.stream()
			.mapToDouble(
				markovModel -> markovModel.probability(
						sentence.stream()
							.map(f)
				)
			)
			.average();
	}
}