KARTIK MAHALEY
Information retrieval-CS6200
NUID-001716263
homework-4

The program requires three external libraries which are present in the folder
(lucene-core-4.7.2.jar,lucene-queryparser-4.7.2.jar,lucene-analyzers-common-4.7.2.jar)

To run the HW4 program please follow below steps(commands)
1) Compile command:

javac -cp lucene-core-4.7.2.jar:lucene-queryparser-4.7.2.jar:lucene-analyzers-common-4.7.2.jar HW4.java

2) execute 
java -cp .:lucene-core-4.7.2.jar:lucene-queryparser-4.7.2.jar:lucene-analyzers-common-4.7.2.jar HW4


This assignments includes following files
1) Readme and source code
2) A sorted (by frequency) list of (term, term_freq pairs)
term_result.txt is informat (term, rank, term_freq pairs) which I have used to draw Zipfian curve
3) A plot of the resulting Zipfian curve
HW4 _Zipfian's curve.pdf
4) Four lists (one per query) each containing at MOST 100 docIDs ranked by score
HW4-Queries_output.txt
5) A table comparing the total number of documents retrieved per query using Lucene’s scoring function vs. using your search engine (index with BM25) from the previous assignment 
Comparing BM25 Index.pdf
6) program is set to give top 100 docIDs per query