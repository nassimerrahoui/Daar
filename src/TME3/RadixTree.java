package TME3;

import java.util.HashMap;

public class RadixTree {
	
	HashMap<String,RadixTree> fils = new HashMap <String,RadixTree>();
	boolean isWord = true;
	
	public void addRadixTree(String mot) {
		boolean creer_fils = true;
		String k = "";
		String suffixe = "";
		
		for(String key : fils.keySet()) {
			
			char[] lettres = key.toCharArray();
			char[] prefixes = mot.toCharArray();
			
			int bon_char = 0;
			
			if(mot.equals(key))
				return;
			
			for (int i=0; i<lettres.length; i++) {
			
				if(prefixes[i] == lettres[i])
					bon_char++;
					
				if(prefixes[i] != lettres[i] && bon_char > 0) {
					RadixTree t = new RadixTree();
					t.fils.put(key.substring(bon_char),new RadixTree());
					t.fils.put(mot.substring(bon_char),new RadixTree());
					fils.put(key.substring(0,bon_char), t);
					fils.remove(key);
					creer_fils = false;
					isWord = false;
					break;
				}

			}
			
			if(bon_char == lettres.length) {
				creer_fils = false;
				k = key;
				suffixe = mot.substring(lettres.length);
				break;
			}
		}
		
		if(creer_fils) {
			fils.put(mot, new RadixTree());
		}else {
			fils.get(k).addRadixTree(suffixe);
			
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
		RadixTree t = new RadixTree();
		t.addRadixTree("R");
		t.addRadixTree("T");
		t.addRadixTree("RA");
		t.addRadixTree("RAA");
		t.addRadixTree("RAA");
		t.addRadixTree("RAT");
		t.addRadixTree("RATATATATATATATATA");
		t.affichage("", 0);
		
	}
	
}
