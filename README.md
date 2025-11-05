Folosim model: romanian-ud-1.2-160523.udpipe
Udpipe 1: udpipe.jar prin module settings si load la udpipe-java.dll
Levenshtein pentru corectari.

Stemmer de la snowball pentru ro - nu func bine.

first:
-created an server-client library app with the files server.java, client.java and firclient.java
-client sends an text then from that only distinct words get selected but at this point its only based on characters (later its gonna be on the meaning of words/verb forms/singular or plural thru NLP algorithms)
-added features of search an word in the list, add an word in the list and an STOP process for the client to exit
-the list of words gonna be sorted

new things:
-tried to add the UDPipe 1 library model to differentiate between the words based on verb forms/singular plural so that an word like mergi, mergeam,mergând, mers to be taken as one
-UDPipe 1 didnt come with an romanian model so i had to download one: romanian-ud-1.2-160523.udpipe
-The result is average. Words like mergeam and mergând goes to mergi but the model cant differenciate between merg and mers
-The model also somethimes gets a very few lemmas wrong like frate -> frat.
-The model also takes some words(Part-of-Speech tags) that shouldnt be taken like pronun, particle, adposition, conjuction.

new things:
-added exceptions for Part-of-Speech tags so they wont be considered
-personal corrections(frat we gonna take it as frate)
-added snowball romanian stemmer to try and make words like merg and mers be one but its not reliable, since we want to do it on verbs but sometimes an word thats not an pos(verb) can go thru here since the udpipe model categorize it wrong. So we need to find another solution

9-5-2025:
-added Levenshtein Distance after udpipe lemma(NLP) so words like mergi,merg,mers can be taken as one.

10-5-2025:
-the final list of words(library) is now available in server too
-the clients can see the list of words(library) before sending a text
-the server gets information about clients when connected


11-5-2025:
-added other crud operations: delete, update

11-5-2025:
-Modificam levenshtein astfel incat sa se uite la sufix. 
-La comparatie intre nu baga in seama un sufix care pleaca de un cuvant
de 2 sau 3 caractere. 
-Prag mutat la 1, prea vag la 2.
