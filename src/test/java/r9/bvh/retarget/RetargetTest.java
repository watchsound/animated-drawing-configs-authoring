package r9.bvh.retarget;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import r9.bvh.simpe.utils.u;
import r9.bvh.simple.model.NodeBase;
import r9.bvh.simple.retarget.RetargetConfig;
import r9.bvh.simple.retarget.RetargetConfig.bvh_projection_bodypart_group ;
import r9.bvh.simple.retarget.RetargetConfig.char_bodypart_group;
import r9.bvh.simple.ui.fbfig.CharacterConfig; 

/**
 * Unit test for simple App.
 */
public class RetargetTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public RetargetTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( RetargetTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    { 
    	 InputStream is = getClass().getResourceAsStream( "cm1_pfp.yaml" ); 
    	 RetargetConfig target = new RetargetConfig(is);
    	 try {
 			is.close();
 		 } catch (IOException e) { 
 		 }
    	 
    	 String yamlstr = target.toYaml();
    	 System.out.println(yamlstr);
    	// Yaml yaml = new Yaml();  
    	// yaml.load(yamlstr);
    	 
    	 is = new ByteArrayInputStream(yamlstr.getBytes());
    	 target = new RetargetConfig(is);
    	 try {
 			is.close();
 		 } catch (IOException e) { 
 		 }
    	 
    	 
	  //   System.out.println( fig );
    }
}
