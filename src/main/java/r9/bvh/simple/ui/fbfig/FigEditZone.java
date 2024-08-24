package r9.bvh.simple.ui.fbfig;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileFilter;

import r9.bvh.simpe.utils.NodeHelper;
import r9.bvh.simpe.utils.NodePopupMenuHelper;
import r9.bvh.simpe.utils.NodePopupMenuHelper.NodeCallback;
import r9.bvh.simpe.utils.PropertyUIHelper;
import r9.bvh.simpe.utils.R9Properties;
import r9.bvh.simple.Settings;
import r9.bvh.simple.model.NodeBase;
import r9.bvh.simple.model.Skeleton;

public class FigEditZone extends JPanel {

	private static final long serialVersionUID = 1L;
	private FigEditCanvas skeletonCanvas;
	// private DiscreteSlider timeSlider;
	private JScrollPane figEditCanvasScrollPane;

	FbFigWorkspace workspace;
	private JLabel statusLabel;
	private JSlider zoomSlider;
	private JLabel zoomLabel;
	private JButton loadImageButton;
	private BufferedImage bgImage;
	private JTextField nameField;
	private JButton saveButton;
	private JButton deleteImageButton;

	public FigEditZone(final FbFigWorkspace workspace) {
		this.workspace = workspace;
		skeletonCanvas = new FigEditCanvas();
		JPanel bottom = new JPanel();
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));

		nameField = new JTextField(10);
		nameField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (skeletonCanvas.highlight != null)
					skeletonCanvas.highlight.setName(nameField.getText());
			}
		});

		bottom.add(PropertyUIHelper.createRow("节点名称", new JLabel(), nameField));

		zoomLabel = new JLabel();
		zoomSlider = new JSlider();
		zoomSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int v = zoomSlider.getValue();
				double zoom = 1;
				if (v == 10)
					zoom = 1;
				else if (v > 10)
					zoom = v - 10;
				else {
					zoom = v / 10.0;
				}
				skeletonCanvas.setScale(zoom);
				zoomLabel.setText(zoom + "");
			}
		});
		zoomSlider.setMinimum(1);
		zoomSlider.setMaximum(50);
		zoomSlider.setValue(10); // zoom level is one
		zoomSlider.setPreferredSize(new Dimension(200, 25));
		bottom.add(PropertyUIHelper.createRow("zoom level", zoomLabel, zoomSlider));

		loadImageButton = new JButton("导入图片");
		loadImageButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(Settings.homedir);
				chooser.setFileFilter(new FileFilter() {
					@Override
					public boolean accept(File f) {
						return f.isDirectory() || f.getName().endsWith("png") || f.getName().endsWith("jpg");
					}

					@Override
					public String getDescription() {
						return "图片";
					}
				});

				int selection = chooser.showOpenDialog(null);
				if (selection == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					try {
						bgImage = ImageIO.read(file);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		deleteImageButton = new JButton("删除");
		deleteImageButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bgImage = null;
			}
		});
		bottom.add(PropertyUIHelper.createRow("动画对象图片", loadImageButton, deleteImageButton));

		saveButton = new JButton("保存");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				skeletonCanvas.save();

			}
		});
		bottom.add(PropertyUIHelper.createRow("", saveButton));

		statusLabel = new JLabel();
		statusLabel.setPreferredSize(new Dimension(400, 25));
		bottom.add(statusLabel);

		figEditCanvasScrollPane = new JScrollPane(skeletonCanvas);
		figEditCanvasScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		figEditCanvasScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		setLayout(new BorderLayout());
		add(figEditCanvasScrollPane, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
	}

	public void update(CharacterConfig data) {
		nameField.setText("");
		skeletonCanvas.update(data);
	}

	public void highlight(NodeBase<NodeBase> data) {
		skeletonCanvas.highlight(data);
		nameField.setText(data.getName());
	}

	public void highlightByMouseClick(NodeBase<NodeBase> data) {
		workspace.highlightByMouseClick(data);

		nameField.setText(data == null ? "" : data.getName());
	}

	

	public class FigEditCanvas extends FigBasePanel {

		private static final long serialVersionUID = 1L;

	

		//private int tx; 
		//private int ty;
		private boolean duringDragging;

		public FigEditCanvas() {
			MouseAdapter ma = new MouseAdapter() {
				Point2D.Double prevP;

				private NodeBase getHitNode(MouseEvent me) {
					for (NodeBase<NodeBase> node : getSkeleton().getNodes()) {
						double x = node.getPosition().getX();
						double y = node.getPosition().getY();
						if ((me.getX() - x) * (me.getX() - x) + (y - me.getY()) * (y - me.getY()) < 81) {
							return node;
						}
					}
					return null;
				}

				public void mousePressed(MouseEvent me) {
					if (me.isPopupTrigger()) {
						handlePopup(me);
						return;
					}
					prevP = new Point2D.Double(me.getX(), me.getY());
					NodeBase hit = getHitNode(me);
					if (hit == highlight)
						return;
					highlight = hit;

					highlightByMouseClick(highlight);
					repaint();
				}

				private void createNewNode(String newName, int offsetX, int offsetY) {
					NodeBase<NodeBase> n = new NodeBase<>();
					n.setName(newName);
					if (offsetX == 0 && offsetY == 0) {
						offsetX = 50;
						offsetY = 50;
					}
					n.getPosition().setX(highlight.getPosition().getX() + offsetX);
					n.getPosition().setY(highlight.getPosition().getY() + offsetY);
					highlight.getChildrens().add(n);
					n.setParent(highlight);
					getSkeleton().getNodes().add(n);
					highlight = n;
					repaint();
				}

				private void handlePopup(MouseEvent me) {
					if (highlight == null)
						return;
					final JPopupMenu menu = new JPopupMenu();
					menu.add(PropertyUIHelper.createTitleRow("创建子节点"));
					final JTextField aname = new JTextField(20);
					menu.add(PropertyUIHelper.createRow("名称", aname));
					JMenu submenu = new JMenu("预设定节点名：");
					menu.add(submenu);
					CharacterConfig defaultOne = FbCharacterFigRegister.sharedInstance.getDefaultOne();
					NodeBase<NodeBase> nn = NodeHelper.find(defaultOne.getNodes(), highlight.getName());
					NodePopupMenuHelper.setupMenu(submenu, nn == null ? defaultOne.getRootNode() : nn,
							new NodeCallback() {
								public void nodeSelected(NodeBase node) {
									if (node == null)
										return;
									double offsetX = 0;
									double offsetY = 0;
									if (node.getParent() != null) {
										double px = node.getParent().getPosition().getX();
										double py = node.getParent().getPosition().getY();
										double x = node.getPosition().getX();
										double y = node.getPosition().getY();
										offsetX = x - px;
										offsetY = y - py;
									}
									createNewNode(node.getName(), (int) offsetX, (int) offsetY);
								}
							});
					JMenuItem item = new JMenuItem("Close");
					item.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							menu.setVisible(false);
						}
					});
					menu.add(item);
					menu.addPopupMenuListener(new PopupMenuListener() {
						public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
						}

						public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
							String name = aname.getText().trim();
							if (name.length() == 0)
								return;
							createNewNode(name, 50, 50);
						}

						public void popupMenuCanceled(PopupMenuEvent e) {
						}
					});
					menu.show(FigEditCanvas.this, me.getX(), me.getY());
				}

				public void mouseReleased(MouseEvent me) {
					if (me.isPopupTrigger()) {
						handlePopup(me);
						return;
					}
					if (duringDragging && highlight != null && prevP != null) {
						updateNodeLocBy(me);
					}
					duringDragging = false;
					prevP = null;
				}

				public void mouseDragged(MouseEvent me) {
					if (highlight == null)
						return;
					duringDragging = true;
					//
				}

				public void mouseClicked(MouseEvent me) {
					CharacterConfig skeleton = getSkeleton();
					if (skeleton.getNodes().isEmpty()) {
						NodeBase<NodeBase> n = new NodeBase<>();
						n.setName("root");
						n.getPosition().setX(me.getX());
						n.getPosition().setY(me.getY());
						skeleton.setRootNode(n);
						skeleton.getNodes().add(n);
						highlight = n;
						repaint();
						return;
					}
				}

				public void updateNodeLocBy(MouseEvent me) {
					int xChange = me.getX() - (int) prevP.getX();
					int yChange = (me.getY() - (int) prevP.getY());
					if (highlight == null || (xChange == 0 && yChange == 0))
						return;
					System.out.println(highlight.getPosition());

					double xo = highlight.getPosition().getX();
					double yo = highlight.getPosition().getY();
					double zo = highlight.getPosition().getZ();

					double x = xo + xChange;
					double y = yo + yChange;
					double z = zo;
					System.out.println(" highlight(changed) px = " + x + "  py = " + y + " pz = " + zo);

					highlight.getPosition().setX(x);
					highlight.getPosition().setY(y);
					workspace.undoManager.pushAction(new FigNodeLocChange(workspace, highlight, xo, yo, zo, x, y, z));

					repaint();
				}
			};
			this.addMouseListener(ma);
			this.addMouseMotionListener(ma);
		}

	 

		public void update(CharacterConfig data) {
			this.setSkeleton(data);  
			highlight = null;
			mapFigSpaceToCanvas();
			repaint();
		}

		public void save() {
			CharacterConfig skeleton = getSkeleton();
			double scale = getScale();
			Rectangle r = calculateSize(skeleton, 1);
			for (int n = 0; n < skeleton.getNodes().size(); n++) {
				NodeBase<NodeBase> node = skeleton.getNodes().get(n);
				int x1 = (int) ((node.getPosition().getX() - r.getX()) / scale);
				int y1 = (int) ((node.getPosition().getY() - r.getY()) / scale);
				node.getPosition().setX(x1);
				node.getPosition().setY(y1);
			}
			skeleton.setWidth((int) (r.getWidth() / scale));
			skeleton.setHeight((int) (r.getHeight() / scale));

			if (highlight != null)
				highlight.setName(nameField.getText());
			FbCharacterFigRegister.sharedInstance.save(skeleton);
			// change mode back to be in canvas space
			update(skeleton);
		}

	

	 
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			CharacterConfig skeleton = getSkeleton();
			double scale = getScale();
			if (skeleton == null && bgImage == null)
				return;
			int w = getWidth();
			int h = getHeight();
			if (bgImage != null) {
				int wi = (int) (bgImage.getWidth() * scale);
				int hi = (int) (bgImage.getHeight() * scale);
				int tx = (w - wi) / 2;
				int ty = (h - hi) / 2;
				g2d.drawImage(bgImage, tx, ty, tx+wi, ty+hi, 0, 0, bgImage.getWidth(), bgImage.getHeight(), null);
			}
			if (skeleton == null)
				return;
			R9Properties props = R9Properties.getSharedProperties();
			int dotSize = props.dotSize();
			for (int n = 0; n < skeleton.getNodes().size(); n++) {
				NodeBase<NodeBase> node = skeleton.getNodes().get(n);
				int x1 = (int) (node.getPosition().getX());
				int y1 = (int) (node.getPosition().getY());

				g2d.setColor(node == highlight ? Color.RED : Color.BLACK);
				g2d.fillOval((int) (x1 - dotSize/2), (int) (y1 - dotSize/2), dotSize, dotSize);

				g2d.drawString(node.getName(), x1 + dotSize + 10, y1 - 5);
 
				g2d.setColor(Color.BLACK);
				for (NodeBase child : node.getChildrens()) {
					int x2 = (int) (child.getPosition().getX());
					int y2 = (int) (child.getPosition().getY());
					g2d.drawLine(x1, y1, x2, y2);
				}
			}
		}

	}

}
