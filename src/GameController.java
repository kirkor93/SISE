public class GameController 
{
	public Map MyMap;
	
//	private int _initActionPoints = 5;
//	private int _initHitPoints = 15;
//	private int _initPsychicalPoints = 15;

	
    private final int _normalMoveCost = 1;
    private final int _foodMoveCost = 3;
    private final int _foodMoveHpRegen = 10;
    private final int _woodMoveCost = 3;
    private final int _woodCollectedOnMove = 1;
    private final int _kindleFireActionPointsCost = 3;
    private final int _kindleFireWoodCost = 1;
    private final int _setTrapActionPointsCost = 3;
    private final int _setTrapWoodCost = 1;
    private final int _throwSpearActionPointsCost = 5;
    private final int _throwSpearWoodCost = 1;
    private final int _eatCorposeActionPointsCost = 3;
    private final int _eatCorposePsychicalCost = 3;
    private final int _eatCorposeHpRegen = 10;
    private final int _trapEnterDamage = 8;
    private final int _spearInHeadDamage = 8;
    
    private Map _map = null;
    private int _activePlayer = 0;
    private Player[] _players = null;
    
    GameController(int[] names)
    {
    	_map = new Map();
    	_players = new Player[4];
    	Vector2[] positions = {new Vector2(0,0),new Vector2(0,49), new Vector2(49,0), new Vector2(49,49)};
    	for(int i=0; i<4; ++i)
    	{
    		_players[i] = new Player(positions[i]);
    		switch(names[i])
    		{
    			case 1:
    				_players[i].Bot = new MajsterBot();
    				break;
    			case 2:
    				_players[i].Bot = new StengerdtBot();
    				break;
    			case 3:
    				_players[i].Bot = new MarcinBot();
    				break;
    			case 4:
    				_players[i].Bot = new SzczochBot();
    				break;
    			case 5:
    				_players[i].Bot = new JaniakBot();
    				break;
    			case 6: 
    				_players[i].Bot = new PatrykBot();
    				break;
				default:
					System.out.println("Retard Alert!!!!");
					_players[i].Bot = new JaniakBot();
					break;    				
    		}   		
    	}
    	
    	
    	
    }
    
	
}
