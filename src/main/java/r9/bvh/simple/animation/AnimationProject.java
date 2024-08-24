package r9.bvh.simple.animation;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import r9.bvh.simpe.utils.ImageUtil;
import r9.bvh.simpe.utils.u;
import r9.bvh.simple.Settings;
import r9.bvh.simple.model.Skeleton;
import r9.bvh.simple.retarget.RetargetConfig;
import r9.bvh.simple.ui.fbfig.CharacterConfig;

public class AnimationProject {
	public static final String MVC = "mvc.yaml";
	public static final String MOTION = "motion.yaml";
	public static final String CHARACTER = "character.yaml";
	public static final String RETARGET = "retarget.yaml";
	public static final String SKELETON = "skeleton.bvh";
	public static final String IMAGE = "image.png";
	public static final String MASK = "mask.png";
	public static final String TEXTURE = "texture.png";

	public String name;
	// mvc.yaml
	public MvcConfig mvcConfig;
	// motion.yaml
	public MotionConfig motionConfig;
	// image.png
	private BufferedImage image;
	// mask.pngy
	private BufferedImage mask;
	// texture.png
	private BufferedImage texture;
	// character.yaml
	public CharacterConfig characterConfig;
	// retarget.yaml
	public RetargetConfig retarget;
	// skeleton.bvh
	public Skeleton skeleton;

	public File folder;

	public AnimationProject() {
	}

	public AnimationProject(File dir) {
		this.name = dir.getName();
		this.folder = dir;

		reloadMVC();

		reloadMotion();

		reloadCharacter();

		reloadSkeleton();

		reloadRetarget();

	}

	public void reloadMVC() {
		File f = new File(folder, MVC);
		if (f.exists()) {
			try {
				mvcConfig = MvcConfig.loadFromFile(f);
			} catch (Exception e) {
				e.printStackTrace();
				mvcConfig = new MvcConfig();
			}
		} else {
			mvcConfig = new MvcConfig();
		}
	}

	public void reloadMotion() {
		File f = new File(folder, MOTION);
		if (f.exists()) {
			try {
				motionConfig = MotionConfig.loadFromFile(f);
			} catch (Exception e) {
				e.printStackTrace();
				motionConfig = new MotionConfig();
			}
		} else {
			motionConfig = new MotionConfig();
		}
	}

	public void reloadCharacter() {
		File f = new File(folder, CHARACTER);
		if (f.exists()) {
			try {
				characterConfig = CharacterConfig.loadFromFile(f);
			} catch (Exception e) {
				e.printStackTrace();
				characterConfig = new CharacterConfig();
			}
		} else {
			characterConfig = new CharacterConfig();
		}
	}

	public void reloadSkeleton() {
		File f = new File(folder, SKELETON);
		if (f.exists()) {
			try {
				skeleton = new Skeleton(f);
			} catch (Exception e) {
				e.printStackTrace();
				skeleton = null;// new Skeleton();
			}
		} else {
			skeleton = null;// new Skeleton();
		}
	}

	public void reloadRetarget() {
		File f = new File(folder, RETARGET);
		if (f.exists()) {
			try {
				retarget = RetargetConfig.loadFromFile(f);
			} catch (Exception e) {
				e.printStackTrace();
				retarget = new RetargetConfig();
			}
		} else {
			retarget = new RetargetConfig();
		}
	}

	public boolean hasImage() {
		return new File(folder, IMAGE).exists();
	}

	public boolean hasTexture() {
		return new File(folder, TEXTURE).exists();
	}

	public boolean hasMask() {
		return new File(folder, MASK).exists();
	}

	public void saveMVC() {
		if( mvcConfig == null ) throw new RuntimeException("mvcConfig 文件缺失");
		File f = new File(folder, MVC);
		String yamlStr = mvcConfig.toYaml();
		try {
			u.streamToFile(new ByteArrayInputStream(yamlStr.getBytes()), f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveMotion() {
		if( motionConfig == null ) throw new RuntimeException("motionConfig 文件缺失");
		File f = new File(folder, MOTION);
		String yamlStr = motionConfig.toYaml();
		try {
			u.streamToFile(new ByteArrayInputStream(yamlStr.getBytes()), f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveCharacter() {
		if( characterConfig == null ) throw new RuntimeException("characterConfig 文件缺失");
		File f = new File(folder, CHARACTER);
		String yamlStr = characterConfig.toYaml();
		try {
			u.streamToFile(new ByteArrayInputStream(yamlStr.getBytes()), f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveSkeleton() {
		if( skeleton == null ) throw new RuntimeException("Skeleton 文件缺失");
		File f = new File(folder, SKELETON);
		String yamlStr = skeleton.toBVHFile();
		try {
			u.streamToFile(new ByteArrayInputStream(yamlStr.getBytes()), f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveRetarget() {
		if( retarget == null ) throw new RuntimeException("retarget 文件缺失");
		File f = new File(folder, RETARGET);
		String yamlStr = retarget.toYaml();
		try {
			u.streamToFile(new ByteArrayInputStream(yamlStr.getBytes()), f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void save() {
		saveMVC();

		saveMotion();

		saveCharacter();

		saveSkeleton();

		saveRetarget();
	}
	
	public BufferedImage getImage() {
		if( this.image != null) 
			return this.image;
		File f = new File(folder, IMAGE); 
		try {
			if( f.exists())
			  this.image = ImageIO.read(f);
		} catch (IOException e) { 
			e.printStackTrace();
		}
		return this.image;
	}
	

	public void saveImage(File file) {
		this.image = null;
		File f = new File(folder, IMAGE); 
		try {
			 u.copy(file, f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public BufferedImage getMask() {
		if( this.mask != null) 
			return this.mask;
		File f = new File(folder, MASK); 
		try {
			if( f.exists())
			  this.mask = ImageIO.read(f);
		} catch (IOException e) { 
			e.printStackTrace();
		}
		return this.mask;
	}
	public void saveMask(File file) {
		this.mask = null;
		File f = new File(folder, MASK); 
		try {
			 u.copy(file, f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public BufferedImage getTexture() {
		if( this.texture != null) 
			return this.texture;
		File f = new File(folder, TEXTURE); 
		try {
			if( f.exists())
			  this.texture = ImageIO.read(f);
		} catch (IOException e) { 
			e.printStackTrace();
		}
		return this.texture;
	}
	public void saveTexture(File file) {
		this.texture = null;
		File f = new File(folder, TEXTURE); 
		try {
			 u.copy(file, f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void resetMask() {
		File f = new File(folder, IMAGE); 
		File f2 = new File(folder, MASK); 
		try {
			 u.copy(f, f2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.mask = null;
	}
	public void resetTexture() {
		File f = new File(folder, IMAGE); 
		File f2 = new File(folder, TEXTURE); 
		try {
			 u.copy(f, f2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.texture = null;
	}
	public void saveImage(BufferedImage bitmapImage) {
		 this.image = bitmapImage;
		 this.mask = null;
		 this.texture = null;
		 File f2 = new File(folder, IMAGE); 
		 if( bitmapImage == null )
			 f2.delete();
		 else {
			 try {
					ImageUtil.saveImageToFile(bitmapImage, f2.getAbsolutePath(), 100, false);
				} catch (IOException e) { 
					e.printStackTrace();
				}
			 resetTexture();
			 resetMask();
		 }
		
	}
	public void saveMask(BufferedImage bitmapImage) {
		 this.mask = bitmapImage;
		 File f2 = new File(folder, MASK); 
		 if( bitmapImage == null )
			 f2.delete();
		 else {
			 try {
					ImageUtil.saveImageToFile(bitmapImage, f2.getAbsolutePath(), 100, false);
				} catch (IOException e) { 
					e.printStackTrace();
				}
		 }
		
	}
	public void saveTexture(BufferedImage bitmapImage) {
		 this.texture = bitmapImage;
		 File f2 = new File(folder, TEXTURE); 
		 if( bitmapImage == null )
			 f2.delete();
		 else {
			 try {
					ImageUtil.saveImageToFile(bitmapImage, f2.getAbsolutePath(), 100, false);
				} catch (IOException e) { 
					e.printStackTrace();
				}
		 }
		
	}
}
