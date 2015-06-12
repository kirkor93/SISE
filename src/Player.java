public class Player {

	public Vector2 Position;
	public int HP;
	public int PP;
	public int AP;
	public int WP;
	public IBot Bot;
	public String MySymbol = "X";
	
	Player()
	{
		HP = 15;
		PP = 15;
		AP = 5;
		WP = 0;
		Position = new Vector2(0,0);
	}
	
	Player(Vector2 pos)
	{
		HP = 15;
		PP = 15;
		AP = 5;
		WP = 0;
		Position = new Vector2(pos);
	}
	
	Player(int hp, int pp, int ap, int wp, Vector2 pos)
	{
		HP = hp;
		PP = pp;
		AP = ap;
		WP = wp;
		Position = new Vector2(pos);		
	}
	
}
