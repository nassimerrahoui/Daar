package TME3;

public class Match {

	/**
	 * A l'aide de la retenue, cherche la premiere occurence du facteur dans un texte
	 * @param facteur
	 * @param retenue
	 * @param texte
	 * @return
	 */
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

	
	/**
	 * Renvoie la retenue d'un facteur
	 */
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
}
