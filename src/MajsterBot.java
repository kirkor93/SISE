import CLIPSJNI.Environment;
import CLIPSJNI.FactAddressValue;
import CLIPSJNI.MultifieldValue;
import CLIPSJNI.SymbolValue;


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
		while(Broker.GetMyAP() > 0)
		{
			this.Clips.eval("(do-for-all-facts ((?f tile)) TRUE (retract ?f))"); //refreshing tiles (deleting old neighbours)
			this.Clips.eval("(do-for-fact ((?b bot)) (retract ?b))"); //refreshing bot state
			//this.Clips.eval("(do-for-fact ((?d dir)) (retract ?d))");
			this.Clips.assertString("(bot (hitPoints " + Broker.GetMyHP() + ")" + "(actionPoints " + Broker.GetMyAP() + "))");	//adding current bot state
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
							"(direction right)" +
							"(type neighbour)"
							+ "(fieldType " + Broker.GetFieldType(Broker.GetMyPosition().X + i, Broker.GetMyPosition().Y) + "))");
				}
				if(Broker.GetMyPosition().X - i >= 0)
				{
					this.Clips.assertString("(tile (x " + (Broker.GetMyPosition().X - i) + ")" +
							"(y " + Broker.GetMyPosition().Y + ")" +
							"(direction left)" +
							"(type neighbour)"
							+ "(fieldType " + Broker.GetFieldType(Broker.GetMyPosition().X - i, Broker.GetMyPosition().Y) + "))");
				}
				if(Broker.GetMyPosition().Y + i <= 49)
				{
					this.Clips.assertString("(tile (x " + Broker.GetMyPosition().X + ")" +
							"(y " + (Broker.GetMyPosition().Y + i) + ")" +
							"(direction down)" +
							"(type neighbour)"
							+ "(fieldType " + Broker.GetFieldType(Broker.GetMyPosition().X, Broker.GetMyPosition().Y + i) + "))");
				}
				if(Broker.GetMyPosition().Y - i >= 0)
				{
					this.Clips.assertString("(tile (x " + Broker.GetMyPosition().X + ")" +
							"(y " + (Broker.GetMyPosition().Y - i) + ")" +
							"(direction up)" +
							"(type neighbour)"
							+ "(fieldType " + Broker.GetFieldType(Broker.GetMyPosition().X, Broker.GetMyPosition().Y - i) + "))");	
				}
			}

			System.out.println("Majster");
			this.Clips.run();

			String evalStr = "?*dir*";
			//String evalStr = "(find-all-facts ((?f dir)) TRUE)";
			
			SymbolValue sv = (SymbolValue) this.Clips.eval(evalStr);
			String currentDirection = sv.stringValue();
			
			//MultifieldValue pv = (MultifieldValue) this.Clips.eval(evalStr);
			//int tNum = pv.listValue().size();
			//FactAddressValue fv = (FactAddressValue) pv.listValue().get(tNum-1);
			//String currentDirection = fv.getFactSlot("value").toString();
			
			//this.Clips.eval("(facts)");
			System.out.println(currentDirection);
			
			System.out.println("Current HP: " + Broker.GetMyHP());
			if(Broker.GetMyHP() == 0)
			{
				System.out.println("Majster is dead");
				return;
			}
			System.out.println("Current AP: " + Broker.GetMyAP());
			System.out.println("Position: X - " + Broker.GetMyPosition().X + " Y - " + Broker.GetMyPosition().Y);
			
			switch(currentDirection)
			{
			case "up": 
				Broker.Action(ActionType.MOVE, new Vector2(0, -1));
				break;
			case "down":
				Broker.Action(ActionType.MOVE, new Vector2(0, 1));
				break;
			case "left":
				Broker.Action(ActionType.MOVE, new Vector2(-1, 0));
				break;
			case "right":
				Broker.Action(ActionType.MOVE, new Vector2(1, 0));
				break;
			default:
				Broker.Action(ActionType.MOVE, new Vector2(0, 0));
				break;
			}
			
			this.Clips.eval("(bind ?*dir*)");
			//this.Clips.run();
		}
	}
}