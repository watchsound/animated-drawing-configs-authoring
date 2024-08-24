package r9.bvh.simpe.utils;
 

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
public class StringUtils {
	public static final DecimalFormat  df  =  new  DecimalFormat("######.00");
	static final Pattern utf16coder = Pattern.compile("[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]");
	public static class ReplaceUnit{
		public int pos;
		public int replaceLength;
		public String replaceStr;
		public ReplaceUnit(int pos, int replaceLength, String replaceStr) {
			this.pos = pos;
			this.replaceLength = replaceLength;
			this.replaceStr = replaceStr;
		} 
	}
	public static String toString(List objs) {
		StringBuilder sb = new StringBuilder();
		for(Object obj : objs) {
			sb.append(", " + obj);
		}
		return sb.substring(2);
	}
	
	public static boolean isEmpty(String input) {
		return input == null || input.length() == 0;
	}
	public static boolean isContentEmpty(String input) {
		return input == null || input.trim().length() == 0;
	}
	
	public static String getContentFromClipboard() {
		 Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
		 Clipboard systemClipboard = defaultToolkit.getSystemClipboard();
	     DataFlavor dataFlavor = DataFlavor.stringFlavor;

       if (!systemClipboard.isDataFlavorAvailable(dataFlavor))
       { 
           return  "";
       }
        
       try {
       	return (String)systemClipboard.getData(dataFlavor);
       }catch(Exception ex) {
       	return "";
       } 
	}
	
	
	public static String wrap(String content, int maxWidth) {
		if( content.length() > maxWidth ) {
			StringBuilder tmp = new StringBuilder();
			int c = 1;
			int len = content.length();
			while( c * maxWidth < len ) {
				tmp.append( content.substring((c-1)* maxWidth , c* maxWidth) );
				tmp.append( "\n" );
				c++;
			}
		    tmp.append(content.substring((c-1)*maxWidth));
			return tmp.toString();
		} else {
			return content;
		}
	}
	
	
	/**
	 * //check if a line has number start;
	 * @param content
	 * @return
	 */
	public static int checkNumberRow(String content) {
	    Pattern p = Pattern.compile("^([0-9A-Za-z]{1,2})\\.([\\s]*).+");
		Matcher m = p.matcher(content);
    	if( m.matches() ) {
    		String n = m.group(1);
    		try {
    			return Integer.parseInt(n);
    		}catch(Exception e) {
    			if( n.length() == 1 ) {
    				char nc = n.charAt(0);
    				if( Character.isAlphabetic(nc) && Character.isUpperCase(nc) )
    					return (nc -'A');
    				if( Character.isAlphabetic(nc) && Character.isLowerCase(nc))
    					return (nc - 'a');
    			}
    		} 
    	}
    	return -1;
	}
	
	public static String stripLeadingNumberOrder(String content) {
	    Pattern p = Pattern.compile("^([0-9A-Za-z]{1,2})\\.([\\s]*)(.+)");
		Matcher m = p.matcher(content);
    	if( m.matches() ) {
    		return m.group(3); 
    	}
    	return content;
	}
	
	
	public static boolean hasNumberOrder(String[] lines, int minCount) {
		int preNum = -1;
		for(String line: lines) {
			int n = checkNumberRow(line);
			if(n > preNum) {
				minCount --;
				preNum = n;
			}
		}
		return minCount <= 0;
	}
	 
	
	
	/**
	 * for //  reduce to /, //// -> //
	 * for / , do nothing.
	 * @param input
	 * @return
	 */
	public static String reduceBackslash(String input) {
		if( input == null || input.length() == 0 )
			return input;
		StringBuilder sb = new StringBuilder();
		boolean prevbs = false;
		for(char c : input.toCharArray()) {
			if( c == '\\' ) {
				if( prevbs ) {
					prevbs = false;
				} else {
					sb.append(c);
					prevbs = true;
				}
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	private static int backslashCount(String input, int startPos) {
		int count = 0; 
		while( input.charAt(startPos) == '\\' ) {
			count ++;
			startPos ++;
		}
		return count;
	}
	public static String increaseBackslash(String input) {
		if( input == null || input.length() == 0 )
			return input;
		StringBuilder sb = new StringBuilder();
		boolean prevbs = false;
		for(int i = 0;i < input.length(); i++) {
			char c = input.charAt(i);  
			if( c == '\\' ) {
				 int count = backslashCount(input, i);
				 for(int j = 0; j < count*2; j++)
					 sb.append(c);
				 i += count-1;
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
//	public static void main(String[] arvg){
////		String b = "a8982ca8-ddaa-aaaa-aaaa-aaaaaaaaaaaa";
////		String c = mapTo36(b);
////		if( b.equals(c)){
////			System.out.println("OK");
////		}else {
////			System.out.println("ERROR");
////		}
////		String a = "a8982ca8ddaaaaaaaaaaaaaaaaaaaaaa";
////		
////		System.out.println(a);
////		a = mapTo36(a);
////		System.out.println(a);
////		String d = "a8982ca8-ddaa-aaaa-aaaa-aaaaaaaaaaaa";
////		if( a.equals(d)){
////			System.out.println("OK");
////		}else {
////			System.out.println("ERROR");
////		}
//		
// 		String b = createUID(36);
// 		String API_TOKEN = mapTo36(b);
// 		System.out.println(API_TOKEN);
// 		long time = new Date().getTime();
// 		String t =  "t1=" + time  + "&t2=" + 10 + "&t3=" + API_TOKEN;
// 		System.out.println(t);
// 		String token = getMD5(t);
// 		System.out.println(token);
// 		
// 		String t2 =  "t1=" + time + "&t2=" + 10 + "&t3=" + token;
// 		System.out.println(t2);
// 		String[] fs =  t2.split("&");
// 		for(int i = 0; i < fs.length; i++){
// 			String[] fsf = fs[i].split("=");
// 			System.out.println(fsf[1]);
// 		}
// 		//t1=11111&t2=5&t3=xxxxxxx 
// 		boolean valid = hasValidKey(t2, API_TOKEN);
// 		System.out.println(valid);
// 		
// 		boolean valid2 = isValidForFirstTime(t2);
// 		System.out.println(valid);
	
	   
//	}
	 public static String getNormalizedLatex(String latex) {
	    	return latex.replaceAll("\\\\", "\\\\\\\\");
      }
	
	 public static boolean equals(Object obj1, Object obj2) {
		 if( obj1 == null && obj2 == null )
			 return true;
		 if( obj1 == null && obj2 != null )
			 return false;
		 if( obj1 != null && obj2 == null)
			 return false;
		 if( !obj1.getClass().equals(obj2.getClass()))
			 return false;
		 if( obj1 instanceof List) {
			 List list1 = (List)obj1;
			 List list2 = (List)obj2;
			 if( list1.size() != list2.size() )
				 return false;
			 for(int i = 0; i < list1.size(); i++) {
				 boolean t = equals(list1.get(i), list2.get(i));
			     if( !t )
			    	 return false;
			 }
			 return true;
		 }
		 else if( obj1 instanceof Set) {
			 Set list1 = (Set)obj1;
			 Set list2 = (Set)obj2;
			 if( list1.size() != list2.size() )
				 return false;
			 for(Object o1 : list1) {
				 if( !list2.contains(o1) )
					 return false;
			 }
			 return true;
		 }  if( obj1 instanceof Map) {
			 Map list1 = (Map)obj1;
			 Map list2 = (Map)obj2;
			 if( list1.size() != list2.size() )
				 return false;
			 for(Object o1 : list1.keySet()) {
				 if( ! equals(list1.get(o1), list2.get(o1)) )
					 return false;
			 }
			 return true;
		 }
		 
		 return obj1.equals(obj2);
	 }
	/**
	 *  asdf[123] ->  [123] will be subscripe..
	 */
	public static int getSubscriptStartPos(String name) {
		if( name == null || name.length()==0 )
			return -1;
		int pos = -1;
		for(int i = name.length()-1; i>=0;i--) {
			if(!Character.isDigit(name.charAt(i))) {
				pos = i;
				break;
			}
		}
		if( pos <0 || pos == name.length()-1)
			return -1;
		return pos+1;
	}
	public static int getSubscriptStartPos2(String name) {
		if( name == null || name.length()==0 || name.charAt(name.length()-1) != ']')
			return -1;
		int pos = -1;
		for(int i = name.length()-2; i>=0;i--) {
			if( name.charAt(i) == '[') {
				pos = i;
				break;
			}
		}
		if( pos < 0  )
			return -1;
		return pos;
	}
	
	 
	
	public static String toJsArray(List<String> data, String prefix, String posfix){
		StringBuilder sb = new StringBuilder();
		if( data == null || data.size() ==0) 
			return "[ ]";
		for(String d : data)
			sb.append("," +   prefix +   d + posfix);
		return "[" + sb.substring(1) + "]";
	}
	 
	public static String generateStringUID(){
		return  createUID(10) ;
	}
	public static int createUniqueIntID(){
		return createUID(10).hashCode();
	}
	public static String replaceNewLine(String content){ 
		return content == null ? null : content.replaceAll("\r\n", "\n").replaceAll("\r", "\n");
	}
	
	public static boolean isValidForFirstTime(String content){
		String[] fs =  content.split("&");
		String time = fs[0].split("=")[1];
		String delay = fs[1].split("=")[1];
		//String token = fs[2].split("=")[1];
		 long time2 = Long.parseLong(time);
		 int delay2 = Integer.parseInt(delay);
		 long ctime = new Date().getTime();
		 
		 return ctime < time2 + delay2 * 3600*24 * 1000 ;
	}
	
	public static boolean hasValidKey(String content, String key){
		try{
		String[] fs =  content.split("&");
		String time = fs[0].split("=")[1];
		String delay = fs[1].split("=")[1];
		String token = fs[2].split("=")[1];
		String t2 =  "t1=" + time + "&t2=" + delay + "&t3=" + key;
		String token2 = getMD5(t2);
		return token2.equals(token); 
		}catch(Exception ex){
			return false;
		}
	}
	
	public static String mapTo36(String uuid){
		int length = uuid.length();
		if( length == 36 ){
			if( uuid.charAt(8) == '-' && uuid.charAt(13) == '-' && uuid.charAt(18) == '-' && uuid.charAt(23) == '-'  ){
				return uuid;
			}
		}
		StringBuilder sb = new StringBuilder();
		//first to 36;
		for(int i = 0; i< 36; i ++){ 
			if ( i < length ){
				sb.append( uuid.charAt(i));
		 	} else {
				sb.append("a");
			} 
		}
		if( sb.charAt(8) != '-' ){
			sb.insert(8, "-");
		}
		if( sb.charAt(13) != '-' ){
			sb.insert(13, "-");
		}
		if( sb.charAt(18) != '-' ){
			sb.insert(18, "-");
		}
		if( sb.charAt(23) != '-' ){
			sb.insert(23, "-");
		}
		StringBuilder sb2 = new StringBuilder();
		for(int i = 0; i< 36; i ++){
			if( i == 8 || i == 13 || i == 18 || i == 23 ){
				sb2.append("-");
			} else {
				if( Character.isLetterOrDigit(sb.charAt(i))){
					sb2.append( sb.charAt(i));
				}else {
					sb2.append("a");
				}
			} 
		}
		
		return sb2.toString(); 
	}
    public static String toString(int[] values){
    	if( values == null || values.length == 0)
    		return "";
    	
    	StringBuilder sb = new StringBuilder();
    	for(int v : values){
    		sb.append(" " + v);
    	}
    	return sb.substring(1);
    }
    
    public static String toString(String[] values){
    	if( values == null || values.length == 0)
    		return "";
    	
    	StringBuilder sb = new StringBuilder();
    	for(String v : values){
    		sb.append(" " + v);
    	}
    	return sb.substring(1);
    }
    
    public static String[] fromString(String values, boolean decompose){
    	if( values == null || values.trim().length() == 0)
    		return new String[0];
    	
    	values = values.replaceAll("/\\s+/g", " ");
    	 
    	String[] result = values.split(" ");
    	if( result.length == 1 && result[0].length() >1 && decompose ){
    		String  key = result[0];
    		String[] nresult = new String[key.length()];
    		for(int i = 0; i < key.length(); i++){
    			nresult[i] = key.charAt(i) + "";
    		}
    		return nresult;
    	}
    	return result;
    }
    
    public static void copyFileToFile(File srcfile, File outfile) throws IOException {
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

    public static void copyStreamToFile(InputStream in, File outfile) throws IOException {
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
    public static void copyStringToFile(String content, File outfile) throws IOException {
		try {
			 
			if (outfile.exists()) {
				outfile.delete();
			}
			outfile.createNewFile();

			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outfile), "UTF8"));

			out.write(content);
			out.close(); 
		} catch (Exception ex) {
			u.p(ex);
		}  
	}

   
    public static int parseInt(String value, int defaultValue){
    	try{
    		return Integer.parseInt(value);
    	}catch(Exception ex){
    		return defaultValue;
    	}
    }
    
    public static  void copyFolder(File src, File dest) throws IOException{
        if(src.isDirectory()){
            if(!dest.exists()){
                dest.mkdir();
            }

            String files[] = src.list();

            for (String file : files) {
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);

                copyFolder(srcFile,destFile);
            }

        } else {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest); 

            byte[] buffer = new byte[1024];

            int length;
            while ((length = in.read(buffer)) > 0){
                out.write(buffer, 0, length);
            }

            in.close();
            out.close();
        }
    }
     

	public static String getMD5(String s) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};        
        try {
            byte[] btInput = s.getBytes("utf-8");
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
        	u.p(e);
            return null;
        }
    }
	 
 
	
	private static AffineTransform atf = new AffineTransform(); 
    private static FontRenderContext frc = new FontRenderContext(atf, true,   true);
    
	public static String createEmptyString(java.awt.Font font, String word, int uiWidth){ 
		double w =  font.getStringBounds(word + "", frc).getWidth();
		StringBuilder sb = new StringBuilder();
		int count = (int)Math.ceil( uiWidth / w );
		for(int i = 0;i < count;i++)
			sb.append(word);
		return sb.toString();
	}
	
	public static class AbcOrder{
		String orders;
		int curPos;
		public AbcOrder(String orders){
			this.orders = orders;
			reset();
		}
		public void reset(){
			curPos = -1; 
		}
		private String get(){
			if( orders == null || orders.length() == 0 ) return null;
			curPos = curPos % orders.length();
			return orders.charAt(curPos) + "";
		}
		public String next(){
			curPos++;
			return get();
		} 
	}
	
	/**
     * 将一段错误解码的字符串重新解码
     */
    public static String convertEncodingFormat(String str, String formatFrom, String FormatTo) {
        String result = null;
        if (!(str == null || str.length() == 0)) {
            try {
                result = new String(str.getBytes(formatFrom), FormatTo);
            } catch (Exception e) {
                return str;
            }
        }
        return result;
    }
    public static String tryNormalize(String s){
    	if(!(java.nio.charset.Charset.forName("GBK").newEncoder().canEncode(s))){
    		try {
    			return   new String(s.getBytes("ISO-8859-1"),"UTF-8");
    		} catch (UnsupportedEncodingException e) {
    			e.printStackTrace(); 
    			return  s;
    		}	
    	} 
    	return  s;
    }
    
    private static String getEncoding(String str) {   
    	String encode = "GB2312"; //"UTF-8"; 
    	 try {    
             if (twoStringEncodeEquals(str, new String(str.getBytes(encode), encode))) {    
                  String s1 = encode;    
                 return s1;    
              }    
          } catch (Exception exception1) {    
          }    
           
         encode = "UTF-8";    
       try {    
           if (twoStringEncodeEquals( str, new String(str.getBytes(encode), encode))) {    
                String s = encode;    
               return s;    
            }    
        } catch (Exception exception) {    
        }    
        encode = "ISO-8859-1";    
      
       try {    
           if (twoStringEncodeEquals(str, new String(str.getBytes(encode), encode))) {    
                String s2 = encode;    
               return s2;    
            }    
        } catch (Exception exception2) {    
        }    
        encode = "GBK";    
       try {    
           if (twoStringEncodeEquals(str, new String(str.getBytes(encode), encode))) {    
                String s3 = encode;    
               return s3;    
            }    
        } catch (Exception exception3) {    
        }    
       return "";    
    }    
    public static void printChar(String str){
    	for(int i = 0; i < str.length(); i++)
    		System.out.print( (int)str.charAt(i) + " " );
    	System.out.println();
    }
    private static boolean twoStringEncodeEquals(String str1, String str2){
    	return toCharString(str1).equals( toCharString(str2) );
    }
    private static String toCharString(String str){
    	StringBuilder sb = new StringBuilder();
    	for(int i = 0; i < str.length(); i++)
    		sb.append( (int)str.charAt(i) + " ");
    		//System.out.print( (int)str.charAt(i) + " " );
    	return sb.toString();
    }
    
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }
 
    /**
     * 判断字符串是否是乱码
     *
     * @param strName 字符串
     * @return 是否是乱码
     */
    public static boolean isMessyCode(String strName) {
        Pattern p = Pattern.compile("\\s*|t*|r*|n*");
        Matcher m = p.matcher(strName);
        String after = m.replaceAll("");
        String temp = after.replaceAll("\\p{P}", "");
        char[] ch = temp.trim().toCharArray();
        float chLength = ch.length;
        float count = 0;
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (!Character.isLetterOrDigit(c)) {
                if (!isChinese(c)) {
                    count = count + 1;
                }
            }
        }
        float result = count / chLength;
        if (result > 0.4) {
            return true;
        } else {
            return false;
        } 
    }

    public static String removePosfix(String content){
    	if( content == null ) return "";
		int index = content.lastIndexOf(".");
		
		return index <= 0 ? content : content.substring(0, index);
    }
    
    public static void main(String[] args) {
    	String content0 = "asdf\u0008";
    	Pattern pattern = Pattern.compile("[\u0008-\u0009]");
    	boolean m = pattern.matcher(content0).find();
    	System.out.println(" match \\u0008 : " + m);
    	String content = "sfadf{A}{B}sdf{{C}}sfwew{Dsdf}{F}";
    	List<String> tags = extractTags(content, true);
    	System.out.println(tags);
    	  content = "aafdasfd\r\n2fdasdfafd";
    	System.out.println(content);
    	System.out.println("------");
    	content = replaceNewLine( content );
    	System.out.println(content);
    	System.out.println("------");
//          // utf-8编码
//           String str = "你好，少年！";  
//           printChar(str);
//           System.out.println(str);
//            System.out.println(getEncoding(str));
//            System.out.println( isMessyCode(str) ? "messy!" : "acceptable");
//            System.out.println( tryNormalize(str));
//           // UTF-8编码的byte流强行用iso-8859-1解码，毫无疑问的乱码了
//            String str1 = convertEncodingFormat(str, "UTF-8", "iso-8859-1");
//            System.out.println( "convertEncodingFormat(str,  UTF-8 ,  iso-8859-1 )"); 
//            
//            printChar(str1);
//            System.out.println(str1);
//            System.out.println(getEncoding(str1));
//            System.out.println( isMessyCode(str1) ? "messy!" : "acceptable");
//            System.out.println( tryNormalize(str1));
//            
//            String str2 = convertEncodingFormat(str, "UTF-8", "GBK");
//            System.out.println( "convertEncodingFormat(str,  UTF-8 ,  GBK )"); 
//            printChar(str2);
//            System.out.println(str2);
//            System.out.println(getEncoding(str2));
//            System.out.println( isMessyCode(str2) ? "messy!" : "acceptable");
//            System.out.println( tryNormalize(str2));
//            
//            String str3 = convertEncodingFormat(str, "UTF-8", "GB2312");
//            System.out.println( "convertEncodingFormat(str,  UTF-8 ,  GB2312 )"); 
//            printChar(str3);
//            System.out.println(str3);
//            System.out.println(getEncoding(str3));
//            System.out.println( isMessyCode(str3) ? "messy!" : "acceptable");
//            System.out.println( tryNormalize(str3));
//            str3 = tryNormalize(str3);
//            System.out.println( isMessyCode(str3) ? "messy!" : "acceptable");
//           
//            str1 = convertEncodingFormat(str, "iso-8859-1", "iso-8859-1");
//           printChar(str1);
//           System.out.println(str1);
//           System.out.println(getEncoding(str1));
//           System.out.println( isMessyCode(str1) ? "messy!" : "acceptable");
           
           // 将str1再转化为byte流,重新用UTF-8解码，乱码问题解决
//           String str2 = convertEncodingFormat(str1, "iso-8859-1", "UTF-8");
//           System.out.println( "convertEncodingFormat(str,    iso-8859-1  UTF-8 , )"); 
//            printChar(str2);
//           System.out.println(str2);
//           System.out.println(getEncoding(str2));
//           System.out.println( isMessyCode(str2) ? "messy!" : "acceptable");
//           System.out.println( tryNormalize(str2));
//           
//           str2 = convertEncodingFormat(str2, "UTF-8", "GBK");
//           System.out.println( "convertEncodingFormat(str,      UTF-8 , GBK )"); 
//           printChar(str2);
//          System.out.println(str2);
//          System.out.println(getEncoding(str2));
//          System.out.println( isMessyCode(str2) ? "messy!" : "acceptable");
//          System.out.println( tryNormalize(str2));
//          
//          str2 = convertEncodingFormat(str2, "GBK", "UTF-8");
//          System.out.println( "convertEncodingFormat(str,      GBK  UTF-8 ,)"); 
//          System.out.println(str2);
//          System.out.println( isMessyCode(str2) ? "messy!" : "acceptable");
//          System.out.println( tryNormalize(str2));
//          
//          str2 = convertEncodingFormat(str2, "UTF-8", "GB2312");
//          System.out.println( "convertEncodingFormat(str,         UTF-8 ,GB2312)"); 
//          printChar(str2);
//         System.out.println(str2);
//         System.out.println(getEncoding(str2));
//         System.out.println( isMessyCode(str2) ? "messy!" : "acceptable");
//         System.out.println( tryNormalize(str2));
//         
//         str2 = convertEncodingFormat(str2, "GB2312", "UTF-8");
//         System.out.println( "convertEncodingFormat(str, GB2312   UTF-8 ,)"); 
//         System.out.println(str2);
//         System.out.println( isMessyCode(str2) ? "messy!" : "acceptable");
//         System.out.println( tryNormalize(str2));
       }

    public static String createCompactUID(String prefix) {
    	return prefix + createUID(8);
    }
    public static String createUID(String prefix, int length) {
    	return prefix + createUID(length);
    }
    
    public static String createCompactUID() {
    	return createUID(8);
    }
    
    //createUID has format:  a..z...z...z...z
    public static boolean isCreatedUID(String uid) {
    	if( uid.length() < 2 || uid.charAt(0) != 'a' ) return false;
    	int i = 3;
    	while( i < uid.length() ) {
    		if( uid.charAt(i) != 'z' )  return false;
    		i +=4;
    	}
    	return true;
    }
    
	public static String createUID(int length)
	{
		String uuid = UUID.randomUUID().toString();
		//create a random string with 40 characters.
		char[]  uid = new char[length];
		Random random = new Random();
		 uid[0] = 'a';
		for (int i =1; i < length; i++ )
		{
			uid[i] = (i+1) % 4 == 0 ? 'z' : uuid.charAt(random.nextInt(uuid.length())); 
			if ( uid[i] == '-' ) 	uid[i] = '_';
			if ( uid[i] == '.' ) 	uid[i] = 'i';
		}
		return new String(uid);
	}

	public static String normalizeFileName(String name){
	    if( name == null || name.length() ==0 ) return "";
		int index = name.lastIndexOf(".");
		int code = Math.abs( name.hashCode() );
		if( index >= 0){
			 return "_im" + code + name.length() + name.substring(index);
		} else {
			return "_im" + code + name.length();
		}
	}

	public static String normalize(String value){
		return value.replaceAll("-", "_").replaceAll(" ", "_");
	}


    public static String join(List<String> strings, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.size(); i++) {
            sb.append(strings.get(i));
            if (i < strings.size()-1) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }
	 
    public static String formatValue_PI(double value) {
    	double ratio = value / Math.PI;
    	//int whole = (int)Math.floor(ratio);
    	//ratio = ratio - whole;
    	return formatValue(ratio) + "π";
    }
    public static String formatValue(double value) {
    	 String v = df.format(value);	
    	 if(  v.endsWith("00") )
    		 v = v.substring(0, v.length()-2);
    	 else if( v.endsWith("0"))
    		 v = v.substring(0, v.length()-1);
    	
         if( v.endsWith("."))
        	 v = v.substring(0, v.length()-1);
         
    	 if( v.startsWith("."))
    		 return "0" + v;
    	 return v;
    }
    
    public static String removeInvalidJsonCode(String text){
    	if( text == null ) return "";
    	 text = text.replaceAll("\n", "").replaceAll("\r", "").
    			 replaceAll("'",  " ").replaceAll("\"",  " ").replaceAll("\\|",  " ")
    			  .replaceAll("<", " ").replaceAll(">", " ") ;
    	 return text;
    }
    public static String manim_normalize(String text){
    	if( text == null ) return "";
    	 text = text. replaceAll("\"",  "\\\\\"") . replaceAll("'",  "\\\\\'")  ;
    	 return text;
    }
 
	 
	public static boolean containUTF16Code(String text) {
		return utf16coder.matcher(text).find();
	}
	 
	
	/**
	 * tags are {A} ....{Z}  
	 * @param rawText  
	 * return A....Z.. in order found in rawText
	 */
	public static List<String>  extractTags(String rawText, boolean keepUnique) {
		List<String> tags = new ArrayList<>();
		int spos = 0;
		int epos = 0;
		int length = rawText.length();
		while( (spos = rawText.indexOf("{", epos)) >= 0) {
			if( spos +2 >= length ) break;
			if( rawText.charAt(spos+2) == '}' ) {
				char c = rawText.charAt(spos+1);
				if( c >= 'A' && c <= 'Z') {
					if( keepUnique ) {
						if(! tags.contains(c+""))
							tags.add(c+"");
					} else {
						tags.add(c+"");
					} 
				}
				epos = spos+3;
			} else {
				epos = spos+1;
			}
		} 
		return tags;
	}
	  public static String trimTrailing(String str)
	    {
	      if( str == null || str.length() == 0)  return str;
	      int len = str.length();
	      for( ; len > 0; len--)
	      {
	        if( ! Character.isWhitespace( str.charAt( len - 1)))
	           break;
	      }
	      return str.substring( 0, len);
	    } 
//	public static String EncoderByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException{
//        MessageDigest md5=MessageDigest.getInstance("MD5");
//        Encoder base64en = Base64.getEncoder();
//       // byte[] newstr=base64en.encode(md5.digest(str.getBytes("utf-8")));
//          ;
//          byte[] result = md5.digest(str.getBytes("utf-8"));
//          for(int i = 0; i < result.length; i ++)
//        	  System.out.print( i  );
//          return "";
//	 }
	public static String toString(double[] values) {
		if( values == null || values.length == 0)
    		return "";
    	
    	StringBuilder sb = new StringBuilder();
    	for(double v : values){
    		sb.append(" " + v);
    	}
    	return sb.substring(1);
	}
}
