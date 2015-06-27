import net.sourceforge.jFuzzyLogic.FIS;
import CLIPSJNI.Environment;


public abstract class Bot
{
	public ActionBroker Broker;
	public String MySymbol;
	public Environment Clips;
	public FIS fis;
	
	public abstract void Play();
}
