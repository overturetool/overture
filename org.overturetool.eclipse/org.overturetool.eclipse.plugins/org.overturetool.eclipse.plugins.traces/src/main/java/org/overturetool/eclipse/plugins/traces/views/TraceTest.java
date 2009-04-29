package org.overturetool.eclipse.plugins.traces.views;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.*;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.overturetool.eclipse.plugins.traces.OvertureTracesPlugin;
import org.overturetool.traces.utility.*;
import org.overturetool.traces.utility.TracesHelper.TestResult;
import org.overturetool.traces.utility.TracesHelper.TestResultType;
import org.overturetool.eclipse.plugins.traces.views.*;
import org.overturetool.eclipse.plugins.traces.views.TracesTreeView.*;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class TraceTest extends ViewPart implements ISelectionListener {
	private TableViewer viewer;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;

	private class Data {
		String traceDef;
		String description;
		TestResultType status;

		private Data(String traceDef, String Description, TestResultType status) {
			this.description = Description;
			this.traceDef = traceDef;
			this.status = status;
		}
		
		public String GetDescription(){return this.description;}

	}

	/*
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view,
	 * or ignore it and always show the same content (like Task List, for
	 * example).
	 */

	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object inputElement) {
			return ((List) inputElement).toArray();
		}

	}

	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public String getColumnText(Object element, int columnIndex) {
			Data data = (Data) element;
			String columnText;
			switch (columnIndex) {
			case 0:
				columnText = data.traceDef;
				break;
			case 1:
				columnText = data.description; // StringConverter.asString(data.number);
				break;
			case 2:
				columnText = ""; // StringConverter.asString(data.number);
				break;
			default:
				columnText = "not set";
			}
			return columnText;

		}

		public Image getColumnImage(Object obj, int index) {
			if (index == 2) {
				return getImage(obj);
			}
			return null;
		}

		public Image getImage(Object obj) {
			Data data = (Data) obj;
//			if (data.status == TestResultType.Fail) {
//				return OvertureTracesPlugin.getImageDescriptor(
//						OvertureTracesPlugin.IMG_TRACE_TEST_CASE_FAIL)
//						.createImage();
//			}
//			if (data.status == TestResultType.Ok) {
//				return OvertureTracesPlugin.getImageDescriptor(
//						OvertureTracesPlugin.IMG_TRACE_TEST_CASE_SUCCES)
//						.createImage();
//			}
			
			String imgPath = OvertureTracesPlugin.IMG_TRACE_TEST_CASE_UNKNOWN;
			
			if (data.status  == TestResultType.Ok)
				imgPath = OvertureTracesPlugin.IMG_TRACE_TEST_CASE_SUCCES;
			else if (data.status  == TestResultType.Unknown)
				imgPath = OvertureTracesPlugin.IMG_TRACE_TEST_CASE_UNKNOWN;
			else if (data.status  == TestResultType.Inconclusive)
				imgPath = OvertureTracesPlugin.IMG_TRACE_TEST_CASE_UNDETERMINED;
			else if (data.status  == TestResultType.Fail)
				imgPath = OvertureTracesPlugin.IMG_TRACE_TEST_CASE_FAIL;
			else if (data.status  == TestResultType.ExpansionFaild)
				imgPath = OvertureTracesPlugin.IMG_TRACE_TEST_CASE_EXPANSIN_FAIL;
			else if (data.status == TestResultType.Skipped)
				imgPath = OvertureTracesPlugin.IMG_TRACE_TEST_CASE_SKIPPED;

			return OvertureTracesPlugin.getImageDescriptor(imgPath)
					.createImage();
			
			
			

			// return
			// PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);

		}
	}

	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public TraceTest() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.H_SCROLL
				| SWT.V_SCROLL);
		// test setup columns...
		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(50, 100, true));
		layout.addColumnData(new ColumnWeightData(50, 100, true));
		//layout.addColumnData(new ColumnWeightData(50, 100, true));
		viewer.getTable().setLayout(layout);
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setSortDirection(SWT.NONE);
		viewer.setSorter(null);

		TableColumn column = new TableColumn(viewer.getTable(), SWT.LEFT);
		column.setText("Trace Test case");
		column.setToolTipText("Trace Name");

		TableColumn column2 = new TableColumn(viewer.getTable(), SWT.LEFT);
		column2.setText("Result");
		column2.setToolTipText("Show Description");

//		TableColumn column3 = new TableColumn(viewer.getTable(), SWT.LEFT);
//		column3.setText("Verdict");
//		column3.setToolTipText("Show verdict");

		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		// viewer.setSorter(new NameSorter());

		// input

		// viewer.setInput(getViewSite());

		makeActions();
		//hookContextMenu();
		hookDoubleClickAction();
		//contributeToActionBars();

		getViewSite().getPage().addSelectionListener(this);

	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				TraceTest.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				showMessage("Action 1 executed");
				for (IViewDescriptor v : getViewSite().getWorkbenchWindow()
						.getWorkbench().getViewRegistry().getViews()) {
					if (v.getId().equals(
							"org.overturetool.traces.views.TracesView")) {
						this.addListenerObject(v);
					}
				}
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				if(obj instanceof Data)
				showMessage(((Data) obj).GetDescription().toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(),
				"Trace Test", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// TODO Auto-generated method stub
		if (selection instanceof IStructuredSelection) {
			Object first = ((IStructuredSelection) selection).getFirstElement();
			if (first instanceof TraceTestCaseTreeNode
					&& part instanceof TracesTreeView) {
				TraceTestCaseTreeNode traceTestCaseNode = (TraceTestCaseTreeNode) first;
				TraceTreeNode traceNode = (TraceTreeNode) traceTestCaseNode
						.getParent();
				ClassTreeNode classNode = (ClassTreeNode) traceNode.getParent();
				ProjectTreeNode projectNode = (ProjectTreeNode) classNode
						.getParent();

				TracesHelper tr = ((TracesTreeView) part)
						.GetTracesHelper(projectNode.getName());
				try {
					TestResult res = tr.GetResult(classNode.getName(),
							traceNode.getName(), traceTestCaseNode.getName());

					List<Data> list = new ArrayList<Data>();
					//Set<Data> set = new HashSet<Data>();
					for (int i = 0; i < res.args.length; i++) {
						
						if( res.result.length>i)
						list.add(new Data(res.args[i], res.result[i], res.Status));
						else if( res.result.length<=i)
							list.add(new Data(res.args[i], "", res.Status));
					}

					viewer.setInput(list);

				} catch (CGException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}
}