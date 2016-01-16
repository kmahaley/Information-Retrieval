package nbtrain;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class readobjectfromfile {

	static private Map<String, Integer> vocabulary=new LinkedHashMap<String, Integer>();
	static private Map<String, Integer> pos_map=new LinkedHashMap<String, Integer>();
	static private Map<String, Integer> neg_map=new LinkedHashMap<String, Integer>();
	static private Map<String, Integer> pos_map_length=new LinkedHashMap<String, Integer>();
	static private Map<String, Integer> neg_map_length=new LinkedHashMap<String, Integer>();
	
	public static void main(String[] args) {
		read_from_input_file();
		
//		System.out.println(vocabulary.size());
//		System.out.println(pos_map.size());
//		System.out.println(neg_map.size());
//		System.out.println(pos_map_length);
//		System.out.println(neg_map_length);
//		
//		for(Entry<String, Integer> entry : neg_map.entrySet() ){
//			System.out.print(entry.getKey()+" => "+entry.getValue()+" ");
//		}
//		System.out.println("");
//		for(Entry<String, Integer> entry : pos_map.entrySet() ){
//			System.out.print(entry.getKey()+" => "+entry.getValue()+" ");
//		}
//		System.out.println("");
//		for(Entry<String, Integer> entry : vocabulary.entrySet() ){
//			System.out.print(entry.getKey()+" => "+entry.getValue()+" ");
//		}

	}
	private static void read_from_input_file() {
		try {
			String objectfilename="objectfile.txt";
			FileInputStream fis = new FileInputStream(objectfilename);
			ObjectInputStream ois = new ObjectInputStream(fis);
			List readobject = (List) ois.readObject();

			vocabulary = (Map<String, Integer>) readobject.get(0);
			pos_map = (Map<String, Integer>) readobject.get(1);
			neg_map = (Map<String, Integer>) readobject.get(2);
			pos_map_length = (Map<String, Integer>) readobject.get(3);
			neg_map_length = (Map<String, Integer>) readobject.get(4);
			
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


}
