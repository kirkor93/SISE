import CLIPSJNI.Environment;


public abstract class Bot
{
	public ActionBroker Broker;
	public String MySymbol;
	public Environment Clips;
	
	public abstract void Play();
}
