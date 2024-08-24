package r9.bvh.simple;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import r9.bvh.simpe.utils.ImageUtil;
import r9.bvh.simpe.utils.LRUCache;
import r9.bvh.simpe.utils.LabelButton;
import r9.bvh.simpe.utils.ResourceHelper;
import r9.bvh.simple.model.Motion;
import r9.bvh.simple.model.Motion.MFrame;
import r9.bvh.simple.model.Skeleton;
 

public class MotionListPanel extends JPanel{
 
	private static final long serialVersionUID = 1L;
	private LRUCache<String, BufferedImage> cachedImages = new LRUCache<String, BufferedImage>(30);
	Motion motion;
	Skeleton skeleton;
	private TimeshotListModel timeshotListModel;
	private JList<MFrame> timeshotListView;
	private JScrollPane timeshotScrollPane;
	Workspace workspace;
	boolean blockEventFire;
	public MotionListPanel(final Workspace workspace) {
		setLayout(new BorderLayout());
        this.workspace = workspace;
		timeshotListModel = new TimeshotListModel();
		timeshotListView = new JList<>(timeshotListModel);

		timeshotListView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		timeshotListView.setCellRenderer(new DataCellPreviewPanel());
		timeshotListView.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
			 	if (!e.getValueIsAdjusting()) {
					ListSelectionModel lsm = (ListSelectionModel) e.getSource();
					if (!lsm.isSelectionEmpty()) {
						int row = lsm.getLeadSelectionIndex();
						timeshotListModel.itemSelected(row); 
						if( blockEventFire ) return;
						workspace.frameIndexChanged(row);
					}
				}
			}
		});
		timeshotListView.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
			 	Point p = event.getPoint();
				int index = timeshotListView.locationToIndex(p);
				Rectangle rect = timeshotListView.getCellBounds(index, index);
				if (rect == null || !timeshotListModel.canSelectItem())
					return;
				 
				int x = rect.x;
				int y = rect.y;
				int xoffset = p.x - x;
				int yoffset = p.y - y;
				handleMouseEvent(xoffset, yoffset, event, index);
			}
		});

		timeshotListView.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		timeshotListView.setVisibleRowCount(1);
		timeshotScrollPane = new JScrollPane(timeshotListView);
		timeshotScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		timeshotScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		add(timeshotScrollPane, BorderLayout.CENTER);
	}
	public void update(Skeleton skeleton, Motion motion) {
		this.motion = motion;
		this.skeleton=skeleton;
		timeshotListModel.rebuildList();
	}
	
	private Motion getCurMotion() { 
		return motion;
	}
	
	protected void previewFrom(int index) {
	     
	}

	public JList getList() {
		return this.timeshotListView;
	}

	public void repaintCurrentCell() {
		int row = timeshotListView.getSelectedIndex();
		if (row < 0)
			row = 0;
		Rectangle r = timeshotListView.getCellBounds(row, row);
		if( r == null )
			return;
		timeshotListView.repaint(r);
	}

	public void reloadPhaseList() {

		cachedImages.clear();
		timeshotListModel.rebuildList();
 
	}

	public void scrollToPageIndex(int pageIndex) { 
		Rectangle r = timeshotListView.getCellBounds(pageIndex, pageIndex);
		if( r != null)
		    timeshotListView.scrollRectToVisible(r);
		if( pageIndex < timeshotListView.getModel().getSize())
		    timeshotListView.setSelectedIndex(pageIndex);
	}

 
  
	public Dimension getPreferredSize() {
		return new Dimension((int)(Settings.CARD_WIDTH * 3 + Settings.SIDE_MENU_WIDTH * 2),
				(int) (Settings.PREVIEW_HEIGHT / 10.0 * 4.5));
	}

	long lastAddPhaseTime; 

	protected void addNewTimeshot(boolean copy ) {
		// this time checking is used to avoid creating new pages accidently
		long curTime = new Date().getTime();
		if (curTime - lastAddPhaseTime < 500)
			return;
		lastAddPhaseTime = curTime;

		motion.duplicate(motion.getFrameSize()-1);
		  
		timeshotListModel.rebuildList();
		 

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				timeshotListView.ensureIndexIsVisible(timeshotListModel.getSize() - 1);
			}
		});
	}

	protected void insertNewTimeshot(boolean after) {
		 
        int pos =  timeshotListView.getSelectedIndex();
        if( pos < 0 ) return;
        
		motion.duplicate(pos);  
		timeshotListModel.rebuildList();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				timeshotListView.ensureIndexIsVisible(pos);
			}
		});
	}

	protected void deleteSelectedTimeshot() {
	    int pos =  timeshotListView.getSelectedIndex();
	    if( pos <= 0 ) return;
		motion.delete(pos);  
		timeshotListModel.rebuildList();
		timeshotListView.setSelectedIndex(pos);
	}

	public void animationRoutineChanged() {
		cachedImages.clear();
		timeshotListModel.rebuildList();
		 
	}
 

	public void handleMouseEvent(int xoffset, int yoffset, MouseEvent e, int index) {
		int w = Settings.PREVIEW_SCENE_WIDTH / 3;
		int h = Settings.PREVIEW_SCENE_HEIGHT / 3;
		int hit = 25;
		int gap = 5;
		if (e.getClickCount() == 2) {
			showDetailOpsPane(index, e);
			return;
		} 
		if (  yoffset <   gap + hit) {
			 if (xoffset > w - gap - hit && xoffset < w - gap) {
			    insertNewTimeshot(true);
				return;
			}
		}
		if (yoffset > h - hit) {
			if (xoffset > gap && xoffset < hit) {
				int dialogResult = JOptionPane.showConfirmDialog(null, "" + "delete warning" , "Warning",
						JOptionPane.YES_NO_OPTION);
				if (dialogResult == JOptionPane.YES_OPTION) {
					deleteSelectedTimeshot();
				}
				return;
			} else if (xoffset > w - gap - hit && xoffset < w - gap) {
				previewFrom(index);
				return;
			}

		}

		if (xoffset > w && xoffset < w + TransGAP) {
			if (motion.getFrameSize() - 1 == index) {
				showAddNewOptionPane(index, e); 
			} else { 
				repaintCurrentCell(); 
			}
			return;
		}

	}

	private void showDetailOpsPane(final int index, MouseEvent e) {
		JPopupMenu menu = new JPopupMenu();
		  
		 
			JMenuItem addToNextButton = new JMenuItem( "insert Page After" );
			addToNextButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					insertNewTimeshot(true);
				}
			});
			menu.add(addToNextButton);

			JMenuItem previewButton = new JMenuItem( "preview from here" );
			previewButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					previewFrom(index);
				}
			});
			menu.add(previewButton);
 
			 

			JMenuItem deleteButton = new JMenuItem( "delete Page" );
			deleteButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int dialogResult = JOptionPane.showConfirmDialog(null,
							"" +  "confirm Delete Page"  + " ？", "Warning", JOptionPane.YES_NO_OPTION);
					if (dialogResult == JOptionPane.YES_OPTION) {
						deleteSelectedTimeshot();
					}
				}
			});
			menu.add(deleteButton);
			menu.add(new JMenuItem( "cancel" ));
 
		menu.show(timeshotListView, e.getX(), e.getY());
	}

	private void showAddNewOptionPane(int index, MouseEvent e) {
		JPopupMenu menu = new JPopupMenu();
	 
			 
			JMenuItem copyNoLinkButton = new JMenuItem( "copy Endings" );
			copyNoLinkButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					addNewTimeshot(true);
				}
			}); 
			menu.add(copyNoLinkButton);
			menu.add(new JMenuItem( "cancel" ));
		 
		menu.show(timeshotListView, e.getX(), e.getY());

	}



 

	
	

	static final int TransGAP = 50;

	private class DataCellPreviewPanel extends JPanel implements ListCellRenderer<MFrame> {

		private static final long serialVersionUID = -4624762713662343786L;

		private JList list;
		private int index;
		private boolean isSelected;
		private boolean cellHasFocus;
		private MFrame page;
		
	   private LabelButton insertPageButton;
	   
		private LabelButton deletePageButton;
		// private LabelButton hotspotButton;
	 
		private LabelButton previewButton;

		private TransCellPanel transPanel;

		public Component getListCellRendererComponent(final JList list, final MFrame value, final int index,
				final boolean isSelected, final boolean cellHasFocus) {
			setup(list, value, index, isSelected, cellHasFocus);
			return this;
//				if( timeshotListModel.isDataRow(index)) {
//					dataCell.setup(list, value, index, isSelected,
//						   cellHasFocus);
//					return dataCell;
//				} else {
//					transCell.setup(list, value, index, isSelected,
//							   cellHasFocus);
//					return transCell;
//				}
		}

		DataCellPreviewPanel() {
			ImageIcon simage0 = ResourceHelper.getToolIcon("add.png");
				insertPageButton = new LabelButton( simage0);
				insertPageButton.setToolTipText( "inserttip" );
				 
			 	insertPageButton.setPreferredSize(new Dimension(20,20)); 
				insertPageButton.setIconTextGap(0);  
		 		insertPageButton.setText(null);   
	 		 
		  

			ImageIcon simage = ResourceHelper.getToolIcon("delete_circle.png");
			deletePageButton = new LabelButton(simage);
			deletePageButton.setToolTipText("");
			deletePageButton.setPreferredSize(new Dimension(25, 25));
			deletePageButton.setIconTextGap(0);
			deletePageButton.setText(null);

			 

			simage = ResourceHelper.getToolIcon("video.png");
			previewButton = new LabelButton(simage);
			previewButton.setPreferredSize(new Dimension(25, 25));
  
			CellPreview buttonPanel = new CellPreview();
			GridBagLayout layout = new GridBagLayout();
			buttonPanel.setLayout(layout);
			GridBagConstraints s = new GridBagConstraints();
			s.fill = GridBagConstraints.BOTH;
			s.gridx = 3;
			s.gridy = 1;
			s.weightx = 0;
			buttonPanel.add(insertPageButton, s);

			s.gridx = 3;
			s.gridy = 2;
			s.weightx = 0; 
			s.gridx = 2;
			s.gridy = 3;
			s.weighty = 1;
			s.weightx = 1;
			buttonPanel.add(new JLabel(), s); 
			s.gridx = 2;
			s.gridy = 4;
			s.weighty = 1;
			s.weightx = 1;
			buttonPanel.add(new JLabel(), s);

			s.gridx = 0;
			s.gridy = 5;
			s.weightx = 0;
			s.weighty = 0;
			buttonPanel.add(deletePageButton, s);

			s.gridx = 0;
			s.gridy = 4;
			s.weightx = 0;
			s.weighty = 0;
		   // buttonPanel.add(insertBeforePageButton, s);

			s.gridx = 3;
			s.gridy = 4;
			s.weightx = 0;
			// buttonPanel.add(insertPageButton, s);

			s.gridx = 3;
			s.gridy = 5;
			s.weightx = 0;
			buttonPanel.add(previewButton, s);
 
			buttonPanel.setSize(Settings.PREVIEW_SCENE_WIDTH / 3, Settings.PREVIEW_HEIGHT / 3);
			// buttonPanel.setOpaque(false);

			transPanel = new TransCellPanel();
			this.setLayout(new BorderLayout());
			this.add(buttonPanel, BorderLayout.CENTER);
			this.add(transPanel, BorderLayout.EAST);

			setSize(this.getPreferredSize());
		}

		public void setup(final JList list, final MFrame value, final int index, final boolean isSelected,
				final boolean cellHasFocus) {
			this.list = list;
			this.page =  value;
			this.index = index;
			this.isSelected = isSelected;
			this.cellHasFocus = cellHasFocus;

			this.transPanel.setupData(value, isSelected);
			  insertPageButton.setVisible(isSelected);
			
		  //  insertBeforePageButton.setVisible(false);
			 
			deletePageButton.setVisible(isSelected);
		//	timelineButton.setVisible(isSelected);
			previewButton.setVisible(isSelected);
			// hotspotButton.setSelected( page.isHotPhase());

			Color defaultColor = Color.darkGray;
			int defaultLineWidth = 1; 

			if (!isSelected)
				setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2),
						BorderFactory.createLineBorder(defaultColor, defaultLineWidth)));
			else
				setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.red, 3),
						BorderFactory.createLineBorder(defaultColor, defaultLineWidth)));

		}

		public Dimension getPreferredSize() {
			return new Dimension(Settings.PREVIEW_SCENE_WIDTH / 3 + TransGAP, Settings.PREVIEW_HEIGHT / 3);
		}

		private class TransCellPanel extends JPanel {

			private static final long serialVersionUID = 1L;

			// private R9TextField transitionField;
		//	JCheckBox pauseBox;
		//	JCheckBox hotBox;
			// private R9DurationComboBox transitionFieldCombo; 
 
			private JButton addNewPageButton;
			private JPanel newPanel;
			private JPanel transPanel;
			private CardLayout cardLayout;
   

			TransCellPanel() {

				transPanel = new JPanel();
				transPanel.setLayout(new BoxLayout(transPanel, BoxLayout.Y_AXIS));
 	 
				newPanel = new JPanel();
				newPanel.setLayout(new BorderLayout());
				addNewPageButton = new JButton();
				addNewPageButton.setIcon(ResourceHelper.getToolIcon("add.png"));
				addNewPageButton.setToolTipText( "insert " );
				addNewPageButton.setMargin(new Insets(1, 1, 1, 1));
				addNewPageButton.setIconTextGap(0);
				newPanel.add(addNewPageButton, BorderLayout.CENTER);
				cardLayout = new CardLayout();
				this.setLayout(cardLayout);
				this.add(transPanel);
				this.add(newPanel);
				cardLayout.first(this);

			}

			public void setupData(MFrame page, boolean selected) { 
				 
				if (motion.indexOf(page) == motion.getFrameSize() - 1) {
					cardLayout.last(this);
				} else {
					cardLayout.first(this);
				}
			}

			public Dimension getPreferredSize() {
				return new Dimension(TransGAP, Settings.PREVIEW_HEIGHT / 3);
			}
		}

		private class CellPreview extends JPanel {
			public Dimension getPreferredSize() {
				return new Dimension(Settings.PREVIEW_SCENE_WIDTH / 3, Settings.PREVIEW_HEIGHT / 3);
			}

			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				BufferedImage cachedImage = cachedImages.get(page.id + "");
				boolean needRepaint = false;
				if (page.dirty) {
					needRepaint = true;
					page.dirty = false;
//							if( !ApplicationSetting.sharedInstance.showAlignmentDragHandler() ){
//								needRepaint = false;
//								if( AuthoringWorkspace.singleInstance.globalMouseEvent != null &&
//										AuthoringWorkspace.singleInstance.globalMouseEvent.getID() 
//										==  MouseEvent.MOUSE_RELEASED )
//									needRepaint = true;
//							}
				}
				if (needRepaint || cachedImage == null) {
					int w = Settings.PREVIEW_SCENE_WIDTH / 3 - 4;
					int h = Settings.PREVIEW_HEIGHT / 3 - 4;
					
				    Rectangle r = BVHBasePanel.calculateSize(   skeleton,   5 );
					 
				  //  int w2 = Math.max(w, r.width);
				  //  int h2 = Math.max(h, r.height);
					cachedImage = new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_ARGB);

					Graphics2D g2 = (Graphics2D) cachedImage.getGraphics();
					g2.clipRect(0,0, r.width, r.height);
					int frameindex = skeleton.getFrameIndex();
					skeleton.setPose(index);
					int scale = 5; 
				    
			        int tx =  - r.x;
			        int ty =   - r.y;
			        BVHBasePanel.paintFigure(g2, skeleton, null,  tx, ty, scale);
					 
					//cachedImage = cachedImage.getSubimage((w-r.width)/2 , (h-r.height)/2, r.width, r.height);
				 	cachedImage = ImageUtil.scale(cachedImage, w, h);
					skeleton.setPose(frameindex);
					page.dirty = false;
					cachedImages.put(page.id + "", cachedImage);

				}
//						page.doGFXDrawing(getContext(), g, getWidth(),
//								getHeight());
				if (cachedImage != null)
					g.drawImage(cachedImage, 2, 2, null);

				g.drawString(index + "", 20, 20);
				 
				// g.setColor(Color.blue);
				// int w = this.getWidth();
				// int h = this.getHeight();
				// g.drawLine(w/2, 0, w/2, h);
				g.setColor(isSelected ? list.getSelectionBackground() : list.getBackground());

			}
		}

	}	
	
	
	
	
	class TimeshotListModel extends DefaultListModel<MFrame> {
		private static final long serialVersionUID = 1L;

		private long lastSelectedTime = 0;

		public TimeshotListModel() {

		}

		public void itemSelected(int index) {
			lastSelectedTime = new Date().getTime();
		}

		public boolean canSelectItem() {
			return new Date().getTime() > lastSelectedTime + 500;
		}

		public void setElementAt(MFrame element, int index) {
			Motion cxt = getCurMotion();
			if (cxt == null)
				return;
			cxt.set(index, element);
			fireContentsChanged(this, index, index);
		}

		public void add(int index, MFrame item) {
			Motion cxt = getCurMotion();
			if (cxt == null)
				return;
			cxt.add(index, item);
			// rebuildList();
			fireIntervalAdded(this, index, index);
		}

		public MFrame remove(int index) {
			Motion cxt = getCurMotion();
			if (cxt == null)
				return null;

			 MFrame p =  cxt.getData(index);
			 cxt.delete(index);
			fireIntervalRemoved(this, index, index);
			return p;
		}

		public void clear() {
			super.clear();
			Motion cxt = getCurMotion();
			if (cxt == null)
				return;
			cxt.clearAllData();
			rebuildList();
		}

		

		private void rebuildList() {
			this.fireContentsChanged(this, 0, getSize());
		}

		public int getSize() {
			int size = getCurMotion() == null ? 0 : getCurMotion().getFrameSize();
			return /* ruler.useVideo() ? size : */ size;
		}

		public  MFrame getElementAt(int index) {
			if (getCurMotion() == null)
				return new MFrame(-1,new double[0]);
			Motion cxt = getCurMotion();
			return cxt.get(index);
		}

	}




	public void updateFrameIndexSilently(int pos) {
		if( motion == null ) return;
		try {
			blockEventFire = true; 
			try {
			    scrollToPageIndex(pos);
			}catch(Exception ex) {}
		} finally {
			blockEventFire = false;
		} 
	}
	public void reloadCurrentFrame() {
		MFrame f = timeshotListView.getSelectedValue();
		if( f == null ) return;
		cachedImages.put(f.id+"", null);
		repaint();
	}

}
