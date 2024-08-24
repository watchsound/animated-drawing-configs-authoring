package r9.bvh.simple;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import r9.bvh.simpe.utils.PropertyUIHelper;
import r9.bvh.simpe.utils.StringUtils;
import r9.bvh.simple.BVHTreeView.CALLBACK;
import r9.bvh.simple.model.Motion.MFrame;
import r9.bvh.simple.model.Node;
import r9.bvh.simple.model.Skeleton;

public class NodeAnimationSettingPanel extends JPanel {
 
	private static final long serialVersionUID = 1L;
	private CALLBACK callback;
	private Node node;
	private Skeleton skeleton;
	private JTextField offsetXField;
	private JTextField offsetYField;
	private JTextField offsetZField;
	private JLabel channelsLabel;
	private JLabel nameLabel;
	private JSlider zrotationSlider;
	private JSlider yrotationSlider;
	private JSlider xrotationSlider;
	private JTextField zrotationField;
	private JTextField yrotationField;
	private JTextField xrotationField;
	private JButton saveButton;
	private Workspace workspace;
	private JButton redoButton;
	private JButton undoButton;

	public NodeAnimationSettingPanel(Workspace workspace, BVHTreeView.CALLBACK callback) {
		this.callback = callback;
		this.workspace = workspace;
		this.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(5, 5, 5, 5) ,
				BorderFactory.createCompoundBorder( 
				   BorderFactory.createLineBorder(Color.black),
				   BorderFactory.createEmptyBorder(5,5,5,5))));
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		offsetXField = new JTextField(10);
		offsetYField = new JTextField(10);
		offsetZField = new JTextField(10);
		channelsLabel = new JLabel();
		nameLabel = new JLabel();
		zrotationField = new JTextField( 10);
		yrotationField = new JTextField( 10);
		xrotationField = new JTextField( 10);
		
		
		zrotationSlider = new JSlider();
		zrotationSlider.addChangeListener(new ChangeListener() { 
			public void stateChanged(ChangeEvent e) {
				zrotationField.setText(zrotationSlider.getValue()+"");
			}});
		zrotationSlider.setMinimum(-90);
		zrotationSlider.setMaximum(90); 
		yrotationSlider = new JSlider();
		yrotationSlider.addChangeListener(new ChangeListener() { 
			public void stateChanged(ChangeEvent e) {
				yrotationField.setText(yrotationSlider.getValue()+"");
			}});
		yrotationSlider.setMinimum(-90);
		yrotationSlider.setMaximum(90); 
		xrotationSlider = new JSlider();
		xrotationSlider.addChangeListener(new ChangeListener() { 
			public void stateChanged(ChangeEvent e) {
				xrotationField.setText(xrotationSlider.getValue()+"");
			}});
		xrotationSlider.setMinimum(-90);
		xrotationSlider.setMaximum(90); 
		
		
		saveButton = new JButton("保持修改");
		saveButton.addActionListener(new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent e) {
				 saveUIState();
			}});
		
		add(PropertyUIHelper.createRow("名称", nameLabel));
		add(PropertyUIHelper.createRow("OFFSET-X", offsetXField, 28, 100));
		add(PropertyUIHelper.createRow("OFFSET-Y", offsetYField, 28, 100));
		add(PropertyUIHelper.createRow("OFFSET-Z", offsetZField, 28, 100));
		add(PropertyUIHelper.createTitleRow("CHANNELS"  ));
		add(PropertyUIHelper.createRow(" ", channelsLabel));
		add(PropertyUIHelper.createRow("Zrotation" , zrotationField, 28, 100));
		add(zrotationSlider);
		add(PropertyUIHelper.createRow("Yrotation", yrotationField, 28, 100));
		add(yrotationSlider);
		add(PropertyUIHelper.createRow("Xrotation", xrotationField, 28, 100));
		add(xrotationSlider);
		add(PropertyUIHelper.createRow(" ", saveButton));
		
		add(PropertyUIHelper.createLine());
		redoButton = new JButton("Redo");
		undoButton = new JButton("Undo"); 
		redoButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				if( workspace.undoManager.canRedo() )
					workspace.undoManager.stepForwards();
			}});
		undoButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				if( workspace.undoManager.canUndo() )
					workspace.undoManager.stepBackwards();
			}});
		add(PropertyUIHelper.createRow(" ", redoButton, undoButton));
		
		add(PropertyUIHelper.createVerticalFill());
		
	}
	public void saveUIState() {
		if( node == null) return;
		int frameIndex = skeleton.getFrameIndex();
		if( frameIndex == 0 ) {
			node.getOffset().setX(Double.parseDouble(offsetXField.getText()));
			node.getOffset().setY(Double.parseDouble(offsetYField.getText()));
			node.getOffset().setZ(Double.parseDouble(offsetZField.getText()));
		}
		double oldx = node.getXrotation();
		double oldy = node.getYrotation();
		double oldz = node.getZrotation();
		double newx = Double.parseDouble(xrotationField.getText());
		double newy = Double.parseDouble(yrotationField.getText());
		double newz = Double.parseDouble(zrotationField.getText());
		
		node.setXrotation(newx);
		node.setYrotation(newy);
		node.setZrotation(newz); 
		workspace.undoManager.pushNodeRotationChange( node, oldx, oldy, oldz, newx, newy, newz );
		 workspace.refreshWorkspace();
	}
	public void update(Skeleton data, Node node) {
		 this.node = node;
		 this.skeleton = data;  
		 updateFrameIndex(); 
	}

	public void updateFrameIndex( ) { 
          offsetXField.setEnabled(skeleton.getFrameIndex() == 0);
			offsetYField.setEnabled(skeleton.getFrameIndex() == 0);
			offsetZField.setEnabled(skeleton.getFrameIndex() == 0);
		 if( node == null) return;
		 this.offsetXField.setText(node.getOffset().getX()+"");
		 this.offsetYField.setText(node.getOffset().getY()+"");
		 this.offsetZField.setText(node.getOffset().getZ()+"");
		 this.nameLabel.setText(node.getName());
		 channelsLabel.setText(StringUtils.toString( node.getChannels()));
		 xrotationField.setText(node.getXrotation()+"");
		 xrotationSlider.setValue((int)node.getXrotation());
		 yrotationField.setText(node.getYrotation()+"");
		 yrotationSlider.setValue((int)node.getYrotation());
		 zrotationField.setText(node.getZrotation()+"");
		 zrotationSlider.setValue((int)node.getZrotation());
	}
}
