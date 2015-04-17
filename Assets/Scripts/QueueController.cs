using System;
using UnityEngine;
using System.Collections;
using System.Linq;
using Random = UnityEngine.Random;

#region EnumForActionDirection
public enum ActionDirection
{
    Up = 1,
    Right,
    Down,
    Left
}
#endregion

public class QueueController : MonoBehaviour, IQueueController
{
    #region Variables
    public Bot[] ActivePlayers;
    public int InitActionPoints = 5;
    public int InitHitPoints = 15;
    public int InitPsychicalPoints = 15;
    public Map CurrentMap;
    public Action OnEndTurn;
    public MapUpdater MapUpdate;

    private const int _normalMoveCost = 1;
    private const int _foodMoveCost = 3;
    private const int _foodMoveHpRegen = 10;
    private const int _woodMoveCost = 3;
    private const int _woodCollectedOnMove = 1;
    private const int _kindleFireActionPointsCost = 3;
    private const int _kindleFireWoodCost = 1;
    private const int _setTrapActionPointsCost = 3;
    private const int _setTrapWoodCost = 1;
    private const int _throwSpearActionPointsCost = 5;
    private const int _throwSpearWoodCost = 1;
    private const int _eatCorposeActionPointsCost = 3;
    private const int _eatCorposePsychicalCost = 3;
    private const int _eatCorposeHpRegen = 10;
    private const int _trapEnterDamage = 8;
    private const int _spearInHeadDamage = 8;

    private int _activePlayer = 0;
    private int[] _playerActionPoints;
    private int[] _playerHitPoints;
    private int[] _playerPsychicalPoints;
    private int[] _playerWoodCount;
    public IntVector2[] _playerPosition;
    #endregion

    #region Properties

    public int NormalMoveCost
    {
        get { return _normalMoveCost; }
    }

    public int FoodMoveCost
    {
        get { return _foodMoveCost; }
    }

    public int FoodMoveHpRegen
    {
        get { return _foodMoveHpRegen; }
    }

    public int WoodMoveCost
    {
        get { return _woodMoveCost; }
    }

    public int WoodCollectedOnMove
    {
        get { return _woodCollectedOnMove; }
    }

    public int KindleFireActionPointsCost
    {
        get { return _kindleFireActionPointsCost; }
    }

    public int KindleFireWoodCost
    {
        get { return _kindleFireWoodCost; }
    }

    public int SetTrapActionPointsCost
    {
        get { return _setTrapActionPointsCost; }
    }

    public int SetTrapWoodCost
    {
        get { return _setTrapWoodCost; }
    }

    public int ThrowSpearActionPointsCost
    {
        get { return _throwSpearActionPointsCost; }
    }

    public int ThrowSpearWoodCost
    {
        get { return _throwSpearWoodCost; }
    }

    public int EatCorposeActionPointsCost
    {
        get { return _eatCorposeActionPointsCost; }
    }

    public int EatCorposePsychicalCost
    {
        get { return _eatCorposePsychicalCost; }
    }

    public int EatCorposeHpRegen
    {
        get { return _eatCorposeHpRegen; }
    }

    public int ActionPointsLeft
    {
        get { return _playerActionPoints[_activePlayer]; }
    }

    public int HitPoints
    {
        get { return _playerHitPoints[_activePlayer]; }
    }

    public int PsychicalCondition
    {
        get { return _playerPsychicalPoints[_activePlayer]; }
    }

    public int WoodCount
    {
        get { return _playerWoodCount[_activePlayer]; }
    }

    #endregion

    #region Methods
    #region MonoBehaviour
    protected void Start () 
    {
        _playerActionPoints  = new int[ActivePlayers.Length];
        _playerWoodCount = new int[ActivePlayers.Length];
        _playerHitPoints = new int[ActivePlayers.Length];
        _playerPsychicalPoints = new int[ActivePlayers.Length];
        _playerPosition = new IntVector2[ActivePlayers.Length];
	    for (int i = 0; i < _playerActionPoints.Length; i++)
	    {
	        _playerActionPoints[i] = InitActionPoints;
            _playerHitPoints[i] = InitHitPoints;
	        _playerPsychicalPoints[i] = InitPsychicalPoints;
            _playerWoodCount[i] = 0;
	        ActivePlayers[i].Controller = this;
	    }


        //shuffling Players queue
        if (ActivePlayers.Length > 1)
        {
            for (int i = 0; i < ActivePlayers.Length * 2; i++)
            {
                int a = Random.Range(0, ActivePlayers.Length);
                int b = a;
                while (b == a)
                {
                    b = Random.Range(0, ActivePlayers.Length);
                }

                Bot tmp = ActivePlayers[a];
                ActivePlayers[a] = ActivePlayers[b];
                ActivePlayers[b] = tmp;
                
            }
            for (int i = 0; i < ActivePlayers.Length; i++)
            {
                switch(i)
                {
                    case 0:
                        _playerPosition[i] = new IntVector2(0,0);
                        break;
                    case 1:
                        _playerPosition[i] = new IntVector2(0, 49);
                        break;
                    case 2:
                        _playerPosition[i] = new IntVector2(49, 0);
                        break;
                    case 3:
                        _playerPosition[i] = new IntVector2(49, 49);
                        break;
                }
            }
        }

        //starting game
        ActivePlayers[_activePlayer].Play();
    }

    void Update()
    {
        if(_activePlayer >= 0 && _activePlayer < ActivePlayers.Length)
        {
            ActivePlayers[_activePlayer].Play();
        }
    }

    #endregion

    #region IQueueController
    public bool Move(ActionDirection direction)
    {
        IntVector2 move = new IntVector2(0,0);
        switch(direction)
        {
            case ActionDirection.Up:
                move.y = -1;
                break;
            case ActionDirection.Down:
                move.y = 1;
                break;
            case ActionDirection.Left:
                move.x = -1;
                break;
            case ActionDirection.Right:
                move.x = 1;
                break;
                
        }

        move += _playerPosition[_activePlayer];
        if (move.x < 0 || move.x > 49)
            return false;
        if (move.y < 0 || move.y > 49)
            return false;

        switch(CurrentMap.GetCurrentMapState().MapArray[move.x,move.y].Type)
        {
            case FieldType.EMPTY:
                if (_playerActionPoints[_activePlayer] >= NormalMoveCost)
                {
                    StepOnTrap(move);
                    _playerActionPoints[_activePlayer] -= NormalMoveCost;
                    _playerPosition[_activePlayer] = move;
                    OnPlayerAction();
                    return true;
                }
                else
                {
                    return false;
                }
            case FieldType.FOOD:
                if (_playerActionPoints[_activePlayer] >= FoodMoveCost)
                {
                    StepOnTrap(move);
                    _playerActionPoints[_activePlayer] -= FoodMoveCost;
                    Heal(FoodMoveHpRegen);
                    MapUpdate.ChangeFieldOnMap(move, FieldType.EMPTY);
                    _playerPosition[_activePlayer] = move;
                    OnPlayerAction();
                    return true;
                }
                else
                {
                    return false;
                }
            case FieldType.CORPSE:
                if (_playerActionPoints[_activePlayer] >= EatCorposeActionPointsCost)
                {
                    StepOnTrap(move);
                    _playerActionPoints[_activePlayer] -= EatCorposeActionPointsCost;
                    Heal(EatCorposeHpRegen);
                    GetMentalDmg(_eatCorposePsychicalCost);
                    MapUpdate.ChangeFieldOnMap(move, FieldType.EMPTY);
                    _playerPosition[_activePlayer] = move;
                    OnPlayerAction();
                    return true;
                }
                else
                {
                    return false;
                }
            case FieldType.WOOD:
                if (_playerActionPoints[_activePlayer] >= WoodMoveCost)
                {
                    StepOnTrap(move);
                    _playerActionPoints[_activePlayer] -= WoodMoveCost;
                    _playerWoodCount[_activePlayer] += _kindleFireWoodCost;
                    MapUpdate.ChangeFieldOnMap(move, FieldType.EMPTY);
                    _playerPosition[_activePlayer] = move;
                    OnPlayerAction();
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

    private void StepOnTrap(IntVector2 move)
    {
        if (CurrentMap.GetCurrentMapState().MapArray[move.x, move.y].isTrapped)
        {
            GetPhysicalDmg(_trapEnterDamage);
            MapUpdate.ChangeFieldOnMap(move, FieldType.EMPTY);
            if (_playerHitPoints[_activePlayer] <= 0) EndTurn();
        }
    }

    public bool KindleFire()
    {
        if (_playerWoodCount[_activePlayer] >= _kindleFireWoodCost
            && _playerActionPoints[_activePlayer] >= _kindleFireActionPointsCost)
        {
            _playerWoodCount[_activePlayer] -= _kindleFireWoodCost;
            _playerActionPoints[_activePlayer] -= _kindleFireActionPointsCost;
            OnPlayerAction();
            return true;
        }

        return false;
    }

    public bool SetTrap(ActionDirection direction)
    {
        IntVector2 move = new IntVector2(0, 0);
        switch (direction)
        {
            case ActionDirection.Up:
                move.y = 1;
                break;
            case ActionDirection.Down:
                move.y = -1;
                break;
            case ActionDirection.Left:
                move.x = -1;
                break;
            case ActionDirection.Right:
                move.x += 1;
                break;

        }
        move += _playerPosition[_activePlayer];
        MapUpdate.SetTrapOnField(move);
        return false;
    }

    public bool ThrowSpear(IntVector2 target)
    {
        if(_playerActionPoints[_activePlayer] >= _throwSpearActionPointsCost)
        {
            for(int i=0;i<ActivePlayers.Length;i++)
            {
                if(target == _playerPosition[i] && InRange(target))
                {
                    _playerHitPoints[i] -= _spearInHeadDamage;
                    break;
                }
            }
            _playerActionPoints[_activePlayer] -= _throwSpearActionPointsCost;
            _playerWoodCount[_activePlayer] -= _throwSpearWoodCost;
            return true;
        }
        return false;
    }

    private bool InRange(IntVector2 target)
    {
        return ((Math.Abs(_playerPosition[_activePlayer].x - target.x) < 4) && (Math.Abs(_playerPosition[_activePlayer].y - target.y) < 4));
    }

    public MapState GetMapState()
    {
        MapState state = CurrentMap.GetCurrentMapState();
        for (int i = 0; i < Map.MAP_SIZE; i += 1)
        {
            for(int j = 0 ; j < Map.MAP_SIZE ; j += 1)
            {
                state.MapArray[i, j].isTrapped = false;
            }
        }
        return state;
    }

    public void EndTurn()
    {
        _playerActionPoints[_activePlayer] = InitActionPoints;
        _activePlayer += 1;
        _activePlayer %= ActivePlayers.Length;
        _playerHitPoints[_activePlayer] -= 1;
        _playerPsychicalPoints[_activePlayer] -= 1;
        int counter = 0;
        while (_playerHitPoints[_activePlayer] <= 0 || _playerPsychicalPoints[_activePlayer] <= 0)
        {
            _playerActionPoints[_activePlayer] = 0;
            _activePlayer += 1;
            _activePlayer %= ActivePlayers.Length;
            _playerHitPoints[_activePlayer] -= 1;
            _playerPsychicalPoints[_activePlayer] -= 1;
            ++counter;
            if (counter == ActivePlayers.Length)
            {
                break;
            }
        }

        //ActivePlayers[_activePlayer].Play();

        if(OnEndTurn != null)
        {
            OnEndTurn();
        }
    }

    #endregion

    private void Heal(int value)
    {
        _playerHitPoints[_activePlayer] += value;
    }

    private void GetMentalDmg(int value)
    {
        _playerPsychicalPoints[_activePlayer] -= value;
    }

    private void GetPhysicalDmg(int value)
    {
        _playerHitPoints[_activePlayer] -= value;
    }

    private void OnPlayerAction()
    {
        if (_playerActionPoints[_activePlayer] == 0)
        {
            EndTurn();
        }
    }
    #endregion
}

    #region MyIntVector2
    [Serializable]
    public struct IntVector2
    {
        public int x;
        public int y;

        public IntVector2(int x,int y)
        {
            this.x = x;
            this.y = y;
        }

        public static IntVector2 operator +(IntVector2 vec1, IntVector2 vec2)
        {
            IntVector2 tmp = new IntVector2(vec1.x + vec2.x, vec1.y + vec2.y);
            return tmp;
        }

        public static bool operator ==(IntVector2 vec1, IntVector2 vec2)
        {
            return (vec1.x == vec2.x) && (vec1.y == vec2.y);
        }

        public static bool operator !=(IntVector2 vec1, IntVector2 vec2)
        {
            return !(vec1.x == vec2.x) && (vec1.y == vec2.y);
        }
    }
    #endregion
