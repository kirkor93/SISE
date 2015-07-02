import CLIPSJNI.Environment;
import CLIPSJNI.SymbolValue;


public class JaniakBot extends Bot
{
	private static int vision = 0;
	private static int turns = -1;
	private String moves = ""; 
	private boolean flag = true;
	
	public JaniakBot()
	{
		this.MySymbol = "J";
		this.Clips = new Environment();
		this.Clips.load("src/JaniakClips.clp");
		this.Clips.reset();
		this.Clips.run();
	}
	
	@Override
	public void Play() {
		// TODO Auto-generated method stub
		

		if(Broker.GetMyHP() <= 0 || Broker.GetMyPP() <=0)
		{
			return;
		}
		
		while (Broker.GetMyAP() > 0 && Broker.GetMyHP() > 0 && Broker.GetMyPP() > 0)
		{
			Clips.assertString("(bot" +  "(x " + String.valueOf(Broker.GetMyPosition().X) + ")"
					+ "(y " + String.valueOf(Broker.GetMyPosition().Y) + ")"
					+ "(HP " + Broker.GetMyHP() + ")" + "(PP " + Broker.GetMyPP() + ")" 
					+ "(AP " + Broker.GetMyAP() + ")" + "(WP " + Broker.GetMyWP() + "))"
					);
			
			while (flag){
			vision++;
			
			this.Clips.eval("(do-for-all-facts ((?f tile)) TRUE (retract ?f))");
			this.Clips.eval("(do-for-fact ((?b bot)) (retract ?b))");
			
				for (int i = -vision; i <= vision; i++)
				{
					for (int j = -vision; j <= vision ; j++)
					{
						if(Broker.GetMyPosition().X + i < 50 && Broker.GetMyPosition().X + i >= 0 &&
							Broker.GetMyPosition().Y + j < 50 && Broker.GetMyPosition().Y + j >= 0)
						{
							if((i + j) != 0)
							{
								Clips.assertString("(tile (fx " + (Broker.GetMyPosition().X + i) + ")" + 
										"(fy " + (Broker.GetMyPosition().Y + j) + ")" + "(type neighbour)" + 
										"(fieldType " + Broker.GetFieldType((Broker.GetMyPosition().X+i), 
										(Broker.GetMyPosition().Y+j)) + "))");
							}
						
						if (i != 0 && j != 0 && (Broker.GetFieldType((Broker.GetMyPosition().X+i), 
								(Broker.GetMyPosition().Y+j)) != "NORMAL"))
						{
							flag = false;
						}
						}
					}
				}
			}
			
		
			Clips.run();
		
			String evalStr = "?*action*";
			SymbolValue sv = (SymbolValue) this.Clips.eval(evalStr);
			String currentaction = sv.stringValue();

			switch(currentaction)
			{
			case "random":
				Randomize();
				break;
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
			case "wait":
				Broker.Action(ActionType.MOVE, new Vector2(0, 0));
				break;
			case "kindle":
				Broker.Action(ActionType.KINDLE_FIRE, null);
				break;
			case "attack":
				int outputX = Integer.parseInt(this.Clips.eval("?*enemyX*").toString());
				int outputY = Integer.parseInt(this.Clips.eval("?*enemyY*").toString());
				Broker.Action(ActionType.THROW_SPEAR, new Vector2(outputX, outputY));
				break;
			default:
				break;
			}
			this.Clips.eval("(bind ?*action*)");
			
			moves += currentaction + ";";
		}
		
		turns++;
		flag = true;
		vision = 0;
		//moves = "";
		//System.out.println(truns + "" + moves);
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