/**
 * Thumbnail.java
 * Holds and resizes as needed an image for slideshow
 * 
 * Slideshow Creator
 * Timothy Couch, Joseph Hoang, Fernando Palacios, Austin Vickers
 * CS 499 Senior Design with Dr. Rick Coleman
 * 2/13/19
 */

package core;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Thumbnail
{
	private static Coord2 thumbSize = new Coord2(100, 75);
	
    /**
     * Publicly read-only path to image
     */
    private String imagePath;

    public String getImagePath() {
        return imagePath;
    }

    /**
     * Publicly read-only image itself
     */
    private Image imageRaw;

    public Image getImageRaw() {
        return imageRaw;
    }
    
    /**
     * Publicly read-only image resized to thumbnail size
     */
    private Image imageThumb;
    
    public Image getImageThumb() {
    	return imageThumb;
    }

    /**
     * Thumbnail - creates an image from given path
     * @param imagePath string file path to image (can be from current directory or full directory)
     * 
     * @author Timothy Couch
     */
    public Thumbnail(String imagePath)
    {
        this.imagePath = imagePath;
        imageRaw = loadImage(imagePath);
        imageThumb = resizeImageThumb(imageRaw);
    }

    /**
     * loadImage - loads up an image from the path
     * @param imagePath the path from which to load the image
     * 
     * @return image if successfully loaded image, null otherwise
     */
    private Image loadImage(String imagePath)
    {
        File imFile = new File(imagePath);
        Image image = null;

        //load the image. Hopefully doesn't actually throw exceptions
        try
        {
            //load up the image if it exists - this hopefully prevents exceptions
            if (imFile != null && imFile.exists())
                image = ImageIO.read(imFile);
            else System.out.println("Raw image not loaded! " + imagePath + " does not exist");
        }
        catch (IOException e)
        {
            System.out.println("Exception: Raw image not loaded! " + imagePath + "\n" + e);
        }

        return image;
    }
    
    /**
     * resizeImageThumb - resizes the raw image to the proper size for thumbnails
     * @return resized image
     */
    private Image resizeImageThumb(Image image)
    {
    	//x is negative to preserve aspect ratio
    	return image.getScaledInstance(-thumbSize.x, thumbSize.y, Image.SCALE_DEFAULT);
    }
    
    /**
     * drawFill - draws the thumbnail image into the specified graphics in the container
     * @param g graphics to draw into (JLabel paintComponent method argument)
     * @param container the container in which to draw this image
     * @return the result of g.drawImage (false if still in process of drawing, true otherwise)
     * 
     * @author Timothy Couch
     */
    public boolean drawFill(Graphics g, Container container)
    {
	  Image displayImage = imageRaw;
	  
	  //calculate position and size to draw image with proper aspect ratio
	  int[] drawCoords = getLetterBoxCoords(displayImage, container);
	  
	  //draw image
	  return g.drawImage(displayImage, drawCoords[0], drawCoords[1], drawCoords[2], drawCoords[3], null);
    }
    
    /**
     * getLetterBoxCoords - returns the x, y, width, and height of the proper display position for the image in the container at a letterbox aspect ratio
     * @param image the image to draw
     * @param container the container to fit the image in
     * @return int[4]: {x, y, width, height}
     * 
     * @author Timothy Couch
     */
    public static int[] getLetterBoxCoords(Image image, Container container)
    {
    	int[] dimensions = new int[4];
    	
    	//get raw dimensions
		Dimension containerDim = container.getSize();
		double containerWidth = containerDim.getWidth();
		double containerHeight = containerDim.getHeight();
		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);
		
		//determine if borders need to be vertical or horizontal
		double imageAspect = (double) imageWidth / imageHeight;
		double containerAspect = containerWidth / containerHeight;
		
		//borders on top and bottom
		if (imageAspect > containerAspect)
		{
			//get multiplier to make image smaller
			double imageScale = containerWidth / imageWidth;
			
			//y is down some to make image in middle
			dimensions[1] = (int) Math.round(((containerHeight - imageHeight * imageScale) / 2));
			
			//make image smaller
			dimensions[2] = (int) Math.round(containerWidth);
			dimensions[3] = (int) Math.round(imageHeight * imageScale);
		}
		else//borders on left and right
		{
			//get multiplier to make image smaller
			double imageScale = containerHeight / imageHeight;
			
			//x is down some to make image in middle
			dimensions[0] = (int) Math.round(((containerWidth - imageWidth * imageScale) / 2));
			
			//make image smaller
			dimensions[2] = (int) Math.round(imageWidth * imageScale);
			dimensions[3] = (int) Math.round(containerHeight);
		}
    	
    	return dimensions;
    }
}
