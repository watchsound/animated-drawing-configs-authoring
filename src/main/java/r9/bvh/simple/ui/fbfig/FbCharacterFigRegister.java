package r9.bvh.simple.ui.fbfig;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException; 
import java.util.ArrayList;
import java.util.List;
 

import r9.bvh.simpe.utils.u;
import r9.bvh.simple.Settings;

public class FbCharacterFigRegister {

	public static FbCharacterFigRegister sharedInstance = new FbCharacterFigRegister();
	
	private List<CharacterConfig> figList;
	CharacterConfig defaultOne;
	private FbCharacterFigRegister() {
		defaultOne = CharacterConfig.getDefaultFig();
	}
	
	public CharacterConfig getDefaultOne() {
		return defaultOne;
	}

	public List<CharacterConfig> getFigList(){
		if( figList == null || figList.isEmpty()) {
			figList = new ArrayList<>();
			File f = new File(Settings.homedir, "fb_characterfigs");
			if( !f.exists()) {
				f.mkdirs();
			}
			for(File f0 : f.listFiles()) {
				if(f0.getName().endsWith(".yaml") ) {
					try {
						figList.add( CharacterConfig.loadFromFile(f0) );
					} catch (Exception e) {
						 e.printStackTrace();
					}
				}
			}
		}
		return figList;
	}
	
	public CharacterConfig reload(String name) {
		File f = new File(Settings.homedir, "fb_characterfigs");
		File f2 = new File(f, name + ".yaml");
		try {
			return CharacterConfig.loadFromFile(f2);
		} catch (Exception e) { 
			e.printStackTrace();
			return null;
		}
	}
	
	public void save(CharacterConfig fig) {
		File f = new File(Settings.homedir, "fb_characterfigs");
		File f2 = new File(f, fig.getName()+ ".yaml"); 
		String yamlStr = fig.toYaml();
		try {
			u.streamToFile(new ByteArrayInputStream(yamlStr.getBytes()), f2);
		} catch (IOException e) { 
			e.printStackTrace();
		}
	}
	public void delete(CharacterConfig fig) {
		File f = new File(Settings.homedir, "fb_characterfigs");
		File f2 = new File(f, fig.getName()+ ".yaml");
		if( f2.exists())
			f2.delete();
	}
	
}
