package nl.jchmb.humor.sentiment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.SynsetID;

public class SentiWordNetFactory {
	private IDictionary dictionary;
	private File file;
	
	private static final int INDEX_POS = 0;
	private static final int INDEX_ID = 1;
	private static final int INDEX_POSITIVITY = 2;
	private static final int INDEX_NEGATIVITY = 3;
	//private static final int INDEX_WORDS = 4;
	//private static final int INDEX_GLOSS = 5;
	private static final int ENTRY_SIZE = 6;
	
	public SentiWordNetFactory(IDictionary dictionary) {
		this.dictionary = dictionary;
		this.file = new File("res/sentiment/sentiwordnet.txt");
	}
	
	public SentiWordNet produce() {
		try {
			Map<ISynsetID, Sentiment> sentiments = Files.lines(Paths.get(file.getAbsolutePath()))
				.filter(line -> !line.startsWith("#"))
				.map(line -> line.split("\t"))
				.filter(parts -> parts.length >= ENTRY_SIZE)
				.filter(parts -> parts[INDEX_POS].length() == 1)
				.collect(
					Collectors.toMap(
						parts -> new SynsetID(
							Integer.parseInt(parts[INDEX_ID]),
							getPOS(parts[INDEX_POS].charAt(0))
						),
						parts -> new Sentiment(
							Double.parseDouble(parts[INDEX_POSITIVITY]),
							Double.parseDouble(parts[INDEX_NEGATIVITY])
						)
					)
				);
			return new SentiWordNet(dictionary, sentiments);
		} catch (Exception e) {
			e.printStackTrace();
			//System.exit(1);
			return null;
		}
	}
	
	private POS getPOS(char tag) {
		return POS.getPartOfSpeech(tag);
	}
}
