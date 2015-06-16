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
		this.Clips.eval("(do-for-all-facts ((?f tile)) TRUE (retract ?f))"); //refreshing tiles (deleting old neighbours)
		this.Clips.eval("(do-for-fact ((?b bot)) (eq ?b:direction nil) (retract ?b))"); //refreshing bot state
		this.Clips.assertString("(bot (hitPoints " + Broker.GetMyHP() + ")" +
								"(state current)"
								+ "(currentField " + Broker.GetFieldType(Broker.GetMyPosition().X, Broker.GetMyPosition().Y) + "))");	//adding current bot state
		this.Clips.assertString("(tile (x " + Broker.GetMyPosition().X + ")" +
								"(y " + Broker.GetMyPosition().Y + ")" +
								"(type current)"
								+ "(fieldType " + Broker.GetFieldType(Broker.GetMyPosition().X, Broker.GetMyPosition().Y) + "))");	//adding current tile
		for(int i=1; i<6; ++i)	//adding neighbours
		{
			if(Broker.GetMyPosition().X + i <= 49)
			{
				this.Clips.assertString("(tile (x " + (Broker.GetMyPosition().X + i) + ")" +
						"(y " + Broker.GetMyPosition().Y + ")" +
						"(type neighbour)"
						+ "(fieldType " + Broker.GetFieldType(Broker.GetMyPosition().X + i, Broker.GetMyPosition().Y) + "))");
			}
			if(Broker.GetMyPosition().X - i >= 0)
			{
				this.Clips.assertString("(tile (x " + (Broker.GetMyPosition().X - i) + ")" +
						"(y " + Broker.GetMyPosition().Y + ")" +
						"(type neighbour)"
						+ "(fieldType " + Broker.GetFieldType(Broker.GetMyPosition().X - i, Broker.GetMyPosition().Y) + "))");
			}
			if(Broker.GetMyPosition().Y + i <= 49)
			{
				this.Clips.assertString("(tile (x " + Broker.GetMyPosition().X + ")" +
						"(y " + (Broker.GetMyPosition().Y + i) + ")" +
						"(type neighbour)"
						+ "(fieldType " + Broker.GetFieldType(Broker.GetMyPosition().X, Broker.GetMyPosition().Y + i) + "))");
			}
			if(Broker.GetMyPosition().Y - i >= 0)
			{
				this.Clips.assertString("(tile (x " + Broker.GetMyPosition().X + ")" +
						"(y " + (Broker.GetMyPosition().Y - i) + ")" +
						"(type neighbour)"
						+ "(fieldType " + Broker.GetFieldType(Broker.GetMyPosition().X, Broker.GetMyPosition().Y - i) + "))");	
			}
		}
		
		System.out.println("Majster");

		//String evalStr = "(find-all-facts ((?f bot)) TRUE)";
		String evalStr = "(find-all-facts ((?f bot)) (and (= ?f:hitPoints " + (Broker.GetMyHP()) + ")" +
				"(eq ?f:state current) " + "(not (eq ?f:currentField normal))))";
		
		MultifieldValue pv = (MultifieldValue) this.Clips.eval(evalStr);
		int tNum = pv.listValue().size();
		FactAddressValue fv = (FactAddressValue) pv.listValue().get(0);
		String currentDirection = fv.getFactSlot("direction").toString();
		String currentFieldType = fv.getFactSlot("currentField").toString();
		
		System.out.println("Current HP: " + Broker.GetMyHP());
		if(Broker.GetMyHP() == 0)
		{
			System.out.println("Majster is dead");
			return;
		}
		
		System.out.println("Position: X - " + Broker.GetMyPosition().X + " Y - " + Broker.GetMyPosition().Y);
		System.out.println("Current Field Type: " + currentFieldType);
		
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
		
		this.Clips.run();
	}
}