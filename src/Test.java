import java.util.Scanner;

public class Test {

	public static void main(String[] args) {
		 Scanner s = new Scanner(System.in); 
         System.out.println("ÇëÊäÈë×Ö·û´®£º"); 
         while (true) { 
                 String line = s.nextLine(); 
                 if (line.equals("exit")) break; 
                 System.out.println(line); 
         } 
	}
}
