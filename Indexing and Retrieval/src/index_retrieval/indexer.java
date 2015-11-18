package index_retrieval;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class indexer {

	//static String filename = "tccorpus.txt";
	//static String outputfilename = "index.out";

	static Map<String, List<String>> indexer = new HashMap<String, List<String>>();
	static Map<String, Integer> indexerinteger = new HashMap<String, Integer>();
	static Map<String, HashMap<String, Integer>> invertedindex = new HashMap<String, HashMap<String, Integer>>();
	
	static List<String> pageline;
	static HashMap<String, Integer> innermap;

	public static void main(String[] args) throws ClassNotFoundException {
		long startTime = System.currentTimeMillis();
		
		String filename = args[0];
		String outputfilename = args[1];
		
		indexfunction(filename);
		invertedindexerfunction();
		
		//printfunction(indexer);
		// printfunctioninteger(indexerinteger);

		write_object_to_file(outputfilename);
		//write_to_a_file(outputfilename);

		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.println("\nTime taken in seconds = " + (elapsedTime / 1000));
	}



	public static void indexfunction(String filename) {

		String key = "";
		String line = null;
		try {
			FileReader fileReader = new FileReader(filename);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while ((line = bufferedReader.readLine()) != null) {

				if (line.contains("#")) {

					String[] pagekey = line.split(" ");
					key = pagekey[1];
					// System.out.println(key);
					pageline = new ArrayList<String>();

				} else {
					String[] pagewords = line.split(" ");

					for (int i = 0; i < pagewords.length; i++) {
						try {
							Integer.parseInt(pagewords[i]);
							continue;
						} catch (NumberFormatException e) {
							pageline.add(pagewords[i]);
						}

					}
				}
				indexer.put(key, pageline);
				indexerinteger.put(key, pageline.size());

			}

		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + filename + "'");
		} catch (IOException ex) {
			System.out.println("Error in reading file '" + filename + "'");
		}

	}

	private static void invertedindexerfunction() {

		for (Map.Entry<String, List<String>> entry : indexer.entrySet()) {
			// System.out.println(entry.getKey() + " " + entry.getValue());
			for (int i = 0; i < entry.getValue().size(); i++) {

				if (!invertedindex.containsKey(entry.getValue().get(i))) {

					innermap = new HashMap<String, Integer>();
					// int mytokencount =
					// getwordcount(entry.getValue().get(i),entry.getValue());
					int mytokencount = 1;
					// System.out.println(entry.getValue().get(i)+" "+mytookencount);
					innermap.put(entry.getKey(), mytokencount);
					invertedindex.put(entry.getValue().get(i), innermap);

				} else {
					innermap = invertedindex.get(entry.getValue().get(i));
					if (!innermap.containsKey(entry.getKey())) {
						int mytokencount = 1;
						innermap.put(entry.getKey(), mytokencount);
					} else {
						for (Map.Entry<String, Integer> innerentry : innermap
								.entrySet()) {
							if (entry.getKey().equals(innerentry.getKey())) {
								int myexistingtokencount = innerentry
										.getValue();
								myexistingtokencount++;
								innermap.put(innerentry.getKey(),
										myexistingtokencount);
							}
						}
					}

				}

			}

		}
		// System.out.println(invertedindex);
	}

	private static void write_to_a_file(String outputfilename) {
		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outputfilename)));

			writer.write(" ------- \n");

			for (Map.Entry<String, Integer> entry : indexerinteger.entrySet()) {
				writer.write(entry.getKey() + " = " + entry.getValue() + " \n");
			}

			writer.write(" ------- \n \n");

			for (Map.Entry<String, HashMap<String, Integer>> ientry : invertedindex
					.entrySet()) {
				writer.write("\n \"" + ientry.getKey() + "\" { \n");

				HashMap<String, Integer> insidemap = ientry.getValue();
				// + ientry.getValue().toString()+" \n");
				for (Map.Entry<String, Integer> entry : insidemap.entrySet()) {
					writer.write("\t \t" + entry.getKey() + " = "
							+ entry.getValue() + " \n");
				}
				writer.write("}");
			}

			writer.close();
		} catch (IOException ex) {
			System.out.println("Not able to Write into file");
		}

	}

	private static void write_object_to_file(String outputfilename) {
		try {
			List myobject=new ArrayList();
			myobject.add(invertedindex);
			myobject.add(indexerinteger);
			FileOutputStream fos = new FileOutputStream(outputfilename);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(myobject);
			oos.close();
			fos.close();
			// System.out.printf("Serialized HashMap data is saved in hashmap.ser");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}
	
	 private static void printfunction(Map<String, List<String>> map) { for
	 (Map.Entry<String, List<String>> entry : map.entrySet()) {
	 System.out.println(entry.getKey() + " " + entry.getValue()); }
	 
	 }
	  
	 private static void printfunctioninteger(Map<String, Integer> map) { for
	 (Map.Entry<String, Integer> entry : map.entrySet()) {
	 System.out.println(entry.getKey() + " " + entry.getValue()); }
	 
	 }
	 

}
