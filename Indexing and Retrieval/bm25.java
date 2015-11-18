package index_retrieval;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class bm25 {
	static String queryfilename = "queries.txt";
	static String outputfilename = "index.out";
	static String bm25filename = "results.eval";
	static int MAX = 100;
	static Map<String, Integer> readindexerinteger = new HashMap<String, Integer>();
	static Map<String, HashMap<String, Integer>> readinvertedindex = new HashMap<String, HashMap<String, Integer>>();
	static Map<String, Double> bm25index = new HashMap<String, Double>();

	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();

		read_from_input_file();
		read_myquery_file();

		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.println("\nTime taken is seconds = " + (elapsedTime / 1000));
	}

	private static void read_myquery_file() throws IOException {
		Writer writer = null;
		try {

			// System.out.println(readinvertedindex);
			String line = null;
			List<String> linelist = new ArrayList<String>();
			FileReader fileReader = new FileReader(queryfilename);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while ((line = bufferedReader.readLine()) != null) {
				linelist.add(line);

			}
			// String s="portabl oper system";
			int count = 1;
			// file
			File file = new File(bm25filename);
			if (!file.exists()) {
				file.createNewFile();
			} else {
				file.delete();
				file.createNewFile();
			}
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file)));
			writer.write("query_id\tQ0\tdoc_id\trank\tBM25_score\tsystem_name\n");
			
			for (String s : linelist) {
				bm25index.clear();
				for (String token : s.split(" ")) {
					double qf = calculateQF(s, token);
					calculateBM25(token, qf);
					
				}
				TreeMap<String, Double> sortedmap = sortbyvalue(bm25index);
				LinkedHashMap<String, Double> first100BM25 = firstmaxlist(MAX,
						sortedmap);
				printmap(s, count, first100BM25);
				write_to_a_file(writer, s, count, first100BM25);
				count++;

			}
			// System.out.println(linelist);
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + queryfilename + "'");
		} catch (IOException ex) {
			System.out.println("Error in reading file '" + queryfilename + "'");
		} finally {
			writer.close();
		}
	}

	private static void calculateBM25(String token, double qf) {

		double k1 = 1.2, b = 0.75, k2 = 100;
		double ri = 0.0, R = 0.0;
		double N = readindexerinteger.size();
		double totaltoken = 0.0, avdl = 0.0;
		double finalterm = 0.0;
		
		for (Map.Entry<String, Integer> entry : readindexerinteger.entrySet()) {
			totaltoken = totaltoken + entry.getValue();
		}
		avdl = (totaltoken / N);
		//System.out.println(token);
		
		if (readinvertedindex.containsKey(token)) {
			Map<String, Integer> invertedindexinnermap = readinvertedindex.get(token);
				
			double ni = invertedindexinnermap.size();

			for (Map.Entry<String, Integer> entry : invertedindexinnermap.entrySet()) {

				double dl = readindexerinteger.get(entry.getKey());
				
				double K =  (k1 * ((1 - b) + b * (dl / avdl)));
				double fi = entry.getValue();
				//System.out.println(dl+" "+ ni+" "+K+" "+ fi+" "+ qf);

				double term3 =  (((k2 + 1) * qf) / (k2 + qf));
				double term2 =  (((k1 + 1) * fi) / (K + fi));
				double term1 =  Math.log(((ri + 0.5) / (R - ri + 0.5))
						/ ((ni - ri + 0.5) / (N - ni - R + ri + 0.5)));
				finalterm =  ((term1) * (term2) * (term3));
				
				
				if (!bm25index.containsKey(entry.getKey())) {
					bm25index.put(entry.getKey(), finalterm);
				} else {
			//		System.out.println("OLD= "+entry.getKey());
					double d =bm25index.get(entry.getKey());
					d = d + finalterm;
					bm25index.put(entry.getKey(),d);
				}
				
				
				
				
			}
		}

	}

	public static TreeMap<String, Double> sortbyvalue(Map<String, Double> map) {
		rankcomparator rc = new rankcomparator(map);
		TreeMap<String, Double> sortedMap = new TreeMap<String, Double>(rc);
		sortedMap.putAll(map);
		return sortedMap;
	}

	private static LinkedHashMap<String, Double> firstmaxlist(int MAX,
			Map<String, Double> pR2) {
		LinkedHashMap<String, Double> treemap = new LinkedHashMap<String, Double>();
		int count = 0;

		for (Map.Entry<String, Double> entry : pR2.entrySet()) {
			if (count >= MAX)
				break;

			treemap.put(entry.getKey(), entry.getValue());
			count++;
		}

		return treemap;
	}

	private static void printmap(String s, int c,
			LinkedHashMap<String, Double> mapset) {
		int rank = 1;
		for (Map.Entry<String, Double> entry : mapset.entrySet()) {
			System.out.println(c + "\tQ0\t" + entry.getKey() + "\t" + rank
					+ "\t" + entry.getValue());
			rank++;
		}

	}

	private static double calculateQF(String stringlist, String word) {

		String[] splitStr = stringlist.split(" ");

		Map<String, Double> wordcountmap = new HashMap<>();
		for (String s : splitStr) {
			if (wordcountmap.containsKey(s)) {
				// Map already contains the word key. Just increment it's count
				// by 1
				wordcountmap.put(s, wordcountmap.get(s) + 1);
			} else {
				// Map doesn't have mapping for word. Add one with count = 1
				wordcountmap.put(s, 1.0);
			}
		}
		return wordcountmap.get(word);
	}

	private static void read_from_input_file() {
		try {

			FileInputStream fis = new FileInputStream(outputfilename);
			ObjectInputStream ois = new ObjectInputStream(fis);
			List readobject = (List) ois.readObject();

			readindexerinteger = (Map<String, Integer>) readobject.get(1);
			readinvertedindex = (Map<String, HashMap<String, Integer>>) readobject
					.get(0);

			// System.out.println(readindexerinteger);
			// System.out.println(readinvertedindex);

			ois.close();
			// 'calculateBM25();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	private static void write_to_a_file(Writer writer, String s, int c,
			LinkedHashMap<String, Double> mapset) {
		try {
			int rank = 1;
			for (Map.Entry<String, Double> entry : mapset.entrySet()) {
				writer.write(c + "\tQ0\t" + entry.getKey() + "\t" + rank + "\t"
						+ entry.getValue() + "\tkartik\n");
				rank++;
			}

		} catch (IOException ex) {
			System.out.println("Not able to Write into file");
		}

	}

}
