package r9.bvh.simple.animation;


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
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import r9.bvh.simpe.utils.PropertyUIHelper;
import r9.bvh.simpe.utils.R9Properties;
import r9.bvh.simple.Settings;
import r9.bvh.simple.model.NodeBase;
import r9.bvh.simple.ui.fbfig.CharacterConfig;
import r9.bvh.simple.ui.fbfig.FigBasePanel;

public class CharacterPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private FigEditCanvas skeletonCanvas;
	// private DiscreteSlider timeSlider;
	private JScrollPane figEditCanvasScrollPane;

	AnimationWorkspace workspace;
	private JLabel statusLabel;
	private JSlider zoomSlider;
	private JLabel zoomLabel;
	private JButton loadImageButton;
	//private BufferedImage bgImage;  
	private JButton loadFigButton;

	public CharacterPanel(final AnimationWorkspace workspace) {
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
	//	bottom.add(PropertyUIHelper.createRow("zoom level", zoomLabel, zoomSlider));

		loadImageButton = new JButton("导入图片");
		loadImageButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(Settings.homedir);
				chooser.setFileFilter(new FileFilter() {
					@Override
					public boolean accept(File f) {
						return f.isDirectory() || f.getName().endsWith("png") || f.getName().endsWith("jpg");
					}

					@Override
					public String getDescription() {
						return "图片";
					}
				});

				int selection = chooser.showOpenDialog(null);
				if (selection == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					workspace.curProject.saveImage(file);
					workspace.curProject.saveTexture(file);
					workspace.curProject.saveMask(file); 
				}
			}
		});
		
		loadFigButton = new JButton("导入FB Figure骨架");
		loadFigButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(new File(Settings.homedir, "fb_characterfigs"));
				chooser.setFileFilter(new FileFilter() {
					@Override
					public boolean accept(File f) {
						return f.isDirectory() || f.getName().endsWith("yaml");
					}

					@Override
					public String getDescription() {
						return "FB 骨架";
					}
				});

				int selection = chooser.showOpenDialog(null);
				if (selection == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					try {
						workspace.curProject.characterConfig =  CharacterConfig.loadFromFile(file);
						update( workspace.curProject.characterConfig );
					} catch (Exception e1) { 
						e1.printStackTrace();
					}
				}
			}
		});
		bottom.add(PropertyUIHelper.createRow(" ", loadImageButton, loadFigButton));

		
		 
		statusLabel = new JLabel("可选取一个或多个节点同时推拽改变位置");
		statusLabel.setPreferredSize(new Dimension(400, 25));
		bottom.add(statusLabel);

		figEditCanvasScrollPane = new JScrollPane(skeletonCanvas);
		figEditCanvasScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		figEditCanvasScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		setLayout(new BorderLayout());
		add(figEditCanvasScrollPane, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
	}

	public void update(CharacterConfig data) { 
		skeletonCanvas.update(data);
	}

	public void highlight(NodeBase<NodeBase> data) {
		skeletonCanvas.highlight(data); 
	}

	public void highlightByMouseClick(NodeBase<NodeBase> data) {
		 
	}

	

	public class FigEditCanvas extends FigBasePanel {

		private static final long serialVersionUID = 1L;

	

		//private int tx; 
		//private int ty;
		private boolean duringDragging;
		Point2D.Double startP;
		Point2D.Double currentP;
		public FigEditCanvas() { 
			MouseAdapter ma = new MouseAdapter() {
				

				private NodeBase getHitNode(MouseEvent me) {
					if(  getSkeleton() == null ) return null;
					for (NodeBase<NodeBase> node : getSkeleton().getNodes()) {
						double x = node.getPosition().getX();
						double y = node.getPosition().getY();
						if ((me.getX() - x) * (me.getX() - x) + (y - me.getY()) * (y - me.getY()) < 81) {
							return node;
						}
					}
					return null;
				}

				public void mousePressed(MouseEvent me) {
					startP = null;
					currentP = null;
					startP = new Point2D.Double(me.getX(), me.getY());
					highlight = getHitNode(me);
					if( highlight == null ) { 
						selections.clear();
						repaint();
						return;
					}  
                    if( !selections.contains(highlight)) {
                    	selections.add(highlight);
                    	repaint();
                    } 
				}
 
				 

				public void mouseReleased(MouseEvent me) {
					 
				   if( duringDragging   && startP != null&& currentP != null) {
					   if( highlight == null ) {
						   int minx = (int) Math.min(me.getX(), startP.getX());
							 int miny = (int) Math.min(me.getY(), startP.getY());
							 int w = (int) Math.abs(me.getX() - startP.getX());
							 int h = (int) Math.abs(me.getY() - startP.getY());
							if( w > 50 || h > 50 ) {
								multipleSelections(new Rectangle2D.Double(minx, miny, w, h)); 
							}
					   } else {
						   updateNodeLocBy(me); 
					   } 
					}
					highlight = null;
					duringDragging = false;
					startP = null;
					currentP = null;
				}

				public void mouseDragged(MouseEvent me) {
					currentP = new Point2D.Double(me.getX(), me.getY());  
					duringDragging = true;
					repaint();
				}

				public void mouseClicked(MouseEvent me) { 
					startP = null;
					currentP = null;
				}

				public void updateNodeLocBy(MouseEvent me) {
					int xChange = me.getX() - (int) startP.getX();
					int yChange = (me.getY() - (int) startP.getY());
					if (selections.isEmpty()  || (xChange == 0 && yChange == 0))
						return;
					 
					for(NodeBase node : selections) {
						double xo = node.getPosition().getX();
						double yo = node.getPosition().getY();
						double zo = node.getPosition().getZ(); 
						double x = xo + xChange;
						double y = yo + yChange;
						double z = zo; 
						node.getPosition().setX(x);
						node.getPosition().setY(y);
					}
					
					 
					repaint();
				}
			};
			this.addMouseListener(ma);
			this.addMouseMotionListener(ma);
		}

	 

		public void update(CharacterConfig data) {
			this.setSkeleton(data);  
			highlight = null;
			selections.clear();
			mapFigSpaceToCanvas();
			repaint();
		}

		public void save() {
			CharacterConfig skeleton = getSkeleton();
			if(skeleton == null ) return;
			double scale = getScale();
			Rectangle r = calculateSize(skeleton, 1);
			for (int n = 0; n < skeleton.getNodes().size(); n++) {
				NodeBase<NodeBase> node = skeleton.getNodes().get(n);
				int x1 = (int) ((node.getPosition().getX() - r.getX()) / scale);
				int y1 = (int) ((node.getPosition().getY() - r.getY()) / scale);
				node.getPosition().setX(x1);
				node.getPosition().setY(y1);
			}
			skeleton.setWidth((int) (r.getWidth() / scale));
			skeleton.setHeight((int) (r.getHeight() / scale));

			workspace.curProject.characterConfig = skeleton;
			
			// change mode back to be in canvas space
			update(skeleton);
		}

	

	 
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if( workspace.curProject == null)
				return;
			Graphics2D g2d = (Graphics2D) g;
			CharacterConfig skeleton = getSkeleton();
			double scale = getScale();
			BufferedImage bgImage = workspace.curProject.getImage();
			if (skeleton == null && bgImage == null)
				return;
			int w = getWidth();
			int h = getHeight();
			if (bgImage != null) {
				int wi = (int) (bgImage.getWidth() * scale);
				int hi = (int) (bgImage.getHeight() * scale);
				int tx = (w - wi) / 2;
				int ty = (h - hi) / 2;
				g2d.drawImage(bgImage, tx, ty, tx+wi, ty+hi, 0, 0, bgImage.getWidth(), bgImage.getHeight(), null);
			}
			if (skeleton == null)
				return;
			R9Properties props = R9Properties.getSharedProperties();
			int dotSize = props.dotSize();
			for (int n = 0; n < skeleton.getNodes().size(); n++) {
				NodeBase<NodeBase> node = skeleton.getNodes().get(n);
				int x1 = (int) (node.getPosition().getX());
				int y1 = (int) (node.getPosition().getY());

				g2d.setColor(selections.contains(node) ? Color.RED : Color.BLACK);
				g2d.fillOval((int) (x1 - dotSize/2), (int) (y1 - dotSize/2), dotSize, dotSize);

				g2d.drawString(node.getName(), x1 + dotSize + 10, y1 - 5);
 
				g2d.setColor(Color.BLACK);
				for (NodeBase child : node.getChildrens()) {
					int x2 = (int) (child.getPosition().getX());
					int y2 = (int) (child.getPosition().getY());
					g2d.drawLine(x1, y1, x2, y2);
				}
			}
			
			if( startP != null && currentP != null && highlight == null) {
				 int minx = (int) Math.min(currentP.getX(), startP.getX());
				 int miny = (int) Math.min(currentP.getY(), startP.getY());
				 int w0 = (int) Math.abs(currentP.getX() - startP.getX());
				 int h0 = (int) Math.abs(currentP.getY() - startP.getY());
				 g.drawRect(minx, miny, w0, h0);
			}
		}

	} 

	 
	public void updateByProjectChange() {
		 if( workspace.curProject!=null) {
			 this.update(workspace.curProject.characterConfig);
		 }
	}

	public void saveUIState() {
		skeletonCanvas.save();
	}

}
