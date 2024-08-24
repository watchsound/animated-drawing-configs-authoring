package r9.bvh.simpe.utils; 

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent; 

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.Timer;

public class LabelButton extends JLabel {

	public static interface Callback {
		public void mouseClicked(MouseEvent e);
	}

	private static final long serialVersionUID = 1L;

	Icon iconSelected;
	Icon icon;
	Callback _listener;
	int inertia;

	private String tip;

	private String selectedTip;
	
	private String selectedText;
	private String text;
	//long lastActionTime;
	public LabelButton(String text, Icon icon, Icon iconSelected, boolean textLeading, Callback listener) {
		this(text, icon, iconSelected, textLeading, 1000, listener);
	}
	public LabelButton(String text, Icon icon, Icon iconSelected, boolean textLeading, int inertia, Callback listener) {
		this( text, icon, null, iconSelected, textLeading, inertia, listener);
	}
	/**
	 * 
	 * @param text
	 * @param icon
	 * @param iconSelected
	 * @param textLeading
	 * @param inertia  in minisecond..   minimal time between two actions. 
	 * @param listener
	 */
	public LabelButton(String text, Icon icon, String selectedText, Icon iconSelected, boolean textLeading, int inertia, Callback listener) {
		super(text, icon, CENTER);// textLeading ? LEADING : TRAILING);
		this.selectedText = selectedText;
		this.text = text;
		setHorizontalTextPosition(textLeading ? LEADING : TRAILING);
		this.iconSelected = iconSelected;
		this.icon = icon;
		this._listener = listener;
		this.inertia = inertia;
		setSelected(false);
		registerMouseListener();
	}
	public LabelButton(String text) {
		this(text, null, null, false, null);
	}
	public void setActionListener(final ActionListener listener) {  
		this._listener = new Callback() {  
			public void mouseClicked(MouseEvent e) {
			//	if(! isEnabled() ) return;
				 listener.actionPerformed(new ActionEvent(e.getSource(),e.getID(), "")) ;
			} 
		};
		registerMouseListener();
	}
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
	}
	
	
	private void registerMouseListener() {

		this.addMouseListener(new MouseAdapter() {
			Cursor prevone;

			public void mouseEntered(MouseEvent e) {
				prevone = getCursor();
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			public void mouseExited(MouseEvent e) {
				setCursor(prevone);
			}

			public void mouseClicked(MouseEvent e) {
				if( !isEnabled() )
					return;
				setEnabled(false);
				Timer timer = new Timer(inertia, new java.awt.event.ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						setEnabled(true);
						}});
				timer.start();
			  
				
				toggleSelected();
				if (iconSelected != null) {
					setIcon(isSelected() ? iconSelected : icon);
				}
				if (_listener != null)
					_listener.mouseClicked(e);

			}
		});
	}

	public String getSelectedText() {
		return selectedText;
	}
	public void setSelectedText(String selectedText) {
		this.selectedText = selectedText;
	}
	public void setCallback(Callback listener) {
		this._listener = listener;
	}

	public void toggleSelected() {
		setSelected(!isSelected());
	}

	public void setSelected(boolean selected) {
		putClientProperty("_selected_", selected);
		if (iconSelected != null) 
		    this.setIcon(selected? this.iconSelected : this.icon);
		if( selected ) {
			if( selectedTip != null ) this.setToolTipText(selectedTip);
			if( this.selectedText != null  ) this.setText(this.selectedText);
		} else {
			if( tip != null ) this.setToolTipText(tip);
			if( this.text != null  ) this.setText(this.text);
		}
	}

	public boolean isSelected() {
		return (boolean) this.getClientProperty("_selected_");
	}

	public void setToolTipText(String tip, String selectedTip) {
		this.tip = tip;
		this.selectedTip = selectedTip;
	}
	public void packIconSize() {
		int w = this.getIcon().getIconWidth();
		int h = this.getIcon().getIconHeight();
		setPreferredSize(new Dimension(w, h));
		setMaximumSize(new Dimension(w, h));
		setMinimumSize(new Dimension(w, h));
	}

	public LabelButton(String text, Icon icon, Icon iconSelected, Callback listener) {
		this(text, icon, iconSelected, false, listener);
	}

	public LabelButton(String text, boolean textLeading, Callback listener) {
		this(text, null, null, textLeading, listener);
	}

	public LabelButton(String text, Icon icon) {
		this(text, icon, null, false, null);
	}

	public LabelButton(Icon icon) {
		this("", icon, null, false, null);
	}

	public LabelButton(String text, Callback listener) {
		this(text, null, null, false, listener);
	}

	public LabelButton(Icon image, boolean textLeading, Callback listener) {
		this(null, image, null, textLeading, listener);
	}

	public LabelButton(Icon image, Callback listener) {
		this(null, image, null, false, listener);
	}

	public LabelButton(Callback listener) {
		this("", null, null, false, listener);
	}

}
