package r9.bvh.simple.model;

public class Utils {
    public static String getIndent(int nestLevel) {
    	StringBuilder sb = new StringBuilder();
    	for(int i = 0; i < nestLevel; i++) {
    		sb.append("    ");
    	}
    	return sb.toString();
    }
    public static String toString(String[] fs) {
    	StringBuilder sb = new StringBuilder();
    	for(int i = 0; i < fs.length; i++) {
    		sb.append(" " + fs[i]);
    	}
    	return sb.substring(1);
    }
    public static String toString(double[] fs) {
    	StringBuilder sb = new StringBuilder();
    	for(int i = 0; i < fs.length; i++) {
    		sb.append(" " + fs[i]);
    	}
    	return sb.substring(1);
    }
}
