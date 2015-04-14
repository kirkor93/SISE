using UnityEngine;
using System.Collections;

public class MapUpdater : MonoBehaviour
{
    public GameObject[] Players;
    public QueueController Controller;

	protected void Start () 
    {

	}
	
	protected void Update () 
    {
	    
	}

    public void ChangeFieldOnMap(Vector2 position, FieldType type)
    {
        position.x += 50; position.x /= 2;
        position.y += 50; position.y /= 2;
        Controller.GetMapState().MapArray[(int)position.x, (int)position.y] = type;
    }

    public void RemoveNonEmptyField(Vector2 position)
    {
        position.x += 50; position.x /= 2;
        position.y += 50; position.y /= 2;
        Controller.GetMapState().MapArray[(int)position.x, (int)position.y] = FieldType.EMPTY;
    }
}
