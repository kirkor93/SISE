import java.util.Random;
import java.math.*;

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
    				_players[i].Bot = new MajsterBot(_players[i]);
    				break;
    			case 2:
    				_players[i].Bot = new StengerdtBot(_players[i]);
    				break;
    			case 3:
    				_players[i].Bot = new MarcinBot(_players[i]);
    				break;
    			case 4:
    				_players[i].Bot = new SzczochBot(_players[i]);
    				break;
    			case 5:
    				_players[i].Bot = new JaniakBot(_players[i]);
    				break;
    			case 6: 
    				_players[i].Bot = new PatrykBot(_players[i]);
    				break;
				default:
					System.out.println("Retard Alert!!!!");
					_players[i].Bot = new JaniakBot(_players[i]);
					break;    				
    		}   		
    	}		
    	_map.SetGameController(this);
    	System.out.println(_map.toString());
    }
    
    public void Run()
    {
    	int alivePlayers = 4;
    	while(alivePlayers > 0)
    	{
    		if(_players[_activePlayer].HP > 0)
    		{
    			_players[_activePlayer].Bot.Play();
    			_players[_activePlayer].AP = _initActionPoints;
    			++_activePlayer;
    			_activePlayer = _activePlayer % 4;
    			if(_players[_activePlayer].HP <= 0) --alivePlayers;
    		} 		
    	}
    }
    
    public Player[] GetPlayers()
    {
    	return _players;
    }
    
    
    public boolean Move(ActionDirection direction)
    {
        Vector2 move = new Vector2();
        switch(direction)
        {
            case UP:
                move.Y = -1;
                break;
            case DOWN:
                move.Y = 1;
                break;
            case LEFT:
                move.X = -1;
                break;
            case RIGHT:
                move.X = 1;
                break;
                
        }

        move.Add(_players[_activePlayer].Position);
        if (move.Y < 0 || move.X > 49)
            return false;
        if (move.Y < 0 || move.X > 49)
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

    public boolean SetTrap(ActionDirection direction)
    {
        Vector2 move = new Vector2(0, 0);
        switch (direction)
        {
            case UP:
                move.Y = 1;
                break;
            case DOWN:
                move.Y = -1;
                break;
            case LEFT:
                move.X = -1;
                break;
            case RIGHT:
                move.X += 1;
                break;

        }
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

	
}
