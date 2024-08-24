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
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileFilter;

import r9.bvh.simpe.utils.PageColors;
import r9.bvh.simpe.utils.PropertyUIHelper;
import r9.bvh.simpe.utils.R9Properties;
import r9.bvh.simple.Settings;
import r9.bvh.simple.model.Node;
import r9.bvh.simple.model.NodeBase;
import r9.bvh.simple.model.Skeleton;
import r9.bvh.simple.retarget.RetargetConfig.char_bodypart_group;
import r9.bvh.simple.ui.fbfig.CharacterConfig;
import r9.bvh.simple.ui.fbfig.FigBasePanel;

public class CharacterRigGroupPane extends JPanel {

	private static final long serialVersionUID = 1L;
	private FigEditCanvas skeletonCanvas;
	// private DiscreteSlider timeSlider;
	private JScrollPane figEditCanvasScrollPane;

	RetargetWorkspace workspace;
	private JLabel statusLabel;
	private JSlider zoomSlider;
	private JLabel zoomLabel;
	private JLabel charFileNameLabel;
	private JComboBox<String> bvh_depth_driversComboBox; 

//	private Retarget retarget;

	private char_bodypart_group curGroup;
	private Map<char_bodypart_group, Color> name2Color = new HashMap<>();
	
	
	public CharacterRigGroupPane(final RetargetWorkspace workspace) {
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

		charFileNameLabel = new JLabel();
		JButton loadFileButton = new JButton("导入文件");
		loadFileButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(new File(Settings.homedir, "fb_characterfigs"));
	    		chooser.setFileFilter(new FileFilter(){
	             	@Override
					public boolean accept(File f) {
	             	     return f.isDirectory()  ||  f.getName().endsWith("yaml") ;
					} 
					@Override
					public String getDescription() {
					   	return  "Character Yaml";
					}});
	    		
	    		 int selection = chooser.showSaveDialog( null);
	    		 if ( selection == JFileChooser.APPROVE_OPTION ){
	    			  File file = chooser.getSelectedFile(); 
	    			  try { 
	    				  CharacterConfig s = CharacterConfig.loadFromFile(file);
	    				  workspace.curRetarget.char_file = file.getAbsolutePath();
	    				  workspace.curRetarget.cfig = s;
	    				  workspace.reloadWorkspace(workspace.curRetarget);
	    				 // workspace.update(s);
	    				//  update(s, file.getAbsolutePath());
						} catch ( Exception e1) {
							 e1.printStackTrace();
						}
		         }
			}});
		bottom.add(PropertyUIHelper.createRow("Character 文件", charFileNameLabel, loadFileButton));
		
		
        bottom.add(PropertyUIHelper.createTitleRow("Grouping" ));
		 
        bvh_depth_driversComboBox = new JComboBox<String>( );
        bvh_depth_driversComboBox.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				 if( curGroup != null ) {
					 curGroup.getBvh_depth_drivers( ).clear();
					 curGroup.getBvh_depth_drivers().add((String) bvh_depth_driversComboBox.getSelectedItem());
				 }
			}});
		
		bottom.add(PropertyUIHelper.createRow("bvh_depth_drivers", new JLabel(), bvh_depth_driversComboBox)); 
	 
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
	
	public void update(Skeleton data) {
		if( data == null ) return;
		Vector<String> names = new Vector<>();
		for(Node n : data.getNodes()) {
			if( !names.contains(n.getName()))
				names.add(n.getName());
		}
		bvh_depth_driversComboBox.setModel(new DefaultComboBoxModel<String>(names));
	}

	public void update(CharacterConfig data, String fname) { 
		update(workspace.curRetarget.skeleton);
		skeletonCanvas.update(data);
		if(fname.length() > 20)
			fname = "..." + fname.substring(fname.length()-20);
		charFileNameLabel.setText(fname);
	}
	public Color getColor(char_bodypart_group gname) {
		Color c = name2Color.get(gname);
		if( c != null) return c;
		c = PageColors.nextColor(name2Color.size());
		name2Color.put(gname, c);
		return c;
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
					if( skeleton == null ) return null;
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

			 

				private void handlePopup(MouseEvent me, List<NodeBase> nodes) {
					if (nodes.size() == 0)
						return;
					highlight = nodes.get(0);
					final JPopupMenu menu = new JPopupMenu();
					menu.add(PropertyUIHelper.createTitleRow("选取已经创建的Driver"));
					RetargetConfig retarget = workspace.curRetarget;
					List<String> driversInUse = retarget.get_char_group_bvh_drivers(); 
					  
					final char_bodypart_group curDriver = retarget.get_char_group_by_char_name(highlight.getName());
					for(int i = 0; i < driversInUse.size(); i++) {
						final String s = (String)driversInUse.get(i);
						JCheckBoxMenuItem item = new JCheckBoxMenuItem(s);
						item.setSelected( curDriver != null && curDriver.bvh_depth_drivers.contains(s));
						item.addActionListener(new ActionListener() { 
							public void actionPerformed(ActionEvent e) {
								for(NodeBase n : nodes) {
									char_bodypart_group aDriver = retarget.get_char_group_by_char_name(n.getName());
									if( aDriver != null)
										aDriver.char_joints.remove(n.getName());
									 char_bodypart_group newDriver = retarget.get_char_group_by_bvh_name( s );
									 newDriver.char_joints.add(n.getName());
								}
								 
							}});
						menu.add(item);
					}
					JMenu submenu = new JMenu("选取可创建的Driver");
					menu.add(submenu);
					 
					List<Node> snodes = workspace.curRetarget.skeleton.getNodes();
					List<String> bvhnodes = new ArrayList<>(); 
					int size = snodes.size();
					for(int i = 0; i < size; i++) {
						String node = (String)snodes.get(i).getName();
						if( driversInUse.contains(node) )
							continue;
						bvhnodes.add(node); 
					}
					for(int i = 0; i < bvhnodes.size(); i++) {
						final String s = (String)bvhnodes.get(i);
						JMenuItem item = new JMenuItem(s);
						item.addActionListener(new ActionListener() { 
							public void actionPerformed(ActionEvent e) {
								for(NodeBase n : nodes) {
									char_bodypart_group aDriver = retarget.get_char_group_by_char_name(n.getName());
									 if( aDriver != null)
										 aDriver.char_joints.remove(n.getName());
								}
								char_bodypart_group g = new char_bodypart_group();
								g.bvh_depth_drivers.add(s);
								for(NodeBase n : nodes) {  
									g.char_joints.add(n.getName()); 
								} 
								retarget.char_bodypart_groups.add(g);
							}});
						submenu.add(item);
					}
					 
					
					menu.addPopupMenuListener(new PopupMenuListener() {
						public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
						}

						public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
							 
						}

						public void popupMenuCanceled(PopupMenuEvent e) {
						}
					});
					menu.show(FigEditCanvas.this, me.getX(), me.getY());
				}

				public void mouseReleased(MouseEvent me) {
					if (me.isPopupTrigger()) {  
						handlePopup(me, Arrays.asList(highlight));
						return;
					}
					CharacterConfig skeleton = getSkeleton(); 
					double scale = getScale();
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
					currentP = new Point2D.Double(me.getX(), me.getY());
					repaint();
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

				char_bodypart_group aGroup = retarget == null ? null : retarget.get_char_group_by_char_name( node.getName()) ;
				Color c = Color.black;
				if( aGroup != null) {
					c = getColor(aGroup);
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
