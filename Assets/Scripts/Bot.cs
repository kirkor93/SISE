using UnityEngine;
using System.Collections;
using UnityEditorInternal;

public abstract class Bot : MonoBehaviour
{
    public IQueueController Controller;

    protected Player PlayerScript;

    protected virtual void Start()
    {
        PlayerScript = gameObject.GetComponent<Player>();
    }

    public abstract void Play();
}
