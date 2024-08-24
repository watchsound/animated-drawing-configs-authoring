package r9.bvh.simple;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import r9.bvh.simpe.utils.R9Properties;
import r9.bvh.simple.model.Node;
import r9.bvh.simple.model.Skeleton;

public class BVHBasePanel extends JPanel{
 
	private static final long serialVersionUID = 1L;
	private Skeleton skeleton;
	protected Node highlight;
	private double scale = 10;
	protected List<Rectangle> locs = new ArrayList<>();
	protected List<Node> node4loc = new ArrayList<>();

	protected int tx; 
	protected  int ty;
	
	public void highlight(Node data) { 
		this.highlight = data;
		repaint();
	}

 
	public Skeleton getSkeleton() {
		return skeleton;
	}


	public void setSkeleton(Skeleton skeleton) {
		this.skeleton = skeleton;
		resize();
	}


	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
		resize();
	}
	
	public void resize() {
		if( skeleton == null ) return;
		 Rectangle s = calculateSize(  skeleton,   scale) ;
		 this.setSize((int)(  s.width + 200), (int)( s.height + 200));
		 this.setPreferredSize(new Dimension(  s.width + 200 ,   s.height + 200));
		 this.getParent().invalidate();
		 this.getParent().repaint();
	}

	
	public static void paintFigure(Graphics2D g2d, Skeleton skeleton, Node highlight, double tx, double ty,
			double scale) {
		paintFigure(g2d, skeleton, highlight, tx, ty, scale, null, null);
	}

	public static void paintFigure(Graphics2D g2d, Skeleton skeleton, Node highlight, double tx, double ty,
			double scale, List<Rectangle> locs, List<Node> node4loc) {

		g2d.translate(tx, ty);
		R9Properties props = R9Properties.getSharedProperties();
		int dotSize = props.dotSize();
		for (int n = 0; n < skeleton.getNodes().size(); n++) {
			Node node = skeleton.getNodes().get(n);
			int x1 = (int) (scale * node.getPosition().getX());
			int y1 = (int) (-scale * node.getPosition().getY());

			g2d.setColor(node == highlight ? Color.RED : Color.BLACK);
			g2d.fillOval((int) (x1 - dotSize/2), (int) (y1 - dotSize/2), dotSize, dotSize);
			if (locs != null) {
				locs.add(new Rectangle(x1 - dotSize/2 + (int) tx, y1 - dotSize/2 + (int) ty, dotSize, dotSize));
				node4loc.add(node);
			}
			if (node == highlight && node.getParent() != null) {
				Node p = node.getParent();
				int xp = (int) (scale * p.getPosition().getX());
				int yp = (int) (-scale * p.getPosition().getY());
				int radius = (int) (node.getDistanceToParent() * scale);
			//	AffineTransform ot = g2d.getTransform(); 
			//	g2d.setTransform(p.getTransform().toShear2DTransform( )); 
				g2d.drawOval(xp - radius, yp - radius, radius * 2, radius * 2); 
			//	g2d.setTransform(ot);
				
				g2d.setColor(Color.green); 
				g2d.fillOval((int) (xp - dotSize/2), (int) (yp - dotSize/2), dotSize, dotSize);
				
			}
			g2d.setColor(Color.BLACK);
			// g2d.drawString(node.getName(), x1 + 10, y1);

			for (Node child : node.getChildrens()) {
				int x2 = (int) (scale * child.getPosition().getX());
				int y2 = (int) (-scale * child.getPosition().getY());
				g2d.drawLine(x1, y1, x2, y2);
			}
		}
		g2d.translate(-tx, -ty);
	}

	public static Rectangle calculateSize(Skeleton skeleton, double scale) {
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double maxY = Double.MIN_VALUE;
		for (int n = 0; n < skeleton.getNodes().size(); n++) {
			Node node = skeleton.getNodes().get(n);
			int x1 = (int) (scale * node.getPosition().getX());
			int y1 = (int) (-scale * node.getPosition().getY());
			if (minX > x1)
				minX = x1;
			if (minY > y1)
				minY = y1;
			if (maxX < x1)
				maxX = x1;
			if (maxY < y1)
				maxY = y1;

			for (Node child : node.getChildrens()) {
				int x2 = (int) (scale * child.getPosition().getX());
				int y2 = (int) (-scale * child.getPosition().getY());
				if (minX > x2)
					minX = x2;
				if (minY > y2)
					minY = y2;
				if (maxX < x2)
					maxX = x2;
				if (maxY < y2)
					maxY = y2;
			}
		}
		return new Rectangle((int) minX, (int) minY, (int) maxX - (int) minX, (int) maxY - (int) minY);
	}

}
