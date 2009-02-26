package org.overturetool.traces.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.overturetool.traces.utility.TracesHelper;
import org.overturetool.traces.utility.TracesHelper.TestResult;
import org.overturetool.traces.utility.TracesHelper.TestResultType;
import org.overturetool.traces.utility.TracesHelper.TestStatus;

import java.awt.Dimension;

public class MainFrame extends JFrame {

	// private Icon customOpenIcon = new ImageIcon("images/Circle_1.gif");
	// private Icon customClosedIcon = new ImageIcon("images/Circle_2.gif");
	private Boolean expanded = false; // @jve:decl-index=0:
	private Boolean updateResultEnabled = true; // @jve:decl-index=0:

	private TracesHelper th = null;

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JButton jButton = null;
	private JList jListArgs = null;
	private JList jListResult = null;
	private JTextField jTextFieldFileName = null;
	private JScrollBar jScrollBar = null;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private JTree jTreeTraces = null;

	private JScrollPane jScrollPaneTraces = null;

	private JScrollPane jScrollPaneArguments = null;

	private JScrollPane jScrollPaneResults = null;

	private JTextArea jTextAreaTraceDefinition = null;

	private JLabel jLabel3 = null;

	private JButton jButtonRunAll = null;

	private JTextField jTextFieldVDMToolsPath = null;

	private JRadioButton jRadioButtonVDMTools = null;

	private JRadioButton jRadioButtonVDMJ = null;

	private JTextField jTextFieldMax = null;

	private JLabel jLabelMax = null;
	private JButton jButtonFail = null;
	private JButton jButtonAccept = null;
	private JButton jButtonStartManualInspection = null;
	private JPanel jPanelManuelInspection = null;
	private JScrollPane jScrollPaneTraceDefinition = null;
	private JButton jButtonBrows = null;

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabelMax = new JLabel();
			jLabelMax.setBounds(new Rectangle(619, 48, 34, 22));
			jLabelMax.setText("Max");
			jLabel3 = new JLabel();
			jLabel3.setBounds(new Rectangle(200, 47, 118, 16));
			jLabel3.setText("Trace definition");
			jLabel2 = new JLabel();
			jLabel2.setBounds(new Rectangle(456, 236, 60, 16));
			jLabel2.setText("Results");
			jLabel1 = new JLabel();
			jLabel1.setBounds(new Rectangle(198, 236, 91, 16));
			jLabel1.setText("Trace test case");
			jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(12, 49, 52, 16));
			jLabel.setText("Traces");
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getJButton(), null);
			jContentPane.add(getJTextFieldFileName(), null);
			jContentPane.add(jLabel, null);
			jContentPane.add(jLabel1, null);
			jContentPane.add(jLabel2, null);
			jContentPane.add(getJScrollPaneTraces(), null);
			jContentPane.add(getJScrollPaneArguments(), null);
			jContentPane.add(getJScrollPaneResults(), null);
			jContentPane.add(jLabel3, null);
			jContentPane.add(getJButtonRunAll(), null);
			jContentPane.add(getJTextFieldVDMToolsPath(), null);
			jContentPane.add(getJRadioButtonVDMTools(), null);
			jContentPane.add(getJRadioButtonVDMJ(), null);
			jContentPane.add(getJTextFieldMax(), null);
			jContentPane.add(jLabelMax, null);
			jContentPane.add(getJButtonStartManualInspection(), null);
			jContentPane.add(getJPanelManuelInspection(), null);
			jContentPane.add(getJScrollPaneTraceDefinition(), null);
			jContentPane.add(getJButtonBrows(), null);
		}
		return jContentPane;
	}

	/**
	 * Resizes an image using a Graphics2D object backed by a BufferedImage.
	 * 
	 * @param srcImg
	 *            - source image to scale
	 * @param w
	 *            - desired width
	 * @param h
	 *            - desired height
	 * @return - the new resized image
	 */
	private Image getScaledImage(Image srcImg, int w, int h) {
		BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = resizedImg.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(srcImg, 0, 0, w, h, Color.white, null);
		g2.dispose();
		return resizedImg;
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = MainFrame.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setBounds(new Rectangle(759, 75, 118, 44));
			jButton.setText("Expand traces");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("actionPerformed()"); // TODO
					// Auto-generated
					// Event stub
					// actionPerformed()
					try {
						int max = 3;
						try {
							max = Integer.parseInt(jTextFieldMax.getText());
						} catch (Exception e2) {
							// TODO: handle exception
						}
						
						
						
						

						File[] files =new File[] { new File(jTextFieldFileName.getText()) };
						th = new TracesHelper(jTextFieldVDMToolsPath.getText(), files, jRadioButtonVDMJ.isSelected(), max);

						DefaultMutableTreeNode root = new CustomIconNode("Classes", new ImageIcon(getScaledImage(createImageIcon("images/database.png", "").getImage(), 20, 20)));
						// DefaultMutableTreeNode("Classes");
						for (String c : th.GetTraceClasNames()) {
							DefaultMutableTreeNode childClass = new CustomIconNode(c, new ImageIcon(getScaledImage(createImageIcon("images/class.png", "").getImage(), 20, 20)));
							// DefaultMutableTreeNode(c);

							for (String trace : th.GetTraces(c)) {
								DefaultMutableTreeNode childTrace = new CustomIconNode(trace, new ImageIcon(getScaledImage(createImageIcon("images/trace.png", "").getImage(), 20, 20)));
								// DefaultMutableTreeNode(trace);

								for (String testCase : th.GetTraceTestCases(c, trace)) {
									DefaultMutableTreeNode childTestCase = new CustomIconNode(testCase, new ImageIcon(getScaledImage(createImageIcon("images/search.png", "").getImage(), 20, 20)));
									// DefaultMutableTreeNode(testCase);

									childTrace.add(childTestCase);
								}

								childClass.add(childTrace);
							}

							root.add(childClass);
						}
						TracesHelper.PrintErrors();
						
						jScrollPaneTraces.remove(jTreeTraces);

						Point loc = jTreeTraces.getLocation();
						Rectangle rec = jTreeTraces.getBounds();
						jTreeTraces = new JTree(root);
						jTreeTraces.setLocation(loc);
						jTreeTraces.setBounds(rec);
						jTreeTraces.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

						jTreeTraces.setCellRenderer(new CustomTreeCellRenderer());

						jScrollPaneTraces.setViewportView(jTreeTraces);
						
						//jTextAreaTraceDefinition.setText(TracesHelper.GetClassNamesWithTraces(files).toString());
						// jScrollPaneTraces.updateUI();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					jTreeTraces.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
						public void valueChanged(

						javax.swing.event.TreeSelectionEvent e) {
							try {

								if (!updateResultEnabled)
									return;
								DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTreeTraces.getLastSelectedPathComponent();

								if (node == null)
									return;

								Object nodeInfo = node.getUserObject();

								if (!node.isRoot()) {
									String[] path = GetTreeNodeTraceName(node).split(":");
									try {
										jTextAreaTraceDefinition.setText(th.GetTraceDefinitionString(path[1], path[2]));
									} catch (Exception e1) {

									}
									if (path.length >= 4)
										SetNodeResult(path[1], path[2], path[3]);
									else {
										SetNodeResult(null, null, null);
										jPanelManuelInspection.setVisible(false);
										jButtonStartManualInspection.setVisible(true);
									}

									try {
										jButtonStartManualInspection.setEnabled(path.length == 3);
									} catch (Exception e2) {
										// TODO: handle exception
										int i = 0;
									}

								}

								System.out.println("valueChanged(): "
										+ GetTreeNodeTraceName(node)); // TODO
								// Auto-generated
								// Event stub
								// valueChanged()
							} catch (Exception e2) {
								// TODO: handle exception
								System.out.println("ERROR IN TREE NODE SELECTION CHANGED");
								e2.printStackTrace();
							}
						}
					});

				}
			});
		}
		return jButton;
	}

	private void SetNodeResult(String className, String trace, String num) {
		try {
			if (className == null) {
				jListArgs.setListData(new Object[] {});
				jListResult.setListData(new Object[] {});
				return;
			}

			TestResult tmp = th.GetResult(className, trace, num);

			if (tmp == null)
				return;

			SetTreeIcon(className, trace, num,  tmp.Status);
			// argsForList.add(tmp[0]);
			// resultsForList.add(tmp[1].toString());

			jListArgs.setListData(tmp.args);
			jListResult.setListData(tmp.result);
		} catch (Exception e1) {
			jListArgs.setListData(new Object[] {});
			jListResult.setListData(new Object[] {});
		}
	}

	public void SetTreeIcon(String className, String trace, String node, TestResultType status) {
		CustomIconNode root = ((CustomIconNode) jTreeTraces.getModel().getRoot());
		for (int i = 0; i < root.getChildCount(); i++) {
			CustomIconNode classNode = ((CustomIconNode) root.getChildAt(i));
			if (classNode.getUserObject().toString().endsWith(className)) {
				for (int j = 0; j < classNode.getChildCount(); j++) {
					CustomIconNode traceNode = ((CustomIconNode) classNode.getChildAt(j));
					if (traceNode.getUserObject().toString().endsWith(trace)) {
						for (int k = 0; k < traceNode.getChildCount(); k++) {

							CustomIconNode traceCaseNode = ((CustomIconNode) traceNode.getChildAt(k));
							if (traceCaseNode.getUserObject().toString().endsWith(node)) {
								if (status == TestResultType.Ok)
									traceCaseNode.SetIcon(new ImageIcon(getScaledImage(createImageIcon("images/ok.png", "").getImage(), 20, 20)));
								else if (status == TestResultType.Fail) {
									traceCaseNode.SetIcon(new ImageIcon(getScaledImage(createImageIcon("images/faild.png", "").getImage(), 20, 20)));
									jTreeTraces.setSelectionPath(new TreePath(traceCaseNode.getPath()));
								} else if (status == TestResultType.Inconclusive) {
									traceCaseNode.SetIcon(new ImageIcon(getScaledImage(createImageIcon("images/undetermined.png", "").getImage(), 20, 20)));
									//jTreeTraces.setSelectionPath(new TreePath(traceCaseNode.getPath()));

								}else if (status == TestResultType.ExpansionFaild) {
									traceCaseNode.SetIcon(new ImageIcon(getScaledImage(createImageIcon("images/typeFaild.png", "").getImage(), 20, 20)));
									//jTreeTraces.setSelectionPath(new TreePath(traceCaseNode.getPath()));

								}
								jScrollPaneTraces.updateUI();
							}

						}
					}
				}
			}
		}

	}

	public String GetTreeNodeTraceName(DefaultMutableTreeNode node) {
		String result = "";

		if (node == null)
			return "";

		result += node.getUserObject().toString();

		if (!node.isRoot())
			result = GetTreeNodeTraceName((DefaultMutableTreeNode) node.getParent())
					+ ":" + result;

		return result;
	}

	/**
	 * This method initializes jListArgs
	 * 
	 * @return javax.swing.JList
	 */
	private JList getJListArgs() {
		if (jListArgs == null) {
			jListArgs = new JList();
			jListArgs.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
				public void valueChanged(javax.swing.event.ListSelectionEvent e) {
					try {
						jListResult.setSelectedIndex(jListArgs.getSelectedIndex());

					} catch (Exception e2) {
						// TODO: handle exception
						e2.printStackTrace();
					}
					// TODO Auto-generated Event stub valueChanged()
				}
			});
		}
		return jListArgs;
	}

	/**
	 * This method initializes jTextPaneResult
	 * 
	 * @return javax.swing.JTextPane
	 */
	private JList getJListResult() {
		if (jListResult == null) {
			jListResult = new JList();
		}
		return jListResult;
	}

	/**
	 * This method initializes jTextFieldFileName
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldFileName() {
		if (jTextFieldFileName == null) {
			jTextFieldFileName = new JTextField();
			jTextFieldFileName.setBounds(new Rectangle(5, 4, 692, 25));
			// jTextFieldFileName.setText("C:\\Users\\kela\\Desktop\\Overture Traces\\trunk\\src\\overtureTraces\\src\\testData\\tracerepeat\\tracerepeat-05.vdm");
			jTextFieldFileName.setText("C:\\Users\\kela\\Desktop\\tr\\overtureTraces\\src\\documentation\\buffers.vpp");
		}
		return jTextFieldFileName;
	}

	/**
	 * This method initializes jScrollBar
	 * 
	 * @return javax.swing.JScrollBar
	 */
	private JScrollBar getJScrollBar() {
		if (jScrollBar == null) {
			jScrollBar = new JScrollBar();
			jScrollBar.setBounds(new Rectangle(721, 145, 0, 0));
		}
		return jScrollBar;
	}

	/**
	 * This method initializes jTreeTraces
	 * 
	 * @return javax.swing.JTree
	 */
	private JTree getJTreeTraces() {
		if (jTreeTraces == null) {
			jTreeTraces = new JTree(new Object[] {});
		}
		return jTreeTraces;
	}

	/**
	 * This method initializes jScrollPaneTraces
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPaneTraces() {
		if (jScrollPaneTraces == null) {
			jScrollPaneTraces = new JScrollPane();
			jScrollPaneTraces.setBounds(new Rectangle(7, 71, 182, 551));
			jScrollPaneTraces.setViewportView(getJTreeTraces());
		}
		return jScrollPaneTraces;
	}

	/**
	 * This method initializes jScrollPaneArguments
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPaneArguments() {
		if (jScrollPaneArguments == null) {
			jScrollPaneArguments = new JScrollPane();
			jScrollPaneArguments.setBounds(new Rectangle(197, 255, 252, 371));
			jScrollPaneArguments.setViewportView(getJListArgs());
		}
		return jScrollPaneArguments;
	}

	/**
	 * This method initializes jScrollPaneResults
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPaneResults() {
		if (jScrollPaneResults == null) {
			jScrollPaneResults = new JScrollPane();
			jScrollPaneResults.setBounds(new Rectangle(457, 256, 312, 371));
			jScrollPaneResults.setViewportView(getJListResult());
		}
		return jScrollPaneResults;
	}

	/**
	 * This method initializes jTextAreaTraceDefinition
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJTextAreaTraceDefinition() {
		if (jTextAreaTraceDefinition == null) {
			jTextAreaTraceDefinition = new JTextArea();
		}
		return jTextAreaTraceDefinition;
	}

	/**
	 * This method initializes jButtonRunAll
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonRunAll() {
		if (jButtonRunAll == null) {
			jButtonRunAll = new JButton();
			jButtonRunAll.setBounds(new Rectangle(760, 131, 117, 42));
			jButtonRunAll.setText("Run All");
			jButtonRunAll.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						if (!expanded) {
							jButton.doClick();
							expanded = true;
						}
						updateResultEnabled = true;
						ArrayList result = th.RunAll();

						for (int i = 0; i < result.size(); i++) {
							TestStatus tmp = (TestResult) result.get(i);

							// jListArgs.setListData((Object[]) tmp[1]);
							// jListResult.setListData((Object[]) tmp[2]);
							// String[] fff = tmp[0].toString().split(":");

							SetTreeIcon(tmp.ClassName, tmp.TraceName, tmp.TraceTestCaseNumber, tmp.Status);
						}
						updateResultEnabled = true;
						;
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});
		}
		return jButtonRunAll;
	}

	/**
	 * This method initializes jTextFieldVDMToolsPath
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldVDMToolsPath() {
		if (jTextFieldVDMToolsPath == null) {
			jTextFieldVDMToolsPath = new JTextField();
			jTextFieldVDMToolsPath.setBounds(new Rectangle(5, 29, 690, 20));
			jTextFieldVDMToolsPath.setText("c:\\Program Files\\The VDM++ Toolbox v8.2b\\bin\\vppgde.exe");
		}
		return jTextFieldVDMToolsPath;
	}

	/**
	 * This method initializes jRadioButtonVDMTools
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonVDMTools() {
		if (jRadioButtonVDMTools == null) {
			jRadioButtonVDMTools = new JRadioButton();
			jRadioButtonVDMTools.setBounds(new Rectangle(399, 46, 100, 21));
			jRadioButtonVDMTools.setSelected(false);
			jRadioButtonVDMTools.setText("VDM Tools");
			jRadioButtonVDMTools.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					try {

						jRadioButtonVDMJ.setSelected(!jRadioButtonVDMTools.isSelected());
					} catch (Exception e2) {
						// TODO: handle exception
						e2.printStackTrace();
					}
				}
			});
		}
		return jRadioButtonVDMTools;
	}

	/**
	 * This method initializes jRadioButtonVDMJ
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonVDMJ() {
		if (jRadioButtonVDMJ == null) {
			jRadioButtonVDMJ = new JRadioButton();
			jRadioButtonVDMJ.setBounds(new Rectangle(525, 48, 84, 21));
			jRadioButtonVDMJ.setSelected(true);
			jRadioButtonVDMJ.setText("VDMJ");
			jRadioButtonVDMJ.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					try {

						jRadioButtonVDMTools.setSelected(!jRadioButtonVDMJ.isSelected());
					} catch (Exception e2) {
						// TODO: handle exception
						e2.printStackTrace();
					}
				}
			});
		}
		return jRadioButtonVDMJ;
	}

	/**
	 * This method initializes jTextFieldMax
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldMax() {
		if (jTextFieldMax == null) {
			jTextFieldMax = new JTextField();
			jTextFieldMax.setBounds(new Rectangle(653, 50, 28, 20));
			jTextFieldMax.setText("3");
		}
		return jTextFieldMax;
	}

	/**
	 * This method initializes jButtonFail
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonFail() {
		if (jButtonFail == null) {
			jButtonFail = new JButton();
			jButtonFail.setIcon(new ImageIcon(getScaledImage(createImageIcon("images/faild.png", "").getImage(), 20, 20)));
			jButtonFail.setText("Fail");
			jButtonFail.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {

						System.out.println("FAIL actionPerformed()"); // TODO
						// Auto-generated
						// Event
						// stub
						// actionPerformed()
						Object[] j = jTreeTraces.getSelectionPath().getPath();

						String[] tmp = GetTreeNodeTraceName((DefaultMutableTreeNode) j[j.length - 1]).split(":");
						try {
							th.SetFail(tmp[1], tmp[2], tmp[3]);
							SetNodeResult(tmp[1], tmp[2], tmp[3]);

						} catch (CGException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

						// Object[] j =
						// jTreeTraces.getSelectionPath().getPath();

						TestNextTestCase();
						// jTreeTraces.updateUI();
					} catch (Exception e2) {
						// TODO: handle exception
						e2.printStackTrace();
					}
				}

			});
		}
		return jButtonFail;
	}

	private void TestNextTestCase() {
		Object[] j = jTreeTraces.getSelectionPath().getPath();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) j[j.length - 1];

		DefaultMutableTreeNode nodeParent = (DefaultMutableTreeNode) node.getParent();

		DefaultMutableTreeNode nodeToTest = (DefaultMutableTreeNode) nodeParent.getChildAfter(node);

		if (nodeToTest != null) {

			String[] path = GetTreeNodeTraceName(nodeToTest).split(":");

			TestNode(nodeToTest);

			jTreeTraces.setSelectionPath(new TreePath(nodeToTest.getPath()));
		} else {
			try {
				jPanelManuelInspection.setVisible(false);
				jButtonStartManualInspection.setVisible(!jPanelManuelInspection.isVisible());
			} catch (Exception e) {
				// TODO: handle exception
				int i = 0;
			}

		}

	}

	/**
	 * This method initializes jButtonAccept
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonAccept() {
		if (jButtonAccept == null) {
			jButtonAccept = new JButton();
			jButtonAccept.setIcon(new ImageIcon(getScaledImage(createImageIcon("images/ok.png", "").getImage(), 20, 20)));
			jButtonAccept.setText("Accept");
			jButtonAccept.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						System.out.println("OK actionPerformed()"); // TODO
						Object[] j = jTreeTraces.getSelectionPath().getPath();

						String[] tmp = GetTreeNodeTraceName((DefaultMutableTreeNode) j[j.length - 1]).split(":");
						try {
							th.SetOk(tmp[1], tmp[2], tmp[3]);
							SetNodeResult(tmp[1], tmp[2], tmp[3]);

						} catch (CGException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						TestNextTestCase();

					} catch (Exception e2) {
						// TODO: handle exception
						e2.printStackTrace();
					}

					// try {
					// jTreeTraces.invalidate();
					// jTreeTraces.updateUI();
					//
					// } catch (Exception e2) {
					// e2.printStackTrace();
					// }

				}
			});
		}
		return jButtonAccept;
	}

	public void TestNode(DefaultMutableTreeNode n) {
		String[] path = GetTreeNodeTraceName(n).split(":");

		try {

			ArrayList result = th.RunSingle(path[1], path[2], path[3]);

			for (int i = 0; i < result.size(); i++) {
				TestResult tmp = (TestResult) result.get(i);

				// jListArgs.setListData((Object[]) tmp[1]);
				// jListResult.setListData((Object[]) tmp[2]);
				// String[] fff = tmp[0].toString().split(":");

				SetTreeIcon(tmp.ClassName, tmp.TraceName, tmp.TraceTestCaseNumber, tmp.Status);
			}

			;
		} catch (CGException e1) {

		}
	}

	/**
	 * This method initializes jButtonStartManualInspection
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonStartManualInspection() {
		if (jButtonStartManualInspection == null) {
			jButtonStartManualInspection = new JButton();
			jButtonStartManualInspection.setBounds(new Rectangle(774, 255, 162, 62));
			jButtonStartManualInspection.setText("Start manuel inspection");
			jButtonStartManualInspection.setEnabled(false);
			jButtonStartManualInspection.setVisible(true);
			jButtonStartManualInspection.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("Start actionPerformed()"); // TODO
					// Auto-generated
					// Event
					// stub
					// actionPerformed()
					jPanelManuelInspection.setVisible(true);
					jButtonStartManualInspection.setVisible(!jPanelManuelInspection.isVisible());

					Object[] j = jTreeTraces.getSelectionPath().getPath();
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) j[j.length - 1];

					DefaultMutableTreeNode nodeToTest = (DefaultMutableTreeNode) node.getFirstChild();

					String[] path = GetTreeNodeTraceName(nodeToTest).split(":");

					TestNode(nodeToTest);

					jTreeTraces.setSelectionPath(new TreePath(nodeToTest.getPath()));
				}
			});
		}
		return jButtonStartManualInspection;
	}

	/**
	 * This method initializes jPanelManuelInspection
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelManuelInspection() {
		if (jPanelManuelInspection == null) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(2);
			jPanelManuelInspection = new JPanel();
			jPanelManuelInspection.setLayout(gridLayout);
			jPanelManuelInspection.setBounds(new Rectangle(773, 320, 162, 151));
			jPanelManuelInspection.setVisible(false);
			jPanelManuelInspection.add(getJButtonAccept(), null);
			jPanelManuelInspection.add(getJButtonFail(), null);

		}
		return jPanelManuelInspection;
	}

	/**
	 * This method initializes jScrollPaneTraceDefinition	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPaneTraceDefinition() {
		if (jScrollPaneTraceDefinition == null) {
			jScrollPaneTraceDefinition = new JScrollPane();
			jScrollPaneTraceDefinition.setBounds(new Rectangle(199, 73, 553, 158));
			jScrollPaneTraceDefinition.setViewportView(getJTextAreaTraceDefinition());
		}
		return jScrollPaneTraceDefinition;
	}

	/**
	 * This method initializes jButtonBrows	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonBrows() {
		if (jButtonBrows == null) {
			jButtonBrows = new JButton();
			jButtonBrows.setBounds(new Rectangle(702, 8, 27, 21));
			jButtonBrows.setText("...");
			jButtonBrows.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {

					 JFileChooser fc = new JFileChooser();
						int returnVal = fc.showOpenDialog(MainFrame.this);

					        if (returnVal == JFileChooser.APPROVE_OPTION) {
					            jTextFieldFileName.setText( fc.getSelectedFile().getAbsolutePath());
					            
					        }

				}
			});
		}
		return jButtonBrows;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainFrame thisClass = new MainFrame();
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.setVisible(true);
			}
		});
	}

	/**
	 * This is the default constructor
	 */
	public MainFrame() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(959, 664);
		this.setContentPane(getJContentPane());

		this.setTitle("VDM Traces");
	}

} // @jve:decl-index=0:visual-constraint="10,10"
