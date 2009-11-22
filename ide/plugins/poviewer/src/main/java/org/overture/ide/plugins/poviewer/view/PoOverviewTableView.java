package org.overture.ide.plugins.poviewer.view;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.overture.ide.plugins.poviewer.PoviewerPluginConstants;
import org.overture.ide.utility.FileUtility;
import org.overture.ide.utility.ProjectUtility;
import org.overturetool.vdmj.pog.ProofObligation;


public class PoOverviewTableView  extends ViewPart implements ISelectionListener
{
	
	
	private TableViewer viewer;
	private Action doubleClickAction;
	final Display display = Display.getCurrent();
	private IProject project;


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
			ProofObligation data = (ProofObligation) element;
			String columnText;
			switch (columnIndex)
			{
			case 0:
				columnText = data.name;
				break;
			case 1:
				columnText = data.kind.toString();
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
	public PoOverviewTableView()
	{
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		viewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.H_SCROLL
				| SWT.V_SCROLL);
		// test setup columns...
		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(100, 40, true));
		layout.addColumnData(new ColumnWeightData(60, 35, false));
		layout.addColumnData(new ColumnWeightData(25, 25, false));
		viewer.getTable().setLayout(layout);
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setSortDirection(SWT.NONE);
		viewer.setSorter(null);

		TableColumn column = new TableColumn(viewer.getTable(), SWT.LEFT);
		column.setText("PO Name");
		column.setToolTipText("PO Name");

		TableColumn column2 = new TableColumn(viewer.getTable(), SWT.LEFT);
		column2.setText("Type");
		column2.setToolTipText("Show Type");

		TableColumn column3 = new TableColumn(viewer.getTable(), SWT.LEFT);
		column3.setText("Status");
		column3.setToolTipText("Show status");

		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		
	
		makeActions();
		
		hookDoubleClickAction();
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				
				Object first = ((IStructuredSelection) event.getSelection()).getFirstElement();
				if(first instanceof ProofObligation){
					try {
						IViewPart v = getSite().getPage().showView(
								PoviewerPluginConstants.PoTableViewId);
						
						if(v instanceof PoTableView)
							((PoTableView)v).setDataList(project,(ProofObligation) first);
					} catch (PartInitException e) {
						
						e.printStackTrace();
					}
				}
				
			}
		});
	}

	

	private void makeActions()
	{
		doubleClickAction = new Action()
		{
			@Override
			public void run()
			{
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				if (obj instanceof ProofObligation)
				{
					gotoDefinition((ProofObligation)obj);
					//showMessage(((ProofObligation) obj).toString());
				}
			}

			private void gotoDefinition(ProofObligation po) {
			IFile file=	ProjectUtility.findIFile(project, po.location.file);
			FileUtility.gotoLocation(file, po.location, po.name);
				
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

//	private void showMessage(String message)
//	{
//		MessageDialog.openInformation(
//				viewer.getControl().getShell(),
//				"PO Test",
//				message);
//	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus()
	{
		viewer.getControl().setFocus();
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection)
	{

		if (selection instanceof IStructuredSelection && part instanceof PoOverviewTableView)
		{
			Object first = ((IStructuredSelection) selection).getFirstElement();
			if(first instanceof ProofObligation){
				try {
					IViewPart v = part
					.getSite()
					.getPage().showView(
							"org.overture.ide.plugins.poviewer.views.PoTableView");
					
					if(v instanceof PoTableView)
						((PoTableView)v).setDataList(project,(ProofObligation) first);
				} catch (PartInitException e) {
					
					e.printStackTrace();
				}
			}
		}


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
	
	public void setDataList(final IProject project,final List<ProofObligation> data)
	{
		this.project = project;
		display.asyncExec(new Runnable()
		{

			public void run()
			{
				viewer.setInput(data);
			}

		});
	}
}
