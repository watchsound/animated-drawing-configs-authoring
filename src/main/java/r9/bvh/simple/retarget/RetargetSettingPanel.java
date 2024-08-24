package r9.bvh.simple.retarget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import r9.bvh.simpe.utils.PropertyUIHelper;
import r9.bvh.simpe.utils.StringUtils;
import r9.bvh.simple.model.NodeBase;

public class RetargetSettingPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	
	RetargetWorkspace workspace;

	private JTextField locXField;

	private JTextField locYField;

	private JTextField locZField;

	private JPanel checkListPane;

	private JButton saveButton;

	//private Retarget retarget;
	 
	 
	public RetargetSettingPanel(RetargetWorkspace workspace) {
		this.workspace = workspace;
		this.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(5, 5, 5, 5) ,
				BorderFactory.createCompoundBorder( 
				   BorderFactory.createLineBorder(Color.black),
				   BorderFactory.createEmptyBorder(5,5,5,5))));
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		this.add(PropertyUIHelper.createTitleRow("char_starting_location"));
		locXField = new JTextField(10);
		locYField = new JTextField(10);
		locZField = new JTextField(10);
		
		this.add(PropertyUIHelper.createRow("x loc", new JLabel(), locXField));
		this.add(PropertyUIHelper.createRow("y loc", new JLabel(), locYField));
		this.add(PropertyUIHelper.createRow("z loc", new JLabel(), locZField));
		
		
		this.add(PropertyUIHelper.createTitleRow("char_runtime_checks"));
		
		checkListPane = new JPanel();
		checkListPane.setLayout(new BoxLayout(checkListPane, BoxLayout.Y_AXIS));
		
		JPanel checkListPaneWrap = new JPanel();
		checkListPaneWrap.setLayout(new BorderLayout());
		checkListPaneWrap.add(checkListPane, BorderLayout.WEST);
		
		this.add(checkListPaneWrap);
		 
		saveButton = new JButton("保存");
		saveButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				workspace.save();
			}});
		this.add(PropertyUIHelper.createLine());
		this.add(PropertyUIHelper.createRow("", saveButton));
		this.add(PropertyUIHelper.createVerticalFill());
		 
	}
	public void save() {
		String v = this.locXField.getText();
		RetargetConfig retarget = workspace.curRetarget;
		if( !StringUtils.isEmpty(v)) {
			 retarget.char_starting_location[0] = Double.parseDouble(v);
		}
		v = this.locYField.getText();
		if( !StringUtils.isEmpty(v)) {
			 retarget.char_starting_location[1] = Double.parseDouble(v);
		}
		v = this.locZField.getText();
		if( !StringUtils.isEmpty(v)) {
			 retarget.char_starting_location[2] = Double.parseDouble(v);
		}
		saveModelByCheckUI(); 
	}
	public void createCheckerUI(List<String> charNodes) {
		this.checkListPane.removeAll();
		for(String v : charNodes) {
			final JCheckBox cbox = new JCheckBox(v);
			this.checkListPane.add(cbox);
		}
	}
	public void updateChecksByModel() {
		RetargetConfig retarget = workspace.curRetarget;
		if( retarget.cfig != null) {
			this.checkListPane.removeAll();
			for(NodeBase v : retarget.cfig.getNodes()) {
				final JCheckBox cbox = new JCheckBox(v.getName());
				this.checkListPane.add(cbox);
			}
		}
		if( retarget.getChar_runtime_checks().size() > 0 ) {
			List<String> checks = retarget.getChar_runtime_checks().get(0);
			for(Component comp : this.checkListPane.getComponents()) {
				if( comp instanceof JCheckBox) {
					JCheckBox box = (JCheckBox)comp;
					box.setSelected(checks.contains(box.getText()));
				}
			}
		} 
		Dimension d = this.workspace.getSize();
		Random r = new Random(System.currentTimeMillis());
		this.workspace.setSize(new Dimension(d.width+ (r.nextInt(2)==0 ? -1 : 1), d.height));
	}
	public void saveModelByCheckUI() { 
		List<String> checks = new ArrayList<>();
		for(Component comp : this.checkListPane.getComponents()) {
			if( comp instanceof JCheckBox) {
				JCheckBox box = (JCheckBox)comp;
			    if( box.isSelected() )
			    	checks.add(box.getText());
			}
		}
		RetargetConfig retarget = workspace.curRetarget;
		if( retarget.getChar_runtime_checks().size() > 0 ) {
			retarget.getChar_runtime_checks().set(0, checks);
		} else {
			retarget.getChar_runtime_checks().add(checks);
		}
	}
	public void update(RetargetConfig retarget) {
		//this.retarget = retarget;
		if( retarget.char_starting_location != null ) {
			this.locXField.setText( retarget.char_starting_location[0] + "");
			this.locYField.setText( retarget.char_starting_location[1] + "");
			this.locZField.setText( retarget.char_starting_location[2] + "");
		}
		updateChecksByModel();
	}
}
