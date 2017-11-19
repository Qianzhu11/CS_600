import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;

public class SearchEngine {
	Trie t = new Trie();
	Loader l = new Loader();
	List<String> stopWordList = Arrays.asList(new String[]{"a", "above", "about", "after", "again", "against", "all", "am", "an",
			"and", "any", "are", "as", "at", "be", "because", "been", "being", "below", "bwtween",
			"both", "but", "by", "cannot", "could", "did", "do", "does", "doing", "down", "during",
			"each", "few", "for", "from", "further", "had", "has", "have", "having", "he", "her", 
			"here", "hers", "herself", "him", "himself", "his", "how", "i", "if", "in", "into",
			"is", "it", "its", "itself", "me", "more", "most", "my", "myself", "no", "nor", "not",
			"of", "off", "on", "once", "only", "or", "other", "ought", "our", "ours"});
	
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
	private Map<String, ArrayList<Integer>> invertedFile = new HashMap<String, ArrayList<Integer>>();
	
	public void insertToTrie() {
		
	}
	
	public static void main(String[] args) {
		SearchEngine se = new SearchEngine();
		Document doc = se.l.loadPage("https://www.stevens.edu/news");
		Elements p = doc.select("p");
		String s = p.get(2).toString().toLowerCase();
		s = s.substring(3, s.length() - 5);
		String[] li = s.replaceAll("^[.,\\s]+", "").split("[.,\\s]+");
		for (String s1 : li) {
			System.out.println(s1);
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