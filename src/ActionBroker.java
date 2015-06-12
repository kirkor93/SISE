
public class ActionBroker {
	
	private GameController _GC;
	
	ActionBroker()
	{
		_GC = null;
	}
	
	ActionBroker(GameController gc)
	{
		_GC = gc;
	}
	
	public int GetMyHP()
	{
		if(_GC != null) return _GC.GetActivePlayerHP();
		else return 0;
	}
	
	public int GetMyPP()
	{
		if(_GC != null) return _GC.GetActivePlayerPP();
		else return 0;
	}
	
	public int GetMyWP()
	{
		if(_GC != null) return _GC.GetActivePlayerWP();
		else return 0;
	}
	
	public int GetMyAP()
	{
		if(_GC != null) return _GC.GetActivePlayerAP();
		else return 0;
	}
	
	public Vector2 GetMyPosition()
	{
		if(_GC != null) return _GC.GetActivePlayerPosition();
		else return new Vector2();
	}
	
	public boolean Action(ActionType type, Vector2 vec)
	{
		return _GC.Action(type, vec);
	}
	
}
