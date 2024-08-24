package r9.bvh.simpe.utils;
 

import java.awt.Component;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;
 
import javax.swing.JPanel;

public class R9Panel extends JPanel {

	private static final long serialVersionUID = 1L;

	private List<Component> disabledComps;
	
	public R9Panel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
	}

	public R9Panel(LayoutManager layout) {
		super(layout);
	}

	public R9Panel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
	}

	public R9Panel() {
		super();
	}

	public void disableAll() {
		if( disabledComps == null ) disabledComps = new ArrayList<>();
		disabledComps.clear();
		for(Component  comp: this.getComponents()) {
			if( !comp.isEnabled() || !comp.isVisible() ) continue;
			comp.setEnabled(false);
			disabledComps.add(comp);
			if( comp instanceof R9Panel ) {
				((R9Panel)comp).disableAll();
			}
		}
		this.setEnabled(false);
		this.disabledComps.add(this);
	}
	public void undoDisableAll() {
		if( disabledComps == null || disabledComps.isEmpty() ) return;
		for(Component  comp: this.getComponents()) {
			 comp.setEnabled(true);
			if( (comp != this) && (comp instanceof R9Panel) ) {
				((R9Panel)comp).undoDisableAll();
			}
		}
		this.disabledComps.clear();
	}
	
}
