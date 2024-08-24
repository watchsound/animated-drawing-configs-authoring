package r9.bvh.simple.model;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import r9.bvh.simpe.math.Vec4;

public class NodeBase<T extends NodeBase>{
  
	public static enum Type { ROOT, JOINT, END }
    
    protected   Type type;
    protected   String name;
    protected   T parent;
    protected   double distanceToParent;
    protected   List<T> childrens = new ArrayList<>();
    protected final Vec4 position = new Vec4();
    protected final Vec4 offset = new Vec4();
    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }
    
    public T getParent() {
        return parent;
    }
    public void setType(Type type) {
  		this.type = type;
  	}

  	public void setName(String name) {
  		this.name = name;
  	}

  	public void setParent(T parent) {
  		this.parent = parent;
  	}

    public Vec4 getPosition() {
        return position;
    }

    public List<T> getChildrens() {
        return childrens;
    }


    public Vec4 getOffset() {
        return offset;
    }
   
 
    protected void setOffset(String[] offsetStr) {
        offset.setX(Double.parseDouble(offsetStr[1]));
        offset.setY(Double.parseDouble(offsetStr[2]));
        offset.setZ(Double.parseDouble(offsetStr[3]));
        offset.setW(1);
    }

    
    public double getDistanceToParent() {
    	if( this.distanceToParent == 0 && this.parent != null) 
    		parent.calculateDistanceToChildren(1);
		return distanceToParent;
	}

	public  void calculateDistanceToChildren( double scale ) { 
		 int x1 = (int) (scale *  getPosition().getX());
         int y1 = (int) (-scale *  getPosition().getY());   
         int z1 = (int) ( scale *  getPosition().getZ());   
		//this.distanceToParent =  this.getOffset().distance() * scale;
      for (int n = 0; n < childrens.size(); n++) {
          NodeBase<NodeBase> node = childrens.get(n);
          int x2 = (int) (scale * node.getPosition().getX());
          int y2 = (int) (-scale * node.getPosition().getY());
          int z2 = (int) ( scale * node.getPosition().getZ());
          node.distanceToParent = Math.sqrt( (x1-x2)*(x1-x2)+(y1-y2)*(y1-y2)+(z1-z2)*(z1-z2));
          for (NodeBase child : node.getChildrens()) {
              child.calculateDistanceToChildren(scale);
          }
      } 
  }
	public  void shift( double xc,  double yc, double zc) { 
		  getOffset().setX( getOffset().getX()+xc);
		  getOffset().setY( getOffset().getY()+yc);  
		  getOffset().setZ( getOffset().getZ()+zc);  
//	     for (int n = 0; n < childrens.size(); n++) {
//	         NodeBase<NodeBase> node = childrens.get(n); 
//	         for (NodeBase child : node.getChildrens()) {
//	             child.shiftAll(xc, yc);
//	         }
//	     } 
   }
}
