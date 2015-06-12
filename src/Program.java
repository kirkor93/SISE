import CLIPSJNI.*;
import net.sourceforge.jFuzzyLogic.*;
import java.util.Scanner;

public class Program {

	public static void main(String[] args) 
	{
		Scanner scan = new Scanner(System.in);
		int[] names = new int[4];		
		System.out.println("1 - Majster's bot \n2 - Stengerdt's bot \n3 - Marcin's bot \n4 - Szoch's bot \n5 - Janiak's bot \n5 - Patryk's bot");	

		System.out.println("Pick bot");	
		names[0] = scan.nextInt();
		System.out.println("Pick bot");	
		names[1] = scan.nextInt();
		System.out.println("Pick bot");	
		names[2] = scan.nextInt();
		System.out.println("Pick bot");	
		names[3] = scan.nextInt();
		
		GameController controller = new GameController(names);
	}

}
