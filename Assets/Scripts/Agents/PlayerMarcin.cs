using UnityEngine;
using System.Collections;

public class PlayerMarcin : Bot
{
    public override void Play()
    {
        Debug.Log("BBBB");
        Controller.EndTurn();
    }
}
