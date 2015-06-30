import net.sourceforge.jFuzzyLogic.FIS;


public class MarcinBotFuzzy extends Bot {

	public MarcinBotFuzzy() {
		this.MySymbol = "Q";
		this.fis = FIS.load("src/MarcinFuzzy.fcl");
	}
	
	@Override
	public void Play() {
		/*
		while(Broker.GetMyAP() > 0)
		{
			// action!
		}
		*/
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
