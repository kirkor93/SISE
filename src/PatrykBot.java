import CLIPSJNI.Environment;


public class PatrykBot extends Bot
{
	public PatrykBot()
	{
		this.MySymbol = "K";
		this.Clips = new Environment();
		this.Clips.load("src/MajsterClips.clp");
		this.Clips.reset();
		this.Clips.run();
	}
	
	@Override
	public void Play() {
		// TODO Auto-generated method stub
		System.out.println("Kochanski");
		
	}
}