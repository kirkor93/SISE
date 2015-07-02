import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;

public class StengerdtBotFuzzy extends Bot {
	
	public StengerdtBotFuzzy()
	{
		this.MySymbol = "S";
		this.fis = FIS.load("src/StengerdtFuzzy.fcl");
	}
	
	@Override
	public void Play() 
	{
		

	}

}
