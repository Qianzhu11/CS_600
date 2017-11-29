import java.util.*;

public class Test {
	
	public static int countMatches(String str, String sub) {
	      int count = 0;
	      int idx = 0;
	      while ((idx = str.indexOf(sub, idx)) != -1) {
	          count++;
	          idx += sub.length();
	      }
	      return count;
	}
	  
	
	public static void main(String[] args) throws Exception {
		Test t = new Test();
		List<Integer> l = new ArrayList<Integer>();
		l.add(0, 4);
		l.add(1, 2);
		l.add(2, 4);
		l.add(3, 1);
		System.out.println(l.set(0, 1));
		System.out.println(l.set(0, 2));
		while (!l.isEmpty()) {
			int max = l.get(0);
			int delete = 0;
			for (int i = 0; i < l.size(); i++) {
				if (l.get(i) > max) {
					delete = i;
					max = l.get(i);
				}
			}
			System.out.println(l.get(delete));
			l.remove(delete);
		}
		
		
	}
}
