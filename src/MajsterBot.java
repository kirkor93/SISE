import CLIPSJNI.Environment;
import CLIPSJNI.FactAddressValue;
import CLIPSJNI.MultifieldValue;


public class MajsterBot extends Bot
{
	public MajsterBot()
	{
		this.MySymbol = "M";
		this.Clips = new Environment();
		this.Clips.load("src/MajsterClips.clp");
		this.Clips.reset();
		this.Clips.run();
		
	}
	
	@Override
	public void Play() {
		// TODO Auto-generated method stub
		this.Clips.assertString("(bot (hitPoints " + Broker.GetMyHP() + ")" +
								"(state current))");
		System.out.println("Majster");
//		if(Broker.GetMyHP() == 0)
//		{
//			this.Clips.eval("(facts)");
//		}
		
		String evalStr = "(find-all-facts ((?f bot)) TRUE)";
		
		MultifieldValue pv = (MultifieldValue) this.Clips.eval(evalStr);
		int tNum = pv.listValue().size();
		FactAddressValue fv = (FactAddressValue) pv.listValue().get(tNum - 1);
		String currentHP = fv.getFactSlot("hitPoints").toString();
		
		System.out.println("Current HP: " + currentHP);
		
		this.Clips.reset();
	}
}