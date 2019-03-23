/**
 * Timeline.java
 * Coordinates thumbnails and transitions
 * 
 * Slideshow Creator
 * Timothy Couch, Joseph Hoang, Fernando Palacios, Austin Vickers
 * CS 499 Senior Design with Dr. Rick Coleman
 * 2/13/19
 */

package core;

import java.util.ArrayList;

public class Timeline
{
    public ThumbnailsList thumbnailsList;
    public TransitionsList transitionsList;

    /**
     * Timeline - initializes an empty timeline
     * 
     * @author Timothy Couch
     */
    public Timeline()
    {
        thumbnailsList = new ThumbnailsList();
        transitionsList = new TransitionsList();
    }
    
    /**
     * UpdateProjectSettings - updates the settings
     * 
     * @author Austin Vickers
     */
    public void UpdateProjectSettings(Settings s) {
    	s.PrintAll();
    	
    	System.out.println("Timeline Settings updated!");
    }
    
    /**
     * UpdateTransitionSettings - updates all transition settings
     * 
     * @author Fernando Palacios
     */
    public void UpdateTransitionSettings(double speed, TransitionType type)
    {
    	//Update all transition lengths and types
    	ArrayList<Transition> list = transitionsList.getTransitions();
    	for(Transition t : list) {
    		t.setTransitionLength(speed);
    		t.setTransitionType(type);
    	}
    }

    /**
     * Adds the given slide to the timeline with a default transition
     * @param thumbnail the thumbnail to add
     * 
     * @author Timothy Couch
     */
    public void addSlide(Thumbnail thumbnail)
    {
        thumbnailsList.addThumbnail(thumbnail);
        transitionsList.addTransition(new Transition(TransitionType.NONE, 1));
    }

    /**
     * Removes the slide at the specified index from the timeline
     * @param index thumbnail and transition combo to remove
     * @return whether successful
     * 
     * @author Timothy Couch
     */
    public boolean removeSlide(int index)
    {
        thumbnailsList.removeThumbnail(thumbnailsList.getThumbnail(index));
        transitionsList.removeTransition(transitionsList.getTransition(index));
        return true;
    }
}
