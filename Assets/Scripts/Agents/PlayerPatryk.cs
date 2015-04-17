using UnityEngine;
using System.Collections;

public class PlayerPatryk : Bot
{
    public override void Play()
    {
        Debug.Log("Patryk's turn");
        if (Input.GetKeyDown(KeyCode.UpArrow))
        {
            Controller.Move(ActionDirection.Up);
            Controller.EndTurn();
        }
        else if (Input.GetKeyDown(KeyCode.DownArrow))
        {
            Controller.Move(ActionDirection.Down);
            Controller.EndTurn();
        }
        else if (Input.GetKeyDown(KeyCode.LeftArrow))
        {
            Controller.Move(ActionDirection.Left);
            Controller.EndTurn();
        }
        else if (Input.GetKeyDown(KeyCode.RightArrow))
        {
            Controller.Move(ActionDirection.Right);
            Controller.EndTurn();
        }
    }
}
