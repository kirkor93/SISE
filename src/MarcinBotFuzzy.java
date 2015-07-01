import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import sun.misc.Queue;
import CLIPSJNI.IntegerValue;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Variable;


public class MarcinBotFuzzy extends Bot {

	private static final String MAIN_BLOCK_NAME = "action";
	private static final int FIELD_OF_VIEW = 6;
	private static final int DATA_SLOTS = 9;
	private static final int MAX_BOT_TIME = 5;
	private static final int WOOD_TO_LAY_TRAP = 8;
	private static final int WILL_BE_CANNIBAL_BELOW_HP = 6;
	private static final int VARIABLE_COUNT = 4;
	
	private static final int DIR_UP = 0;
	private static final int DIR_DOWN = 1;
	private static final int DIR_LEFT = 2;
	private static final int DIR_RIGHT = 3;
	
	private int tl = -1;
	private Random randomGenerator = new Random();
	private double[] dataArray = new double[DATA_SLOTS];
	private ArrayList<Integer> dataHelper = new ArrayList<Integer>();
	private String[] dataLabels = new String[]
	{
		"Up",
		"Down",
		"Left",
		"Right",
		"Fire",
		"Trap",
		"Throw",
		"Wait",
		"Random"
	};
	private String[] variableLabels = new String[]
	{
		"clFood",
		"clWood",
		"clCorpse",
		"clPlayer"
	};
	private ArrayList<String> actList = new ArrayList<String>();
	private LinkedList<Vector2> prevPositions = new LinkedList<Vector2>();
	
	private FunctionBlock fb;
	
	public MarcinBotFuzzy() 
	{
		this.MySymbol = "Q";
		this.fis = FIS.load("src/MarcinFuzzy.fcl");
		this.fb = fis.getFunctionBlock(MAIN_BLOCK_NAME);
		
		Initialize();
	}
	
	private void Initialize()
	{

	}
	
	@Override
	public void Play() 
	{
		if(Broker.GetMyHP() <= 0)
		{
//			System.out.println("MarcinFuzzy | " + 
//					"POS=" + Broker.GetMyPosition().toString() + " | " +
//					"TL=" + String.valueOf(tl) + " | " +
//					"DEAD"
//					);
			return;
		}
		
		actList.clear();

		while(Broker.GetMyAP() > 0)
		{
			// cleanup
			for(int i = 0; i < DATA_SLOTS; ++i)
			{
				dataArray[i] = Integer.MIN_VALUE;
			}
			if(prevPositions.size() > MAX_BOT_TIME)
			{
				prevPositions.removeFirst();
			}
			
			// send data to FIS
			fis.setVariable("botHP", Broker.GetMyHP());
			fis.setVariable("botAP", Broker.GetMyAP());
			fis.setVariable("botWP", Broker.GetMyWP());
			fis.setVariable("botPP", Broker.GetMyPP());
			
			// collect map data
			Vector2 mPos = Broker.GetMyPosition();
			Vector2[] vVs = new Vector2[VARIABLE_COUNT];
			for(int i = 0; i < VARIABLE_COUNT; ++i)
				vVs[i] = null;
			int clFoodDist, clWoodDist, clCorpseDist, clPlayerDistX, clPlayerDistY;
			clFoodDist = clWoodDist = clCorpseDist = clPlayerDistX = clPlayerDistY = -1;
			String ft;
			
			for(int i = 1; i < FIELD_OF_VIEW; ++i)
			{
				if(vVs[0] != null && vVs[1] != null && vVs[2] != null && vVs[3] != null)
				{
					break;
				}
				for(int j = -i; j <= i; ++j)
				{
					if(vVs[0] != null && vVs[1] != null && vVs[2] != null && vVs[3] != null)
					{
						break;
					}
					for(int k = -i; k <= i; ++k)
					{
						if((vVs[0] != null && vVs[1] != null && vVs[2] != null && vVs[3] != null) ||
								(k < 0 || k > 49 || j < 0 || j > 49))
						{
							break;
						}
						ft = Broker.GetFieldType(mPos.X + k, mPos.Y + j);
						if(ft.equals(FieldType.FOOD.toString()) && vVs[0] == null)
						{
							vVs[0] = new Vector2(k, j);
							clFoodDist = Math.abs(vVs[0].X - mPos.X) + Math.abs(vVs[0].Y - mPos.Y);
						}
						else if(ft.equals(FieldType.WOOD.toString()) && vVs[1] == null)
						{
							vVs[1] = new Vector2(k, j);
							clWoodDist = Math.abs(vVs[1].X - mPos.X) + Math.abs(vVs[1].Y - mPos.Y);
						}
						else if(ft.equals(FieldType.CORPSE.toString()) && vVs[2] == null)
						{
							vVs[2] = new Vector2(k, j);
							clCorpseDist = Math.abs(vVs[2].X - mPos.X) + Math.abs(vVs[2].Y - mPos.Y);
						}
						else if(ft.equals(FieldType.ENEMY.toString()) && vVs[3] == null)
						{
							vVs[3] = new Vector2(k, j);
							clPlayerDistX = Math.abs(vVs[3].X - mPos.X);
							clPlayerDistY = Math.abs(vVs[3].Y - mPos.Y);
						}
					}
				}
			}
			
			// assign map data to FIS variables
			String cDir;
			for(int i = 0; i < VARIABLE_COUNT; ++i)
			{
				if(vVs[i] != null)
				{
					if(mPos.Y - vVs[i].Y > 0 && mPos.X - vVs[i].X <= 0)
					{
						fis.setVariable(variableLabels[i] + "Dir", DIR_UP);
					}
					else if(mPos.Y - vVs[i].Y < 0 && mPos.X - vVs[i].X >= 0)
					{
						fis.setVariable(variableLabels[i] + "Dir", DIR_DOWN);
					}
					else if(mPos.Y - vVs[i].Y >= 0 && mPos.X - vVs[i].X > 0)
					{
						fis.setVariable(variableLabels[i] + "Dir", DIR_LEFT);
					}
					else if(mPos.Y - vVs[i].Y <= 0 && mPos.X - vVs[i].X < 0)
					{
						fis.setVariable(variableLabels[i] + "Dir", DIR_RIGHT);
					}
				}
				else
				{
					fis.setVariable(variableLabels[i] + "Dir", -1);
				}
			}
			
			fis.setVariable(variableLabels[0] + "Dist", clFoodDist);
			fis.setVariable(variableLabels[1] + "Dist", clWoodDist);
			fis.setVariable(variableLabels[2] + "Dist", clCorpseDist);
			fis.setVariable(variableLabels[3] + "DistX", clPlayerDistX);
			fis.setVariable(variableLabels[3] + "DistY", clPlayerDistY);
			
			// action!
			fis.evaluate();
			
			// get data from FIS
			
			String valStr;
			dataHelper.clear();
			double max = Double.MIN_VALUE;
			int maxID;
			for(int i = 0; i < DATA_SLOTS; ++i)
			{
				Variable var = fis.getVariable("slot" + dataLabels[i]);
				dataArray[i] = var.defuzzify();
				if(dataArray[i] > max)
				{
					dataHelper.clear();
					max = dataArray[i];
					maxID = i;
					dataHelper.add(i);
				}
				else if(dataArray[i] == max)
				{
					dataHelper.add(i);
				}
			}
			
			if(dataHelper.size() > 1)
			{
				// calculate rand
				int id = randomGenerator.nextInt(dataHelper.size());
				id = dataHelper.get(id);
				dataHelper.clear();
				dataHelper.add(id);
			}
			
			maxID = dataHelper.get(0);
			
			
			// make action according to the data
			
			if(dataArray[maxID] >= 0)
			{
				if(maxID == 0)
				{
					if(CanGo(new Vector2(mPos.X, mPos.Y - 1)))
					{
						Broker.Action(ActionType.MOVE, new Vector2(0, -1));
						prevPositions.add(new Vector2(mPos.X, mPos.Y - 1));
					}
					else
					{
						Broker.Action(ActionType.MOVE, new Vector2(0, 0));
					}
				}
				else if(maxID == 1)
				{
					if(CanGo(new Vector2(mPos.X, mPos.Y + 1)))
					{
						Broker.Action(ActionType.MOVE, new Vector2(0, 1));
						prevPositions.add(new Vector2(mPos.X, mPos.Y + 1));
					}
					else
					{
						Broker.Action(ActionType.MOVE, new Vector2(0, 0));
					}
				}
				else if(maxID == 2)
				{
					if(CanGo(new Vector2(mPos.X - 1, mPos.Y)))
					{
						Broker.Action(ActionType.MOVE, new Vector2(-1, 0));
						prevPositions.add(new Vector2(mPos.X - 1, mPos.Y));
					}
					else
					{
						Broker.Action(ActionType.MOVE, new Vector2(0, 0));
					}
				}
				else if(maxID == 3)
				{
					if(CanGo(new Vector2(mPos.X + 1, mPos.Y)))
					{
						Broker.Action(ActionType.MOVE, new Vector2(1, 0));
						prevPositions.add(new Vector2(mPos.X + 1, mPos.Y));
					}
					else
					{
						Broker.Action(ActionType.MOVE, new Vector2(0, 0));
					}
				}
				else if(maxID == 4)	// kindle bonfire
				{
					Broker.Action(ActionType.KINDLE_FIRE, Broker.GetMyPosition());
				}
				else if(maxID == 5)	// set trap where u stand
				{
					Broker.Action(ActionType.SET_TRAP, new Vector2(0, 0));
				}
				else if(maxID == 6)	// throw spear
				{
					Broker.Action(ActionType.THROW_SPEAR, vVs[3]);
				}
				else if(maxID == 7) // wait
				{
					Broker.Action(ActionType.MOVE, new Vector2(0, 0));
				}
				else if(maxID == 8) // walk in random direction
				{
					Boolean[] cans = new Boolean[4];
					
					// first check if we can even go in any direction
					cans[0] = CanGo(new Vector2(mPos.X, mPos.Y - 1));
					cans[1] = CanGo(new Vector2(mPos.X, mPos.Y + 1));
					cans[2] = CanGo(new Vector2(mPos.X - 1, mPos.Y));
					cans[3] = CanGo(new Vector2(mPos.X + 1, mPos.Y));
					
					if(!cans[0] && !cans[1] && !cans[2] && !cans[3])
					{
						Broker.Action(ActionType.MOVE, new Vector2(0, 0));
					}
					else
					{
						while(true)
						{
							int rand = randomGenerator.nextInt(4);
							if(cans[rand])
							{
								if(rand == 1)
								{
									Broker.Action(ActionType.MOVE, new Vector2(0, 1));
									prevPositions.add(new Vector2(mPos.X, mPos.Y + 1));
								}
								else if(rand == 2)
								{
									Broker.Action(ActionType.MOVE, new Vector2(-1, 0));
									prevPositions.add(new Vector2(mPos.X - 1, mPos.Y));
								}
								else if(rand == 3)
								{
									Broker.Action(ActionType.MOVE, new Vector2(1, 0));
									prevPositions.add(new Vector2(mPos.X + 1, mPos.Y));
								}
								else
								{
									Broker.Action(ActionType.MOVE, new Vector2(0, -1));
									prevPositions.add(new Vector2(mPos.X, mPos.Y - 1));
								}
								break;
							}
						}
					}
				}
				else
				{
					System.out.println("MarcinFuzzy: ERROR: GOT TRASH FROM JFUZZY: " + String.valueOf(maxID));
					return;
				}
				valStr = dataLabels[maxID];
			}
			else
			{
				Broker.Action(ActionType.MOVE, new Vector2(0, 0));
				valStr = dataLabels[7];
			}
			
			actList.add(valStr);
		}
		
		String tmpAct = "";
		for(String str : actList)
		{
			tmpAct += str + ";";
		}
		
		if(Broker.GetMyHP() > 0)
		{
			// so we live to see another day!
			++tl;
		}
		
//		System.out.println("MarcinFuzzy | " + 
//				"POS=" + Broker.GetMyPosition().toString() + " | " +
//				"TL=" + String.valueOf(tl) + " | " +
//				"HP=" + String.valueOf(Broker.GetMyHP()) + " | " +
//				"PP=" + String.valueOf(Broker.GetMyPP()) + " | " +
//				"AP=" + String.valueOf(Broker.GetMyAP()) + " | " +
//				"WP=" + String.valueOf(Broker.GetMyWP()) + " | " +
//				"ACTS=" + String.valueOf(tmpAct)
//				);
	}
	
	private Boolean CanGo(Vector2 fieldPos)
	{
		if(fieldPos.X >= 0 &&
			fieldPos.X < 50 &&
			fieldPos.Y >= 0 &&
			fieldPos.Y < 50)
		{
			int ap = Broker.GetMyAP();
			String ft = Broker.GetFieldType(fieldPos.X, fieldPos.Y);
			if((ft.equals(FieldType.FOOD.toString()) &&
					ap < GameController.GetCostFoodMove()) ||
				(ft.equals(FieldType.WOOD.toString()) &&
					ap < GameController.GetCostWoodMove()) ||
				(ft.equals(FieldType.CORPSE.toString()) &&
					ap < GameController.GetCostEatCorpse()) ||
					(ft.equals(FieldType.ENEMY.toString())))
			{
				return false;
			}
			
			int s = prevPositions.size();
			for(int i = 0; i < s; ++i)
			{
				Vector2 prev = prevPositions.get(i);
				if(prev.X == fieldPos.X &&
					prev.Y == fieldPos.Y)
				{
					return false;
				}
			}
			return true;
		}
		return false;
	}

}
