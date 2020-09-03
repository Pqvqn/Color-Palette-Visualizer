import java.awt.Color;
import java.awt.Graphics;
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
		int totalPixels = 0;
		while(it.hasNext()) {
			Integer rgb = it.next();
			int i;
			for(i=0; i<sorted.size() && colors.get(sorted.get(i)) > colors.get(rgb); i++);
			sorted.add(i,rgb);
			totalPixels+=colors.get(rgb);
		}
		for(int i=sorted.size()-1; i>=0; i--) {
			int rgb = sorted.get(i);
			Color c = convertColor(rgb);
			System.out.println("("+c.getRed()+","+c.getGreen()+","+c.getBlue()+") : "+colors.get(rgb));
			//Color c = new Color(red,green,blue);
		}
		int width = 300;
		int height = 100;
		
		BufferedImage output = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		Graphics g = output.createGraphics();
		
		double unit = (double)totalPixels/width;
		System.out.println(unit);
		int pos = 0;
		for(int i=0; i<sorted.size(); i++) {
			int rgb = sorted.get(i);
			for(int j=0; j<(int)(.5+colors.get(rgb)/unit); j++) {
				g.setColor(convertColor(rgb));
				System.out.println(j+"  "+pos);
				g.drawLine(pos,0,pos,height);
				pos++;
			}
		}
		
		System.out.print("Filepath: "+homepath);
		String filepath_save = homepath+sc.nextLine();
		System.out.println();
		sc.close();
		
		File ret = new File(filepath_save);
		ret.getParentFile().mkdirs();
		if(!ret.exists()) {
			try {
				ret.createNewFile();
				ImageIO.write(output, "png", ret);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}


	private static Color convertColor(int rgb) {
		int red = (rgb >> 16) & 0xFF;
		int green = (rgb >> 8) & 0xFF;
		int blue = rgb & 0xFF;
		Color c = new Color(red,green,blue);
		return c;
	
	}
}
