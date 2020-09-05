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
		
		for(int i=0; i<6; i++) {
			Color col1 = convertColor(sorted.get((int)(Math.random()*sorted.size())));
			Color col2 = convertColor(sorted.get((int)(Math.random()*sorted.size())));
			
			System.out.print("("+col1.getRed()+","+col1.getGreen()+","+col1.getBlue()+")" );
			System.out.print("("+col2.getRed()+","+col2.getGreen()+","+col2.getBlue()+")" );
			System.out.println(colorsDistance(col1,col2));
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


	public static Color convertColor(int rgb) {
		int red = (rgb >> 16) & 0xFF;
		int green = (rgb >> 8) & 0xFF;
		int blue = rgb & 0xFF;
		Color c = new Color(red,green,blue);
		return c;
	
	}
	
	public static boolean colorsClose(Color c1, Color c2, double cut) {
		return colorsDistance(c1,c2)<cut;
	}
	public static boolean colorsClose(int c1, int c2, double cut) {
		return colorsDistance(c1,c2)<cut;
	}
	public static boolean colorsClose(int r1, int g1, int b1, int r2, int g2, int b2, double cut) {
		return colorsDistance(r1,g1,b1,r2,g2,b2) < cut;
	}
	public static double colorsDistance(Color c1, Color c2) {
		return colorsDistance(c1.getRed(), c1.getGreen(), c1.getBlue(), c2.getRed(), c2.getGreen(), c2.getBlue());
	}
	public static double colorsDistance(int c1, int c2) {
		int red1 = (c1 >> 16) & 0xFF;
		int green1 = (c1 >> 8) & 0xFF;
		int blue1 = c1 & 0xFF;
		int red2 = (c2 >> 16) & 0xFF;
		int green2 = (c2 >> 8) & 0xFF;
		int blue2 = c2 & 0xFF;
		return colorsDistance(red1,green1,blue1,red2,green2,blue2);
	}
	public static double colorsDistance(int r1, int g1, int b1, int r2, int g2, int b2) {
		return Math.sqrt(Math.pow((r1-r2),2)+Math.pow((g1-g2),2)+Math.pow((b1-b2),2));
	}
}
