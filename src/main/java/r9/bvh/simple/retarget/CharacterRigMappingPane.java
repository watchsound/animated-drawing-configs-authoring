package r9.bvh.simple.retarget;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileFilter;

import python.tuple;
import r9.bvh.simpe.utils.PageColors;
import r9.bvh.simpe.utils.PropertyUIHelper;
import r9.bvh.simpe.utils.R9Properties;
import r9.bvh.simpe.utils.StringUtils;
import r9.bvh.simple.Settings;
import r9.bvh.simple.model.Node;
import r9.bvh.simple.model.NodeBase;
import r9.bvh.simple.model.Skeleton;
import r9.bvh.simple.retarget.RetargetConfig.BvhGroupMethod;
import r9.bvh.simple.retarget.RetargetConfig.bvh_projection_bodypart_group;
import r9.bvh.simple.retarget.RetargetConfig.char_bodypart_group;
import r9.bvh.simple.retarget.RetargetConfig.char_bvh_root_offset;
import r9.bvh.simple.ui.fbfig.CharacterConfig;
import r9.bvh.simple.ui.fbfig.FigBasePanel;

public class CharacterRigMappingPane extends JPanel {

	private static final long serialVersionUID = 1L;
	private FigEditCanvas skeletonCanvas;
	// private DiscreteSlider timeSlider;
	private JScrollPane figEditCanvasScrollPane;

	RetargetWorkspace workspace;
	private JLabel statusLabel;
	private JSlider zoomSlider;
	private JLabel zoomLabel; 

	//private Retarget retarget;
 
	
	
	public CharacterRigMappingPane(final RetargetWorkspace workspace) {
		this.workspace = workspace;
		skeletonCanvas = new FigEditCanvas();
		JPanel bottom = new JPanel();
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
 
		zoomLabel = new JLabel();
		zoomSlider = new JSlider();
		zoomSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int v = zoomSlider.getValue();
				double zoom = 1;
				if (v == 10)
					zoom = 1;
				else if (v > 10)
					zoom = v - 10;
				else {
					zoom = v / 10.0;
				}
				skeletonCanvas.setScale(zoom);
				zoomLabel.setText(zoom + "");
			}
		});
		zoomSlider.setMinimum(1);
		zoomSlider.setMaximum(50);
		zoomSlider.setValue(10); // zoom level is one
		zoomSlider.setPreferredSize(new Dimension(200, 25));
		bottom.add(PropertyUIHelper.createRow("zoom level", zoomLabel, zoomSlider));

		 
		statusLabel = new JLabel();
		 statusLabel.setText("选取一个节点后打开弹出菜单");
		statusLabel.setPreferredSize(new Dimension(400, 25));
		bottom.add(statusLabel);

		figEditCanvasScrollPane = new JScrollPane(skeletonCanvas);
		figEditCanvasScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		figEditCanvasScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		setLayout(new BorderLayout());
		add(figEditCanvasScrollPane, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
	}
	
 
	public void update(CharacterConfig data ) { 
		skeletonCanvas.update(data); 
	}
 
	public void highlight(NodeBase<NodeBase> data) {
		skeletonCanvas.highlight(data); 
	}

	public void highlightByMouseClick(MouseEvent me, NodeBase<NodeBase> data) {
		 
		workspace.mappingFromCharMouseClick(data);
 
	}

	

	public class FigEditCanvas extends FigBasePanel {

		private static final long serialVersionUID = 1L;
 
 
		private boolean duringDragging;

		public FigEditCanvas() {
			MouseAdapter ma = new MouseAdapter() {
		    Point2D.Double prevP;

				private NodeBase getHitNode(MouseEvent me) {
					CharacterConfig skeleton = getSkeleton();
					double scale =  getScale();
					for (NodeBase<NodeBase> node : skeleton.getNodes()) {
						double x = scale * node.getPosition().getX();
						double y = scale *  node.getPosition().getY();
						if ((me.getX() - x) * (me.getX() - x) + (y - me.getY()) * (y - me.getY()) < 81) {
							return node;
						}
					}
					return null;
				}

				public void mousePressed(MouseEvent me) {
					if (me.isPopupTrigger()) {
						handlePopup(me,  highlight );
						return;
					}
					prevP = new Point2D.Double(me.getX(), me.getY());
					NodeBase hit = getHitNode(me);
					if (hit == highlight)
						return;
					highlight = hit;

					highlightByMouseClick(me, highlight);
					repaint();
				}

			 

				private void handlePopup(MouseEvent me,  NodeBase  data) {
					if (data == null)
						return;
					final RetargetConfig retarget = workspace.curRetarget;
				   final tuple mappingobj =  retarget.getBvhNodesFromCharNodeMapping(data.getName());
					final JPopupMenu menu = new JPopupMenu();
					menu.add(PropertyUIHelper.createTitleRow("创建Mapping")); 
					menu.add(new JLabel("选择一个或者多个BVH节点"));
					for(final Node n : retarget.getSkeleton().getNodes()) {
					    final JCheckBoxMenuItem item = new JCheckBoxMenuItem( n.getName() ); 
					    item.setSelected( mappingobj != null && mappingobj.getValues().contains(n.getName()));
						item.addActionListener(new ActionListener() { 
							public void actionPerformed(ActionEvent e) {
								 if( item.isSelected() ) {
									 if( mappingobj == null ) {
								    	 tuple t2 = new tuple(n.getName() );
								    	 retarget.char_joint_bvh_joints_mapping.put(data.getName(), t2);
								     } else {
								    	 mappingobj.getValues().add(n.getName());
								     }
									 workspace.getMappingColor(data.getName(), true);
								 } else {
									 if( mappingobj != null)
										 mappingobj.getValues().remove(n.getName());
								 }
							    
							}});
					    menu.add(item);
					}   
					menu.show(FigEditCanvas.this, me.getX(), me.getY());
				}

				public void mouseReleased(MouseEvent me) {
					if (me.isPopupTrigger()) {  
						handlePopup(me,  highlight );
						return;
					}
					if (duringDragging && highlight != null && prevP != null) { 
					}
					duringDragging = false;
					prevP = null;
				}

				public void mouseDragged(MouseEvent me) { 
					duringDragging = true; 
				}

				public void mouseClicked(MouseEvent me) {
					 
				}

				 
			};
			this.addMouseListener(ma);
			this.addMouseMotionListener(ma);
		}

	

		public void update(CharacterConfig data) {
			this.setSkeleton(data);  
			highlight = null;
			mapFigSpaceToCanvas();
			repaint();
		}
  
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			 
			int w = getWidth();
			int h = getHeight();
			CharacterConfig skeleton = getSkeleton();
			double scale = getScale();
			if (skeleton == null)
				return;
			R9Properties props = R9Properties.getSharedProperties();
			int dotSize = props.dotSize();
			for (int n = 0; n < skeleton.getNodes().size(); n++) {
				NodeBase<NodeBase> node = skeleton.getNodes().get(n);
				int x1 = (int) (scale * node.getPosition().getX());
				int y1 = (int) (scale * node.getPosition().getY());

				tuple m =  workspace.curRetarget == null ? null :
					workspace.curRetarget.getBvhNodesFromCharNodeMapping(node.getName());
				Color c = Color.black; 
				if( m != null ) {
					c =  workspace.getMappingColor(node.getName(), true);
				}
				g2d.setColor(node == highlight ? Color.RED : c); 
				g2d.fillOval((int) (x1 - dotSize/2), (int) (y1 - dotSize/2), dotSize, dotSize);

				g2d.drawString(node.getName(), x1 + dotSize + 10, y1 - 5);
 
				g2d.setColor(Color.BLACK);
				for (NodeBase child : node.getChildrens()) {
					int x2 = (int) (scale * child.getPosition().getX());
					int y2 = (int) (scale * child.getPosition().getY());
					g2d.drawLine(x1, y1, x2, y2);
				}
			}
		}

	}

} 
