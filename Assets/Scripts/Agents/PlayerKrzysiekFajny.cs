using UnityEngine;
using System.Collections;

public class PlayerKrzysiekFajny : Bot
{
    public override void Play()
    {
        Debug.Log("Krzysiek fajny's turn");
        if(Input.GetKeyDown(KeyCode.RightArrow))
        {
            Controller.Move(ActionDirection.Right);
            Controller.EndTurn();
        }
    }
}
