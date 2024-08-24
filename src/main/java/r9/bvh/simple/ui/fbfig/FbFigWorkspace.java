package r9.bvh.simple.ui.fbfig;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;

import r9.bvh.simpe.utils.ContextProviderI;
import r9.bvh.simpe.utils.ResourceHelper;
import r9.bvh.simpe.utils.UndoManager;
import r9.bvh.simple.ProjectSettingDialog;
import r9.bvh.simple.model.NodeBase;
import r9.bvh.simple.retarget.RetargetWorkspace;
 

public class FbFigWorkspace  extends JFrame implements ContextProviderI{
 
	private static final long serialVersionUID = 1L;
	private FigEditZone canvas;
	private JMenuBar jmenubar;
	CharacterConfig skeleton; 
	 
	private SavedFigListPanel savedFigListPanel;
	private JSplitPane leftSplitPane;
	public final UndoManager undoManager; 
	public FbFigWorkspace() {
		this.setIconImage(ResourceHelper.getToolIcon("math2.png", 16,16).getImage());
		
		undoManager = new UndoManager(this);
		canvas = new FigEditZone(this);
        setLayout(new BorderLayout());
     //   add(canvas, BorderLayout.CENTER);
        
        createMenuBar();
        savedFigListPanel = new SavedFigListPanel(this);
	    
        savedFigListPanel.setMinimumSize(new Dimension(200,900));
        savedFigListPanel.setPreferredSize(new Dimension(200,900));
	  //  add(treeView, BorderLayout.WEST);
	    
	    leftSplitPane = new JSplitPane();
		leftSplitPane.setOneTouchExpandable(true);
		leftSplitPane.setContinuousLayout(true);
		leftSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		leftSplitPane.setLeftComponent(savedFigListPanel);
		leftSplitPane.setRightComponent(canvas );
		leftSplitPane.setDividerSize(4);
		leftSplitPane.setDividerLocation( 200 );

	    add(leftSplitPane, BorderLayout.CENTER);
		
	    
        pack();
        setVisible(true);
	}
	public void reloadWorkspace(CharacterConfig skeleton) {
		this.skeleton = skeleton; 
		canvas.update(skeleton); 
	}
	 
	 
	
	 
	private void createMenuBar() {
		jmenubar = new JMenuBar();
		setJMenuBar(jmenubar);
		JMenu fileMenu = new JMenu( "文件" );
		jmenubar.add(fileMenu);
		fileMenu.add(new JMenuItem(new AbstractAction("产生新的FIG文件") {  
			private static final long serialVersionUID = 1L; 
			public void actionPerformed(ActionEvent e) {
			    String name = JOptionPane.showInputDialog("FIG的名称", "");
			    if( name.length() == 0) return;
			    CharacterConfig fig = new CharacterConfig();
			    fig.setName(name);
			    FbCharacterFigRegister.sharedInstance.getFigList().add(fig);
			    savedFigListPanel.reloadPhaseList();
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
			    w.setLocationRelativeTo(FbFigWorkspace.this);
				w.setVisible(true);
			} 
	    }));
	}
	 
	public void highlightByMouseClick(NodeBase<NodeBase> data) {
		 
	}
	 
	public void refreshWorkspace() {
		 
	}
	public void figSelectionChanged(CharacterConfig characterFig) {
		canvas.update(characterFig);
	}

}
