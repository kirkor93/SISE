import CLIPSJNI.*;
import net.sourceforge.jFuzzyLogic.*;
import java.util.Scanner;

public class Program {

	public static void main(String[] args) 
	{
		Scanner scan = new Scanner(System.in);
		int[] names = new int[4];		
		System.out.println("1 - Majster's bot \n2 - Stengerdt's bot \n3 - Marcin's bot \n4 - Szczech's bot \n5 - Janiak's bot \n6 - Patryk's bot"
				+ "\n7 - Majster's fuzzy bot"
				+ "\n8 - Maricn's fuzzy bot"
				+ "\n9 - Stengerdt's fuzzy bot"
				+ "\n10 - Patryk's fuzzy bot"
				+ "\n11 - Janiak's fuzzy bot"
				+ "\n12 - Szczech's fuzzy bot ");	

		System.out.println("Pick bot");	
		names[0] = scan.nextInt();
		System.out.println("Pick bot");	
		names[1] = scan.nextInt();
		System.out.println("Pick bot");	
		names[2] = scan.nextInt();
		System.out.println("Pick bot");	
		names[3] = scan.nextInt();
		
		scan.close();
		
		GameController controller = new GameController(names);	
		ActionBroker abroker = new ActionBroker(controller);
		controller.SetActionBrokerOnBots(abroker);
		controller.Run();
		
	}

}
