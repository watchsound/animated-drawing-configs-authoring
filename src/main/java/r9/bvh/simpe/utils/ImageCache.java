package r9.bvh.simpe.utils;
 

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import r9.bvh.simple.Settings;
 
  
public class ImageCache {

	public static final ImageCache sharedInstance = new ImageCache();
	
	public static class ImageUnit {
		public BufferedImage image;
		public ImageUnit(BufferedImage image) {
			this.image = image;
		}
	}
	
	//private LRUCache<String, Image>  imagecache;
	private LRUCache<String, ImageUnit>  bimagecache;
	private LRUCache<String, ImageUnit>  bimagecacheLarge;
	
	private ImageCache(){
	//	imagecache = new LRUCache<String, Image>(10);
		bimagecache = new LRUCache<String, ImageUnit>(50);
		bimagecacheLarge = new LRUCache<String, ImageUnit>(5);
	}
	
	public void clearAll(){
		//imagecache.clear();
		bimagecache.clear();
		bimagecacheLarge.clear();
	}
	public void remove(String fileName){
		//imagecache.clear();
		bimagecache.remove(fileName);
		bimagecacheLarge.remove(fileName);
	}
	
	public ImageUnit get(File tempDest, String fileName){
		File imagefile = new File(tempDest,fileName);
		if( !imagefile.exists() ){
			return null;
		} 
		return get(imagefile);
	}
	public ImageUnit get(File file){
		ImageUnit bimage = bimagecache.get(file.getName());
		if( bimage== null)
			bimage = bimagecacheLarge.get(file.getName());
		if( bimage == null ){ 
			BufferedImage image;
			try {
				  image = ImageIO.read(file);
			} catch (IOException e) {
				u.p(e);
				return null; 
			}
			bimage = new ImageUnit(image);
			if( image.getWidth() > Settings.CARD_WIDTH *.8 &&  image.getHeight() > Settings.CARD_HEIGHT *0.8)
				bimagecacheLarge.put(file.getName(), bimage);
			else
			    bimagecache.put(file.getName(), bimage);
		//	imagecache.put(fileName,Image.create(bimage) ); 
		}
		return bimage;
	}
	
	public ImageUnit getCached(String fileName,  BufferedImage image){
		ImageUnit bimage = bimagecache.get(fileName);
		if( bimage== null)
			bimage = bimagecacheLarge.get(fileName);
		if( bimage != null )
			return bimage;
		if(image != null) {
			ImageUnit u =  putOrReplace(fileName, image);
		    return u;
		}
		if( fileName != null ) {
			File f = new File(fileName);
			if( !f.exists() ) {
				f = Settings.getEditingDir();
				if( f != null ) {
					f = new File(f, fileName);
				}
			} 
			if( f.exists() ) {
				return get(f);
			}
			if( !fileName.endsWith(".png") ) {
				f = Settings.getEditingDir();
				if( f != null ) {
					f = new File(f, fileName + ".png");
				}
			}
			if( f.exists() ) {
				return get(f);
			}
		}
		return null;
	}
	
	public ImageUnit putOrReplace(String fileName,  BufferedImage bimage){
		ImageUnit unit = new ImageUnit(bimage);
		if(bimage.getWidth() > Settings.CARD_WIDTH *.8 && bimage.getHeight() > Settings.CARD_HEIGHT *0.8)
			bimagecacheLarge.put(fileName, unit);
		else
		    bimagecache.put(fileName, unit);
		return unit;
	}
	public ImageUnit putOrReplace(File file){
		BufferedImage bimage;
		try {
			bimage = ImageIO.read(file);
		} catch (IOException e) {
			return null;
		}
		ImageUnit unit = new ImageUnit(bimage);
		if(bimage.getWidth() > Settings.CARD_WIDTH *.8 && bimage.getHeight() > Settings.CARD_HEIGHT *0.8)
			bimagecacheLarge.put(file.getName(), unit);
		else
		    bimagecache.put(file.getName(), unit);
		return unit;
	}
	
	
	
}
