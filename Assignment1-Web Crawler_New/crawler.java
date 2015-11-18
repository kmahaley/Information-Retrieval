package crawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class crawler {

	// static varibales to keep track of visited links, to be visited links
	// and unique links
	private static final int MYDEPTH = 6;
	private static final int TOCRAWL = 1000;
	private static final int DELAY = 1000;
	private static String appendedstring;
	private static Set<String> pagesvisited = new HashSet<String>();
	private static int depth = 1;
	private static Set<String> currentList = new HashSet<String>();
	private static Set<String> childList = new HashSet<String>();

	// Checking if given link is to be discared or not
	private static boolean checkurl(String appended, String host, String path)
			throws IOException, MalformedURLException {
		return (!path.contains("#") && !path.contentEquals("/wiki/Main_Page")
				&& !path.contains(":") && host.contains("en.wikipedia.org")
				&& path.startsWith("/wiki/")
				&& !pagesvisited.contains("https://" + host + path) && !appended
					.contentEquals(""));
	}

	// this function is to make proper complete links
	public static String appendcode(String s) {
		if (!s.startsWith("http")) {
			if (s.startsWith("/")) {
				s = "https://en.wikipedia.org" + s;
			} else if (s.startsWith("//")) {
				s = "https:" + s;
			} else if (s.startsWith("#"))
				return "";
			else
				return "";
		}
		return s;
	}

	// this function returns boolean value if HTML contains keyphrase
	public static boolean validateHtml(String htmlContent, String keyphrase) {
		// System.out.println("********************** in focusedCrawl function");
		return (htmlContent.toLowerCase().contains(keyphrase));
	}

	private static void getChildList(Elements elts) throws IOException {
		for (Element e : elts) {
			appendedstring = appendcode(e.attr("href").toString());
			if (appendedstring != "") {
				URL myurl = new URL(appendedstring);
				String host = myurl.getHost().toString();
				String path = myurl.getPath().toString();

				if (checkurl(appendedstring, host, path) && !pagesvisited.contains("https://" + host + path)) {
					childList.add("https://" + host + path);
				}
				;
			}
		}
	}

	//This function verifies condition before calling child function 
	public static void crawlerMain(Set<String> crawlList, String key) {
		// System.out.println("********************** in searchlist function");
		if (depth < MYDEPTH && pagesvisited.size() < TOCRAWL) {
			focusedcrawlNew(crawlList, key);
		}
	}
	
	// Iterating parent and collecting its child in childlist
	private static void focusedcrawlNew(Set<String> crawlList, String keyphrase) {
		try {
			/*
			 * System.out.println(crawlList.size()+
			 * "********************** in focusedcrawlNew function");
			 */
			for (Iterator<String> iterator = crawlList.iterator(); iterator.hasNext();) {
				String url = iterator.next();
			//for (String parentelement : crawlList) {
			//	String url = parentelement;
				Thread.sleep(DELAY);
				Connection connection = Jsoup.connect(url);
				Document doc = connection.timeout(0).get();
				Elements elts = doc.select("a");
				String htmlContent = doc.text();
				if (depth < MYDEPTH && pagesvisited.size() < TOCRAWL) {

					if (keyphrase != "") {
						if (validateHtml(htmlContent, keyphrase)) {
							pagesvisited.add(url);
							System.out.println(url);
							getChildList(elts);
						}
					} else {
						pagesvisited.add(url);
						System.out.println(url);
						getChildList(elts);
					}

				} else if (pagesvisited.size() == TOCRAWL)
					break;
			}

			System.out.println("Unique URL numbers== "+pagesvisited.size()+"    DEPTH == "+depth);
			/*System.out.println("SIZE current&pagevisited::::::::::::::::"
					+ currentList.size() + " :::::: " + pagesvisited.size()
					+ "::::::::::" + depth);
			System.out.println("childlist:::" + childList.size());*/
			currentList.clear();
			depth++;
			currentList.addAll(childList);
			crawlerMain(currentList, keyphrase);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
