package trie;


/**
 * ==== Attributes ====
 * - words: number of words
 * - term: the ITerm object
 * - prefixes: number of prefixes 
 * - references: Array of references to next/children Nodes
 * 
 * ==== Constructor ====
 * Node(String word, long weight)
 * 
 * @author Kun Hwi Ko
 */
public class Node
{
    private Term term;
    private int words;
    private int prefixes;
    private Node[] references;
    
    public Node() throws IllegalArgumentException {
    	this.term = null;
    	this.words = 0;
    	this.prefixes = 0;
    	this.references = new Node[26];
    }
	
	public Node(String query, long weight) throws IllegalArgumentException {
		if(query == null || weight < 0) 
			throw new IllegalArgumentException();
		this.term = new Term(query,weight); 
    	this.words = 0;
    	this.prefixes = 0;
		this.references = new Node[26];
	}
	
	/**
     * @return term value
     */
	public Term getTerm() {
		return this.term;
	}

	/**
     * @param term set term to this term
     */
	public void setTerm(Term term) {
		this.term = term;
	}
	
	/**
     * @return words value
     */
	public int getWords() {
		return this.words;
	}
	
	/**
     * @param words sets words to this value
     */
	public void setWords(int words) {
		this.words = words;
	}
	
	/**
     * @return prefixes value
     */
	public int getPrefixes() {
		return this.prefixes;
	}
	
    /**
     * @param prefixes sets prefixes to this value
     */
	public void setPrefixes(int prefixes) {
		this.prefixes = prefixes;
	}
	
	/**
     * @return references array
     */
	public Node[] getReferences() {
		return this.references;
	}
	
	/**
     * @return weight, tab, query
     */
	public String toString() {
		return Long.toString(this.term.getWeight()) + "\t" + this.term.getTerm();
	}
}
