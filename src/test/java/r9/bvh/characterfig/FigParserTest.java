package r9.bvh.characterfig;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.yaml.snakeyaml.Yaml;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import r9.bvh.simple.model.NodeBase;
import r9.bvh.simple.model.Skeleton;
import r9.bvh.simple.ui.fbfig.CharacterConfig; 

/**
 * Unit test for simple App.
 */
public class FigParserTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public FigParserTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( FigParserTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    { 
    	 InputStream is = getClass().getResourceAsStream( "char_cfg.yaml" );
	     
	     CharacterConfig fig = new CharacterConfig(is);
	     try {
			is.close();
		} catch (IOException e) { 
		}
	     System.out.println( fig );
    }
}
