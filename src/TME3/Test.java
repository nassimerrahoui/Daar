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
			sc.close();
			try {
				index.read(filename);
				System.out.println("Fin indexing...");
				
				RadixTree t = new RadixTree("result/index_table-"+filename);
				long startTime = System.currentTimeMillis();
				
				ArrayList<Point> positions = t.searchMotif("Sargon");
				
				for(String line : index.getLines(positions)) 
					System.out.println(line);
				
				
				long stopTime = System.currentTimeMillis();
			    long elapsedTime = stopTime - startTime;
			    
			    
			    System.out.println("Temps d'execution : " + elapsedTime + " ms");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			
			
			
			//t.getLines(positions);
			/*for(String line : t.getLines(positions)) {
				System.out.println(line);
			}*/
			
		
	}
		
}
