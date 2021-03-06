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
	Map<String, String> pages = new HashMap<String, String>();
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
		String s1 = "";
		
		for (int i = 0; i < e.size(); i++) {
			s1 += e.get(i).toString().toLowerCase();
		}
		pages.put(page, s1);
		
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
	
	public List<String> getResult(String[] s) {
		List<String> res = new ArrayList<String>();
		List<String> web = new ArrayList<String>();
		
		for (int i = 0; i < s.length; i++) {
			if (!invertedFile.containsKey(s[i])) {
				return null;
			} else res.add(s[i]);
		}
		if (res.size() == 1) return invertedFile.get(res.get(0));
		else {
			web = invertedFile.get(res.get(0));
			for (int i = 1; i < res.size(); i++) {
				web = intersection(web, invertedFile.get(res.get(i)));
			}
			return web;
		}
	}
	
	public <T> List<T> intersection(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<T>();
        
        for (T t : list1) {
            if(list2.contains(t)) {
                list.add(t);
            }
        }
        return list;
    }
	
	public List<String> sort(List<String> res, String[] words) {
		List<String> newRes = new ArrayList<String>(res.size());
		List<Integer> freq = new ArrayList<Integer>(res.size());
		for (int i = 0; i < res.size(); i++) {
			int count = 0;
			String s1 = pages.get(res.get(i));
			for (int j = 0; j < words.length; j++) {
				count += countMatches(s1, words[j]);
			}
			freq.add(count);
		}
		while (!freq.isEmpty()) {
			int max = freq.get(0);
			int delete = 0;
			for (int i = 0; i < freq.size(); i++) {
				if (freq.get(i) > max) {
					delete = i;
					max = freq.get(i);
				}
			}
			newRes.add(res.get(delete));
			res.remove(delete);
			freq.remove(delete);
		}
		return newRes;
	}
	
	public static int countMatches(String str, String sub) {
	      int count = 0;
	      int idx = 0;
	      while ((idx = str.indexOf(sub, idx)) != -1) {
	          count++;
	          idx += sub.length();
	      }
	      return count;
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
		System.out.println("type quit() to end typing.");
		
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
		boolean containStopWords = false;
		for (int i = 0; i < s1.length; i++) if (se.stopWords.contains(s1[i])) containStopWords = true;
		if (containStopWords == true) System.out.println("Your are searching the stop words.");
		else {
			List<String> res = se.getResult(s1);
			if (res == null) System.out.println("Sorry, no such word or words combination in these websites.");
			else {
				List<String> newRes = se.sort(res, s1);
				System.out.println("Here are the results(sorted by words frequence):");
				for (String s2 : newRes) System.out.println(s2);
			}
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