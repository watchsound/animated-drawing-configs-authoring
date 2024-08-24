package r9.bvh.simple;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper; 

import com.alee.laf.WebLookAndFeel;
import com.alee.laf.optionpane.WebOptionPaneUI;

import r9.bvh.simpe.utils.u;
 

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )  
    {
		try {
			SwingUtilities.invokeAndWait( new Runnable ()
			{
			    public void run ()
			    { 
//			    	try {
//			            // Install WebLaF as application L&F 
//			        	int fontSize =  14  ; 
//			        	WebLookAndFeel.globalControlFont  = new FontUIResource("宋体",0, fontSize); //            
//			        	WebLookAndFeel.globalTooltipFont  = new FontUIResource("宋体",0, fontSize);//             
//			        //	WebLookAndFeel.globalAlertFont= new FontUIResource("宋体",0, fontSize); //       
//			        	
//			        	WebLookAndFeel.globalMenuFont = new FontUIResource("宋体",0, fontSize);//             
//			       // 	WebLookAndFeel.globalAcceleratorFont = new FontUIResource("宋体",0, fontSize);//        
//			       // 	WebLookAndFeel.globalTitleFont = new FontUIResource("宋体",0, fontSize); //             
//			        	WebLookAndFeel.globalTextFont = new FontUIResource("宋体",0, fontSize);            
//			    	           
//			    	
//			    		  WebLookAndFeel.install ();
//			    		  
//			    		  UIManager.put("OptionPaneUI",  WebOptionPaneUI.class.getCanonicalName() );
//			              
//			              ////////////////////////////////////
//			              //add customized slider
//			              UIManager.put("Slider.background",new ColorUIResource(BeautyEyeLNFHelper.commonBackgroundColor)); 
//			      		UIManager.put("Slider.tickColor",new ColorUIResource(new Color(154,154,154)));
//			      		UIManager.put("Slider.foreground",new ColorUIResource(BeautyEyeLNFHelper.commonForegroundColor)); 
////              		UIManager.put("Slider.focusInsets",new InsetsUIResource(2,2,7,7));  
//			      		UIManager.put("Slider.focus",new ColorUIResource(BeautyEyeLNFHelper.commonFocusedBorderColor)); 
//			      		UIManager.put("SliderUI",org.jb2011.lnf.beautyeye.ch15_slider.BESliderUI.class.getName()); 
//			    	 }catch(Exception ex) {
//			    		 u.p(ex.getMessage(), u.LEVEL.DEBUG);
//			    		 ex.printStackTrace();
//			    	 }
//			       
			      
			    	
			    	
			    	try {
			    	    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			    	        if ("Nimbus".equals(info.getName())) {
			    	            UIManager.setLookAndFeel(info.getClassName());
			    	            break;
			    	        }
			    	    }
			    	    
			    	} catch (Exception e) {
			    	    // If Nimbus is not available, you can set the GUI to another look and feel.
			    	}
			    	
				 	new Workspace();
			    }
			} );
		} catch (InvocationTargetException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
}
