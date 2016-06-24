package nl.jchmb.humor.source.web;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import jodd.jerry.Jerry;

public class JerryStream {
	public static Stream<Jerry> of(Jerry jerry, String selector) {
		return StreamSupport.stream(jerry.$(selector).spliterator(), false);
	}
}
