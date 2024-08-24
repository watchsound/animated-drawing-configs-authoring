package r9.bvh.simple.animation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import r9.bvh.simpe.utils.ContextProviderI;
import r9.bvh.simpe.utils.PageColors;
import r9.bvh.simpe.utils.ResourceHelper;
import r9.bvh.simpe.utils.StringUtils;
import r9.bvh.simpe.utils.UndoManager;
import r9.bvh.simple.ProjectSettingDialog;
import r9.bvh.simple.Settings;
import r9.bvh.simple.Workspace;
import r9.bvh.simple.model.Node;
import r9.bvh.simple.model.NodeBase;
import r9.bvh.simple.model.Skeleton; 
import r9.bvh.simple.ui.fbfig.CharacterConfig;  


public class AnimationWorkspace  extends JFrame  {
 
	private static final long serialVersionUID = 1L;
	 
	private JMenuBar jmenubar;
	
	AnimationProject curProject; 
	 
	private SavedAnimationListPanel saveAnimationListPanel;
	private JSplitPane leftSplitPane; 
	private SettingsPanel settingPanel;

	private CharacterPanel characterPanel;

	private TexturePanel figurePanel;

	private MaskPanel maskPanel;

	private JScrollPane settingScrollPane;

	private MotionConfigPanel motionConfigPanel;
	 
	 
	//private Skeleton skeleton;
	//private CharacterFig cfig; 
	public AnimationWorkspace() {
		this.setIconImage(ResourceHelper.getToolIcon("math2.png", 16,16).getImage());
		 
		characterPanel = new CharacterPanel(this);
		figurePanel = new TexturePanel(this);
		maskPanel = new MaskPanel(this); 
		 
		motionConfigPanel = new MotionConfigPanel(this);
		
		final JTabbedPane  contentPane = new JTabbedPane();  
		contentPane.add("Motion Config", motionConfigPanel);
		contentPane.add("Image Setting", figurePanel);
		contentPane.add("Character Setting", characterPanel);
	    contentPane.add("Mask", maskPanel);
		
	    contentPane.addChangeListener(new ChangeListener() { 
			public void stateChanged(ChangeEvent e) {
				int selectedIndex = contentPane.getSelectedIndex();
				if( selectedIndex == 1 ) {
					 
				}
			}});
        setLayout(new BorderLayout());
        add(contentPane, BorderLayout.CENTER);
        
        createMenuBar();
        saveAnimationListPanel = new SavedAnimationListPanel(this);
	    
        saveAnimationListPanel.setMinimumSize(new Dimension(200,900));
        saveAnimationListPanel.setPreferredSize(new Dimension(200,900));
	  //  add(treeView, BorderLayout.WEST);
	    
	    leftSplitPane = new JSplitPane();
		leftSplitPane.setOneTouchExpandable(true);
		leftSplitPane.setContinuousLayout(true);
		leftSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		leftSplitPane.setLeftComponent(saveAnimationListPanel);
		leftSplitPane.setRightComponent( contentPane );
		leftSplitPane.setDividerSize(4);
		leftSplitPane.setDividerLocation( 200 );

	    add(leftSplitPane, BorderLayout.CENTER);
		 
	    settingPanel = new SettingsPanel(this);
	    
		settingScrollPane = new JScrollPane(settingPanel);
		settingScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		settingScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		
	    add(settingScrollPane, BorderLayout.EAST);
	    
	    
        pack();
        setVisible(true);
	}
	public void reloadWorkspace(File retarget) {
		this.curProject = new AnimationProject(retarget);  
		saveAnimationListPanel.reload();
		
	}
	  
	private void createMenuBar() {
		jmenubar = new JMenuBar();
		setJMenuBar(jmenubar);
		JMenu fileMenu = new JMenu( "文件" );
		jmenubar.add(fileMenu);
		fileMenu.add(new JMenuItem(new AbstractAction("产生新的动画项目") {  
			private static final long serialVersionUID = 1L; 
			public void actionPerformed(ActionEvent e) {
			    String name = JOptionPane.showInputDialog("R项目名称", "");
			    if( name.length() == 0) return;
			    File f = new File(Settings.homedir, "exports");
			    f = new File(f, name);
			    if( f.exists() ) {
			    	JOptionPane.showMessageDialog(null, "同名项目已经存在，请换一个名字");
			    	return;
			    }
			    f.mkdir();
			    saveAnimationListPanel.reload(); 
			}

	    })); 
		JMenu settingsMenu = new JMenu( "设置" );
		jmenubar.add(settingsMenu);
		settingsMenu.add(new JMenuItem(new AbstractAction("Preference") {  
			private static final long serialVersionUID = 1L; 
			public void actionPerformed(ActionEvent e) {
				ProjectSettingDialog w = new ProjectSettingDialog();
				w.setSize(400,400);
			    w.setLocationRelativeTo(AnimationWorkspace.this);
				w.setVisible(true);
			} 
	    }));
	}
  
	public void refreshWorkspace() {
		 
	}
	 
	
	public void save() {
		if( curProject == null ) return;
		
		motionConfigPanel.saveUIState();
		    settingPanel.saveUIState();
	        characterPanel.saveUIState();
	        figurePanel.saveUIState();;
	        maskPanel.saveUIState();;
	        
	        this.curProject.save();
	}
	  
	public AnimationProject getCurFile() {
		return this.curProject;
	}
	public void animationSelected(File file) {
	    this.curProject = new AnimationProject(file);
	    
	    motionConfigPanel.updateByProjectChange();
	    
	    settingPanel.updateByProjectChange();
        characterPanel.updateByProjectChange();
        figurePanel.updateByProjectChange();;
        maskPanel.updateByProjectChange();;
	}
	 
	public void setupNewImage( ) { 
		  characterPanel.updateByProjectChange();
	      figurePanel.updateByProjectChange();;
	      maskPanel.updateByProjectChange();;
	}
}