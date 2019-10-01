package TME3;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Indexing {
	
	public static HashMap<String, ArrayList<Point>> mots = new HashMap<>();
	public static HashMap<String, ArrayList<Point>> sorted_mots = new LinkedHashMap<String, ArrayList<Point>>(); 
	
	public static void main(String[] args) {
		try {
			read("resources/t");	        
	        
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void read(String filename) throws FileNotFoundException, IOException {
		String line = "";
		
		int line_number = 0;
		
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			while ((line = br.readLine()) != null) {
				if (line.isEmpty()) {
					line_number++;
					continue;
				}
				
				String word_line[] = line.trim().split("\\s*[^1-9a-zA-Z'-]+\\s*");
				int indice = 0;
				for (int i=0; i<word_line.length; i++) {
					
					int[] retenues = Match.retenues(word_line[i].toCharArray());
					int n = Match.matchingAlgo(word_line[i].toCharArray(), retenues, line.toCharArray(), indice);
					for(int j = 0; j< word_line[i].length(); j++) {
						line = line.substring(0,j+n)+' '+ line.substring(j+n+1);
					}
					System.out.println(word_line[i] + " : "+n);
					
					if (mots.containsKey(word_line[i])) {
						mots.get(word_line[i]).add(new Point(line_number,n));
					}else {
						mots.put(word_line[i], new ArrayList<>());
						mots.get(word_line[i]).add(new Point(line_number,n));
					}
						
				}
				System.out.println("ok");
				line_number++;
			}
				
			System.out.println("*************** Map non trie **************");
			for (String key : mots.keySet()) {
				System.out.print(key + " : ");
				for (Point p : mots.get(key)) {
					System.out.print(p + "| ");
				}
				System.out.println();
			}
			
			System.out.println("*************** Map trie **************");
			sorted_mots = sortByFrequence(mots);
	        for (String key : sorted_mots.keySet()) {
				System.out.print(key + " : ");
				for (Point p : sorted_mots.get(key)) {
					System.out.print(p + "| ");
				}
				System.out.println();
			}
		}
	}
	
	/** Trier hashmap index des mots **/
    public static HashMap<String, ArrayList<Point>> sortByFrequence(HashMap<String, ArrayList<Point>> mots) 
    { 

        List<Map.Entry<String, ArrayList<Point>> > list = 
               new LinkedList<Map.Entry<String, ArrayList<Point>> >(mots.entrySet()); 
  

        // Trie de la liste par la frequence du mots
        Collections.sort(list, new Comparator<Map.Entry<String, ArrayList<Point>> >() { 
            public int compare(Map.Entry<String, ArrayList<Point>> o1,  
                               Map.Entry<String, ArrayList<Point>> o2) 
            { 
            	if (o1.getValue().size() < o2.getValue().size()) {
                    return -1;
                } else {
                    return 1;
                } 
            }
        }); 
        
        for (Map.Entry<String, ArrayList<Point>> aa : list) { 
            sorted_mots.put(aa.getKey(), aa.getValue()); 
        }
        mots.clear();
        return sorted_mots; 
    } 
}
