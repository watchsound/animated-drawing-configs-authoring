package r9.bvh.simple;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
 
import r9.bvh.simpe.utils.PropertyUIHelper;
import r9.bvh.simpe.utils.R9Properties;

public class ProjectSettingDialog extends JDialog implements PropertyChangeListener { 
	private static final long serialVersionUID = 1L;
	private JLabel dotSizeLabel;
	private JSlider dotSizeSlider;
	private String cancel = "取消";
	private String save = "保存";
	private JOptionPane optionPane;

	
	public ProjectSettingDialog() {
		JPanel contentPane = new JPanel();
		contentPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(5, 5, 5, 5) ,
				BorderFactory.createCompoundBorder( 
				   BorderFactory.createLineBorder(Color.black),
				   BorderFactory.createEmptyBorder(5,5,5,5))));
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		
		R9Properties props = R9Properties.getSharedProperties();
		
		contentPane.add(PropertyUIHelper.createTitleRow("UI Setting"));
		
		dotSizeLabel = new JLabel();
		dotSizeSlider = new JSlider();
		dotSizeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int v = dotSizeSlider.getValue(); 
				dotSizeLabel.setText(v + "");
			}
		});
		dotSizeSlider.setMinimum(1);
		dotSizeSlider.setMaximum(30);
		dotSizeSlider.setValue(props.dotSize()); // zoom level is one
		dotSizeSlider.setPreferredSize(new Dimension(200, 25));
		contentPane.add(PropertyUIHelper.createRow("骨骼节点大小", dotSizeLabel, dotSizeSlider));
		
		contentPane.add(PropertyUIHelper.createVerticalFill());

		Object[] array = { contentPane };

		// Create an array specifying the number of dialog buttons
		// and their text.
		Object[] options = { cancel, save };

		// Create the JOptionPane.
	    optionPane = new JOptionPane(array, JOptionPane.PLAIN_MESSAGE, JOptionPane.YES_NO_OPTION, null, options,
				options[0]);

		// Make this dialog display it.
		setContentPane(optionPane); 
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) { 
				optionPane.setValue(JOptionPane.CLOSED_OPTION);
			}
		}); 
		optionPane.addPropertyChangeListener(this);
		
	}
	
	
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		String prop = e.getPropertyName();

		if (isVisible() && (e.getSource() == optionPane)
				&& (JOptionPane.VALUE_PROPERTY.equals(prop) || JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
			Object value = optionPane.getValue();

			if (value == JOptionPane.UNINITIALIZED_VALUE) {
				// ignore reset
				return;
			}

		 
			optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);

			if (save.equals(value)) {
				R9Properties props = R9Properties.getSharedProperties();
				props.dotSize(dotSizeSlider.getValue());
				props.save(); 
				setVisible(false);
			} else {
				setVisible(false);
			}
		}
	}
}
