package trie;

/**
 * @author Kun Hwi Ko
 */

public class Term implements ITerm {

	private String query;
	private long weight;

	public Term(String query, long weight) throws IllegalArgumentException {
		if (query == null || weight < 0)
			throw new IllegalArgumentException();
		this.query = query;
		this.weight = weight;
	}

	@Override
	public int compareTo(ITerm that) {
		String w1 = getTerm();
		String w2 = ((Term) that).getTerm();

		return w1.compareTo(w2);
	}

	/**
	 * @return weight value
	 */
	public long getWeight() {
		return this.weight;
	}

	/**
	 * @return query value
	 */
	public String getTerm() {
		return this.query;
	}

	/**
	 * @param weight set to this weight
	 */
	public void setWeight(long weight) {
		this.weight = weight;
	}

	/**
	 * @param query set to this query
	 */
	public void setTerm(String query) {
		this.query = query;
	}
	
	@Override
	public String toString() {
		return Long.toString(this.weight) + "\t" + query;
	}

}
