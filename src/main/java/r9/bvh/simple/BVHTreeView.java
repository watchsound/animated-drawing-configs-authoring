package r9.bvh.simple;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
 
import r9.bvh.simpe.utils.ResourceHelper;
import r9.bvh.simple.model.*;
import r9.bvh.simple.model.NodeBase.Type;

/** 
  */
public class BVHTreeView extends JPanel {

	private static final long serialVersionUID = 1L;

	public static interface CALLBACK {
		void onSelection(Node object);
	}

	private static ImageIcon EndNode = ResourceHelper.getToolIcon("d-circle.png");
	private static ImageIcon JointNode = ResourceHelper.getToolIcon("zirkel.png");
	private static ImageIcon RootNode = ResourceHelper.getToolIcon("tree.png");

	CALLBACK callback;

	private Skeleton curPage;

	private DefaultMutableTreeNode allNodeRootNode;

	private JTree allNodeRootTree;

	public void update(Skeleton provider) {
		populateData(provider);
		allNodeRootTree.updateUI();
		allNodeRootTree.validate();
	}

	private void populateData(DefaultMutableTreeNode parent, Node node) {
		DefaultMutableTreeNode selfnode = new DefaultMutableTreeNode(node);
		parent.add(selfnode);
		if (node.getType() == Type.END)
			return;
		for (Node n : node.getChildrens()) {
			populateData(selfnode, n);
		}
	}

	private void populateData(Skeleton provider) {
		this.curPage = provider;

		allNodeRootNode.removeAllChildren();

		allNodeRootNode.setUserObject(provider.getRootNode());

		for (Node n : provider.getRootNode().getChildrens()) {
			populateData(allNodeRootNode, n);
		}

	}

	public BVHTreeView(final CALLBACK callback, boolean useStandalone) {
		this.callback = callback;
		this.setBackground(Color.WHITE);

		allNodeRootNode = new DefaultMutableTreeNode(null);
		allNodeRootTree = new JTree(allNodeRootNode);
		allNodeRootTree.setCellRenderer(new BVHTreeCellRenderer());
		createTree(allNodeRootTree, useStandalone);

		JScrollPane scrollPane = new JScrollPane(allNodeRootTree);

		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);

	}

	private void createTree(final JTree atree, boolean useStandalone) {
		// 设置树显示根节点句柄
		atree.setShowsRootHandles(true);
		// tree.setRootVisible(false);
		// 设置树节点可编辑
		atree.setEditable(false);
		// 设置节点选中监听�?
		atree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) atree.getLastSelectedPathComponent();
				if (selectNode == null)
					return;
				if (selectNode.getUserObject() instanceof Node) {
					Node db = (Node) selectNode.getUserObject();
					callback.onSelection(db);
				}
			}
		});
		if (!useStandalone) {
			atree.setMinimumSize(new Dimension(200, 80));
		}
	}

	static class BVHTreeCellRenderer implements TreeCellRenderer {
		private final JLabel label;

		BVHTreeCellRenderer() {
			label = new JLabel();
		}

		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {
			Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
			if (userObject instanceof Node) {
				Node node = (Node) userObject;
				if (node.getType() == Type.ROOT)
					label.setIcon(RootNode);
				else if (node.getType() == Type.END)
					label.setIcon(EndNode);
				else
					label.setIcon(JointNode);

				label.setText(node.toString());
			} else {
				label.setIcon(null);
				label.setText(value.toString());
			}
			return label;
		}
	}

	public void highlightByMouseClick(Node data) {
		Object root = allNodeRootTree.getModel().getRoot();
		TreePath treePath = new TreePath(root);
		List<TreePath> result = new ArrayList<>();
		findInPath(allNodeRootTree, treePath, data, result);
		if (result.size() > 0) {
			allNodeRootTree.setSelectionPath(result.get(0));
			allNodeRootTree.scrollPathToVisible(result.get(0));
			callback.onSelection(data);
		}
	}

	private void findInPath(JTree tree, TreePath treePath, Node target, List<TreePath> result) {
		Object object = treePath.getLastPathComponent();
		if (object == null) {
			return;
		}

		if (object instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) object;
			if (node.getUserObject() == target) {
				result.add(treePath);
			}
		}
		{
			TreeModel model = tree.getModel();
			int n = model.getChildCount(object);
			for (int i = 0; i < n; i++) {
				Object child = model.getChild(object, i);
				TreePath path = treePath.pathByAddingChild(child);
				findInPath(tree, path, target, result);
			}
		}
	}

}
