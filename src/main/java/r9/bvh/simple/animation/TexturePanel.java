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
import java.awt.image.BufferedImage;
import java.io.File;

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
import r9.bvh.simple.Settings;
import r9.bvh.simple.animation.CharacterPanel.FigEditCanvas;
import r9.bvh.simple.imageprocess.FloodFillAlgorithm;

public class TexturePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private ImageEditCanvas maskCanvas;
	// private DiscreteSlider timeSlider;
	private JScrollPane figEditCanvasScrollPane;

	AnimationWorkspace workspace;
	private JLabel statusLabel;
	private JSlider zoomSlider;
	private JLabel zoomLabel;
	private JButton loadImageButton; 
	 private BufferedImage bitmapImage;  
	private JButton saveMaskButton;
	private JButton resetButton;

	public TexturePanel(final AnimationWorkspace workspace) {
		this.workspace = workspace;
		maskCanvas = new ImageEditCanvas();
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
				maskCanvas.setScale(zoom);
				zoomLabel.setText(zoom + "");
			}
		});
		zoomSlider.setMinimum(1);
		zoomSlider.setMaximum(50);
		zoomSlider.setValue(10); // zoom level is one
		zoomSlider.setPreferredSize(new Dimension(200, 25));
		bottom.add(PropertyUIHelper.createRow("zoom level", zoomLabel, zoomSlider));

		loadImageButton = new JButton("导入Texture图片");
	
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
					workspace.curProject.saveTexture(file);
					bitmapImage = workspace.curProject.getTexture();
					workspace.setupNewImage( );
					repaint(); 
				}
			}
		});
		 
		saveMaskButton = new JButton("保存Texture图片");
		saveMaskButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				workspace.curProject.saveTexture(bitmapImage);
			}
		});
		resetButton = new JButton("重置");
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bitmapImage = null;
				workspace.curProject.resetTexture( );
				repaint(); 
			}
		});
		bottom.add(PropertyUIHelper.createRow("", loadImageButton, resetButton, saveMaskButton));
		
		statusLabel = new JLabel("使用鼠标拖拽出剪切和保存的区域");
		statusLabel.setPreferredSize(new Dimension(400, 25));
		bottom.add(statusLabel);

		figEditCanvasScrollPane = new JScrollPane(maskCanvas);
		figEditCanvasScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		figEditCanvasScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		setLayout(new BorderLayout());
		add(figEditCanvasScrollPane, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
	}
	
	public BufferedImage getBufferedImage() {
		if( workspace.curProject == null )
			return null;
		if( this.bitmapImage == null ) {
			this.bitmapImage = workspace.curProject.getTexture();  
		}
		if( this.bitmapImage == null ) {
			this.bitmapImage = workspace.curProject.getImage();  
		}
		return this.bitmapImage;
	}

	 
	

	public class ImageEditCanvas extends ScalablePane {

		private static final long serialVersionUID = 1L;
 
		Point2D.Double startP;
		Point2D.Double currentP;
		public ImageEditCanvas() {
			MouseAdapter ma = new MouseAdapter() {
				
				private boolean duringDragging;
 
				public void mousePressed(MouseEvent me) { 
					startP = null;
					currentP = null;
					if( me.isPopupTrigger() ) {
						//showPopup(me);
					}
					startP = new Point2D.Double(me.getX(), me.getY());
					 
					repaint();
				}

				 

				public void mouseReleased(MouseEvent me) {
					if( me.isPopupTrigger() ) {
					//	showPopup(me);
					}
					if( duringDragging && startP != null&& currentP != null) {
						int minx = (int) Math.min(me.getX(), startP.getX());
						 int miny = (int) Math.min(me.getY(), startP.getY());
						 int w = (int) Math.abs(me.getX() - startP.getX());
						 int h = (int) Math.abs(me.getY() - startP.getY());
						if( w > 50 || h > 50 ) {
							JPopupMenu popup = new JPopupMenu();
							JMenuItem item = new JMenuItem("Crop");
							item.addActionListener(new ActionListener() { 
								public void actionPerformed(ActionEvent e) {
								   int w0 = getWidth();
								    int h0 = getHeight();
								    BufferedImage bi = new BufferedImage(w0, h0, BufferedImage.TYPE_INT_RGB);
								    Graphics2D g = bi.createGraphics();
								    paint(g);
								    g.dispose();
								    BufferedImage bii = bi.getSubimage(minx, miny, w, h);
								    workspace.curProject.saveImage(bii);
								    workspace.setupNewImage( );
									startP = null;
									currentP = null;
								    repaint();
								}});
							popup.add(item);
							popup.show(ImageEditCanvas.this, me.getX(), me.getY());
						}
					}
				//	startP = null;
				//	currentP = null;
				}

				public void mouseDragged(MouseEvent me) { 
					duringDragging = true; 
					currentP = new Point2D.Double(me.getX(), me.getY());
					repaint();
				}

				public void mouseClicked(MouseEvent me) {
   				    startP = null;
				    currentP = null;
				}

			 
			};
			this.addMouseListener(ma);
			this.addMouseMotionListener(ma);
		}

	  
		public void saveImage() {
			 getBufferedImage();
			 workspace.curProject.saveMask(bitmapImage); 
		}

	

	 
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			 
			double scale = getScale();
			 getBufferedImage();
			if ( bitmapImage == null)
				return;
			int w = getWidth();
			int h = getHeight();
			if (bitmapImage != null) {
				int wi = (int) (bitmapImage.getWidth() * scale);
				int hi = (int) (bitmapImage.getHeight() * scale);
				int tx = (w - wi) / 2;
				int ty = (h - hi) / 2;
				g2d.drawImage(bitmapImage, tx, ty, tx+wi, ty+hi, 0, 0, bitmapImage.getWidth(), bitmapImage.getHeight(), null);
			} 
			
			if( startP != null && currentP != null) {
				 g.setColor(Color.black);
				 int minx = (int) Math.min(currentP.getX(), startP.getX());
				 int miny = (int) Math.min(currentP.getY(), startP.getY());
				 int w0 = (int) Math.abs(currentP.getX() - startP.getX());
				 int h0 = (int) Math.abs(currentP.getY() - startP.getY());
				 g.drawRect(minx, miny, w0, h0);
			}
		}


		@Override
		protected Rectangle calculateSize(double scale ) {
			if(workspace.curProject == null)
				return new Rectangle(0,0, getWidth(), getHeight());
			BufferedImage bgImage = workspace.curProject.getImage();
			if(bgImage == null)
				return new Rectangle(0,0, getWidth(), getHeight());
			return new Rectangle(0,0, 
					(int)(bgImage.getWidth()*scale), (int)(bgImage.getHeight()*scale));
		}

	} 

	 

	public void updateByProjectChange() {
		 this.bitmapImage = null;
		 repaint();
	}

	public void saveUIState() {
		// TODO Auto-generated method stub
		
	}

}
