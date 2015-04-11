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

    public int NormalMoveCost = 1;
    public int FoodMoveCost = 3;
    public int FoodMoveHpRegen = 10;
    public int WoodMoveCost = 3;
    public int WoodCollectedOnMove = 1;
    public int KindleFireActionPointsCost = 3;
    public int KindleFireWoodCost = 1;
    public int SetTrapActionPointsCost = 3;
    public int SetTrapFireWoodCost = 1;
    public int ThrowSpearActionPointsCost = 5;
    public int ThrowSpearWoodCost = 1;
    public int EatCorposeActionPointsCost = 3;
    public int EatCorposePsychicalCost = 3;
    public int EatCorposeHpRegen = 10;

    private int _activePlayer = 0;
    private int[] _playerActionPoints;
    private int[] _playerHitPoints;
    private int[] _playerPsychicalPoints;
    private int[] _playerWoodCount;
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
            
	    }


        //shuffling Players queue
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

    #endregion

    public bool Move(ActionDirection direction)
    {
        return false;
    }

    public bool KindleFire()
    {
        if (_playerWoodCount[_activePlayer] >= KindleFireWoodCost
            && _playerActionPoints[_activePlayer] >= KindleFireActionPointsCost)
        {
            _playerWoodCount[_activePlayer] -= KindleFireWoodCost;
            _playerActionPoints[_activePlayer] -= KindleFireActionPointsCost;
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
    }

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
