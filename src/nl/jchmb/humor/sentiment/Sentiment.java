package nl.jchmb.humor.sentiment;

public class Sentiment {
	public static final Sentiment NEUTRAL = new Sentiment(0.0d, 0.0d);
	private final double positivity, negativity;

	public Sentiment(double positivity, double negativity) {
		this.positivity = positivity;
		this.negativity = negativity;
	}

	public double positivity() {
		return positivity;
	}

	public double negativity() {
		return negativity;
	}
	
	public double subjectivity() {
		return positivity + negativity;
	}
	
	public double objectivity() {
		return 1.0d - (positivity + negativity);
	}
	
	public double potential() {
		return positivity - negativity;
	}
	
	public boolean isNeutral() {
		return equals(NEUTRAL);
	}
	
	@Override
	public boolean equals(Object that) {
		if (!(that instanceof Sentiment)) {
			return false;
		}
		Sentiment thatSentiment = (Sentiment) that;
		return this.positivity == thatSentiment.positivity &&
				this.negativity == thatSentiment.negativity;
	}
	
	@Override
	public int hashCode() {
		return Double.hashCode(positivity) + 13 * Double.hashCode(negativity);
	}
}