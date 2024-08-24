package r9.bvh.simple.ui.fbfig;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import r9.bvh.simpe.utils.R9Properties;
import r9.bvh.simple.model.NodeBase;

public class FigBasePanel extends JPanel{
 
	private static final long serialVersionUID = 1L;
	private CharacterConfig skeleton;
	protected NodeBase<NodeBase> highlight;
	protected List<NodeBase<NodeBase>> selections = new ArrayList<>();
	private double scale = 1;
	private boolean useScale;
	public FigBasePanel() {}
	public FigBasePanel(boolean useScale) {
		this.useScale = useScale;
	}
	
	public boolean isUseScale() {
		return useScale;
	}
	public void setUseScale(boolean useScale) {
		this.useScale = useScale;
	}
	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
		resize();
	}
	
	
	public CharacterConfig getSkeleton() {
		return skeleton;
	}

	public void setSkeleton(CharacterConfig skeleton) {
		this.skeleton = skeleton;
		resize();
	}

	public void highlight(NodeBase<NodeBase> data) {
		this.highlight = data;
		repaint();
	}
	public void resize() {
		if( skeleton == null || !useScale ) return;
		 Rectangle s = calculateSize(  skeleton,   scale) ;
		 this.setSize((int)(  s.width + 200), (int)( s.height + 200));
		 this.setPreferredSize(new Dimension(  s.width + 200 ,   s.height + 200));
		 this.getParent().invalidate();
		 this.getParent().repaint();
	}
	
	public Rectangle calculateSize(CharacterConfig skeleton, double scale) {
		if( skeleton == null)
			return new Rectangle ();
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double maxY = Double.MIN_VALUE;
		for (int n = 0; n < skeleton.getNodes().size(); n++) {
			NodeBase<NodeBase> node = skeleton.getNodes().get(n);
			int x1 = (int) (scale * node.getPosition().getX());
			int y1 = (int) (scale * node.getPosition().getY());
			if (minX > x1)
				minX = x1;
			if (minY > y1)
				minY = y1;
			if (maxX < x1)
				maxX = x1;
			if (maxY < y1)
				maxY = y1;

			for (NodeBase child : node.getChildrens()) {
				int x2 = (int) (scale * child.getPosition().getX());
				int y2 = (int) (scale * child.getPosition().getY());
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
	public void paintFigure(Graphics2D g2d, CharacterConfig skeleton, NodeBase<NodeBase> highlight, double tx, double ty,
			double scale) {
		paintFigure(g2d, skeleton, highlight, tx, ty, scale, null, null);
	}

	public void paintFigure(Graphics2D g2d, CharacterConfig skeleton, NodeBase<NodeBase> highlight, double tx, double ty,
			double scale, List<Rectangle> locs, List<NodeBase> node4loc) {
        if( skeleton == null) return;
		g2d.translate(tx, ty);
		R9Properties props = R9Properties.getSharedProperties();
		int dotSize = props.dotSize();
		for (int n = 0; n < skeleton.getNodes().size(); n++) {
			NodeBase<NodeBase> node = skeleton.getNodes().get(n);
			int x1 = (int) (scale * node.getPosition().getX());
			int y1 = (int) (-scale * node.getPosition().getY());

			g2d.setColor(node == highlight ? Color.RED : Color.BLACK);
			g2d.fillOval((int) (x1 - dotSize/2), (int) (y1 - dotSize/2), dotSize, dotSize);
			if (locs != null) {
				locs.add(new Rectangle(x1 - dotSize/2 + (int) tx, y1 - dotSize/2 + (int) ty, dotSize, dotSize));
				node4loc.add(node);
			}
			if (node == highlight && node.getParent() != null) {
				NodeBase p = node.getParent();
				int xp = (int) (scale * p.getPosition().getX());
				int yp = (int) (-scale * p.getPosition().getY());
				int radius = (int) (node.getDistanceToParent() * scale);
				// AffineTransform ot = g2d.getTransform();
				// g2d.setTransform(p.getTransform().toShear2DTransform( ));
				g2d.drawOval(xp - radius, yp - radius, radius * 2, radius * 2);
				// g2d.setTransform(ot);

				g2d.setColor(Color.green);
				g2d.fillOval((int) (xp - dotSize/2), (int) (yp - dotSize/2), dotSize, dotSize);

			}
			g2d.setColor(Color.BLACK);
			// g2d.drawString(node.getName(), x1 + 10, y1);

			for (NodeBase child : node.getChildrens()) {
				int x2 = (int) (scale * child.getPosition().getX());
				int y2 = (int) (-scale * child.getPosition().getY());
				g2d.drawLine(x1, y1, x2, y2);
			}
		}
		g2d.translate(-tx, -ty);
	}
	
	public void multipleSelections(Rectangle2D region ) {
		if( skeleton == null) return;
	    selections.clear();
		for (int n = 0; n < skeleton.getNodes().size(); n++) {
			NodeBase<NodeBase> node = skeleton.getNodes().get(n);
			int x1 = (int) (scale * node.getPosition().getX());
			int y1 = (int) (scale * node.getPosition().getY());
			if( region.contains(x1,y1) )
				selections.add(node);
		}
	}
	
	public void mapFigSpaceToCanvas() {
		int w = getWidth();
		int h = getHeight();
		if( skeleton == null) return;
		Rectangle r = calculateSize(skeleton, scale);

		int tx = (w - r.width) / 2 - r.x;
		int ty = (h - r.height) / 2 - r.y;

		for (int n = 0; n < skeleton.getNodes().size(); n++) {
			NodeBase<NodeBase> node = skeleton.getNodes().get(n);
			int x1 = (int) (scale * node.getPosition().getX());
			int y1 = (int) (scale * node.getPosition().getY());
			node.getPosition().setX(tx + x1);
			node.getPosition().setY(ty + y1);
		}
	}
}
