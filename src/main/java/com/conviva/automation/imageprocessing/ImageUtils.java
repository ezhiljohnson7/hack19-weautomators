package com.conviva.automation.imageprocessing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.conviva.automation.imageprocessing.Constants.FolderNames;

public class ImageUtils {
	
	private final static Logger LOG = Logger.getLogger(ImageUtils.class);

	public BufferedImage cropImage(BufferedImage bufferedImage, int startX, int startY, int endX, int endY) {

		BufferedImage img = bufferedImage.getSubimage(startX, startY, endX, endY); // fill in the corners of the desired crop location here
		BufferedImage copyOfImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics g = copyOfImage.createGraphics();
		g.drawImage(img, 0, 0, null);

		return copyOfImage;
	}

	public void createImage(BufferedImage bufferedImage) throws IOException {
		
		File outputfile = new File(FolderNames.IMAGES, "scrCopy.png");
		ImageIO.write(bufferedImage, "png", outputfile);
	}
	
	public float compareImage(File fileA, File fileB) {

	    float percentage = 0;
	    try {
	        // take buffer data from both image files //
	        BufferedImage biA = ImageIO.read(fileA);
	        DataBuffer dbA = biA.getData().getDataBuffer();
	        int sizeA = dbA.getSize();
	        BufferedImage biB = ImageIO.read(fileB);
	        DataBuffer dbB = biB.getData().getDataBuffer();
	        int sizeB = dbB.getSize();
	        int count = 0;
	        if (sizeA == sizeB) {

	            for (int i = 0; i < sizeA; i++) {

	                if (dbA.getElem(i) == dbB.getElem(i)) {
	                    count = count + 1;
	                }

	            }
	            percentage = (count * 100) / sizeA;
	        } else {
	            System.out.println("Both the images are not of same size");
	        }

	    } catch (Exception e) {
	        System.out.println("Failed to compare image files ...");
	    }
	    return percentage;
	}
	
	public boolean compareImage(BufferedImage biA, BufferedImage biB) {

		boolean isImageIdentical = false;
	    float percentage = 0;
	    try {
	        // take buffer data from both image files //
	        int count = 0;
	        int totalPixelsInImageA = biA.getHeight() * biA.getWidth();
	        int totalPixelsInImageB = biB.getHeight() * biB.getWidth();
	        if (totalPixelsInImageA == totalPixelsInImageB) {
	            for(int a = 0; a < biA.getWidth(); a++) {
	            	for(int b = 0; b < biA.getHeight(); b++) {
	            		Color iA = new Color(biA.getRGB(a, b));
	            		Color iB = new Color(biB.getRGB(a, b));
	            		if(iA.getAlpha() == iB.getAlpha() && iA.getRed() == iB.getRed() && iA.getGreen() == iB.getGreen() && iA.getBlue() == iB.getBlue()) {
//	            			System.out.println(String.format("ARGB values of pixel(%s,%s) in Image-A is %s,%s,%s,%s and Image-B is is %s,%s,%s,%s", a, b, iA.getAlpha(), iA.getRed(), iA.getGreen(),
//									iA.getBlue(), iB.getAlpha(), iB.getRed(), iB.getGreen(), iB.getBlue()));
	            			count++;
	            		}
	            	}
	            }
	            percentage = (count * 100) / totalPixelsInImageA;
	            System.out.println(String.format("Total no of pixels in the image are %s and %s pixels are perfect match", biA.getWidth()*biA.getHeight(), count));
	            LOG.info(String.format("Out of total %s pixels %s pixels are a perfect match", biA.getWidth()*biA.getHeight(), count));
		        if(percentage == 100.0) {
		        	isImageIdentical = true;
		        	System.out.println("Image-1 and Image-2 are 100% match");
		        } else {
		        	System.out.println(String.format("Image-1 and Image-2 are not identical and they're %.2f percent match", percentage));
		        }
	        } else {
	            System.out.println("Both the images are not of same size");
	        }
	        
	        //Comparing the databuffer instead of pixels argb values.
/*	        DataBuffer dbA = biA.getData().getDataBuffer();
	        int sizeA = dbA.getSize();
	        DataBuffer dbB = biB.getData().getDataBuffer();
	        int sizeB = dbB.getSize();
	        if(sizeA == sizeB) {
	        	for(int i=0; i<sizeA; i++) { 
	                if(dbA.getElem(i) == dbB.getElem(i)) {
	                	System.out.println(String.format("The %sth element data from the data-buffer in Image-A is %s and Image-B is %s", i, dbA.getElem(i), dbB.getElem(i)));
	                }
	            }
	        }*/
	    } catch (Exception e) {
	        System.out.println("Failed to compare image files ...");
	    }
	    return isImageIdentical;
	}

}
