package r9.bvh.simple; 
 

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import r9.bvh.simpe.utils.ContextProviderI;
import r9.bvh.simpe.utils.ResourceHelper;
import r9.bvh.simpe.utils.UndoManager;
import r9.bvh.simpe.utils.u;
import r9.bvh.simple.animation.AnimationWorkspace;
import r9.bvh.simple.model.*;
import r9.bvh.simple.retarget.RetargetWorkspace;
import r9.bvh.simple.ui.fbfig.FbFigWorkspace;
 

public class Workspace  extends JFrame implements ContextProviderI{
 
	private static final long serialVersionUID = 1L;
	private CenterDrawingZone canvas;
	private JMenuBar jmenubar;
	Skeleton skeleton;
	private BVHTreeView treeView;
	private NodeAnimationSettingPanel nodeAnimationPanel;
	private MotionListPanel motionListPanel;
	private JSplitPane leftSplitPane;
	public final UndoManager undoManager;
	private SavedBVHListPanel savedBVHListPanel; 
	public Workspace() {
		this.setIconImage(ResourceHelper.getToolIcon("math2.png", 16,16).getImage());
		
		undoManager = new UndoManager(this);
		canvas = new CenterDrawingZone(this);
        setLayout(new BorderLayout());
     //   add(canvas, BorderLayout.CENTER);
        
        createMenuBar();
	    treeView = new BVHTreeView(new BVHTreeView.CALLBACK() { 
			public void onSelection(Node object) {
				nodeAnimationPanel.update(skeleton, object);
				canvas.highlight(object);
			}
		}, false);
	    treeView.setMinimumSize(new Dimension(200,900));
	    treeView.setPreferredSize(new Dimension(200,900));
	  //  add(treeView, BorderLayout.WEST);
	    
	    leftSplitPane = new JSplitPane();
		leftSplitPane.setOneTouchExpandable(true);
		leftSplitPane.setContinuousLayout(true);
		leftSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		leftSplitPane.setLeftComponent(treeView);
		leftSplitPane.setRightComponent(canvas );
		leftSplitPane.setDividerSize(4);
		leftSplitPane.setDividerLocation( 200 );

	    add(leftSplitPane, BorderLayout.CENTER);
		
	    savedBVHListPanel = new SavedBVHListPanel(this);
	    savedBVHListPanel.setMinimumSize(new Dimension(200,900));
	    savedBVHListPanel.setPreferredSize(new Dimension(200,900));
	    add(savedBVHListPanel, BorderLayout.WEST);
	    
	    nodeAnimationPanel = new NodeAnimationSettingPanel(this, new BVHTreeView.CALLBACK() { 
			public void onSelection(Node object) { 
			}
		});
	    add(nodeAnimationPanel, BorderLayout.EAST);
	    
	    
	    motionListPanel = new MotionListPanel(this);
	    add(motionListPanel, BorderLayout.SOUTH);
	    
        pack();
        setVisible(true);
	}
	public void reloadWorkspace(Skeleton skeleton) {
		this.skeleton = skeleton;
		treeView.update(skeleton);
		nodeAnimationPanel.update(skeleton, null);
		canvas.update(skeleton);
		motionListPanel.update(skeleton, skeleton.getMotion());
	}
	 
	public void loadNewFile(File file) {
		InputStream io = null;
		try {
			io = new FileInputStream(file);
			Skeleton skeleton = new Skeleton(io);
			reloadWorkspace(skeleton);
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if( io != null )
				try {
					io.close();
				} catch (IOException e) { 
				}
		} 
	}
	
	 
	private void createMenuBar() {
		jmenubar = new JMenuBar();
		setJMenuBar(jmenubar);
		JMenu fileMenu = new JMenu( "BVH文件" );
		jmenubar.add(fileMenu);
		fileMenu.add(new JMenuItem(new AbstractAction("打开文件") {  
			private static final long serialVersionUID = 1L; 
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(Settings.homedir);
	    		chooser.setFileFilter(new FileFilter(){
	             	@Override
					public boolean accept(File f) {
	             	     return f.isDirectory()  ||  f.getName().endsWith("bvh") ;
					} 
					@Override
					public String getDescription() {
					   	return  "BVH file";
					}});
	    		
	    		 int selection = chooser.showOpenDialog(null);
	    		 if ( selection == JFileChooser.APPROVE_OPTION ){
	    			  File file = chooser.getSelectedFile();
	    			  File bvhdir = new File(Settings.homedir, "bvh");
	    			  File outfile = new File(bvhdir, file.getName());
	    			  if( outfile.exists() ) { 
	    			  } else {
	    				  try {
							u.copy(file, outfile);
						} catch (IOException e1) { 
							e1.printStackTrace();
						}
	    			  }
	    			  savedBVHListPanel.reload();
	    			  loadNewFile(file);
	    		 }
			}

	    }));
		
		fileMenu.add(new JMenuItem(new AbstractAction("保持为新的BVH文件") {  
			private static final long serialVersionUID = 1L; 
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(Settings.homedir);
	    		chooser.setFileFilter(new FileFilter(){
	             	@Override
					public boolean accept(File f) {
	             	     return f.isDirectory()  ||  f.getName().endsWith("bvh") ;
					} 
					@Override
					public String getDescription() {
					   	return  "BVH file";
					}});
	    		
	    		 int selection = chooser.showSaveDialog( null);
	    		 if ( selection == JFileChooser.APPROVE_OPTION ){
	    			  File file = chooser.getSelectedFile();
	    			  String bvh = skeleton.toBVHFile();
	    			  try {
						u.stringToFile(bvh, file);
					} catch (IOException e1) {
						 e1.printStackTrace();
					}
	    		 }
			}

	    }));
		
		JMenu viewMenu = new JMenu( "FB Fig库" );
		jmenubar.add(viewMenu);
		viewMenu.add(new JMenuItem(new AbstractAction("FB Character 库") {  
			private static final long serialVersionUID = 1L; 
			public void actionPerformed(ActionEvent e) {
				FbFigWorkspace w = new FbFigWorkspace();
				w.setSize(900,700);
			    w.setLocationRelativeTo(Workspace.this);
				w.setVisible(true);
			} 
	    }));
		JMenu retargetMenu = new JMenu( "Retarget库" );
		jmenubar.add(retargetMenu);
		retargetMenu.add(new JMenuItem(new AbstractAction("Retarget 库") {  
			private static final long serialVersionUID = 1L; 
			public void actionPerformed(ActionEvent e) {
				RetargetWorkspace w = new RetargetWorkspace();
				w.setSize(1200,700);
			    w.setLocationRelativeTo(Workspace.this);
				w.setVisible(true);
			} 
	    }));
		 
		JMenu animationMenu = new JMenu( "动画" );
		jmenubar.add(animationMenu);
		animationMenu.add(new JMenuItem(new AbstractAction("FB Character 库") {  
			private static final long serialVersionUID = 1L; 
			public void actionPerformed(ActionEvent e) {
				AnimationWorkspace w = new AnimationWorkspace();
				w.setSize(1200,780);
			    w.setLocationRelativeTo(Workspace.this);
				w.setVisible(true);
			} 
	    }));
		
		JMenu settingsMenu = new JMenu( "设置" );
		jmenubar.add(settingsMenu);
		settingsMenu.add(new JMenuItem(new AbstractAction("Preference") {  
			private static final long serialVersionUID = 1L; 
			public void actionPerformed(ActionEvent e) {
				ProjectSettingDialog w = new ProjectSettingDialog();
				w.setSize(400,400);
			    w.setLocationRelativeTo(Workspace.this);
				w.setVisible(true);
			} 
	    }));
	} 
	public void frameIndexChanged(int row) {
		boolean changed = skeleton.getFrameIndex() != row;
		skeleton.setPose(row);
		nodeAnimationPanel.updateFrameIndex( );
		canvas.setFrameIndex(row);
		if( changed )
			this.undoManager.clear();
	}
	public void updateFrameIndexSilently(int pos) {
		this.motionListPanel.updateFrameIndexSilently(pos);
	}
	public void highlightByMouseClick(Node data) {
		this.treeView.highlightByMouseClick(data);
		this.nodeAnimationPanel.update(skeleton, data);
	}
	public void reloadCurrentFrame() {
		canvas.setFrameIndex(skeleton.getFrameIndex());
		motionListPanel.reloadCurrentFrame();
	}
	public void refreshWorkspace() {
		 skeleton.updateMotionData();
		 reloadCurrentFrame();
	}

}
