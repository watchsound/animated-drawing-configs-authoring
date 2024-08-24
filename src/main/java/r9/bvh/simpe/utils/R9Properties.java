package r9.bvh.simpe.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream; 
import java.util.Properties;
 

import r9.bvh.simple.Settings;
 
 

public class R9Properties {
	
	  
	//////////////////////////////////////
	
	protected static String _property_file_name_ = "r9-core-settings.properties";
	

    protected   Properties props;
    
    private static R9Properties shared;
    public static R9Properties getSharedProperties() {
    	if( shared == null )
    		shared = new R9Properties(_property_file_name_);
    	return shared;
    }
    
	public   Properties  getProperties(boolean reload){
		if( props == null || reload) {
			props = loadProperties();
		}
		return props;
	}
	
	private String filename;
	private R9Properties(String filename) {
		this.filename = filename;
		getProperties(true);
	}
	
	public   void save(){
		save( getProperties(false) );
	}
	
	 
    public   void save(Properties props){
		save(props, Settings.homedir , filename );
    }
    
    public void saveProperty(String propName, String value) {
    	props.put(propName, value);
    	save();
    }
    public void removeProperty(String propName ) {
    	props.remove(propName );
    	save();
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
    public    Properties loadProperties( ){
		 return loadProperties( Settings.homedir , filename); 
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

	public void dotSize(int dotSize){
		if( dotSize <= 0)   dotSize = 10; 
		saveProperty("dotSize",  dotSize + "");
		 
	}
	
	public int dotSize(){
		try{
			String w = props.getProperty("dotSize");
			if ( w == null )
				return 10;
			return Integer.parseInt(w);
		}catch(Exception ex){
			return 10;
		}
	}
	
//	public void saveStringList(List<String> data, String tag) {
//		Gson gson = new Gson();
//		props.setProperty(tag, gson.toJson(data));
//		save();
//	}
//
//	public List<String> saveStringList(String tag) {
//		String info = props.getProperty(tag);
//		List<String> collection = null;
//		if (info == null) {
//			collection = new ArrayList<String>();
//		} else {
//			Gson gson = new Gson();
//			collection = gson.fromJson(info, ArrayList.class);
//			if (collection.size() > 20)
//				collection.remove(0);
//		}
//		return collection;
//	}
}
