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


11-5-2025 1:
-added other crud operations: delete, update

11-5-2025 2:
-Modificam levenshtein astfel incat sa se uite la sufix. 
-La comparatie intre nu baga in seama un sufix care pleaca de un cuvant
de 2 sau 3 caractere. 
-Prag mutat la 1, prea vag la 2.

11-5-2025 3:
-levenshtein cu sufixe o idee proasta, ne intoarcem doar la levenshtein. Mai bine sa ramana unele cuvinte decat sa dispara.
-Totusi pastram ca la comparatia levenshtein sa nu bage in seama cuvinte cu 2 caractere
-Crestem la prag 2 Levenshtein
-Am uitat sa scriem text cu diacritice ofofofofof. Aici e problema la lemma de nu gaseste un lema la mergand,probabil. Dupa continuam cu levenshtein
-Deocamdata ramanem cu versiunea asta. La textul:
"Ana merge pe drum și îl vede pe Mihai mergând încet. El spusese că a mers mult ieri și că va merge din nou mâine. Maria pleca spre casă, iar fratele ei plecase deja. Copiii fug spre parc, fugind de ploaie. Toți au ajuns acolo obosiți, dar fericiți."
Avem cuvintele dupa UDPipe:
size: 23
ajunge, ană, casa, copil, deja, drum, fericit, frat, fug, fugind, ier, maria, merge, mergând, mers, mihai, mâine, nou, parc, pleca, ploaie, spuses, vedea
Avem cuvintele dupa Levenshtein distance:
size: 22
ajunge, ană, casa, copil, deja, drum, fericit, frat, fug, fugind, ier, maria, merge, mergând, mihai, mâine, nou, parc, pleca, ploaie, spuses, vedea

-corectari de noi ca ieri sa ramane ieri si nu ier, fiindca romanian model are lipsuri uneori
-fugind UdPipe nu reuseste sa ia lemma fug/fugi si mergand la merg/mergi. Sa incercam sa corectam din nou in unele cazuri cu snowball stemmer romanian? Poate acesta reuseste sa treaca prin el si sa gaseasca un stem?


11-5-2025 4:
-Adaugam ca serverul sa observe cand un client se deconecteaza

TODO:
->fugind UdPipe nu reuseste sa ia lemma fug/fugi si mergand la merg/mergi. Sa incercam sa corectam din nou in unele cazuri cu snowball stemmer romanian? Poate acesta reuseste sa treaca prin el si sa gaseasca un stem?
