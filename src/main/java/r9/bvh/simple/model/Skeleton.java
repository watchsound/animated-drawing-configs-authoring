package r9.bvh.simple.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import r9.bvh.simple.model.Motion.MFrame;

/**
 * Skeleton class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Skeleton {
    
	private int id = 0;
    private Node rootNode;
    private Motion motion;
    private final List<Node> nodes = new ArrayList<Node>();
    int frameIndex = -1;
    public Skeleton(InputStream is) {
        Parser parser = new Parser();
        parser.load(is);
        rootNode = new Node(parser);
        motion = new Motion(parser);
        rootNode.fillNodesList(nodes);
        this.setPose(0);
        rootNode.calculateDistanceToChildren(1);
    }
    public Skeleton(File file) {
    	InputStream is = null;
		try {
			is = new FileInputStream(file);
			 Parser parser = new Parser();
	        parser.load(is);
	        rootNode = new Node(parser);
		     motion = new Motion(parser);
		     rootNode.fillNodesList(nodes);
		      this.setPose(-1);
		       rootNode.calculateDistanceToChildren(1);
		} catch (FileNotFoundException e) {
			 e.printStackTrace();
		} finally {
			try {
				if(is != null ) 
					is.close();
			} catch (IOException e) { 
				e.printStackTrace();
			}
		}
        
    }
    
    public int nextId() {
    	id++;
    	return id;
    }
    

    public int getFrameIndex() {
		return frameIndex;
	}

	public Node getRootNode() {
        return rootNode;
    }

    public Motion getMotion() {
        return motion;
    }

    public List<Node> getNodes() {
        return nodes;
    }
    
    public int getFrameSize() {
        return motion.getFrameSize();
    }
    
    public void setPose(int frameIndex) {
    	this.frameIndex = frameIndex;
        if (frameIndex < 0) {
            rootNode.setPose(null);
        }
        else {
            rootNode.setPose(motion.getData(frameIndex).datum);
        }
    }
    
    public void updateMotionData() {
    	MFrame f = motion.getData(this.frameIndex);
    	double[] newdata = new double[f.datum.length];
    	rootNode.updateMotionData(newdata);
    	for(int i =0; i < newdata.length; i++)
    	    f.datum[i] = newdata[i];
    }

    
    public String toBVHFile() {
    	StringBuilder sb = new StringBuilder();
    	int nestLevel = 1;
    	sb.append("HIERARCHY" + "\n");
     	sb.append("ROOT " + rootNode.getName()+ "\n");
     	sb.append("{" + "\n");
    	sb.append(Utils.getIndent(nestLevel) + "OFFSET " + rootNode.getOffset().toValueStr(false) + "\n");
    	String[] channels = rootNode.getChannels();
    	sb.append(Utils.getIndent(nestLevel) + "CHANNELS " + channels.length + " " + Utils.toString(channels)+ "\n");
    	for(Node node: rootNode.getChildrens()) {
    		String c = node.toBVHFile(nestLevel+1);
    		sb.append(c);
    	}
    	
    	sb.append("}" + "\n");
    	
    	sb.append("MOTION" + "\n");
    	sb.append("Frames: " + this.getFrameSize() + "\n");
    	sb.append("Frame Time: " + this.getMotion().getFrameTime() + "\n");
        for(int i = 0; i < this.getFrameSize(); i++) {
        	double[] d = this.getMotion().getData(i).datum;
        	sb.append(Utils.toString(d) + "\n");
        } 
    	return sb.toString(); 
    }
}
