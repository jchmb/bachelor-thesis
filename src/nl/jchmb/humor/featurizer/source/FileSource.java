package nl.jchmb.humor.featurizer.source;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import nl.jchmb.humor.featurizer.Category;
import nl.jchmb.humor.featurizer.Observation;
import nl.jchmb.humor.featurizer.ObservationFactory;

public class FileSource implements ObservationSource {
	private File file;
	private ObservationFactory factory;
	
	public FileSource(File file, ObservationFactory factory) {
		this.file = file;
		this.factory = factory;
	}
	
	@Override
	public Stream<Observation> stream(Category category) {
		try {
			return Files.lines(Paths.get(file.getAbsolutePath()))
					.map(quote -> factory.produce(quote, category));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Stream.empty();
		}
	}

}
