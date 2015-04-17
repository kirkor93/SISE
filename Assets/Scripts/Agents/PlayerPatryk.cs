using UnityEngine;
using System.Collections;

public class PlayerPatryk : Bot
{
    public override void Play()
    {
        Debug.Log("Patryk's turn");
        if (Input.GetKeyDown(KeyCode.DownArrow))
        {
            Controller.Move(ActionDirection.Down);
            Controller.EndTurn();
        }
    }
}
