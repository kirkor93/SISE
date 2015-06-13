import java.io.IOException;
import java.util.Random;

public class GameController 
{	
	private int _initActionPoints = 5;
	private int _initHitPoints = 15;
	private int _initPsychicalPoints = 15;

	
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
    	Random generator = new Random();
    	int mapRand = generator.nextInt(7);
    	switch(mapRand)
    	{
	    	case 0:
	        	_map = new Map(MapBMP.counted);
	    		break;
	    	case 1:
	        	_map = new Map(MapBMP.fight);
	    		break;
	    	case 2:
	        	_map = new Map(MapBMP.fight2);
	    		break;
	    	case 3:
	    		_map = new Map(MapBMP.groups);
	    		break;
	    	case 4:
	    		_map = new Map(MapBMP.middle);
	    		break;
	    	case 5: 
	    		_map = new Map(MapBMP.random);
	    		break;
	    	case 6: 
	    		_map = new Map(MapBMP.random2);
	    		break;
    		default:
    			_map = new Map(MapBMP.counted);
    			break;
    	}
    	_map.LoadMap();
    	_players = new Player[4];
    	Vector2[] positions = {new Vector2(0,0),new Vector2(0,49), new Vector2(49,0), new Vector2(49,49)};
    	for(int i=0; i<4; ++i)
    	{
    		_players[i] = new Player(positions[i]);
    		switch(names[i])
    		{
    			case 1:
    				_players[i].MyBot = new MajsterBot();
    				_players[i].MySymbol = _players[i].MyBot.MySymbol;
    				break;
    			case 2:
    				_players[i].MyBot = new StengerdtBot();
    				_players[i].MySymbol = _players[i].MyBot.MySymbol;
    				break;
    			case 3:
    				_players[i].MyBot = new MarcinBot();
    				_players[i].MySymbol = _players[i].MyBot.MySymbol;
    				break;
    			case 4:
    				_players[i].MyBot = new SzczochBot();
    				_players[i].MySymbol = _players[i].MyBot.MySymbol;
    				break;
    			case 5:
    				_players[i].MyBot = new JaniakBot();
    				_players[i].MySymbol = _players[i].MyBot.MySymbol;
    				break;
    			case 6: 
    				_players[i].MyBot = new PatrykBot();
    				_players[i].MySymbol = _players[i].MyBot.MySymbol;
    				break;
				default:
					System.out.println("Retard Alert!!!!");
					_players[i].MyBot = new JaniakBot();
					_players[i].MySymbol = _players[i].MyBot.MySymbol;
					break;    				
    		}   		
    	}		
    	_map.SetGameController(this);
    }
    
    public void Run()
    {
    	int alivePlayers = 4;
    	while(alivePlayers > 0)
    	{
    		if(_players[_activePlayer].HP > 0)
    		{

        		if(_activePlayer == 0)
        		{
        			System.out.println();
        			System.out.println();
        			System.out.println(_map);
        		}
    			_players[_activePlayer].HP -= 1;
    			_players[_activePlayer].PP -= 1;
    			_players[_activePlayer].MyBot.Play();
    			_players[_activePlayer].AP = _initActionPoints;
    			if(_players[_activePlayer].HP <= 0) 
    			{
    				--alivePlayers;
    			}
    		}
			++_activePlayer;
			_activePlayer = _activePlayer % 4;
    	}
    }
    
    public void SetActionBrokerOnBots(ActionBroker ab)
    {
    	for(int i=0; i<4 ;++i)
    	{
    		_players[i].MyBot.Broker = ab;
    	}
    }
    
	public boolean Action(ActionType type, Vector2 vec)
	{
		switch(type)
		{
		case MOVE:
			return Move(vec);
		case SET_TRAP:
			return SetTrap(vec);
		case THROW_SPEAR:
			return ThrowSpear(vec);
		case KINDLE_FIRE:
			return KindleFire();
			default:
				return false;
		}
	}
    
    
    public Player[] GetPlayers()
    {
    	return _players;
    }
    
    
    public boolean Move(Vector2 vec)
    {
    	if((Math.abs(vec.X) >1 ) || (Math.abs(vec.Y) > 1)) return false;
    	if((Math.abs(vec.X) == 1) && (Math.abs(vec.Y)==1)) return false;
    	Vector2 move = vec;
        move.Add(_players[_activePlayer].Position);
        if (move.Y < 0 || move.Y > 49)
            return false;
        if (move.X < 0 || move.X > 49)
            return false;

        switch(_map.MyFields[move.X][move.Y].MyFieldType)
        {
            case NORMAL:
                if (_players[_activePlayer].AP >= _normalMoveCost)
                {
                    StepOnTrap(move);
                    _players[_activePlayer].AP -= _normalMoveCost;
                    _players[_activePlayer].Position = move;
                    return true;
                }
                else
                {
                    return false;
                }
            case FOOD:
                if (_players[_activePlayer].AP >= _foodMoveCost)
                {
                    StepOnTrap(move);
                    _players[_activePlayer].AP -= _foodMoveCost;
                    Heal(_foodMoveHpRegen);
                    _map.MyFields[move.X][move.Y].MyFieldType = FieldType.NORMAL;
                    _players[_activePlayer].Position = move;
                    return true;
                }
                else
                {
                    return false;
                }
            case CORPSE:
                if (_players[_activePlayer].AP >= _eatCorposeActionPointsCost)
                {
                    StepOnTrap(move);
                    _players[_activePlayer].AP -= _eatCorposeActionPointsCost;
                    Heal(_eatCorposeHpRegen);
                    GetMentalDmg(_eatCorposePsychicalCost);
                    _map.MyFields[move.X][move.Y].MyFieldType = FieldType.NORMAL;
                    _players[_activePlayer].Position = move;
                    return true;
                }
                else
                {
                    return false;
                }
            case WOOD:
                if (_players[_activePlayer].AP >= _woodMoveCost)
                {
                    StepOnTrap(move);
                    _players[_activePlayer].AP -= _woodMoveCost;
                    _players[_activePlayer].WP += _woodCollectedOnMove;
                    _map.MyFields[move.X][move.Y].MyFieldType = FieldType.NORMAL;
                    _players[_activePlayer].Position = move;
                    return true;
                }
                else
                {
                    return false;
                }
            default:
                return false;
        }
    }

    private void StepOnTrap(Vector2 move)
    {
        if (_map.MyFields[move.X][move.Y].IsTrap)
        {
            GetPhysicalDmg(_trapEnterDamage);
            _map.MyFields[move.X][move.Y].IsTrap = false;
            if (_players[_activePlayer].HP <= 0) _players[_activePlayer].AP  = 0;
        }
    }

    public boolean KindleFire()
    {
        if (_players[_activePlayer].WP >= _kindleFireWoodCost
            && _players[_activePlayer].AP >= _kindleFireActionPointsCost)
        {
        	_players[_activePlayer].WP -= _kindleFireWoodCost;
        	_players[_activePlayer].AP -= _kindleFireActionPointsCost;
        	_players[_activePlayer].PP = _initPsychicalPoints;
        	_players[_activePlayer].HP = _initHitPoints;
            return true;
        }

        return false;
    }

    public boolean SetTrap(Vector2 vec)
    {
    	if((Math.abs(vec.X) >1 ) || (Math.abs(vec.Y) > 1)) return false;
    	if((Math.abs(vec.X) == 1) && (Math.abs(vec.Y)==1)) return false;
    	Vector2 move = vec;
        move.Add(_players[_activePlayer].Position);
        if (move.Y < 0 || move.X > 49 || move.Y < 0 || move.X > 49 || _players[_activePlayer].AP < _setTrapActionPointsCost 
        		|| _players[_activePlayer].WP < _setTrapWoodCost) return false;
        _players[_activePlayer].AP -= _setTrapActionPointsCost;
        _players[_activePlayer].WP -= _setTrapWoodCost;
        _map.MyFields[move.X][move.Y].IsTrap = true;
        return true;
    }

    public boolean ThrowSpear(Vector2 target)
    {
        if( _players[_activePlayer].AP >= _throwSpearActionPointsCost)
        {
            for(int i=0; i<4 ;i++)
            {
                if(target == _players[i].Position && InRange(target))
                {
                	_players[_activePlayer].HP -= _spearInHeadDamage;
                    break;
                }
            }
            _players[_activePlayer].AP -= _throwSpearActionPointsCost;
            _players[_activePlayer].WP -= _throwSpearWoodCost;
            return true;
        }
        return false;
    }

    private boolean InRange(Vector2 target)
    {
        return ((Math.abs(_players[_activePlayer].Position.X - target.X) < 4) && (Math.abs(_players[_activePlayer].Position.Y - target.Y) < 4));
    }

    private void Heal(int value)
    {
    	_players[_activePlayer].HP += value;
    }

    private void GetMentalDmg(int value)
    {
    	_players[_activePlayer].PP -= value;
    }

    private void GetPhysicalDmg(int value)
    {
    	_players[_activePlayer].HP -= value;
    }

	public int GetActivePlayerHP()
	{
		return _players[_activePlayer].HP;
	}
	
	public int GetActivePlayerPP()
	{
		return _players[_activePlayer].PP;
	}
	
	public int GetActivePlayerWP()
	{
		return _players[_activePlayer].WP;
	}
	
	public int GetActivePlayerAP()
	{
		return _players[_activePlayer].AP;
	}
	
	public Vector2 GetActivePlayerPosition()
	{
		return _players[_activePlayer].Position;
	}
}
