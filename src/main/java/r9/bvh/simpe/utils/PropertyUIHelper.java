package r9.bvh.simpe.utils;
 
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
 

import javax.swing.Box.Filler;

public class PropertyUIHelper {
	public static JComponent createVerticalFill() {
		return new Filler(new Dimension(0, 0), new Dimension(0, Short.MAX_VALUE), new Dimension(0, Short.MAX_VALUE));
	}

	public static JComponent createHorizontalFill() {
		return new Filler(new Dimension(0, 0), new Dimension(Short.MAX_VALUE, 0), new Dimension(Short.MAX_VALUE, 0));
	}

	public static JComponent createTitleRow(String title) {
		return createTitleRow(title, false);
	}

	public static JComponent createTitleRow(String title, boolean bold) {
		return createTitleRow(title, bold, null);
	}

	public static JComponent createTitleRow(String title, boolean bold, Color bgColor) {
		return createTitleRow(title, bold, bgColor, false);
	}

	public static JComponent createTitleRow(String title, boolean bold, Color bgColor, boolean right) {
		return createTitleRow(title, null, bold, bgColor, right);
	}
	public static JComponent createTitleRow(String title,  ImageIcon icon, boolean bold, Color bgColor, boolean right) {
		JPanel atitleRow = new R9Panel();
		atitleRow.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
		if (bgColor != null)
			atitleRow.setBackground(bgColor);
		atitleRow.setLayout(new BoxLayout(atitleRow, BoxLayout.X_AXIS));
		JLabel titleLabel = new JLabel(title);
		if( icon != null )
			titleLabel.setIcon(icon);
		if (bold) {
			titleLabel.setFont(new Font("Ariel", Font.BOLD, 13));
		}
		if (right) {
			atitleRow.add(Box.createHorizontalGlue());
			atitleRow.add(titleLabel, Box.RIGHT_ALIGNMENT);
		} else {
			atitleRow.add(titleLabel, Box.LEFT_ALIGNMENT);
			atitleRow.add(Box.createHorizontalGlue());
		}
		if (bold) {
			atitleRow.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(1, 2, 2, 2),
					BorderFactory.createMatteBorder(0, 0, 2, 0, Color.GRAY)));
		} else {
			atitleRow.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(1, 2, 2, 2),
					BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY)));
		}

		return atitleRow;
	}

	public static JComponent createRow(String title, ImageIcon icon, boolean toggleButton, int iconSize,
			ActionListener listener) {
		JLabel label = null;
		if (title != null && title.length() > 0) {
			label = new JLabel(title);
		}
		return createRow(label, true, icon, toggleButton, iconSize, listener);
	}

	public static JComponent createRow(JComponent title, boolean left, ImageIcon icon, boolean toggleButton, int iconSize,
			ActionListener listener) {
		AbstractButton button = toggleButton ? new JToggleButton(icon) : new JButton(icon);
		button.setPreferredSize(new Dimension(iconSize, iconSize));
		button.setMaximumSize(new Dimension(iconSize, iconSize));
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setIconTextGap(0);
		button.addActionListener(listener);
		if (left)
			return createRow(title, button);
		else
			return createRow("", title, button);
	}

	public static JComponent createTitleRow(String title, boolean bold, Color bgColor, String tips) {
		JPanel atitleRow = (JPanel) createTitleRow(title, bold, bgColor);

		JLabel titleLabel = new JLabel(tips);
		titleLabel.setFont(new Font("Ariel", Font.PLAIN, 12));
		atitleRow.add(titleLabel, Box.RIGHT_ALIGNMENT);
		return atitleRow;
	}

	public static JComponent createTitleRow(String title, boolean bold, Color bgColor, JComponent tips) {
		JPanel atitleRow = (JPanel) createTitleRow(title, bold, bgColor);
		atitleRow.add(tips, Box.RIGHT_ALIGNMENT);
		return atitleRow;
	}

	public static JComponent createLine() {
		return createLine(false, 2);
	}

	public static JComponent createLine(int height) {
		return createLine(false, height);
	}

	public static JComponent createLine(boolean forceWhite, final int height) {
		JPanel atitleRow = new R9Panel() {
			private static final long serialVersionUID = 1L;

			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Color c = g.getColor();
				if (forceWhite) {
					g.setColor(Color.WHITE);
					g.fillRect(0, 0, getWidth(), getHeight());
				}
				g.setColor(c);
				int yoffset = (this.getHeight() - height) / 2;
				g.fillRect(5, yoffset, getWidth() - 10, height);
			}
		};

		return atitleRow;
	}

	public static JComponent createDashLine(boolean forceWhite, final int gap, final int height) {
		JPanel atitleRow = new R9Panel() {

			private static final long serialVersionUID = 1L;

			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Color c = g.getColor();
				int w = getWidth();
				if (forceWhite) {
					g.setColor(Color.WHITE);
					g.fillRect(0, 0, w, getHeight());
				}
				g.setColor(c);
				int yoffset = (this.getHeight() - height) / 2;
				if (height == 1) {
					int curpos = 5;
					while (curpos + gap < w) {
						g.drawLine(curpos, yoffset, curpos + gap, yoffset);
						curpos += gap + gap;
					}
				} else {
					int curpos = 5;
					while (curpos + gap < w) {
						g.fillRect(curpos, yoffset, gap, height);
						curpos += gap + gap;
					}
				}
			}
		};

		return atitleRow;
	}

	public static JComponent createEmptyRow(int height) {
		return createRow(new JLabel(""), new JLabel(""), height);
	}

	public static JComponent createRow(String title, JComponent comp, int fixHeight) {
		return createRow(new JLabel(title), comp, fixHeight);
	}

	public static JComponent createRow(JComponent title, JComponent comp, int fixHeight) {
		return createRow(title, comp, fixHeight, 0, 0);
	}

	public static JComponent createRow(JComponent title, JComponent comp, int fixHeight, int leftPadding,
			int rightPadding) {
		JPanel atitleRow = new R9Panel();
		atitleRow.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
		atitleRow.setLayout(new BoxLayout(atitleRow, BoxLayout.X_AXIS));
		if (leftPadding > 0)
			atitleRow.add(Box.createHorizontalStrut(leftPadding), Box.LEFT_ALIGNMENT);
		atitleRow.add(title, Box.LEFT_ALIGNMENT);
		atitleRow.add(Box.createHorizontalGlue());
		atitleRow.add(comp, Box.RIGHT_ALIGNMENT);
		if (rightPadding > 0)
			atitleRow.add(Box.createHorizontalStrut(rightPadding), Box.RIGHT_ALIGNMENT);
		if (fixHeight > 0) {
			atitleRow.setMaximumSize(new Dimension(800, fixHeight));
		}
		return atitleRow;
	}

	public static JComponent createRow(String title, JComponent comp, int fixHeight, int compSize) {
		return createRow(new JLabel(title), comp, fixHeight, compSize);
	}

	public static JComponent createRow(JComponent title, JComponent comp, int fixHeight, int compSize) {
		JPanel atitleRow = (JPanel) createRow(title, comp, fixHeight);
		comp.setMaximumSize(new Dimension(compSize, fixHeight));
		comp.setPreferredSize(new Dimension(compSize, fixHeight));
		return atitleRow;
	}

	public static JComponent createFieldUnit(String unit, JComponent field) {
		JPanel atitleRow = new R9Panel();
		atitleRow.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
		atitleRow.setLayout(new BorderLayout());
		atitleRow.add(new JLabel(unit), BorderLayout.EAST);
		atitleRow.add(field, BorderLayout.CENTER);
		return atitleRow;
	}

	public static JComponent createRow(String title, boolean left) {
		return createRow(title, left, Color.WHITE);
	}

	public static JComponent createRow(String title, boolean left, Color bgColor) {
		return createRow(title, left, bgColor, 0, 0);
	}

	public static JComponent createRow(String title, boolean left, Color bgColor, int leftPadding, int rightPadding) {
		JPanel atitleRow = new R9Panel();
		atitleRow.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
		if (bgColor != null)
			atitleRow.setBackground(bgColor);
		atitleRow.setLayout(new BoxLayout(atitleRow, BoxLayout.X_AXIS));
		if (left) {
			if (leftPadding > 0)
				atitleRow.add(Box.createHorizontalStrut(leftPadding));
			atitleRow.add(new JLabel(title), Box.LEFT_ALIGNMENT);
			atitleRow.add(Box.createHorizontalGlue());
		} else {
			atitleRow.add(Box.createHorizontalGlue());
			atitleRow.add(new JLabel(title), Box.RIGHT_ALIGNMENT);
			if (rightPadding > 0)
				atitleRow.add(Box.createHorizontalStrut(rightPadding));
		}

		return atitleRow;
	}

	public static JComponent createRow(String title, JComponent... comps) {
		JLabel label = null;
		if (title != null && title.length() > 0) {
			label = new JLabel(title);
		}
		return createRow(label, comps);
	}
	public static JComponent createRow(String title, JTextField field, JComponent comp) {
		JPanel atitleRow = new R9Panel();
		atitleRow.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
		atitleRow.setLayout(new BorderLayout());
		if( title != null )
			atitleRow.add(new JLabel(title), BorderLayout.WEST);
		atitleRow.add(field, BorderLayout.CENTER);
		atitleRow.add(comp, BorderLayout.EAST);
		atitleRow.setMaximumSize(new Dimension(1000,30));
		return atitleRow;
	}

	public static JComponent createRow(JComponent title, JComponent... comps) {
		List<JComponent> comps2 = new ArrayList<JComponent>();
		for (JComponent c : comps)
			comps2.add(c);
		return createRow(title, comps2, 5, false, null);
	}

	public static JComponent createRow(JComponent comp, boolean left) {
		return createRow(comp, left, null);
	}

	public static JComponent createRow(JComponent comp, boolean left, Color color) {
		List<JComponent> comps = new ArrayList<JComponent>();
		comps.add(comp);
		return createRow(comps, 5, left, color);
	}

	public static JComponent createRow(JComponent comp1, JComponent comp2, boolean left, Color color) {
		return createRow("", comp1, comp2, left, color);
	}

	public static JComponent createRow(String title, JComponent comp1, JComponent comp2, boolean left, Color color) {
		List<JComponent> comps = new ArrayList<JComponent>();
		comps.add(comp1);
		comps.add(comp2);
		return createRow(title, comps, 5, left, color);
	}

	public static JComponent createRow(String title, JComponent comp1, JComponent comp2, JComponent comp3, boolean left,
			Color color) {
		List<JComponent> comps = new ArrayList<JComponent>();
		comps.add(comp1);
		comps.add(comp2);
		comps.add(comp3);
		return createRow(title, comps, 5, left, color);
	}

	public static JComponent createRow(JComponent comp1, JComponent comp2, JComponent comp3, boolean left,
			Color color) {
		List<JComponent> comps = new ArrayList<JComponent>();
		comps.add(comp1);
		comps.add(comp2);
		comps.add(comp3);
		return createRow(comps, 5, left, color);
	}

	public static JComponent createRow(boolean left, Color color, JComponent... comps) {
		List<JComponent> comps2 = new ArrayList<JComponent>();
		for (JComponent com : comps)
			comps2.add(com);
		return createRow(comps2, 5, left, color);
	}

	public static JComponent createRow(List<JComponent> comps, int gap, boolean left, Color color) {
		return createRow("", comps, gap, left, color);
	}

	public static JComponent createRow(String title, List<JComponent> comps) {
		return createRow(title, comps, 5, false, null);
	}

	public static JComponent createRow(JComponent title, List<JComponent> comps) {
		return createRow(title, comps, 5, false, null);
	}

	public static JComponent createRow(String title, List<JComponent> comps, int gap, boolean left, Color color) {
		JLabel label = null;
		if (title != null && title.length() > 0) {
			label = new JLabel(title);
		}
		return createRow(label, comps, gap, left, color);
	}
	

	public static JComponent createRow(JComponent title, List<JComponent> comps, int gap, boolean left, Color color) {
		JPanel atitleRow = new R9Panel();
		atitleRow.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
		if (color != null)
			atitleRow.setBackground(color);
		// else
		// atitleRow.setBackground(Color.white);
		atitleRow.setLayout(new BoxLayout(atitleRow, BoxLayout.X_AXIS));
		if (title != null) {
			atitleRow.add(title, Box.LEFT_ALIGNMENT);
		}
		if (left) {
			for (JComponent comp : comps) {
				atitleRow.add(comp, Box.LEFT_ALIGNMENT);
				atitleRow.add(Box.createHorizontalStrut(gap));
			}
			atitleRow.add(Box.createHorizontalGlue());
		} else {
			atitleRow.add(Box.createHorizontalGlue());
			for (JComponent comp : comps) {
				atitleRow.add(comp, Box.RIGHT_ALIGNMENT);
				atitleRow.add(Box.createHorizontalStrut(gap));
			}
		}
		return atitleRow;
	}
	
	public static JComponent createRow(List<JComponent> lcomps, List<JComponent> rcomps, int gap, Color color) {
		JPanel atitleRow = new R9Panel();
		atitleRow.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
		if (color != null)
			atitleRow.setBackground(color);
		// else
		// atitleRow.setBackground(Color.white);
		atitleRow.setLayout(new BoxLayout(atitleRow, BoxLayout.X_AXIS));
		if (lcomps != null) {
			for (JComponent comp : lcomps) {
				atitleRow.add(comp, Box.LEFT_ALIGNMENT);
				atitleRow.add(Box.createHorizontalStrut(gap));
			} 
		} 
	    atitleRow.add(Box.createHorizontalGlue());
	    if (rcomps != null) {
			for (JComponent comp : rcomps) {
				atitleRow.add(comp, Box.RIGHT_ALIGNMENT);
				atitleRow.add(Box.createHorizontalStrut(gap));
			} 
		}
		return atitleRow;
	}

	public static JComponent createRow(JComponent left, JComponent right, Color color) {
		JPanel atitleRow = new R9Panel();
		atitleRow.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
		if (color != null)
			atitleRow.setBackground(color);
		atitleRow.setLayout(new BoxLayout(atitleRow, BoxLayout.X_AXIS));
		atitleRow.add(left, Box.LEFT_ALIGNMENT);
		atitleRow.add(createHorizontalFill());
		atitleRow.add(right, Box.RIGHT_ALIGNMENT);
		return atitleRow;
	}
}
