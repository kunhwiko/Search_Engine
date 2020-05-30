package autocomplete;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Kun Hwi Ko
 */

public class Autocomplete implements IAutocomplete {

	private Node root;
	private int maxDisplay;
	
	@Override
	public void addWord(String word, long weight) {
		/* initialize root */
		if (root == null) {
			root = new Node();
		}
		/* if word is invalid, then do not add */
		for(int i = 0; i < word.length(); i++) {
			if(!Character.isLetter(word.charAt(i)))
				return;
		}
		addWord(root, word.toLowerCase(), word.toLowerCase(), weight);
	}

	/**
	 * helper function for addWord
	 * 
	 * @param vertex   current vertex
	 * @param fullWord original word
	 * @param word     substring of word
	 * @param weight   weight of term
	 */
	private void addWord(Node vertex, String fullWord, String word, long weight) {
		/* if words is empty, increment words, prefixes, and add term to the node */
		if (word.equals("")) {
			vertex.setWords(vertex.getWords() + 1);
			vertex.setPrefixes(vertex.getPrefixes() + 1);
			vertex.setTerm(new Term(fullWord, weight));
		} else {
			/* increment prefixes */
			vertex.setPrefixes(vertex.getPrefixes() + 1);

			/* get first letter of word and change to : 'a':0 'b':1 ...'z':25 */
			int k = word.charAt(0) - 97;

			/*
			 * if references array has a null node at index k, initialize and recursively call
			 * addWord again until word is ""
			 */
			if (vertex.getReferences()[k] == null) {
				vertex.getReferences()[k] = new Node();
			}
			addWord(vertex.getReferences()[k], fullWord, word.substring(1), weight);
		}
	}

	@Override 
	public Node buildTrie(String filename, int k) { 
		try {
			/* k should be a non-negative number */
			if (k < 0) {
				throw new IllegalArgumentException();
			} else {
				maxDisplay = k;
			}

			/* use bufferedreader to read one line at a time from file */
			BufferedReader br = new BufferedReader(new FileReader(filename)); 
			String line = br.readLine();

			/* continue until end of document while ignoring the first line */
			while ((line = br.readLine()) != null) {
				/* if line is only white space, move to next line */
				if (line.trim().equals(""))
					continue;
				/* tokens[0] contains weight and tokens[1] contains query */
				String[] tokens = line.split("\t");
				long weight = Long.parseLong(tokens[0].trim());
				String query = tokens[1].trim();
				addWord(query.toLowerCase(), weight);
			}
			return root;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			System.out.println("Exception has occurred");
			return null;
		}
	}

	@Override
	public int numberSuggestions() {
		return maxDisplay;
	}

	@Override
	public Node getSubTrie(String prefix) {
		/* if word is invalid, then do not add */
		for(int i = 0; i < prefix.length(); i++) {
			if(!Character.isLetter(prefix.charAt(i)))
				return null;
		}
		
		Node curr = this.root; 
		String lowerCasePrefix = prefix.toLowerCase();

		/* traverse through the trie until curr is null or prefix is an empty string */
		while (curr != null && !lowerCasePrefix.equals("")) {
			int firstChar = lowerCasePrefix.charAt(0) - 97;
			curr = curr.getReferences()[firstChar];
			lowerCasePrefix = lowerCasePrefix.substring(1);
		}
		/* if curr is null (prefix not found) return null or return subtrie */
		return curr == null ? null : curr; 
	}

	@Override
	public int countPrefixes(String prefix) { 
		/* if word is invalid, then do not add */
		for(int i = 0; i < prefix.length(); i++) {
			if(!Character.isLetter(prefix.charAt(i)))
				return 0;
		}	
		Node prefixRoot = getSubTrie(prefix.toLowerCase());
		
		/* if prefixRoot is null (prefix not found) return 0 or return count */
		return prefixRoot == null ? 0 : prefixRoot.getPrefixes(); 
	}
 
	@Override
	public List<ITerm> getSuggestions(String prefix) {
		List<ITerm> arrList = new ArrayList<>();
		/* if word is invalid, then do not add */
		for(int i = 0; i < prefix.length(); i++) {
			if(!Character.isLetter(prefix.charAt(i)))
				return arrList;
		}
		/* put words with following prefix to arraylist and sort */
		Node newRoot = this.getSubTrie(prefix.toLowerCase());
		addToList(newRoot, arrList);
		Collections.sort(arrList, ITerm.byPrefixOrder(prefix.length()));
		return arrList;
	}

	/**
	 * helper function for addToList, traverse through the trie and add terms to
	 * list
	 * 
	 * @param vertex  current position at trie
	 * @param arrList list of all suggestions
	 * 
	 */
	private void addToList(Node vertex, List<ITerm> arrList) {
		if(vertex == null) return;
		/* if the current vertex has a term, add to arrList */
		if (vertex.getTerm() != null) {
			arrList.add(vertex.getTerm());
		}
		/* recursively continue addToList */
		for (int i = 0; i < vertex.getReferences().length; i++) {
			if (vertex.getReferences()[i] != null) {
				addToList(vertex.getReferences()[i], arrList);
			}
		}
	}

	public Node getRoot() {
		return this.root;
	}
}
