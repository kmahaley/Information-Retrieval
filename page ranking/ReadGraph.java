import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class ReadGraph {
	final static double d=0.85;
	final static int MAX=50;
	
	static Map<String, Set<String>[]> graph = new HashMap<String, Set<String>[]>();
	static List<String> newinlinks;
	static Set<String> pages = new TreeSet<String>();
	static Set<String> sourcepages = new TreeSet<String>();
	static Set<String> sinkpages= new HashSet<String>();
	static Map<String, Set<String>> inlinksMap = new HashMap<String, Set<String>>();
	static Map<String, Double> inlinkscountMap = new HashMap<String, Double>();
	static Map<String, Integer> outlinksMap = new HashMap<String, Integer>();
	static Map<String,Double> PR=new TreeMap<String, Double>();
	static int pagerankcounter=0;
	
	public static void main(String[] args)  throws IOException {
		 
	    String filename = args[0];
	    System.out.println("You entered filename "+filename);
	      
		long startTime = System.currentTimeMillis();
		//String filename = "trial.txt";
		
		String line = null;
		try {
			FileReader fileReader = new FileReader(filename);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while ((line = bufferedReader.readLine()) != null) {
				String[] pagearray = line.split(" ");
				String key = pagearray[0];
//				pages.add(key);
				if(graph.get(key) == null){

					Set<String> outlinks = new TreeSet<String>();
					Set<String> inlinks = new TreeSet<String>();
					
					for (int i = 1; i < pagearray.length; i++) {
						inlinks.add(pagearray[i]);
						TreeSet[] test1 = new TreeSet[2];
						test1[0] = (TreeSet) inlinks;
						test1[1] = (TreeSet) outlinks;
						graph.put(key, test1);
						
						if(graph.get(pagearray[i]) == null){
							Set<String> outlinksNew = new TreeSet<String>();
							Set<String> inlinksNew = new TreeSet<String>();
							outlinksNew.add(key);
							TreeSet[] test = new TreeSet[2];
							test[0] = (TreeSet) inlinksNew;
							test[1] = (TreeSet) outlinksNew;
							graph.put(pagearray[i], test) ;
						}else{
							Set<String> existingOutlinks = graph.get(pagearray[i])[1];
							Set<String> existingInlinks = graph.get(pagearray[i])[0];
							existingOutlinks.add(key);
							TreeSet[] test = new TreeSet[2];
							test[0] = (TreeSet) existingInlinks;
							test[1] = (TreeSet) existingOutlinks;
							graph.put(pagearray[i], test) ;
						}
						
					}
				}else{
					Set<String> outlinks = graph.get(key)[1];
					Set<String> inlinks = graph.get(key)[0];
					for (int i = 1; i < pagearray.length; i++) {
						inlinks.add(pagearray[i]);
						TreeSet[] test1 = new TreeSet[2];
						test1[0] = (TreeSet) inlinks;
						test1[1] = (TreeSet) outlinks;
						graph.put(key, test1);
						if(graph.get(pagearray[i]) == null){
							Set<String> outlinksNew = new TreeSet<String>();
							Set<String> inlinksNew = new TreeSet<String>();
							outlinksNew.add(key);
							TreeSet[] test = new TreeSet[2];
							test[0] = (TreeSet) inlinksNew;
							test[1] = (TreeSet) outlinksNew;
							graph.put(pagearray[i], test) ;
						}else{
							Set<String> existingOutlinks = graph.get(pagearray[i])[1];
							Set<String> existingInlinks = graph.get(pagearray[i])[0];
							existingOutlinks.add(key);
							TreeSet[] test = new TreeSet[2];
							test[0] = (TreeSet) existingInlinks;
							test[1] = (TreeSet) existingOutlinks;
							graph.put(pagearray[i], test) ;
						}
						
					}
				}
				
			}
			// Close file
			bufferedReader.close();
			
			//making individual maps and set for page ranking algorithm
			for(Map.Entry<String, Set<String>[]> entry:graph.entrySet()){
				pages.add(entry.getKey());
				
				//if(!(entry.getValue()[1].isEmpty())){
					outlinksMap.put(entry.getKey(), entry.getValue()[1].size());
				//}
				
				if(entry.getValue()[1].isEmpty()){
					sinkpages.add(entry.getKey());
				}
				if(entry.getValue()[0].isEmpty()){
					sourcepages.add(entry.getKey());
				}
				
				//if(!(entry.getValue()[0].isEmpty())){
					inlinksMap.put(entry.getKey(), entry.getValue()[0]);
					inlinkscountMap.put(entry.getKey(), (double)entry.getValue()[0].size());
				//}
				
			}
			//System.out.println(pages.size());
			//System.out.println(sourcepages.size());
			//System.out.println(sinkpages.size());
			//System.out.println(inlinkscountMap.size());
			//System.out.println(outlinksMap.size());
			
			//page rank algorithm
			//page rank algorithm
			pagerankfunction(graph.keySet(), sinkpages, inlinksMap, outlinksMap);
			
		    
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + filename + "'");
		} catch (IOException ex) {
			System.out.println("Error in reading file '" + filename + "'");
		}
		
		long stopTime = System.currentTimeMillis();
	    long elapsedTime = stopTime - startTime;
	    
	    System.out.println("\nTime taken is seconds = " +(elapsedTime/1000));
		
	}
	
	//page rank algorithm
	private static void pagerankfunction(Set<String> setofpages ,Set<String> sinkpages, 
			Map<String,Set<String>> inlinkpages,Map<String, Integer> outlinkpages) {
		
		double N=(double)setofpages.size();
		double initialpagerank=(double)1/N;
		int I=4;
		int j=0,i=0 ;
		double sinkPR, perplexitynew;
		
		Map<String,Double> newPR=new TreeMap<String, Double>();
		for(String p : setofpages){
			PR.put(p, initialpagerank);
		}
		
		
		double perplexityold = perplexity(PR);
		//System.out.println("perplexity  = "+perplexityold);
		
		while(i<I){
			System.out.println("----------- i "+i);
			sinkPR =0;
			for(String p : sinkpages){
				sinkPR +=(double)PR.get(p);
				
			}
			//System.out.println(sinkPR);
			
			for(String p : setofpages){
				newPR.put(p, (1-d)/N);
				newPR.put(p, newPR.get(p)+d*sinkPR/N) ;
				//System.out.println("P");
				if(null != inlinkpages.get(p)){
					for(String q : inlinkpages.get(p)){
						if(null != outlinkpages.get(q) && outlinkpages.get(q) != 0){
							newPR.put(p,newPR.get(p)+(d*PR.get(q)/outlinkpages.get(q)));
							
						}
					}
				}
				
			}
			for(String p : setofpages)
			{
				PR.put(p, newPR.get(p));
			}	
			j++;
			
			perplexitynew= perplexity(PR);
			System.out.println(j+"  perplexitynew ="+ perplexitynew);
			
			if(Math.abs(perplexityold-perplexitynew)<1){
				i++;
				
			}
			perplexityold=perplexitynew;
		}
		
		//System.out.println(PR.size());
		//System.out.println(PRlessthaninitial.size());
		
		TreeMap<String, Double> sortedmap = sortbyvalue(PR);
		//System.out.println(sortedmap);
		
		LinkedHashMap<String, Double> firstfiftypageranking=firstmaxlist(MAX,sortedmap);
		printmap("pagerank",firstfiftypageranking);
		
		TreeMap<String, Double> sortedinlinkmap=sortbyvalue(inlinkscountMap);
		//System.out.println(sortedinlinkmap);
		
		LinkedHashMap<String, Double> firstfiftyinlinkpages=firstmaxlist(MAX,sortedinlinkmap);
		printmap("inlinks",firstfiftyinlinkpages);
		
		//System.out.println(pages.size());
		System.out.println("Source Pages = " +sourcepages.size());
		System.out.println("Proportion = "+(double)(sourcepages.size()/N));
		
		System.out.println("Sink Pages = "+ sinkpages.size());
		System.out.println("Proportion = "+(double)(sinkpages.size()/N));
		
		for (Map.Entry<String, Double> entry : PR.entrySet()) {
			if (entry.getValue() < (double) (1 / N)) {
				pagerankcounter++;
			}
		}
		System.out.println("No of pages with Page rank less than intial= "+pagerankcounter);
		System.out.println("Proportion = "+(double)(pagerankcounter/N));
		
	}
	
	
	

	private static void printmap(String str,
			LinkedHashMap<String, Double> mapset) {
		String toprint="";
		
		if(str=="pagerank")
			toprint="Page rank  ";
		else
			toprint="Inlink count  ";
		
		for(Map.Entry<String, Double> entry : mapset.entrySet()){
			System.out.println(toprint +" "+entry.getKey()+"\t"+entry.getValue());
			
		}
		
	}

	//entropy and perplexity function
		private static double perplexity(Map<String, Double> pR) {
			double e=0;
			for(Map.Entry<String, Double> entry:pR.entrySet()){
				e = e + (double) (entry.getValue()* (double)(Math.log(entry.getValue())/Math.log(2)));
				
			}
			
			return (Math.pow(2, -e));
		}

	
	//Sort map in decreasing order of values
	public static TreeMap<String, Double> sortbyvalue(Map<String, Double> map) {
		rankcomparator rc =  new rankcomparator(map);
		TreeMap<String,Double> sortedMap = new TreeMap<String,Double>(rc);
		sortedMap.putAll(map);
		return sortedMap;
	}
	
	//top 50 elements in map
	private static LinkedHashMap<String, Double> firstmaxlist(int MAX, Map<String, Double> pR2) {
		LinkedHashMap<String, Double> treemap=new LinkedHashMap<String, Double>();
		int count = 0;
		 
		  for (Map.Entry<String,Double> entry:pR2.entrySet()) {
		     if (count >= MAX) 
		    	 break;

		     treemap.put(entry.getKey(),entry.getValue());
		     count++;
		  }
		  
		return treemap;
	}

		
	
}
