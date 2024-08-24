package r9.bvh.simple.retarget;

import java.io.File;
import java.io.FileInputStream;
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

import python.tuple;
import r9.bvh.simpe.utils.u;
import r9.bvh.simple.model.NodeBase;
import r9.bvh.simple.model.Skeleton;
import r9.bvh.simple.retarget.RetargetConfig.bvh_projection_bodypart_group;
import r9.bvh.simple.retarget.RetargetConfig.char_bodypart_group;
import r9.bvh.simple.ui.fbfig.CharacterConfig;

public class RetargetConfig {
	public static enum BvhGroupMethod {
		pca, frontal, saggital
	}

	public static List<String> get_bvh_group_names(List<bvh_projection_bodypart_group> groups) {
		List<String> names = new ArrayList<>();
		for (bvh_projection_bodypart_group g : groups)
			names.add(g.getName());
		return names;
	}

	public static bvh_projection_bodypart_group get_bvh_group_by_bvhnode(List<bvh_projection_bodypart_group> groups,
			String bvhnode) {
		if (bvhnode == null)
			return null;
		for (bvh_projection_bodypart_group g : groups)
			if (g.getBvh_joint_names().contains(bvhnode))
				return g;
		return null;
	}

	public static bvh_projection_bodypart_group get_bvh_group_by_group_name(List<bvh_projection_bodypart_group> groups,
			String groupname) {
		if (groupname == null)
			return null;
		for (bvh_projection_bodypart_group g : groups)
			if (groupname.equals(g.getName()))
				return g;
		return null;
	}

	public static char_bodypart_group get_char_group_by_bvh_name(List<char_bodypart_group> groups, String bvhgroup) {
		for (char_bodypart_group g : groups) {
			if (g.getBvh_depth_drivers().contains(bvhgroup))
				return g;
		}
		return null;
	}

	public static char_bodypart_group get_char_group_by_char_name(List<char_bodypart_group> groups, String charname) {
		for (char_bodypart_group g : groups) {
			if (g.getChar_joints().contains(charname))
				return g;
		}
		return null;
	}

	public static List<String> get_char_group_bvh_drivers(List<char_bodypart_group> groups) {
		List<String> drivers = new ArrayList<>();
		for (char_bodypart_group g : groups) {
			drivers.addAll(g.bvh_depth_drivers);
		}
		return drivers;
	}

	public static class bvh_projection_bodypart_group {
		List<String> bvh_joint_names = new ArrayList<>();
		BvhGroupMethod method;
		String name;

		public List<String> getBvh_joint_names() {
			return bvh_joint_names;
		}

		public void setBvh_joint_names(List<String> bvh_joint_names) {
			this.bvh_joint_names = bvh_joint_names;
		}

		public BvhGroupMethod getMethod() {
			return method;
		}

		public void setMethod(BvhGroupMethod method) {
			this.method = method;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String toString() {
			return this.name;
		}
	}

	public static class char_bodypart_group {
		List<String> char_joints = new ArrayList<>();
		List<String> bvh_depth_drivers = new ArrayList<>();

		public List<String> getChar_joints() {
			return char_joints;
		}

		public void setChar_joints(List<String> char_joints) {
			this.char_joints = char_joints;
		}

		public List<String> getBvh_depth_drivers() {
			return bvh_depth_drivers;
		}

		public void setBvh_depth_drivers(List<String> bvh_depth_drivers) {
			this.bvh_depth_drivers = bvh_depth_drivers;
		}
	}

	public static class char_bvh_root_offset {
		public String bvh_projection_bodypart_group_for_offset;
		public List<List<String>> bvh_joints = new ArrayList<>();
		public List<List<String>> char_joints = new ArrayList<>();

		public void removeBvh(String node) {
			for (List<String> s : bvh_joints) {
				if (s.contains(node)) {
					s.remove(node);
					// break;
				}
			}
		}

		public void removeChar(String node) {
			for (List<String> s : char_joints) {
				if (s.contains(node)) {
					s.remove(node);
					// break;
				}
			}
		}

		public int getIndexOfListInJoins(String node, boolean isbvh) {
			if (isbvh) {
				for (int i = 0; i < bvh_joints.size(); i++) {
					if (bvh_joints.get(i).contains(node))
						return i;
				}
			} else {
				for (int i = 0; i < char_joints.size(); i++) {
					if (char_joints.get(i).contains(node))
						return i;
				}
			}
			return -1;
		}

		public String getBvh_projection_bodypart_group_for_offset() {
			return bvh_projection_bodypart_group_for_offset;
		}

		public void setBvh_projection_bodypart_group_for_offset(String bvh_projection_bodypart_group_for_offset) {
			this.bvh_projection_bodypart_group_for_offset = bvh_projection_bodypart_group_for_offset;
		}

		public List<List<String>> getBvh_joints() {
			return bvh_joints;
		}

		public void setBvh_joints(List<List<String>> bvh_joints) {
			this.bvh_joints = bvh_joints;
		}

		public List<List<String>> getChar_joints() {
			return char_joints;
		}

		public void setChar_joints(List<List<String>> char_joints) {
			this.char_joints = char_joints;
		}

	}

	public double[] char_starting_location = new double[3];
	public List<bvh_projection_bodypart_group> bvh_projection_bodypart_groups = new ArrayList<>();
	public List<char_bodypart_group> char_bodypart_groups = new ArrayList<>();

	public char_bvh_root_offset char_bvh_root_offset = new char_bvh_root_offset();
	public Map<String, tuple> char_joint_bvh_joints_mapping = new HashMap<>();
	public List<List<String>> char_runtime_checks = new ArrayList<>();

	public transient Skeleton skeleton;
	public transient CharacterConfig cfig;
	public String char_file;
	public String name;
	public String bvh_file;

	public static RetargetConfig loadFromFile(File file) throws Exception {
		InputStream is = new FileInputStream(file);
		RetargetConfig f = null;
		// Yaml yaml = new Yaml();
		// Object document = yaml.load(is);
		// if( document instanceof CharacterFig) {
		// f = (CharacterFig)document;
		// } else {
		f = new RetargetConfig(is);
		// }
		is.close();
		return f;
	}

	public static RetargetConfig getDefaultRetarget() {
		InputStream is = CharacterConfig.class.getResourceAsStream("cm1_pfp.yaml");
		RetargetConfig f = new RetargetConfig(is);
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return f;
	}

	public RetargetConfig() {
	}

	/**
	 * 
	 * @param is yarml data
	 */
	public RetargetConfig(InputStream is) {
		// InputStream is = getClass().getResourceAsStream( "cm1_pfp.yaml" );
		String input = "";
		try {
			input = u.fileToString(is);
			input = input.replaceAll("python/tuple", "python.tuple");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Yaml yaml = new Yaml();

		Map<Object, Object> document = yaml.load(input);
		bvh_file = (String) document.get("bvh_file");
		char_file = (String) document.get("char_file");
		name = (String) document.get("name");
		List<Double> locs = (List<Double>) document.get("char_starting_location");
		// double[] char_starting_location = new double[3];
		char_starting_location[0] = locs.get(0);
		char_starting_location[1] = locs.get(1);
		char_starting_location[2] = locs.get(2);

		ArrayList<LinkedHashMap<String, Object>> bgroup = (ArrayList<LinkedHashMap<String, Object>>) document
				.get("bvh_projection_bodypart_groups");

		for (LinkedHashMap<String, Object> eo : bgroup) {
			bvh_projection_bodypart_group bvh_group = new bvh_projection_bodypart_group();
			bvh_group.setName((String) eo.get("name"));
			String method = (String) eo.get("method");
			RetargetConfig.BvhGroupMethod m = RetargetConfig.BvhGroupMethod.valueOf(method);
			bvh_group.setMethod(m);
			List<String> names = (List<String>) eo.get("bvh_joint_names");
			bvh_group.getBvh_joint_names().addAll(names);
			getBvh_projection_bodypart_groups().add(bvh_group);
		}

		ArrayList<LinkedHashMap<String, Object>> cgroup = (ArrayList<LinkedHashMap<String, Object>>) document
				.get("char_bodypart_groups");

		for (LinkedHashMap<String, Object> eo : cgroup) {
			char_bodypart_group char_group = new char_bodypart_group();
			List<String> drivers = (List<String>) eo.get("bvh_depth_drivers");

			char_group.getBvh_depth_drivers().addAll(drivers);

			List<String> char_joints = (List<String>) eo.get("char_joints");
			char_group.getChar_joints().addAll(char_joints);
			getChar_bodypart_groups().add(char_group);
		}

		LinkedHashMap<String, Object> cboffsets = (LinkedHashMap<String, Object>) document.get("char_bvh_root_offset");

		String pname = (String) cboffsets.get("bvh_projection_bodypart_group_for_offset");
		getChar_bvh_root_offset().setBvh_projection_bodypart_group_for_offset(pname);
		List<List<String>> bvh_joints = (List<List<String>>) cboffsets.get("bvh_joints");
		getChar_bvh_root_offset().setBvh_joints(bvh_joints);
		List<List<String>> char_joints = (List<List<String>>) cboffsets.get("char_joints");
		getChar_bvh_root_offset().setChar_joints(char_joints);

		LinkedHashMap<String, tuple> mappings = (LinkedHashMap<String, tuple>) document
				.get("char_joint_bvh_joints_mapping");
		setChar_joint_bvh_joints_mapping(mappings);

		List<List<String>> checks = (List<List<String>>) document.get("char_runtime_checks");
		setChar_runtime_checks(checks);
	}

	public String toYaml() {
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		data.put("bvh_file", this.bvh_file);
		data.put("char_file", this.char_file);
		data.put("name", this.name);
		List<Double> locs = new ArrayList<>();
		locs.add(char_starting_location[0]);
		locs.add(char_starting_location[1]);
		locs.add(char_starting_location[2]);
		data.put("char_starting_location", locs);

		ArrayList<LinkedHashMap<String, Object>> bgroup = new ArrayList<>();

		for (bvh_projection_bodypart_group eo : bvh_projection_bodypart_groups) {
			LinkedHashMap<String, Object> vs = new LinkedHashMap<>();
			vs.put("name", eo.getName());
			vs.put("method", eo.getMethod().name());
			vs.put("bvh_joint_names", eo.getBvh_joint_names());
			bgroup.add(vs);
		}
		data.put("bvh_projection_bodypart_groups", bgroup);

		ArrayList<LinkedHashMap<String, Object>> cgroup = new ArrayList<>();

		for (char_bodypart_group eo : char_bodypart_groups) {
			LinkedHashMap<String, Object> vs = new LinkedHashMap<>();
			vs.put("bvh_depth_drivers", eo.getBvh_depth_drivers());
			vs.put("char_joints", eo.getChar_joints());
			cgroup.add(vs);
		}
		data.put("char_bodypart_groups", cgroup);

		char_bvh_root_offset offset = getChar_bvh_root_offset();
		LinkedHashMap<String, Object> cboffsets = new LinkedHashMap<>();
		cboffsets.put("bvh_projection_bodypart_group_for_offset", offset.getBvh_projection_bodypart_group_for_offset());
		cboffsets.put("bvh_joints", offset.getBvh_joints());
		cboffsets.put("char_joints", offset.getChar_joints());
		data.put("char_bvh_root_offset", cboffsets);

		LinkedHashMap<String, tuple> mappings = (LinkedHashMap<String, tuple>) this.getChar_joint_bvh_joints_mapping();
		data.put("char_joint_bvh_joints_mapping", mappings);

		List<List<String>> checks = this.getChar_runtime_checks();
		data.put("char_runtime_checks", checks);

		Yaml yaml = new Yaml();
		StringWriter writer = new StringWriter();
		yaml.dump(data, writer);
		String code = writer.toString();
		code = code.replaceAll("python.tuple", "python/tuple");
		return code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double[] getChar_starting_location() {
		return char_starting_location;
	}

	public void setChar_starting_location(double[] char_starting_location) {
		this.char_starting_location = char_starting_location;
	}

	public List<String> get_char_group_bvh_drivers() {
		return get_char_group_bvh_drivers(this.char_bodypart_groups);
	}

	public List<String> get_bvh_group_names() {
		return get_bvh_group_names(this.bvh_projection_bodypart_groups);
	}

	public bvh_projection_bodypart_group get_bvh_group_by_bvhnode(String bvhnode) {
		return get_bvh_group_by_bvhnode(this.bvh_projection_bodypart_groups, bvhnode);
	}

	public bvh_projection_bodypart_group get_bvh_group_by_group_name(String groupname) {
		return get_bvh_group_by_group_name(this.bvh_projection_bodypart_groups, groupname);
	}

	public char_bodypart_group get_char_group_by_bvh_name(String bvhgroup) {
		return get_char_group_by_bvh_name(this.char_bodypart_groups, bvhgroup);
	}

	public char_bodypart_group get_char_group_by_char_name(String charname) {
		return get_char_group_by_char_name(this.char_bodypart_groups, charname);
	}

	public List<bvh_projection_bodypart_group> getBvh_projection_bodypart_groups() {
		return bvh_projection_bodypart_groups;
	}

	public void setBvh_projection_bodypart_groups(List<bvh_projection_bodypart_group> bvh_projection_bodypart_groups) {
		this.bvh_projection_bodypart_groups = bvh_projection_bodypart_groups;
	}

	public List<char_bodypart_group> getChar_bodypart_groups() {
		return char_bodypart_groups;
	}

	public void setChar_bodypart_groups(List<char_bodypart_group> char_bodypart_groups) {
		this.char_bodypart_groups = char_bodypart_groups;
	}

	public char_bvh_root_offset getChar_bvh_root_offset() {
		return char_bvh_root_offset;
	}

	public void setChar_bvh_root_offset(char_bvh_root_offset char_bvh_root_offset) {
		this.char_bvh_root_offset = char_bvh_root_offset;
	}

	public Map<String, tuple> getChar_joint_bvh_joints_mapping() {
		return char_joint_bvh_joints_mapping;
	}

	public void setChar_joint_bvh_joints_mapping(Map<String, tuple> char_joint_bvh_joints_mapping) {
		this.char_joint_bvh_joints_mapping = char_joint_bvh_joints_mapping;
	}

	public tuple getBvhNodesFromCharNodeMapping(String node) {
		return this.char_joint_bvh_joints_mapping.get(node);
	}

	public String getCharNodeFromBvhNodeMapping(String node) {
		for (Entry<String, tuple> e : this.char_joint_bvh_joints_mapping.entrySet()) {
			if (e.getValue().getValues().contains(node))
				return e.getKey();
		}
		return null;
	}

	public tuple getBvhNodesFromBvhNodeMapping(String node) {
		for (Entry<String, tuple> e : this.char_joint_bvh_joints_mapping.entrySet()) {
			if (e.getValue().getValues().contains(node))
				return e.getValue();
		}
		return null;
	}

	public List<List<String>> getChar_runtime_checks() {
		return char_runtime_checks;
	}

	public void setChar_runtime_checks(List<List<String>> char_runtime_checks) {
		this.char_runtime_checks = char_runtime_checks;
	}

	public String getBvh_file() {
		return bvh_file;
	}

	public void setBvh_file(String bvh_file) {
		this.bvh_file = bvh_file;
	}

	public String getChar_file() {
		return char_file;
	}

	public Skeleton getSkeleton() {
		return skeleton;
	}

	public void setSkeleton(Skeleton skeleton) {
		this.skeleton = skeleton;
	}

	public void setChar_file(String char_file) {
		this.char_file = char_file;
	}

	public CharacterConfig getCfig() {
		return cfig;
	}

	public void setCfig(CharacterConfig cfig) {
		this.cfig = cfig;
	}

	public String toString() {
		return this.name;
	}
}
