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
import java.util.Scanner;

public class Indexing {

	public static HashMap<String, ArrayList<Point>> mots = new HashMap<>();
	public static HashMap<String, ArrayList<Point>> sorted_mots = new LinkedHashMap<String, ArrayList<Point>>();

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println(" >> Please enter a filename from resources file : ");
		String filename = sc.next();
		sc.close();
		try {
			read("resources/" + filename);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Fin indexing...");
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
				for (int i = 0; i < word_line.length; i++) {

					int[] retenues = Match.retenues(word_line[i].toCharArray());
					int n = Match.matchingAlgo(word_line[i].toCharArray(), retenues, line.toCharArray());
					for (int j = 0; j < word_line[i].length(); j++) {
						line = line.substring(0, j + n) + ' ' + line.substring(j + n + 1);
					}
					// System.out.println(word_line[i] + " : "+n);

					if (mots.containsKey(word_line[i])) {
						mots.get(word_line[i]).add(new Point(line_number, n));
					} else {
						mots.put(word_line[i], new ArrayList<>());
						mots.get(word_line[i]).add(new Point(line_number, n));
					}

				}
				line_number++;
			}

			// DEBUG MODE

			// System.out.println("*************** Map non trie **************");
			// for (String key : mots.keySet()) {
			// System.out.print(key + " : ");
			// for (Point p : mots.get(key)) {
			// System.out.print(p + "| ");
			// }
			// System.out.println();
			// }
			//
			// System.out.println("*************** Map trie **************");
			// sorted_mots = sortByFrequence(mots);
			// for (String key : sorted_mots.keySet()) {
			// System.out.print(key + " : ");
			// for (Point p : sorted_mots.get(key)) {
			// System.out.print(p + "| ");
			// }
			// System.out.println();
			// }

			FileWriter w = new FileWriter("result/index_table.txt");
			BufferedWriter bw = new BufferedWriter(w);
			sorted_mots = sortByFrequence(mots);
			
			for (String key : sorted_mots.keySet()) {

				// les mots qui apparaissent plus de 100 sont exclus.
				if (sorted_mots.get(key).size() > 100)
					break;
				bw.write(key + " : ");
				for (Point p : sorted_mots.get(key)) {
					bw.write("(" + p.x + "," + p.y + ") | ");
				}
				bw.newLine();
			}
			bw.close();
			w.close();
		}
	}

	/** Trier hashmap index des mots **/
	public static HashMap<String, ArrayList<Point>> sortByFrequence(HashMap<String, ArrayList<Point>> mots) {

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

	public static ArrayList<String> searchPatternInIndex(String index_filename, String motif)
			throws FileNotFoundException, IOException {
		ArrayList<String> result = new ArrayList<>();
		String line = "";

		try (BufferedReader br = new BufferedReader(new FileReader(index_filename))) {
			while ((line = br.readLine()) != null) {
				if (line.isEmpty()) {
					continue;
				}

				String word_line[] = line.trim().split(":");

				/** TO DO **/
				/** Changer le parcours en parcours dichotomique **/

				char[] motif_array = motif.toCharArray();

				for (int i = 0; i < word_line.length; i++) {
					char[] word_array = word_line[i].toCharArray();
					for (int j = 0; j < word_array.length; j++) {
						if (word_array[j] != motif_array[j])
							result.add(word_line[i]);
					}
				}
			}
		}
		return null;
	}
}
