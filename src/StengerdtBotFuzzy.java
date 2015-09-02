import java.util.Random;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;

public class StengerdtBotFuzzy extends Bot {
	
	private Random rnd = new Random();
	private int dir = 0;
	
	private Vector2 closestFoodPos;
	private double closestFoodDist = 10000.0;
	private Vector2 closestWoodPos;
	private double closestWoodDist = 10000.0;
	private Vector2 currentPosition;
	
	private int currentIterationCounter = 0;
	private final int MAX_ITERATIONS = 100;
	
	public StengerdtBotFuzzy()
	{
		this.MySymbol = "s";
		this.fis = FIS.load("src/StengerdtFuzzy.fcl");
	}
	
	@Override
	public void Play() 
	{
		dir = rnd.nextInt(4);
		this.currentIterationCounter = 0;
		while(this.Broker.GetMyAP() > 0 && this.currentIterationCounter < MAX_ITERATIONS)
		{
			++this.currentIterationCounter;
			this.currentPosition = this.Broker.GetMyPosition();
			closestFoodDist = 10000;
			closestWoodDist = 10000;
			for(int i = -10; i <= 10; ++i)
			{
				for(int j = -10; j <= 10; ++j)
				{
					Vector2 fieldPos = Vector2.Plus(this.currentPosition, new Vector2(i,j));
					if(fieldPos.X < 0 || fieldPos.X > 49 || fieldPos.Y < 0 || fieldPos.Y > 49)
					{
						continue;
					}
					String fieldType = this.Broker.GetFieldType(fieldPos.X, fieldPos.Y);
					if(fieldType == "FOOD")
					{
						double dist = Math.abs(i) + Math.abs(j);
						if(dist < closestFoodDist)
						{
							closestFoodDist = dist;
							closestFoodPos = fieldPos;
						}
					}
					else if(fieldType == "WOOD")
					{
						double dist = Math.abs(i) + Math.abs(j);
						if(dist < closestWoodDist)
						{
							closestWoodDist = dist;
							closestWoodPos = fieldPos;
						}
					}
				}
			}
			
			if(closestFoodDist == 10000)
			{
				closestFoodDist = -1;
			}
			if(closestWoodDist == 10000)
			{
				closestWoodDist = -1;
			}
			
			this.fis.setVariable("HP", this.Broker.GetMyHP());
			this.fis.setVariable("WP", this.Broker.GetMyWP());
			this.fis.setVariable("AP", this.Broker.GetMyAP());
			this.fis.setVariable("PP", this.Broker.GetMyPP());
			this.fis.setVariable("closestFoodDist", this.closestFoodDist);
			this.fis.setVariable("closestWoodDist", this.closestWoodDist);
			this.fis.evaluate();
			double foodPrio = this.fis.getVariable("food").getLatestDefuzzifiedValue();
			double woodPrio = this.fis.getVariable("wood").getLatestDefuzzifiedValue();
			double firePrio = this.fis.getVariable("fire").getLatestDefuzzifiedValue();
			if(foodPrio <= 0 && woodPrio <= 0 && firePrio <= 0)
			{
				RandomMove();
			}
			else
			{
				if(foodPrio >= woodPrio && foodPrio >= firePrio && closestFoodDist != -1)
				{
					GoForFoodOrWood(closestFoodPos.X, closestFoodPos.Y);
				}
				else if(woodPrio > foodPrio && woodPrio >= firePrio && closestWoodDist != -1)
				{
					GoForFoodOrWood(closestWoodPos.X, closestWoodPos.Y);
				}
				else if(firePrio > foodPrio && firePrio > woodPrio)
				{
					this.Broker.Action(ActionType.KINDLE_FIRE, new Vector2(0,0));
				}
				else
				{
					RandomMove();
				}
			}
		}
	}
	
	private void GoForFoodOrWood(int x, int y)
	{
		boolean flag = true;
		this.currentIterationCounter = MAX_ITERATIONS;
		while(this.Broker.GetMyAP() >= 3 && flag)
		{
			this.currentPosition = this.Broker.GetMyPosition();
			if(this.currentPosition.X < x)
			{
				this.Broker.Action(ActionType.MOVE, new Vector2(1, 0));
			}
			else if(this.currentPosition.X > x)
			{
				 this.Broker.Action(ActionType.MOVE, new Vector2(-1, 0));
			}
			else
			{
				if(this.currentPosition.Y < y)
				{
					this.Broker.Action(ActionType.MOVE, new Vector2(0, 1));
				}
				else if(this.currentPosition.Y > y)
				{
					this.Broker.Action(ActionType.MOVE, new Vector2(0, -1));
				}
				else
				{
					flag = false;
				}
			}
		}
	}
	
	private void RandomMove()
	{
		int x, y;
		x = 0;
		y = 0;
		if(dir == 0)
		{
			x = 1;
			y = 0;
		}
		else if(dir == 1)
		{
			x = -1;
			y = 0;
		}
		else if(dir == 2)
		{
			x = 0;
			y = 1;
		}
		else
		{
			x = 0;
			y = -1;
		}
		
		this.Broker.Action(ActionType.MOVE, new Vector2(x,y));
	}

}
