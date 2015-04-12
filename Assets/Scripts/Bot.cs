using UnityEngine;
using System.Collections;
using UnityEditorInternal;

public abstract class Bot : MonoBehaviour
{
    public IQueueController Controller;

    public abstract void Play();
}
