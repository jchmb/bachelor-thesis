package nl.jchmb.humor.source.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.stream.Stream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import jodd.jerry.Jerry;

public class BrainyQuoteInspirationalExtractor {
	public static final String ADDRESS = "http://www.brainyquote.com/quotes/topics/topic_inspirational{i}.html";
	
	public static void main(String[] args) throws MalformedURLException, IOException {
		HttpClient client = HttpClientBuilder.create().build();
		PrintWriter writer = new PrintWriter(new FileOutputStream(new File("data/bq-inspirational.txt")));
		for (int i = 1; i <= 14; i++) {
			HttpGet get = new HttpGet(
					ADDRESS.replace("{i}", Integer.toString(i))
			);
			HttpResponse response = client.execute(get);
			Stream<Jerry> stream = JerryStream.of(
					JerryFactory.produce(
							response.getEntity().getContent()
					),
					".masonryitem span.bqQuoteLink a"
			);
			stream.map(jerry -> jerry.text())
					.forEach(line -> writer.write(line + "\n"));
		}
		writer.close();
	}
}
