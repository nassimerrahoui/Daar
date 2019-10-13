package project;

import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import TME1.Automate;
import TME1.RegEx;
import TME3.Indexing;
import TME3.RadixTree;

public class Launcher {

	/**
	 * Verifie s'il existe des operations de RegEx (hormis la concatenantion)
	 * et renvoie true si c'est le cas
	 * -> Utilser la methode adaptee : Automate ou KMP + RadixTree
	 * @param motif
	 * @return
	 */
	public static boolean isRegex(String motif) {
		char c_precedent = ' ';
		for (char c : motif.toCharArray()) {
			if (c == '.' || c == '*' || c == '|') {
				if (c_precedent != '\\')
					return true;
			}
			if (c == ' ')
				return true;
			c_precedent = c;
		}
		return false;
	}

	/**
	 * Lance la methode 1 : Automate
	 * @param motif
	 * @param filename
	 */
	public static void automateMethod(String motif, String filename) {
		try {
			long startTime = System.currentTimeMillis();
			RegEx.regEx = motif;
			RegEx.ret = RegEx.parse();

			Automate a = new Automate(RegEx.ret);
			a.remplir2(RegEx.ret, 0);
			a.setSortieDirecte();
			a.determiniser();
			HashMap<Integer, String> resultat = a.custom_grep("resources/" + filename);

			a.afficher_grep(resultat);

			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;

			System.out.println("nb de resultats = " + resultat.size());
			System.out.println("Temps d'execution : " + elapsedTime + " ms");

		} catch (Exception e) {
			System.err.println("  >> ERROR: syntax error for regEx \"" + RegEx.regEx + "\".");
		}
	}

	/**
	 * Lance la methode 2 : KMP + Radix Tree
	 * @param motif
	 * @param filename
	 * @param sc
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void radixTreeMethod(String motif, String filename, Scanner sc)
			throws FileNotFoundException, IOException {

		Indexing index = new Indexing();
		System.out.println("Indexing...");
		index.read(filename);

		while (true) {
			try {
				System.out.println(" >> Please enter a word to look for (STOPEND to stop) : ");

				if (motif == "")
					motif = sc.nextLine();

				if (motif.equals("STOPEND"))
					break;

				RadixTree t = new RadixTree("result/index_table-" + filename);

				long startTime = System.currentTimeMillis();

				ArrayList<Point> positions = t.searchMotif(motif);

				for (String line : index.getLines(positions))
					System.out.println(line);

				long stopTime = System.currentTimeMillis();
				long elapsedTime = stopTime - startTime;

				System.out.println("Temps d'execution : " + elapsedTime + " ms");

				motif = "";

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {

		/** Cette classe permet de faire les tests sur la base Gutenberg **/

		System.out.println("***** Bienvenue sur EGREP CUSTOM ******");
		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.println();
			System.out.println(" >> Veuillez entrer le nom du fichier (depose au prealable dans resources): ");
			String filename = sc.nextLine();

			if (filename.equals("STOPEND")) {
				sc.close();
				break;
			}

			System.out.println(" >> Veuillez entrer un motif : ");
			System.out.println(" >> (Si le motif est un mot vous accederai au radix tree sinon ce sera l'automate.)");
			String motif = sc.nextLine();

			if (isRegex(motif))
				automateMethod(motif, filename);
			else
				try {
					radixTreeMethod(motif, filename, sc);
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

}
