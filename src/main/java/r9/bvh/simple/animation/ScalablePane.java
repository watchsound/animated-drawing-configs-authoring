package r9.bvh.simple.animation;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JPanel;
 

public abstract class ScalablePane extends JPanel{
 
	private static final long serialVersionUID = 1L;
	private double scale = 1;
	
	 
	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
		resize();
	}
	 
	public void resize() { 
		 Rectangle s = calculateSize( scale) ;
		 this.setSize((int)(  s.width + 200), (int)( s.height + 200));
		 this.setPreferredSize(new Dimension(  s.width + 200 ,   s.height + 200));
		 if( this.getParent() == null ) return;
		 this.getParent().invalidate();
		 this.getParent().repaint();
	}

	protected abstract Rectangle calculateSize(double scale2);
}
