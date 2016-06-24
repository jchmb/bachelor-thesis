package nl.jchmb.humor.featurizer.source;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import nl.jchmb.humor.featurizer.Category;
import nl.jchmb.humor.featurizer.Observation;
import nl.jchmb.humor.featurizer.ObservationFactory;
import nl.jchmb.utils.tuple.Tuple2;

public class ObservationSourceProvider {
	private Map<Category, List<ObservationSource>> sources;
	private ObservationFactory factory;
	
	public ObservationSourceProvider(ObservationFactory factory) {
		this.factory = factory;
		sources = new HashMap<>();
		
		//add(Category.MEMORABLE, new File("data/bq-funny.txt"));
		//add(Category.MEMORABLE, new File("data/rd.txt"));
		add(Category.HUMOROUS, new File("data/bq-funny.txt"));
		add(Category.HUMOROUS, new File("data/rd.txt"));
		add(Category.MEMORABLE, new File("data/bq-inspirational.txt"));
		add(Category.MEMORABLE, new File("data/bq-motivational.txt"));
		add(Category.MEMORABLE, new File("data/bq-success.txt"));
		//add(Category.OTHER, new File("data/gutenberg.txt"));
	}
	
	public void add(Category category, File file) {
		add(category, new FileSource(file, factory));
	}
	
	public void add(Category category, ObservationSource source) {
		sources.computeIfAbsent(
				category,
				key -> new ArrayList<>()
			)
			.add(source);
	}
	
	public Stream<Observation> stream() {
		return sources.entrySet().stream()
				.flatMap(
						(Map.Entry<Category, List<ObservationSource>> entry) -> entry.getValue().stream()
										.flatMap(source -> source.stream(entry.getKey()))
				)
				.filter(observation -> observation.flatStream().count() > 0L);
	}
}
