package r9.bvh.simple.retarget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
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
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import r9.bvh.simpe.utils.ContextProviderI;
import r9.bvh.simpe.utils.PageColors;
import r9.bvh.simpe.utils.ResourceHelper;
import r9.bvh.simpe.utils.StringUtils;
import r9.bvh.simpe.utils.UndoManager;
import r9.bvh.simple.ProjectSettingDialog;
import r9.bvh.simple.Workspace;
import r9.bvh.simple.model.Node;
import r9.bvh.simple.model.NodeBase;
import r9.bvh.simple.model.Skeleton; 
import r9.bvh.simple.ui.fbfig.CharacterConfig;  


public class RetargetWorkspace  extends JFrame implements ContextProviderI{
 
	private static final long serialVersionUID = 1L;
	private CharacterRigGroupPane characterRigGroupPane;
	private BvhRetargetGroupPane bvhGroupPane;
	private JMenuBar jmenubar;
	
	RetargetConfig curRetarget; 
	 
	private SavedRetargetListPanel savedRetargetListPanel;
	private JSplitPane leftSplitPane;
	public final UndoManager undoManager;
	private RetargetSettingPanel settingPanel;
	private CharacterRigOffsetPane rootOffsetRigPane;
	private BvhRetargetOffsetPane rootOffsetBvhPane;
	private CharacterRigMappingPane mappingRigPane;
	private BvhRetargetMappingPane mappingBvhPane;
	
	
	private Map<String, Color> mapColors = new HashMap<>();

	//private Skeleton skeleton;
	//private CharacterFig cfig; 
	public RetargetWorkspace() {
		this.setIconImage(ResourceHelper.getToolIcon("math2.png", 16,16).getImage());
		
		undoManager = new UndoManager(this);
		characterRigGroupPane = new CharacterRigGroupPane(this);
		bvhGroupPane = new BvhRetargetGroupPane(this);
		
		JPanel groupPane = new JPanel();
		groupPane.setLayout(new GridLayout(1,2));
		groupPane.add(bvhGroupPane);
		groupPane.add(characterRigGroupPane);
		
		rootOffsetRigPane = new CharacterRigOffsetPane(this);
		rootOffsetBvhPane = new BvhRetargetOffsetPane(this);
		
		JPanel rootOffsetPane = new JPanel();
		rootOffsetPane.setLayout(new GridLayout(1,2));
		rootOffsetPane.add(rootOffsetBvhPane);
		rootOffsetPane.add(rootOffsetRigPane);
		
		mappingRigPane = new CharacterRigMappingPane(this);
		mappingBvhPane = new BvhRetargetMappingPane(this);
		
		JPanel mappingPane = new JPanel();
		mappingPane.setLayout(new GridLayout(1,2));
		mappingPane.add(mappingBvhPane);
		mappingPane.add(mappingRigPane);
		
		
		final JTabbedPane  contentPane = new JTabbedPane(); 
		contentPane.add("Grouping", groupPane);
		contentPane.add("Root Offset", rootOffsetPane);
	    contentPane.add("Mapping", mappingPane);
		
	    contentPane.addChangeListener(new ChangeListener() { 
			public void stateChanged(ChangeEvent e) {
				int selectedIndex = contentPane.getSelectedIndex();
				if( selectedIndex == 1 ) {
					rootOffsetBvhPane.panelSelected();
				}
			}});
        setLayout(new BorderLayout());
        add(contentPane, BorderLayout.CENTER);
        
        createMenuBar();
        savedRetargetListPanel = new SavedRetargetListPanel(this);
	    
        savedRetargetListPanel.setMinimumSize(new Dimension(200,900));
        savedRetargetListPanel.setPreferredSize(new Dimension(200,900));
	  //  add(treeView, BorderLayout.WEST);
	    
	    leftSplitPane = new JSplitPane();
		leftSplitPane.setOneTouchExpandable(true);
		leftSplitPane.setContinuousLayout(true);
		leftSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		leftSplitPane.setLeftComponent(savedRetargetListPanel);
		leftSplitPane.setRightComponent( contentPane );
		leftSplitPane.setDividerSize(4);
		leftSplitPane.setDividerLocation( 200 );

	    add(leftSplitPane, BorderLayout.CENTER);
		 
	    settingPanel = new RetargetSettingPanel(this);
	    
	    add(settingPanel, BorderLayout.EAST);
	    
	    
        pack();
        setVisible(true);
	}
	public void reloadWorkspace(RetargetConfig retarget) {
		this.curRetarget = retarget; 
		this.mapColors.clear();
		String bvhfile = retarget.getBvh_file();
		if( StringUtils.isEmpty(bvhfile)) {
			retarget.skeleton = null;
			bvhGroupPane.update(retarget , "");
			rootOffsetBvhPane.update(retarget );
			mappingBvhPane.update(retarget );
		} else {
			File bvh = RetargetRegister.sharedInstance.getResourceInRetargetDir( bvhfile );
		 	 Skeleton skeleton = new Skeleton( bvh );
		 	retarget.skeleton = skeleton;
			bvhGroupPane.update(retarget , bvhfile);
			rootOffsetBvhPane.update(retarget );
			mappingBvhPane.update(retarget );
		}

		String charfile = retarget.getChar_file();
		if( StringUtils.isEmpty(charfile)) {
			characterRigGroupPane.update((CharacterConfig)null, ""); 
			rootOffsetRigPane.update((CharacterConfig)null);
			mappingRigPane.update((CharacterConfig)null);
			 retarget.cfig = null;
			 
		} else {
			File cfile = RetargetRegister.sharedInstance.getResourceInRetargetDir( charfile );
	    	try {
				 CharacterConfig cfig = CharacterConfig.loadFromFile(cfile);
				 retarget.cfig = cfig;
				characterRigGroupPane.update(cfig, cfile.getAbsolutePath()); 
				rootOffsetRigPane.update((CharacterConfig)cfig);
				mappingRigPane.update((CharacterConfig)cfig);
			} catch (Exception e) { 
				e.printStackTrace();
			}  
		}
		settingPanel.update(retarget);
	}
	  
	private void createMenuBar() {
		jmenubar = new JMenuBar();
		setJMenuBar(jmenubar);
		JMenu fileMenu = new JMenu( "文件" );
		jmenubar.add(fileMenu);
		fileMenu.add(new JMenuItem(new AbstractAction("产生新的Retarget文件") {  
			private static final long serialVersionUID = 1L; 
			public void actionPerformed(ActionEvent e) {
			    String name = JOptionPane.showInputDialog("Retarget文件的名称", "");
			    if( name.length() == 0) return;
			    RetargetConfig fig = new RetargetConfig();
			    fig.setName(name);
			    RetargetRegister.sharedInstance.getFigList().add(fig);
			    savedRetargetListPanel.reloadPhaseList();
			    figSelectionChanged(fig);
			}

	    })); 
		JMenu settingsMenu = new JMenu( "设置" );
		jmenubar.add(settingsMenu);
		settingsMenu.add(new JMenuItem(new AbstractAction("Preference") {  
			private static final long serialVersionUID = 1L; 
			public void actionPerformed(ActionEvent e) {
				ProjectSettingDialog w = new ProjectSettingDialog();
				w.setSize(400,400);
			    w.setLocationRelativeTo(RetargetWorkspace.this);
				w.setVisible(true);
			} 
	    }));
	}
	public Color getMappingColor(String gname, boolean createNew) {
		Color c = this.mapColors.get(gname);
		if( c != null ) return c;
		if( !createNew ) return c; 
		c = PageColors.nextColor(mapColors.size());
		mapColors.put(gname, c);
		return c;
	}
	public void highlightByMouseClick(NodeBase<NodeBase> data) {
		 
	}
	public void highlightByMouseClick(Node data) {
	 
	}  
	public void refreshWorkspace() {
		 
	}
	public void figSelectionChanged(RetargetConfig  characterFig) {
		reloadWorkspace(characterFig);
	}
	public void update(Skeleton s) {
		characterRigGroupPane.update(s);
	}
	public void update(CharacterConfig s) {
		this.settingPanel.update(this.curRetarget);
	}
	
	
	public void save() {
		 if( curRetarget != null) {
			 settingPanel.save();  
			 RetargetRegister.sharedInstance.save(curRetarget);
		 }
	}
	public void inMappingViewClickBVHNode(List<Node> data) {
		// TODO Auto-generated method stub
		
	}
	public void mappingFromCharMouseClick(NodeBase<NodeBase> data) {
		// TODO Auto-generated method stub
		
	}
	
	public RetargetConfig getCurRetarget() {
		return this.curRetarget;
	}
}