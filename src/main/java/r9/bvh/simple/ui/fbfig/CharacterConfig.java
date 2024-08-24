package r9.bvh.simple.ui.fbfig;

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

import r9.bvh.simpe.utils.StringUtils;
import r9.bvh.simple.model.NodeBase;

public class CharacterConfig {
	@SuppressWarnings("rawtypes")
	private NodeBase rootNode;
	private final List<NodeBase> nodes = new ArrayList<NodeBase>();
	private int height;
	private int width;
	private String name;

	public static CharacterConfig loadFromFile(File file) throws Exception {
		InputStream is = new FileInputStream(file);
		CharacterConfig f = null;
		//Yaml yaml = new Yaml();  
		//Object document = yaml.load(is);
		//if( document instanceof CharacterFig) {
		//	f = (CharacterFig)document;
	//	} else {
			f = new CharacterConfig(is);
		//} 
		is.close();
		return f;
	}

	public static CharacterConfig getDefaultFig() {
		InputStream is = CharacterConfig.class.getResourceAsStream("char_cfg.yaml");
		CharacterConfig f = new CharacterConfig(is);
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return f;
	}
	 
	public CharacterConfig() {}

	/**
	 * 
	 * @param is yarml data
	 */
	public CharacterConfig(InputStream is) {
		Yaml yaml = new Yaml();  
		Map<Object, Object> document = yaml.load(is);
		int height = (Integer) document.get("height");
		int width = (Integer) document.get("width");
		this.name = (String)document.get("name");
		List<Map> locs = (List<Map>) document.get("skeleton");
		this.setWidth(width);
		this.setHeight(height);
		Map<NodeBase, String> n2p = new HashMap<>();
		for (Map loc : locs) {
			String name = (String) loc.get("name");
			String parent = (String) loc.get("parent");
			List<Integer> ls = (List<Integer>) loc.get("loc");
			NodeBase nb = new NodeBase();
			nb.setName(name);
			nb.getPosition().setX(ls.get(0));
			nb.getPosition().setY(ls.get(1));
			this.getNodes().add(nb);
			n2p.put(nb, parent);
			if (parent == null || parent.equals("null"))
				this.setRootNode(nb);
		}
		for (Entry<NodeBase, String> record : n2p.entrySet()) {
			String pname = record.getValue();
			if (pname == null)
				continue; // root
			NodeBase p = null;
			for (NodeBase k : n2p.keySet()) {
				if (k.getName().equals(pname)) {
					p = k;
					break;
				}
			}
			if( p == null )
				continue;
			record.getKey().setParent(p);
			p.getChildrens().add(record.getKey());
		}
		//rootNode.calculateDistanceToChildren(1);
	}

	public String toYaml() {
		  Map<String, Object> data = new LinkedHashMap<String, Object>();
	      data.put("name", this.name);
	      data.put("width", this.width);
	      data.put("height", this.height);
	      List<Map<String,Object>> locs = new ArrayList<>();
	      data.put("skeleton", locs);
	      for(NodeBase<NodeBase> node : this.nodes) {
	    	  Map<String, Object> data0 = new LinkedHashMap<String, Object>();
	    	  data0.put("name", node.getName());
	    	  data0.put("parent", node.getParent() == null ? "null" : node.getParent().getName());
	    	//  Map<String, Object> loc = new LinkedHashMap<String, Object>();
	    	  //loc.put("x", node.getPosition().getX());
	    	 // loc.put("y", node.getPosition().getY());
	    	  List<Integer> loc = new ArrayList<>();
	    	  loc.add((int) node.getPosition().getX());
	    	  loc.add((int) node.getPosition().getY());
	    	  data0.put("loc", loc);
	    	  locs.add(data0);
	      }
	      Yaml yaml = new Yaml();
	      StringWriter writer = new StringWriter();
	      yaml.dump(data, writer);
	      return writer.toString();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public NodeBase getRootNode() {
		return rootNode;
	}

	public void setRootNode(NodeBase rootNode) {
		this.rootNode = rootNode;
	}

	public List<NodeBase> getNodes() {
		return nodes;
	}

	public String toString() {
		if( StringUtils.isEmpty(this.name))
			return this.rootNode.toString();
		return this.name;
	}
}