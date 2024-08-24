package r9.bvh.simpe.utils;
 


import java.awt.geom.Point2D;
import java.text.NumberFormat;
import java.text.ParseException;
 
 

public class NumberHelp {
	public static final float epsilon = 0.00000001f;
	static NumberFormat format = NumberFormat.getInstance();
	
	public static String trimFloat(double value){
	    return trimFloat(value,2);
	}
	public static String trimFloat(double value, int fractionDigits){
	    format.setMaximumFractionDigits(fractionDigits);
	    try {
			return   format.parse( format.format(value) ) + "";
		} catch (ParseException e) {
		    return format.format(value);
		}
	}
	public static double[] copy(double[] values){
		double[] copyed = new double[values.length];
		for(int i = 0; i < values.length; i++)
			copyed[i] = values[i];
		return copyed;
	}
	 
	public static final boolean isSameValue(float v1, float v2){
		return Math.abs(v1 - v2) < epsilon;
	}
	public static final boolean isSameValue(double v1, double v2){
		return Math.abs(v1 - v2) < epsilon;
	}
	public static final boolean isSameValue(double v1, double v2, double margin){
		return Math.abs(v1 - v2) < margin;
	}
	public static final boolean isZero(double v){
		return Math.abs(v) < epsilon;
	}
	//if both v1 and v3 are null,  treat them as same
	public static final boolean isSameValue(String v1, String v2){
		 if ( v1 == null && v2 == null )
			 return true;
		 if ( v1 != null )
			 return v1.equals(v2);
		 return v2.equals(v1);
	}
	
	public static int getEstimatedDurationForText(String content){
		if ( content == null || content.length() < 2 )
			return 0;
		return content.length() / 12 + 1;
	}
	

    public static double clamp(double min, double value, double max) {
        if(value < min) return min;
        if(value > max) return max;
        return value;
    }

    public static boolean isBetween(double min, double value, double max) {
        if(value < min) return false;
        if(value > max) return false;
        return true;
    }

    public static Point2D interpolatePoint(Point2D start, Point2D end, double position) {
        return new Point2D.Double(
                (end.getX()-start.getX())*position + start.getX(),
                (end.getY()-start.getY())*position + start.getY()
        );
    }

    ///////////////////////
    public static void main(String[] argvs) {
		System.out.println( nextSequenceSymbol("23") );
		System.out.println( nextSequenceSymbol("a_1") );
		System.out.println( nextSequenceSymbol("b^34") );
		System.out.println( nextSequenceSymbol("b") );
		System.out.println( nextSequenceSymbol("E") );
		System.out.println( nextSequenceSymbol("Z") );
		System.out.println( nextSequenceSymbol("z") );
		System.out.println( nextSequenceSymbol("a12") ); 
	}
	/**
	 * if input is integer, return integer + 1;
	 * if input is letter, return next letter;
	 * if input is letter+integer, return  letter + next integer
	 * if input is letter_integer, return letter_next integer
	 * if input is letter^integer, return letter^next integer
	 * otherwise
	 *     return null;
	 * @param symbol
	 * @return
	 */
	public static String nextSequenceSymbol(String symbol) {
		return nextSequenceSymbol(symbol, false);
	}
	public static String nextSequenceSymbol(String symbol, boolean upcase) {
		if( symbol == null || symbol.length() == 0 )
			return upcase? "A" : "a";
		try {
			int v = Integer.parseInt(symbol);
			return (v + 1) + "";
		}catch(Exception e) {}
		int pos = symbol.indexOf("_");
		if( pos == 0 || pos == symbol.length()-1)
			return null;
		if( pos > 0 ) {
			try {
				int v = Integer.parseInt(symbol.substring(pos+1));
				return symbol.substring(0, pos) + "_" + (v+1);
			}catch(Exception e) {
				return null;
			}
		}
	    pos = symbol.indexOf("^");
		if( pos == 0 || pos == symbol.length()-1)
			return null;
		if( pos > 0 ) {
			try {
				int v = Integer.parseInt(symbol.substring(pos+1));
				return symbol.substring(0, pos) + "^" + (v+1);
			}catch(Exception e) {
				return null;
			}
		}
		char c = symbol.charAt(0);
		if( !Character.isAlphabetic(c) )
			return null;
		if( symbol.length() == 1 ) { 
			if( Character.isUpperCase(c) ) {
				if( c  == 'Z' )
				    return "A0";
				return   (char)(c + 1) +"" ;
			}
			else {
				if( c  == 'z' )
				    return "a0";
				return   (char)(c + 1) +"" ;
			}
		}
		try {
			int v = Integer.parseInt(symbol.substring(1));
			return "" + c + (v+1);
		}catch(Exception e) {
			return null;
		} 
	}
	
}
