using UnityEngine;
using System.Collections;

public class MapDisplay : MonoBehaviour
{

    #region Variables

    public QueueController QueueControllerInstance;
    public GameObject FieldPrefab;
    public Sprite EmptyFieldSprite;
    public Sprite FoodFieldSprite;
    public Sprite WoodFieldSprite;
    public Sprite CorpseFieldSprite;

    private MapState mapState;
    private Field[,] fields = new Field[50, 50];
    private Bot[] players;

    #endregion

    // Use this for initialization
	void Start ()
    {
        QueueControllerInstance.OnEndTurn += this.OnEndTurn;
        this.mapState = QueueControllerInstance.CurrentMap.GetCurrentMapState();
        this.players = QueueControllerInstance.ActivePlayers;
        for (int i = 0; i < 50; ++i)
        {
            for (int j = 0; j < 50; ++j)
            {
                GameObject prefabInstantiated = Instantiate(FieldPrefab, new Vector3((i - 25) * 2, (25 - j) * 2, 0.0f), Quaternion.identity) as GameObject;
                prefabInstantiated.transform.parent = this.transform;
                this.fields[i, j] = prefabInstantiated.GetComponent<Field>();
                Sprite sprite = EmptyFieldSprite;

                switch (this.mapState.MapArray[i, j])
                {
                    case FieldType.CORPSE:
                        sprite = CorpseFieldSprite;
                        break;
                    case FieldType.EMPTY:
                        sprite = EmptyFieldSprite;
                        break;
                    case FieldType.FOOD:
                        sprite = FoodFieldSprite;
                        break;
                    case FieldType.TRAP:
                        sprite = EmptyFieldSprite;
                        break;
                    case FieldType.WOOD:
                        sprite = WoodFieldSprite;
                        break;
                }
                this.fields[i, j].SetSprite(sprite);
            }
        }
	}
	
	// Update is called once per frame
	void Update ()
    {

	}

    void OnEndTurn()
    {
        this.mapState = QueueControllerInstance.CurrentMap.GetCurrentMapState();
        for(int i = 0; i < 50; ++i)
        {
            for(int j = 0; j < 50; ++j)
            {
                Sprite sprite = EmptyFieldSprite;
                switch (this.mapState.MapArray[i, j])
                {
                    case FieldType.CORPSE:
                        sprite = CorpseFieldSprite;
                        break;
                    case FieldType.EMPTY:
                        sprite = EmptyFieldSprite;
                        break;
                    case FieldType.FOOD:
                        sprite = FoodFieldSprite;
                        break;
                    case FieldType.TRAP:
                        sprite = EmptyFieldSprite;
                        break;
                    case FieldType.WOOD:
                        sprite = WoodFieldSprite;
                        break;
                }
                this.fields[i, j].SetSprite(sprite);
            }
        }
    }

}
