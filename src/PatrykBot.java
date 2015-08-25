import CLIPSJNI.Environment;
import CLIPSJNI.SymbolValue;


public class PatrykBot extends Bot
{
	private int _loopCnt = 0;
	private final int _C_fov = 4;
	
	public PatrykBot()
	{
		this.MySymbol = "K";
		this.Clips = new Environment();
		this.Clips.clear();
		this.Clips.load("src/PatrykClips.clp");
		this.Clips.reset();
		//this.Clips.run();
	}
	
	@Override
	public void Play() {
		_loopCnt = 0;
		while(Broker.GetMyAP() > 0)
		{
			//System.out.println("Przygotuj Clipsa");
			this.Clips.eval("(do-for-all-facts ((?f tileBase)) TRUE (retract ?f))"); //refreshing tiles (deleting old neighbours)
			this.Clips.eval("(do-for-all-facts ((?tF tileFood)) TRUE (retract ?tF))");
			this.Clips.eval("(do-for-all-facts ((?tF tileCorpse)) TRUE (retract ?tF))");
			this.Clips.eval("(do-for-all-facts ((?tF tileWood)) TRUE (retract ?tF))");
			this.Clips.eval("(do-for-all-facts ((?tF tileEnemy)) TRUE (retract ?tF))");
			this.Clips.eval("(do-for-all-facts ((?b bot)) TRUE (retract ?b))"); //refreshing bot state
			this.Clips.eval("(do-for-fact ((?aC actionHandler)) (retract ?aC))");
//			this.Clips.eval("(do-for-fact ((?b NeedHPMult)) (retract ?b))");
//			this.Clips.eval("(do-for-fact ((?b NeedPPMult)) (retract ?b))");
//			this.Clips.eval("(do-for-fact ((?b NeedWPMult)) (retract ?b))");
			Vector2 myPos = Broker.GetMyPosition();
			
			
			for(int i = -_C_fov; i <= _C_fov; ++i)
			{
				for(int j = -_C_fov; j <= _C_fov; ++j)
				{
					Vector2 currentPos = new Vector2(i, j);
					currentPos.Add(myPos);
					if(currentPos != myPos)
					{
						if(currentPos.X >= 0 &&
							currentPos.X < 50 &&
							currentPos.Y >= 0 &&
							currentPos.Y < 50)
						{
	//						String type;
	//						if((Math.abs(myPos.X - currentPos.X) == 1 && Math.abs(myPos.Y - currentPos.Y) == 0) || 
	//								(Math.abs(myPos.Y - currentPos.Y) == 1 && Math.abs(myPos.X - currentPos.X) == 0))
	//						{
	//							type = "neighbour";
	//						}
	//						else if(myPos.X == currentPos.X && myPos.Y == currentPos.Y )
	//						{
	//							type = "current";
	//						}
	//						else
	//						{
	//							type = "other";
	//						}					
							Clips.assertString("(tileBase"
									+ "(fieldX " + currentPos.X + ")"
									+ "(fieldY " + currentPos.Y + ")"
									//+ "(type " + type + ")"
									+ "(fieldType " + Broker.GetFieldType(currentPos.X, currentPos.Y) + ")"
									+ ")");
						}
					}
				}
			}
			
			Clips.assertString("(bot"
					+ "(HP " + Broker.GetMyHP() + ")" 
					+ "(PP " + Broker.GetMyPP() + ")" 
					+ "(AP " + Broker.GetMyAP() + ")" 
					+ "(WP " + Broker.GetMyWP() + ")"
					+ "(botX " + String.valueOf(myPos.X) + ")"
					+ "(botY " + String.valueOf(myPos.Y) + ")"
					//+ "(currentField " + Broker.GetFieldType(myPos.X, myPos.Y) + ")"
					+ ")");
//			this.Clips.assertString("(tile (x " + Broker.GetMyPosition().X + ")" +
//									"(y " + Broker.GetMyPosition().Y + ")" +
//									"(type current)"
//									+ "(fieldType " + Broker.GetFieldType(Broker.GetMyPosition().X, Broker.GetMyPosition().Y) + "))");	//adding current tile
//			
			Clips.assertString("(actionHandler"
					+ "(action WAIT) "
					+ "(distanceCorpse 1000) "
					+ "(distanceFood 1000) "
					+ "(distanceWood 1000) "
					+ "(distanceEnemy 1000) "
					+ "(foodCount 0) "
					+ "(corpseCount 0) "
					+ "(woodCount 0) "
					+ "(enemiesCount 0) "
					+ ")");

			
			
			//System.out.println("Odpal Clipsa");
			
			if(_loopCnt == 0 )this.Clips.run();

			//System.out.println("Wyci¹gnij Clipsa");
			
			String evalStr = "?*action*";
			
			SymbolValue sv = (SymbolValue) this.Clips.eval(evalStr);
			String action = sv.stringValue();
			
			String evalStrX = "?*newX*";
			String evalStrY = "?*newY*";
			//SymbolValue x = (SymbolValue) this.Clips.eval(evalStrX);
			int X = Integer.parseInt(this.Clips.eval(evalStrX).toString());
			//SymbolValue y = (SymbolValue) this.Clips.eval(evalStrY);
			int Y = Integer.parseInt(this.Clips.eval(evalStrY).toString());
			
			
			if(Broker.GetMyHP() == 0)
			{
				return;
			}
			
			/* KINDLE
			 * MOVE
			 * WAIT
			 * THROW
			 * MOVERAND
			 */
			
			System.out.println(action);
//			System.out.println(new Vector2(X, Y));
//			System.out.println(_loopCnt);
			if(_loopCnt == 1) 
			{
				action = "MOVERAND";
			}
			else if(_loopCnt == 2) 
			{	
				action = "WAIT";
				_loopCnt = 0;
			}
			
			
			switch(action)
			{
			case "MOVE":
				if(X!=Y)Broker.Action(ActionType.MOVE, new Vector2(X, Y));
				else Broker.Action(ActionType.MOVE, new Vector2(X, 0));
				break;
			case "MOVERAND":
				Randomize();
				break;
			case "WAIT":
				Broker.Action(ActionType.MOVE, new Vector2(0, 0));
				break;
			case "KINDLE":
				Broker.Action(ActionType.KINDLE_FIRE, null);
				break;
			case "THROW":
				Broker.Action(ActionType.THROW_SPEAR, new Vector2(X, Y));
				break;
			default:
				Broker.Action(ActionType.MOVE, new Vector2(0, 0));
				break;
			}	
			++_loopCnt;
		}	
	}
	
	public void Randomize()
	{
		while(true)
		{
		int n = (int) (Math.random()*4);
		if(n == 0 && Broker.GetMyPosition().Y != 0)
		{
			Broker.Action(ActionType.MOVE, new Vector2(0, -1));
			return;
			}
		else if(n == 1 && Broker.GetMyPosition().Y != 49)
		{
			Broker.Action(ActionType.MOVE, new Vector2(0, 1));
			return;
			}
		else if(n == 2 && Broker.GetMyPosition().X != 0)
		{
			Broker.Action(ActionType.MOVE, new Vector2(-1, 0));
			return;
			}
		else if(n == 3 && Broker.GetMyPosition().X != 49)
		{
			Broker.Action(ActionType.MOVE, new Vector2(1, 0));
			return;
			}
		}
	}
}