using UnityEngine;
using System.Collections;

public class QueueController : MonoBehaviour
{
    #region Variables
    public Bot[] ActivePlayers;
    public int InitActionPoints = 5;
    public int InitHitPoints = 15;

    private int _activePlayer = 0;
    private int[] _playerActionPoints;
    private int[] _playerHitPoints;
    #endregion

    #region Methods
    #region MonoBehaviour
    protected void Start () 
    {
        _playerActionPoints  = new int[ActivePlayers.Length];
	    for (int i = 0; i < _playerActionPoints.Length; i++)
	    {
	        _playerActionPoints[i] = InitActionPoints;
	    }

        _playerHitPoints = new int[ActivePlayers.Length];
        for (int i = 0; i < _playerHitPoints.Length; i++)
        {
            _playerHitPoints[i] = InitHitPoints;
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
	
	protected void Update () 
    {

    }
    #endregion

    public bool Move(Bot playerToMove)
    {
        return false;
    }



    #endregion
}
