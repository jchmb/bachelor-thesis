package nl.jchmb.humor.source.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.stream.Collectors;

import jodd.jerry.Jerry;

public class JerryFactory {
	public static Jerry produce(URL url) throws IOException {
		return produce(url.openStream());
	}
	
	public static Jerry produce(File file) throws FileNotFoundException, IOException {
		return produce(new BufferedReader(new FileReader(file)));
	}
	
	public static Jerry produce(BufferedReader reader) throws IOException {
		String content = reader.lines()
				.collect(Collectors.joining(""));
		reader.close();
		return produce(content);
	}
	
	public static Jerry produce(InputStream inputStream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		return produce(reader);
	}
	
	public static Jerry produce(String content) {
		return Jerry.jerry(content);
	}
}
