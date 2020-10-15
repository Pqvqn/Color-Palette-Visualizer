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
		

		//ask user for input file and output specifications
		Scanner sc = new Scanner(System.in);
		String homepath = System.getProperty("user.home")+ "\\";
		System.out.print("Filepath: "+homepath);
		String filepath = homepath+sc.nextLine();
		System.out.println();
		
		int width = 600;
		int height = 3000;
		int imagecount = 1;
		int loopfrom = 1;
		int loopto = 1;

		String imagename = "";
		System.out.print("Number of Images: ");
		imagecount = sc.nextInt();
		if(imagecount == 1) {
			sc.nextLine();
			System.out.print("Image Name: ");
			imagename = sc.nextLine();
		}else {
			System.out.print("Filename Start Index: ");
			loopfrom = sc.nextInt();
			System.out.print("Filename End Index: ");
			loopto = sc.nextInt();
		}
		System.out.print("Width per Image: ");
		width = sc.nextInt();
		System.out.print("Output Height: ");
		height = sc.nextInt();

		//the final output image (all breakdowns rendered in order)
		BufferedImage grandoutput = new BufferedImage(width*imagecount,height,BufferedImage.TYPE_INT_ARGB);
		Graphics g2 = grandoutput.createGraphics();
		
		int vpos = 0; //column of grand output currently drawn in
		
		//loop over every image
		for(int km=loopfrom; km<=loopto; km++) {
		
		BufferedImage img = null; //image for this breakdown
		try {
			Map<Integer,Integer> colors = new HashMap<Integer,Integer>(); //map of original image (Integer Color, Quantity)
			String name = "";
			if(imagecount == 1) {
				name = imagename; //if one image, use submitted name
			}else {
				name = "image-"+ ((km>=10)?((km>=100)?""+km:"0"+km):"00"+km)  +".png"; //if multiple, use image-%03d.png
			}
			img = ImageIO.read(new File(filepath+name)); //get image
			int[] pixels = img.getRGB(0,0,img.getWidth(),img.getHeight(),null,0,img.getWidth()); //all pixels of image
			
			//use pixel array to find quantity of each color of pixel present
			System.out.println("S0 "+km);
			
			for(int i=0; i<pixels.length; i++) {
				int rgb = pixels[i];
				if(!colors.containsKey(rgb))colors.put(rgb,0);
				colors.put(rgb,colors.get(rgb)+1);
			}
			
			//sort the colors into an ArrayList, sorted by quantity
			System.out.println("S1");
			
			Iterator<Integer> it = colors.keySet().iterator();
			ArrayList<Integer> sorted = new ArrayList<Integer>(); //sorted list of colors, first to last = most prevalent to least
			int totalPixels = 0;
			while(it.hasNext()) {
				Integer rgb = it.next(); //color to be sorted
				totalPixels+=colors.get(rgb);
				
				if(sorted.isEmpty()) {
					sorted.add(rgb); //if first entry
				}else {
					//binary sort color into sorted list
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
			
			//clump least prevalent colors into larger ones that they are close to
			System.out.println("S2");
			double cutoff = 30; //maximum distance of colors to still be clumped
			Map<Integer,Integer> clumps = new HashMap<Integer,Integer>(); //map of (Clump Lead Integer Color, Total Quantity of All Subcolors)
			Map<Integer,ArrayList<Integer>> subcols = new HashMap<Integer,ArrayList<Integer>>(); //map of (Clump Lead Integer Color, List of Subcolors)
			//put every color into the clump maps with no subcols
			for(int i=0; i<sorted.size(); i++) {
				int rgb = sorted.get(i);
				clumps.put(rgb,colors.get(rgb));
				subcols.put(rgb,new ArrayList<Integer>());
			}
			
			System.out.println("S2.5");
			//continue clumping and updating the sorted list
			for(int i=sorted.size()-1; i>0; i--) {
				int rgb = sorted.get(i); //color to potentially be clumped
				System.out.println(i);
				//loop through all colors more prevalent than this one
				for(int j=0; j<i; j++) {
					//if close enough to clump
					if(colorsClose(rgb,sorted.get(j),cutoff)){
						int rgb2 = sorted.get(j); //lead color of clump
						clumps.put(rgb2,clumps.get(rgb)+clumps.get(rgb2)); //combine clump quantities
						clumps.remove(rgb); //remove subcol from clumps list
						ArrayList<Integer> addfrom = subcols.get(rgb); //subcols to add into clump
						ArrayList<Integer> addto = subcols.get(rgb2); //original clump
						addfrom.add(0,rgb); //add smaller clump's lead color to its start
						int lastindex = 0;
						//binary sort all of the subcols into the clump by prevalence
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
								}else if(right<addto.size() && colors.get(addto.get(right))>prevalence) {
									lastindex = right+1;
								}else {
									lastindex = right;
								}
							}
							addto.add(lastindex,rgb3);
						}
						//remove the smaller clump from subcol map and sorted list
						subcols.remove(rgb);
						sorted.remove(i);
						//binary sort the merged clump into the sorted list by prevalence
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
			}
			
			System.out.println("S3");
			
			//draw the breakdown for this image
			BufferedImage output = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB); //breakdown for this image
			Graphics g = output.createGraphics();
			
			
			double unit = (double)totalPixels/height; //number of color pixels corresponding to one output pixel
			int pos = 0; //position vertically; which row is being drawn
			int sumPixels = 0; //number of original image's pixels that have been drawn
			for(int s=0; s<sorted.size(); s++) {
				int rgb = sorted.get(s); //lead clump color
				int spos = pos; //starting row of this clump
				ArrayList<Integer> clumped = subcols.get(rgb); //subcolors
				clumped.add(0,rgb);

				int clumpwid = (int)(.5+(height - pos)*(((double)clumps.get(rgb))/(totalPixels - sumPixels))); //amount of rows this clump should take up
				
				sumPixels+=clumps.get(rgb);
				
				//loop through each subcolor
				for(int i=0; i<clumped.size(); i++) {
					int rgb2 = clumped.get(i); //subcolor
					//draw proportional number of rows
					for(int j=0; j<(int)(colors.get(rgb2)/unit) && pos<spos+clumpwid; j++) {
						g.setColor(convertColorI2C(rgb2));
						g.drawLine(0,pos,width,pos);
						pos++;
					}
					//if there is space and this color is less than 1 unit, draw 1 pixel per color to fill space
					if((int)(colors.get(rgb2)/unit)==0 && pos<spos+clumpwid) {
						g.setColor(convertColorI2C(rgb2));
						g.drawLine(0,pos,width,pos);
						pos++;
					}
				}

				
			
				
			}
			//draw this image breakdown onto the grand output
			g2.drawImage(output,vpos,0,null);
			vpos+=width;
		} catch (IOException e) {
			//skip image if filename doesn't exist
			System.out.println("Image "+km+" does not exist");
		}
		
		
		}
		
		
		

		System.out.println("S4");
		
		//save the final output
		System.out.print("Filepath: "+homepath);
		sc.nextLine();
		String filepath_save = homepath+sc.nextLine();
		sc.close();
		System.out.println();
		
		//create file
		File ret = new File(filepath_save);
		ret.getParentFile().mkdirs();
		if(!ret.exists()) {
			try {
				ret.createNewFile();
				ImageIO.write(grandoutput, "png", ret);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	//converts integer color to Color class
	public static Color convertColorI2C(int rgb) {
		int red = (rgb >> 16) & 0xFF;
		int green = (rgb >> 8) & 0xFF;
		int blue = rgb & 0xFF;
		Color c = new Color(red,green,blue);
		return c;
	
	}
	//converts Color class to integer color
	public static int convertColorC2I(Color c) {
		int red = (c.getRed() << 16) & 0x00FF0000;
		int green = (c.getGreen() << 8) & 0x0000FF00;
		int blue = c.getBlue() & 0x000000FF;
		return 0xFF000000 | red | green | blue;
	}
	//converts RGB ints color to integer color
	public static int convertColor32I(int r, int g, int b) {
		int red = (r << 16) & 0x00FF0000;
		int green = (g << 8) & 0x0000FF00;
		int blue = b & 0x000000FF;
		return 0xFF000000 | red | green | blue;
	}
	//converts integer color to RGB ints color
	public static int[] convertColorI23(int rgb) {
		int red = (rgb >> 16) & 0xFF;
		int green = (rgb >> 8) & 0xFF;
		int blue = rgb & 0xFF;
		return new int[] {red,green,blue};
	}
	
	//averages two Colors
	public static Color averageColor(Color c1, Color c2) {
		double red = Math.sqrt(Math.pow(c1.getRed(),2)+Math.pow(c2.getRed(),2))/2;
		double green = Math.sqrt(Math.pow(c1.getGreen(),2)+Math.pow(c2.getGreen(),2))/2;
		double blue = Math.sqrt(Math.pow(c1.getBlue(),2)+Math.pow(c2.getBlue(),2))/2;
		return new Color((int)(.5+red),(int)(.5+green),(int)(.5+blue));
	}
	
	//averages two int colors
	public static int averageColor(int rgb1, int rgb2) {
		int[] cols1 = convertColorI23(rgb1);
		int[] cols2 = convertColorI23(rgb2);
		double red = Math.sqrt(Math.pow(cols1[0],2)+Math.pow(cols2[0],2))/2;
		double green = Math.sqrt(Math.pow(cols1[1],2)+Math.pow(cols2[1],2))/2;
		double blue = Math.sqrt(Math.pow(cols1[2],2)+Math.pow(cols2[2],2))/2;
		return convertColor32I((int)(.5+red),(int)(.5+green),(int)(.5+blue));
	}
	
	//test if two colors are close enough (if distance is less than cut)
	public static boolean colorsClose(Color c1, Color c2, double cut) {
		return colorsDistance(c1,c2)<cut;
	}
	public static boolean colorsClose(int c1, int c2, double cut) {
		return colorsDistance(c1,c2)<cut;
	}
	public static boolean colorsClose(int r1, int g1, int b1, int r2, int g2, int b2, double cut) {
		return colorsDistance(r1,g1,b1,r2,g2,b2) < cut;
	}
	
	//returns a number representing the distance between two colors
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
