# Search Engine
-----
### Description
This search engine is a news aggregator that takes multiple HTML links, parses words, and allows the user to input a word in where the program will attempt to return the most relevant HTML links associated with the word. 

For instance, when the user inputs "New York", links with the keyword "New York" will be shown in most relevant order.

As a side, the engine will generate a file with all the terms that have been parsed.

The search engine will allow the user to input the following :

  1) RSS feed with HTML links
  2) Word to search for 
  

  
### Compile and Run
  1) Download folder

  
### Folders
* Trie

  * Keeps all the words that are found in documents in a trie data structure
 
* Engine

  * Takes an RSS feed with HTML links and parses words from each link
  * Gets rid of "insignificant" words (e.g. conjunctions) during parsing process
  * Uses an algorithm to map each word to links that the word is most found  
  * Creates a file that shows all the terms that were parsed

