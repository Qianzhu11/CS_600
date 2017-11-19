import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;

public class SearchEngine {
	public static final String startPage = "https://www.stevens.edu/news";
	Trie t = new Trie();
	Loader l = new Loader();
	List<String> stopWordList = Arrays.asList(new String[]{"a", "above", "about", "after", "again", "against", 
			"all", "am", "an", "only", "or", "other", "ought", "our", "ours", "on", "once","of", "off",
			"and", "any", "are", "as", "at", "be", "because", "been", "being", "below", "bwtween",
			"both", "but", "by", "cannot", "could", "did", "do", "does", "doing", "down", "during",
			"each", "few", "for", "from", "further", "had", "has", "have", "having", "he", "her", 
			"here", "hers", "herself", "him", "himself", "his", "how", "i", "if", "in", "into",
			"is", "it", "its", "itself", "me", "more", "most", "my", "myself", "no", "nor", "not"});
	
	Set<String> stopWords = new HashSet<String>();	//we use set to store stop words since contains operation in set is O(1).
	{
		for (String s : stopWordList) {
			stopWords.add(s);
		}
	}
	
	public void insert(List<String> s) {
		for (int i = 0; i < s.size(); i++) {
			String word = s.get(i).toLowerCase();
			if (!stopWords.contains(s)) t.insert(word);
		}
	}
	
	/*
	 * we use map to store the index term and occurrence lists.
	 */
	private Map<String, ArrayList<String>> invertedFile = new HashMap<String, ArrayList<String>>();
	
	public void insertToTrie(String page) {
		Document doc = l.loadPage(page);
		Elements e = doc.select("p");
		for (int i = 0; i < e.size(); i++) {
			String s = e.get(i).toString().toLowerCase();
			String[] li = s.replaceAll("[\\pP\\p{Punct}]", "").split("[.,\\s]+");
			for (String string : li) {
				if (!stopWords.contains(string)) {
					if (t.search(string)) {
						ArrayList<String> res = invertedFile.get(string);
						if (!res.contains(page)) {
							res.add(page);
							invertedFile.put(string, res);
						}			
					} else {
						t.insert(string);
						ArrayList<String> res = new ArrayList<String>();
						res.add(page);
						invertedFile.put(string, res);
					}
				}
			}
		}
	}
	
	public HashSet<String> getResult(String[] s) {
		HashSet<String> res = new HashSet<String>();
		for (int i = 0; i < s.length; i++) {
			ArrayList<String> s1 = invertedFile.get(s[i]);
			for (String s2 : s1) res.add(s2);
		}
		return res;
	}
	
	public static void main(String[] args) {
		SearchEngine se = new SearchEngine();
		se.insertToTrie(startPage);
		Elements links = se.l.loadPage(startPage).select("a[href]");
		for (int i = 0; i < 10; i++) {
			Element link = links.get(i);
			se.insertToTrie(link.attr("abs:href"));
		}
		List<String> li = new ArrayList<String>();
		Scanner s = new Scanner(System.in);
		System.out.println("Please type the words you want to search for:");
		System.out.println("type quit() to exit");
		while (true) {
			if (s.hasNextInt()) {
				System.out.println("You must provide valid words.");
				return;
			}
			String line = s.nextLine();
			if (line.equals("quit()")) break; 
			li.add(line);
		}
		String[] s1 = new String[li.size()];
		s1 = li.toArray(s1);
		HashSet<String> res = se.getResult(s1);
		System.out.println("Here are the results:");
		for (String s2 : res) {
			System.out.println(s2);
		}
	}
}

class Loader {
	public Document loadPage(String url) {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}
}