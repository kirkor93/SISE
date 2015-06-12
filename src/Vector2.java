
public class Vector2 {
	public int X;
	public int Y;
	
	Vector2(int x, int y)
	{
		X = x;
		Y = y;
	}
	
	Vector2()
	{
		X = 0;
		Y = 0;
	}
	
	Vector2(final Vector2 vector)
	{
		X = vector.X;
		Y = vector.Y;
	}
}
