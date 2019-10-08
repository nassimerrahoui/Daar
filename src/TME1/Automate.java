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

	HashMap<Set<Integer>, Set<Integer>[]> etats;

	public Automate(RegExTree t) {
		int n = t.parcours();
		System.out.println("NB DE NOEUDS " + n);
		entrees = new boolean[n];
		sorties = new boolean[n];
		transition = new int[n][256];
		epsilon = new boolean[n][n];

		for (int i = 0; i < n; i++)
			for (int j = 0; j < 256; j++)
				transition[i][j] = -1;

		etats = new HashMap<Set<Integer>, Set<Integer>[]>();
		
	}

	public int remplir(RegExTree t, int id) {
		int id_gauche;
		int id_droite;

		if (t.root == RegEx.CONCAT) {
			id_gauche = remplir(t.subTrees.get(0), id);
			id_droite = remplir(t.subTrees.get(1), id_gauche + 1);

			epsilon[id_gauche][id_droite - 1] = true;

			sorties[id_gauche] = false;
			entrees[id_gauche + 1] = false;
			entrees[id_droite - 1] = false;

			return id_droite;

		} else if (t.root == RegEx.DOT) {
			id_gauche = remplir(t.subTrees.get(0), id);
			id_droite = remplir(t.subTrees.get(1), id_gauche + 1);

			epsilon[id_gauche][id_droite - 1] = true;

			sorties[id_gauche] = false;
			entrees[id_gauche + 1] = false;
			entrees[id_droite - 1] = false;

			return id_droite;

		} else if (t.root == RegEx.ALTERN) {
			id_gauche = remplir(t.subTrees.get(0), id);
			id_droite = remplir(t.subTrees.get(1), id_gauche + 1);

			epsilon[id_droite + 1][id_gauche - 1] = true;
			epsilon[id_gauche][id_droite + 2] = true;

			epsilon[id_droite + 1][id_gauche + 1] = true;
			epsilon[id_droite][id_droite + 2] = true;

			sorties[id_gauche] = false;
			sorties[id_droite] = false;
			sorties[id_droite + 2] = true;

			entrees[id] = false;
			entrees[id_gauche + 1] = false;
			entrees[id_gauche - 1] = false;
			entrees[id_droite + 1] = true;

			return id_droite + 2;

		} else if (t.root == RegEx.ETOILE) {
			int debut = id;
			int fin = remplir(t.subTrees.get(0), debut);

			epsilon[fin][fin - 1] = true;
			epsilon[fin][fin + 2] = true;
			epsilon[fin + 1][fin - 1] = true;
			epsilon[fin + 1][fin + 2] = true;

			entrees[debut] = false;
			entrees[fin + 1] = true;
			entrees[fin - 1] = false;
			sorties[fin] = false;
			sorties[fin + 2] = true;

			return fin + 2;

		} else if (t.root == RegEx.PARENTHESEOUVRANT) {
			return remplir(t.subTrees.get(0), id);

		} else if (t.root == RegEx.PARENTHESEFERMANT) {
			return remplir(t.subTrees.get(0), id);

		} else {
			entrees[id] = true;
			transition[id][t.root] = ++id;
			sorties[id] = true;
			return id;
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
					if (epsilon[debut][i]) {
						new_etat.add(i);
						System.out.println(i);
					}
			} else {
				new_etat = new_keys.get(0);
			}

			Set<Integer>[] transitions_determinisation = new HashSet[256];

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
			System.out.println("---------");
		}
	}

	public boolean matching(String mot, int index, Set<Integer> key_etat) {
		System.out.println("matching 1");
		for (Iterator<Integer> it = key_etat.iterator(); it.hasNext();) {
			int i = it.next();
			if (sorties[i])
				return true;
		}
		
		System.out.println("matching 4");
		System.out.println(key_etat);
		System.out.println(index);
		System.out.println(mot);
		System.out.println(mot.length());
		System.out.println(mot.charAt(2));
		if (index >= mot.length() || etats.get(key_etat)[mot.charAt(index)] == null)
			return false;
		
		System.out.println("matching 6");
		
		if (etats.get(key_etat)[mot.charAt(index)] != null) {
			return matching(mot, index+1, etats.get(key_etat)[mot.charAt(index)]);
		}
		
		System.out.println("matching 7");
		return false;
	}

	public HashMap<Integer, ArrayList<String>> custom_grep(String filename) throws FileNotFoundException, IOException {

		HashMap<Integer, ArrayList<String>> mots = new HashMap<Integer, ArrayList<String>>();
		String line = null;
		int line_number = 0;

		// System.out.println("BEFORE READFILE");
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			// System.out.println("FILE FOUND");
			while ((line = br.readLine()) != null) {

				if (line.isEmpty()) {
					line_number++;
					continue;
				}

				String word_line[] = line.trim().split("\\s+");
				// System.out.println("SPLIT");
				// on parcours les mots de la ligne

				for (int m = 0; m < word_line.length; m++) {

					//System.out.println(word_line[m]);
					// System.out.println("size : " + etats.size());

					// on compare la lettre et la transition
					boolean result_matching = false;
					System.out.println(word_line[m]);
					for (Set<Integer> keys : etats.keySet()) {
						if (!result_matching) {
							for (Iterator<Integer> it = keys.iterator(); it.hasNext();) {
								int etat = it.next();
								System.out.println("ok1");
								if (entrees[etat]) {
									System.out.println("ok2");
									if (matching(word_line[m], 0, keys)) {
										result_matching = true;
										System.out.println("ok3");
										if (!mots.containsKey(line_number)) {
											System.out.println("ok4");
											mots.put(line_number, new ArrayList<>());
										}
										System.out.println("ok5");
										//mots.get(line_number).add(word_line[m]);
										mots.get(line_number).add(line);
										break;
									}
									System.out.println("ok6");
								}

							}
						}
					}
					System.out.println(word_line[m] + " ok");

				}
				line_number++;
				line = null;
			}
			// System.out.println("END READ LINE");
		}
		return mots;

	}

	public void afficher_grep(HashMap<Integer, ArrayList<String>> results) {
		Map<Integer, ArrayList<String>> map = new TreeMap<>(results);
		for(Integer i : map.keySet()) {
			System.out.println("ligne "+ Integer.sum(i, 1) + " : " + map.get(i));
		}
//		results.forEach((key, value) -> System.out.println("ligne " + key + " : " + value));
		System.out.println("Size : " + results.size());
	}

}
