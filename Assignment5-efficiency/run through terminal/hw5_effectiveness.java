

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class hw5_effectiveness {
	static private Map<Integer, LinkedList<String>> cacm_map = new TreeMap<Integer, LinkedList<String>>();
	static private Map<Integer, LinkedHashMap<String, Double>> bm25_outer_map = new TreeMap<Integer, LinkedHashMap<String, Double>>();
	
	static private Map<Integer,LinkedHashMap<String,LinkedList<Double>>> final_value_map=new LinkedHashMap<Integer, LinkedHashMap<String,LinkedList<Double>>>();
	static private Map<Integer, Double> p_at_k=new LinkedHashMap<Integer, Double>();
	static private double MAP=0.0;

	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		// The name of the file to read
//		String filename1 = "cacm.rel";
//		String filename2 = "results_hw5.eval";
		
		String filename1 = args[0];
		String filename2 = args[1];
		System.out.println(filename1+"   "+filename2);
		// reference to each line in file
		read_from_file(filename1);
		read_from_file(filename2);
		calculate_precision_recall();
		MAP=MAP/3;
		WriteToFileExample();

//		System.out.println(cacm_map);
//		System.out.println(bm25_outer_map);
		System.out.println(p_at_k);
		System.out.println(MAP);
		
//		for (Entry<Integer, LinkedList<String>> entry : cacm_map.entrySet()) {
//		System.out.println(entry.getKey() + " => " + entry.getValue());
//		}
//		for (Entry<Integer, LinkedHashMap<String, Double>> entry : bm25_outer_map.entrySet()) {
//			System.out.println(entry.getKey() + " => "	+ entry.getValue().size());
//		}
//		System.out.println(final_value_map);
		
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.println("\nTime taken in seconds = " + (elapsedTime / 1000));
		
	}

	private static void WriteToFileExample() 
	{
		try{
			File file = new File("output_hw5.txt");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			for (Entry<Integer, LinkedHashMap<String, LinkedList<Double>>> entry : final_value_map.entrySet()) {
				//System.out.println("====>>>> " + entry.getKey());
				bw.write("Query ID==========> "+entry.getKey()+"\n");
				bw.write("Rank \tDocument_ID \tDocument_score \tRelevance_level \tPrecision \tRecall \tNDCG\n");
				for (Entry<String, LinkedList<Double>> e : entry.getValue().entrySet()) {
					//System.out.println(e.getKey() + "==== " + e.getValue());
					
					bw.write(e.getValue().get(0)+" \t"+e.getKey()+" \t"+e.getValue().get(1)+" \t"+e.getValue().get(2)+" \t"
					+e.getValue().get(3)+" \t"+e.getValue().get(4)+" \t"+e.getValue().get(5)+"\n");
				}
			}
			
			
			bw.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private static void calculate_precision_recall() {
		
		for (Entry<Integer, LinkedHashMap<String, Double>> entry : bm25_outer_map.entrySet()) {
		//System.out.println(entry.getKey() + " => "	+ entry.getValue().size());
			Integer myqueryid=entry.getKey();
			
			if(cacm_map.containsKey(myqueryid)){
				//System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^"+myqueryid);
				LinkedHashMap<String,LinkedList<Double>> docid_with_values= new LinkedHashMap<String,LinkedList<Double>>();
				LinkedHashMap<String, Double> bm25_indexer=entry.getValue();
				LinkedList<String> list=cacm_map.get(myqueryid);
				//System.out.println(myqueryid+"   "+list); 
				Integer total_relevant_doc=list.size();
				Set<String> bm25_doc_list=bm25_indexer.keySet();
				
				//System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^"+total_relevant_doc);
				int retreived_docs=0;
				int relevant_docs=0;
				int relevance=0;
				int rank=0;
				double avg_precision=0.0;
				double prev_dcg=0.0;
				double prev_idcg=0.0;
				double ndcg=0.0;
				
				for(String doc_id : bm25_doc_list){
					//System.out.println(doc_id);
					rank++;
					LinkedList<Double> values=new LinkedList<Double>();
					
					if(list.contains(doc_id)){
						relevance=1;
						relevant_docs++;
						
					}else{
						relevance=0;
						
					}
					retreived_docs++;
					
					double precision=(double)relevant_docs/retreived_docs;
					double recall=(double)relevant_docs/total_relevant_doc;
					//System.out.println(doc_id +"  "+precision+"   "+recall);
					//System.out.println(precision+ " : "+recall);
					double dcg=0.0;
					double idcg=0.0;
					if(rank==1.0){
						dcg=relevance;
					}else{
						dcg=relevance/(Math.log(rank)/Math.log(2));
					}
					if(rank==1.0){
						idcg=1.0;
					}else if(rank<=total_relevant_doc){
						idcg=1.0/(Math.log(rank)/Math.log(2));
					}else{
						idcg=0.0;
					}
					dcg+=prev_dcg;
					idcg+=prev_idcg;
					ndcg=dcg/idcg;
					
					values.add((double)rank);
					values.add(bm25_indexer.get(doc_id));
					values.add((double)relevance);
					values.add(precision);
					values.add(recall);
					values.add(ndcg);
					
					if(relevance==1){
						avg_precision=avg_precision+precision;
						//System.out.println(avg_precision);
					}
					docid_with_values.put(doc_id, values);
					//System.out.println(docid_with_values);
					prev_dcg=dcg;
					prev_idcg=idcg;
					
					if(retreived_docs==20){
						p_at_k.put(myqueryid, precision);
						//System.out.println(p_at_k);
					}
				}
				avg_precision=avg_precision/relevant_docs;
				MAP+=avg_precision;
				//System.out.println(myqueryid+" : "+MAP);
				final_value_map.put(myqueryid, docid_with_values);
			}
			
		}
		
	}

	private static void read_from_file(String filename) {
		String line = null;
		try {
			// FileReader reads text files in the default encoding.
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);

			while ((line = br.readLine()) != null) {
				// System.out.println(line);
				String[] line_array = line.split(" ");

				if (filename.equals("cacm.rel")) {
					populate_cacm_map(Integer.parseInt(line_array[0]),
							line_array[2]);
				} else if (filename.equals("results_hw5.eval")) {

					populate_queryid_bm25(Integer.parseInt(line_array[0]),
							line_array[2], Double.parseDouble(line_array[4]));
				}
			}

			// close files.
			br.close();

		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + filename + "'");
		} catch (IOException ex) {
			System.out.println("Error reading file '" + filename + "'");

		}
	}

	private static void populate_cacm_map(Integer key, String doc_id) {
		LinkedList<String> list;

		if (!cacm_map.containsKey(key)) {
			list = new LinkedList<String>();
			list.add(doc_id);
			cacm_map.put(key, list);
			// System.out.println("new key added : "+key);
		} else {
			list = cacm_map.get(key);
			list.add(doc_id);
			cacm_map.put(key, list);
		}

	}

	private static void populate_queryid_bm25(int key, String queryid,
			Double bm25) {
		LinkedHashMap<String, Double> bm25_indexer;

		if (key == 1)
			key = 12;
		if (key == 2)
			key = 13;
		if (key == 3)
			key = 19;
		queryid = "CACM-" + queryid;

		if (!bm25_outer_map.containsKey(key)) {
			bm25_indexer = new LinkedHashMap<String, Double>();
			bm25_indexer.put(queryid, bm25);
			bm25_outer_map.put(key, bm25_indexer);

		} else {
			bm25_indexer = bm25_outer_map.get(key);
			bm25_indexer.put(queryid, bm25);
			bm25_outer_map.put(key, bm25_indexer);
		}
	}

}
