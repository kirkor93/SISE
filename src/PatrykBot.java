import CLIPSJNI.Environment;
import CLIPSJNI.SymbolValue;


public class PatrykBot extends Bot
{
	private int _loopCnt = 0;
	private Vector2 _previousMove = new Vector2();
	private int _previousDecision;
	private final int _C_fov = 20;
	
	public PatrykBot()
	{
		this.MySymbol = "K";
		this.Clips = new Environment();
		this.Clips.clear();
		this.Clips.load("src/PatrykClips.clp");
		this.Clips.reset();
	}
	
	@Override
	public void Play() {
		_loopCnt = 0;
		while(Broker.GetMyAP() > 0)
		{
			this.Clips.reset();
			
			//System.out.println("Przygotuj Clipsa");
			this.Clips.eval("(do-for-all-facts ((?f tileBase)) TRUE (retract ?f))"); //refreshing tiles (deleting old neighbours)
			this.Clips.eval("(do-for-all-facts ((?tF tileFood)) TRUE (retract ?tF))");
			this.Clips.eval("(do-for-all-facts ((?tF tileCorpse)) TRUE (retract ?tF))");
			this.Clips.eval("(do-for-all-facts ((?tF tileWood)) TRUE (retract ?tF))");
			this.Clips.eval("(do-for-all-facts ((?tF tileEnemy)) TRUE (retract ?tF))");
			this.Clips.eval("(do-for-all-facts ((?b bot)) TRUE (retract ?b))"); //refreshing bot state
			this.Clips.eval("(do-for-fact ((?aC actionHandler)) (retract ?aC))");
			
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

			
			this.Clips.run();

			
			String evalStr = "?*action*";
			
			SymbolValue sv = (SymbolValue) 
					
			this.Clips.eval(evalStr);
					
			String action = sv.stringValue();
			
			String evalStrX = "?*newX*";
			String evalStrY = "?*newY*";
			//SymbolValue x = (SymbolValue) this.Clips.eval(evalStrX);
			int X = Integer.parseInt(this.Clips.eval(evalStrX).toString());
			//SymbolValue y = (SymbolValue) this.Clips.eval(evalStrY);
			int Y = Integer.parseInt(this.Clips.eval(evalStrY).toString());
			
			System.out.println(action);
			System.out.println("AP " + this.Broker.GetMyAP());
			System.out.println(new Vector2(X, Y));
			System.out.println(_loopCnt);
			
			if(X+myPos.X > 49 || X+myPos.X < 0)
			{
				action = "MOVERAND";
			}
			if(Y+myPos.Y > 49 || Y+myPos.Y < 0)
			{
				action = "MOVERAND";
			}


			
			if(Broker.GetMyHP() == 0)
			{
				return;
			}
			
			if(_loopCnt == 1) 
			{	
				System.out.println("dupa");
				action = "MOVERAND";
				_loopCnt = 0;
			}
			
			
			switch(action)
			{
			case "MOVE":
				if(X!=Y)
				{
					Broker.Action(ActionType.MOVE, new Vector2(X, Y));
					_previousMove.X = X;
					_previousMove.Y = Y;
					_loopCnt = 0;
				}
				else 
				{
					Broker.Action(ActionType.MOVE, new Vector2(X, 0));
					_previousMove.X = X;
					_previousMove.Y = 0;
					_loopCnt = 0;
				}
				break;
			case "MOVERAND":
				RandomMove();
				_loopCnt = 0;
				break;
			case "WAIT":
				Broker.Action(ActionType.MOVE, new Vector2(0, 0));
				_loopCnt = 0;
				break;
			case "KINDLE":
				Broker.Action(ActionType.KINDLE_FIRE, null);
				_loopCnt = 0;
				break;
			case "THROW":
				Broker.Action(ActionType.THROW_SPEAR, new Vector2(X, Y));
				_loopCnt = 0;
				break;
			default:
				Broker.Action(ActionType.MOVE, new Vector2(0, 0));
				_loopCnt = 0;
				break;
			}	
			//_loopCnt+=1;
		}	
	}
	
	public void RandomMove()
	{
		while(true)
		{
			int n = (int) (Math.random()*4);
			if(n == 0 && Broker.GetMyPosition().Y != 0 && _previousDecision != 1 && _previousMove != new Vector2(0,1))
			{
				_previousDecision = n;
				_previousMove.X = 0;
				_previousMove.Y = -1;
				Broker.Action(ActionType.MOVE, _previousMove);
				return;
			}
			else if(n == 1 && Broker.GetMyPosition().Y != 49 && _previousDecision != 0  && _previousMove != new Vector2(0,-1))
			{
				_previousDecision = n;
				_previousMove.X = 0;
				_previousMove.Y = 1;
				Broker.Action(ActionType.MOVE, _previousMove);
				return;
			}
			else if(n == 2 && Broker.GetMyPosition().X != 0 && _previousDecision != 3 && _previousMove != new Vector2(1,0))
			{
				_previousDecision = n;
				_previousMove.X = -1;
				_previousMove.Y = 0;
				Broker.Action(ActionType.MOVE, _previousMove);
				return;
			}
			else if(n == 3 && Broker.GetMyPosition().X != 49 && _previousDecision != 2 && _previousMove != new Vector2(-1,0))
			{
				_previousDecision = n;
				_previousMove.X = 1;
				_previousMove.Y = 0;
				Broker.Action(ActionType.MOVE, _previousMove);
				return;
			}
		}
	}
}