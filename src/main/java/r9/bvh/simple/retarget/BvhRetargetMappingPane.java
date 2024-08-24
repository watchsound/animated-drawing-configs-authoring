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
import java.awt.geom.Point2D.Double;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BoxLayout;
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
import r9.bvh.simpe.utils.NodeHelper;
import r9.bvh.simpe.utils.NodePopupMenuHelper;
import r9.bvh.simpe.utils.PropertyUIHelper;
import r9.bvh.simpe.utils.R9Properties;
import r9.bvh.simpe.utils.StringUtils;
import r9.bvh.simpe.utils.u;
import r9.bvh.simpe.utils.NodePopupMenuHelper.NodeCallback;
import r9.bvh.simpe.utils.PageColors;
import r9.bvh.simple.BVHBasePanel;
import r9.bvh.simple.Settings;
import r9.bvh.simple.model.Node;
import r9.bvh.simple.model.NodeBase;
import r9.bvh.simple.model.Skeleton;
import r9.bvh.simple.retarget.RetargetConfig.BvhGroupMethod;
import r9.bvh.simple.retarget.RetargetConfig.bvh_projection_bodypart_group;
import r9.bvh.simple.retarget.RetargetConfig.char_bvh_root_offset;
import r9.bvh.simple.ui.fbfig.CharacterConfig;
import r9.bvh.simple.ui.fbfig.FbCharacterFigRegister;
import r9.bvh.simple.ui.fbfig.FigEditZone.FigEditCanvas;

public class BvhRetargetMappingPane extends JPanel {

	private static final long serialVersionUID = 1L;
	private SkeletonCanvas skeletonCanvas;
	//private DiscreteSlider timeSlider;
	private JScrollPane skeletonCanvasScrollPane;

	RetargetWorkspace workspace;
	private JLabel statusLabel;
	private JSlider zoomSlider;
	private JLabel zoomLabel; 
	
	//private Retarget retarget;
  

	public BvhRetargetMappingPane(final RetargetWorkspace workspace) {
		this.workspace = workspace;
		skeletonCanvas = new SkeletonCanvas();
 
		JPanel bottom = new JPanel();
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
 
		zoomLabel = new JLabel();
		zoomSlider = new JSlider();
		zoomSlider.addChangeListener(new ChangeListener() { 
			public void stateChanged(ChangeEvent e) {
				int v = zoomSlider.getValue();
				double zoom = 1;
				if( v == 10 ) zoom = 1;
				else if ( v > 10 ) zoom = v - 10;
				else {
					zoom = v / 10.0;
				}
				skeletonCanvas.setScale(zoom);
				zoomLabel.setText(zoom + "");
			}});
		zoomSlider.setMinimum(1);
		zoomSlider.setMaximum(50); 
		zoomSlider.setPreferredSize(new Dimension(200,25)); 
		bottom.add(PropertyUIHelper.createRow("zoom level", zoomLabel, zoomSlider));
		   
		bottom.add(PropertyUIHelper.createTitleRow("" ));
		 
	 
		statusLabel = new JLabel();
		//statusLabel.setText("Ctrl- with mouse drag to move node toward you.");
		statusLabel.setPreferredSize(new Dimension(400, 25));
		bottom.add(statusLabel);

		skeletonCanvasScrollPane = new JScrollPane(skeletonCanvas);
		skeletonCanvasScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		skeletonCanvasScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		setLayout(new BorderLayout());
		add(skeletonCanvasScrollPane, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
	}
	
	 

	public void update(RetargetConfig retarget ) { 
		// if( retarget != null )
		//	 this.retarget = retarget; 
		 skeletonCanvas.update(retarget.skeleton);  
	}
 

	public void highlight(Node data) {
		skeletonCanvas.highlight(data);
	}

	public void highlightByMouseClick(MouseEvent me, List<Node> data) {
		  if( data.isEmpty()) return;
		  workspace.inMappingViewClickBVHNode(data);
	}
 

	
	public class SkeletonCanvas extends BVHBasePanel {

		private static final long serialVersionUID = 1L;
 
		private boolean duringDragging;
		Point2D.Double startP; 
		Point2D.Double currentP;
		

		public SkeletonCanvas() {
			MouseAdapter ma = new MouseAdapter() {
				
				private Node getHitNode(MouseEvent me) {
					for (int i = 0; i < locs.size(); i++) {
						Rectangle loc = locs.get(i);
						if (loc.contains(me.getX(), me.getY())) {
							return node4loc.get(i);
						}
					}
					return null;
				}

				public void mousePressed(MouseEvent me) {
					startP = new Point2D.Double(me.getX(), me.getY());

					Node hit = getHitNode(me); 
					highlight = hit;
					if( highlight == null || highlight.getName().equalsIgnoreCase("Site")) {
						repaint();
						return;
					}
					
					
                    List<Node> nodes = new ArrayList<>();
                    nodes.add(highlight);
					highlightByMouseClick(me, nodes);
					repaint();
				}

				public void mouseReleased(MouseEvent me) {
					if (duringDragging && highlight != null && startP != null) {
						 
					}
					duringDragging = false;
					startP = null;
				}

				public void mouseDragged(MouseEvent me) { 
					duringDragging = true; 
					currentP = new Point2D.Double(me.getX(), me.getY());
					repaint();
				}

				public void mouseClicked(MouseEvent me) {

				}

			 
			};
			this.addMouseListener(ma);
			this.addMouseMotionListener(ma);
		}

		public void update( Skeleton data) { 
			this.setSkeleton(data);   
			highlight = null; 
			repaint();
		}

		private  void paintFigures(Graphics2D g2d, Skeleton skeleton, Node highlight, double tx, double ty,
				double scale, List<Rectangle> locs, List<Node> node4loc) {

			g2d.translate(tx, ty);
			R9Properties props = R9Properties.getSharedProperties();
			int dotSize = props.dotSize();
			for (int n = 0; n < skeleton.getNodes().size(); n++) {
				Node node = skeleton.getNodes().get(n);
				int x1 = (int) (scale * node.getPosition().getX());
				int y1 = (int) (-scale * node.getPosition().getY());
			 	Color c = Color.black;
			 
			 	String m =  workspace.curRetarget == null ? null :
					workspace.curRetarget.getCharNodeFromBvhNodeMapping( node.getName());
			 	if( m != null )
			 		c = workspace.getMappingColor(m, false);
			 	
				g2d.setColor(node == highlight ? Color.RED : c);
				g2d.fillOval((int) (x1 - dotSize/2), (int) (y1 - dotSize/2), dotSize, dotSize);
				if (locs != null) {
					locs.add(new Rectangle(x1 - dotSize/2 + (int) tx, y1 - dotSize/2 + (int) ty, dotSize, dotSize));
					node4loc.add(node);
				}
				 
				g2d.setColor(Color.BLACK);
				g2d.drawString(node.getName(), x1 + dotSize + 10, y1 - 5);
			 
				for (Node child : node.getChildrens()) {
					int x2 = (int) (scale * child.getPosition().getX());
					int y2 = (int) (-scale * child.getPosition().getY());
					g2d.drawLine(x1, y1, x2, y2);
				}
			}
			g2d.translate(-tx, -ty);
		}
	 
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			Skeleton skeleton = this.getSkeleton();
			double scale = getScale();
			if (skeleton == null)
				return;
			Rectangle r = calculateSize(skeleton, scale);
			int w = getWidth();
			int h = getHeight();
			tx = (w - r.width) / 2 - r.x;
			ty = (h - r.height) / 2 - r.y;
			locs.clear();
			node4loc.clear();
			paintFigures(g2d, skeleton, highlight, tx, ty, scale, locs, node4loc);
			if( startP != null && currentP != null) {
				 int minx = (int) Math.min(currentP.getX(), startP.getX());
				 int miny = (int) Math.min(currentP.getY(), startP.getY());
				 int w0 = (int) Math.abs(currentP.getX() - startP.getX());
				 int h0 = (int) Math.abs(currentP.getY() - startP.getY());
				 g.drawRect(minx, miny, w0, h0);
			}
		}

	}

}
