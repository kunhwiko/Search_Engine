
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
        arr.add(args[0]);                          // input RSS 
        Map<String, List<String>> docs = search.parseFeed(arr);
    
        Map<String, Map<String, Double>> index = search.buildIndex(docs);
    
        Map<String, ArrayList<Entry<String, Double>>> inverted = 
            (Map<String, ArrayList<Entry<String, Double>>>) search.buildInvertedIndex(index);
    
        List<Entry<String, List<String>>> homepage = (List<Entry<String, List<String>>>) search.buildHomePage(inverted);
    
        Collection<?> res = search.createAutocompleteFile(homepage);
    
        List<String> articles = search.searchArticles(args[1],inverted);
    }
}
