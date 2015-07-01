import java.util.Random;

import CLIPSJNI.Environment;
import CLIPSJNI.FactAddressValue;
import CLIPSJNI.IntegerValue;
import CLIPSJNI.MultifieldValue;
import CLIPSJNI.SymbolValue;

public class StengerdtBot extends Bot
{
	final int MAX_ITERATIONS = 10;
	final int MAX_HORIZONTAL_FIELDS = 5;
	final int MAX_VERTICAL_FIELDS = 5;
	
	private final String ACTION_UP = "up";
	private final String ACTION_DOWN = "down";
	private final String ACTION_LEFT = "left";
	private final String ACTION_RIGHT = "right";
	private final String ACTION_FIRE = "fire";
	private final String ACTION_THROW = "throw";
	private final String ACTION_TRAP = "trap";
	private final String ACTION_NOTHING = "nothing";
	
	private int currentIterationCounter = 0;
	private Vector2 currentPosition;
	private int randDirection;
	private Random rnd = new Random();
	
	public StengerdtBot()
	{
		this.MySymbol = "S";
		this.Clips = new Environment();
		this.Clips.load("src/StengerdtClips.clp");
		this.Clips.reset();
		this.Clips.run();
	}
	
	@Override
	public void Play() {
		if(this.Broker.GetMyHP() <= 0)
		{
			return;
		}
		
		this.currentIterationCounter = 0;
		this.randDirection = rnd.nextInt(4);
		while(this.Broker.GetMyAP() > 0 && this.currentIterationCounter < MAX_ITERATIONS)
		{
			++this.currentIterationCounter; 
			//Clearing fields' info
			Clips.eval("(do-for-all-facts "
					+ "((?f tile)) TRUE "
					+ "(retract ?f))");
			//Clearing bot's info
			Clips.eval("(do-for-all-facts ((?b bot)) (retract ?b))");
			
			this.currentPosition = this.Broker.GetMyPosition();
			
			//So updating bot's state
			Clips.assertString("(bot"
					+ "(HP " + this.Broker.GetMyHP() + ")"
					+ "(PP " + this.Broker.GetMyPP() + ")"
					+ "(AP " + this.Broker.GetMyAP() + ")"
					+ "(WP " + this.Broker.GetMyWP() + ")"
					+ "(state current)"
					+ "(posX " + this.currentPosition.X + ")"
					+ "(posY " + this.currentPosition.Y + ")"
					+ ")"
					);
			
			//And fields' states
			Vector2 somePosition = this.currentPosition;
			String fieldType = "neighbour";
			for(int i = -MAX_HORIZONTAL_FIELDS; i <= MAX_HORIZONTAL_FIELDS; ++i)
			{
				for(int j = -MAX_VERTICAL_FIELDS; j <= MAX_VERTICAL_FIELDS; ++j)
				{
					if(i == 0 && j == 0) continue;
					fieldType = "neighbour";
					somePosition = Vector2.Plus(this.currentPosition, new Vector2(i, j));
					if(this.PositionCondition(somePosition))
					{
						if(!CheckIfNeighbour(i, j))
						{
							fieldType = "other";
						}
						this.Clips.assertString("(tile"
												+ "(x " + somePosition.X + ")"
												+ "(y " + somePosition.Y + ")"
												+ "(type " + fieldType + ")"
												+ "(fieldType " + this.Broker.GetFieldType(somePosition.X, somePosition.Y) +")"
												+ ")");
					}
				}
			}
			this.Clips.eval("(bind ?*action* nothing)");
			this.Clips.eval("(bind ?*curPrio* 0)");
			this.Clips.eval("(bind ?*foodX* -1)");
			this.Clips.eval("(bind ?*foodY* -1)");
			this.Clips.eval("(bind ?*woodX* -1)");
			this.Clips.eval("(bind ?*woodY* -1)");
			this.Clips.run();
			String action = "xyz";
			SymbolValue symbolValue = (SymbolValue)this.Clips.eval("?*action*");
			action = symbolValue.stringValue();
			int fx,fy,wx,wy;
			if(action.equals("food"))
			{
				IntegerValue iv = (IntegerValue)this.Clips.eval("?*foodX*");
				fx = iv.intValue();
				iv = (IntegerValue)this.Clips.eval("?*foodY*");
				fy = iv.intValue();
				boolean flag = true;
				this.currentIterationCounter = MAX_ITERATIONS;
				while(this.Broker.GetMyAP() >= 3 && flag)
				{
					this.currentPosition = this.Broker.GetMyPosition();
					if(this.currentPosition.X < fx)
					{
						DoAction(ACTION_RIGHT);
					}
					else if(this.currentPosition.X > fx)
					{
						DoAction(ACTION_LEFT);
					}
					else
					{
						if(this.currentPosition.Y < fy)
						{
							DoAction(ACTION_DOWN);
						}
						else if(this.currentPosition.Y > fy)
						{
							DoAction(ACTION_UP);
						}
						else
						{
							flag = false;
						}
					}
				}
			}
			else if(action.equals("wood"))
			{
				IntegerValue iv = (IntegerValue)this.Clips.eval("?*woodX*");
				wx = iv.intValue();
				iv = (IntegerValue)this.Clips.eval("?*woodY*");
				wy = iv.intValue();
				boolean flag = true;
				this.currentIterationCounter = MAX_ITERATIONS;
				while(this.Broker.GetMyAP() >= 3 && flag)
				{
					this.currentPosition = this.Broker.GetMyPosition();
					if(this.currentPosition.X < wx)
					{
						DoAction(ACTION_RIGHT);
					}
					else if(this.currentPosition.X > wx)
					{
						DoAction(ACTION_LEFT);
					}
					else
					{
						if(this.currentPosition.Y < wy)
						{
							DoAction(ACTION_DOWN);
						}
						else if(this.currentPosition.Y > wy)
						{
							DoAction(ACTION_UP);
						}
						else
						{
							flag = false;
						}
					}
				}
			}
			else
			{	
				this.DoAction(action);
			}
		}
	}
	
	private boolean PositionCondition(Vector2 position)
	{
		return (position.X >= 0 && position.X < 50 && position.Y >= 0 && position.Y < 50);
	}
	
	private boolean CheckIfNeighbour(int i, int j)
	{
		if(i + j == 1)
		{
			return true;
		}
		
		if(i*j == 1 || i*j == -1)
		{
			return true;
		}
		
		return false;
	}
	
	private void DoAction(String action)
	{
		boolean flag = true;
		switch(action)
		{
		case ACTION_DOWN:
			flag = this.Broker.Action(ActionType.MOVE, new Vector2(0, 1));
			break;
		case ACTION_UP:
			flag = this.Broker.Action(ActionType.MOVE, new Vector2(0, -1));
			break;
		case ACTION_LEFT:
			flag = this.Broker.Action(ActionType.MOVE, new Vector2(-1, 0));
			break;
		case ACTION_RIGHT:
			flag = this.Broker.Action(ActionType.MOVE, new Vector2(1, 0));
			break;
		case ACTION_FIRE:
			System.out.println("Hey, fire");
			this.Broker.Action(ActionType.KINDLE_FIRE, new Vector2(0, 0));
			break;
		case ACTION_THROW:
			//get throw position
			break;
		case ACTION_TRAP:
			//get trap position
			break;
		case ACTION_NOTHING:
			this.Broker.Action(ActionType.MOVE, this.RandomDirection());
			break;
		}
		
		if(!flag)
		{
			System.out.println("Lol, dosent work");
		}
	}
	
	private Vector2 RandomDirection()
	{
		switch(this.randDirection)
		{
		case 0:
			return new Vector2(0, 1);
		case 1:
			return new Vector2(1, 0);
		case 2:
			return new Vector2(0, -1);
		case 3:
			return new Vector2(-1, 0);
		default:
			return new Vector2(1, 0);
		}
	}
}
