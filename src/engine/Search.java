package engine;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.AbstractMap.SimpleEntry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Kun Hwi Ko
 */

public class Search implements ISearch
{

    @Override
    public Map<String, List<String>> parseFeed(List<String> feeds) {
        try {
            Map<String, List<String>> res = new HashMap<>();

            for (String rssFeed : feeds) {
                /* get links of each rss file */
                Document doc = Jsoup.connect(rssFeed).get();
                Elements links = doc.getElementsByTag("link");

                for (Element link : links) {
                    String htmlText = link.text();

                    /* get body of each html link */
                    Document html = Jsoup.connect(htmlText).get();
                    Elements unparsedText = html.getElementsByTag("body");

                    /* parse the text part of <body> for each html link */
                    String parsedText = unparsedText.get(0).text();

                    /* break the text into words and put into list */
                    List<String> value = new ArrayList<>();

                    /* use split to parse into words */
                    for (String word : parsedText.split(" ")) {
                        /* let's -> lets, self-balancing -> selfbalancing */
                        while (word.contains("'")) {
                            int index = word.indexOf("'");
                            word = word.substring(0, index) + word.substring(index + 1);
                        }
                        while (word.contains("-")) {
                            int index = word.indexOf("-");
                            word = word.substring(0, index) + word.substring(index + 1);
                        } 
                        while (word.contains("(")) {
                            int index = word.indexOf("(");
                            word = word.substring(0, index) + word.substring(index + 1);
                        } 
                        /* remove all numerics in string */
                        word = word.replaceAll("[*0-9]", "");
                        /*
                         * use regex to parse punctuation, colons etc, then if not empty change to
                         * lowercase
                         */
                        word = word.split("\\W+")[0];
                        if (!word.equals(""))
                            value.add(word.toLowerCase());
                    }
                    res.put(htmlText, value);
                }
            }
            return res;
        } catch (IllegalArgumentException e) {
            return null;
        } catch (IOException e) {
            return null;
        } 
    }

    @Override
    public Map<String, Map<String, Double>> buildIndex(Map<String, List<String>> docs) {
        Map<String, Map<String, Double>> res = new HashMap<>();

        /* go through every key in docs and calculate respective TF-IDF */
        for (String key : docs.keySet()) {
            Map<String, Double> counter = new HashMap<>();
            
            /* treemap will allow for lexicographical order of value */
            Map<String, Double> forwardIndex = new TreeMap<>();

            /* go through every word in an html file to create a counter */
            for (String value : docs.get(key)) {
                counter.put(value, counter.getOrDefault(value, 0.0) + 1);
            }

            /* calculate TF and IDF values */
            for (String keyWord : counter.keySet()) {
                /* TF value */
                double tf = counter.get(keyWord) / docs.get(key).size();

                /* find the number of documents with current term */
                int termCount = 0;
                for (String key2 : docs.keySet()) {
                    if (docs.get(key2).contains(keyWord))
                        termCount++;
                }
                /* IDF value */
                double ratio = (double) (docs.size()) / (double) termCount;
                double idf = Math.log(ratio);

                /* TF-IDF value */
                double tfidf = tf * idf;
                forwardIndex.put(keyWord, tfidf);
                res.put(key, forwardIndex);
            }
        }
        return res;
    }

    @Override
    public Map<?, ?> buildInvertedIndex(Map<String, Map<String, Double>> index) {
        Map<String, ArrayList<Entry<String, Double>>> res = new HashMap<>();

        for (String outerKey : index.keySet()) {
            for (String innerKey : index.get(outerKey).keySet()) {
                /* create an Entry object as (document, TFIDF value) */
                AbstractMap.SimpleEntry<String, Double> tuple = new SimpleEntry<>(outerKey,
                        index.get(outerKey).get(innerKey));

                ArrayList<Entry<String, Double>> arr;
                if (!res.containsKey(innerKey)) {
                    /*
                     * if the term does not already exist in hash map, create a new list and map as
                     * term(key) : arr(value)
                     */
                    arr = new ArrayList<>();
                } else {
                    /* otherwise if term already exists in hash map, append to list */
                    arr = res.get(innerKey);
                }
                arr.add(tuple);
                res.put(innerKey, arr);
            }
        }
        /* sort in reverse tag term TFIDF value */
        for (String key : res.keySet()) {
            Collections.sort(res.get(key), IndexBuilder.compInvertedIndex());
        }
        return res;
    }

    /**
     * Create and return a comparator to use for buildInvertedIndex Collection will
     * be sorted by reverse tag term TFIDF value
     * 
     * @return the comparator
     */
    public static Comparator<Entry<String, Double>> compInvertedIndex() {
        return new Comparator<Entry<String, Double>>() {
            public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Entry<String, List<String>>> buildHomePage(Map<?, ?> invertedIndex) {
        List<Entry<String, List<String>>> res = new ArrayList<>();

        /* for loop through every term */
        for (Object term : invertedIndex.keySet()) {
            List<String> arr = new ArrayList<>();

            /* do not include stop words */
            if (IIndexBuilder.STOPWORDS.contains((String) term))
                continue;

            /* for loop through every entry */
            for (Object entries : (ArrayList<Entry<String, List<String>>>) (invertedIndex.get(term))) {
                arr.add(((Entry<String, List<String>>) entries).getKey());
            }
            AbstractMap.SimpleEntry<String, List<String>> tuple = new SimpleEntry<>((String) term, arr);
            res.add(tuple);
        }
        Collections.sort(res,IndexBuilder.compHomePage());
        return res;
    }

    /**
     * Create and return a comparator to use for buildHomePage Tag term are sorted
     * by no. of articles, and if equal, by reverse lexicographic order
     * 
     * @return the comparator
     */
    public static Comparator<Entry<String, List<String>>> compHomePage() {
        return new Comparator<Entry<String, List<String>>>() {
            public int compare(Entry<String, List<String>> o1, Entry<String, List<String>> o2) {
                /* has same number of articles, then sort by reverse lexicographic order */
                if (o1.getValue().size() == o2.getValue().size()) {
                    return o2.getKey().compareTo(o1.getKey());
                }
                /* else sort by no. of articles */
                return o2.getValue().size() - o1.getValue().size();
            }
        };
    }

    @Override
    public Collection<?> createAutocompleteFile(Collection<Entry<String, List<String>>> homepage) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("autocomplete.txt"));
            ArrayList<String> res = new ArrayList<>();
            
            /* write to file the number of words */
            writer.write(homepage.size() + "\n");
            
            for(Entry<String,List<String>> entry : homepage) {
                String word = entry.getKey();
                res.add(word);
            }   
            
            /* write words to file in lexicographical order */
            Collections.sort(res,IndexBuilder.compAutocompleteFile());
            for(String word : res) {
                writer.write(" 0 " + word + "\n");
            }
            writer.close();
            return res;     
        }catch(IOException e) {
            return null;
        }
    }
    
    /**
     * Lexicographical order
     * 
     * @return the comparator
     */
    public static Comparator<String> compAutocompleteFile() {
        return new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> searchArticles(String queryTerm, Map<?, ?> invertedIndex) {
        List<String> res = new ArrayList<>();
        for(Object entries : (ArrayList<Entry<String, List<String>>>) (invertedIndex.get(queryTerm))) {
            res.add(((Entry<String, List<String>>) entries).getKey());
        }
        return res;
    }

}
