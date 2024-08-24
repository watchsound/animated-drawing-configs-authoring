package r9.bvh.mvc;


import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.yaml.snakeyaml.Yaml;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import python.tuple;
import r9.bvh.simple.animation.MvcConfig;
import r9.bvh.simple.animation.MvcConfig.ANIMATED_CHARACTER;
import r9.bvh.simple.model.NodeBase;
import r9.bvh.simple.model.Skeleton;
import r9.bvh.simple.retarget.RetargetConfig.bvh_projection_bodypart_group;
import r9.bvh.simple.retarget.RetargetConfig.char_bodypart_group;
import r9.bvh.simple.retarget.RetargetConfig.char_bvh_root_offset;
import r9.bvh.simple.ui.fbfig.CharacterConfig; 

/**
 * Unit test for simple App.
 */
public class MVCTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MVCTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( MVCTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    { 
    	 InputStream is = getClass().getResourceAsStream( "export_gif_example.yaml" );
		 MvcConfig mvc = new MvcConfig(is);   
		 
		 
		 Map<String, Object> data = new LinkedHashMap<String, Object>();
		 
		 LinkedHashMap<String, Object> scene = new LinkedHashMap<>();
		 data.put("scene", scene);
		 
		 ArrayList<LinkedHashMap<String, Object>> characters = new ArrayList<>();

		for (ANIMATED_CHARACTER eo : mvc.scene.ANIMATED_CHARACTERS) {
			LinkedHashMap<String, Object> vs = new LinkedHashMap<>();
			vs.put("character_cfg", eo.character_cfg );
			vs.put("motion_cfg", eo.motion_cfg);
			vs.put("retarget_cfg", eo.retarget_cfg);
			characters.add(vs);
		}
		scene.put("ANIMATED_CHARACTERS", characters);
		if(  mvc.scene.ADD_FLOOR )
		    scene.put("ADD_FLOOR", mvc.scene.ADD_FLOOR);
		if(  mvc.scene.ADD_AD_RETARGET_BVH )
		    scene.put("ADD_AD_RETARGET_BVH", mvc.scene.ADD_AD_RETARGET_BVH);
		
		
		
		 
		 if( mvc.view != null) {
			 LinkedHashMap<String, Object> view = new LinkedHashMap<>();
			 data.put("view", view);
			 
			 if(mvc.view.CLEAR_COLOR != null) {
				    List<Double> cs = new ArrayList<>();
				    cs.add(mvc.view.CLEAR_COLOR[0]);
				    cs.add(mvc.view.CLEAR_COLOR[1]);
				    cs.add(mvc.view.CLEAR_COLOR[2]);
				    if( mvc.view.CLEAR_COLOR.length>3)
				        cs.add(mvc.view.CLEAR_COLOR[3]); 
				    view.put("CLEAR_COLOR", cs);
			 }
			 if(mvc.view.WINDOW_DIMENSIONS != null) {
				    List<Integer> cs = new ArrayList<>();
				    cs.add(mvc.view.WINDOW_DIMENSIONS[0]);
				    cs.add(mvc.view.WINDOW_DIMENSIONS[1]); 
				    view.put("WINDOW_DIMENSIONS", cs);
			 }
			 if(mvc.view.CAMERA_POS != null) {
				    List<Double> cs = new ArrayList<>();
				    cs.add(mvc.view.CAMERA_POS[0]);
				    cs.add(mvc.view.CAMERA_POS[1]);
				    cs.add(mvc.view.CAMERA_POS[2]); 
				    view.put("CAMERA_POS", cs);
			 }
			 if(mvc.view.CAMERA_FWD != null) {
				    List<Double> cs = new ArrayList<>();
				    cs.add(mvc.view.CAMERA_FWD[0]);
				    cs.add(mvc.view.CAMERA_FWD[1]);
				    cs.add(mvc.view.CAMERA_FWD[2]); 
				    view.put("CAMERA_FWD", cs);
			 }
			 if( mvc.view.DRAW_AD_COLOR )
				 view.put("DRAW_AD_COLOR", mvc.view.DRAW_AD_COLOR);
			 if( mvc.view.USE_MESA )
				 view.put("USE_MESA", mvc.view.USE_MESA);
			 if( mvc.view.DRAW_AD_RIG )
				 view.put("DRAW_AD_RIG", mvc.view.DRAW_AD_RIG);
			 if( mvc.view.DRAW_AD_TXTR )
				 view.put("DRAW_AD_TXTR", mvc.view.DRAW_AD_TXTR);
			 if( mvc.view.DRAW_AD_MESH_LINES )
				 view.put("DRAW_AD_MESH_LINES", mvc.view.DRAW_AD_MESH_LINES);
			 if( mvc.view.BACKGROUND_IMAGE != null)
				 view.put("BACKGROUND_IMAGE", mvc.view.BACKGROUND_IMAGE); 
		 }
		 
		    
		 if( mvc.controller != null) {
			 LinkedHashMap<String, Object> controller = new LinkedHashMap<>();
			 data.put("controller", controller);
			 
			 if( mvc.controller.MODE != null)
				 controller.put("MODE", mvc.controller.MODE); 
			 if( mvc.controller.KEYBOARD_TIMESTEP != 0)
				 controller.put("KEYBOARD_TIMESTEP", mvc.controller.KEYBOARD_TIMESTEP); 
			
			 if( mvc.controller.OUTPUT_VIDEO_PATH != null)
				 controller.put("OUTPUT_VIDEO_PATH", mvc.controller.OUTPUT_VIDEO_PATH); 
			 if( mvc.controller.OUTPUT_VIDEO_CODEC != null)
				 controller.put("OUTPUT_VIDEO_CODEC", mvc.controller.OUTPUT_VIDEO_CODEC); 
		 
		 }
		 
		  
	     try {
			is.close();
		} catch (IOException e) { 
		}
	    // System.out.println( fig );
    }
}
