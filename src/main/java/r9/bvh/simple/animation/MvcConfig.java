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

import python.tuple;
import r9.bvh.simpe.utils.u;
import r9.bvh.simple.animation.MvcConfig.ANIMATED_CHARACTER;
import r9.bvh.simple.retarget.RetargetConfig;
import r9.bvh.simple.retarget.RetargetConfig.bvh_projection_bodypart_group;
import r9.bvh.simple.retarget.RetargetConfig.char_bodypart_group;
import r9.bvh.simple.retarget.RetargetConfig.char_bvh_root_offset;
import r9.bvh.simple.ui.fbfig.CharacterConfig;

public class MvcConfig {

	public static class ANIMATED_CHARACTER {
		public String character_cfg = "character.yaml";
		public String motion_cfg = "motion.yaml";
		public String retarget_cfg = "retarget.yaml";
	}

	public static class scene {
		// If True , a floor will be added to the scene and rendered.
		public boolean ADD_FLOOR;
		// If True , a visualization of the original BVH motion driving hte Animated
		// Drawing
		// characters will be added to the scene.
		public boolean ADD_AD_RETARGET_BVH;

		public List<ANIMATED_CHARACTER> ANIMATED_CHARACTERS = new ArrayList<>();
	}

	public static class view {
		public double[] CLEAR_COLOR; // (List[float, float, float, float]): 0-1 float values indicating RGBA clear
										// color (i.e. background color).
		public int[] WINDOW_DIMENSIONS;// (List[int, int]): Width, height (in pixels) of the window or output video
										// file.
		public boolean DRAW_AD_RIG;// (bool): If True , renders the rig used to deform the Animated Drawings Mesh.
									// Hides it otherwise.
		public boolean DRAW_AD_TXTR;// (bool): If True , renders the texture of the Animated Drawings character.
									// Hides it otherwise.
		public boolean DRAW_AD_COLOR;// (bool): If True , renders the Animated Drawings mesh using per-joint colors
										// instead of the
		// original texture. Hides this otherwise.
		public boolean DRAW_AD_MESH_LINES;// (bool): If True , renders the Animated Drawings mesh edges. Hides this
											// otherwise.
		public double[] CAMERA_POS;// (List[float, float, float]): The xyz position of the camera used to render
									// the scene.
		public double[] CAMERA_FWD;// (List[float, float, float]): The vector used to define the 'foward'
									// orientation of the camera.
		public boolean USE_MESA;// (bool): If True , will attempt to use osmesa to to render the scene directly
								// to a file without requiring a
		// window. Necessary for headless video rendering.

		// This cannot be used if using an interactive mode controller.
		public String BACKGROUND_IMAGE;// (str): Path to an image to use for the video background. Will be stretched to
										// fit
		// WINDOW_DIMENSIONS.

	}

	public static class controller {
		public String MODE; // interactive
		public double KEYBOARD_TIMESTEP; // The number of seconds to step forward/backward using left/right arrow keys.
											// Only
		// used in interactive mode.
		public String OUTPUT_VIDEO_PATH;// (str): The full filepath where the output video will be saved. Only used in
										// video_render
		// mode. Currently, only .gif and .mp4 video formats are supported. Transparency
		// is only available for .gif videos.
		public String OUTPUT_VIDEO_CODEC;// (str): The codec to use when encoding the output video. Only used in
											// video_render
		// mode and only if a .mp4 output video file is specified.

	}

	public scene scene = new scene();
	public view view = new view();
	public controller controller = new controller();

	public static MvcConfig loadFromFile(File file) throws Exception {
		InputStream is = new FileInputStream(file);
		MvcConfig f = null;
		// Yaml yaml = new Yaml();
		// Object document = yaml.load(is);
		// if( document instanceof CharacterFig) {
		// f = (CharacterFig)document;
		// } else {
		f = new MvcConfig(is);
		// }
		is.close();
		return f;
	}

	public MvcConfig() {
	}

	/**
	 * 
	 * @param is yarml data
	 */
	public MvcConfig(InputStream is) {
		// InputStream is = getClass().getResourceAsStream( "cm1_pfp.yaml" );
		String input = "";
		try {
			input = u.fileToString(is);
			// input = input.replaceAll("python/tuple", "python.tuple");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		MvcConfig mvc = this;
		Yaml yaml = new Yaml();
		Map<Object, Object> document = yaml.load(input); 
		LinkedHashMap<String, Object> scene = (LinkedHashMap<String, Object>) document.get("scene");

		List<Map> characters = (List<Map>) scene.get("ANIMATED_CHARACTERS");
		for (Map m : characters) {
			ANIMATED_CHARACTER c = new ANIMATED_CHARACTER();
			c.character_cfg = (String) m.get("character_cfg");
			c.motion_cfg = (String) m.get("motion_cfg");
			c.retarget_cfg = (String) m.get("retarget_cfg");
			mvc.scene.ANIMATED_CHARACTERS.add(c);
		}
		if (scene.containsKey("ADD_FLOOR"))
			mvc.scene.ADD_FLOOR = (boolean) scene.get("ADD_FLOOR");
		if (scene.containsKey("ADD_AD_RETARGET_BVH"))
			mvc.scene.ADD_AD_RETARGET_BVH = (boolean) scene.get("ADD_AD_RETARGET_BVH");

		LinkedHashMap<String, Object> view = (LinkedHashMap<String, Object>) document.get("view");

		if (view != null) {
			if (view.containsKey("DRAW_AD_RIG"))
				mvc.view.DRAW_AD_RIG = (boolean) view.get("DRAW_AD_RIG");
			if (view.containsKey("DRAW_AD_TXTR"))
				mvc.view.DRAW_AD_TXTR = (boolean) view.get("DRAW_AD_TXTR");
			if (view.containsKey("DRAW_AD_COLOR"))
				mvc.view.DRAW_AD_COLOR = (boolean) view.get("DRAW_AD_COLOR");
			if (view.containsKey("DRAW_AD_MESH_LINES"))
				mvc.view.DRAW_AD_MESH_LINES = (boolean) view.get("DRAW_AD_MESH_LINES");
			if (view.containsKey("USE_MESA"))
				mvc.view.USE_MESA = (boolean) view.get("USE_MESA");

			if (view.containsKey("CLEAR_COLOR")) {
				List<Double> cc = (List<Double>) view.get("CLEAR_COLOR");
				mvc.view.CLEAR_COLOR = new double[4];
				mvc.view.CLEAR_COLOR[0] = cc.get(0);
				mvc.view.CLEAR_COLOR[1] = cc.get(1);
				mvc.view.CLEAR_COLOR[2] = cc.get(2);
				if (cc.size() > 3)
					mvc.view.CLEAR_COLOR[3] = cc.get(3);
			}
			if (view.containsKey("WINDOW_DIMENSIONS")) {
				List<Integer> cc = (List<Integer>) view.get("WINDOW_DIMENSIONS");
				mvc.view.WINDOW_DIMENSIONS = new int[2];
				mvc.view.WINDOW_DIMENSIONS[0] = cc.get(0);
				mvc.view.WINDOW_DIMENSIONS[1] = cc.get(1);
			}
			if (view.containsKey("CAMERA_POS")) {
				List<Double> cc = (List<Double>) view.get("CAMERA_POS");
				mvc.view.CAMERA_POS = new double[3];
				mvc.view.CAMERA_POS[0] = cc.get(0);
				mvc.view.CAMERA_POS[1] = cc.get(1);
				mvc.view.CAMERA_POS[2] = cc.get(2);
			}
			if (view.containsKey("CAMERA_FWD")) {
				List<Double> cc = (List<Double>) view.get("CAMERA_FWD");
				mvc.view.CAMERA_FWD = new double[3];
				mvc.view.CAMERA_FWD[0] = cc.get(0);
				mvc.view.CAMERA_FWD[1] = cc.get(1);
				mvc.view.CAMERA_FWD[2] = cc.get(2);
			}
		}
		LinkedHashMap<String, Object> controller = (LinkedHashMap<String, Object>) document.get("controller");
		if (controller != null) {
			if (controller.containsKey("MODE"))
				mvc.controller.MODE = (String) controller.get("MODE");
			else
				mvc.controller.MODE = "video_render"; // interactive
			if (controller.containsKey("KEYBOARD_TIMESTEP"))
				mvc.controller.KEYBOARD_TIMESTEP = (Double) controller.get("KEYBOARD_TIMESTEP");
			if (controller.containsKey("OUTPUT_VIDEO_PATH"))
				mvc.controller.OUTPUT_VIDEO_PATH = (String) controller.get("OUTPUT_VIDEO_PATH");
			if (controller.containsKey("OUTPUT_VIDEO_CODEC"))
				mvc.controller.OUTPUT_VIDEO_CODEC = (String) controller.get("OUTPUT_VIDEO_CODEC");

		} 
	}
	public String toYaml() {
		 MvcConfig mvc = this;
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
		 
			Yaml yaml = new Yaml();
			StringWriter writer = new StringWriter();
			yaml.dump(data, writer);
			String code = writer.toString();
			//code = code.replaceAll("python.tuple", "python/tuple");
			return code;
	}
}
