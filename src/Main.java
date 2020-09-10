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
		
		int[] pixels = img.getRGB(0,0,img.getWidth(),img.getHeight(),null,0,img.getWidth());
		
		System.out.println("S0");
		
		for(int i=0; i<pixels.length; i++) {
			int rgb = pixels[i];
			if(!colors.containsKey(rgb))colors.put(rgb,0);
			colors.put(rgb,colors.get(rgb)+1);
		}
		
		System.out.println("S1");
		
		Iterator<Integer> it = colors.keySet().iterator();
		ArrayList<Integer> sorted = new ArrayList<Integer>();
		int totalPixels = 0;
		while(it.hasNext()) {
			Integer rgb = it.next();
			//int i;
			//for(i=0; i<sorted.size() && colors.get(sorted.get(i)) > colors.get(rgb); i++);
			//sorted.add(i,rgb);
			totalPixels+=colors.get(rgb);
			
			if(sorted.isEmpty()) {
				sorted.add(rgb);
			}else {
				int left = 0;
				int right = sorted.size();
				int prevalence = colors.get(rgb);
				while(right-left>1) {
					int divider = (right-left)/2+left;
					if(colors.get(sorted.get(divider))>prevalence) {
						left = divider;
					}else if (colors.get(sorted.get(divider))<prevalence){
						right = divider;
					}else {
						left = divider;
					}
				}
				if(colors.get(sorted.get(0))<prevalence) {
					sorted.add(0,rgb);
				}else if(right<sorted.size() && colors.get(sorted.get(right))>prevalence) {
					sorted.add(right+1,rgb);
				}else {
					sorted.add(right,rgb);
				}
			}
		}
		
		System.out.println("S2");
		/*for(int i=sorted.size()-1; i>=0; i--) {
			int rgb = sorted.get(i);
			Color c = convertColor(rgb);
			System.out.println("("+c.getRed()+","+c.getGreen()+","+c.getBlue()+") : "+colors.get(rgb));
			//Color c = new Color(red,green,blue);
		}*/
		double cutoff = 30;
		Map<Integer,Integer> clumps = new HashMap<Integer,Integer>();
		Map<Integer,ArrayList<Integer>> subcols = new HashMap<Integer,ArrayList<Integer>>();
		for(int i=0; i<sorted.size(); i++) {
			int rgb = sorted.get(i);
			clumps.put(rgb,colors.get(rgb));
			subcols.put(rgb,new ArrayList<Integer>());
		}
		
		System.out.println("S2.5");
		
		for(int i=sorted.size()-1; i>0; i--) {
			int rgb = sorted.get(i);
			//System.out.print(System.currentTimeMillis());
			System.out.println(i);
			for(int j=0; j<i; j++) {
				//System.out.print(" "+j);
				if(colorsClose(rgb,sorted.get(j),cutoff)){
					int rgb2 = sorted.get(j);
					clumps.put(rgb2,clumps.get(rgb)+clumps.get(rgb2));
					clumps.remove(rgb);
					ArrayList<Integer> addfrom = subcols.get(rgb);
					ArrayList<Integer> addto = subcols.get(rgb2);
					addfrom.add(0,rgb);
					//for(int c=cc.size()-1; c>=0; c--)subcols.get(rgb2).add(0,cc.get(c));
					int lastindex = 0;
					for(int c=0; c<addfrom.size(); c++) {
						int rgb3 = addfrom.get(c);
						if(addto.isEmpty()) {
							lastindex = 0;
						}else {
							int left = lastindex;
							int right = addto.size();
							int prevalence = colors.get(rgb3);
							while(right-left>1) {
								int divider = (right-left)/2+left;
								if(colors.get(addto.get(divider))>prevalence) {
									left = divider;
								}else if (colors.get(addto.get(divider))<prevalence){
									right = divider;
								}else {
									left = divider;
								}
							}
							if(colors.get(addto.get(lastindex))<prevalence) {
								//lastindex = 0;
							}else if(right<addto.size() && colors.get(addto.get(right))>prevalence) {
								lastindex = right+1;
							}else {
								lastindex = right;
							}
						}
						addto.add(lastindex,rgb3);
					}
					//subcols.get(rgb2).add(0,rgb);
					//System.out.println(subcols.get(rgb2));
					subcols.remove(rgb);
					sorted.remove(i);
					sorted.remove(j);
					if(sorted.isEmpty()) {
						sorted.add(rgb2);
					}else {
						int left = 0;
						int right = sorted.size();
						int prevalence = clumps.get(rgb2);
						while(right-left>1) {
							int divider = (right-left)/2+left;
							if(clumps.get(sorted.get(divider))>prevalence) {
								left = divider;
							}else if (clumps.get(sorted.get(divider))<prevalence){
								right = divider;
							}else {
								left = divider;
							}
						}
						if(clumps.get(sorted.get(0))<prevalence) {
							sorted.add(0,rgb2);
						}else if(right<sorted.size() && clumps.get(sorted.get(right))>prevalence) {
							sorted.add(right+1,rgb2);
						}else {
							sorted.add(right,rgb2);
						}
					}
					j=i;
				}
			}
			//System.out.println();
		}
		
		System.out.println("S3");
		
		int width = 3200;
		int height = 300;
		
		BufferedImage output = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		Graphics g = output.createGraphics();
		
		
		double unit = (double)totalPixels/width;
		//System.out.println(unit);
		int pos = 0;
		for(int s=0; s<sorted.size(); s++) {
			int rgb = sorted.get(s);
			int spos = pos;
			ArrayList<Integer> clumped = subcols.get(rgb);
			clumped.add(0,rgb);
			int clumpwid = (int)(.5+clumps.get(rgb)/unit);
			
			/*int total = 0;
			for(int i=0; i<clumped.size(); i++)total+=colors.get(clumped.get(i));
			System.out.println(clumps.get(rgb)+"   "+total);*/
			
			for(int i=0; i<clumped.size(); i++) {
				int rgb2 = clumped.get(i);
				for(int j=0; j<(int)(.5+colors.get(rgb2)/unit) && pos<spos+clumpwid; j++) {
					g.setColor(convertColorI2C(rgb2));
					//System.out.println(j+"  "+pos);
					g.drawLine(pos,0,pos,height);
					pos++;
				}
				if((int)(.5+colors.get(rgb2)/unit)==0 && pos<spos+clumpwid) {
					g.setColor(convertColorI2C(rgb2));
					//System.out.println(1+"  "+pos);
					g.drawLine(pos,0,pos,height);
					pos++;
				}
				if(s==2)System.out.println(i+"  "+colors.get(rgb2));
			}
			if((int)(.5+clumps.get(rgb)/unit)==0 && pos<width) {
				g.setColor(convertColorI2C(rgb));
				//System.out.println(1+"  "+pos);
				g.drawLine(pos,0,pos,height);
				pos++;
			}
			
			
		}

		System.out.println("S4");
		/*for(int i=0; i<6; i++) {
			Color col1 = convertColor(sorted.get((int)(Math.random()*sorted.size())));
			Color col2 = convertColor(sorted.get((int)(Math.random()*sorted.size())));
			
			System.out.print("("+col1.getRed()+","+col1.getGreen()+","+col1.getBlue()+")" );
			System.out.print("("+col2.getRed()+","+col2.getGreen()+","+col2.getBlue()+")" );
			System.out.println(colorsDistance(col1,col2));
		}*/
		
		
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


	public static Color convertColorI2C(int rgb) {
		int red = (rgb >> 16) & 0xFF;
		int green = (rgb >> 8) & 0xFF;
		int blue = rgb & 0xFF;
		Color c = new Color(red,green,blue);
		return c;
	
	}
	public static int convertColorC2I(Color c) {
		int red = (c.getRed() << 16) & 0x00FF0000;
		int green = (c.getGreen() << 8) & 0x0000FF00;
		int blue = c.getBlue() & 0x000000FF;
		return 0xFF000000 | red | green | blue;
	}
	
	public static int convertColor32I(int r, int g, int b) {
		int red = (r << 16) & 0x00FF0000;
		int green = (g << 8) & 0x0000FF00;
		int blue = b & 0x000000FF;
		return 0xFF000000 | red | green | blue;
	}
	
	public static int[] convertColorI23(int rgb) {
		int red = (rgb >> 16) & 0xFF;
		int green = (rgb >> 8) & 0xFF;
		int blue = rgb & 0xFF;
		return new int[] {red,green,blue};
	}
	
	public static Color averageColor(Color c1, Color c2) {
		double red = Math.sqrt(Math.pow(c1.getRed(),2)+Math.pow(c2.getRed(),2))/2;
		double green = Math.sqrt(Math.pow(c1.getGreen(),2)+Math.pow(c2.getGreen(),2))/2;
		double blue = Math.sqrt(Math.pow(c1.getBlue(),2)+Math.pow(c2.getBlue(),2))/2;
		return new Color((int)(.5+red),(int)(.5+green),(int)(.5+blue));
	}
	
	public static int averageColor(int rgb1, int rgb2) {
		int[] cols1 = convertColorI23(rgb1);
		int[] cols2 = convertColorI23(rgb2);
		double red = Math.sqrt(Math.pow(cols1[0],2)+Math.pow(cols2[0],2))/2;
		double green = Math.sqrt(Math.pow(cols1[1],2)+Math.pow(cols2[1],2))/2;
		double blue = Math.sqrt(Math.pow(cols1[2],2)+Math.pow(cols2[2],2))/2;
		return convertColor32I((int)(.5+red),(int)(.5+green),(int)(.5+blue));
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
