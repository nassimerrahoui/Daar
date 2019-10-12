package TME1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Automate {
	int[][] transition;
	boolean[] entrees;
	boolean[] sorties;
	boolean[][] epsilon;
	boolean sortie_directe;

	HashMap<Set<Integer>, Set<Integer>[]> etats;

	public Automate(RegExTree t) {
		int n = t.parcours();
		entrees = new boolean[n];
		sorties = new boolean[n];
		transition = new int[n][66000];
		epsilon = new boolean[n][n];

		for (int i = 0; i < n; i++)
			for (int j = 0; j < 66000; j++)
				transition[i][j] = -1;

		etats = new HashMap<Set<Integer>, Set<Integer>[]>();
		sortie_directe = false;
		
	}
	
	
	public int[] remplir2(RegExTree t, int id) {
		int[] id_gauche;
		int[] id_droite;

		if (t.root == RegEx.CONCAT) {
			int[]res = new int[2];
			res[0]= id;
			id_gauche = remplir2(t.subTrees.get(0), id);
			id_droite = remplir2(t.subTrees.get(1), id_gauche[1] + 1);
			
			res[1]=id_droite[1];

			epsilon[id_gauche[1]][id_droite[1] - 1] = true;

			sorties[id_gauche[1]] = false;
			entrees[id_gauche[1] + 1] = false;
			entrees[id_droite[1] - 1] = false;
			return res;

		} else if (t.root == RegEx.DOT) {
			int[]res= new int[2];
			res[0]= id;
			
			entrees[id] = true;
			int incr = id+1;
			for (int i=0; i<66000; i++)
				transition[id][i] = incr;
			id++;
			sorties[id] = true;
			res[1]= id;
			return res;

		} else if (t.root == RegEx.ALTERN) {
			int[]res = new int[2];
			
			id_gauche = remplir2(t.subTrees.get(0), id);
			id_droite = remplir2(t.subTrees.get(1), id_gauche[1] + 1);
			res[0]= id_droite[1]+1;
			res[1]= id_droite[1]+2;

			epsilon[id_droite[1] + 1][id_gauche[0]] = true;
			epsilon[id_gauche[1]][id_droite[1] + 2] = true;

			epsilon[id_droite[1] + 1][id_droite[0]] = true;
			epsilon[id_droite[1]][id_droite[1] + 2] = true;

			sorties[id_gauche[1]] = false;
			sorties[id_droite[1]] = false;
			sorties[id_droite[1] + 2] = true;

			entrees[id] = false;
			entrees[id_gauche[1] + 1] = false;
			entrees[id_gauche[1] - 1] = false;
			entrees[id_droite[1] + 1] = true;

			return res;

		} else if (t.root == RegEx.ETOILE) {
			int debut = id;
			int fin = remplir2(t.subTrees.get(0), debut)[1];

			epsilon[fin][fin - 1] = true;
			epsilon[fin][fin + 2] = true;
			epsilon[fin + 1][fin - 1] = true;
			epsilon[fin + 1][fin + 2] = true;

			entrees[debut] = false;
			entrees[fin + 1] = true;
			entrees[fin - 1] = false;
			sorties[fin] = false;
			sorties[fin + 2] = true;

			int[]res= new int[2];
			res[0]= fin+1;
			res[1]= fin+2;
			return res;

		} else if (t.root == RegEx.PARENTHESEOUVRANT) {
			return remplir2(t.subTrees.get(0), id);

		} else if (t.root == RegEx.PARENTHESEFERMANT) {
			return remplir2(t.subTrees.get(0), id);

		} else {
			
			int[]res= new int[2];
			res[0]= id;
			
			
			entrees[id] = true;
			transition[id][t.root] = ++id;
			sorties[id] = true;
			
			res[1]= id;
			return res;
		}

	}
	
	
	public void setSortieDirecte() {
		for (int i=0; i<entrees.length; i++) {
			if(entrees[i]) {
				for (int j=0; j<sorties.length; j++) {
					if(sorties[j] && epsilon[i][j]) {
						sortie_directe = true;
						return;
					}
				}
			}
		}
	}

	

	public void afficher() {

		System.out.println("**********transitions***********");
		for (int i = 0; i < transition.length; i++) {
			for (int j = 0; j < transition[i].length; j++) {
				if (transition[i][j] != -1)
					System.out.println("i = " + i + "; j = " + (char) j + "; etat = " + transition[i][j]);
			}
		}

		System.out.println("***********epsilon**************");
		for (int i = 0; i < epsilon.length; i++) {
			for (int j = 0; j < epsilon[i].length; j++) {
				if (epsilon[i][j])
					System.out.println("etat " + i + " vers " + j);
			}
		}

		System.out.println("**********entrees***************");
		for (int i = 0; i < entrees.length; i++) {
			System.out.println(entrees[i]);
		}

		System.out.println("**********sorties***************");
		for (int i = 0; i < sorties.length; i++) {
			System.out.println(sorties[i]);
		}
	}

	public void determiniser() {
		int debut = 0;

		for (int i = 0; i < entrees.length; i++) {
			if (entrees[i])
				debut = i;
		}

		int nb_iterations = 0;
		ArrayList<Set<Integer>> new_keys = new ArrayList<>();

		do {

			Set<Integer> new_etat = new HashSet<>();

			if (nb_iterations == 0) {
				new_etat.add(debut);
				for (int i = 0; i < epsilon[debut].length; i++)
					if (epsilon[debut][i]) 
						new_etat.add(i);
			} else {
				new_etat = new_keys.get(0);
			}

			Set<Integer>[] transitions_determinisation = new HashSet[66000];

			for (Iterator<Integer> ite = new_etat.iterator(); ite.hasNext();) {
				int i = ite.next();
				for (int j = 0; j < transition[i].length; j++) {
					if (transition[i][j] != -1) {
						if (transitions_determinisation[j] == null)
							transitions_determinisation[j] = new HashSet<>();

						transitions_determinisation[j].add(transition[i][j]);
						transitions_determinisation[j].addAll(cherche_espilon(transition[i][j]));

						if (!etats.containsKey(transitions_determinisation[j])
								&& (!new_keys.contains(transitions_determinisation[j]))) {
							new_keys.add(transitions_determinisation[j]);
						}
					}

				}

			}

			etats.put(new_etat, transitions_determinisation);
			new_keys.remove(new_etat);

			nb_iterations++;

		} while (!new_keys.isEmpty());
	}

	public Set<Integer> cherche_espilon(int e) {
		Set<Integer> voisins_direct = new HashSet<>();
		Set<Integer> voisins_lointains = new HashSet<>();

		for (int i = 0; i < epsilon[e].length; i++) {
			if (epsilon[e][i])
				voisins_direct.add(i);
		}

		for (Iterator<Integer> it = voisins_direct.iterator(); it.hasNext();) {
			Integer a = it.next();
			voisins_lointains.addAll(cherche_espilon(a));
		}

		voisins_direct.addAll(voisins_lointains);

		return voisins_direct;
	}

	public void afficher_determinisation() {
		for (Set<Integer> keys : etats.keySet()) {
			for (Iterator<Integer> it = keys.iterator(); it.hasNext();) {
				System.out.print(it.next() + " ");
			}
			for (int i = 0; i < etats.get(keys).length; i++) {
				if (etats.get(keys)[i] != null) {
					System.out.print((char) i + ": ");
					for (Iterator<Integer> x = etats.get(keys)[i].iterator(); x.hasNext();) {
						System.out.print(x.next() + " ");
					}
					System.out.println();
				}

			}
		}
	}

	public boolean matching(String mot, int index, Set<Integer> key_etat) {
		
		for (Iterator<Integer> it = key_etat.iterator(); it.hasNext();) {
			int i = it.next();
			if (sorties[i]) {
				return true;
			}
		}
			
		if (index >= mot.length() || etats.get(key_etat)[mot.charAt(index)] == null)
			return false;
			
		if (etats.get(key_etat)[mot.charAt(index)] != null) {
			return matching(mot, index+1, etats.get(key_etat)[mot.charAt(index)]);
		}
		
		return false;
	}

	public HashMap<Integer, ArrayList<String>> custom_grep(String filename) throws FileNotFoundException, IOException {

		HashMap<Integer, ArrayList<String>> mots = new HashMap<Integer, ArrayList<String>>();
		String line = null;
		int line_number = 0;

		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			while ((line = br.readLine()) != null) {
			
				if (line.isEmpty()) {
					if(sortie_directe) {
						mots.put(line_number, new ArrayList<>());
						mots.get(line_number).add(line);
					}
					line_number++;
					continue;
				}

				for (int m = 0; m < line.length(); m++) {

					// on compare la lettre et la transition
					boolean result_matching = false;

					for (Set<Integer> keys : etats.keySet()) {
						if (!result_matching) {
							for (Iterator<Integer> it = keys.iterator(); it.hasNext();) {
								int etat = it.next();
								if (entrees[etat]) {
										if (matching(line.substring(m), 0, keys)) {
											result_matching = true;
											if (!mots.containsKey(line_number)) {
												mots.put(line_number, new ArrayList<>());
											}
											mots.get(line_number).add(line);
											break;
										}
								}

							}
						}
					}

				}
				line_number++;
				line = null;
			}
			
			if(sortie_directe && mots.get(line_number-1).get(0).isEmpty()) {
				mots.remove(line_number-1);
			}
		}
		return mots;

	}

	public void afficher_grep(HashMap<Integer, ArrayList<String>> results) {
		Map<Integer, ArrayList<String>> map = new TreeMap<>(results);
		for(Integer i : map.keySet()) {
			System.out.println("ligne "+ Integer.sum(i, 1) + " : " + map.get(i));
		}
	}

}
