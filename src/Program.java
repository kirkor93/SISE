import CLIPSJNI.*;
import net.sourceforge.jFuzzyLogic.*;

public class Program {

	public static void main(String[] args) {
		System.out.println("Hello");
		System.out.println("is it me you're looking for?");	
		
		Map m = new Map(MapBMP.fight);
		if(!m.LoadMap())
		{
			System.out.println("SLAKDL:");
		}
		else
		{
			System.out.println("Worked");
			System.out.println(m);
		}
	}

}
