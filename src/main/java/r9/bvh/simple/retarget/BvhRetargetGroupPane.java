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

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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

import r9.bvh.simpe.utils.NodeHelper;
import r9.bvh.simpe.utils.NodePopupMenuHelper;
import r9.bvh.simpe.utils.PropertyUIHelper;
import r9.bvh.simpe.utils.R9Properties;
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
import r9.bvh.simple.ui.fbfig.CharacterConfig;
import r9.bvh.simple.ui.fbfig.FbCharacterFigRegister;
import r9.bvh.simple.ui.fbfig.FigEditZone.FigEditCanvas;

public class BvhRetargetGroupPane extends JPanel {

	private static final long serialVersionUID = 1L;
	private SkeletonCanvas skeletonCanvas;
	//private DiscreteSlider timeSlider;
	private JScrollPane skeletonCanvasScrollPane;

	RetargetWorkspace workspace;
	private JLabel statusLabel;
	private JSlider zoomSlider;
	private JLabel zoomLabel;
	private JLabel bvhFileNameLabel;
	private JTextField groupNameField;
	private JComboBox<BvhGroupMethod> groupTypeComboBox;
	
	//private Retarget retarget;

	private bvh_projection_bodypart_group curGroup;
	private Map<String, Color> name2Color = new HashMap<>();
	private JCheckBox useMotionCheckBox;
	

	public BvhRetargetGroupPane(final RetargetWorkspace workspace) {
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
		  
		bvhFileNameLabel = new JLabel();
		JButton loadFileButton = new JButton("导入文件");
		loadFileButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(Settings.homedir);
	    		chooser.setFileFilter(new FileFilter(){
	             	@Override
					public boolean accept(File f) {
	             	     return f.isDirectory()  ||  f.getName().endsWith("bvh") ;
					} 
					@Override
					public String getDescription() {
					   	return  "BVH file";
					}});
	    		
	    		 int selection = chooser.showSaveDialog( null);
	    		 if ( selection == JFileChooser.APPROVE_OPTION ){
	    			  File file = chooser.getSelectedFile(); 
	    			  try {
	    				 
	    				  Skeleton s = new Skeleton(file);
	    				  workspace.curRetarget.bvh_file = file.getAbsolutePath();
	    				  workspace.curRetarget.skeleton = s;
	    				  workspace.reloadWorkspace(workspace.curRetarget);
	    				  useMotionCheckBox.setVisible(s.getMotion() !=null && s.getMotion().getFrameSize()>0);
	    				 // update(workspace.curRetarget, file.getAbsolutePath());
						} catch ( Exception e1) {
							 e1.printStackTrace();
						}
		         }
			}});
		bottom.add(PropertyUIHelper.createRow("BVH 文件", bvhFileNameLabel, loadFileButton));
		
		useMotionCheckBox = new JCheckBox("使用运动数据");
		useMotionCheckBox.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				workspace.curRetarget.skeleton.setPose( useMotionCheckBox.isSelected()? 0 : -1);
				skeletonCanvas.repaint();
			}});
		useMotionCheckBox.setVisible(false);
		bottom.add(PropertyUIHelper.createRow( useMotionCheckBox, true, null));
		
		
		
		bottom.add(PropertyUIHelper.createTitleRow("Grouping" ));
		
		groupNameField = new JTextField(20)	;
		groupNameField.addFocusListener(new FocusListener() { 
			public void focusGained(FocusEvent e) { 
			} 
			public void focusLost(FocusEvent e) { 
				 if( curGroup != null )
					 curGroup.setName(groupNameField.getName());
			}});
		groupTypeComboBox = new JComboBox<RetargetConfig.BvhGroupMethod>(RetargetConfig.BvhGroupMethod.values());
		groupTypeComboBox.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				 if( curGroup != null )
					 curGroup.setMethod((BvhGroupMethod) groupTypeComboBox.getSelectedItem());
			}});
		
		bottom.add(PropertyUIHelper.createRow("Group Name", new JLabel(), groupNameField));
		bottom.add(PropertyUIHelper.createRow("Method Type", new JLabel(), groupTypeComboBox));
		
		statusLabel = new JLabel();
	    statusLabel.setText("用鼠标推拽选取多个节点");
		statusLabel.setPreferredSize(new Dimension(400, 25));
		bottom.add(statusLabel);

		skeletonCanvasScrollPane = new JScrollPane(skeletonCanvas);
		skeletonCanvasScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		skeletonCanvasScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		setLayout(new BorderLayout());
		add(skeletonCanvasScrollPane, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
	}
	
	public Color getColor(String gname) {
		Color c = name2Color.get(gname);
		if( c != null) return c;
		c = PageColors.nextColor(name2Color.size());
		name2Color.put(gname, c);
		return c;
	}

	public void update(RetargetConfig retarget , String fname) { 
		// if( retarget != null )
		//	 this.retarget = retarget;
		 this.name2Color.clear();
		 this.curGroup = null;
		 skeletonCanvas.update(retarget.skeleton);
		 if(fname.length() > 20)
				fname = "..." + fname.substring(fname.length()-20);
		 bvhFileNameLabel.setText(fname);
		 useMotionCheckBox.setVisible(retarget.skeleton!=null && retarget.skeleton.getMotion().getFrameSize()>0);
	}
	public void setGroup(bvh_projection_bodypart_group group) {
		this.curGroup = group;
		this.groupNameField.setText(group.name);
		this.groupTypeComboBox.setSelectedItem(group.getMethod());
	}

	public void highlight(Node data) {
		skeletonCanvas.highlight(data);
	}

	public void highlightByMouseClick(MouseEvent me, List<Node> data) {
		if( data.isEmpty()) return;
		workspace.highlightByMouseClick(data.get(0));
		RetargetConfig retarget = workspace.curRetarget;
	    List<String> groupNames = retarget.get_bvh_group_names( );
	   
	    
		final JPopupMenu menu = new JPopupMenu();
		menu.add(PropertyUIHelper.createTitleRow("创建Group"));
		final JTextField aname = new JTextField(20);
		menu.add(PropertyUIHelper.createRow("名称", aname));
		JMenu submenu = new JMenu("使用已经定义的Group：");
		menu.add(submenu);
		for(final String gname : groupNames) {
			JCheckBoxMenuItem item = new JCheckBoxMenuItem(gname);
			item.setSelected(curGroup != null && gname.equals(curGroup.name));
			item.addActionListener(new ActionListener() { 
				public void actionPerformed(ActionEvent e) {
					for(Node node: data) {
						 bvh_projection_bodypart_group acurGroup = retarget.get_bvh_group_by_bvhnode( node.getName() ) ;
						 if( acurGroup != null)
							 acurGroup.bvh_joint_names.remove(node.getName());
						 acurGroup = retarget.get_bvh_group_by_group_name( gname ) ;
						 if( acurGroup != null )
							 acurGroup.bvh_joint_names.add(node.getName());
						 setGroup(acurGroup);	
					}
					
				}});
			submenu.add(item);
		}
		 
		JMenuItem item = new JMenuItem("Close");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menu.setVisible(false);
			}
		});
		menu.add(item);
		menu.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				String name = aname.getText().trim();
				if (name.length() == 0)
					return;
			//	 if( curGroup != null && curGroup.getName().equals(name))
			//		return;
				bvh_projection_bodypart_group acurGroup = null;
				for(Node node: data) {
					 acurGroup = retarget.get_bvh_group_by_bvhnode( node.getName() ) ;
					 if( acurGroup != null)
						 acurGroup.bvh_joint_names.remove(node.getName());
					 acurGroup = retarget.get_bvh_group_by_group_name(  name ) ;
					 if( acurGroup != null ) {
						 acurGroup.bvh_joint_names.add(node.getName());
					 } else {
						 acurGroup = new bvh_projection_bodypart_group();
						 acurGroup.name = name;
						 acurGroup.method = RetargetConfig.BvhGroupMethod.pca;
						 acurGroup.bvh_joint_names.add(node.getName()); 
						 retarget.bvh_projection_bodypart_groups.add(acurGroup);
					 }
				} 
				 setGroup(acurGroup);
			}

			public void popupMenuCanceled(PopupMenuEvent e) {
			}
		});
		menu.show( this, me.getX(), me.getY());
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

					
				}

				public void mouseReleased(MouseEvent me) {
					if (duringDragging &&  startP != null) {
						 int minx = (int) Math.min(me.getX(), startP.getX());
						 int miny = (int) Math.min(me.getY(), startP.getY());
						 int w = (int) Math.abs(me.getX() - startP.getX());
						 int h = (int) Math.abs(me.getY() - startP.getY());
						 Rectangle r = new Rectangle(minx, miny, w, h);
						 List<Node> nodes = new ArrayList<>();
						 for (int i = 0; i < locs.size(); i++) {
								Rectangle loc = locs.get(i);
								if (r.contains(loc)) {
									Node n = node4loc.get(i);
									if(n.getName().equalsIgnoreCase("Site"))
										continue;
									nodes.add(n);
								}
							}
						 highlightByMouseClick(me, nodes);
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
					Node hit = getHitNode(me);
					if (hit == highlight)
						return;
					highlight = hit;
//					if( !me.isPopupTrigger() ) return;
//                    List<Node> nodes = new ArrayList<>();
//                    nodes.add(highlight);
//					highlightByMouseClick(me, nodes);
					repaint();
				}

			 
			};
			this.addMouseListener(ma);
			this.addMouseMotionListener(ma);
		}

		public void update( Skeleton data) { 
			this.setSkeleton(data);
			 curGroup = null;
			highlight = null; 
			repaint();
		}

		private  void paintFigures(Graphics2D g2d, Skeleton skeleton, Node highlight, double tx, double ty,
				double scale, List<Rectangle> locs, List<Node> node4loc) {

			g2d.translate(tx, ty);
			R9Properties props = R9Properties.getSharedProperties();
			int dotSize = props.dotSize();
            RetargetConfig retarget = workspace.curRetarget;
			for (int n = 0; n < skeleton.getNodes().size(); n++) {
				Node node = skeleton.getNodes().get(n);
				int x1 = (int) (scale * node.getPosition().getX());
				int y1 = (int) (-scale * node.getPosition().getY());
				bvh_projection_bodypart_group aGroup = retarget == null ? null :retarget.get_bvh_group_by_bvhnode( node.getName()) ;
				Color c = Color.black;
				if( aGroup != null) {
					c = getColor(aGroup.name);
				}
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
