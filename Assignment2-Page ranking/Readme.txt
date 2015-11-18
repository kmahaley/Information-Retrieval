Assignment2
Kartik Mahaley
Page rank algorithm

This zip folder contains "trial.txt","wt2g_inlinks.txt" input graph files
"rankcomparator.java" and "ReadGraph.java" file which will take txt file as input

#Keep all file in same folder

To run the ReadGraph.java program please follow below steps(commands)
1) javac ReadGraph.java
2) java ReadGraph wt2g_inlinks.txt

File will print 
- perplexity values until it converges
- top 50 pages with page ranks
- top 50 pages with inlink count
- proportion of sourcepages, sinkpages and count of pages whose page rank is less than initial page rank value

Output is saved in output.txt file

Analysis is in analysis.pdf file