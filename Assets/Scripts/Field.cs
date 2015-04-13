using UnityEngine;
using System.Collections;

[RequireComponent(typeof(SpriteRenderer))]
public class Field : MonoBehaviour
{

    #region Variables

    private SpriteRenderer spriteRenderer;

    #endregion

    void Awake()
    {
        this.spriteRenderer = this.GetComponent<SpriteRenderer>();
    }

    // Use this for initialization
	void Start () 
    {
	
	}
	
	// Update is called once per frame
	void Update () 
    {
	
	}

    public void SetSprite(Sprite _sprite)
    {
        this.spriteRenderer.sprite = _sprite;
    }
}
