using UnityEngine;
using System.Collections;
using AiRuleEngine;

[ScriptName("Move")]
[ScriptCategory("Movement")]
public class MyAction : BaseAction
{

    public GameObject targetObject;
    public float x = 0;
    public float y = 0;
    public float z = 0;

    public override bool Execute()
    {
        targetObject.transform.position += new Vector3(x, y, z);

        return true;
    }
}
