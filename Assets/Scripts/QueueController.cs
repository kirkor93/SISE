using System;
using UnityEngine;
using System.Collections;
using System.Linq;
using Random = UnityEngine.Random;

public class QueueController : MonoBehaviour, IQueueController
{
    #region Variables
    public Bot[] ActivePlayers;
    public int InitActionPoints = 5;
    public int InitHitPoints = 15;
    public int InitPsychicalPoints = 15;
    public Map CurrentMap;

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

    private int _activePlayer = 0;
    private int[] _playerActionPoints;
    private int[] _playerHitPoints;
    private int[] _playerPsychicalPoints;
    private int[] _playerWoodCount;
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

    #endregion

    #region Methods
    #region MonoBehaviour
    protected void Start () 
    {
        _playerActionPoints  = new int[ActivePlayers.Length];
        _playerWoodCount = new int[ActivePlayers.Length];
        _playerHitPoints = new int[ActivePlayers.Length];
        _playerPsychicalPoints = new int[ActivePlayers.Length];
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
        }

        //starting game
        ActivePlayers[_activePlayer].Play();
    }

    #endregion

    #region IQueueController
    public bool Move(ActionDirection direction)
    {
        return false;
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

    public bool SetTrap(ActionDirection dir)
    {


        //Majster's method setting the trap on map
        return false;
    }

    public bool ThrowSpear(Vector2 target)
    {
        return false;
    }

    public MapState GetMapState()
    {
        return CurrentMap.GetMapState();
    }

    public void EndTurn()
    {
        _playerActionPoints[_activePlayer] = InitActionPoints;
        _activePlayer += 1;
        _activePlayer %= ActivePlayers.Length;

        ActivePlayers[_activePlayer].Play();
    }

    #endregion

    private void Heal(int value)
    {
        _playerHitPoints[_activePlayer] += value;
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
