package crawler;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class testing {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String myurl = "https://en.wikipedia.org/wiki/Hugh_of_Saint-Cher";
		Connection connection = Jsoup.connect(myurl);
		Document doc = connection.timeout(0).get();
		Elements elts = doc.select("a");
		String htmlContent = doc.text();
		System.out.println(htmlContent);
		if(htmlContent.contains("concordance"))
			System.out.println("YES");
	}

}
