using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public interface IQueueController
{
    bool Move(ActionDirection direction);
    bool KindleFire();
    bool SetTrap(ActionDirection dir);
    bool ThrowSpear(Vector2 target);
    MapState GetMapState();
    void EndTurn();
    int ActionPointsLeft { get; }
    int HitPoints { get; }
    int PsychicalCondition { get; }
    int WoodCount { get; }
    int NormalMoveCost { get; }
    int FoodMoveCost { get; }
    int FoodMoveHpRegen { get; }
    int WoodMoveCost { get; }
    int WoodCollectedOnMove { get; }
    int KindleFireActionPointsCost { get; }
    int KindleFireWoodCost { get; }
    int SetTrapActionPointsCost { get; }
    int SetTrapWoodCost { get; }
    int ThrowSpearActionPointsCost { get; }
    int ThrowSpearWoodCost { get; }
    int EatCorposeActionPointsCost { get; }
    int EatCorposePsychicalCost { get; }
    int EatCorposeHpRegen { get; }
}
