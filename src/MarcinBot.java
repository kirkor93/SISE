import CLIPSJNI.Environment;


public class MarcinBot extends Bot
{
	private static final int FIELD_OF_VIEW = 3;
	
	public MarcinBot()
	{
		this.MySymbol = "Q";
		this.Clips = new Environment();
		this.Clips.load("src/MarcinCLIPS.clp");
		this.Clips.reset();
	}
	
	@Override
	public void Play() {
		
		// clear CLIPS assert data
		Clips.eval("(do-for-all-facts "
						+ "((?f tile)) TRUE "
						+ "(retract ?f))");
//		Clips.eval("(do-for-fact "
//						+ "((?b bot)) "
//						+ "(eq ?b:state current) "
//						+ "(retract ?b))");
		
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
				+ "(WP " + Broker.GetMyWP() + ")" 
				+ "(state current)"
				+ "(posX " + String.valueOf(myPos.X) + ")"
				+ "(posY " + String.valueOf(myPos.Y) + ")"
				+ "(currentField " + Broker.GetFieldType(myPos.X, myPos.Y) + ")"
				+ ")");
		
		// upload others positions
		
		// RUN!
		Clips.run();
		
		// retrieve decision from the CLIPS and proceed accordingly
		
		// cleanup
//		Clips.eval("(do-for-all-facts ((?b bot)) (eq ?b:state current) (modify ?b()))");
//		Clips.eval("(facts)");
		
		System.out.println("Marcin | " + 
				"POS=" + Broker.GetMyPosition().toString() + " | " +
				"CFLD=" + Broker.GetFieldType(Broker.GetMyPosition().X, Broker.GetMyPosition().Y).toString() + " | " +
				"HP=" + String.valueOf(Broker.GetMyHP()) + " | " +
				"PP=" + String.valueOf(Broker.GetMyPP()) + " | " +
				"AP=" + String.valueOf(Broker.GetMyAP()) + " | " +
				"WP=" + String.valueOf(Broker.GetMyWP())
				);
	}
}