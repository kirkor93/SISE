using UnityEngine;
using System.Collections;
using AiRuleEngine;

public class change : MonoBehaviour {

    private State myState;
    private Variable movingVar;
    private bool moving = false;

	// Use this for initialization
	void Start () 
    {
        this.myState = this.GetComponent<State>();
	}
	
	// Update is called once per frame
	void Update () {
        if(Input.GetKeyDown(KeyCode.G))
        {
            if(this.myState.GetVariable("moving", out movingVar))
            {
                moving = !moving;
                movingVar.SetValue(moving);
                this.myState.EditVariable(movingVar);
            }
        }
	}
}
