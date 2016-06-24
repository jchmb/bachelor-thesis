package nl.jchmb.humor.ngram;

import java.util.List;
import java.util.stream.Stream;

import nl.jchmb.utils.stream.SlidingWindow;

public class NGramStream {
	public static <T> Stream<NGram<T>> of(Stream<List<T>> stream) {
		return stream.map(
			list -> new NGram<>(list)
		);
	}

	public static <T> Stream<NGram<T>> of(Stream<T> stream, int n) {
		return of(SlidingWindow.of(stream, n));
	}
}