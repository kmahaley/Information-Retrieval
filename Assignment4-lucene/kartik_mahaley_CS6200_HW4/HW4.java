package hw4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * To create Apache Lucene index in a folder and add files into this index based
 * on the input of the user.
 */
public class HW4 {
	// private static Analyzer analyzer = new
	// StandardAnalyzer(Version.LUCENE_47);
	private static Analyzer sAnalyzer = new SimpleAnalyzer(Version.LUCENE_47);
	private static Map<String, Long> termfreqhshmap = new HashMap<String, Long>();
	//private static Map<Integer, Long> ranking = new HashMap<Integer, Long>();
	private static String hashmapfilename = "term_result.txt";

	private IndexWriter writer;
	private ArrayList<File> queue = new ArrayList<File>();

	public static void main(String[] args) throws IOException {
		System.out
				.println("Enter the FULL path where the index will be created: (e.g. /Usr/index or c:\\temp\\index)");

		String indexLocation = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s = br.readLine();

		HW4 indexer = null;
		try {
			indexLocation = s;
			indexer = new HW4(s);
		} catch (Exception ex) {
			System.out.println("Cannot create index..." + ex.getMessage());
			System.exit(-1);
		}

		// ===================================================
		// read input from user until he enters q for quit
		// ===================================================
		while (!s.equalsIgnoreCase("q")) {
			try {
				System.out
						.println("Enter the FULL path to add into the index (q=quit): (e.g. /home/mydir/docs or c:\\Users\\mydir\\docs)");
				System.out
						.println("[Acceptable file types: .xml, .html, .html, .txt]");
				s = br.readLine();
				if (s.equalsIgnoreCase("q")) {
					break;
				}

				// try to add file into the index
				indexer.indexFileOrDirectory(s);
			} catch (Exception e) {
				System.out.println("Error indexing " + s + " : "
						+ e.getMessage());
			}
		}

		// ===================================================
		// after adding, we always have to call the
		// closeIndex, otherwise the index is not created
		// ===================================================
		indexer.closeIndex();

		// =========================================================
		// Now search
		// =========================================================
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(
				indexLocation)));
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(100, true);

		createhashmap(reader);

		s = "";
		while (!s.equalsIgnoreCase("q")) {
			try {
				System.out.println("Enter the search query (q=quit):");
				s = br.readLine();
				if (s.equalsIgnoreCase("q")) {
					break;
				}

				Query q = new QueryParser(Version.LUCENE_47, "contents",
						sAnalyzer).parse(s);
				searcher.search(q, collector);
				ScoreDoc[] hits = collector.topDocs().scoreDocs;

				// 4. display results
				System.out.println("Found " + hits.length + " hits.");
				for (int i = 0; i < hits.length; ++i) {
					int docId = hits[i].doc;
					Document d = searcher.doc(docId);
					System.out.println((i + 1) + ". " + d.get("path")
							+ " score=" + hits[i].score);
				}
				// 5. term stats --> watch out for which "version" of the term
				// must be checked here instead!
				Term termInstance = new Term("contents", s);
				long termFreq = reader.totalTermFreq(termInstance);
				long docCount = reader.docFreq(termInstance);
				System.out.println(s + " Term Frequency " + termFreq
						+ " - Document Frequency " + docCount);

			} catch (Exception e) {
				System.out.println("Error searching " + s + " : "
						+ e.getMessage());
				break;
			}

		}

	}
// this part of code is taken reference from stack overflow site
	private static void createhashmap(IndexReader reader) throws IOException {
		Fields fields = MultiFields.getFields(reader);
		for (String field : fields) {
			if (field.equals("contents")) {
				Terms terms = fields.terms(field);
				TermsEnum termsEnum = terms.iterator(null);
				//Integer c=0;
				while (termsEnum.next() != null) {
					String termText = termsEnum.term().utf8ToString();
					Term termInstance = new Term("contents", termsEnum.term()
							.utf8ToString());
					long termFreq = reader.totalTermFreq(termInstance);
					//c++;
					termfreqhshmap.put(termText, termFreq);
					//ranking.put(c, termFreq);
				}
			}
		}
		TreeMap<String, Long> sortedmap = sortbyvalue(termfreqhshmap);
		writetreemaptofile(sortedmap);
	}

	private static void writetreemaptofile(TreeMap<String, Long> sortedmap) {
		try {
			Writer writertofile = null;

			File file = new File(hashmapfilename);
			writertofile = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file)));
			Integer c=0;
			for (Map.Entry<String, Long> entry : sortedmap.entrySet()) {
				c++;
				writertofile.write(entry.getKey() + "\t\t"+c+"\t\t"+ entry.getValue()+"\n" );
				}
			writertofile.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static TreeMap<String, Long> sortbyvalue(Map<String, Long> map) {
		rankcomparator rc = new rankcomparator(map);
		TreeMap<String, Long> sortedMapinternal = new TreeMap<String, Long>(rc);
		sortedMapinternal.putAll(map);
		return sortedMapinternal;
	}

	/**
	 * Constructor
	 * 
	 * @param indexDir
	 *            the name of the folder in which the index should be created
	 * @throws java.io.IOException
	 *             when exception creating index.
	 */
	HW4(String indexDir) throws IOException {

		FSDirectory dir = FSDirectory.open(new File(indexDir));

		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47,
				sAnalyzer);

		writer = new IndexWriter(dir, config);
	}

	/**
	 * Indexes a file or directory
	 * 
	 * @param fileName
	 *            the name of a text file or a folder we wish to add to the
	 *            index
	 * @throws java.io.IOException
	 *             when exception
	 */
	public void indexFileOrDirectory(String fileName) throws IOException {
		// ===================================================
		// gets the list of files in a folder (if user has submitted
		// the name of a folder) or gets a single file name (is user
		// has submitted only the file name)
		// ===================================================
		addFiles(new File(fileName));

		int originalNumDocs = writer.numDocs();
		for (File f : queue) {
			FileReader fr = null;
			try {
				Document doc = new Document();

				// ===================================================
				// add contents of file
				// ===================================================
				fr = new FileReader(f);
				doc.add(new TextField("contents", fr));
				doc.add(new StringField("path", f.getPath(), Field.Store.YES));
				doc.add(new StringField("filename", f.getName(),
						Field.Store.YES));

				writer.addDocument(doc);
				System.out.println("Added: " + f);
			} catch (Exception e) {
				System.out.println("Could not add: " + f);
			} finally {
				fr.close();
			}
		}

		int newNumDocs = writer.numDocs();
		System.out.println("");
		System.out.println("************************");
		System.out
				.println((newNumDocs - originalNumDocs) + " documents added.");
		System.out.println("************************");

		queue.clear();
	}

	private void addFiles(File file) {

		if (!file.exists()) {
			System.out.println(file + " does not exist.");
		}
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				addFiles(f);
			}
		} else {
			String filename = file.getName().toLowerCase();
			// ===================================================
			// Only index text files
			// ===================================================
			if (filename.endsWith(".htm") || filename.endsWith(".html")
					|| filename.endsWith(".xml") || filename.endsWith(".txt")) {
				removehtmltags(file);
				queue.add(file);
			} else {
				System.out.println("Skipped " + filename);
			}
		}
	}

	private void removehtmltags(File file) {
		// TODO Auto-generated method stub
		try {
				Path pathvariable = Paths.get(file.toURI());
				Charset characterset = StandardCharsets.UTF_8;

				String stringvalue = null;
				stringvalue = new String(Files.readAllBytes(pathvariable), characterset);
				
				stringvalue = stringvalue.toLowerCase().replaceAll("<html>", " ").replaceAll("</html>", " ");
				stringvalue = stringvalue.replaceAll("<pre>", " ").replaceAll("</pre>", " ");
				Files.write(pathvariable, stringvalue.getBytes(characterset));
		

				FileReader fr = null;
				fr = new FileReader(file);
				
		}catch (FileNotFoundException e) {
			System.out.println("File not found : "+file);
		} 
		catch (IOException e1) {
			System.out.println("IO exception occurred");
		
				} 
		
	}
	/**
	 * Close the index.
	 * 
	 * @throws java.io.IOException
	 *             when exception closing
	 */
	public void closeIndex() throws IOException {
		writer.close();
	}
}