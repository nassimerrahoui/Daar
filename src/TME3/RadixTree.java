package TME3;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class RadixTree {

	HashMap<String, RadixTree> fils = new HashMap<String, RadixTree>();
	ArrayList<Point> occurences = new ArrayList<Point>();
    boolean isWord = true;
	String source_textfile_name;
	
	/** Constructeur par defaut **/
	public RadixTree() {}
	
	/** Construction a partir d'un index **/
	public RadixTree(String indexfilename) throws IOException {
		String line = "";

		try (BufferedReader br = new BufferedReader(new FileReader(indexfilename))) {
			source_textfile_name = indexfilename.split("-")[1];
					
			while ((line = br.readLine()) != null) {
				if (line.isEmpty()) {
					continue;
				}
				
				ArrayList<Point> pos = new ArrayList<Point>();
				
				String[] ligne = line.split(" : ");
				String[] points = ligne[1].split(" ");
				
				for (int i=0; i<points.length; i++) {
					String[] coord = points[i].split(",");
					if(coord.length == 2)
						pos.add(new Point(Integer.parseInt(coord[0]), Integer.parseInt(coord[1])));
				}
				
				addRadixTree(ligne[0], pos);
			}
		}
	}

	public void addRadixTree(String mot, ArrayList<Point> pos) {
		boolean creer_fils = true;
		String k = "";
		String cle = "";
		String suffixe = "";

		for (String key : fils.keySet()) {
			char[] lettres = key.toCharArray();
			char[] prefixes = mot.toCharArray();

			int bon_char = 0;

			if (mot.equals(key)) {
				fils.get(key).isWord = true;
				fils.get(key).occurences = pos;
				return;
			}

			for (int i = 0; i < lettres.length; i++) {
				if (i >= mot.length())
					break;

				if (prefixes[i] == lettres[i])
					bon_char++;

				if (prefixes[i] != lettres[i] && bon_char > 0) {
					
					RadixTree t = new RadixTree();
					t.isWord = false;
					
					RadixTree u = new RadixTree();
					u.fils = fils.get(key).fils;
					u.occurences = fils.get(key).occurences;
					
					t.fils.put(key.substring(bon_char), u);
					t.occurences = fils.get(key).occurences;
					
					
					RadixTree v = new RadixTree();
					v.occurences = pos;
					t.fils.put(mot.substring(bon_char), v);
					
					fils.put(key.substring(0, bon_char), t);
					//fils.get(key.substring(0, bon_char)).isWord = false;
					
					k = mot.substring(bon_char);
					cle = key.substring(0, bon_char);
					fils.remove(key);
					creer_fils = false;
					return;
				}

			}

			if (bon_char == lettres.length) {
				creer_fils = false;
				k = key;
				suffixe = mot.substring(lettres.length);
				break;
			} else if (bon_char == prefixes.length && mot.length() < key.length()) {
				RadixTree t = new RadixTree();
				String suffixe2 = key.substring(mot.length());
				
				t.occurences = pos;
				t.isWord = isWord;

				fils.put(key.substring(0, mot.length()), t);
				t.fils.put(suffixe2, fils.get(key));
				fils.remove(key);

				return;
			}
		}

		if (creer_fils) {
			RadixTree t = new RadixTree();
			t.occurences = pos;
			fils.put(mot, t);
		} else if (cle != "") {
			fils.get(cle).fils.get(k).addRadixTree(suffixe, pos);
			
		} else {
			fils.get(k).addRadixTree(suffixe, pos);
		}

	}

	public void affichage(String prefix, int profondeur) {
		for (String key : fils.keySet()) {
			if (fils.get(key).isWord) {
				System.out.print(prefix + key + " : " + profondeur + " | ");
				for (Point p : fils.get(key).occurences)
					System.out.print(p + " ");
				System.out.println();

			}
			fils.get(key).affichage(prefix + key, profondeur + 1);
		}
	}
	
	public void affichage() {
		affichage("", 0);
	}

	public ArrayList<Point> searchMotif(String mot, String suffixe, String temoin) {
		ArrayList<Point> points = new ArrayList<Point>();
		boolean trouve = false;

		if (suffixe == "") {
			temoin = "";
			suffixe = mot;
		}
		
		if (Objects.equals(temoin, mot) && isWord) {
			trouve = true;
			points = occurences;
		}

		System.out.println(temoin + " : " + suffixe);
		
		for (String key : fils.keySet()) {
			if (key.length() <= suffixe.length()) {
				char[] key_array = key.toCharArray();
				char[] suffixe_array = suffixe.toCharArray();
				int bon_char = 0;
	
				for (int i = 0; i < key_array.length; i++) {
					if (key_array[i] == suffixe_array[i]) {
						bon_char++;
					}
					
					if (bon_char == key_array.length) {
						temoin += suffixe.substring(0, key_array.length);
						suffixe = suffixe.substring(key_array.length);
						points = (fils.get(key).searchMotif(mot, suffixe, temoin));
						break;
					}
				}
				if (trouve)
					break;
			}
		}
		System.out.println(occurences);
		System.out.println(points);
		return occurences;
	}
	
	public ArrayList<Point> searchMotif(String mot){
		return searchMotif(mot, "", "");
	}

	public static void main(String[] args) {
		
		/*
		ArrayList<Point> ligne0 = new ArrayList<Point>();
		ligne0.add(new Point(0,5));
		ligne0.add(new Point(0,7));
		ligne0.add(new Point(0,10));
		
		ArrayList<Point> ligne1 = new ArrayList<Point>();
		ligne1.add(new Point(1,2));
		ligne1.add(new Point(1,6));
		ligne1.add(new Point(1,14));
		
		ArrayList<Point> ligne2 = new ArrayList<Point>();
		ligne2.add(new Point(2,3));
		ligne2.add(new Point(2,9));
		ligne2.add(new Point(2,20));
		
		ArrayList<Point> ligne3 = new ArrayList<Point>();
		ligne3.add(new Point(3,30));
		ligne3.add(new Point(3,34));
		ligne3.add(new Point(3,39));
		
		ArrayList<Point> ligne4 = new ArrayList<Point>();
		ligne4.add(new Point(4,45));
		ligne4.add(new Point(4,41));
		ligne4.add(new Point(4,46));
		
		ArrayList<Point> ligne5 = new ArrayList<Point>();
		ligne5.add(new Point(5,55));
		ligne5.add(new Point(5,51));
		ligne5.add(new Point(5,56));
		
		
		RadixTree t = new RadixTree();
		t.addRadixTree("RATATA", ligne0);
		t.addRadixTree("RAT", ligne4);
		t.addRadixTree("RAA", ligne3);
		t.addRadixTree("RAA", ligne3);
		t.addRadixTree("RA", ligne1);
		t.addRadixTree("R", ligne2);
		t.addRadixTree("T", ligne5);
		
		t.affichage("", 0);
		*/
		

//		RadixTree r = new RadixTree();
//		r.addRadixTree("RATON", ligne4);
//		r.addRadixTree("RATIR", ligne3);
//		r.addRadixTree("RA", ligne0);
//		r.affichage("", 0);

//		RadixTree o = new RadixTree();
//		o.addRadixTree("RA", ligne0);
//		o.addRadixTree("R", ligne2);
//		o.addRadixTree("T", ligne1);
//		o.affichage("", 0);

//		System.out.println();
//		String mot = "RA";
//		boolean present = o.searchMotif(mot, "", "");
//		if (present)
//			System.out.println(mot + " est present");
//		else
//			System.out.println(mot + " est non present");
	}

}