import net.sourceforge.jFuzzyLogic.FIS;

public class SzczechBotFuzzy extends Bot {

	private int _fieldOfView = 15;
	
	public SzczechBotFuzzy() {
		MySymbol = "R";
		fis = FIS.load("src/SzczechFuzzy.fcl");
	}
	
	@Override
	public void Play() {
		
		boolean actionPerformed = true;	
		
		while (Broker.GetMyAP() > 0
				&& Broker.GetMyHP() > 0
				&& actionPerformed) {
			
			int foodDistance = Integer.MAX_VALUE;
			Vector2 foodPosition = new Vector2();
			int corpseDistance = Integer.MAX_VALUE;
			Vector2 corpsePosition = new Vector2();
			int woodDistance = Integer.MAX_VALUE;
			Vector2 woodPosition = new Vector2();
			int enemyDistance = Integer.MAX_VALUE;
			Vector2 enemyPosition = new Vector2();
			
			Vector2 pos = Broker.GetMyPosition();
			
			for(int i = -_fieldOfView ; i < _fieldOfView ; i++){
				for(int j = -_fieldOfView ; j < _fieldOfView ; j++){
					
					Vector2 checkedPosition = new Vector2(pos.X + i, pos.Y + j);
					if(!checkBoundries(checkedPosition))
					{
						continue;
					}
					
					int dist = getDistance(pos, checkedPosition);
					String fieldType = Broker.GetFieldType(checkedPosition.X, checkedPosition.Y);
					
					switch(fieldType)
					{
					case "FOOD":
						if(dist < foodDistance)
						{
							foodDistance = dist;
							foodPosition = checkedPosition;
						}	
						break;
					case "CORPSE":
						if(dist < corpseDistance)
						{
							corpseDistance = dist;
							corpsePosition = checkedPosition;
						}
						break;
					case "WOOD":
						if(dist < woodDistance)
						{
							woodDistance = dist;
							woodPosition = checkedPosition;
						}
						break;
					case "ENEMY":
						if(dist < enemyDistance)
						{
							if(checkedPosition.X != pos.X && checkedPosition.Y != pos.Y)
							{
								enemyDistance = dist;
								enemyPosition = checkedPosition;
							}
						}
						break;
					}
				}
			}
			
			if(foodDistance == Integer.MAX_VALUE){
				foodDistance = -1;
			}
			if(corpseDistance == Integer.MAX_VALUE){
				corpseDistance = -1;
			}
			if(woodDistance == Integer.MAX_VALUE){
				woodDistance = -1;
			}
			if(enemyDistance == Integer.MAX_VALUE){
				enemyDistance = -1;
			}
			
			fis.setVariable("botHP", Broker.GetMyHP());
			fis.setVariable("botWP", Broker.GetMyWP());
			fis.setVariable("botAP", Broker.GetMyAP());
			fis.setVariable("botPP", Broker.GetMyPP());
			fis.setVariable("foodDistance", foodDistance);
			fis.setVariable("corpseDistance", corpseDistance);
			fis.setVariable("woodDistance", woodDistance);
			fis.setVariable("enemyDistance", enemyDistance);
			
			//do the magic!
			fis.evaluate();
			
			double[] output = new double[]{
					fis.getVariable("eatFood").getLatestDefuzzifiedValue(),
					fis.getVariable("eatCorpse").getLatestDefuzzifiedValue(),
					fis.getVariable("collectWood").getLatestDefuzzifiedValue(),
					fis.getVariable("throwSpear").getLatestDefuzzifiedValue(),
					fis.getVariable("kindleFire").getLatestDefuzzifiedValue()
			};			
			
			switch (getHighestIndex(output)) {
			case 0:
				actionPerformed = Broker.Action(ActionType.MOVE, getMovementDirection(pos, foodPosition));
//				System.out.println("Moved towards food");
				break;
			case 1:
				actionPerformed = Broker.Action(ActionType.MOVE, getMovementDirection(pos, corpsePosition));
//				System.out.println("Moved towards corpse");
				break;
			case 2:
				actionPerformed = Broker.Action(ActionType.MOVE, getMovementDirection(pos, woodPosition));
//				System.out.println("Moved towards wood");
				break;
			case 3:
				actionPerformed = Broker.Action(ActionType.THROW_SPEAR, getMovementDirection(pos, enemyPosition));
//				System.out.println("Spear thrown");
				break;
			case 4:
				actionPerformed = Broker.Action(ActionType.KINDLE_FIRE, getMovementDirection(pos, null));
//				System.out.println("Fire kindled");
				break;				
			default:
				return;
			}
		}
	}
	
	public static int getHighestIndex(double[] input){
		double maxValue = Double.MIN_VALUE;
		int index = -1;
		for (int i = 0 ; i < input.length ; i++){
			if(input[i] > maxValue){
				maxValue = input[i];
				index = i;
			}
		}
		return index;
	}
	
	public static boolean checkBoundries(Vector2 pos)
	{
		return (pos.X >= 0 && pos.Y >= 0 && pos.X <=49 && pos.Y <= 49);
	}
	
	public static int getDistance(Vector2 pos1, Vector2 pos2)
	{			
		return (Math.abs(pos1.X - pos2.X)) + (Math.abs(pos1.Y - pos2.Y));
	}
	
	public static Vector2 getMovementDirection(Vector2 position, Vector2 targetPosition){
		if(position == null
			|| targetPosition == null){
			return new Vector2(0, 0);
		}
		
		Vector2 dir = new Vector2(targetPosition.X - position.X, targetPosition.Y - position.Y);		
		Vector2 output = new Vector2(Math.round(Math.signum(dir.X)), Math.round(Math.signum(dir.Y)));
		
		if(Math.abs(dir.X) > Math.abs(dir.Y)){
			output.Y = 0;
		}
		else {
			output.X = 0;
		}
		
		return output;
	}

}