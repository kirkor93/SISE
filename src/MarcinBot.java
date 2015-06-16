import CLIPSJNI.Environment;


public class MarcinBot extends Bot
{
	public MarcinBot()
	{
		this.MySymbol = "Q";
		this.Clips = new Environment();
		this.Clips.load("src/MarcinCLIPS.clp");
		this.Clips.reset();
		this.Clips.run();
	}
	
	@Override
	public void Play() {
		
		// get map state from game
		
		// send state to CLIPS
		
		// retrieve decision from the CLIPS and proceed accordingly
		
		System.out.println("Marcin | " + 
				"POS=" + Broker.GetMyPosition().toString() + " | " +
				"CFLD=" + Broker.GetFieldType(Broker.GetMyPosition().X, Broker.GetMyPosition().Y).toString() + " | " +
				"HP=" + String.valueOf(Broker.GetMyHP()) + " | " +
				"PP=" + String.valueOf(Broker.GetMyPP()) + " | " +
				"AP=" + String.valueOf(Broker.GetMyAP()) + " | " +
				"WP=" + String.valueOf(Broker.GetMyWP())
				);
	}
}