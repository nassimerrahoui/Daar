package TME3;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Launcher {

	public static void main(String[] args) {
		
		/** Cette classe permet de faire les tests sur la base Gutenberg **/ 
		
		Scanner sc = new Scanner(System.in);
		System.out.println(" >> Veuillez entrer le nom du fichier : ");
		System.out.println(" >> NB : Le fichier doit etre dans resources.");
		String filename = sc.next();
		sc.close();
		
		try {
			System.out.println("Debut indexing...");
			Indexing index = new Indexing();
			index.read("resources/" + filename);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Fin indexing...");
		
		/** TO DO
		Scanner s = new Scanner(System.in);
		System.out.println(" >> Veuillez entrer un mot : ");
		String mot = s.next();
		sc.close(); **/
	}

}
