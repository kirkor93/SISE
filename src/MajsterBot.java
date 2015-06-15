import CLIPSJNI.Environment;
import CLIPSJNI.FactAddressValue;
import CLIPSJNI.MultifieldValue;


public class MajsterBot extends Bot
{
	public MajsterBot()
	{
		this.MySymbol = "M";
		this.Clips = new Environment();
		this.Clips.load("src/MajsterClips.clp");
		this.Clips.reset();
		this.Clips.run();
		
	}
	
	@Override
	public void Play() {
		// TODO Auto-generated method stub
		this.Clips.assertString("(bot (hitPoints " + Broker.GetMyHP() + ")" +
								"(state current))");
		System.out.println("Majster");

		String evalStr = "(find-all-facts ((?f bot)) (not (eq ?f:direction nil)))";
		
		MultifieldValue pv = (MultifieldValue) this.Clips.eval(evalStr);
		int tNum = pv.listValue().size();
		FactAddressValue fv = (FactAddressValue) pv.listValue().get(tNum - 1);
		String currentHP = fv.getFactSlot("hitPoints").toString();
		String currentDirection = fv.getFactSlot("direction").toString();
		
		System.out.println("Current HP: " + Broker.GetMyHP());
		if(Broker.GetMyHP() == 0)
		{
			System.out.println("Majster is dead");
			return;
		}
		
		System.out.println("Current direction: " + currentDirection);
		System.out.println("Position: " + Broker.GetMyPosition().Y);
		
		switch(currentDirection)
		{
		case "up": 
			Broker.Action(ActionType.MOVE, new Vector2(0, -1));
			break;
		case "down":
			Broker.Action(ActionType.MOVE, new Vector2(0, 1));
			break;
		default:
			Broker.Action(ActionType.MOVE, new Vector2(0, -1));
			break;
		}
		
		//this.Clips.reset();
		this.Clips.run();
	}
}