package r9.bvh.simple.animation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import r9.bvh.simpe.utils.u;

/** 
 * This contains information about the motion used to drive the Animated Drawing. Currently, only BVH (BioVision Hierarchy) files
are supported, but there is considerable flexibility regarding the skeleton specified within the BVH (note- only BVH's with one
skeleton are supported).
 */
public class MotionConfig {
	public String filepath;// (str): Path to the BVH file. This can be an absolute path, path relative to the current working directory, or path
	//relative the AnimatedDrawings root directory.
	public int start_frame_idx;// (int): If you want to skip beginning motion frames, this can be set to an int between 0
	//and end_frame_idx , inclusive.
	public int end_frame_idx ;//(int); If you want to skip ending motion frames, this can be set to an int between start_frame_idx+1 and
	//the BVH Frames Count, inclusive.
	public String groundplane_joint;// (str): The name of a joint that exists within the BVH's skeleton. When visualizing the BVH's motion,
	//the skeleton will have it's worldspace y offset adjusted so this joint is within the y=0 plane at start_frame_idx .
	public List<List<String>> forward_perp_joint_vectors = new ArrayList<>() ;//(list[List[str, str]]): During retargeting, it is necessary to compute the 'foward' vector for the
	//skeleton at each frame. To compute this, we define a series of joint name pairs. Each joint name specifies a joint within
	//the BVH skeleton. For each pair, we compute the normalized vector from the first joint to the second joint. We then
	//compute the average of these vectors and compute its counter-clockwise perpendicular vector. We zero out this vector's y
	//value, and are left with a vector along an xz plane indicating the skeleton's forward vector.
	public double scale;// (float): Uniformly scales the BVH skeleton. Useful for visualizing the BVH motion. Scaling the skeleton so it fits
	//roughly within a (1, 1, 1) cube will visualize nicely.
	public String up = "+z";// (str): The direction corresponding to 'up' within the BVH. This is used during retargeting, not just BVH motion
	//visualization. Currently, only +y and +z are supported.
	
	
	public static MotionConfig loadFromFile(File file) throws Exception {
		InputStream is = new FileInputStream(file);
		MotionConfig f = null;
		// Yaml yaml = new Yaml();
		// Object document = yaml.load(is);
		// if( document instanceof CharacterFig) {
		// f = (CharacterFig)document;
		// } else {
		f = new MotionConfig(is);
		// }
		is.close();
		return f;
	}

	public MotionConfig() {
	}

	/**
	 * 
	 * @param is yarml data
	 */
	public MotionConfig(InputStream is) {
		// InputStream is = getClass().getResourceAsStream( "cm1_pfp.yaml" );
		String input = "";
		try {
			input = u.fileToString(is);
			// input = input.replaceAll("python/tuple", "python.tuple");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		MotionConfig motion = this;
		Yaml yaml = new Yaml();
		Map<Object, Object> document = yaml.load(input); 
		
		if( document.containsKey("filepath"))
			motion.filepath = (String)document.get("filepath");
		if( document.containsKey("start_frame_idx"))
			motion.start_frame_idx = (int)document.get("start_frame_idx");
		if( document.containsKey("end_frame_idx"))
			motion.end_frame_idx = (int)document.get("end_frame_idx");
		if( document.containsKey("groundplane_joint"))
			motion.groundplane_joint = (String)document.get("groundplane_joint");
		
		if( document.containsKey("forward_perp_joint_vectors")) {
			List<List<String>> jvectors = (List<List<String>>) document.get("forward_perp_joint_vectors");
		    motion.forward_perp_joint_vectors = jvectors;
		}
		
		if( document.containsKey("up"))
			motion.up = (String)document.get("up");
		if( document.containsKey("scale"))
			motion.scale = (double)document.get("scale");
		 
	}
	
	public String toYaml() {
		 MotionConfig motion = this;
		 Map<String, Object> data = new LinkedHashMap<String, Object>();
		 
		 if( motion.filepath != null )
			 data.put("filepath", motion.filepath);
		 
		 data.put("start_frame_idx", motion.start_frame_idx);
		 data.put("end_frame_idx", motion.end_frame_idx);
		 
		 if( motion.groundplane_joint != null )
			 data.put("groundplane_joint", motion.groundplane_joint);
		
		 if( motion.forward_perp_joint_vectors != null ) {
			 data.put("forward_perp_joint_vectors", motion.forward_perp_joint_vectors);
		 }
		 
		 data.put("scale", motion.scale);
		 data.put("up", motion.up);
			
		 
		 Yaml yaml = new Yaml();
			StringWriter writer = new StringWriter();
			yaml.dump(data, writer);
			String code = writer.toString();
			//code = code.replaceAll("python.tuple", "python/tuple");
			return code;
	}
	
}
