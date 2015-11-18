Assignment3
Kartik Mahaley

This zip folder contains "queries.txt","tccorpus.txt" input files

"rankcomparator.java" "indexer.java" and bm25.java" file which will take txt file as input

index.out and results.eval as program output.

"implementation.txt" file explain how code is implementaed for bm 25 index

#Keep all file in same folder

To run the indexer.java program please follow below steps(commands)
this program takes 2 parameters (tccorpus.txt index.out)
1) javac indexer.java
2) java indexer tccorpus.txt index.out

Output from this file is present in index.out file. This is object file hence unreadable

Now run bm25.java program please follow below steps(commands)
this program takes 4 paramemters (index.out queries.txt 100 results.eval)
1) javac bm25.java
2) java bm25 index.out queries.txt 100 results.eval


Output is saved in results.eval file

Output will have file in format mentined below with top 100 doc_id per query.
Sample output is given below

query_id Q0 doc_id rank BM25_score system_name
1	 Q0 234	   1	14.232434  karik


