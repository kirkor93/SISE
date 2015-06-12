import javax.imageio.*;

import java.awt.image.BufferedImage;
import java.io.*;

public class Map 
{
	final int BLACK = -16777216;
	final int GREEN = -16711936;
	final int BLUE = -16776961;
	
	public Field[][] MyFields = new Field[50][50];
	
	private String mapPath = "Maps/";
	
	private BufferedImage loadedImage;
	
	private GameController gameController;
	
	public Map(MapBMP map)
	{
		this.mapPath += map.toString();
		this.mapPath += ".bmp";
	}
	
	public boolean LoadMap()
	{
		try
		{
			loadedImage = ImageIO.read(new File(this.mapPath));
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
			return false;
		}
		
		int width = loadedImage.getWidth();
		int height = loadedImage.getHeight();
		if(width != 50 || height != 50)
		{
			System.out.println("Oops, the image is not 50x50 size");
			return false;
		}
		
		for(int i = 0; i < height; ++i)
		{
			for(int j = 0; j < width; ++j)
			{
				int val = loadedImage.getRGB(i, j);
				FieldType ft = FieldType.NORMAL;
				if(val == BLUE)
				{
					ft = FieldType.FOOD;
				}
				else if(val == GREEN)
				{
					ft = FieldType.WOOD;
				}
				
				MyFields[i][j] = new Field(ft, false);
			}
		}
		
		return true;
	}
	
	public void SetGameController(GameController instance)
	{
		this.gameController = instance;
	}
	
	@Override
	public String toString() {
		String toRet = "";
		Player[] players = gameController.GetPlayers();
		boolean flag = false;
		for(int i = 0; i < 50; ++i)
		{
			for(int j = 0; j < 50; ++j)
			{
				flag = false;
				for(int k = 0; k < 4; ++k)
				{
					if(players[k].Position.X == i && players[k].Position.Y == j)
					{
						toRet += players[k].MySymbol;
						flag = true;
					}
				}
				if(!flag)
				{
					switch(MyFields[i][j].MyFieldType)
					{
					case WOOD:
						toRet += "W";
						break;
					case FOOD:
						toRet += "F";
						break;
					case CORPSE:
						toRet += "C";
						break;
					default:
						toRet += " ";
						break;
					}
				}
			}
			toRet += "\n";
		}
		return toRet;
	}
}
