import net.sourceforge.jFuzzyLogic.FIS;
import CLIPSJNI.Environment;


public abstract class Bot
{
	public ActionBroker Broker;
	public String MySymbol;
	public Environment Clips;
	public FIS fis;
	public int turnCtr = -1;
	
	public abstract void Play();
	
	@Override
	public String toString() {
		String myState = "";
		myState += MySymbol;
		if(this.Broker.GetMyHP() <= 0 || this.Broker.GetMyPP() <= 0)
		{
			myState += " | IS DEAD";
		}
		else
		{
			myState += " | HP: " + this.Broker.GetMyHP();
			myState += " | PP: " + this.Broker.GetMyPP();
			myState += " | WP: " + this.Broker.GetMyWP();
			myState += " | POSITION: " + this.Broker.GetMyPosition();
		}
		myState += " | TL: " + String.valueOf(turnCtr);
		return myState;
	}
}
