package crawler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public class executecrawler {
	
	private static Set<String> inputList = new HashSet<String>();
	public static void main(String[] args) throws IOException,
			InterruptedException {
		
		int argumentlength = args.length;
		// System.out.println("Enter link and optional keyphrase");
		if (argumentlength != 0) {
			inputList.add(args[0]);
			if (argumentlength > 2) {
				System.out.println("Not more than 2 arguments please");
			} else if (argumentlength == 2) {
				crawler.crawlerMain(inputList, args[1].toLowerCase());
			} else {
				crawler.crawlerMain(inputList, "");
			}
		} else
			System.out.println("Enter atleast one argument");
	}

}
