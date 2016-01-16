package nbtrain;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class nbtrain {
	static private Map<String, Integer> vocabulary=new LinkedHashMap<String, Integer>();
	static private Map<String, Integer> pos_map=new LinkedHashMap<String, Integer>();
	static private Map<String, Integer> neg_map=new LinkedHashMap<String, Integer>();
	static private Map<String, Integer> pos_map_length=new LinkedHashMap<String, Integer>();
	static private Map<String, Integer> neg_map_length=new LinkedHashMap<String, Integer>();

	
	public static void main(String[] args) {
		
			Readfromfolder("E:\\MS @ NEU\\Semester3\\IR\\Assignment6-classification\\textcat\\train\\neg");
			Readfromfolder("E:\\MS @ NEU\\Semester3\\IR\\Assignment6-classification\\textcat\\train\\pos");
			
			refine_maps();
			write_object_to_file();
			//write_file();
			
//			System.out.println(vocabulary.size());
//			System.out.println(pos_map.size());
//			System.out.println(neg_map.size());
//			System.out.println(pos_map_length);
//			System.out.println(neg_map_length);
//			int x=0; 
//			int y=0;
//			int z=0;
//			for(Entry<String, Integer> entry : neg_map.entrySet() ){
//				//x=x+entry.getValue();	
//				System.out.print(entry.getKey()+" => "+entry.getValue()+" ");
//			}
//			System.out.println("");
//			for(Entry<String, Integer> entry : pos_map.entrySet() ){
//				//y=y+entry.getValue();	
//				System.out.print(entry.getKey()+" => "+entry.getValue()+" ");
//			}
//			System.out.println("");
//			for(Entry<String, Integer> entry : vocabulary.entrySet() ){
//				//z=z+entry.getValue();	
//				System.out.print(entry.getKey()+" => "+entry.getValue()+" ");
//			}
//			System.out.println(x);
//			System.out.println(y);
//			System.out.println(z);
		
	}



	private static void write_file() {
		try{
			PrintWriter writer = new PrintWriter("newfile.txt", "UTF-8");
			for(Entry<String, Integer> entry : vocabulary.entrySet() ){
				//z=z+entry.getValue();	
				writer.println(entry.getKey()+" => "+entry.getValue());
				
			}
			writer.println("***************** positive map");
			for(Entry<String, Integer> entry : pos_map.entrySet() ){
				//z=z+entry.getValue();	
				writer.println(entry.getKey()+" => "+entry.getValue());
				
			}
			writer.println("***************** negative map");
			for(Entry<String, Integer> entry : neg_map.entrySet() ){
				//z=z+entry.getValue();	
				writer.println(entry.getKey()+" => "+entry.getValue());
				
			}
			writer.close();
		}catch(IOException e){
			System.out.println(e.getStackTrace());
		}
		
	}



	private static void Readfromfolder(String string) {
		File folder = new File(string);
		
		String foldername=string.substring(string.length()-3);
		//System.out.println(string.substring(string.length()-3));
		File[] listOfFiles = folder.listFiles();
		//System.out.println(listOfFiles);
		
		if(foldername.equals("pos")){
			pos_map_length.put("pos", listOfFiles.length);
		}else if(foldername.equals("neg")){
			neg_map_length.put("neg", listOfFiles.length);
		}
		
		for (File file : listOfFiles) {
		    if (file.isFile()) {
		        //System.out.println(file.getName());
		    	read_from_file(file,foldername);
		    }
		}
		
	}
	
	
	private static void read_from_file(File filename, String foldername) {
		String line = null;
		try {
			
			
			// FileReader reads text files in the default encoding.
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);
			
			while ((line = br.readLine()) != null) {
				// System.out.println(line);
				
				String[] line_array = line.split(" +");
				
				for(String s: line_array){
					s=s.trim();
					if(foldername.equals("neg")){
						if(neg_map.containsKey(s)){
							int value=neg_map.get(s);
							value+=1;
							neg_map.put(s, value);
						}else{
							neg_map.put(s, 1);
						}
						
					}else if(foldername.equals("pos")){
						if(pos_map.containsKey(s)){
							int value=pos_map.get(s);
							value+=1;
							pos_map.put(s, value);
						}else{
							pos_map.put(s, 1);
						}
					}
					
					if(vocabulary.containsKey(s)){
						int value=vocabulary.get(s);
						value+=1;
						vocabulary.put(s, value);
					}else{
						vocabulary.put(s, 1);
					}
					
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
	
	
	private static void write_object_to_file() {
		try {
			List myobject=new ArrayList();
			myobject.add(vocabulary);
			myobject.add(pos_map);
			myobject.add(neg_map);
			myobject.add(pos_map_length);
			myobject.add(neg_map_length);
			FileOutputStream fos = new FileOutputStream("objectfile.txt");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(myobject);
			oos.close();
			fos.close();
			// System.out.printf("Serialized HashMap data is saved in hashmap.ser");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}
	
	private static void refine_maps() {
		
		for(Iterator<Map.Entry<String, Integer>> it = vocabulary.entrySet().iterator(); it.hasNext(); ) {
		      Map.Entry<String, Integer> entry = it.next();
		      if(entry.getValue()<5) {
		    	  if(pos_map.containsKey(entry.getKey())) {
		    		  pos_map.remove(entry.getKey());
		    	  }
		    	  if(neg_map.containsKey(entry.getKey())) {
		    		  neg_map.remove(entry.getKey());
		    	  }
		        it.remove();
		      }
		}

		
	}



}
