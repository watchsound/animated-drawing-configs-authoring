package r9.bvh.simple.retarget;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import r9.bvh.simple.Settings;
 

public class SavedRetargetListPanel extends JPanel{
 
	private static final long serialVersionUID = 1L;
	  
	 
	private FigListModel figListModel;
	private JList<RetargetConfig> figListView;
	private JScrollPane figlistScrollPane;
	RetargetWorkspace workspace;
	boolean blockEventFire;
	
	private RetargetConfig curFig;
//	private List<Retarget> figList;
	
	public SavedRetargetListPanel(final RetargetWorkspace workspace) {
		setLayout(new BorderLayout());
        this.workspace = workspace;
     //   figList = FbCharacterFigRegister.sharedInstance.getFigList();
		figListModel = new FigListModel();
		figListView = new JList<>(figListModel);

		figListView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
		figListView.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
			 	if (!e.getValueIsAdjusting()) {
					ListSelectionModel lsm = (ListSelectionModel) e.getSource();
					if (!lsm.isSelectionEmpty()) {
						int row = lsm.getLeadSelectionIndex();
						figListModel.itemSelected(row); 
						if( blockEventFire ) return;
						workspace.figSelectionChanged(figListModel.get(row));
					}
				}
			}
		});
	  
		figlistScrollPane = new JScrollPane(figListView);
		figlistScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		figlistScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		add(figlistScrollPane, BorderLayout.CENTER);
	}
	public void addNewFig(RetargetConfig curFig ) { 
		this.curFig=curFig;
		RetargetRegister.sharedInstance.getFigList() .add(curFig);
		figListModel.rebuildList();
	}
	
	private RetargetConfig getCurFigure() { 
		return curFig;
	}
	 
	public JList getList() {
		return this.figListView;
	}

	public void repaintCurrentCell() {
		int row = figListView.getSelectedIndex();
		if (row < 0)
			row = 0;
		Rectangle r = figListView.getCellBounds(row, row);
		if( r == null )
			return;
		figListView.repaint(r);
	}

	public void reloadPhaseList() { 
		figListModel.rebuildList(); 
	}

	public void scrollToPageIndex(int pageIndex) { 
		Rectangle r = figListView.getCellBounds(pageIndex, pageIndex);
		if( r != null)
		    figListView.scrollRectToVisible(r);
		if( pageIndex < figListView.getModel().getSize())
		    figListView.setSelectedIndex(pageIndex);
	}

 
  
	public Dimension getPreferredSize() {
		return new Dimension((int)(Settings.CARD_WIDTH * 3 + Settings.SIDE_MENU_WIDTH * 2),
				(int) (Settings.PREVIEW_HEIGHT / 10.0 * 4.5));
	}

	long lastAddPhaseTime; 

  

	protected void deleteSelectedTimeshot() {
	    int pos =  figListView.getSelectedIndex();
	    if( pos <= 0 ) return;
	    RetargetRegister.sharedInstance.getFigList() .remove(pos);  
		figListModel.rebuildList();
		figListView.setSelectedIndex(pos);
	}

	
	private  List<RetargetConfig> getFigList() {
		return RetargetRegister.sharedInstance.getFigList();
	}
 
  
 
	
	
	class FigListModel extends AbstractListModel<RetargetConfig> {
		private static final long serialVersionUID = 1L;

		private long lastSelectedTime = 0;

		public FigListModel() {

		}

		public RetargetConfig get(int row) {
		 	return getElementAt(row);
		}

		public void itemSelected(int index) {
			lastSelectedTime = new Date().getTime();
		}

		public boolean canSelectItem() {
			return new Date().getTime() > lastSelectedTime + 500;
		}

		public void setElementAt(RetargetConfig element, int index) { 
			if (getFigList() == null)
				return;
			getFigList().set(index, element);
			RetargetRegister.sharedInstance.save(element);
			fireContentsChanged(this, index, index);
		}

		public void add(int index, RetargetConfig item) { 
			if (getFigList() == null)
				return;
			getFigList().add(index, item);
			RetargetRegister.sharedInstance.save(item);
			fireIntervalAdded(this, index, index);
		}

		public RetargetConfig remove(int index) {
			 
			if (getFigList() == null)
				return null;

			 RetargetConfig p =  getFigList().get(index);
			 getFigList().remove(index);
			 RetargetRegister.sharedInstance.delete(p);
			fireIntervalRemoved(this, index, index);
			return p;
		}

		public void clear() { 
			if (getFigList() == null)
				return;
			getFigList().clear();
			rebuildList();
		}

		

		private void rebuildList() {
			this.fireContentsChanged(this, 0, getSize());
		}

		public int getSize() { 
			return getFigList() == null ? 0 : getFigList().size();
		}

		public  RetargetConfig getElementAt(int index) {
			if (getFigList() == null)
				return null; 
			return getFigList().get(index);
		}

	}


 
}
