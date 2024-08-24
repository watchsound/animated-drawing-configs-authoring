package r9.bvh.simple;

import java.io.File;

import r9.bvh.simpe.utils.u;

public class Settings {

	public static final String SCRIPTS_DIR = null;
	public static final File homedir = new File(u.getBaseStorageDir("fb_animation"));
	
	public static final String RETARGET_DIR = "fb_retarget";
	public static final String BVH_DIR = "bvh";
	public static final String CHARACTER_DIR = "fb_characterfigs";
	public static final String EXPORT_DIR = "exports";
	
	public static final String IMAGES_DIR = null;
	
	public static int CANVAS_MARGIN = 50;
    public static  int PREVIEW_WIDTH = 370;
    public static  int PREVIEW_HEIGHT = 300;
    
    public static  int PREVIEW_SCENE_WIDTH = 160;
    public static  int PREVIEW_SCENE_HEIGHT = 200;
     
	
	public static final double CARD_WIDTH = 400;
	public static final double CARD_HEIGHT = 500;  
	public static final int SIDE_MENU_WIDTH = 80; 

	public static String getEditingDir(String projectName) {
		 return new File(homedir, projectName).getAbsolutePath();
	}

	public static File getEditingDir() {
		return new File(homedir, "temp"); 
	}

}
