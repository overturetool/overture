package org.overture.ide.plugins.proofsupport.views.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.IViewDescriptor;


public class PoTableView  extends ViewPart implements ISelectionListener
{
	
	
	private TableViewer viewer;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;
	final Display display = Display.getCurrent();
	

	/*
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view,
	 * or ignore it and always show the same content (like Task List, for
	 * example).
	 */

	class ViewContentProvider implements IStructuredContentProvider
	{
		public void inputChanged(Viewer v, Object oldInput, Object newInput)
		{
		}

		public void dispose()
		{
		}

		@SuppressWarnings("unchecked")
		public Object[] getElements(Object inputElement)
		{
			if(inputElement instanceof List)
			{
			List list = (List) inputElement;
			return list.toArray();
			}
			return new Object[0];
		}

	}

	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider
	{
		public String getColumnText(Object element, int columnIndex)
		{
			Data data = (Data) element;
			String columnText;
			switch (columnIndex)
			{
			case 0:
				columnText = data.getPoId();
				break;
			case 1:
				columnText = data.getVdmPo(); // StringConverter.asString(data.number);
				break;
			case 2:
				columnText = data.getStatus(); // StringConverter.asString(data.number);
				break;
			default:
				columnText = "not set";
			}
			return columnText;

		}

		public Image getColumnImage(Object obj, int index)
		{
			if (index == 2)
			{
				return getImage(obj);
			}
			return null;
		}

		
	}

	class IdSorter extends ViewerSorter
	{
	}

	/**
	 * The constructor.
	 */
	public PoTableView()
	{
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
		viewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.H_SCROLL
				| SWT.V_SCROLL);
		// test setup columns...
		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(50, 100, true));
		layout.addColumnData(new ColumnWeightData(50, 100, true));
		layout.addColumnData(new ColumnWeightData(50, 100, true));
		viewer.getTable().setLayout(layout);
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setSortDirection(SWT.NONE);
		viewer.setSorter(null);

		TableColumn column = new TableColumn(viewer.getTable(), SWT.LEFT);
		column.setText("PO");
		column.setToolTipText("PO Number");

		TableColumn column2 = new TableColumn(viewer.getTable(), SWT.LEFT);
		column2.setText("Expression");
		column2.setToolTipText("Show Expression");

		TableColumn column3 = new TableColumn(viewer.getTable(), SWT.LEFT);
		column3.setText("Status");
		column3.setToolTipText("Show status");

		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		//viewer.setSorter(new IdSorter());

		// input

		// viewer.setInput(getViewSite());

		makeActions();
		// hookContextMenu();
		hookDoubleClickAction();
		// contributeToActionBars();

		getViewSite().getPage().addSelectionListener("org.overturetool.eclipse.plugins.traces.views.TracesView",this);
		
	}

	// private void hookContextMenu() {
	// MenuManager menuMgr = new MenuManager("#PopupMenu");
	// menuMgr.setRemoveAllWhenShown(true);
	// menuMgr.addMenuListener(new IMenuListener() {
	// public void menuAboutToShow(IMenuManager manager) {
	// TraceTest.this.fillContextMenu(manager);
	// }
	// });
	// Menu menu = menuMgr.createContextMenu(viewer.getControl());
	// viewer.getControl().setMenu(menu);
	// getSite().registerContextMenu(menuMgr, viewer);
	// }
	//
	// private void contributeToActionBars() {
	// IActionBars bars = getViewSite().getActionBars();
	// fillLocalPullDown(bars.getMenuManager());
	// fillLocalToolBar(bars.getToolBarManager());
	// }

	// private void fillLocalPullDown(IMenuManager manager) {
	// manager.add(action1);
	// manager.add(new Separator());
	// manager.add(action2);
	// }
	//
	// private void fillContextMenu(IMenuManager manager) {
	// manager.add(action1);
	// manager.add(action2);
	// // Other plug-ins can contribute there actions here
	// manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	// }
	//
	// private void fillLocalToolBar(IToolBarManager manager) {
	// manager.add(action1);
	// manager.add(action2);
	// }

	private void makeActions()
	{
		action1 = new Action()
		{
			public void run()
			{
				showMessage("Action 1 executed");
				for (IViewDescriptor v : getViewSite().getWorkbenchWindow().getWorkbench().getViewRegistry().getViews())
				{
					if (v.getId().equals(
							"org.overturetool.traces.views.TracesView"))
					{
						this.addListenerObject(v);
					}
				}
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
				ISharedImages.IMG_OBJS_INFO_TSK));

		action2 = new Action()
		{
			public void run()
			{
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
				ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action()
		{
			public void run()
			{
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				if (obj instanceof Data)
					showMessage(((Data) obj).toString());
			}
		};
	}

	private void hookDoubleClickAction()
	{
		viewer.addDoubleClickListener(new IDoubleClickListener()
		{
			public void doubleClick(DoubleClickEvent event)
			{
				doubleClickAction.run();
			}
		});
	}

	private void showMessage(String message)
	{
		MessageDialog.openInformation(
				viewer.getControl().getShell(),
				"PO Test",
				message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		viewer.getControl().setFocus();
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection)
	{

//		if (selection instanceof IStructuredSelection && part instanceof TracesTreeView)
//		{
//			Object first = ((IStructuredSelection) selection).getFirstElement();
////			System.out.println(first);
//			if (first instanceof TraceTestTreeNode
//					&& part instanceof TracesTreeView
//					&& !(first instanceof NotYetReadyTreeNode)
//					&& !(first instanceof TraceTestGroup))
//			{
//				TraceTestTreeNode traceTestCaseNode = (TraceTestTreeNode) first;
//			
//				TraceTestResult res = traceTestCaseNode.GetResult();
//				
//				List<Data> list = new ArrayList<Data>();
//
//				for (int i = 0; res != null && i < res.getArguments().size(); i++)
//				{
//
//					if (res.getResults().size() > i)
//						list.add(new Data(res.getArguments().get(i),
//								res.getResults().get(i), res.getStatus()));
//					else if (res.getResults().size() <= i)
//						list.add(new Data(res.getArguments().get(i), "N / A",
//								res.getStatus()));
//				}
//
//				viewer.setInput(list);
//
//			}else
//			{
//				viewer.setInput(null);
//			
//			}
//			refreshList();
//		}

	}
	public void refreshList()
	{
		display.asyncExec(new Runnable()
		{

			public void run()
			{
				viewer.refresh();
			}

		});
	}
	
	public void setDataList(final List<Data> data)
	{
		display.asyncExec(new Runnable()
		{

			public void run()
			{
				viewer.setInput(data);
			}

		});
	}
}
