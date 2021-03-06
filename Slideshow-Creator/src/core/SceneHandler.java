/**
 * SceneHandler.java
 * Controls and manages the overall program
 * Singleton class
 * 
 * Slideshow Creator
 * Timothy Couch, Joseph Hoang, Fernando Palacios, Austin Vickers
 * CS 499 Senior Design with Dr. Rick Coleman
 * 2/10/19
 */
package core;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.*;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.simple.parser.ParseException;

public class SceneHandler {
	
	/**
	 * the one instance of SceneHandler that exists
	 */
	public static SceneHandler singleton;

	/**
	 * appType - which program is running
	 */
	private AppType appType;
	
	public AppType getAppType() {
		return appType;
	}
	
	/**
	 * timeline - to be created on startup if loading a directory
	 * 			- to be loaded in by TimelineParser if loading a file
	 */
	private Timeline timeline;
	
	public Timeline getTimeline() {
		return timeline;
	}
	
	/**
	 * directory - directory where the slideshow is working and using images
	 */
	private String directory = "";

	public void setDirectory(String dir)
	{
		directory = dir;
		timeline = new Timeline();
	}
	
	/**
	 * set directory based on the file supplied
	 * @param file the file to parse and make a timeline from
	 */
	public void setDirectory(File file) throws Exception, NullPointerException, ParseException
	{
		try
		{
			timeline = TimelineParser.ImportTimeline(file.getAbsolutePath());
			if(timeline != null)
			{
				directory = timeline.getDirectory();
			}
			
			//System.out.println("Dir: " + directory);
			//System.out.println("File: " + file.getAbsolutePath());
		}catch (ParseException pe) {
			System.out.println("Invalid file given. Cannot import:" + pe.getMessage());
			throw pe;
		}catch (NullPointerException npe){
			System.out.println("Invalid file given. Cannot import:" + npe.getMessage());
			throw npe;
		}
		catch(Exception e) {
			System.out.println("Invalid file given. Cannot import:" + e.getMessage());
			throw e;
		}
	}

	public String getDirectory()
	{
		return directory;
	}
	
	/**
	 * mainFrame - window frame of program
	 */
	private JFrame mainFrame;
	
	public JFrame getMainFrame()
	{
		return mainFrame;
	}
	
	/* The dictionary of scenes in the current context */
	private HashMap<SceneType, Scene> scenes;
	/* The currently selected scene type */
	private SceneType currentScene;
	
	public final Map<TransitionType, ImageIcon> transitionImages;
	
	/**
	 * SceneHandler - creates program with specified app type
	 * @param aT AppType to open
	 * 
	 * @author Timothy Couch
	 * @author austinvickers
	 */
	public SceneHandler(AppType aT)
	{
		//the first created SceneHandler is the real one. There should never be another one, but just making sure
		if (singleton == null)
			singleton = this;
		appType = aT;
		scenes = new HashMap<SceneType, Scene>();
		currentScene = SceneType.NONE;
		
		//set up transition images
		transitionImages = new HashMap<TransitionType, ImageIcon>();
		transitionImages.put(TransitionType.NONE, new ImageIcon(getClass().getResource("/core/TransitionImages/none.png")));
		transitionImages.put(TransitionType.CROSS_DISSOLVE, new ImageIcon(getClass().getResource("/core/TransitionImages/crossFade.png")));
		transitionImages.put(TransitionType.WIPE_DOWN, new ImageIcon(getClass().getResource("/core/TransitionImages/wipeDown.png")));
		transitionImages.put(TransitionType.WIPE_LEFT, new ImageIcon(getClass().getResource("/core/TransitionImages/wipeLeft.png")));
		transitionImages.put(TransitionType.WIPE_RIGHT, new ImageIcon(getClass().getResource("/core/TransitionImages/wipeRight.png")));
		transitionImages.put(TransitionType.WIPE_UP, new ImageIcon(getClass().getResource("/core/TransitionImages/wipeUp.png")));
		
		launch();
	}
	
	/**
	 * launch - opens the program's main frame
	 * @return true if successfully opened, false otherwise
	 * 
	 * @author Timothy Couch
	 * @author austinvickers
	 */
	public boolean launch()
	{
		ImageIcon slideshowIcon = new ImageIcon(getClass().getResource("/core/Images/slideshowIcon.png"));
		
		if(appType == AppType.CREATOR) {
			timeline = new Timeline();
		}
		
		//set up default window
		mainFrame = new JFrame();
		mainFrame.setExtendedState(mainFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		mainFrame.setTitle("Slideshow " + appType.getTitle());
		mainFrame.setIconImage(slideshowIcon.getImage());
		mainFrame.setMinimumSize(new Dimension(700, 590));
		
		//set up quit confirmation dialog
		//Thanks to https://stackoverflow.com/questions/21330682/confirmation-before-press-yes-to-exit-program-in-java
		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainFrame.addWindowListener(new WindowAdapter() {
			  public void windowClosing(WindowEvent e) {
				  //only show quit confirm in creator and not in directory select scene
				  if (appType == AppType.CREATOR && GetCurrentScene().getSceneType() != SceneType.DIRECTORY)
				  {
					  int confirmed = JOptionPane.showConfirmDialog(null,
							  "Are you sure you want to exit the program?\n\nAny unsaved changes will be lost.", "Confirm Exit",
							  JOptionPane.YES_NO_OPTION);
					  if (confirmed == JOptionPane.YES_OPTION) {
						  exitProgram();
					  }
				  }
				  else exitProgram();
			  }
			});

		mainFrame.setVisible(true);
		
		return true;
	}
	
	/**
	 * Exits the program! Woot!
	 * 
	 * @author Timothy Couch
	 * @author Austin Vickers
	 */
	public void exitProgram() {
		  restartProgram();
	      mainFrame.dispose();
	      System.exit(1);
	}
	
	/**
	 * AddScene - adds a new scene to the context. There may only be one of each type of scene.
	 * 
	 * @param type - the scene type
	 * @param scene - an instance of the scene you want to add
	 * 
	 * @author austinvickers
	 */
	public void AddScene(SceneType type, Scene scene) {
		
		if(!scenes.containsKey(type)) {
			scenes.put(type, scene);
			if (scene.getSceneType() == null)
			{
				scene.setSceneType(type);
			}
			else System.out.println("That scene already has type " + scene.getSceneType().getTitle() + ". This is strange and likely incorrect.");
		}
		else {
			System.out.println("That scene already exists in the context. Use SwitchToScene() to switch to it");
		}
	}
	
	/**
	 * SwitchToScene - Switches to a scene based on the type that was passed in
	 * @param target - the scene object to be switched to
	 * 
	 * @author austinvickers
	 * @author Timothy Couch
	 */
	public void SwitchToScene(SceneType target) {
		
		if(scenes.containsKey(target)) {
			
			//hide scene
			if (GetCurrentScene() != null)
				GetCurrentScene().hide();
			mainFrame.getContentPane().removeAll();

			//show scene
			currentScene = target;
			mainFrame.getContentPane().add(GetCurrentScene());
			GetCurrentScene().show();
			mainFrame.revalidate();
			mainFrame.repaint();
		}
		else {
			System.out.println("That scene does not exist in the current context.");
		}	
	}
	
	/**
	 * GetCurrentScene - returns the Scene object that is currently active
	 * @return - the active Scene
	 * 
	 * @author austinvickers
	 */
	public Scene GetCurrentScene() {
		return scenes.get(currentScene);
	}
	
	public Scene GetSceneInstanceByType(SceneType type) {
		
		if(scenes.containsKey(type)) {
			return scenes.get(type);
		}
		else {
			System.out.println("That scene does not exist in the current context.");
			return null;
		}
	}
	
	/**
	 * makes all scenes rerun initialize method on next show (effectively clears out the working information in the scene)
	 * 
	 * @author Timothy Couch
	 */
	public void restartProgram()
	{
		//thanks to karim79 for how to iterate through a map https://stackoverflow.com/questions/1066589/iterate-through-a-hashmap
		Iterator<Map.Entry<SceneType, Scene>> scenesIt = scenes.entrySet().iterator();
		while (scenesIt.hasNext())
		{
			Scene scene = scenesIt.next().getValue();
			scene.destroy();
		}
		timeline = null;
		System.gc();
	}
}
