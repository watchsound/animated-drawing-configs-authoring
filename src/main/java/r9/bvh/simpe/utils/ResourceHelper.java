package r9.bvh.simpe.utils;
 
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import r9.bvh.simpe.icons.SManager;
import r9.bvh.simple.Settings;
 
  
  
public class ResourceHelper implements java.io.Serializable{
	 	
		private static final long serialVersionUID = 1L;
		
		public static ResourceHelper sharedInstance = new ResourceHelper();
		 
		
		Properties props;
		String lang;
		
		private ResourceHelper(){
			reloadData();  
		}
		
		public Properties getProperties(){
			return props;
		}
		
		public void reloadData(){
			props =  loadProperties();  
		}
		
		//a set of shortcut
		public List<String>  getNiceFeedbacks(){
			String value = props.getProperty("NICE_FEEDBACK");
			if ( value == null ){
				List<String> result = new ArrayList<String>();
				result.add("Great Job!");
				result.add("Wanderful!");
				result.add("You Got it!");
				result.add("Excellent!");
				return result;  
			} else {
				return new ArrayList<String>(Arrays.asList( value.split("#") ));
			}
		}
		
		public void addNiceFeedback(String feedback){
			List<String>  result = getNiceFeedbacks();
			result.add(feedback);
			saveNiceFeedbacks(result);
		}
		
		 
		public void saveNiceFeedbacks(List<String> tools){
		    StringBuilder sb = new StringBuilder();
		    for( String tc : tools){
		       	sb.append("#" + tc ); 
		    }
		    String value = sb.length() > 1 ?  sb.substring(1) : null;
		    props.setProperty("NICE_FEEDBACK", value); 
		    save();
		}
		
		public List<String>  getAwardIcons(){
			String value = props.getProperty("AWARD_ICONS");
			if ( value == null ){
				List<String> result = new ArrayList<String>();
				File awardiconDir = new File(Settings.IMAGES_DIR, "awardicons"); 
	         	if ( awardiconDir.exists() ){
	         		for( File sf : awardiconDir.listFiles()){
	         			result.add(sf.getName()); 
	         		}
	         	} 
	         	setAwardIcons(result);  
				return result;  
			} else {
				return new ArrayList<String>(Arrays.asList( value.split("#") ));
			}
		}
		
		public String importAwardIcon(File file){
			BufferedImage img = null;
			try {
				  img = ImageIO.read(file);
				  img =  ImageUtil.resizeImage(img, 46, 46);
			} catch (IOException e1) {
				u.p(e1);
				img = null;
			}
			if( img == null )
				return "";
			
			 String name =  file.getName();
	         name =  StringUtils.normalizeFileName( name );
	         File awardiconDir = new File(Settings.IMAGES_DIR, "awardicons"); 
	         File tempimage = new File(awardiconDir,name);
	         if (! tempimage.exists() )
				try {
					 ImageUtil.saveImageToFile(img, tempimage.getAbsolutePath(), 100);
					List<String> icons = getAwardIcons();
					icons.add(name);
					 setAwardIcons(icons);
					return name;
			 	} catch (IOException e) {
					 e.printStackTrace();
					 return "";
				} 
	         return "";
		}
		
		 
		public void setAwardIcons(List<String> tools){
			if(tools.isEmpty()) return;
		    StringBuilder sb = new StringBuilder();
		    for( String tc : tools){
		       	sb.append("#" + tc ); 
		    }
		    String value = sb.length() > 1 ?  sb.substring(1) : null;
		    props.setProperty("AWARD_ICONS", value); 
		    save();
		}
		
		 
		public void save(){
			save( props );
		}
		
		 
	    public void save(Properties props){
	     	 String lang_posfix = lang == null || lang.length() == 0 ? "" : "_" + lang;
		     save(props, Settings.homedir , "r9_nicefeedback" + lang_posfix + ".properties" );
	    }
	    
	    public static void save(Properties props, File dir, String name){
			 File pfile = new File( dir, name);
			 if (! pfile.exists() )
				try {
					pfile.createNewFile();
				} catch (IOException e) {
					u.p(e);
				}
			 OutputStream is = null;
			 try {
				 is = new FileOutputStream( pfile );
				 props.store(is, "");
			} catch (Exception e) {
				u.p(e);
			} finally{
				if ( is != null)
					try {
						is.close();
					} catch (IOException e) {
						u.p(e);
					}
			}
		}
	    
	  
		
	    private  Properties loadProperties( ){
	    	 lang = "";//  AuthoringWorkspace.singleInstance.getMainDocument().getLangCode();
	    	 String lang_posfix = lang == null || lang.length() == 0 ? "" : "_" + lang;
			 return loadProperties( Settings.homedir , "r9_nicefeedback" + lang_posfix + ".properties"); 
		}
	    
		public static Properties loadProperties(File dir, String name){
			 Properties props = new Properties();
			 
			 File pfile = new File( dir , name);
			 if ( pfile.exists() ){
				 InputStream is = null;
				 try {
					 is = new FileInputStream( pfile );
					 props.load(is);
				} catch (Exception e) {
					u.p(e);
				} finally{
					if ( is != null)
						try {
							is.close();
						} catch (IOException e) {
							u.p(e);
						}
				}
			 }
			 return props;
		}
		 
	  
		 
		private static LRUCache<String, BufferedImage>  toolIconCache =
				new  LRUCache<String, BufferedImage>(10);
	
		
		public static BufferedImage getToolBufferedImageIcon(String iconName){
			return getToolBufferedImageIcon(SManager.class, iconName);
		}
		
		public static String getResource(Class<?> clazz, String resouceName){ 
			URL url = clazz.getResource( resouceName);
			InputStream is = null;
			try {
			     is = url.openStream();
				 return u.fileToString(is);  
			} catch (Exception e) {
				u.p(e);
				return null;
			}finally{
				if ( is != null )
					try {
						is.close();
					} catch (IOException e) {
						u.p(e);
					} 
			}
		}
		
		public static BufferedImage getToolBufferedImageIcon(Class clazz, String iconName){
			BufferedImage image = toolIconCache.get(iconName);
			if( image != null )
				return image;
			
			URL url = clazz.getResource( iconName);
			try {
				image =  ImageIO.read( url ); 
				toolIconCache.put(iconName, image);
			} catch (Exception e) { 
			}
		  	return image;
		}
		
		public static ImageIcon getToolIcon(String iconName){
			return getToolIcon(iconName, 16);
		}
		public static ImageIcon getToolIcon(String iconName, int size){ 
			try {
				BufferedImage simage = getToolBufferedImageIcon( iconName );
				if( size <=0 ) size = 16;
				return new ImageIcon(ImageUtil.resizeImage(simage, size, size)); 
			} catch (Exception e) {
				 
			}
		  	return null;
		}
		public static BufferedImage getToolIconImage(String iconName){ 
		  	return getToolBufferedImageIcon(iconName);
		}
		
		public static ImageIcon getToolIcon(String iconName, int w, int h){ 
			try {
				BufferedImage simage = getToolBufferedImageIcon( iconName );
				return new ImageIcon(ImageUtil.resizeImage(simage, w, h, false, false)); 
			} catch (Exception e) {
				 
			}
		  	return null;
		}
		public static BufferedImage getToolIconImage(String iconName, int w, int h){ 
			try {
				BufferedImage simage = getToolBufferedImageIcon( iconName );
				return ImageUtil.resizeImage(simage, w, h, false, false); 
			} catch (Exception e) {
				 
			}
		  	return null;
		}
		public static BufferedImage getImageResized(String projectName, String imageName, int width, int height){
		 	 File imageFile = new File(Settings.getEditingDir(projectName), imageName);
			 return getImageResized(imageFile, width, height);
		} 
		public static BufferedImage getImageResized(File imageFile){
			 return getImageResized( imageFile, 16, 16);
		} 
		public static BufferedImage getImageResized(File imageFile, int width, int height){
			 if ( imageFile != null &&  imageFile.exists() ){
				  try {
					  BufferedImage simage =  ImageIO.read(imageFile);
					  return ImageUtil.resizeImage(simage, width, height); 
				} catch (IOException e) { 
				}
			  }  
			 return null;
		} 
	  

		public static void copyImageToWorkspace(String imageName, String projectName) {
			  File poster = new File(Settings.IMAGES_DIR,imageName);
		      File posterTo = new File(Settings.getEditingDir(projectName),imageName);
		      if (! posterTo.exists() ){
					try {
						u.copy(poster, posterTo); 
					} catch (IOException e1) {
						u.p(e1);
					}
		      }
		}
	}

