/**
 * Trans_WipeLeft.java
 * Wipes from right to left from one image to another
 * Original Author: R Coleman
 * Modified by Timothy Couch
 * 
 * Slideshow Creator
 * Timothy Couch, Joseph Hoang, Fernando Palacios, Austin Vickers
 * CS 499 Senior Design with Dr. Rick Coleman
 * 4/11/19
 */

package pkgImageTransitions.Transitions;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import core.SliderColor;
import core.Thumbnail;
import pkgImageTransitions.ColemanTransition;

public class Trans_WipeLeft extends ColemanTransition
{

	/** How many time to fade between the images per second. Updates on the fly based on machine performance */
	protected static int fps = 50;
	
	//---------------------------------------------------
	/** Perform the transition from one image to another */
	// Args:  
	//  imgPanel - Panel the images are drawn into
	//	ImageA - Current Image on the screen
	//  ImageB - Image to transition to
	//  time - Number of seconds to take to do this transition
	// Note both off screen BufferedImages have already been
	// scaled to exactly fit in the area of the imgPanel
	// Note 2: We need some way of estimating the time required
	//    to do a draw otherwise the time value is useless.
	//    Some platforms execute the transitions very rapidly
	//    others take much longer
	// Basic algorithm:
	//   For each iteration
	//      Copy from B iterationIndex * incX of B onto the screen overwriting Image A that is there
	//	        Sections of B are drawn from right to left
	//---------------------------------------------------------
	public void DrawImageTransition(JPanel imgPanel, BufferedImage ImageA, BufferedImage ImageB, double time)
	{
		Graphics gPan = imgPanel.getGraphics();
		
		// Dimension holders
		int bX1, bX2;		// Dimensions for imageB
		int imgWidth, imgHeight;
		int incX;					// X increment each time

		int numIterations = (int) (fps * time);
		int timeMillis = (int) (time * 1000);
		int timeInc = timeMillis / numIterations; // Milliseconds to pause each step
		
		imgWidth = imgPanel.getWidth();
		imgHeight = imgPanel.getHeight();
		incX = imgWidth / numIterations;		// Do 1/numIterations each time
		
		//create an image A the size of the container
		BufferedImage contImageA = new BufferedImage(imgPanel.getWidth(), imgPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Thumbnail.drawImageFillImage(ImageA, contImageA, SliderColor.dark_gray);
		Graphics gA = contImageA.getGraphics();
		
		//create an image B the size of the container with solid background
		BufferedImage contImageB = new BufferedImage(imgPanel.getWidth(), imgPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Thumbnail.drawImageFillImage(ImageB, contImageB, SliderColor.dark_gray);
		
		// Initialize the dimensions for section of ImageB to draw into ImageA
		bX1 = imgWidth - incX;
		bX2 = incX;
		
		// Draw image A
		int avgElapsedTime = 0;//how much time each fade step takes on average
		for(int i=0; i<numIterations; i++)
		{
			if (isAborting())
				break;
			
			long startTime = System.currentTimeMillis();
			
			// Draw part of B over A on the screen
			gA.drawImage(contImageB, bX1, 0, imgWidth, imgHeight, bX1, 0, imgWidth, imgHeight, null); // Draw portion of ImageB into ImageA
			gPan.drawImage(contImageA, 0,0, imgPanel); // Copy ImageA into panel
			bX2 = bX1;
			bX1 -= incX;  // Move another section to the left of the previous section
			
			// Pause a bit so we can actually see the transition
			try 
			{
				int elapsedTime = (int) (System.currentTimeMillis() - startTime);
				avgElapsedTime += elapsedTime;
			    Thread.sleep(Math.max(timeInc - elapsedTime, 0));
			} 
			catch(InterruptedException ex) 
			{
			    Thread.currentThread().interrupt();
			}
		}
		
		if (!isAborting())
		{
			//adjust number of iterations to get a proper transition for next time
			avgElapsedTime /= numIterations;
			
			int prevFps = fps;

			//set fps to how many frames of the average elapsed time will fit into one second
			fps = Math.min(Math.max(Math.round(timeMillis / avgElapsedTime), 5), 60);//limit framerate to between 5 and 60 fps
			
			System.out.println("timeInc: " + timeInc + " avgElapsedTime: " + avgElapsedTime + "\nprevFps: " + prevFps + " fps: " + fps);
		}
	}

}
