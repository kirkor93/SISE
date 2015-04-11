using UnityEngine;
using System.Collections;

public interface IQueueController
{
    bool Move(ActionDirection direction);
    bool KindleFire();
    bool SetTrap(ActionDirection dir);
    bool ThrowSpear(Vector2 target);
    MapState GetMapState();
    void EndTurn();
}
