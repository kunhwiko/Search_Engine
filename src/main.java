
/**
 * @author Kun Hwi Ko
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import engine.Search;

public class Main{
    public static void main(String[] args){
        Search search = new Search();
        List<String> arr = new ArrayList<>();
        arr.add("input RSS link here");              // input RSS 

        Map<String, List<String>> docs = search.parseFeed(arr);
        Map<String, Map<String, Double>> index = search.buildIndex(docs);
        Map<String, ArrayList<Entry<String, Double>>> inverted = 
            (Map<String, ArrayList<Entry<String, Double>>>) search.buildInvertedIndex(index);
        List<Entry<String, List<String>>> homepage = (List<Entry<String, List<String>>>) search.buildHomePage(inverted);

        // creates a file called autocomplete.txt that shows all the words found in lexicographic order
        Collection<?> listOfWords = search.createAutocompleteFile(homepage);

        // when user inputs a term, documents containing the word are returned in most relevant order 
        List<String> articles = search.searchArticles("input word",inverted);  // input word
    }
}
