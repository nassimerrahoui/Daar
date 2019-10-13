package TME3;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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

	public HashMap<String, ArrayList<Point>> mots = new HashMap<>();
	public HashMap<String, ArrayList<Point>> sorted_mots = new LinkedHashMap<String, ArrayList<Point>>();
	public String[] file_lines;

	/**
	 * Lit un fichier et fait un indexing dans un nouveau fichier
	 * Les mots sont classes par ordre croissant par leur nombre d'occurences dans le fichier de base
	 * et est aussi ecrit la position de toutes leurs occurences
	 * 
	 *  Exemple : 
	 *  
	 *  texte.txt : 
	 *  Voiture conducteur
	 *  conducteur
	 *  
	 *  -> 
	 *  index_table-texte.txt :
	 *  Voiture : 0,0 
	 *  conducteur : 0,8 1,0
	 *  
	 * @param filename
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void read(String filename) throws FileNotFoundException, IOException {
		String line = "";
		ArrayList<String> lines = new ArrayList<String>();

		int line_number = 0;

		try (BufferedReader br = new BufferedReader(new FileReader("resources/" +filename))) {
			
			while ((line = br.readLine()) != null) {
				lines.add(line);
				
				if (line.isEmpty()) {
					line_number++;
					continue;
				}

				String word_line[] = line.trim().split("\\s*[^1-9a-zA-Z'-]+\\s*");
				for (int i = 0; i < word_line.length; i++) {

					int[] retenues = Match.retenues(word_line[i].toCharArray());
					int n = Match.matchingAlgo(word_line[i].toCharArray(), retenues, line.toCharArray());
					for (int j = 0; j < word_line[i].length(); j++) {
						line = line.substring(0, j + n) + ' ' + line.substring(j + n + 1);
					}

					if (mots.containsKey(word_line[i])) {
						mots.get(word_line[i]).add(new Point(line_number, n));
					} else {
						mots.put(word_line[i], new ArrayList<>());
						mots.get(word_line[i]).add(new Point(line_number, n));
					}

				}
				line_number++;
			}
			
			file_lines = lines.toArray(new String[0]);
			
			FileWriter w = new FileWriter("result/index_table-"+ filename);
			BufferedWriter bw = new BufferedWriter(w);
			sorted_mots = sortByFrequence(mots);
			
			for (String key : sorted_mots.keySet()) {

				// les mots qui apparaissent plus de 1000 fois sont exclus.
				if (sorted_mots.get(key).size() > 1000)
					break;
				bw.write(key + " : ");
				for (Point p : sorted_mots.get(key)) {
					bw.write(" " + p.x + "," + p.y + " ");
				}
				bw.newLine();
			}
			bw.close();
			w.close();
		}
	}

	/** Trier hashmap index des mots **/
	public HashMap<String, ArrayList<Point>> sortByFrequence(HashMap<String, ArrayList<Point>> mots) {

		List<Map.Entry<String, ArrayList<Point>>> list = new LinkedList<Map.Entry<String, ArrayList<Point>>>(
				mots.entrySet());

		// Trie de la liste par la frequence du mots
		Collections.sort(list, new Comparator<Map.Entry<String, ArrayList<Point>>>() {
			public int compare(Map.Entry<String, ArrayList<Point>> o1, Map.Entry<String, ArrayList<Point>> o2) {
				if (o1.getValue().size() < o2.getValue().size()) {
					return -1;
				} else if (o1.getValue().size() > o2.getValue().size()) {
					return 1;
				} else {
					return 0;
				}
			}
		});

		for (Map.Entry<String, ArrayList<Point>> aa : list) {
			sorted_mots.put(aa.getKey(), aa.getValue());
		}
		mots.clear();
		return sorted_mots;
	}
	
	/**
	 * Retroune toutes les lignes du fichier dont le numero de ligne
	 * correspond aux pos de la liste positions
	 * @param positions
	 * @return
	 * @throws IOException
	 */
	public String[] getLines(ArrayList<Point> positions) throws IOException{
		String[] lines = new String[positions.size()];
		for (int i=0; i<positions.size(); i++)
			lines[i] = file_lines[positions.get(i).x];
		
		return lines;
		
	}
}
