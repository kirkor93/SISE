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
			this.Clips.assertString("(bot (x " + Broker.GetMyPosition().X + ") "
					+ "(y " + Broker.GetMyPosition().Y + ")"
					+ "(hitPoints " + Broker.GetMyHP() + ")" 
					+ "(actionPoints " + Broker.GetMyAP() + ")"
					+ "(woodPoints " + Broker.GetMyWP() + ")"
					+ "(psychicPoints " + Broker.GetMyPP() + "))");	//adding current bot state
			this.Clips.assertString("(tile (x " + Broker.GetMyPosition().X + ")" +
									"(y " + Broker.GetMyPosition().Y + ")" +
									"(type current)"
									+ "(fieldType " + Broker.GetFieldType(Broker.GetMyPosition().X, Broker.GetMyPosition().Y) + "))");	//adding current tile
			for(int i=1; i<8; ++i)	//adding neighbours
			{
				if(Broker.GetMyPosition().X + i <= 49)
				{
					this.Clips.assertString("(tile (x " + (Broker.GetMyPosition().X + i) + ")" +
							"(y " + Broker.GetMyPosition().Y + ")" +
							"(direction right)" +
							"(distance " + i + ")" +
							"(type neighbour)"
							+ "(fieldType " + Broker.GetFieldType(Broker.GetMyPosition().X + i, Broker.GetMyPosition().Y) + "))");
				}
				if(Broker.GetMyPosition().X - i >= 0)
				{
					this.Clips.assertString("(tile (x " + (Broker.GetMyPosition().X - i) + ")" +
							"(y " + Broker.GetMyPosition().Y + ")" +
							"(direction left)" +
							"(distance " + i + ")" +
							"(type neighbour)"
							+ "(fieldType " + Broker.GetFieldType(Broker.GetMyPosition().X - i, Broker.GetMyPosition().Y) + "))");
				}
				if(Broker.GetMyPosition().Y + i <= 49)
				{
					this.Clips.assertString("(tile (x " + Broker.GetMyPosition().X + ")" +
							"(y " + (Broker.GetMyPosition().Y + i) + ")" +
							"(direction down)" +
							"(distance " + i + ")" +
							"(type neighbour)"
							+ "(fieldType " + Broker.GetFieldType(Broker.GetMyPosition().X, Broker.GetMyPosition().Y + i) + "))");
				}
				if(Broker.GetMyPosition().Y - i >= 0)
				{
					this.Clips.assertString("(tile (x " + Broker.GetMyPosition().X + ")" +
							"(y " + (Broker.GetMyPosition().Y - i) + ")" +
							"(direction up)" +
							"(distance " + i + ")" +
							"(type neighbour)"
							+ "(fieldType " + Broker.GetFieldType(Broker.GetMyPosition().X, Broker.GetMyPosition().Y - i) + "))");	
				}
			}

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
//			System.out.println(currentDirection);
			
			if(Broker.GetMyHP() == 0)
			{
				return;
			}
			
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
			case "random":
				Randomize();
				break;
			case "wait":
				Broker.Action(ActionType.MOVE, new Vector2(0, 0));
				break;
			case "kindle":
				Broker.Action(ActionType.KINDLE_FIRE, null);
				break;
			case "attack":
				String evalStrX = "?*iks*";
				String evalStrY = "?*igrek*";
				//SymbolValue x = (SymbolValue) this.Clips.eval(evalStrX);
				int outputX = Integer.parseInt(this.Clips.eval(evalStrX).toString());
				//SymbolValue y = (SymbolValue) this.Clips.eval(evalStrY);
				int outputY = Integer.parseInt(this.Clips.eval(evalStrY).toString());
				Broker.Action(ActionType.THROW_SPEAR, new Vector2(outputX, outputY));
				break;
			default:
				break;
			}
			this.Clips.eval("(bind ?*dir*)");
			//this.Clips.run();
		}
	}
	
	public void Randomize()
	{
		int n = (int) (Math.random()*4);
		if(n == 0 && Broker.GetMyPosition().Y != 0)
			Broker.Action(ActionType.MOVE, new Vector2(0, -1));
		else if(n == 1 && Broker.GetMyPosition().Y != 49)
			Broker.Action(ActionType.MOVE, new Vector2(0, 1));
		else if(n == 2 && Broker.GetMyPosition().X != 0)
			Broker.Action(ActionType.MOVE, new Vector2(-1, 0));
		else if(n == 3 && Broker.GetMyPosition().X != 49)
			Broker.Action(ActionType.MOVE, new Vector2(1, 0));
		else
			Broker.Action(ActionType.MOVE, new Vector2(0, 0));
	}
}