package TME2;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Matching {

	public static int matchingAlgo(char[] facteur, int[] retenue, char[] texte) {
		int i = 0;
		int j = 0;

		while (i <= texte.length) {
			if (j == facteur.length) {
				return i - facteur.length;
			}

			if (i < texte.length && texte[i] == facteur[j]) {
				i++;
				j++;
			} else {
				if (retenue[j] == -1) {
					i++;
					j = 0;
				} else {
					j = retenue[j];
				}
			}
		}

		return -1;
	}

	public static int[] retenues(char[] facteur) {
		int[] result = new int[facteur.length + 1];

		if (facteur.length > 0)
			result[0] = -1;

		for (int i = 1; i < facteur.length; i++) {

			if (facteur[i] != facteur[i - 1]) {

				boolean exist = false;

				for (int j = 0; j < i; j++) {

					// memes lettres
					if (facteur[i] == facteur[j]) {
						exist = true;
						result[i] = result[j];
						break;
					}
				}
				if (!exist) {

					char premiere_lettre = facteur[i - 1];

					for (int j = i - 2; j >= 0; j--) {

						// repetition de pattern
						if (facteur[j] == premiere_lettre) {
							exist = true;
							result[i] = j + 1;
							break;
						}
					}

					// 1ere apparition
					if (!exist) {
						result[i] = 0;
					}
				}
				// memes lettres consecutives
			} else {
				result[i] = result[i - 1];
			}
		}

		return result;
	}
	
	public static ArrayList<Point> findPos(String filename, char[] facteur) throws IOException{
		
		String line = "";
		ArrayList<Point> pos = new ArrayList<Point>();
		int[] r = retenues(facteur);
		int line_number = 1;
		
		try (BufferedReader br = new BufferedReader(new FileReader("resources/"+filename))) {
			
			while ((line = br.readLine()) != null) {
				int indice = matchingAlgo(facteur, r, line.toCharArray());
				while(indice != -1) {
					pos.add(new Point(line_number, indice));
					if(indice + facteur.length < line.length()) {
						line = line.substring(indice + facteur.length);
						indice = matchingAlgo(facteur, r, line.toCharArray());
					}else {
						indice = -1;
					}
				}
				
				
				line_number++;
			}
		}
		return pos;
	}
	
	public static ArrayList<String> findLines(String filename, char[] facteur) throws IOException{
		
		String line = "";
		ArrayList<String> lignes = new ArrayList<String>();
		int[] r = retenues(facteur);
		
		try (BufferedReader br = new BufferedReader(new FileReader("resources/"+filename))) {
			
			while ((line = br.readLine()) != null) {
				if(matchingAlgo(facteur, r, line.toCharArray()) != -1) 
					lignes.add(line);
			}
		}
		return lignes;
	}

	public static void main(String[] args) throws IOException {
		
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Entrez un fichier : ");
		String filename = sc.nextLine();
		
		System.out.println("Entrez un mot : ");
		char[] facteur = sc.nextLine().toCharArray();
		
		sc.close();

		long startTime = System.currentTimeMillis();
		
		/*ArrayList<Point> pos = findPos(filename, facteur);
		
		for(Point p : pos) {
			System.out.println(p);
		}*/
		
		ArrayList<String> lines = findLines(filename, facteur);
		
		for(String l : lines)
			System.out.println(l);
		
		long stopTime = System.currentTimeMillis();
	    long elapsedTime = stopTime - startTime;
	    
	    System.out.println();
	    System.out.println("nb de résultats = " + lines.size());
	    System.out.println("Temps d'éxecution : " + elapsedTime + " ms");

		
	}
}
