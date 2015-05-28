/*
 * #%~
 * org.overture.ide.plugins.poviewer
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.plugins.poviewer.view;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;
import org.overture.ide.core.ElementChangedEvent;
import org.overture.ide.core.ElementChangedEvent.DeltaType;
import org.overture.ide.core.IElementChangedListener;
import org.overture.ide.core.IVdmElement;
import org.overture.ide.core.IVdmElementDelta;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.VdmCore;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.plugins.poviewer.IPoviewerConstants;
import org.overture.ide.plugins.poviewer.PoGeneratorUtil;
import org.overture.ide.ui.utility.EditorUtility;
import org.overture.pog.obligation.ProofObligation;
import org.overture.pog.pub.IProofObligation;

public class PoOverviewTableView extends ViewPart implements ISelectionListener {

	protected TableViewer viewer;
	protected Action doubleClickAction;
	protected Display display = Display.getCurrent();
	protected IVdmProject project;

	protected class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof List) {
				@SuppressWarnings("rawtypes")
				List list = (List) inputElement;
				return list.toArray();
			}
			return new Object[0];
		}

	}

	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		public void resetCounter() {
			count = 0;
		}

		private Integer count = 0;

		public String getColumnText(Object element, int columnIndex) {
			ProofObligation data = (ProofObligation) element;
			String columnText;
			switch (columnIndex) {
			case 0:
				count++;
				columnText = new Integer(data.number).toString();// count.toString();
				break;
			case 1:
				if (!data.getLocation().getModule().equals("DEFAULT"))
					columnText = data.getLocation().getModule() + "`"
							+ data.name;
				else
					columnText = data.name;
				break;
			case 2:
				columnText = data.kind.toString();
				break;
			default:
				columnText = "not set";
			}
			return columnText;

		}

		@Override
		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

	}


	private IElementChangedListener vdmlistner = new IElementChangedListener() {

		@Override
		public void elementChanged(ElementChangedEvent event) {

			if (event.getType() == DeltaType.POST_RECONCILE) {

				if (event.getDelta().getKind() == IVdmElementDelta.F_TYPE_CHECKED) {

					final IVdmElement source = event.getDelta().getElement();

					final UIJob showJob = new UIJob("Generating Proof Obligations") {

						@Override
						public IStatus runInUIThread(IProgressMonitor monitor) {

							if (source instanceof IVdmModel) {
								IVdmModel castSource = (IVdmModel) source;
								IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
								if (!page.getPerspective().getId().equals(IPoviewerConstants.ProofObligationPerspectiveId))
								{
									return new Status(IStatus.OK,
											"org.overture.ide.plugins.poviewer", "Ok");
								}
								PoGeneratorUtil util = new PoGeneratorUtil(
										display.getActiveShell(), page
												.getActivePart().getSite());

								if (!util.isPoggedModel(castSource)){
									return new Status(IStatus.OK,
											"org.overture.ide.plugins.poviewer", "Ok");
								}
								util.generate(castSource);

								System.out.println("built something");
							}

							if (viewer != null && viewer.getControl() != null
									&& viewer.getControl().getDisplay() != null)
								viewer.getControl().getDisplay()
										.asyncExec(new Runnable() {
											/*
											 * (non-Javadoc)
											 * 
											 * @see java.lang.Runnable#run()
											 */
											public void run() {
												if (!viewer.getControl()
														.isDisposed()) {
													viewer.refresh();
												}
											}
										});

							return new Status(IStatus.OK,
									"org.overture.ide.plugins.poviewer", "Ok");
						}

					};
					showJob.schedule();

				}

			}

		}

	};

	/**
	 * The constructor.
	 */
	public PoOverviewTableView() {
		VdmCore.addElementChangedListener(vdmlistner);
	}

	@Override
	public void dispose() {
		super.dispose();
	VdmCore.removeElementChangedListener(vdmlistner);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.H_SCROLL
				| SWT.V_SCROLL);
		// test setup columns...
		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(20, true));
		layout.addColumnData(new ColumnWeightData(30, true));
		layout.addColumnData(new ColumnWeightData(50, false));
		viewer.getTable().setLayout(layout);
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setSortDirection(SWT.NONE);
		viewer.setSorter(null);

		TableColumn column01 = new TableColumn(viewer.getTable(), SWT.LEFT);
		column01.setText("No.");
		column01.setToolTipText("No.");

		TableColumn column = new TableColumn(viewer.getTable(), SWT.LEFT);
		column.setText("PO Name");
		column.setToolTipText("PO Name");

		TableColumn column2 = new TableColumn(viewer.getTable(), SWT.LEFT);
		column2.setText("Type");
		column2.setToolTipText("Show Type");
		
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());

		makeActions();
		hookDoubleClickAction();

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {

				Object first = ((IStructuredSelection) event.getSelection())
						.getFirstElement();
				if (first instanceof ProofObligation) {
					try {
						IViewPart v = getSite().getPage().showView(
								IPoviewerConstants.PoTableViewId);

						if (v instanceof PoTableView)
							((PoTableView) v).setDataList(project,
									(ProofObligation) first);
					} catch (PartInitException e) {

						e.printStackTrace();
					}
				}

			}
		});
	}

	protected void makeActions() {
		doubleClickAction = new Action() {
			@Override
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				if (obj instanceof ProofObligation) {
					gotoDefinition((ProofObligation) obj);
					// showMessage(((ProofObligation) obj).toString());
				}
			}

			private void gotoDefinition(ProofObligation po) {
				IFile file = project.findIFile(po.getLocation().getFile());
				if (IVdmProject.externalFileContentType.isAssociatedWith(file
						.getName())) {
					EditorUtility.gotoLocation(
							IPoviewerConstants.ExternalEditorId, file,
							po.getLocation(), po.name);
				} else {
					EditorUtility.gotoLocation(file, po.getLocation(), po.name);
				}

			}
		};

	}

	protected void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	// private void showMessage(String message)
	// {
	// MessageDialog.openInformation(
	// viewer.getControl().getShell(),
	// "PO Test",
	// message);
	// }

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {

		if (selection instanceof IStructuredSelection
				&& part instanceof PoOverviewTableView) {
			Object first = ((IStructuredSelection) selection).getFirstElement();
			if (first instanceof ProofObligation) {
				try {
					IViewPart v = part
							.getSite()
							.getPage()
							.showView(
									"org.overture.ide.plugins.poviewer.views.PoTableView");

					if (v instanceof PoTableView)
						((PoTableView) v).setDataList(project,
								(ProofObligation) first);
				} catch (PartInitException e) {

					e.printStackTrace();
				}
			}
		}

	}

	public void refreshList() {
		display.asyncExec(new Runnable() {

			public void run() {
				viewer.refresh();
			}

		});
	}

	public void setDataList(final IVdmProject project,
			final List<IProofObligation> data) {
		this.project = project;
		display.asyncExec(new Runnable() {

			public void run() {
				if (viewer.getLabelProvider() instanceof ViewLabelProvider)
					((ViewLabelProvider) viewer.getLabelProvider())
							.resetCounter(); // this
												// is
												// needed
												// to
												// reset
												// the
				// numbering

				viewer.setInput(data);

				for (TableColumn col : viewer.getTable().getColumns()) {
					col.pack();
				}
			}

		});
	}
}
