package r9.bvh.simple.imageprocess;


import java.awt.Color;
import java.awt.image.BufferedImage;


//http://blog.csdn.net/jia20003/article/details/8908464

//floodFillScanLineWithStack(x, y, Color.GREEN.getRGB(), ffa.getColor(x, y)); 

public class FloodFillAlgorithm {
	
	 // Method to extract RGB components from the integer representation of a color
    public static int[] getRGBComponents(int rgb) {
        int[] rgbComponents = new int[3];
        rgbComponents[0] = (rgb >> 16) & 0xFF; // Red component
        rgbComponents[1] = (rgb >> 8) & 0xFF;  // Green component
        rgbComponents[2] = rgb & 0xFF;         // Blue component
        return rgbComponents;
    }
    
    public static int averageColor(int... rgbs) {
    	int[][] vs = new int[rgbs.length][3];
    	for(int i = 0; i < rgbs.length; i++) {
    		vs[i] = getRGBComponents(rgbs[i]);
    	}
    	int[] avg = new int[3];
    	for(int j = 0; j < 3; j++) {
    		int total = 0;
    		for(int i = 0; i < vs.length; i++)
    			total += vs[i][j];
    		avg[j] = total / vs.length;
    	}
    	return new Color(avg[0], avg[1], avg[2]).getRGB();
    }
    
    public static double calculateColorSimilarity(int rgb1, int rgb2) {
        int[] color1Components = getRGBComponents(rgb1);
        int[] color2Components = getRGBComponents(rgb2);

        int r1 = color1Components[0];
        int g1 = color1Components[1];
        int b1 = color1Components[2];
        int r2 = color2Components[0];
        int g2 = color2Components[1];
        int b2 = color2Components[2];

        // Euclidean distance formula in 3D space
        double distance = Math.sqrt(Math.pow(r2 - r1, 2) + Math.pow(g2 - g1, 2) + Math.pow(b2 - b1, 2));
        return distance;
    }
	
	  private BufferedImage inputImage;  
	  private BufferedImage outImage;  
	    private int[] inPixels;  
	    private int[] outPixels;
	    private int width;  
	    private int height;  
	      
	    //  stack data structure  
	    private int maxStackSize = 2000; // will be increased as needed  
	    private int[] xstack = new int[maxStackSize];  
	    private int[] ystack = new int[maxStackSize];  
	    private int stackSize;  
	  
	    private int colorDifference = 100;
	    public void clearall() {
	    	inPixels = null;
	    	outPixels = null;
	    	xstack = null;
	    	ystack = null;
	    }
	    
	    
	    public FloodFillAlgorithm(BufferedImage rawImage, BufferedImage outImage) {  
	        this.inputImage = rawImage;  
	        this.outImage = outImage;
	        width = rawImage.getWidth();  
	        height = rawImage.getHeight();  
	        inPixels = new int[width*height];  
	        for(int j = 0; j < height; j++){
	             for(int i = 0; i < width; i++){ 
	        		inPixels[j*width+i] = rawImage.getRGB(i , j );
	        	}
	        }
	        if( outImage == null){
	        	 
	        } else {
	        	outPixels = new int[width*height];  
		        for(int j = 0; j < height; j++){
		             for(int i = 0; i < width; i++){ 
		            	 outPixels[j*width+i] = 0;
		        	}
		        }
	        }
	        
	        //getRGB(rawImage, 0, 0, width, height, inPixels );  
	    }  
	    
	    
	   



		public int getColorDifference() {
			return colorDifference;
		}

		public void setColorDifference(int colorDifference) {
			this.colorDifference = colorDifference;
		}

		public BufferedImage getInputImage() {  
	        return inputImage;  
	    }  
	  
	    public void setInputImage(BufferedImage inputImage) {  
	        this.inputImage = inputImage;  
	    }  
	    
	      
	    public int getColor(int x, int y)  
	    {  
	        int index = y * width + x;  
	        return inPixels[index];  
	    }  
	      
	    public void setColor(int x, int y, int newColor)  
	    {  
	        int index = y * width + x;  
	        if( outImage == null)
	            inPixels[index] = newColor;  
	        else
	            outPixels[index] = newColor;  
	    }  
	      
	    public void updateResult()  
	    {  
	        //setRGB( inputImage, 0, 0, width, height, inPixels );  
	    	for(int j = 0; j < height; j++){
	            for(int i = 0; i < width; i++){ 
	            	if( outImage == null)
	        	        inputImage.setRGB(i , j , inPixels[j*width+i] );
	            	else
	            		outImage.setRGB(i , j , outPixels[j*width+i] );
	        	}
	        }
	    }  
	      
	    /** 
	     * it is very low calculation speed and cause the stack overflow issue when fill  
	     * some big area and irregular shape. performance is very bad. 
	     *  
	     * @param x 
	     * @param y 
	     * @param newColor 
	     * @param oldColor 
	     */  
	    public void floodFill4(int x, int y, int newColor, int oldColor)  
	    {  
	        if(x >= 0 && x < width && y >= 0 && y < height   
	                && getColor(x, y) == oldColor && getColor(x, y) != newColor)   
	        {   
	            setColor(x, y, newColor); //set color before starting recursion  
	            floodFill4(x + 1, y,     newColor, oldColor);  
	            floodFill4(x - 1, y,     newColor, oldColor);  
	            floodFill4(x,     y + 1, newColor, oldColor);  
	            floodFill4(x,     y - 1, newColor, oldColor);  
	        }     
	    }  
	    /** 
	     *  
	     * @param x 
	     * @param y 
	     * @param newColor 
	     * @param oldColor 
	     */  
	    public void floodFill8(int x, int y, int newColor, int oldColor)  
	    {  
	        if(x >= 0 && x < width && y >= 0 && y < height &&   
	                getColor(x, y) == oldColor && getColor(x, y) != newColor)   
	        {   
	            setColor(x, y, newColor); //set color before starting recursion  
	            floodFill8(x + 1, y,     newColor, oldColor);  
	            floodFill8(x - 1, y,     newColor, oldColor);  
	            floodFill8(x,     y + 1, newColor, oldColor);  
	            floodFill8(x,     y - 1, newColor, oldColor);  
	            floodFill8(x + 1, y + 1, newColor, oldColor);  
	            floodFill8(x - 1, y - 1, newColor, oldColor);  
	            floodFill8(x - 1, y + 1, newColor, oldColor);  
	            floodFill8(x + 1, y - 1, newColor, oldColor);  
	        }     
	    }  
	      
	    /** 
	     *  
	     * @param x 
	     * @param y 
	     * @param newColor 
	     * @param oldColor 
	     */  
	    public void floodFillScanLine(int x, int y, int newColor, int oldColor)  
	    {  
	        if(oldColor == newColor) return;  
	        if(getColor(x, y) != oldColor) return;  
	            
	        int y1;  
	          
	        //draw current scanline from start position to the top  
	        y1 = y;  
	        while(y1 < height && getColor(x, y1) == oldColor)  
	        {  
	            setColor(x, y1, newColor);  
	            y1++;  
	        }      
	          
	        //draw current scanline from start position to the bottom  
	        y1 = y - 1;  
	        while(y1 >= 0 && getColor(x, y1) == oldColor)  
	        {  
	            setColor(x, y1, newColor);  
	            y1--;  
	        }  
	          
	        //test for new scanlines to the left  
	        y1 = y;  
	        while(y1 < height && getColor(x, y1) == newColor)  
	        {  
	            if(x > 0 && getColor(x - 1, y1) == oldColor)   
	            {  
	                floodFillScanLine(x - 1, y1, newColor, oldColor);  
	            }   
	            y1++;  
	        }  
	        y1 = y - 1;  
	        while(y1 >= 0 && getColor(x, y1) == newColor)  
	        {  
	            if(x > 0 && getColor(x - 1, y1) == oldColor)   
	            {  
	                floodFillScanLine(x - 1, y1, newColor, oldColor);  
	            }  
	            y1--;  
	        }   
	          
	        //test for new scanlines to the right   
	        y1 = y;  
	        while(y1 < height && getColor(x, y1) == newColor)  
	        {  
	            if(x < width - 1 && getColor(x + 1, y1) == oldColor)   
	            {             
	                floodFillScanLine(x + 1, y1, newColor, oldColor);  
	            }   
	            y1++;  
	        }  
	        y1 = y - 1;  
	        while(y1 >= 0 && getColor(x, y1) == newColor)  
	        {  
	            if(x < width - 1 && getColor(x + 1, y1) == oldColor)   
	            {  
	                floodFillScanLine(x + 1, y1, newColor, oldColor);  
	            }  
	            y1--;  
	        }  
	    }  
	    private boolean similar(int x, int y, int color ) {
	    	return similar(x, y, color, colorDifference);
	    } 
	    private boolean similar(int x, int y, int color,  double threshold) {
	    	return calculateColorSimilarity( getColor(x, y), color) <= threshold;
	    }
	    
	    private boolean check(int x, int y, int color, boolean useSimilar) {
	    	if( useSimilar )
	    	    return similar(x, y, color, colorDifference);
	    	else
	    		return !similar(x, y, color, colorDifference);
	    } 
	    
	    public void floodFillScanLineWithStack(int x, int y, int newColor, int oldColor, boolean useSimilar)  
	    {  
	        if(oldColor == newColor) {  
	            System.out.println("do nothing !!!, filled area!!");  
	            return;  
	        }  
	        emptyStack();  
	          
	        int y1;   
	        boolean spanLeft, spanRight;  
	        push(x, y);  
	          
	        boolean dojob = true;
	        while(dojob)  
	        {      
	            x = popx();  
	            if(x == -1) return;  
	            y = popy();  
	            y1 = y;  
	            while(y1 >= 0 &&  check( x, y1 ,oldColor, useSimilar) ) y1--; // go to line top/bottom  
	            y1++; // start from line starting point pixel  
	            spanLeft = spanRight = false;  
	            while(y1 < height && check( x, y1 ,oldColor, useSimilar) )  
	            {  
	                setColor(x, y1, newColor);  
	                if(!spanLeft && x > 0 && check( x-1, y1 ,oldColor, useSimilar))// just keep left line once in the stack  
	                {  
	                    try { push(x - 1, y1);  }catch(Exception ex) { dojob = false; break; }
	                    spanLeft = true;  
	                }  
	                else if(spanLeft && x > 0 && check( x-1, y1 ,oldColor, useSimilar))  
	                {  
	                    spanLeft = false;  
	                }  
	                if(!spanRight && x < width - 1 && check( x+1, y1 ,oldColor, useSimilar)) // just keep right line once in the stack  
	                {  
	                	 try { push(x + 1, y1);   }catch(Exception ex) { dojob = false; break; }
	                    spanRight = true;  
	                }  
	                else if(spanRight && x < width - 1 && check( x+1, y1 ,oldColor, useSimilar))  
	                {  
	                    spanRight = false;  
	                }   
	                y1++;  
	            }  
	        }  
	          
	    }  
	      
	    private void emptyStack() {  
	        while(popx() != - 1) {  
	            popy();  
	        }  
	        stackSize = 0;  
	    }  
	  
	    final void push(int x, int y) {     
	        stackSize++;  
	        if( stackSize >= 8192000 )
	        	throw new RuntimeException();
	        if (stackSize==maxStackSize) {  
	            int[] newXStack = new int[maxStackSize*2];  
	            int[] newYStack = new int[maxStackSize*2];  
	            System.arraycopy(xstack, 0, newXStack, 0, maxStackSize);  
	            System.arraycopy(ystack, 0, newYStack, 0, maxStackSize);  
	            xstack = newXStack;  
	            ystack = newYStack;  
	            maxStackSize *= 2;     System.out.println( stackSize );
	        }  
	        xstack[stackSize-1] = x;  
	        ystack[stackSize-1] = y;  
	    }  
	      
	    final int popx() {  
	        if (stackSize==0)  
	            return -1;  
	        else  
	            return xstack[stackSize-1];  
	    }  
	  
	    final int popy() {  
	        int value = ystack[stackSize-1];  
	        stackSize--;  
	        return value;  
	    }  
	  
	 
	  
	} 
