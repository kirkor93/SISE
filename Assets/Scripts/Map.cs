using System;
using UnityEngine;
using System.Collections;

#region Enums

public enum FieldType
{
    Normal, WithFood, WithWood, WithCorpose
}

#endregion

public class Map : MonoBehaviour
{

    #region Methods
    #region MonoBehaviour

    // Use this for initialization
	void Start () 
    {
	
	}
	
	// Update is called once per frame
	void Update () 
    {

    }

    #endregion

    public FieldType GetFieldType(Vector2 position)
    {
        throw new NotImplementedException();
    }

    public void ChangeFieldType(FieldType newFieldType)
    {
        
    }

    public void SetTrap(Vector2 field)
    {

    }

    public void RemoveTrap(Vector2 field)
    {

    }

    public MapState GetMapState()
    {
        throw new NotImplementedException();
    }
    
    #endregion
}
