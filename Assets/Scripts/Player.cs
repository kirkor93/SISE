using UnityEngine;
using System.Collections;

public class Player : MonoBehaviour
{

    #region Variables

    
    #endregion

    #region Properties

    #endregion

    #region Methods

    public void Move(ActionDirection dir)
    {
        
    }

    public void KindleFire()
    {

    }

    public void SetTrap(ActionDirection dir)
    {

    }

    public void ThrowSpear(Vector2 target)
    {

    }

    // Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {

    }
    #endregion

    #region EnumForActionDirection
    public enum ActionDirection
    {
        Up = 1,
        Right,
        Down,
        Left
    }
    #endregion
}
