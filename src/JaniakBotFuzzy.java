import java.util.ArrayList;
import java.util.Comparator;

import net.sourceforge.jFuzzyLogic.FIS;

public class JaniakBotFuzzy extends Bot
{
	private static int vision = 5;
	
	public double foodPriority = 0;
	public double woodPriority = 0;
	public double attackPriority = 0;
	public double kindlePriority = 0;
	
	public ArrayList<Double> actions = new ArrayList<Double>();
	public Comparator<Double> c;
	
	public JaniakBotFuzzy()
	{
		this.MySymbol = "J";
		this.fis = FIS.load("src/JaniakFuzzy.fcl");
	}
	
	@Override
	public void Play() {
		// TODO Auto-generated method stub
		
		if(Broker.GetMyHP() <= 0)
		{
			return;
		}
		
		while (Broker.GetMyAP() > 0 && Broker.GetMyHP() > 0 && Broker.GetMyPP() > 0)
		{
			
			
			//fis.getFunctionBlock("currentaction").reset();
			
			
			Vector2 botPosition = Broker.GetMyPosition();
			Vector2 badPosition = new Vector2(-1, -1);
			Vector2 foodPosition = badPosition;
			int foodDistance = 666;
			Vector2 woodPosition = badPosition;
			int woodDistance = 666;
			Vector2 enemyPosition = badPosition;
			
			for (int i = -vision; i <= vision; i++)
			{
				for (int j = -vision; j <= vision ; j++)
				{
					if(i + j == 0)
					{
						j = j + 1; 
					}
					if(Broker.GetMyPosition().X + i < 50 && Broker.GetMyPosition().X + i >= 0 &&
							Broker.GetMyPosition().Y + j < 50 && Broker.GetMyPosition().Y + j >= 0)
					{
						if(Broker.GetFieldType(botPosition.X + i, botPosition.Y +j) == "FOOD")
						{
							if(foodPosition == badPosition)
							{
								foodPosition = new Vector2(botPosition.X + i, botPosition.Y +j);
								foodDistance = Math.abs((foodPosition.X - botPosition.X) + (foodPosition.Y - botPosition.Y));
							}
							if(foodDistance > (Math.abs(i) + Math.abs(j)))
							{
								foodPosition = new Vector2(botPosition.X + i, botPosition.Y +j);
								foodDistance = Math.abs((foodPosition.X - botPosition.X) + (foodPosition.Y - botPosition.Y));
							}
									
						}
						
						if(Broker.GetFieldType(botPosition.X + i, botPosition.Y +j) == "WOOD")
						{
							if(woodPosition == badPosition)
							{
								woodPosition = new Vector2(botPosition.X + i, botPosition.Y +j);
								woodDistance = Math.abs((foodPosition.X - botPosition.X) + (foodPosition.Y - botPosition.Y));
							}
							if(woodDistance > (Math.abs(i) + Math.abs(j)))
							{
								woodPosition = new Vector2(botPosition.X + i, botPosition.Y +j);
								woodDistance = Math.abs((foodPosition.X - botPosition.X) + (foodPosition.Y - botPosition.Y));
							}
									
						}
						
						if(Broker.GetFieldType(botPosition.X + i, botPosition.Y +j) == "ENEMY")
						{
							if(enemyPosition == badPosition)
							{
								enemyPosition = new Vector2(botPosition.X + i, botPosition.Y +j);
							}
							
									
						}
					}
					
				}
			}
			
			fis.setVariable("hp", Broker.GetMyHP());
			fis.setVariable("pp", Broker.GetMyPP());
			fis.setVariable("wp", Broker.GetMyWP());
			fis.setVariable("distanceFood", foodDistance);
			fis.setVariable("distanceWood", woodDistance);
			if(enemyPosition != badPosition)
			{
				fis.setVariable("enemyInRange", 1);
			}
			if(enemyPosition == badPosition)
			{
				fis.setVariable("enemyInRange", 0);
			}
			
			fis.evaluate();
			
			foodPriority = fis.getVariable("priorityFood").getLatestDefuzzifiedValue();
			woodPriority = fis.getVariable("priorityWood").getLatestDefuzzifiedValue();
			attackPriority = fis.getVariable("priorityAttack").getLatestDefuzzifiedValue();
			kindlePriority = fis.getVariable("priorityKindle").getLatestDefuzzifiedValue();
			actions.add(foodPriority);
			actions.add(woodPriority);
			actions.add(attackPriority);
			actions.add(kindlePriority);
			//actions.sort(c);
			
			if(actions.get(0) <= 0)
			{
				Randomize();
			}
			else
			{
				if(actions.get(0).equals(kindlePriority))
				{
					this.Broker.Action(ActionType.KINDLE_FIRE, new Vector2(0,0));
				}
				if(actions.get(0).equals(attackPriority))
				{
					Broker.Action(ActionType.THROW_SPEAR, new Vector2(enemyPosition.X, enemyPosition.Y));
				}
				if(actions.get(0).equals(foodPriority))
				{
					FindSomething(foodPosition.X, foodPosition.Y);
				}
				if(actions.get(0).equals(woodPriority))
				{
					FindSomething(woodPosition.X, woodPosition.Y);
				}
				if(Broker.GetMyAP() <3)
				{
					Broker.Action(ActionType.MOVE, new Vector2(0, 0));
				}
				else
				{
					Randomize();
				}
			}
			
			actions.clear();
		}
		
	}
	
	private void FindSomething(int x, int y)
	{
		while(Broker.GetMyAP() >= 3)
		{
			if(Broker.GetMyPosition().X < x)
			{
				Broker.Action(ActionType.MOVE, new Vector2(1, 0));
			}
			else if(Broker.GetMyPosition().X > x)
			{
				 this.Broker.Action(ActionType.MOVE, new Vector2(-1, 0));
			}
			else if(Broker.GetMyPosition().Y < y)
			{
				this.Broker.Action(ActionType.MOVE, new Vector2(0, 1));
			}
			else if(Broker.GetMyPosition().Y > y)
			{
				this.Broker.Action(ActionType.MOVE, new Vector2(0, -1));
			}
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