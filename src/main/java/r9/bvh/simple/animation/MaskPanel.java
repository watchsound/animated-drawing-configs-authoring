package r9.bvh.simple.animation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
 

import r9.bvh.simpe.utils.GeomUtil;
import r9.bvh.simpe.utils.ImageUtil;
import r9.bvh.simpe.utils.PropertyUIHelper;
import r9.bvh.simple.Settings;
import r9.bvh.simple.imageprocess.FloodFillAlgorithm;

public class MaskPanel extends JPanel {

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
	private JSlider strokeSlider;
	private JSlider colorSimilarSlider;
	private JRadioButton blackCheckbox;
	private JRadioButton whiteCheckbox;

	public MaskPanel(final AnimationWorkspace workspace) {
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

		
		strokeSlider = new JSlider();
		strokeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				  
			}
		});
		strokeSlider.setMinimum(1);
		strokeSlider.setMaximum(20);
		strokeSlider.setValue(2); // zoom level is one
		strokeSlider.setPreferredSize(new Dimension(100, 25));
		strokeSlider.setSize(new Dimension(100, 25));
		
		blackCheckbox = new JRadioButton("黑色");
		whiteCheckbox = new JRadioButton("白色");
		ButtonGroup bg = new ButtonGroup();
		bg.add(blackCheckbox);
		bg.add(whiteCheckbox);
		blackCheckbox.setSelected(true);
		bottom.add(PropertyUIHelper.createRow("画笔粗细",  strokeSlider, blackCheckbox, whiteCheckbox));

		colorSimilarSlider = new JSlider();
		colorSimilarSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				  
			}
		});
		colorSimilarSlider.setMinimum(1);
		colorSimilarSlider.setMaximum(200);
		colorSimilarSlider.setValue(100); // zoom level is one
		colorSimilarSlider.setPreferredSize(new Dimension(200, 25));
		//colorSimilarSlider.setSize(new Dimension(100, 25));
		bottom.add(PropertyUIHelper.createRow("判别颜色相似度距离", new JLabel(), colorSimilarSlider));
		
		loadImageButton = new JButton("导入Mask图片");
	
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
					workspace.curProject.saveMask(file);
					bitmapImage = workspace.curProject.getMask();
					repaint(); 
				}
			}
		});
		 
		saveMaskButton = new JButton("保存Mask图片");
		saveMaskButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int w = bitmapImage.getWidth();
				int h = bitmapImage.getHeight();
				int black = Color.black.getRGB();
				int white = Color.white.getRGB();
				for(int i = 0; i < w; i++) {
					for(int j = 0; j < h; j++) {
						int v = bitmapImage.getRGB(i, j);
						if( FloodFillAlgorithm.calculateColorSimilarity(v, black)> 10 ) {
							bitmapImage.setRGB(i, j, white);
						}
					}
				} 
				workspace.curProject.saveMask(bitmapImage);
				repaint();
			}
		});
		resetButton = new JButton("重置");
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bitmapImage = null;
				workspace.curProject.resetMask( );
				repaint(); 
			}
		});
		bottom.add(PropertyUIHelper.createRow("", loadImageButton, resetButton, saveMaskButton));
		
		statusLabel = new JLabel( "使用鼠标菜单标记Mask区域，或者拖拽擦试");
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
		if( workspace.curProject == null)
			return null;
		if( this.bitmapImage == null ) {
			this.bitmapImage = workspace.curProject.getMask();  
		}
		if( this.bitmapImage == null ) {
			this.bitmapImage = workspace.curProject.getImage();  
		}
		return this.bitmapImage;
	}

	
 
	

	public class ImageEditCanvas extends ScalablePane {

		private static final long serialVersionUID = 1L;
 

		public ImageEditCanvas() { 
			
			MouseAdapter ma = new MouseAdapter() {
				Point prevP;
				private boolean duringDragging;
 
				public void mousePressed(MouseEvent me) { 
					if( me.isPopupTrigger() ) {
						showPopup(me);
					}
					prevP = new Point(me.getX(), me.getY());
					 
					repaint();
				}

				public void showPopup(MouseEvent me) {
					final JPopupMenu menu = new JPopupMenu();
					JMenuItem item = new JMenuItem("使用当前位置颜色Mask");
					item.addActionListener(new ActionListener() { 
						@Override
						public void actionPerformed(ActionEvent e) {
							magic(me.getX(), me.getY(), true);
						}});
					menu.add(item);
//					JMenuItem item2 = new JMenuItem("保留当前位置区域");
//					item2.addActionListener(new ActionListener() { 
//						@Override
//						public void actionPerformed(ActionEvent e) {
//							magic(me.getX(), me.getY(), false);
//						}});
//					menu.add(item2);
					
					menu.show(ImageEditCanvas.this, me.getX(), me.getY());
				}

				public void mouseReleased(MouseEvent me) {
					if( me.isPopupTrigger() ) {
						showPopup(me);
					}
					if (duringDragging  && prevP != null) {
						 
					}
					duringDragging = false;
					prevP = null;
				}

				public void mouseDragged(MouseEvent  e) { 
					duringDragging = true; 
					
					if (prevP != null ) {
						getBufferedImage();
						if (bitmapImage == null) return;
						double scale = getScale();
						int w = getWidth();
						int h = getHeight();
					    int wi = (int) (bitmapImage.getWidth() * scale);
					    int hi = (int) (bitmapImage.getHeight() * scale);
					    int tx = (w - wi) / 2;
						 int ty = (h - hi) / 2;
						 
						maskByPos(tx, ty, e.getPoint().x , e.getPoint().y );
						
						double dist = GeomUtil.distance(prevP, e.getPoint());
						if( dist > 10 ){
						    double angle = GeomUtil.calcAngle(prevP, e.getPoint());
						    for(int i = 4; i < dist; i+=4){
						    	 Point2D.Double  ap1 = (Point2D.Double)GeomUtil.calcPoint(prevP, angle, i);
						    	 maskByPos(tx, ty, (int)ap1.x , (int)ap1.y );
						    }
						   
						}
					}
					prevP = new Point(e.getPoint());
				 
				}

				public void mouseClicked(MouseEvent me) {
					 
				}

			 
			};
			this.addMouseListener(ma);
			this.addMouseMotionListener(ma);
		}
		protected void maskByPos(int tx, int ty, int mx, int my) {
			int strokeWidth = strokeSlider.getValue();
			int scaleLevel = (int) getScale();
			  if( strokeWidth  == 0 )
				  return;
			  int x = mx - tx;
			  int y = my - ty;
			  
			  int mask =  blackCheckbox.isSelected() ? new Color(0, 0, 0, 255).getRGB() 
					  : new Color(255, 255, 255, 255).getRGB() ;
			  if( strokeWidth == 1 ){  
				   bitmapImage.setRGB(x, y, mask);    
				   repaint(x  -scaleLevel + tx, y -scaleLevel + ty, scaleLevel*2, scaleLevel*2);
				   return;
		 	  }
			  bitmapImage.setRGB(x, y, mask);   
			  int rad2 = strokeWidth* strokeWidth /4;
			  for( int i = x - strokeWidth/2; i <= x + strokeWidth/2; i++){
				  for( int j = y - strokeWidth/2; j <= y + strokeWidth/2; j++){
					  if( (i-x)* (i-x) + (j-y)*(j-y) <= rad2 ){
						  bitmapImage.setRGB(i, j, mask);   
					   }
				  } 
			  }
			  repaint(x - strokeWidth/2 * scaleLevel + tx, y - strokeWidth/2 * scaleLevel + ty, strokeWidth* scaleLevel*2, strokeWidth * scaleLevel*2);
		  }
	  
		public void saveImage() {
			 getBufferedImage();
			 workspace.curProject.saveMask(bitmapImage); 
		}

		protected void magic(int x, int y, boolean useMask) {  
			getBufferedImage();
			if (bitmapImage == null) return;
			double scale = getScale();
			int w = getWidth();
			int h = getHeight();
		    int wi = (int) (bitmapImage.getWidth() * scale);
		    int hi = (int) (bitmapImage.getHeight() * scale);
		    int tx = (w - wi) / 2;
			 int ty = (h - hi) / 2;
				 
			
			FloodFillAlgorithm algo = new FloodFillAlgorithm(bitmapImage, null);
			algo.setColorDifference(colorSimilarSlider.getValue());
			
			if( useMask ) {
				 int rgbold0 = bitmapImage.getRGB(x - tx , y - ty);
				 int rgbold1 = x-tx == 0? rgbold0 : bitmapImage.getRGB(x - tx -1 , y - ty);
				 int rgbold2 = y-ty == 0? rgbold0 : bitmapImage.getRGB(x - tx , y - ty-1);
					
				 int rgbold = FloodFillAlgorithm.averageColor(rgbold0, rgbold1, rgbold2);
				  
				 int rgbnew = new Color(0,0,0,255).getRGB();
				  algo.floodFillScanLineWithStack(x-tx, y-ty, rgbnew, rgbold, true);
			} else {
				 algo.floodFillScanLineWithStack(x-tx, y-ty, Color.WHITE.getRGB(), Color.BLACK.getRGB(), false);
			}
			
			 
			 algo.updateResult();
			 algo.clearall();
			 Runtime.getRuntime().gc();
			 repaint();
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
		}


		@Override
		protected Rectangle calculateSize(double scale ) {
			if(workspace.curProject == null)
				return new Rectangle(0,0, getWidth(), getHeight());
			BufferedImage bgImage = getBufferedImage();
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
