package r9.bvh.simple;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import r9.bvh.simple.model.Skeleton; 

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
    	InputStream is = getClass().getResourceAsStream( "dancing.bvh" );
    	Skeleton   skeleton = new Skeleton(is);
    	
    	String bvh = skeleton.toBVHFile();
    	System.out.println(bvh);
        assertTrue( true );
        try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
