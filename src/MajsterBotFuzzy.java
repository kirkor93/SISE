import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;

public class MajsterBotFuzzy extends Bot {
	
	public MajsterBotFuzzy()
	{
		this.MySymbol = "M";
		this.fis = FIS.load("src/MajsterFuzzy.fcl");
	}
	
	@Override
	public void Play() {
		// TODO Auto-generated method stub
		
		while(Broker.GetMyAP() > 0)
		{
			if(this.fis == null)
			{
				System.err.println("Can't load file");
				return;
			}
			
			fis.setVariable("service", 5);
			fis.setVariable("food", 3);
			
			fis.evaluate();
			
			System.out.println(fis.getVariable("tip").getLatestDefuzzifiedValue());
			
			Broker.Action(ActionType.MOVE, new Vector2(0,0));
		}
		
	}
}
