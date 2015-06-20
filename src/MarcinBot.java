import java.util.ArrayList;
import java.util.Random;

import CLIPSJNI.Environment;
import CLIPSJNI.SymbolValue;
import CLIPSJNI.IntegerValue;


public class MarcinBot extends Bot
{
	private static final int FIELD_OF_VIEW = 3;
	private static final int DATA_SLOTS = 7;
	private static final int MAX_BOT_TIME = 5;
	
	private int[] evalArray = new int[DATA_SLOTS];
	private ArrayList<Integer> evalHelper = new ArrayList<Integer>();
	private String[] evalLabels = new String[]
	{
		"Up",
		"Down",
		"Left",
		"Right",
		"Fire",
		"Trap",
		"Throw",
		"Nothing"
	};
	
	private Random randomGenerator = new Random();
	
	private ArrayList<String> actList = new ArrayList<String>();
	
	public MarcinBot()
	{
		this.MySymbol = "Q";
		this.Clips = new Environment();
		this.Clips.load("src/MarcinCLIPS.clp");
		this.Clips.reset();
		
		Initialize();
	}
	
	private void Initialize()
	{
		Clips.eval("(bind ?*MAX_BOT_TIME* " + String.valueOf(MAX_BOT_TIME) + ")");
	}
	
	@Override
	public void Play() {
		
		actList.clear();
		String cFldStr = "ERROR";
		
		while(Broker.GetMyAP() > 0)
		{
			// clear CLIPS assert data
			Clips.eval("(do-for-all-facts "
							+ "((?f tile)) TRUE "
							+ "(retract ?f))");
			
			for(int i = 0; i < DATA_SLOTS; ++i)
			{
				Clips.eval("(bind ?*slot" + evalLabels[i] + "* -1)");
			}
			
			Clips.eval("(bind ?*throwCoordX* 0)");
			Clips.eval("(bind ?*throwCoordY* 0)");
			Clips.eval("(do-for-all-facts ((?h helper)) TRUE (retract ?h))");
			Clips.eval("(do-for-all-facts"
					+ "((?b bot))"
					+ "(and (eq ?b:state past) (eq ?b:modifiedFlag false))"
					+ "(modify ?b (time (+ ?b:time 1)) (modifiedFlag true))"
					+ ")");
			
			Vector2 myPos = Broker.GetMyPosition();
			
			// upload current map data
			for(int i = -FIELD_OF_VIEW; i <= FIELD_OF_VIEW; ++i)
			{
				for(int j = -FIELD_OF_VIEW; j <= FIELD_OF_VIEW; ++j)
				{
					Vector2 currentPos = new Vector2(i, j);
					currentPos.Add(myPos);
					
					if(currentPos.X >= 0 &&
						currentPos.X < 50 &&
						currentPos.Y >= 0 &&
						currentPos.Y < 50)
					{
						String type;
						if((Math.abs(myPos.X - currentPos.X) == 1 && Math.abs(myPos.Y - currentPos.Y) == 0) || 
								(Math.abs(myPos.Y - currentPos.Y) == 1 && Math.abs(myPos.X - currentPos.X) == 0))
						{
							type = "neighbour";
						}
						else if(myPos.X == currentPos.X && myPos.Y == currentPos.Y )
						{
							type = "current";
						}
						else
						{
							type = "other";
						}
						
						
						Clips.assertString("(tile"
								+ "(x " + currentPos.X + ")"
								+ "(y " + currentPos.Y + ")"
								+ "(type " + type + ")"
								+ "(fieldType " + Broker.GetFieldType(currentPos.X, currentPos.Y) + ")"
								+ ")");
					}
				}
			}
			
			// upload current player data
			Clips.assertString("(bot"
					+ "(HP " + Broker.GetMyHP() + ")" 
					+ "(PP " + Broker.GetMyPP() + ")" 
					+ "(AP " + Broker.GetMyAP() + ")" 
					+ "(WP " + Broker.GetMyWP() + ")" 
					+ "(state current)"
					+ "(posX " + String.valueOf(myPos.X) + ")"
					+ "(posY " + String.valueOf(myPos.Y) + ")"
					+ "(currentField " + Broker.GetFieldType(myPos.X, myPos.Y) + ")"
					+ ")");
			
			// upload others positions
			
			// RUN!
			Clips.run();
			
			// retrieve decision from clips

			String valStr;
			evalHelper.clear();
			int max = Integer.MIN_VALUE;
			int maxID;
			for(int i = 0; i < DATA_SLOTS; ++i)
			{
				evalArray[i] = ((IntegerValue) Clips.eval("?*slot" + evalLabels[i] + "*")).intValue();
				if(evalArray[i] > max)
				{
					evalHelper.clear();
					max = evalArray[i];
					maxID = i;
					evalHelper.add(i);
				}
				else if(evalArray[i] == max)
				{
					evalHelper.add(i);
				}
			}
			
			if(evalHelper.size() > 1)
			{
				// calculate rand
				int id = randomGenerator.nextInt(evalHelper.size());
				id = evalHelper.get(id);
				evalHelper.clear();
				evalHelper.add(id);
			}
			
			maxID = evalHelper.get(0);
			cFldStr = Broker.GetFieldType(Broker.GetMyPosition().X, Broker.GetMyPosition().Y);
			
			if(evalArray[maxID] >= 0)
			{
				if(maxID == 0)
				{
					cFldStr = Broker.GetFieldType(Broker.GetMyPosition().X, Broker.GetMyPosition().Y - 1);
					Broker.Action(ActionType.MOVE, new Vector2(0, -1));
				}
				else if(maxID == 1)
				{
					cFldStr = Broker.GetFieldType(Broker.GetMyPosition().X, Broker.GetMyPosition().Y + 1);
					Broker.Action(ActionType.MOVE, new Vector2(0, 1));
				}
				else if(maxID == 2)
				{
					cFldStr = Broker.GetFieldType(Broker.GetMyPosition().X - 1, Broker.GetMyPosition().Y);
					Broker.Action(ActionType.MOVE, new Vector2(-1, 0));
				}
				else if(maxID == 3)
				{
					cFldStr = Broker.GetFieldType(Broker.GetMyPosition().X + 1, Broker.GetMyPosition().Y);
					Broker.Action(ActionType.MOVE, new Vector2(1, 0));
				}
				else if(maxID == 4)	// kindle bonfire
				{
					Broker.Action(ActionType.KINDLE_FIRE, Broker.GetMyPosition());
				}
				else if(maxID == 5)	// set trap where u stand
				{
					Broker.Action(ActionType.SET_TRAP, Broker.GetMyPosition());
				}
				else if(maxID == 6)	// throw spear
				{
					int cX, cY;
					cX = ((IntegerValue) Clips.eval("?*throwCoordX")).intValue();
					cY = ((IntegerValue) Clips.eval("?*throwCoordY")).intValue();
					Broker.Action(ActionType.THROW_SPEAR, new Vector2(cX, cY));
				}
				else
				{
					cFldStr = "ERROR";
					System.out.println("Marcin: ERROR: GOT TRASH FROM CLIPS: " + String.valueOf(maxID));
					return;
				}
				valStr = evalLabels[maxID];
			}
			else
			{
				Broker.Action(ActionType.MOVE, new Vector2(0, 0));
				valStr = evalLabels[DATA_SLOTS];
			}
			
			actList.add(valStr);
			
			// cleanup		
			Clips.eval("(do-for-all-facts"
					+ "((?b bot))"
					+ "(and (eq ?b:state past) (eq ?b:modifiedFlag true))"
					+ "(modify ?b (modifiedFlag false))"
					+ ")");
			
					Clips.eval("(do-for-all-facts ((?b bot)) (eq ?b:state current) (retract ?b))");
					Clips.assertString("(bot"
							+ "(HP " + Broker.GetMyHP() + ")" 
							+ "(PP " + Broker.GetMyPP() + ")" 
							+ "(AP " + Broker.GetMyAP() + ")" 
							+ "(WP " + Broker.GetMyWP() + ")" 
							+ "(state past)"
							+ "(posX " + String.valueOf(myPos.X) + ")"
							+ "(posY " + String.valueOf(myPos.Y) + ")"
							+ "(currentField " + Broker.GetFieldType(myPos.X, myPos.Y) + ")"
							+ ")");
		}
		
//		Clips.eval("(facts)");
		
		String tmpAct = "";
		for(String str : actList)
		{
			tmpAct += str + ";";
		}
		
		System.out.println("Marcin | " + 
				"POS=" + Broker.GetMyPosition().toString() + " | " +
				"CFLD=" + cFldStr + " | " +
				"HP=" + String.valueOf(Broker.GetMyHP()) + " | " +
				"PP=" + String.valueOf(Broker.GetMyPP()) + " | " +
				"AP=" + String.valueOf(Broker.GetMyAP()) + " | " +
				"WP=" + String.valueOf(Broker.GetMyWP()) + " | " +
				"ACTS=" + String.valueOf(tmpAct)
				);
	}
}