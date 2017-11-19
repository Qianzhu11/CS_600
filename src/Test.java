import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Loader l = new Loader();
		Document d = l.loadPage("http://www.taobao.com");
		for (Element link : d.getElementById("").getElementsByTag("li")) {
			System.out.println(link.text());
		}
	}
}
