package r9.bvh.simpe.utils; 

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import r9.bvh.simple.Settings;
 
 

public class u {
   
   public static  enum LEVEL  { ERROR, INFO, DEBUG }
   
   private static PrintStream wrt;
   private static boolean useWrt = true;
   private static int indent;
   private static String indentString = "";
   
   private static File homeDir = new File(".");
   
   public static LEVEL LevelSetting = LEVEL.INFO;
   
   public static void setHomeDir(File home) {
	   homeDir = home;
   }
   
   public static boolean empty(String value) {
	   return value == null || value.length() == 0 || value.trim().length() == 0;
   }
   
   public static void p(String str) {
   	    p(str, null, LevelSetting);
   }
   public static void p(String str, LEVEL level) {
	   	p(str, null, level);
   }
   public static void p(String str, Exception ex) {
	   p(str, ex, LevelSetting);
   }
   public static void p(String str, Exception ex, LEVEL level) {
	   if( level.ordinal() < LevelSetting.ordinal()) return;
       if(useWrt && wrt == null) {
           try {
           	File f = new File(homeDir, "r9log.log") ;
           	if( f.exists() ) {
					try {
						FileInputStream fstream = new FileInputStream(f);
						BufferedReader br = new BufferedReader( new InputStreamReader(fstream));
						String strLine;
						int numlines = 0;
						while ((strLine = br.readLine()) != null) {
							numlines++;
						}
						fstream.close();
						if ( numlines > 3000 ){
							f.delete();
						}
					} catch (Exception e) {// Catch exception if any
						System.err.println("Error: " + e.getMessage());
					}
           	}
           	wrt = new PrintStream(new FileOutputStream(homeDir.getAbsolutePath()+
                       File.separator+"r9log.log",true)); 
           	  DateFormat dateFormat = new SimpleDateFormat("EE-MM-dd-yyyy");  
           	  wrt.println("------" + dateFormat.format(new Date()) + "------");
           } catch (Throwable thr) {
               thr.printStackTrace();
               
               wrt = null;
               useWrt = false;
           }
       }
       
       System.out.println(indentString+str);
       System.out.flush();
       if(wrt != null) {
           wrt.println(indentString+str);
       	if( ex != null){
       		ex.printStackTrace(wrt);
       	} 
           wrt.flush();
       }
   }
   
   public static void p(Object[] arr) {
       p("array: " + arr);
       if(arr == null) return;
       for(Object o : arr) {
           p(o);
       }
   }
   public static void p(double[] arr) {
       p("double array: " + arr);
       if(arr == null) return;
       for(double d : arr) {
           p(d);
       }
   }
   public static void p(List headerPainters) {
       p("list: " + headerPainters);
       for(Object o : headerPainters) {
           p(o);
       }
   }
   
   public static void p(Object o) {
       p(""+o);
   }
   public static void p(Exception ex) {
	   p(ex, LevelSetting);
   }
   public static void p(Exception ex, LEVEL level) {
	   if( level.ordinal() < LevelSetting.ordinal()) return;
       p(ex.getClass().getName() + ": " + ex.getMessage(), ex);
       ex.printStackTrace();
       
   }
   public static void pr(String string) {
	   pr(string, LevelSetting);
   }
   public static void pr(String string, LEVEL level) {
	   if( level.ordinal() < LevelSetting.ordinal()) return;
       if( level == LEVEL.ERROR) System.err.print(string);
       else System.out.print(string);
      
   }

   
   public static String fileToString(File file)  {
       try{
        	 return fileToString( new FileInputStream(file) );
       }catch(Exception ex){
       	
       } 
       return "";
   }
   
   
   
   public static String fileToString(InputStream in) throws IOException {
       Reader reader = new InputStreamReader(in);
       StringWriter writer = new StringWriter();
       char[] buf = new char[1024];
       while(true) {
           int n = reader.read(buf);
           if(n == -1) {
               break;
           }
           writer.write(buf,0,n);
       }
       return writer.toString();
   }
   
   
   public static void stringToFile(String text, File file) throws IOException {
       FileWriter writer = new FileWriter(file);
       StringReader reader = new StringReader(text);
       char[] buf = new char[1000];
       while(true) {
           int n = reader.read(buf,0,1000);
           if(n == -1) {
               break;
           }
           writer.write(buf,0,n);
       }
       writer.flush();
       writer.close();
   }
   
   public static void streamToFile(InputStream in, File file) throws IOException {
   	if( in == null )
   		return;
       OutputStream out = new FileOutputStream(file);
       byte[] buf = new byte[1024];
       while(true) {
           int n = in.read(buf);
           if(n == -1) {
               break;
           }
           out.write(buf,0,n);
       }
       out.close();
   }
   
   public static void copy(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		while (true) {
			int readCount = in.read(buffer);
			if (readCount < 0) {
				break;
			}
			out.write(buffer, 0, readCount);
		}
	}

   public static void copy(File file, OutputStream out) throws IOException {
		InputStream in = new FileInputStream(file);
		try {
			copy(in, out);
		} finally {
			in.close();
		}
	}

   public static void copy(InputStream in, File file) throws IOException {
		OutputStream out = new FileOutputStream(file);
		try {
			copy(in, out);
		} finally {
			out.close();
		}
	}
	public static void copyFileFromScriptToTemp(String projectName, String fileName) {
	   File f = new File(Settings.getEditingDir(projectName), fileName);
 	   if( !f.exists() ){
 		   try {
                u.copy(new File(Settings.SCRIPTS_DIR, fileName), f);
            } catch (Exception e) {
            	u.p(e);
            }
 	   }
	}
   public static void copy(File filein, File fileout) throws IOException {
   	if( filein.getAbsolutePath().equals(fileout.getAbsolutePath()))
   		return;
		InputStream in = new FileInputStream(filein);
		if (! fileout.exists() )
			fileout.createNewFile();
		FileOutputStream out = new FileOutputStream(fileout);
		try {
			copy(in, out);
		} finally {
			in.close();
			out.close();
		}
	}
   
   public static void copyDirRecursively(File sourceDir, File destDir) throws IOException{
   	copyDirRecursively(sourceDir, destDir, false);
   }
   
   public static void copyDirRecursively(File sourceDir, File destDir, boolean replace) throws IOException{
   	if ( !sourceDir.exists() )
   		return;
   	if (! destDir.exists() ){
   		if( sourceDir.isDirectory() ){
   			destDir.mkdirs();
   		}  
   	}
	System.out.println("sourceDir:::::" + sourceDir.getAbsolutePath());
	System.out.println("destDir):::::" + destDir.getAbsolutePath());

   	
   	if( sourceDir.isDirectory() ){
   		for( File sf : sourceDir.listFiles()){
   		 	 File f = new File(destDir.getAbsolutePath(), sf.getName());
   		 	 if ( sf.isDirectory() ){
   		 		copyDirRecursively( sf, f);
   		 	 }
   		 	 else {
	    		 		 if( replace ){
	    		 			if ( f.exists() ){
	    		 				f.delete();
	    		 			} 
	    		 			  u.copy(sf,f);
	    		 		 } else {
	    		 			 if (! f.exists() )
	     	                    u.copy(sf,f);
	     	    		} 
   		 		 } 
   		 	 } 
		} else {
			 if( replace ){
				 u.copy(sourceDir,destDir); 
			 } else {
				 if (! destDir.exists() )
	                 u.copy(sourceDir,destDir); 
			 } 
		}  
   }
   
   
   private static long time;
   public static void startTimer() {
       time = System.currentTimeMillis();
   }
   
   public static void stopTimer() {
       long stoptime = System.currentTimeMillis();
       p("stopped: " + (stoptime - time));
   }
   
   public static void sleep(long msec) {
       try {
           Thread.currentThread().sleep(msec);
       } catch (InterruptedException ex) {
           p(stack_to_string(ex));
       }
   }
   
   public static String stack_to_string(Exception e) {
       StringWriter sw = new StringWriter();
       PrintWriter pw = new PrintWriter(sw);
       e.printStackTrace(pw);
       pw.close();
       return sw.toString();
   }
   
   public static void dumpStack() {
       p(stack_to_string(new Exception()));
   }
   
   public static boolean betweenInclusive(int lower, int testValue, int upper) {
       if(testValue >= lower && testValue <= upper) {
           return true;
       }
       return false;
   }

   public static void printFullStackTrace(Throwable th) {
       p("exception: " + th.getMessage());
       for(StackTraceElement e :  th.getStackTrace()) {
           p("   "+e);
       }
       if(th.getCause() != null) {
           printFullStackTrace(th.getCause());
       }
   }

   public static void streamToSTDERR(InputStream in) throws IOException {
       byte[] buf = new byte[1024];
       while(true) {
           int n = in.read(buf);
           if(n == -1) {
               break;
           }
           System.err.write(buf,0,n);
           System.err.flush();
       }
   }

   public static void delete(File srcfile ) throws IOException {
	   if( srcfile.isDirectory() ) {
		   for(File f : srcfile.listFiles()) {
			   delete(f);
		   } 
	   }  
	   srcfile.delete(); 
   }
   
   public static void copyToFile(File srcfile, File outfile) throws IOException {
       FileInputStream in = new FileInputStream(srcfile);
       FileOutputStream out = new FileOutputStream(outfile);
       byte[] buf = new byte[1024];
       while(true) {
           int n = in.read(buf);
           if(n == -1) {
               break;
           }
           out.write(buf,0,n);
       }
       in.close();
       out.close();
   }


   public static void copyTemplate(File srcDir, File destDir, Map<String, String> keys) throws IOException {
       u.p("copying template to temp dir: ");
       u.p(destDir);
       if(!destDir.exists()) {
           destDir.mkdir();
       }
       for(File f : srcDir.listFiles()) {
           copyFileToDir(f, destDir, keys);
       }
   }

   private static void copyFileToDir(File srcfile, File destdir, Map<String, String> keys) throws IOException {
       if(!srcfile.isDirectory()) {
           u.p("copying file : " + srcfile.getAbsolutePath());
           copyToFile(srcfile, new File(destdir, srcfile.getName()));
       } else {
           u.p("isdir: " + srcfile.getAbsolutePath());
           u.indent();
           copyTemplate(srcfile, new File(destdir,srcfile.getName()), keys);
           u.outdent();
       }
   }
   
   public static void indent() {
       indent++;
       indentString = "";
       for(int i=0;i<indent;i++) {
           indentString += " ";
       }
   }

   public static void outdent() {
       indent--;
       indentString = "";
       for(int i=0;i<indent;i++) {
           indentString += " ";
       }
   }
   
   public static String getBaseStorageDir(String appName) {
       String os = System.getProperty("os.name").toLowerCase();
       StringBuffer filepath = new StringBuffer(System.getProperty("user.home"));
       if(os.indexOf("windows xp") != -1) {
           filepath.append(File.separator);
           filepath.append("Local Settings");
           filepath.append(File.separator);
           filepath.append("Application Data");
           filepath.append(File.separator);
           filepath.append(appName);
           filepath.append(File.separator);
       } else if (os.indexOf("vista") != -1) {
           filepath.append(File.separator);
           filepath.append("appdata");
           filepath.append(File.separator);
           filepath.append("locallow");
           filepath.append(File.separator);
           filepath.append(appName);
           filepath.append(File.separator);
       } else if (os.startsWith("mac")) {
           filepath.append(File.separator);
           filepath.append("Library");
           filepath.append(File.separator);
           filepath.append("Preferences");
           filepath.append(File.separator);
           filepath.append(appName);
           filepath.append(File.separator);
       } else {
           //if we don't know what OS it is then just use user.home followed by a .
           filepath.append(File.separator);
           filepath.append(".");
           filepath.append(appName);
           filepath.append(File.separator);
       }
       System.out.println("final base storage dir = " + filepath.toString());
       return filepath.toString();
   }
}