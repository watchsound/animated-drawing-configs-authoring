package r9.bvh.simple.model;

import java.util.ArrayList;
import java.util.List;

import r9.bvh.simpe.utils.StringUtils;

/**
 * Motion class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Motion {
	
	public static class MFrame {
		public final int id;
		public final double[] datum;
		public boolean dirty = true;
		
		public MFrame(int id, double[] datum) {
			this.id = id;
			this.datum = datum;
		}
		public String toString() {
			return id + ":" + StringUtils.toString( datum );
		}
		
	}
	private int id = 0;
    private int frameSize;
    private double frameTime;
    private List<MFrame> data = new ArrayList<>();
    
    public Motion(Parser parser) {
        parser.expect("MOTION");
        frameSize = Integer.parseInt(parser.expect("Frames:")[1]);
        frameTime = Double.parseDouble(parser.expect("Frame Time:")[2]);
     //   data = new double[frameSize][];
        for (int f = 0; f < frameSize; f++) {
            String[] values = parser.getLine().split("\\ ");
            double[] datum = new double[values.length];
            for (int d = 0; d < values.length; d++) {
            	datum[d] = Double.parseDouble(values[d]);
            }
            this.data.add(new MFrame(nextId(), datum));
            parser.nextLine();
        }        
    }
    public int nextId() {
    	id++;
    	return id;
    }
    public int getFrameSize() {
        return frameSize;
    }

    public double getFrameTime() {
        return frameTime;
    }
    public void setFrameTime(double f) {
    	this.frameTime = f;
    }

    public MFrame getData(int frame) {
        return data.get(frame);
    }
    
    public MFrame getDataById(int id) {
       for(MFrame f : data)	{
    	   if( f.id == id )
    		   return f;
       }
    	return null;
    } 
    public int getIndex(MFrame d) {
    	return getIndexById(d.id);
    }
    public int getIndexById(int id) {
    	for(int i = 0; i < data.size(); i++) {
           MFrame f = data.get(i);
     	   if( f.id == id )
     		   return i;
        }
     	return -1;
     } 
    public void delete(int frame) {
    	frameSize --;
    	this.data.remove(frame);
//    	double[][] newdata = new double[data.length-1][data[0].length];
//    	for(int i = 0; i < frame; i++) {
//    		newdata[i] = data[i];
//    	}
//    	for(int j = frame+1; j< data.length; j++) {
//    		newdata[j-1] = data[j];
//    	}
//    	this.data = newdata; 
    }
    public int indexOf(double[] row) {
    	for(int j = 0; j< data.size(); j++) {
    		MFrame f = data.get(j);
    		if( f.datum == row)
    			return j;
    	}
    	return -1;
    }
    public int indexOf(MFrame row) {
    	return this.data.indexOf(row);
    }
    public void duplicate(int frame){
    	frameSize ++;
    	MFrame o = this.data.get(frame);
    	double[] dup = new double[ o.datum.length ];
    	for(int i = 0; i < dup.length; i++) {
		    dup[i] = o.datum[i];
	    }
    	MFrame d = new MFrame(this.nextId(), dup);
    	this.data.add(frame, d);
//    	double[] dup = new double[data[frame].length];
//    	for(int i = 0; i < dup.length; i++) {
//    		dup[i] = data[frame][i];
//    	}
//    	
//    	double[][] newdata = new double[data.length+1][data[0].length];
//    	for(int i = 0; i <= frame; i++) {
//    		newdata[i] = data[i];
//    	}
//    	newdata[frame+1] = dup;
//    	
//    	for(int j = frame+1; j< data.length; j++) {
//    		newdata[j+1] = data[j];
//    	}
//    	this.data = newdata;
//    	return this.data;
    }
    public void set(int index, MFrame element) {
		 this.data.set(index, element);
	}
//	public void set(int index, double[] element) {
//		//this.data[index] = element;
//		MFrame o = this.data.get(index);
//		for(int i = 0; i < element.length; i++)
//		    o.datum[i] = element[i];
//	}
    
    public void add(int index, MFrame item) {
        this.data.add(index, item);
        frameSize ++;
    }
    

//	public double[][] add(int index, double[] item) {
//		frameSize ++;
//    	double[] dup =  item;
//    	
//    	double[][] newdata = new double[data.length+1][data[0].length];
//    	for(int i = 0; i < index; i++) {
//    		newdata[i] = data[i];
//    	}
//    	newdata[index] = dup;
//    	
//    	for(int j = index; j< data.length; j++) {
//    		newdata[j+1] = data[j];
//    	}
//    	this.data = newdata;
//    	return this.data;
//	}

//	public void clearAllData() {
//		frameSize = 1;
//		double[][] newdata = new double[1][data[0].length];
//		newdata[0] = data[0];
//		this.data = newdata;
//	}
//
//	public double[] get(int index) {
//		 return this.data[index];
//	}

	 
    public void clearAllData() {
    	MFrame d = this.data.get(0);
    	this.data.clear();
    	this.data.add(d);
    	frameSize = 1;
    }
    
    public MFrame get(int index) {
    	return this.data.get(index);
    }
}
