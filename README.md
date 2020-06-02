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
  1) Download files and make sure jsoup is properly added to the classpath
  2) Inside the main function, locate "input RSS link" and "input word"
  3) The RSS link must be opened to start the program, one can open the link by "python -m http.server <port number>"
  4) For "input RSS link", put the appropriate RSS link 
  5) For "input word", put any word to see the most relevant links associated to the word
  6) Locate variable "listOfWords", and notice that an "autocomplete.txt" has been generated that displays all words parsed
  7) Locate variable "articles", which contains the links in most relevant order  

  
### Folders
* Trie

  * Keeps all the words that are found from the links in a trie data structure
 
* Engine

  * Takes an RSS feed with HTML links and parses words from each link
  * Gets rid of "insignificant" words (e.g. conjunctions) during parsing process
  * Uses an algorithm to map each word to relevant links   
  * Creates a file that shows all the terms that were parsed

