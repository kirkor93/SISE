
public class Vector2 {
	public int X;
	public int Y;
	
	public Vector2(int x, int y)
	{
		X = x;
		Y = y;
	}
	
	public Vector2()
	{
		X = 0;
		Y = 0;
	}
	
	public Vector2(final Vector2 vector)
	{
		X = vector.X;
		Y = vector.Y;
	}
	
	public void Add(Vector2 vec)
	{
		X += vec.X;
		Y += vec.Y;
	}
}
