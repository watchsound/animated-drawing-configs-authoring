package r9.bvh.simpe.utils;

import java.awt.geom.Point2D;

public class GeomUtil {
	 public static double distance(Point2D point1, Point2D point2){
	    	return point1.distance(point2);
	    }
	    public static double distance(double x1, double y1, double x2, double y2){
	    	return  Math.sqrt( (x1-x2)*(x1-x2) + ((y1-y2)*(y1-y2)) );
	    }
	    
	    /**
	     *  calculate the angle of the line formed by the two points, in degrees
	     *  in clock-wise direction
	     */
	    public static double calcAngle(Point2D point1, Point2D point2) {
	        return calcAngle(point1, point2, false);
	    }
	    public static double calcAngle(double x1, double y1, double x2, double y2) {
	        return calcAngle(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2), false);
	    }
	    public static double calcAngle(Point2D point1, Point2D point2, boolean considerFlip) {
	    	if ( considerFlip ){
	    	    int yHeight = (int) point1.getY();
	            return Math.toDegrees(Math.atan2( yHeight -point2.getY(), point2.getX()-point1.getX() ) );
	    	} else {
	    		return Math.toDegrees(Math.atan2(point2.getY()-point1.getY(), point2.getX()-point1.getX()));
	    	}
	    }
	    
	    public static Point2D calcPoint(Point2D point, double angle, double dist) {
	        return new Point2D.Double(
	                point.getX()+Math.cos(Math.toRadians(angle))*dist,
	                point.getY()+Math.sin(Math.toRadians(angle))*dist
	        );
	    }
}
