package r9.bvh.simple.retarget;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import r9.bvh.simpe.utils.PageColors;
import r9.bvh.simpe.utils.PropertyUIHelper;
import r9.bvh.simpe.utils.R9Properties;
import r9.bvh.simpe.utils.StringUtils;
import r9.bvh.simple.model.NodeBase;
import r9.bvh.simple.retarget.RetargetConfig.char_bodypart_group;
import r9.bvh.simple.retarget.RetargetConfig.char_bvh_root_offset;
import r9.bvh.simple.ui.fbfig.CharacterConfig;
import r9.bvh.simple.ui.fbfig.FigBasePanel;

public class CharacterRigOffsetPane extends JPanel {

	private static final long serialVersionUID = 1L;
	private FigEditCanvas skeletonCanvas;
	// private DiscreteSlider timeSlider;
	private JScrollPane figEditCanvasScrollPane;

	RetargetWorkspace workspace;
	private JLabel statusLabel;
	private JSlider zoomSlider;
	private JLabel zoomLabel; 

//	private Retarget retarget;

	private char_bodypart_group curGroup; 
	
	
	public CharacterRigOffsetPane(final RetargetWorkspace workspace) {
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
	    statusLabel.setText("用鼠标推拽选取多个节点");
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
		workspace.highlightByMouseClick(data);
 
	}

	

	public class FigEditCanvas extends FigBasePanel {

		private static final long serialVersionUID = 1L;
 
 
		private boolean duringDragging;
		Point2D.Double startP;
		Point2D.Double currentP;
		
		public FigEditCanvas() {
			MouseAdapter ma = new MouseAdapter() {
		    

				private NodeBase getHitNode(MouseEvent me) {
					CharacterConfig skeleton = getSkeleton(); 
					double scale = getScale();
					for (NodeBase<NodeBase> node : skeleton.getNodes()) {
						double x = scale * node.getPosition().getX();
						double y = scale * node.getPosition().getY();
						if ((me.getX() - x) * (me.getX() - x) + (y - me.getY()) * (y - me.getY()) < 81) {
							return node;
						}
					}
					return null;
				}

				public void mousePressed(MouseEvent me) {
					if (me.isPopupTrigger()) {
						handlePopup(me, Arrays.asList(highlight));
						return;
					}
					startP = new Point2D.Double(me.getX(), me.getY());
					NodeBase hit = getHitNode(me);
					if (hit == highlight)
						return;
					highlight = hit;

					highlightByMouseClick(me, highlight);
					repaint();
				}

			 

				private void handlePopup(MouseEvent me, List<NodeBase> data) {
					if (data.size() == 0)
						return;
					RetargetConfig retarget = workspace.curRetarget;
				    char_bvh_root_offset offsetobj =  retarget.char_bvh_root_offset;
					final JPopupMenu menu = new JPopupMenu();
					JMenuItem item = new JMenuItem("创建Group");
					final List<String> ns = new ArrayList<>();
					 for(NodeBase n : data) {
						 ns.add(n.getName());
					 }
					item.addActionListener(new ActionListener() { 
						public void actionPerformed(ActionEvent e) {
							 for(NodeBase n : data) {
								 offsetobj.removeChar(n.getName());
							 } 
							 offsetobj.char_joints.add(ns);
						}});
					menu.add(item); 
					JMenu submenu = new JMenu("使用已经定义的Group：");
					menu.add(submenu);
					for(final List<String> gname : offsetobj.char_joints) {
					    item = new JMenuItem(StringUtils.toString(gname)); 
						item.addActionListener(new ActionListener() { 
							public void actionPerformed(ActionEvent e) {
								for(NodeBase n : data) {
									 offsetobj.removeChar(n.getName());
								 } 
								gname.addAll(ns); 
							}});
						submenu.add(item);
					}  
					
					menu.show(FigEditCanvas.this, me.getX(), me.getY());
				}

				public void mouseReleased(MouseEvent me) {
					CharacterConfig skeleton = getSkeleton(); 
					double scale = getScale();
					if( skeleton == null ) return;
					if (me.isPopupTrigger()) {  
						duringDragging = false;
						startP = null;
						handlePopup(me, Arrays.asList(highlight));
						return;
					}
					if (duringDragging   && startP != null) {
						 int minx = (int) Math.min(me.getX(), startP.getX());
						 int miny = (int) Math.min(me.getY(), startP.getY());
						 int w = (int) Math.abs(me.getX() - startP.getX());
						 int h = (int) Math.abs(me.getY() - startP.getY());
						 Rectangle r = new Rectangle(minx, miny, w, h);
						 List<NodeBase> nodes = new ArrayList<>();
						 for (NodeBase<NodeBase> node : skeleton.getNodes()) {
								double x = scale * node.getPosition().getX();
								double y = scale * node.getPosition().getY();
								if (r.contains(x,y)) {
									nodes.add(node);
								}
							}
						 handlePopup(me, nodes);
					}
					duringDragging = false;
					startP = null;
				}

				public void mouseDragged(MouseEvent me) { 
					duringDragging = true; 
					currentP = new Point2D.Double(me.getY(), me.getY());
					repaint();
				}

				public void mouseClicked(MouseEvent me) {
					NodeBase hit = getHitNode(me);
					if (hit == highlight)
						return;
					highlight = hit;
					if( highlight == null || highlight.getName().equalsIgnoreCase("Site")) {
						repaint();
						return;
					}
					 
					highlightByMouseClick(me, highlight);
					repaint();
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
			CharacterConfig skeleton = getSkeleton();
			double scale = getScale();
			int w = getWidth();
			int h = getHeight();
			 
			if (skeleton == null)
				return;
			RetargetConfig retarget = workspace.curRetarget;
			R9Properties props = R9Properties.getSharedProperties();
			int dotSize = props.dotSize();
			for (int n = 0; n < skeleton.getNodes().size(); n++) {
				NodeBase<NodeBase> node = skeleton.getNodes().get(n);
				int x1 = (int) (scale * node.getPosition().getX());
				int y1 = (int) (scale * node.getPosition().getY());

				int pos = retarget == null ? -1 : retarget.char_bvh_root_offset.getIndexOfListInJoins(node.getName(), false) ;
				Color c = Color.black;
				if( pos >= 0) {
					c =  PageColors.nextColor(pos);
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

} 
