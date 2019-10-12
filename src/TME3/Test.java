package TME3;

import java.io.IOException;

public class Test {
	public static void main(String[] args) {
		
		try {
			RadixTree t = new RadixTree("result/index_table-babylonia.txt");
			//t.affichage();
			System.out.println(t.searchMotif("Sargon"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
		
}
