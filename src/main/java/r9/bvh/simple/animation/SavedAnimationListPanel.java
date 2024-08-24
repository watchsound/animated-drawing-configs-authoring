package r9.bvh.simple.animation;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import r9.bvh.simpe.utils.PageColors;
import r9.bvh.simple.Settings;
 

public class SavedAnimationListPanel extends JPanel{
 
	private static final long serialVersionUID = 1L;
	  
	 
	private AnimationListModel animationListModel;
	private JList<File> animationListView;
	private JScrollPane figlistScrollPane;
	AnimationWorkspace workspace;
	boolean blockEventFire;
	
	List<File> animationList = new ArrayList<>();
	private File curAnimation;
//	private List<File> figList;
	
	public SavedAnimationListPanel(final AnimationWorkspace workspace) {
		setLayout(new BorderLayout());
        this.workspace = workspace;
     //   figList = FbCharacterFigRegister.sharedInstance.getAnimationList();
		animationListModel = new AnimationListModel();
		animationListView = new JList<>(animationListModel);
		animationListView.setCellRenderer(new ProjectCellRenderer());
		animationListView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
		animationListView.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
			 	if (!e.getValueIsAdjusting()) {
					ListSelectionModel lsm = (ListSelectionModel) e.getSource();
					if (!lsm.isSelectionEmpty()) {
						int row = lsm.getLeadSelectionIndex();
						animationListModel.itemSelected(row); 
						if( blockEventFire ) return;
						workspace.animationSelected(animationListModel.get(row));
					}
				}
			}
		});
	  
		figlistScrollPane = new JScrollPane(animationListView);
		figlistScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		figlistScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		add(figlistScrollPane, BorderLayout.CENTER);
	}
	public void addNewFig(File curAnimation ) { 
		this.curAnimation=curAnimation; 
		this.animationList.add(curAnimation);
		animationListModel.rebuildList();
	}
	
	private File getCurFigure() { 
		return curAnimation;
	}
	 
	public JList getList() {
		return this.animationListView;
	}

	public void repaintCurrentCell() {
		int row = animationListView.getSelectedIndex();
		if (row < 0)
			row = 0;
		Rectangle r = animationListView.getCellBounds(row, row);
		if( r == null )
			return;
		animationListView.repaint(r);
	}

	public void reloadPhaseList() { 
		animationListModel.rebuildList(); 
	}

	public void scrollToPageIndex(int pageIndex) { 
		Rectangle r = animationListView.getCellBounds(pageIndex, pageIndex);
		if( r != null)
		    animationListView.scrollRectToVisible(r);
		if( pageIndex < animationListView.getModel().getSize())
		    animationListView.setSelectedIndex(pageIndex);
	}

 
  
	public Dimension getPreferredSize() {
		return new Dimension((int)(Settings.CARD_WIDTH * 3 + Settings.SIDE_MENU_WIDTH * 2),
				(int) (Settings.PREVIEW_HEIGHT / 10.0 * 4.5));
	}
 
 
	
	private  List<File> getAnimationList() { 
		return this.animationList;
	}
 
  
	class ProjectCellRenderer implements ListCellRenderer {

		public Component getListCellRendererComponent(final JList list,
				final Object value, final int index, final boolean isSelected,
				final boolean cellHasFocus) {
			return new CellPreviewPanel(list, value, index, isSelected,
					cellHasFocus);
		}

		private class CellPreviewPanel extends JLabel {
			private static final long serialVersionUID = 1L;
			private JList list;
			private int index;
			private boolean isSelected;
			private boolean cellHasFocus;
			private File value; 
		 
			public CellPreviewPanel(final JList list, final Object value,
					final int index, final boolean isSelected,
					final boolean cellHasFocus) {
				setLayout(null);
				this.list = list;
				this.value = (File) value;
				this.index = index;
				this.isSelected = isSelected;
				this.cellHasFocus = cellHasFocus;
				  
				this.setText(this.value.getName());
				Color defaultColor = Color.darkGray;
				int defaultLineWidth = 1;
				  
				if (!this.isSelected)
					setBorder(BorderFactory.createCompoundBorder(
							BorderFactory.createEmptyBorder(2, 2, 2, 2),
							BorderFactory.createLineBorder(defaultColor, defaultLineWidth)));
				else
					setBorder(BorderFactory.createCompoundBorder(
							BorderFactory.createLineBorder(PageColors.red1, 1),
							BorderFactory.createLineBorder(defaultColor, defaultLineWidth)));

 			} 
		}
	}
	
	
	class AnimationListModel extends AbstractListModel<File> {
		private static final long serialVersionUID = 1L;

		private long lastSelectedTime = 0;

		public AnimationListModel() {
			setup();
		}
		public void setup() {
			animationList.clear();
			File fs = new File(Settings.homedir, "exports");
			for(File f: fs.listFiles()) {
				if( f.isDirectory())
					animationList.add(f);
			}
		}

		public File get(int row) {
		 	return getElementAt(row);
		}

		public void itemSelected(int index) {
			lastSelectedTime = new Date().getTime();
		}

		public boolean canSelectItem() {
			return new Date().getTime() > lastSelectedTime + 500;
		}
 
		private void rebuildList() {
			this.fireContentsChanged(this, 0, getSize());
		}

		public int getSize() { 
			return getAnimationList() == null ? 0 : getAnimationList().size();
		}

		public  File getElementAt(int index) {
			if (getAnimationList() == null)
				return null; 
			return getAnimationList().get(index);
		}

	}





	public void reload() {
		animationListModel.setup();
		animationListModel.rebuildList();
	}


 
}
