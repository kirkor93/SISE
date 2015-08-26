import net.sourceforge.jFuzzyLogic.FIS;


public class PatrykBotFuzzy extends Bot {

	private final int _C_fov = 30;
	
	private int _previousDecision;
	private int _loopCnt = 0;
	private Vector2 _previousRand = new Vector2();
	
	public PatrykBotFuzzy()
	{
		this.MySymbol = "k";
		this.fis = FIS.load("src/PatrykFuzzy.fcl");
	}
	
	@Override
	public void Play() {
		while(this.Broker.GetMyAP() > 0)
		{
			int foodDistance = Integer.MAX_VALUE;
			Vector2 foodPosition = new Vector2();
			int corpseDistance = Integer.MAX_VALUE;
			Vector2 corpsePosition = new Vector2();
			int woodDistance = Integer.MAX_VALUE;
			Vector2 woodPosition = new Vector2();
			int enemyDistance = Integer.MAX_VALUE;
			Vector2 enemyPosition = new Vector2();
			
			Vector2 myPos = Broker.GetMyPosition();
			
			
			for(int i = -_C_fov; i <= _C_fov; ++i)
			{
				for(int j = -_C_fov; j <= _C_fov; ++j)
				{
					Vector2 currentPos = new Vector2(i, j);
					currentPos.Add(myPos);
					if(currentPos != myPos)
					{
						if(currentPos.X >= 0 &&	currentPos.X < 50 &&
							currentPos.Y >= 0 && currentPos.Y < 50)
						{				
							String fieldType = this.Broker.GetFieldType(currentPos.X, currentPos.Y);
							
							int dist = GetDistance(myPos,currentPos);
							
							switch(fieldType)
							{
							case "FOOD":
								if(dist < foodDistance)
								{
									foodDistance = dist;
									foodPosition = currentPos;
								}	
								break;
							case "CORPSE":
								if(dist < corpseDistance)
								{
									corpseDistance = dist;
									corpsePosition = currentPos;
								}
								break;
							case "WOOD":
								if(dist < woodDistance)
								{
									woodDistance = dist;
									woodPosition = currentPos;
								}
								break;
							case "ENEMY":
								if(dist < enemyDistance)
								{
									enemyDistance = dist;
									enemyPosition = currentPos;
								}
								break;
							}
						}
					}
				}
			}
			
			if(foodDistance == Integer.MAX_VALUE) foodDistance = -1;
			if(corpseDistance == Integer.MAX_VALUE) corpseDistance = -1;
			if(woodDistance == Integer.MAX_VALUE) woodDistance = -1;
			if(enemyDistance == Integer.MAX_VALUE) enemyDistance = -1;
			
			this.fis.setVariable("botHP", this.Broker.GetMyHP());
			this.fis.setVariable("botWP", this.Broker.GetMyWP());
			this.fis.setVariable("botAP", this.Broker.GetMyAP());
			this.fis.setVariable("botPP", this.Broker.GetMyPP());
			this.fis.setVariable("foodDistance", foodDistance);
			this.fis.setVariable("corpseDistance", corpseDistance);
			this.fis.setVariable("woodDistance", woodDistance);
			this.fis.setVariable("enemyDistance", enemyDistance);
			
			this.fis.evaluate();
			
			double eatF = this.fis.getVariable("eatF").getLatestDefuzzifiedValue();
			double eatC = this.fis.getVariable("eatC").getLatestDefuzzifiedValue();
			double wood = this.fis.getVariable("wood").getLatestDefuzzifiedValue();
			double throW = this.fis.getVariable("throw").getLatestDefuzzifiedValue();
			double kindle = this.fis.getVariable("kindle").getLatestDefuzzifiedValue();
			
			int myAP = this.Broker.GetMyAP();
				
			switch(DecideAction( eatF, eatC, wood, throW, kindle))
			{
			case "WAIT":
				Broker.Action(ActionType.MOVE, new Vector2(0,0));
				break;
			case "EATF":
				if(myAP >= 3)
				{
					this.Broker.Action(ActionType.MOVE, GetDirection(myPos,foodPosition));
				}
				else
				{
					if(foodDistance > 1)
					{
						this.Broker.Action(ActionType.MOVE, GetDirection(myPos,foodPosition));
					}
					else
					{
						Broker.Action(ActionType.MOVE, new Vector2(0,0));
					}
				}
				break;
			case "EATC":
				if(myAP >= 3)
				{
					this.Broker.Action(ActionType.MOVE, GetDirection(myPos,corpsePosition));
				}
				else
				{
					if(corpseDistance > 1)
					{
						this.Broker.Action(ActionType.MOVE, GetDirection(myPos,corpsePosition));
					}
					else
					{
						Broker.Action(ActionType.MOVE, new Vector2(0,0));
					}
				}
				break;
			case "WOOD":
				if(myAP >= 3)
				{
					this.Broker.Action(ActionType.MOVE, GetDirection(myPos,woodPosition));
				}
				else
				{
					if(woodDistance > 1)
					{
						this.Broker.Action(ActionType.MOVE, GetDirection(myPos,woodPosition));
					}
					else
					{
						Broker.Action(ActionType.MOVE, new Vector2(0,0));
					}
				}
				break;
			case "THROW":
				if(myAP >= 5)
				{
					this.Broker.Action(ActionType.THROW_SPEAR, enemyPosition);
				}
				else
				{
					if(enemyDistance > 4)
					{
						this.Broker.Action(ActionType.MOVE, GetDirection(myPos,enemyPosition));
					}
					else
					{
						Broker.Action(ActionType.MOVE, new Vector2(0,0));
					}
				}
				break;
			case "KINDLE":
				if(myAP >= 3)
				{
					this.Broker.Action(ActionType.KINDLE_FIRE, GetDirection(myPos,woodPosition));
				}
				else
				{
					Broker.Action(ActionType.MOVE, new Vector2(0,0));
				}
				break;
			case "RANDOM":
				RandomMove();
				break;
			}
			if(++_loopCnt > 2)
			{
				RandomMove();
			}
			++_loopCnt;
		}
	}
	
	private String DecideAction(double eatF,double eatC,double wood,double throW,double kindle)
	{
		String decision = "WAIT";
		
		if(eatF >= eatC && eatF >= wood && eatF >= throW && eatF >= kindle )
		{
			decision = "EATF";
		}
		else if(eatC >= eatF && eatC >= wood && eatC >= throW && eatC >= kindle )
		{
			decision = "EATC";
		}
		else if(wood >= eatF && wood >= eatC && wood >= throW && wood >= kindle )
		{
			decision = "WOOD";
		}
		else if(throW >= eatF && throW >= eatC && throW >= wood && throW >= kindle )
		{
			decision = "THROW";
		}
		else if(kindle >= eatF && kindle >= eatC && kindle >= wood && kindle >= throW )
		{
			decision = "KINDLE";
		}
		
		if(eatF <= 0 && eatC <= 0 && wood <= 0 && throW <= 0 && kindle <= 0)
		{
			decision = "RANDOM";
		}
		return decision;
	}
	
	private int GetDistance(Vector2 bot, Vector2 dest)
	{			
		return (Math.abs(bot.X - dest.X)) + (Math.abs(bot.Y - dest.Y));
	}
	
	private Vector2 GetDirection(Vector2 bot, Vector2 dest)
	{			
		Vector2 direction = new Vector2();
		if(Math.abs(bot.X - dest.X) > Math.abs(bot.Y - dest.Y))
		{
			if(bot.X > dest.X)
			{
				direction.X = -1;
				direction.Y = 0;
			}
			else
			{
				direction.X = 1;
				direction.Y = 0;
			}
		}
		else
		{
			if(bot.Y > dest.Y)
			{
				direction.X = 0;
				direction.Y = -1;
			}
			else
			{
				direction.X = 0;
				direction.Y = 1;
			}
		}
		return direction;
	}
	
	public void RandomMove()
	{
		while(true)
		{
			int n = (int) (Math.random()*4);
		if(n == 0 && Broker.GetMyPosition().Y != 0 && _previousDecision != 1)
		{
			_previousDecision = n;
			_previousRand.X = 0;
			_previousRand.Y = -1;
			Broker.Action(ActionType.MOVE, _previousRand);
			return;
			}
		else if(n == 1 && Broker.GetMyPosition().Y != 49 && _previousDecision != 0)
		{
			_previousDecision = n;
			_previousRand.X = 0;
			_previousRand.Y = 1;
			Broker.Action(ActionType.MOVE, _previousRand);
			return;
			}
		else if(n == 2 && Broker.GetMyPosition().X != 0 && _previousDecision != 3)
		{
			_previousDecision = n;
			_previousRand.X = -1;
			_previousRand.Y = 0;
			Broker.Action(ActionType.MOVE, _previousRand);
			return;
			}
		else if(n == 3 && Broker.GetMyPosition().X != 49 && _previousDecision != 2)
		{
			_previousDecision = n;
			_previousRand.X = 1;
			_previousRand.Y = 0;
			Broker.Action(ActionType.MOVE, _previousRand);
			return;
			}
		}
	}

}