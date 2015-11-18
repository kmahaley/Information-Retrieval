import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class read_graph {
	final static double d=0.85;
	static Map<String, List<String>> graph = new HashMap<String, List<String>>();
	static Map<String, Integer> outlinksgraph = new HashMap<String, Integer>();
	static List<String> newinlinks;
	static Set<String> pages = new TreeSet<String>();
	static Set<String> sinkpages = new TreeSet<String>();
	

	public static void main(String[] args) {

		String filename = "E:\\MS @ NEU\\Semester3\\IR\\Assignment2-Page Rank\\trial.txt";
		String line = null;
		try {
			FileReader fileReader = new FileReader(filename);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while ((line = bufferedReader.readLine()) != null) {
				newinlinks = new ArrayList<String>();
				String[] pagearray = line.split(" ");
				String key = pagearray[0];
				pages.add(key);
				
				for (int i = 1; i < pagearray.length; i++) {
					newinlinks.add(pagearray[i]);
				}
				
				//addinlinks(key, newinlinks);
				graph.put(key, newinlinks);
			}
			// Close file
			bufferedReader.close();
			
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + filename + "'");
		} catch (IOException ex) {
			System.out.println("Error in reading file '" + filename + "'");
		}

		sinkpages = getsinkset(graph, pages);

		for(String s:pages){
			List<String> outList = new ArrayList<String>();
			outList=findOutLinks(graph, s);
			outlinksgraph.put(s,outList.size());
		}
		//System.out.println("Outlinks of A :" + findOutLinks(graph, "A"));
		System.out.println("inlinks Graph :" + graph);
		//System.out.println("P set :" + pages);
		//System.out.println("S set :" + sinkpages);
		System.out.println("outlinks graph :" + outlinksgraph);
		//System.out.println("sink set : " + sinkpages);

		pagerankfunction(pages,graph,sinkpages,outlinksgraph);
	}
	
	// returns sink set by calling findOutLinks function internally
	private static Set<String> getsinkset(Map<String, List<String>> graph2,
			Set<String> pages2) {
		List<String> outList = new ArrayList<String>();
		for (String s : pages2) {
			outList = findOutLinks(graph2, s);
			if (outList.isEmpty()) {
				sinkpages.add(s);
			}
		}
		return sinkpages;
	}

	
	// function adds outlink nodes to a node
	private static List<String> findOutLinks(Map<String, List<String>> graph,
			String node) {
		List<String> outList = new ArrayList<String>();
		for (Map.Entry<String, List<String>> entry : graph.entrySet()) {
			String currentNode = entry.getKey();

			if (!currentNode.equalsIgnoreCase(node)) {
				List<String> inlinks = entry.getValue();
				if (inlinks.contains(node)) {
					outList.add(currentNode);
				}
			}
		}
		return outList;
	}
	
	//page rank
	private static void pagerankfunction(Set<String> P,Map<String, 
			List<String>> M,Set<String> S,Map<String, Integer> L) {
		double N=(double)P.size();
		double initialpagerank=1/N;
		int I=3;
		int i=0, sinkPR;
		Map<String,Double> PR=new TreeMap<String, Double>();
		Map<String,Double> newPR=new TreeMap<String, Double>();
		for(String p : P){
			PR.put(p, initialpagerank);
		}
		//System.out.println(PR);
		while(i<I){
			sinkPR =0;
			for(String s:S){
				sinkPR +=PR.get(s);
				//System.out.println("sinkPR");
			}
			for(String s:P){
				newPR.put(s, (1-d)/N);
				newPR.put(s, newPR.get(s)+d*sinkPR/N) ;
				//System.out.println("P");
				for(String q:M.get(s)){
					newPR.put(s,newPR.get(s)+(d*PR.get(q)/L.get(q)));
					//System.out.println("M");
				}
			}
			for(String p:P)
			{
				PR.put(p, newPR.get(p));
			}	
			i++;
		}
		
		System.out.println(PR);
		entropy(PR);
	}

	
	private static void entropy(Map<String, Double> pR) {
		double e=0;
		for(Map.Entry<String, Double> entry:pR.entrySet()){
			e=e+ (entry.getValue()* (Math.log(entry.getValue())/Math.log(2)));
			
		}
		System.out.println(-e);
		System.out.println(Math.pow(2, -e));
	}



}
