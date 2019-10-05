package TME3;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

public class RadixTree {
	
	HashMap<String,RadixTree> fils = new HashMap <String,RadixTree>();
	ArrayList<Point> occurences = new ArrayList<Point>();
	boolean isWord = true;
	
	public void addRadixTree(String mot, Point pos) {
		boolean creer_fils = true;
		String k = "";
		String cle = "";
		String suffixe = "";
		
		for(String key : fils.keySet()) {
			char[] lettres = key.toCharArray();
			char[] prefixes = mot.toCharArray();
			
			int bon_char = 0;
			
			if(mot.equals(key)) {
				isWord = true;
				return;
			}
			
			for (int i=0; i<lettres.length; i++) {
				if(i >= mot.length())
					break;
				
				if(prefixes[i] == lettres[i])
					bon_char++;
					
				if(prefixes[i] != lettres[i] && bon_char > 0) {
					RadixTree t = new RadixTree();
					t.isWord = false;
					RadixTree u = new RadixTree();
					u.fils = fils.get(key).fils;
					t.fils.put(key.substring(bon_char), u);
					t.fils.put(mot.substring(bon_char),new RadixTree());
					fils.put(key.substring(0,bon_char), t);
					k = mot.substring(bon_char);
					cle = key.substring(0,bon_char);
					fils.remove(key);
					creer_fils = false;
					return;
				}

			}
			
			
			if(bon_char == lettres.length) {
				creer_fils = false;
				k = key;
				suffixe = mot.substring(lettres.length);
				break;
			} else if(bon_char == prefixes.length && mot.length() < key.length()) {
				
				RadixTree t = new RadixTree();
				String suffixe2 = key.substring(mot.length());
				
				t.isWord = isWord;
					
				fils.put(key.substring(0,mot.length()), t);
				t.fils.put(suffixe2, fils.get(key));
				fils.remove(key);
				
				return;
			} 
		}
		
		if(creer_fils) {
			fils.put(mot, new RadixTree());
		}else if(cle != ""){
			fils.get(cle).fils.get(k).addRadixTree(suffixe, pos);
			
		}else {
			fils.get(k).addRadixTree(suffixe, pos);
		}
			
	}
	
	public void affichage(String prefix, int profondeur) {
		for(String key : fils.keySet()) {
			if(fils.get(key).isWord)
				System.out.println(prefix + key + " : "+profondeur);
			fils.get(key).affichage(prefix+key, profondeur+1);
		}
	}
	
	public static void main(String[] args) {
//		RadixTree t = new RadixTree();
//		t.addRadixTree("RATATA", null);
//		t.addRadixTree("RAT", null);
//		t.addRadixTree("RAA", null);
//		
//		t.addRadixTree("RAA", null);
//	
//		t.addRadixTree("RA", null);
//		t.addRadixTree("R", null);
//		t.addRadixTree("T", null);
//	
//		
//		
//		
//		t.affichage("", 0);
		
//		RadixTree r = new RadixTree();
//		r.addRadixTree("RATON", null);
//		r.addRadixTree("RATIR", null);
//		r.addRadixTree("RA", null);
//		r.affichage("", 0);
		
		RadixTree o = new RadixTree();
		o.addRadixTree("RA", null);
		o.addRadixTree("R", null);
		o.addRadixTree("T", null);
		o.affichage("", 0);
	}
	
}
