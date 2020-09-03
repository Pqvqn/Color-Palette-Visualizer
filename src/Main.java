import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedSet;

import javax.imageio.ImageIO;

public class Main {

	public static void main(String[] args) {
		
		Map<Integer,Integer> colors = new HashMap<Integer,Integer>();
		Scanner sc = new Scanner(System.in);
		String homepath = System.getProperty("user.home")+ "\\";
		System.out.print("Filepath: "+homepath);
		String filepath = homepath+sc.nextLine();
		System.out.println();
		sc.close();
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(filepath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i=0; i<img.getWidth(null); i++) {
			for(int j=0; j<img.getHeight(null); j++) {
				int rgb = img.getRGB(i,j);
				if(!colors.containsKey(rgb))colors.put(rgb,0);
				colors.put(rgb,colors.get(rgb)+1);
			}
		}
		Iterator<Integer> it = colors.keySet().iterator();
		ArrayList<Integer> sorted = new ArrayList<Integer>();
		while(it.hasNext()) {
			Integer rgb = it.next();
			int i;
			for(i=0; i<sorted.size() && colors.get(sorted.get(i)) > colors.get(rgb); i++);
			sorted.add(i,rgb);
		}
		for(int i=sorted.size()-1; i>=0; i--) {
			int rgb = sorted.get(i);
			int red = (rgb >> 16) & 0xFF;
			int green = (rgb >> 8) & 0xFF;
			int blue = rgb & 0xFF;
			System.out.println("("+red+","+green+","+blue+") : "+colors.get(rgb));
			//Color c = new Color(red,green,blue);
		}
		
	}

}
