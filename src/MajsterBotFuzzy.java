import java.util.ArrayList;
import java.util.List;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;

public class MajsterBotFuzzy extends Bot {
	
	public class Tile {
		public String type;
		public int distance;
		public int direction;
		public double priority;
		
		public Tile(String type, int distance, int direction)
		{
			this.type = type;
			this.distance = distance;
			this.direction = direction;
			this.priority = 0;
		}
	}
	
	public List<Tile> neighbours = new ArrayList<Tile>();
	
	public MajsterBotFuzzy()
	{
		this.MySymbol = "M";
		this.fis = FIS.load("src/MajsterFuzzy.fcl");
	}
	
	@Override
	public void Play() {
		// TODO Auto-generated method stub
		
		while(Broker.GetMyAP() > 0)
		{
			if(this.fis == null)
			{
				System.err.println("Can't load file");
				return;
			}
			
			System.out.println("AP: " + Broker.GetMyAP());
			System.out.println("HP: " + Broker.GetMyHP());
			System.out.println("WP: " + Broker.GetMyWP());
			System.out.println(Broker.GetMyPosition().toString());
			
			if(Broker.GetMyHP() <= 0)
				return;
			
			CheckNeighbours(Broker.GetMyPosition().X, Broker.GetMyPosition().Y);
			
			if(this.neighbours.isEmpty())
			{
				if(Broker.GetMyAP() >= 3 && Broker.GetMyPP() <= 5 && Broker.GetMyWP() > 0)
					Broker.Action(ActionType.KINDLE_FIRE, new Vector2(0, 0));
				else
					Randomize();
			}
			else
			{
				for(int j = 0; j<this.neighbours.size(); ++j)
				{
					if(this.neighbours.get(j).type == "ENEMY" && Broker.GetMyAP() == 5 && Broker.GetMyWP() > 0)
						Attack(this.neighbours.get(j));
					fis.setVariable("distance", this.neighbours.get(j).distance);
					fis.setVariable("actionPoints", Broker.GetMyAP());
					fis.evaluate();
					this.neighbours.get(j).priority = (double) fis.getVariable("priority").getLatestDefuzzifiedValue();
					
				}
				DecideAndGo();
				
				this.neighbours.clear();
			}
		}
	}
	
	public void CheckNeighbours(int currentX, int currentY){
		for(int i=1; i<9; ++i)
		{
			if(Broker.GetMyPosition().X + i <= 49 && Broker.GetFieldType(currentX + i, currentY) != "NORMAL")
			{
				//0 - right
				this.neighbours.add(
						new Tile(Broker.GetFieldType(currentX + i, currentY), i, 0));
			}
			if(Broker.GetMyPosition().X - i >= 0 && Broker.GetFieldType(currentX - i, currentY) != "NORMAL")
			{
				//1 - left
				this.neighbours.add(
						new Tile(Broker.GetFieldType(currentX - i, currentY), i, 1));
			}
			if(Broker.GetMyPosition().Y + i <= 49 && Broker.GetFieldType(currentX, currentY + i) != "NORMAL")
			{
				//2 - down
				this.neighbours.add(
						new Tile(Broker.GetFieldType(currentX, currentY + i), i, 2));
			}
			if(Broker.GetMyPosition().Y - i >= 0 && Broker.GetFieldType(currentX, currentY - i) != "NORMAL")
			{
				//3 - up
				this.neighbours.add(
						new Tile(Broker.GetFieldType(currentX, currentY - i), i, 3));
			}
		}
	}
	
	public void Randomize()
	{
		int n = (int) (Math.random()*4);
		if(n == 0 && Broker.GetMyPosition().Y != 0 && 
				Broker.GetFieldType(Broker.GetMyPosition().X, Broker.GetMyPosition().Y - 1) == "NORMAL")
			Broker.Action(ActionType.MOVE, new Vector2(0, -1));
		else if(n == 1 && Broker.GetMyPosition().Y != 49 &&
				Broker.GetFieldType(Broker.GetMyPosition().X, Broker.GetMyPosition().Y + 1) == "NORMAL")
			Broker.Action(ActionType.MOVE, new Vector2(0, 1));
		else if(n == 2 && Broker.GetMyPosition().X != 0 &&
				Broker.GetFieldType(Broker.GetMyPosition().X - 1, Broker.GetMyPosition().Y) == "NORMAL")
			Broker.Action(ActionType.MOVE, new Vector2(-1, 0));
		else if(n == 3 && Broker.GetMyPosition().X != 49 &&
				Broker.GetFieldType(Broker.GetMyPosition().X + 1, Broker.GetMyPosition().Y) == "NORMAL")
			Broker.Action(ActionType.MOVE, new Vector2(1, 0));
		else
			Broker.Action(ActionType.MOVE, new Vector2(0, 0));
	}
	
	public int GetHighest()
	{
		int highestIndex = 0;
		for(int i=0; i<this.neighbours.size() - 1; ++i)
		{
			if(this.neighbours.get(i).priority > this.neighbours.get(i+1).priority)
				highestIndex = i;
			else
				highestIndex = i+1;
		}
		if(this.neighbours.get(highestIndex).priority > 6 &&
				this.neighbours.get(highestIndex).priority < 10)
		{
			return -1;
		}
		return highestIndex;
	}
	
	public void DecideAndGo()
	{
		int i = GetHighest();
		if(i == -1)
		{
			Broker.Action(ActionType.MOVE, new Vector2(0, 0));
			return;
		}
		System.out.println(this.neighbours.get(i).priority);
		switch(this.neighbours.get(i).direction)
		{
		case 0:
			Broker.Action(ActionType.MOVE, new Vector2(1, 0));
			break;
		case 1:
			Broker.Action(ActionType.MOVE, new Vector2(-1, 0));
			break;
		case 2:
			Broker.Action(ActionType.MOVE, new Vector2(0, 1));
			break;
		case 3:
			Broker.Action(ActionType.MOVE, new Vector2(0, -1));
			break;
		}
		
	}
	
	public void Attack(Tile t)
	{
		switch(t.direction)
		{
		case 0:
			Broker.Action(ActionType.THROW_SPEAR, 
					new Vector2(Broker.GetMyPosition().X + t.distance, Broker.GetMyPosition().Y));
			break;
		case 1:
			Broker.Action(ActionType.THROW_SPEAR, 
					new Vector2(Broker.GetMyPosition().X - t.distance, Broker.GetMyPosition().Y));
			break;
		case 2:
			Broker.Action(ActionType.THROW_SPEAR, 
					new Vector2(Broker.GetMyPosition().X, Broker.GetMyPosition().Y + t.distance));
			break;
		case 3:
			Broker.Action(ActionType.THROW_SPEAR, 
					new Vector2(Broker.GetMyPosition().X, Broker.GetMyPosition().Y - t.distance));
		}
	}
}
