package nl.jchmb.humor.ngram;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NGram<T> {
	private final List<T> tokens;

	public NGram(List<T> tokens) {
		this.tokens = Collections.unmodifiableList(tokens);
	}

	public List<T> list() {
		return tokens;
	}

	public T get(int offset) {
		return tokens.get(offset);
	}
	
	public int length() {
		return tokens.size();
	}

	@Override
	public int hashCode() {
		return tokens.hashCode();
	}

	@Override
	public boolean equals(Object that) {
		return tokens.equals(that);
	}
	
	public static <T> NGram<T> of(T... tokens) {
		return new NGram<>(Arrays.asList(tokens));
	}
}