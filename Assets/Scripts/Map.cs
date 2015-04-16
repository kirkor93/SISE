using System;
using UnityEngine;
using System.Collections;

#region Enums

public enum FieldType
{
    EMPTY, FOOD, WOOD, CORPSE, TRAP
}

#endregion

public class Map : MonoBehaviour
{
    #region Constants

    public const int MAP_SIZE = 50;
    private const int FIELD_TYPES = 5;

    #endregion

    #region Variables

    private int[][] colorFieldTypeAssignment = new int[][]
    {
        new int[] {0x00000000, (int)FieldType.EMPTY},
        new int[] {0x000000FF, (int)FieldType.FOOD},
        new int[] {0x0000FF00, (int)FieldType.WOOD},
        new int[] {0x00FF0000, (int)FieldType.CORPSE},
        new int[] {0x0000FFFF, (int)FieldType.TRAP}
    };

    #endregion

    #region Properties

    private MapState currentState;

    #endregion

    #region Methods

    #region MonoBehaviour

    // Use this for initialization
	void Start () 
    {
        // for testing purposes only
        Texture2D test = Resources.LoadAssetAtPath<Texture2D>(@"Assets\Maps\testMap.bmp");
        LoadStateFromBitmap(test);
        /////////////////////
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

    public MapState GetCurrentMapState()
    {
        return currentState;
    }

    public void LoadStateFromBitmap(Texture2D bitmap)
    {
        CreateNewMapState(out currentState);

        Color32 col;
        int colInt;
        for(int x = 0; x < MAP_SIZE; ++x)
        {
            for(int y = 0; y < MAP_SIZE; ++y)
            {
                col = bitmap.GetPixel(x, MAP_SIZE - y - 1);
                colInt = ColorToInt(col);

                for(int i = 0; i < FIELD_TYPES; ++i)
                {
                    if(colInt == colorFieldTypeAssignment[i][0])
                    {
                        currentState.MapArray[x, y] = (FieldType)colorFieldTypeAssignment[i][1];
                        continue;
                    }
                    
                }
            }
        }
    }

    private void CreateNewMapState(out MapState state)
    {
        state.MapArray = new FieldType[MAP_SIZE, MAP_SIZE];
    }

    public int[] SetPlayerStartPosition(int activePlayers)
    {
        int[] playerToSlot = new int[activePlayers];
        for (int i = 0; i < activePlayers; i++)
        {
            bool contains = false;
            int x;
            do
            {
                x = UnityEngine.Random.Range(1, activePlayers);
                for (int j = 0; j < activePlayers; j++)
                {
                    if (!contains)
                    {
                        if (playerToSlot[j] == x)
                        {
                            contains = true;
                        }
                        else
                        {
                            contains = false;
                        }
                    }
                }
            } while (!contains);
            playerToSlot[i] = x;
        }
        return playerToSlot;
    }

    private int ColorToInt(Color32 col)
    {
        int toReturn = 0x00000000;
        toReturn = col.r;
        toReturn |= (col.g << 8);
        toReturn |= (col.b << 16);
        return toReturn;
    }
    
    #endregion
}
