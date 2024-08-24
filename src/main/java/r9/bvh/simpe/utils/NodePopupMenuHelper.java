package r9.bvh.simpe.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import r9.bvh.simple.model.NodeBase;

public class NodePopupMenuHelper {
    public static interface NodeCallback{
    	void nodeSelected(NodeBase node);
    }
	public static JPopupMenu showMenu(NodeBase<NodeBase> node, NodeCallback callback) {
		JPopupMenu menu = new JPopupMenu();
		final JMenuItem item = new JMenuItem(node.getName());
		menu.add(item);
		item.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				 callback.nodeSelected(node);
			}});
		if(!node.getChildrens().isEmpty()) {
			JMenu submenu = new JMenu();
			menu.add(submenu);
			for(NodeBase nn : node.getChildrens()) {
				setupMenu(submenu, nn, callback);
			}
		}
		return menu;
	}
	public static void setupMenu(JMenu menu, NodeBase<NodeBase> node, NodeCallback callback) {
	 	final JMenuItem item = new JMenuItem(node.getName());
		menu.add(item);
		item.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				 callback.nodeSelected(node);
			}});
		if(!node.getChildrens().isEmpty()) {
			JMenu submenu = new JMenu();
			menu.add(submenu);
			for(NodeBase nn : node.getChildrens()) {
				setupMenu(submenu, nn, callback);
			}
		}
	}
}
