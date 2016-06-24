package nl.jchmb.humor.source.gutenberg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GutenbergExtractor {
	public static void main(String[] args) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new File("data/gutenberg.txt"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		File dir = new File("res/gutenberg");
		String content = "";
		boolean reading = false;
		int limit = 1000;
		for (File file : dir.listFiles()) {
			try {
				List<String> list = Files.lines(
						Paths.get(file.getAbsolutePath()),
						StandardCharsets.ISO_8859_1
				).collect(Collectors.toList());
				for (int i = 0; i < list.size(); i++) {
					String line = list.get(i);
					if (line.isEmpty()) {
						reading = false;
						content = "";
					}
					if (reading) {
						content += " ";
					}
					for (int j = 0; j < line.length(); j++) {
						if (reading) {
							if (line.charAt(j) == '"') {
								reading = false;
								content += "\n";
								content = content.replaceAll("( )+", " ");
								if (content.length() > 40 && content.length() < 200) {
									writer.write(content);
								} else {
									limit++;
								}
								content = "";
								if (limit-- <= 0) {
									writer.close();
									System.exit(1);
								}
							} else {
								content += line.charAt(j);
							}
						} else {
							if (line.charAt(j) == '"') {
								reading = true;
							}
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		writer.close();
	}
}
