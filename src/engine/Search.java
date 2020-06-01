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
    
}
