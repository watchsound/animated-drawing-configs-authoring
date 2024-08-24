package r9.bvh.simple.retarget;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException; 
import java.util.ArrayList;
import java.util.List;
 

import r9.bvh.simpe.utils.u;
import r9.bvh.simple.Settings;

public class RetargetRegister {

	public static RetargetRegister sharedInstance = new RetargetRegister();
	
	private List<RetargetConfig> figList; 
	private RetargetRegister() { 
	}
	
    public File getResourceInRetargetDir(String name) {
    	File f = new File(name);
    	if( f.exists()) 
    		return f;
        f = new File(Settings.homedir, "fb_retarget");
    	return new File(f, name);
    }

	public List<RetargetConfig> getFigList(){
		if( figList == null ) {
			figList = new ArrayList<>();
			File f = new File(Settings.homedir, "fb_retarget");
			if( !f.exists()) {
				f.mkdirs();
			}
			for(File f0 : f.listFiles()) {
				if(f0.getName().endsWith(".yaml") ) {
					try {
						figList.add( RetargetConfig.loadFromFile(f0) );
					} catch (Exception e) {
						 e.printStackTrace();
					}
				}
			}
		}
		return figList;
	}
	
	public RetargetConfig reload(String name) {
		File f = new File(Settings.homedir, "fb_retarget");
		File f2 = new File(f, name + ".yaml");
		try {
			return RetargetConfig.loadFromFile(f2);
		} catch (Exception e) { 
			e.printStackTrace();
			return null;
		}
	}
	
	public void save(RetargetConfig fig) {
		File f = new File(Settings.homedir, "fb_retarget");
		File f2 = new File(f, fig.getName()+ ".yaml"); 
		String yamlStr = fig.toYaml();
		try {
			u.streamToFile(new ByteArrayInputStream(yamlStr.getBytes()), f2);
		} catch (IOException e) { 
			e.printStackTrace();
		}
	}
	public void delete(RetargetConfig fig) {
		File f = new File(Settings.homedir, "fb_retarget");
		File f2 = new File(f, fig.getName()+ ".yaml");
		if( f2.exists())
			f2.delete();
	}
	
}
