package r9.bvh.simpe.utils;

import java.util.List;

import r9.bvh.simple.model.NodeBase;

public class NodeHelper {

	public static NodeBase<NodeBase> find(List<NodeBase> nodes, String name){
		for(NodeBase<NodeBase> n : nodes) {
			if( n.getName().equalsIgnoreCase(name) )
                return n;
		}
		return null;
	}
	public static NodeBase<NodeBase> find(NodeBase<NodeBase> node, String name){
		if( node.getName().equalsIgnoreCase(name))
			return node;
		for(NodeBase<NodeBase> n : node.getChildrens()) {
			NodeBase<NodeBase> nn = find(n, name);
			if( nn != null ) 
				return nn;
		}
		return null;
	}
}
