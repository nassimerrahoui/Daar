package TME3;

import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Test {
	
	public static void main(String[] args) {
		
			Indexing index = new Indexing();
			Scanner sc = new Scanner(System.in);
			System.out.println(" >> Please enter a filename from resources file : ");
			String filename = sc.next();
			
			try {
				index.read(filename);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.out.println("Fin indexing...");
			
			String mot = "";
			
			while(true) {
				try {
					System.out.println(" >> Please enter a word to look for (STOPEND to stop) : ");
					mot = sc.next();
					
					if(mot.equals("STOPEND"))
						break;
					
					RadixTree t = new RadixTree("result/index_table-"+filename);
					
					long startTime = System.currentTimeMillis();
					
					ArrayList<Point> positions = t.searchMotif(mot);
					
					for(String line : index.getLines(positions)) 
						System.out.println(line);
					
					
					long stopTime = System.currentTimeMillis();
				    long elapsedTime = stopTime - startTime;
				    
				    
				    System.out.println("Temps d'execution : " + elapsedTime + " ms");
				
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			sc.close();
		
	}
		
}
