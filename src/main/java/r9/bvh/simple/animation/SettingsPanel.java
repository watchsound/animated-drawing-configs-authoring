package r9.bvh.simple.animation;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import r9.bvh.simpe.utils.PropertyUIHelper;
import r9.bvh.simpe.utils.u;
import r9.bvh.simple.App;
import r9.bvh.simple.Settings;
import r9.bvh.simple.animation.MvcConfig.ANIMATED_CHARACTER;
import r9.bvh.simple.animation.MvcConfig.controller;
import r9.bvh.simple.animation.MvcConfig.scene;
import r9.bvh.simple.animation.MvcConfig.view;
import r9.bvh.simple.retarget.RetargetConfig.BvhGroupMethod;

public class SettingsPanel extends JPanel{ 
 
	private static final long serialVersionUID = 1L;
	private AnimationWorkspace workspace;
	private JTextField character_cfg;
	private JTextField motion_cfg;
	private JTextField retarget_cfg;
	private JCheckBox ADD_FLOOR;
	private JCheckBox ADD_AD_RETARGET_BVH;
	private JTextField clear_color_r;
	private JTextField clear_color_g;
	private JTextField clear_color_b;
	private JTextField clear_color_a;
	private JTextField window_w;
	private JTextField window_h;
	private JCheckBox DRAW_AD_RIG;
	private JCheckBox DRAW_AD_TXTR;
	private JCheckBox DRAW_AD_COLOR;
	private JCheckBox DRAW_AD_MESH_LINES;
	private JCheckBox USE_MESA;
	private JTextField BACKGROUND_IMAGE;
	private JTextField camera_pos_y;
	private JTextField camera_pos_z;
	private JTextField camera_pos_x;
	private JTextField camera_fwd_x;
	private JTextField camera_fwd_y;
	private JTextField camera_fwd_z;
	private JComboBox<String> MODE;
	private JTextField KEYBOARD_TIMESTEP;
	private JTextField OUTPUT_VIDEO_PATH;
	private JButton OUTPUT_VIDEO_PATH_BTN;
	private JTextField OUTPUT_VIDEO_CODEC;
	private JButton retarget_lookup;
	private JButton save;
	private JButton motion_cfg_lookup;
	private JButton character_cfg_lookup;
	private JLabel saveInfoLabel;
//	private JTextField bvhFileField;
//	private JButton bvhFileButton;
//	private JTextField start_frame_idx;
//	private JTextField end_frame_idx;
//	private JTextField scale;
//	private JLabel groundplane_joint;
//	private JComboBox<String> groundplane_joint_combo;

	public SettingsPanel(AnimationWorkspace workspace) {
		this.workspace = workspace;
		this.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(5, 5, 5, 5) ,
				BorderFactory.createCompoundBorder( 
				   BorderFactory.createLineBorder(Color.black),
				   BorderFactory.createEmptyBorder(5,5,5,5))));
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		this.add(PropertyUIHelper.createTitleRow("MVC Config File", true));
		this.add(PropertyUIHelper.createTitleRow("scene"));
		character_cfg = new JTextField(10);
		character_cfg.setText("character.yaml");
		character_cfg.setEditable(false);
		character_cfg_lookup = new JButton("File");
		character_cfg_lookup.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(new File(Settings.homedir, Settings.CHARACTER_DIR));
				chooser.setFileFilter(new FileFilter() {
					@Override
					public boolean accept(File f) {
						return f.isDirectory() || f.getName().endsWith("yaml")   ;
					} 
					@Override
					public String getDescription() {
						return "yaml file";
					}
				});

				int selection = chooser.showOpenDialog(null);
				if (selection == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					File retarget = new File(workspace.getCurFile().folder, AnimationProject.CHARACTER);
					try {
						u.copy(file, retarget);
						workspace.getCurFile().reloadCharacter();
					} catch (IOException e1) { 
						e1.printStackTrace();
					}
				}
			}});
		this.add(PropertyUIHelper.createRow("character_cfg", new JLabel(), character_cfg, character_cfg_lookup));
		motion_cfg = new JTextField(10);
		motion_cfg.setText("motion.yaml");
		motion_cfg.setEditable(false);
		motion_cfg_lookup = new JButton("File");
		motion_cfg_lookup.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(Settings.homedir);
				chooser.setFileFilter(new FileFilter() {
					@Override
					public boolean accept(File f) {
						return f.isDirectory() || f.getName().endsWith("yaml")   ;
					} 
					@Override
					public String getDescription() {
						return "yaml file";
					}
				});

				int selection = chooser.showOpenDialog(null);
				if (selection == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					File retarget = new File(workspace.getCurFile().folder, AnimationProject.MOTION);
					try {
						u.copy(file, retarget);
						workspace.getCurFile().reloadMotion();
					} catch (IOException e1) { 
						e1.printStackTrace();
					}
				}
			}});
		this.add(PropertyUIHelper.createRow("motion_cfg", new JLabel(), motion_cfg , motion_cfg_lookup));
		retarget_cfg = new JTextField(10);
		retarget_cfg.setText("retarget.yaml");
		retarget_cfg.setEditable(false);
		
		retarget_lookup = new JButton("File");
		retarget_lookup.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(Settings.homedir);
				chooser.setFileFilter(new FileFilter() {
					@Override
					public boolean accept(File f) {
						return f.isDirectory() || f.getName().endsWith("yaml")   ;
					}

					@Override
					public String getDescription() {
						return "yaml file";
					}
				});

				int selection = chooser.showOpenDialog(null);
				if (selection == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					File retarget = new File(workspace.getCurFile().folder, AnimationProject.RETARGET);
					try {
						u.copy(file, retarget);
						workspace.getCurFile().reloadRetarget();
					} catch (IOException e1) { 
						e1.printStackTrace();
					}
				}
			}});
	 //  this.add(PropertyUIHelper.createRow( retarget_lookup, false, null));

		
		this.add(PropertyUIHelper.createRow("retarget_cfg", new JLabel(), retarget_cfg, retarget_lookup));

		ADD_FLOOR = new JCheckBox("ADD_FLOOR");
		this.add(PropertyUIHelper.createRow(  ADD_FLOOR, true, null));

		ADD_AD_RETARGET_BVH = new JCheckBox("ADD_AD_RETARGET_BVH");
		this.add(PropertyUIHelper.createRow(  ADD_AD_RETARGET_BVH, true, null));

		this.add(PropertyUIHelper.createTitleRow("view"));
		clear_color_r = new JTextField(5); 
		clear_color_g = new JTextField(5);
		clear_color_b = new JTextField(5);
		clear_color_a = new JTextField(5);
		this.add(PropertyUIHelper.createRow("CLEAR_COLOR", clear_color_r,clear_color_g,clear_color_b, clear_color_a));

		window_w = new JTextField(5); 
		window_h = new JTextField(5);
		this.add(PropertyUIHelper.createRow("WINDOW_DIMENSIONS",  window_w, window_h));

		DRAW_AD_RIG = new JCheckBox("DRAW_AD_RIG");
		this.add(PropertyUIHelper.createRow(  DRAW_AD_RIG, true, null));
		DRAW_AD_TXTR = new JCheckBox("DRAW_AD_TXTR");
		this.add(PropertyUIHelper.createRow(  DRAW_AD_TXTR, true, null));
		DRAW_AD_COLOR = new JCheckBox("DRAW_AD_COLOR");
		this.add(PropertyUIHelper.createRow(  DRAW_AD_COLOR, true, null));
		DRAW_AD_MESH_LINES = new JCheckBox("DRAW_AD_MESH_LINES");
		this.add(PropertyUIHelper.createRow(  DRAW_AD_MESH_LINES, true, null));

	    camera_pos_x = new JTextField(5); 
		camera_pos_y = new JTextField(5);
		camera_pos_z = new JTextField(5); 
		this.add(PropertyUIHelper.createRow("CAMERA_POS", camera_pos_x, camera_pos_y, camera_pos_z));

	    camera_fwd_x = new JTextField(5); 
		camera_fwd_y = new JTextField(5);
		camera_fwd_z = new JTextField(5); 
		this.add(PropertyUIHelper.createRow("CAMERA_POS", camera_fwd_x, camera_fwd_y, camera_fwd_z));

		
		USE_MESA = new JCheckBox("USE_MESA");
		this.add(PropertyUIHelper.createRow(  USE_MESA, true, null));

		BACKGROUND_IMAGE = new JTextField(10);
		this.add(PropertyUIHelper.createRow("BACKGROUND_IMAGE", BACKGROUND_IMAGE));

		this.add(PropertyUIHelper.createTitleRow("controller"));
		Vector<String> modetypes = new Vector<>();
		modetypes.add("video_render");
		modetypes.add("interactive");
		MODE = new JComboBox<String>(modetypes);
		this.add(PropertyUIHelper.createRow("MODE", MODE));

		KEYBOARD_TIMESTEP = new JTextField(5);
		this.add(PropertyUIHelper.createRow("KEYBOARD_TIMESTEP", KEYBOARD_TIMESTEP));

		 
		OUTPUT_VIDEO_PATH = new JTextField(10);
		OUTPUT_VIDEO_PATH_BTN = new JButton("File");
		OUTPUT_VIDEO_PATH_BTN.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Specify a file to save");   
				int userSelection = fileChooser.showSaveDialog(workspace);
				 
				if (userSelection == JFileChooser.APPROVE_OPTION) {
				    File fileToSave = fileChooser.getSelectedFile();
				    if( fileToSave != null) {
				    	OUTPUT_VIDEO_PATH.setText(fileToSave.getAbsolutePath());
				    }
				} 
			}});
		this.add(PropertyUIHelper.createRow("OUTPUT_VIDEO_PATH", OUTPUT_VIDEO_PATH, OUTPUT_VIDEO_PATH_BTN));

		
		OUTPUT_VIDEO_CODEC = new JTextField(10);
		this.add(PropertyUIHelper.createRow("OUTPUT_VIDEO_CODEC", OUTPUT_VIDEO_CODEC));

		
		//this.add(PropertyUIHelper.createTitleRow("Character Config File", true));
		
		//this.add(PropertyUIHelper.createTitleRow("Motion Config File", true));
		
		
		//this.add(PropertyUIHelper.createTitleRow("Retarget Config File", true));

		
		
		this.add(PropertyUIHelper.createLine());
		saveInfoLabel = new JLabel();
		saveInfoLabel.setForeground(Color.RED);
		save = new JButton("Save");
		save.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				 saveInfoLabel.setText("");
				 try {
					 workspace.save();
				 }catch(Exception ex) {
					 saveInfoLabel.setText(ex.getMessage());
				 } 
			}});
		this.add(PropertyUIHelper.createRow( "", saveInfoLabel, save));

	}
	
	public void saveUIState() {
		AnimationProject ap = workspace.curProject;
		scene scene = ap.mvcConfig.scene;
		 scene.ANIMATED_CHARACTERS.clear();
		ANIMATED_CHARACTER a = new ANIMATED_CHARACTER();
		if( character_cfg.getText().length() > 0)
			a.character_cfg = character_cfg.getText();
		if( motion_cfg.getText().length() > 0)
			a.motion_cfg = motion_cfg.getText();
		if( retarget_cfg.getText().length() > 0)
			a.retarget_cfg = retarget_cfg.getText(); 
		
		 scene.ANIMATED_CHARACTERS.add(new ANIMATED_CHARACTER());
		 scene.ADD_FLOOR = ADD_FLOOR.isSelected();
		 scene.ADD_AD_RETARGET_BVH = ADD_AD_RETARGET_BVH.isSelected();
			  
		 view view = ap.mvcConfig.view; 
		 if( clear_color_r.getText().length()> 0 && clear_color_g.getText().length()>0) {
			if( view.CLEAR_COLOR == null )
				view.CLEAR_COLOR = new double[4];
			view.CLEAR_COLOR[0] = Double.parseDouble(clear_color_r.getText());
			view.CLEAR_COLOR[1] = Double.parseDouble(clear_color_g.getText());
			view.CLEAR_COLOR[2] = Double.parseDouble(clear_color_b.getText());
			view.CLEAR_COLOR[3] = Double.parseDouble(clear_color_a.getText());
			
		 }
		 if( window_w.getText().length()> 0 && window_h.getText().length()>0) {
				if( view.WINDOW_DIMENSIONS == null )
					view.WINDOW_DIMENSIONS = new int[4];
				view.WINDOW_DIMENSIONS[0] = Integer.parseInt(window_w.getText());
				view.WINDOW_DIMENSIONS[1] = Integer.parseInt(window_h.getText()); 
		  }
		 view.DRAW_AD_RIG = DRAW_AD_RIG.isSelected();
		 view.DRAW_AD_TXTR = DRAW_AD_TXTR.isSelected();
		 view.DRAW_AD_COLOR = DRAW_AD_COLOR.isSelected();
		 view.DRAW_AD_MESH_LINES = DRAW_AD_MESH_LINES.isSelected();
		 view.USE_MESA = USE_MESA.isSelected();
		 view.BACKGROUND_IMAGE = BACKGROUND_IMAGE.getText();
 
		 if( camera_pos_x.getText().length()> 0 && camera_pos_y.getText().length()>0) {
				if( view.CAMERA_POS == null )
					view.CAMERA_POS = new double[3];
				view.CAMERA_POS[0] = Double.parseDouble(camera_pos_x.getText());
				view.CAMERA_POS[1] = Double.parseDouble(camera_pos_y.getText());
				view.CAMERA_POS[2] = Double.parseDouble(camera_pos_z.getText());  
			 }
		 if( camera_fwd_x.getText().length()> 0 && camera_fwd_y.getText().length()>0) {
				if( view.CAMERA_FWD == null )
					view.CAMERA_FWD = new double[3];
				view.CAMERA_FWD[0] = Double.parseDouble(camera_fwd_x.getText());
				view.CAMERA_FWD[1] = Double.parseDouble(camera_fwd_y.getText());
				view.CAMERA_FWD[2] = Double.parseDouble(camera_fwd_z.getText());  
			 }
		 
		 controller  controller = ap.mvcConfig.controller; 
		 controller.MODE = (String) MODE.getSelectedItem();
		 if( KEYBOARD_TIMESTEP.getText().length()>0)
		     controller.KEYBOARD_TIMESTEP =Double.parseDouble(KEYBOARD_TIMESTEP.getText());
		 controller.OUTPUT_VIDEO_CODEC = OUTPUT_VIDEO_CODEC.getText();
		 controller.OUTPUT_VIDEO_PATH = OUTPUT_VIDEO_PATH.getText();
			 
	}

	public void updateByProjectChange() {
		AnimationProject ap = workspace.curProject;
		scene scene = ap.mvcConfig.scene;
		if( scene.ANIMATED_CHARACTERS.size()> 0) {
		   character_cfg.setText(scene.ANIMATED_CHARACTERS.get(0).character_cfg);
		   motion_cfg.setText(scene.ANIMATED_CHARACTERS.get(0).motion_cfg);
		   retarget_cfg.setText(scene.ANIMATED_CHARACTERS.get(0).retarget_cfg); 
		};
        
 
   	  ADD_FLOOR.setSelected(scene.ADD_FLOOR);
   	  ADD_AD_RETARGET_BVH.setSelected(scene.ADD_AD_RETARGET_BVH);
       
   	    view view = ap.mvcConfig.view;
   	    if( view.CLEAR_COLOR != null && view.CLEAR_COLOR.length ==4) {
			  clear_color_r.setText(view.CLEAR_COLOR[0]+"");;
			  clear_color_g.setText(view.CLEAR_COLOR[1]+"");;
			  clear_color_b.setText(view.CLEAR_COLOR[2]+"");;
			  clear_color_a.setText(view.CLEAR_COLOR[3]+"");; 
   	    }
   	    if( view.WINDOW_DIMENSIONS != null && view.WINDOW_DIMENSIONS.length ==2) {
   	    	window_w.setText(view.WINDOW_DIMENSIONS[0]+"");;
   	    	window_h.setText(view.WINDOW_DIMENSIONS[1]+"");; 
	    }
 
   	    DRAW_AD_RIG.setSelected(view.DRAW_AD_RIG);
	   	DRAW_AD_TXTR.setSelected(view.DRAW_AD_TXTR);
	   	DRAW_AD_COLOR.setSelected(view.DRAW_AD_COLOR);
	   	DRAW_AD_MESH_LINES.setSelected(view.DRAW_AD_MESH_LINES);
	   	USE_MESA.setSelected(view.USE_MESA);
	   	BACKGROUND_IMAGE.setText(view.BACKGROUND_IMAGE );; 

	    if( view.CAMERA_POS != null && view.CAMERA_POS.length ==3) {
	    	camera_pos_x.setText(view.CAMERA_POS[0]+"");;
	    	camera_pos_y.setText(view.CAMERA_POS[1]+"");;
	    	camera_pos_z.setText(view.CAMERA_POS[2]+"");; 
 	    }
	    if( view.CAMERA_FWD != null && view.CAMERA_FWD.length ==3) {
	    	camera_fwd_x.setText(view.CAMERA_FWD[0]+"");;
	    	camera_fwd_y.setText(view.CAMERA_FWD[1]+"");;
	    	camera_fwd_z.setText(view.CAMERA_FWD[2]+"");; 
 	    }
 
        controller controller = ap.mvcConfig.controller;
        MODE.setSelectedItem(controller.MODE);
         KEYBOARD_TIMESTEP.setText(controller.KEYBOARD_TIMESTEP+"");;
         OUTPUT_VIDEO_PATH.setText(controller.OUTPUT_VIDEO_PATH+"");;
         OUTPUT_VIDEO_CODEC.setText(controller.OUTPUT_VIDEO_CODEC+"");; 
		
	}
}
