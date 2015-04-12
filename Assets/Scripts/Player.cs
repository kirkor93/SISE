using UnityEngine;
using System.Collections;

#region EnumForActionDirection
public enum ActionDirection
{
    Up = 1,
    Right,
    Down,
    Left
}
#endregion

public class Player : Bot
{

    #region Variables

    #endregion

    #region Properties

    #endregion

    #region Methods

    public override void Play()
    {
        Debug.Log("Playing");
    }

    #endregion


}
