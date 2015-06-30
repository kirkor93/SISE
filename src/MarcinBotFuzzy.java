import java.util.HashMap;
import java.util.Random;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Variable;


public class MarcinBotFuzzy extends Bot {

	private static final String MAIN_BLOCK_NAME = "action";
	private static final int FIELD_OF_VIEW = 6;
	private static final int DATA_SLOTS = 9;
	private static final int MAX_BOT_TIME = 5;
	private static final int WOOD_TO_LAY_TRAP = 8;
	private static final int WILL_BE_CANNIBAL_BELOW_HP = 6;
	
	private int tl = -1;
	private Random randomGenerator = new Random();
	
	private FunctionBlock fb;
	
	public MarcinBotFuzzy() 
	{
		this.MySymbol = "Q";
		this.fis = FIS.load("src/MarcinFuzzy.fcl");
		this.fb = fis.getFunctionBlock(MAIN_BLOCK_NAME);
		
		Initialize();
	}
	
	private void Initialize()
	{

	}
	
	@Override
	public void Play() 
	{
		if(Broker.GetMyHP() <= 0)
		{
			System.out.println("Marcin | " + 
					"POS=" + Broker.GetMyPosition().toString() + " | " +
					"TL=" + String.valueOf(tl) + " | " +
					"DEAD"
					);
			return;
		}
		
		/*
		while(Broker.GetMyAP() > 0)
		{
			// action!
		}
		*/
		
		// cleanup
		
		// send data to FIS
		fis.setVariable("botHP", Broker.GetMyHP());
		fis.setVariable("botAP", Broker.GetMyAP());
		fis.setVariable("botWP", Broker.GetMyWP());
		fis.setVariable("botPP", Broker.GetMyPP());
		
		for(int i = 1; i < FIELD_OF_VIEW; ++i)
		{
			
		}
		
		// action!
		fis.evaluate();
		
		// get data from FIS
		
		// make action according to the data
		
		System.out.println("MarcinFuzzy | " + 
				"POS=" + Broker.GetMyPosition().toString() + " | " +
//				"TL=" + String.valueOf(tl) + " | " +
//				"CFLD=" + cFldStr + " | " +
				"HP=" + String.valueOf(Broker.GetMyHP()) + " | " +
				"PP=" + String.valueOf(Broker.GetMyPP()) + " | " +
				"AP=" + String.valueOf(Broker.GetMyAP()) + " | " +
				"WP=" + String.valueOf(Broker.GetMyWP()) + " | "
//				"ACTS=" + String.valueOf(tmpAct)
				);
	}

}
