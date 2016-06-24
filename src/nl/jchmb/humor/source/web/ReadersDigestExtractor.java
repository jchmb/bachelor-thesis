package nl.jchmb.humor.source.web;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.stream.Stream;

import jodd.jerry.Jerry;

public class ReadersDigestExtractor {
	private static final String ADDRESS = "http://www.rd.com/jokes/funny-quotes/";
	
	public Stream<String> fetch() {
		try {
			Stream<Jerry> stream = JerryStream.of(
					JerryFactory.produce(new File("res/rd.html")),
					"article.joke"
			);
			return stream.map(
					article -> article.$("p")
									.first()
									.text()
									.replaceAll("\r\n|\r|\n", " ")
									.replace(new Character((char) 8232).toString(), "")
									.trim()
			);
		} catch (IOException e) {
			e.printStackTrace();
			return Stream.empty();
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(new FileOutputStream(new File("data/rd.txt")));
		new ReadersDigestExtractor().fetch()
				.forEach(quote -> writer.write(quote + "\n"));
		writer.close();
	}
}
