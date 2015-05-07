using AForge;
using AForge.Fuzzy;
using UnityEngine;
using System.Collections;

public class PlayerMarcin : Bot
{
    public override void Play()
    {
        TrapezoidalFunction function1 = new TrapezoidalFunction(2.5f, 5, TrapezoidalFunction.EdgeType.Right);
        FuzzySet fsLow = new FuzzySet("Low", function1);
        TrapezoidalFunction function2 = new TrapezoidalFunction(2.5f, 5, 7.5f, 10);
        FuzzySet fsMedium = new FuzzySet("Medium", function2);
        TrapezoidalFunction function3 = new TrapezoidalFunction(7.5f, 10, TrapezoidalFunction.EdgeType.Left);
        FuzzySet fsHigh = new FuzzySet("High", function3);

        LinguisticVariable lvHP = new LinguisticVariable("HitPoints", 0, 15);
        lvHP.AddLabel(fsLow);
        lvHP.AddLabel(fsMedium);
        lvHP.AddLabel(fsHigh);

        TrapezoidalFunction function4 = new TrapezoidalFunction(1, 2.5f, TrapezoidalFunction.EdgeType.Right);
        FuzzySet fsFood = new FuzzySet("Food", function4);
        TrapezoidalFunction function5 = new TrapezoidalFunction(1, 2.5f, 4, 5.5f);
        FuzzySet fsWood = new FuzzySet("Wood", function5);
        TrapezoidalFunction function6 = new TrapezoidalFunction(4, 5.5f, TrapezoidalFunction.EdgeType.Left);
        FuzzySet fsKill = new FuzzySet("Kill", function6);

        LinguisticVariable lvPrior = new LinguisticVariable("Priority", 0, 8);
        lvPrior.AddLabel(fsFood);
        lvPrior.AddLabel(fsWood);
        lvPrior.AddLabel(fsKill);

        Database db = new Database();
        db.AddVariable(lvHP);
        db.AddVariable(lvPrior);

        InferenceSystem IS = new InferenceSystem(db, new CentroidDefuzzifier(1000));
        IS.NewRule("Test1", "IF HitPoints is Low then Priority is Food");
        IS.NewRule("Test2", "IF HitPoints is Medium then Priority is Wood");
        IS.NewRule("Test3", "IF HitPoints is High then Priority is Kill");

        IS.SetInput("HitPoints", Controller.HitPoints);
        float result = IS.Evaluate("Priority");
        Debug.Log(result.ToString());

        //Debug.Log("Marcin's turn");
        //if (Input.GetKeyDown(KeyCode.UpArrow))
        //{
        //    Controller.Move(ActionDirection.Up);
        //    Controller.EndTurn();
        //}
        //else if(Input.GetKeyDown(KeyCode.DownArrow))
        //{
        //    Controller.Move(ActionDirection.Down);
        //    Controller.EndTurn();
        //}
        //else if (Input.GetKeyDown(KeyCode.LeftArrow))
        //{
        //    Controller.Move(ActionDirection.Left);
        //    Controller.EndTurn();
        //}
        //else if (Input.GetKeyDown(KeyCode.RightArrow))
        //{
        //    Controller.Move(ActionDirection.Right);
        //    Controller.EndTurn();
        //}
        Controller.EndTurn();
    }
}
