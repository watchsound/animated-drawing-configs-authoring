package r9.bvh.simple;

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
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import r9.bvh.simpe.utils.DiscreteSlider;
import r9.bvh.simpe.utils.PropertyUIHelper;
import r9.bvh.simple.model.Node;
import r9.bvh.simple.model.Skeleton;

public class CenterDrawingZone extends JPanel {

	private static final long serialVersionUID = 1L;
	private SkeletonCanvas skeletonCanvas;
	//private DiscreteSlider timeSlider;
	private JScrollPane skeletonCanvasScrollPane;

	Workspace workspace;
	private JLabel statusLabel;
	private JSlider zoomSlider;
	private JLabel zoomLabel;
	private JTextField frameTimesField;
	private JButton frameTimesSaveButton;

	public CenterDrawingZone(final Workspace workspace) {
		this.workspace = workspace;
		skeletonCanvas = new SkeletonCanvas();
//		DiscreteSlider.CALLBACK callback = new DiscreteSlider.CALLBACK() {
//			@Override
//			public void onValueChange(int value) {
//				skeletonCanvas.setScale(value);
//			}
//
//			@Override
//			public Color getColor() {
//				return Color.pink;
//			}
//		};
		JPanel bottom = new JPanel();
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
//		timeSlider = new DiscreteSlider(200, 20, Color.green, 1, 20, 10, -1, callback);
//		bottom.add(PropertyUIHelper.createRow("zoom level", timeSlider));

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
		
		JButton play = new JButton("Play");
		play.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				skeletonCanvas.play();
			}
		});
		JButton stop = new JButton("Stop");
		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				skeletonCanvas.stop();
			}
		});
		bottom.add(PropertyUIHelper.createRow("Preview", play, stop));

		frameTimesField = new JTextField( );
		frameTimesField.setPreferredSize(new Dimension(100,28));
		frameTimesField.setMaximumSize(new Dimension(100,28));
		frameTimesSaveButton = new JButton("保存");
		frameTimesSaveButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				 workspace.skeleton.getMotion().setFrameTime(Double.parseDouble(frameTimesField.getText()));
			}});
		
		bottom.add(PropertyUIHelper.createRow("Frame Times(in sec)", new JLabel(), (JComponent)frameTimesField, frameTimesSaveButton));
		
		statusLabel = new JLabel();
		statusLabel.setText("Ctrl- with mouse drag to move node toward you.");
		statusLabel.setPreferredSize(new Dimension(400, 25));
		bottom.add(statusLabel);

		skeletonCanvasScrollPane = new JScrollPane(skeletonCanvas);
	 	skeletonCanvasScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	 	skeletonCanvasScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		setLayout(new BorderLayout());
		add(skeletonCanvasScrollPane, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
	}

	public void update(Skeleton data) {
		skeletonCanvas.update(data);
	}

	public void highlight(Node data) {
		skeletonCanvas.highlight(data);
	}

	public void highlightByMouseClick(Node data) {
		workspace.highlightByMouseClick(data);
	}

	public void play() {
		skeletonCanvas.play();
	}

	public void stop() {
		skeletonCanvas.stop();
	}

	public void setFrameIndex(int pos) {
		skeletonCanvas.setFrameIndex(pos);
	}

	
	public class SkeletonCanvas extends BVHBasePanel {

		private static final long serialVersionUID = 1L;

		private int frameIndex;

		private TimerTask task;
		

		private boolean duringDragging;

		public SkeletonCanvas() {
			MouseAdapter ma = new MouseAdapter() {
				Point2D.Double prevP;

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
					prevP = new Point2D.Double(me.getX(), me.getY());

					Node hit = getHitNode(me);
					if (hit == highlight)
						return;
					highlight = hit;

					highlightByMouseClick(highlight);
					repaint();
				}

				public void mouseReleased(MouseEvent me) {
					if (duringDragging && highlight != null && prevP != null) {
						updateNodeLocBy(me);
					}
					duringDragging = false;
					prevP = null;
				}

				public void mouseDragged(MouseEvent me) {
					if (highlight == null)
						return;
					duringDragging = true;
					//
				}

				public void mouseClicked(MouseEvent me) {

				}

				public void updateNodeLocBy(MouseEvent me) {
					int xChange = me.getX() - (int) prevP.getX();
					int yChange = - ( me.getY() - (int) prevP.getY());
					if (highlight == null || (xChange == 0 && yChange == 0))
						return;
					System.out.println(highlight.getPosition());
					double scale = getScale();
					Node p = highlight.getParent();
					if (p == null) {
						double xc = xChange / scale;
						double yc = yChange / scale;
					    highlight.shift(xc, yc, 0);
					} else {
						double xc = xChange / scale;
						double yc = yChange / scale;
						System.out.println(" xc = " + xc + "  yc = " + yc);
						
						double maxlen = 0;//highlight.getDistanceToParent();
						double px = highlight.getParent().getPosition().getX();
						double py =   highlight.getParent().getPosition().getY();
						double pz =   highlight.getParent().getPosition().getZ();

						System.out.println(" parent px = " + px + "  py = " + py + " pz = " + pz);
						
						double xo = highlight.getPosition().getX() ;
						double yo =   highlight.getPosition().getY()  ;
						double zo =    highlight.getPosition().getZ()  ;
						
						System.out.println(" highlight px = " + xo + "  py = " + yo + " pz = " + zo);
						
						
						double dis0 = Math.sqrt((px - xo) * (px - xo) + (py - yo) * (py - yo) + (pz - zo) * (pz - zo));
						
						System.out.println(" dis0 = " + dis0);
						
						
						double x = xo + xc;
						double y =   yo  + yc;
						//double z =  zo;
						System.out.println(" highlight(changed) px = " + x  + "  py = " + y  + " pz = " + zo);
						
						//double dis_t = Math.sqrt((px - x) * (px - x) + (py - y) * (py - y) + (pz - z) * (pz - z));
					 	//System.out.println(" maxlen = " + maxlen + "  diso = " + dis0 + " dis_t = " + dis_t);
						//if (dis_t > dis0)
						//	return;
					    double dist_z_2 = dis0*dis0 - (px - x) * (px - x) - (py - y) * (py - y) ;
					    System.out.println(" dis0*dis0  = " + dis0 * dis0+ 
					    		"  (px - x) * (px - x) + (py - y) * (py - y) = " + ((px - x) * (px - x) + (py - y) * (py - y) ));
					    
						if( dist_z_2 < 0 )
							return;
						double projxo = xo - px;
						double projyo = yo - py;
						double projzo = zo - pz;
						double r_xo = Math.atan2(-projyo, projzo );
						double r_yo = Math.atan2(-projzo, projxo);
						double r_zo = Math.atan2(projyo, projxo );
//						double r_xo = Math.acos(projxo / dis0);
//						double r_yo = Math.acos(projyo / dis0);
//						double r_zo = Math.acos(projzo / dis0);
						double r_xo_angle = r_xo * 180 / Math.PI;
						double r_yo_angle = r_yo * 180 / Math.PI;
						double r_zo_angle = r_zo * 180 / Math.PI;
						System.out.println( " proj  before = " + projxo+ " " + projyo+ " " + projzo);
						
					 	System.out.println( " angle before = " + r_xo_angle+ " " + r_yo_angle+ " " + r_zo_angle);
						
						
						double projx = x - px;
						double projy = y - py;
						double projz = (zo < 0 ? -1 : 1)* Math.sqrt(dis0 * dis0 - projx * projx - projy * projy);
						if (me.isControlDown())
							projz = -projz;
						
						double r_x = Math.atan2(-projy, projz );
						double r_y = Math.atan2(-projz, projx);
						double r_z = Math.atan2(projy, projx );
						
//						double r_x = Math.acos(projx / dis0);
//						double r_y = Math.acos(projy / dis0);
//						double r_z = Math.acos(projz / dis0);
						double newx_angle = r_x * 180 / Math.PI;
						double newy_angle = r_y * 180 / Math.PI;
						double newz_angle = r_z * 180 / Math.PI;
						System.out.println( " proj  after = " + projx + " " + projy + " " + projz );
						System.out.println( " angle after = " + newx_angle+ " " + newy_angle+ " " + newz_angle);
						
						
						double changex = newx_angle - r_xo_angle;
						double changey = newy_angle - r_yo_angle;
						double changez = newz_angle - r_zo_angle;
						
						System.out.println( "rotate changes  = " + changex+ " " + changey+ " " + changez);
						
						
						
						p.setXrotation(p.getXrotation()+ changex);
						p.setYrotation(p.getYrotation()+ changey);
						p.setZrotation(p.getZrotation()+ changez);
						workspace.undoManager.pushNodeRotationChange(p, changex, changey, changez);

						
//						double pr_x = p.getXrotation();
//						double pr_y = p.getYrotation();
//						double pr_z = p.getZrotation();
//						
//						double oldx = highlight.getXrotation();
//						double oldy = highlight.getYrotation();
//						double oldz = highlight.getZrotation();
//						double newx = ( r_x - pr_x) * 180 / Math.PI ;
//						double newy = ( r_y - pr_y) * 180 / Math.PI;
//						double newz = ( r_z - pr_z) * 180 / Math.PI;
//
//						highlight.setXrotation(newx);
//						highlight.setYrotation(newy);
//						highlight.setZrotation(newz);
//						workspace.undoManager.pushNodeRotationChange(highlight, oldx, oldy, oldz, newx, newy, newz);

						
//						double oldx = p.getXrotation();
//						double oldy = p.getYrotation();
//						double oldz = p.getZrotation();
//						
//
//						p.setXrotation(newx_angle);
//						p.setYrotation(newy_angle);
//						p.setZrotation(newz_angle);
//						workspace.undoManager.pushNodeRotationChange(p, oldx, oldy, oldz, newx_angle, newy_angle, newz_angle);

												
						
						
						workspace.refreshWorkspace();
					}
					// prevX = me.getX();
					// prevY = me.getY();
					repaint();
				}
			};
			this.addMouseListener(ma);
			this.addMouseMotionListener(ma);
		}

		public void update(Skeleton data) {
			stop();
			this.setSkeleton(data); 
			frameTimesField.setText(data.getMotion().getFrameTime() +"");
			highlight = null;
			setFrameIndex(0);
			repaint();
		}

		public void highlight(Node data) {
			stop();
			this.highlight = data;
			repaint();
		}

		public void play() {
			if (task != null) {
				task.cancel();
				task = null;
			}
			task = new TimerTask() {

				@Override
				public void run() {
					update();
					repaint();
				}
			};
			double ft =  workspace.skeleton.getMotion().getFrameTime();
			new Timer().scheduleAtFixedRate(task, 100, (int)(ft * 1000));
		}

		public void stop() {
			if (task != null) {
				task.cancel();
				task = null;
			}
		}

		public void setFrameIndex(int pos) {
			frameIndex = pos;
			if (frameIndex > getSkeleton().getFrameSize() - 1) {
				frameIndex = 0;
			}
			getSkeleton().setPose((int) frameIndex);
			repaint();
			workspace.updateFrameIndexSilently(pos);
		}
 
		private void update() {
			setFrameIndex(frameIndex + 1);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Skeleton skeleton = this.getSkeleton();
			double scale = getScale();
			Graphics2D g2d = (Graphics2D) g;
			if (skeleton == null)
				return;
			Rectangle r = calculateSize(skeleton, scale);
			int w = getWidth();
			int h = getHeight();
			tx = (w - r.width) / 2 - r.x;
			ty = (h - r.height) / 2 - r.y;
			locs.clear();
			node4loc.clear();
			paintFigure(g2d, skeleton, highlight, tx, ty, scale, locs, node4loc);
		}

	}

}
