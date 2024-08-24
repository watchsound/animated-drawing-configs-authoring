package r9.bvh.simple.model;
 
import java.util.List;

import r9.bvh.simpe.math.Mat4;
import r9.bvh.simpe.math.Vec4;
import r9.bvh.simpe.utils.StringUtils;

/**
 * Node class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Node extends NodeBase<Node>{
    
    
   
    private String[] channels;
  //  private final Node parent;
  //  private final List<Node> childrens = new ArrayList<Node>();
    
    private static final Mat4 transformTmp = new Mat4();
    private final Mat4 transform = new Mat4();
   
    
    private double xrotation = Double.NaN;
    private double yrotation = Double.NaN;
    private double zrotation = Double.NaN;
    
    private double xposition = Double.NaN;
    private double yposition = Double.NaN;
    private double zposition = Double.NaN;
    
    
    public Node(Parser parser) {
        this(parser, Type.ROOT, null);
    }
    
    public Node(Parser parser, Type type, Node parent) {
        this.type = type;
        this.parent = parent;
        switch (type) {
            case ROOT:
                parser.expect("HIERARCHY");
                name = parser.expect("ROOT")[1];
                break;
            case JOINT:
                name = parser.expect("JOINT")[1];
                break;
            case END:
                name = parser.expect("End")[1];
        }
        parser.expect("{");
        setOffset(parser.expect("OFFSET"));
        if (parser.getLine().startsWith("CHANNELS")) {
            setChannels(parser.expect("CHANNELS"));
        }
        while (parser.getLine().startsWith("JOINT")) {
            childrens.add(new Node(parser, Type.JOINT, this));
        }
        if (parser.getLine().startsWith("End")) {
            childrens.add(new Node(parser, Type.END, this));
        }
        parser.expect("}");
    }
    
    public String toString() {
		return name + ":" + type + "  " + StringUtils.toString( channels ) ;
	}
    
  
    private void setChannels(String[] channelsTmp) {
        int size = Integer.parseInt(channelsTmp[1]);
        this.channels = new String[size];
        for (int i = 0; i < size; i++) {
            this.channels[i] = channelsTmp[2 + i].toLowerCase();
        }
    }

  
    public Mat4 getTransform() {
        return transform;
    }

    public String[] getChannels() {
        return channels;
    }

    public Node getParent() {
        return parent;
    }

    public List<Node> getChildrens() {
        return childrens;
    }
    
    public void fillNodesList(List<Node> nodes) {
        if (type == Type.ROOT) {
            nodes.clear();
        }
        if (!nodes.contains(this)) {
            nodes.add(this);
        }
        for (Node children : childrens) {
            children.fillNodesList(nodes);
        }
    }
    private static final int[] DATA_INDEX = { 0 };
    
    public void setPose(double[] data) {
        transform.setIdentity();
        transformTmp.setIdentity();
        DATA_INDEX[0] = 0;
        setPose(data, DATA_INDEX);
    }

    private void setPose(double[] data, int[] dataIndex) {
        if (type == Type.ROOT) {
            transform.setTranslation(offset);
        }
        else {
            transform.set(parent.getTransform());
            transformTmp.setTranslation(offset);
            transform.multiplyBy(transformTmp);
        }
         xrotation = Double.NaN;
         yrotation = Double.NaN;
         zrotation = Double.NaN;
        
         xposition = Double.NaN;
         yposition = Double.NaN;
         zposition = Double.NaN;
        if (channels != null && data != null) {
        	
            for (int c = 0; c < channels.length; c++) {
                String channel = channels[c];
                double value = data[dataIndex[0]++];
                if (channel.equals("xposition")) {
                    transformTmp.setTranslation(value, 0, 0);
                    xposition = value;
                }
                else if (channel.equals("yposition")) {
                    transformTmp.setTranslation(0, value, 0);
                    yposition = value;
                }
                else if (channel.equals("zposition")) {
                    transformTmp.setTranslation(0, 0, value);
                    zposition = value;
                }
                else if (channel.equals("zrotation")) {
                    transformTmp.setRotationZ(Math.toRadians(value));
                    zrotation = value;
                }
                else if (channel.equals("yrotation")) {
                    transformTmp.setRotationY(Math.toRadians(value));
                    yrotation = value;
                }
                else if (channel.equals("xrotation")) {
                    transformTmp.setRotationX(Math.toRadians(value));
                    xrotation = value;
                }
                transform.multiplyBy(transformTmp);
            }
        }
        
        position.set(0, 0, 0, 1);
        transform.multiplyTo(position);
        
        for (Node children : childrens) {
            children.setPose(data, dataIndex);
        }
    }
    
    
    public String toBVHFile(int nestLevel) {
    	if( this.getType() == Type.END ) {
    		StringBuilder sb = new StringBuilder();  
        	sb.append(Utils.getIndent(nestLevel-1) + "End " +  getName()+ "\n");
        	sb.append(Utils.getIndent(nestLevel-1) +"{" + "\n");
        	sb.append(Utils.getIndent(nestLevel) + "OFFSET " + getOffset().toValueStr(false) + "\n");
        	String[] channels =  getChannels();
        	if( channels != null && channels.length>0 )
        	    sb.append(Utils.getIndent(nestLevel) + "CHANNELS " + channels.length + " " + Utils.toString(channels)+ "\n");
         	sb.append(Utils.getIndent(nestLevel-1) +"}" + "\n");
        	return sb.toString();
    	}
    	StringBuilder sb = new StringBuilder();  
    	if( this.getType() == Type.ROOT )
    	    sb.append(Utils.getIndent(nestLevel-1) + "ROOT " +  getName()+ "\n");
    	else
    		sb.append(Utils.getIndent(nestLevel-1) + "JOINT " +  getName()+ "\n");
    	sb.append(Utils.getIndent(nestLevel-1) +"{" + "\n");
    	sb.append(Utils.getIndent(nestLevel) + "OFFSET " + getOffset().toValueStr(false) + "\n");
    	String[] channels =  getChannels();
    	sb.append(Utils.getIndent(nestLevel) + "CHANNELS " + channels.length + " " + Utils.toString(channels)+ "\n");
    	for(Node node:  this.childrens) {
    		String c = node.toBVHFile(nestLevel+1);
    		sb.append(c);
    	}
    	
    	sb.append(Utils.getIndent(nestLevel-1) +"}" + "\n");
    	return sb.toString();
    	
    }

	public double getXrotation() {
		return xrotation;
	}

	public double getYrotation() {
		return yrotation;
	}

	public double getZrotation() {
		return zrotation;
	}

	public double getXposition() {
		return xposition;
	}

	public void setXposition(double xposition) {
		this.xposition = xposition;
	}

	public double getYposition() {
		return yposition;
	}

	public void setYposition(double yposition) {
		this.yposition = yposition;
	}

	public double getZposition() {
		return zposition;
	}

	public void setZposition(double zposition) {
		this.zposition = zposition;
	}

	public void setXrotation(double xrotation) {
		this.xrotation = xrotation;
	}

	public void setYrotation(double yrotation) {
		this.yrotation = yrotation;
	}

	public void setZrotation(double zrotation) {
		this.zrotation = zrotation;
	}

	public void updateMotionData(double[] newdata) {
	    DATA_INDEX[0] = 0;
	    updateMotionData(newdata, DATA_INDEX);
    }

   private void updateMotionData(double[] newdata, int[] dataIndex) { 
      if (channels != null && newdata != null) {
      	
          for (int c = 0; c < channels.length; c++) {
              String channel = channels[c]; 
              if (channel.equals("xposition")) {
            	  newdata[dataIndex[0]++] = xposition  ;
              }
              else if (channel.equals("yposition")) {
            	  newdata[dataIndex[0]++] = yposition  ; 
              }
              else if (channel.equals("zposition")) {
            	  newdata[dataIndex[0]++] = zposition  ;  
              }
              else if (channel.equals("zrotation")) {
            	  newdata[dataIndex[0]++] = zrotation  ; 
              }
              else if (channel.equals("yrotation")) {
            	  newdata[dataIndex[0]++] = yrotation  ;  
              }
              else if (channel.equals("xrotation")) {
            	  newdata[dataIndex[0]++] = xrotation  ;   
              } 
          }
      }
       
      for (Node children : childrens) {
          children.updateMotionData(newdata, dataIndex);
      }
  }
    
    
}
