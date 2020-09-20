import java.util.ArrayList;

public class ColorClump {

	public ColorData rgbhead; //rgb to compare to
	public ArrayList<ColorData> sublist; //list of colors in clump
	public int prevalence; //total prevalence of clump
	
	public ColorClump(ColorData rgbhead) {
		this.rgbhead = rgbhead;
		sublist = new ArrayList<ColorData>();
		prevalence = this.rgbhead.prevalence;
	}
	
	//merge another clump into this one
	public void absorb(ColorClump other) {
		
	}
	
}
