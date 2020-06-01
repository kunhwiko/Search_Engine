
/**
 * @author Kun Hwi Ko
 */

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import search;

public static void main(String[] args){
    Search search = new Search();
    List<String> arr = new ArrayList<>();
    arr.add(args);                          // input RSS 
    Map<String, List<String>> docs = ib.parseFeed(arr);

    Map<String, Map<String, Double>> index = ib.buildIndex(docs);

	Map<String, ArrayList<Entry<String, Double>>> inverted = 
		(Map<String, ArrayList<Entry<String, Double>>>) ib.buildInvertedIndex(index);

	List<Entry<String, List<String>>> homepage = (List<Entry<String, List<String>>>) ib.buildHomePage(inverted);

	Collection<?> res = ib.createAutocompleteFile(homepage);

	List<String> articles = ib.searchArticles("data",inverted);
}