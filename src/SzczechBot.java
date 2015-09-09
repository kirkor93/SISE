import CLIPSJNI.Environment;

public class SzczechBot extends Bot {
	private int _fieldOfView = 15;
	private Vector2 _previousCoordinates = new Vector2(0, 0);
	private String _previousAction = "Move";
	
	public SzczechBot()	{
		MySymbol = "R";
		Clips = new Environment();
		Clips.clear();
		Clips.load("src/SzczechClips.clp");
		Clips.reset();
	}
	
	@Override
	public void Play() {
		
		boolean actionPerformed = true;
		
		while (Broker.GetMyAP() > 0
				&& Broker.GetMyHP() > 0
				&& actionPerformed) {
			
			Clips.reset();
			
			Clips.assertString(String.format("(previousMove (x %s) (y %s) (move %s))", 
					String.valueOf(_previousCoordinates.X), String.valueOf(_previousCoordinates.Y), 
					_previousAction));
			
			//giving Clips some informations about current player and map state before starting it
			Vector2 pos = Broker.GetMyPosition();			
			Clips.assertString(String.format("(player (HP %s) (PP %s) (AP %s) (WP %s) (X %s) (Y %s))",
					Broker.GetMyHP(), Broker.GetMyPP(), Broker.GetMyAP(), Broker.GetMyWP(),
					String.valueOf(pos.X), String.valueOf(pos.Y)));			
			
			for(int i = 0 ; i < _fieldOfView ; i ++){
				for(int j = 0 ; j < _fieldOfView ; j ++){
					Vector2 checkedPosition = new Vector2(pos.X + i, pos.Y + j);
					if(SzczechBotFuzzy.checkBoundries(checkedPosition)){
						Clips.assertString(String.format("(mapSlot (x %s) (y %s) (fieldType %s))", 
								checkedPosition.X, checkedPosition.Y, 
								Broker.GetFieldType(checkedPosition.X,  checkedPosition.Y)));
					}
					checkedPosition = new Vector2(pos.X - i - 1, pos.Y - j - 1);
					if(SzczechBotFuzzy.checkBoundries(checkedPosition)){
						Clips.assertString(String.format("(mapSlot (x %s) (y %s) (fieldType %s))", 
								checkedPosition.X, checkedPosition.Y, 
								Broker.GetFieldType(checkedPosition.X,  checkedPosition.Y)));
					}
				}
			}
			
			String[] slots = new String[] { "WOOD", "FOOD", "CORPSE", "ENEMY" };
			for (String str : slots) {
				Clips.assertString(String.format("(closestSlot (x -1) (y -1) (fieldType %s) (distance 10000))", str));
			}
			
			Clips.run();
			//getting results from clips
			String selectedAction = Clips.eval("?*selectedAction*").toString();
//			System.out.println(selectedAction);
			int x = Integer.parseInt(Clips.eval("?*selectedX*").toString());
			int y = Integer.parseInt(Clips.eval("?*selectedY*").toString());			
			
			//small check (if new coordinates are inside the map)
			x = x < 0 ? 1 : x;
			y = y < 0 ? 1 : y;
			x = x > 49 ? 48 : x;
			y = y > 49 ? 48 : y;
			
			//performing an action
			switch (selectedAction) {
			case "Wait":
				actionPerformed = Broker.Action(ActionType.MOVE, new Vector2(0, 0));
				break;
			case "KindleFire":
				actionPerformed = Broker.Action(ActionType.KINDLE_FIRE, null);
				break;
			case "ThrowSpear":
				actionPerformed = Broker.Action(ActionType.THROW_SPEAR, new Vector2(x, y));
				break;
			case "Move":
				actionPerformed = Broker.Action(ActionType.MOVE, SzczechBotFuzzy.getMovementDirection(pos, new Vector2(x, y)));
				break;
			case "MoveRandomly":
				actionPerformed = Broker.Action(ActionType.MOVE, getRandomDirection(pos));
				break;
			default:
				return;
			}
			
			_previousCoordinates = Broker.GetMyPosition();
			_previousAction = selectedAction;
		}
	}
	
	public static Vector2 getRandomDirection(Vector2 position){
		
		Vector2 direction = null;
		do{
			direction = new Vector2(position);
			double rand = Math.random();
			
			if(rand < 0.25f){
				direction.X++;
			}
			else if(rand < 0.5f){
				direction.X--;
			}
			else if(rand < 0.75f){
				direction.Y++;
			}
			else{
				direction.Y--;
			}
		}
		while(!SzczechBotFuzzy.checkBoundries(direction));
		
		return direction;
	}
}


