package r9.bvh.simpe.utils;
 

import java.awt.Color;
import java.util.List;
import java.util.Random;

public interface PageColors {
	
    public static final Color blue1 = new Color(80,179,226);
    public static final Color blue2 = new Color(48,155,204);
    public static final Color blue3 = new Color(30,125,165);
    public static final Color blue4 = new Color(0,87,122);
    
    public static final Color green1 = new Color(128,236,85);
    public static final Color green2 = new Color(67,203,53);
    public static final Color green3 = new Color(100,183,57);
    public static final Color green4 = new Color(66,142,34);
     
    public static final Color yellow1 = new Color(255,228,84);
    public static final Color yellow2 = new Color(243,213,0);
    public static final Color yellow3 = new Color(230,186,0);
    public static final Color yellow4 = new Color(204,147,0);
    
    public static final Color orange1 = new Color(255,191,104);
    public static final Color orange2 = new Color(255,166,24);
    public static final Color orange3 = new Color(246,156,0);
    public static final Color orange4 = new Color(220,121,0);
    
    public static final Color red1 = new Color(255,59,0);
    public static final Color red2 = new Color(255,0,0);
    public static final Color red3 = new Color(223,0,11);
    public static final Color red4 = new Color(188,0,0);
    
    public static final Color purple1 = new Color(173,7,185);
    public static final Color purple2 = new Color(120,0,134);
    public static final Color purple3  = new Color(95,0,108);
    public static final Color purple4 = new Color(67,0,74);
    
    public static final Color gray1 = new Color(255,255,255);
    public static final Color gray2 = new Color(191,191,191);
    public static final Color gray3 = new Color(127,127,127);
    public static final Color gray4 = new Color(64,64,64);
    public static final Color gray5 = new Color(0,0,0);
     
    public static Color nextColor(int s) { 
    	if( s == 0 ) return blue1;
    	if( s == 1 ) return green1;
    	if( s == 2 ) return orange1;
    	if( s == 3 ) return purple1;
    	if( s == 4 ) return gray1;
    	if( s == 5 ) return blue3;
    	if( s == 6 ) return green3;
    	if( s == 7 ) return orange3;
    	if( s == 8 ) return purple3;
    	if( s == 9 ) return gray3;
    	Random r = new Random(System.currentTimeMillis());
    	return new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255));
    }
}
