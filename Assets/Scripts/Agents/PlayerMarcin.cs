using UnityEngine;
using System.Collections;

public class PlayerMarcin : Bot
{
    public override void Play()
    {
        Debug.Log("Marcin's turn");
        if (Input.GetKeyDown(KeyCode.UpArrow))
        {
            Controller.Move(ActionDirection.Up);
            Controller.EndTurn();
        }
    }
}
