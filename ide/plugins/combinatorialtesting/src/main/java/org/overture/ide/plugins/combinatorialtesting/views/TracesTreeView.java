/*
 * #%~
 * Combinatorial Testing
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
package org.overture.ide.plugins.combinatorialtesting.views;

import java.io.File;
import java.util.concurrent.CancellationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.plugins.combinatorialtesting.ITracesConstants;
import org.overture.ide.plugins.combinatorialtesting.OvertureTracesPlugin;
import org.overture.ide.plugins.combinatorialtesting.debug.TraceDebugLauncher;
import org.overture.ide.plugins.combinatorialtesting.internal.ITracesDisplay;
import org.overture.ide.plugins.combinatorialtesting.internal.VdmjTracesHelper;
import org.overture.ide.plugins.combinatorialtesting.views.internal.InconclusiveTraceViewerFilter;
import org.overture.ide.plugins.combinatorialtesting.views.internal.OkTraceViewerFilter;
import org.overture.ide.plugins.combinatorialtesting.views.internal.TraceNodeSorter;
import org.overture.ide.plugins.combinatorialtesting.views.internal.TraceViewerSorter;
import org.overture.ide.plugins.combinatorialtesting.views.treeView.ITreeNode;
import org.overture.ide.plugins.combinatorialtesting.views.treeView.NotYetReadyTreeNode;
import org.overture.ide.plugins.combinatorialtesting.views.treeView.ProjectTreeNode;
import org.overture.ide.plugins.combinatorialtesting.views.treeView.TraceTestGroup;
import org.overture.ide.plugins.combinatorialtesting.views.treeView.TraceTestTreeNode;
import org.overture.ide.plugins.combinatorialtesting.views.treeView.TraceTreeNode;
import org.overture.ide.ui.utility.EditorUtility;

public class TracesTreeView extends ViewPart implements ITracesDisplay
{
	private TreeViewer viewer;
	private Action actionRunSelected;
	private Action actionRunSelectedAdvanced;
	private Action actionSetOkFilter;
	private Action actionSetSort;
	private Action actionSetInconclusiveFilter;
	private Action actionSendToInterpreter;

	private Action refreshAction;
	final Display display = Display.getCurrent();

	Button buttonSetSort = null;

	private ViewerFilter okFilter = new OkTraceViewerFilter();
	private ViewerSorter traceSorter = new TraceViewerSorter();
	private ViewerSorter defaultTraceSorter = new TraceNodeSorter();
	private ViewerFilter inconclusiveFilter = new InconclusiveTraceViewerFilter();

	/**
	 * The constructor.
	 */
	public TracesTreeView()
	{
	}

	private void init()
	{
		viewer.setContentProvider(new ViewContentProvider(this));
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(defaultTraceSorter);
		viewer.setInput(getViewSite());
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		getSite().setSelectionProvider(viewer);

		makeActions();
		hookContextMenu();
		hookTreeAction();
		contributeToActionBars();

		init();

	}

	private void hookContextMenu()
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener()
		{
			public void menuAboutToShow(IMenuManager manager)
			{
				TracesTreeView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager)
	{
		manager.add(actionSetSort);
		manager.add(new Separator());
		manager.add(actionSetOkFilter);
		manager.add(actionSetInconclusiveFilter);
	}

	private void fillContextMenu(IMenuManager manager)
	{
		ISelection selection = viewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();

		if (obj instanceof ProjectTreeNode /* || obj instanceof ClassTreeNode */
				|| obj instanceof SClassDefinition
				|| obj instanceof AModuleModules
				|| obj instanceof TraceTreeNode) // ||
		{
			manager.add(actionRunSelected);
			manager.add(actionRunSelectedAdvanced);
		}
		if (obj instanceof TraceTestTreeNode)
		{
			if (((TraceTestTreeNode) obj).getStatus() != null)
			{
				manager.add(actionSendToInterpreter);
			}
		}

		manager.add(new Separator());
		manager.add(refreshAction);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(refreshAction);
		manager.add(actionSetSort);
		manager.add(new Separator());
		manager.add(actionSetOkFilter);
		manager.add(actionSetInconclusiveFilter);
	}

	private final Image getImage(String path)
	{
		ImageDescriptor theDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin("org.overture.ide.plugins.combinatorialtesting", path);
		Image theImage = null;
		if (theDescriptor != null)
		{
			theImage = theDescriptor.createImage();
		}
		return theImage;
	}

	private void makeActions()
	{
		refreshAction = new Action("Refresh",OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_REFRESH))
		{
			@Override
			public void run()
			{

				Job job = new Job("Refresh Projects")
				{

					@Override
					protected IStatus run(IProgressMonitor monitor)
					{

						refreshAction.setEnabled(false);

						for (IVdmProject proj : TraceAstUtility.getProjects())
						{

							IVdmModel model = proj.getModel();
							model.refresh(false, null);
						}

						refreshAction.setEnabled(true);

						display.asyncExec(new Runnable()
						{

							public void run()
							{

								init();
							}
						});

						return Status.OK_STATUS;
					}
				};
				job.schedule();
			}
		};
		

		actionRunSelected = new Action("Full Evaluation")
		{

			@Override
			public void run()
			{
				ISelection selection = viewer.getSelection();
				final Object obj = ((IStructuredSelection) selection).getFirstElement();

				handleEvaluateTrace(obj, null);
			}
		};
		actionRunSelected.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_RUN_SELECTED_TRACE));
		
		actionRunSelectedAdvanced = new Action("Filtered Evaluation",OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_RUN_SELECTED_TRACE))
		{
			@Override
			public void run()
			{
				Shell dialog = new Shell(display, SWT.DIALOG_TRIM);
				dialog.setText("Select filtering options");
				dialog.setSize(200, 200);

				Image ctIcon = getImage(new StringBuilder("icons").append(File.separator).append("ctool16").append(File.separator).append("ct_tsk.png").toString());
				dialog.setImage(ctIcon);

				final TraceOptionsDialog d = new TraceOptionsDialog(dialog, SWT.DIALOG_TRIM);
				d.pack();
				dialog.pack();
				Point pt = display.getCursorLocation();
				dialog.setLocation(pt.x - d.getSize().x / 2, pt.y
						- d.getSize().y / 2);
				dialog.open();
				while (!dialog.isDisposed())
				{
					if (d.isCanceled)
					{
						return;
					}
					if (!display.readAndDispatch())
					{
						display.sleep();
					}
				}

				if (d.isCanceled || d.getTraceReductionType() == null)
				{
					return;
				}
				ISelection selection = viewer.getSelection();
				final Object obj = ((IStructuredSelection) selection).getFirstElement();

				handleEvaluateTrace(obj, d);
			}
		};


		actionSetOkFilter = new Action("Filter ok results",Action.AS_CHECK_BOX)
		{
			@Override
			public void run()
			{
				ViewerFilter[] filters = viewer.getFilters();
				boolean isSet = false;
				for (ViewerFilter viewerFilter : filters)
				{
					if (viewerFilter.equals(okFilter))
					{
						isSet = true;
					}
				}
				if (isSet)
				{
					viewer.removeFilter(okFilter);
				} else
				{
					viewer.addFilter(okFilter);
				}

			}

		};

		actionSetOkFilter.setToolTipText("Filter all ok results from tree");
		actionSetOkFilter.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_CASE_FILTER_SUCCES));

		actionSetInconclusiveFilter = new Action("Filter inconclusive results",Action.AS_CHECK_BOX)
		{
			@Override
			public void run()
			{
				ViewerFilter[] filters = viewer.getFilters();
				boolean isSet = false;
				for (ViewerFilter viewerFilter : filters)
				{
					if (viewerFilter.equals(inconclusiveFilter))
					{
						isSet = true;
					}
				}
				if (isSet)
				{
					viewer.removeFilter(inconclusiveFilter);
				} else
				{
					viewer.addFilter(inconclusiveFilter);
				}

			}

		};

		actionSetInconclusiveFilter.setToolTipText("Filter all inconclusive results from tree");
		actionSetInconclusiveFilter.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_CASE_FILTER_UNDETERMINED));

		actionSetSort = new Action("Sort",Action.AS_CHECK_BOX)
		{
			@Override
			public void run()
			{
				if (!actionSetSort.isChecked())
				{
					viewer.setSorter(defaultTraceSorter);
					OvertureTracesPlugin.getDefault().getPreferenceStore().setValue(ITracesConstants.SORT_VIEW, false);
				} else
				{
					viewer.setSorter(traceSorter);
					OvertureTracesPlugin.getDefault().getPreferenceStore().setValue(ITracesConstants.SORT_VIEW, true);
				}
			}
		};

		actionSetSort.setToolTipText("Sort by verdict: Fail, Inconclusive, ok, etc.");
		actionSetSort.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_SORT));
		actionSetSort.setChecked(OvertureTracesPlugin.getDefault().getPreferenceStore().getBoolean(ITracesConstants.SORT_VIEW));
actionSetSort.run();
		
		actionSendToInterpreter = new Action("Send to Interpreter",OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_INTERPRETER))
		{
			@Override
			public void run()
			{
				ISelection selection = viewer.getSelection();
				final Object obj = ((IStructuredSelection) selection).getFirstElement();

				if (obj instanceof TraceTestTreeNode)
				{
					TraceTestTreeNode traceTestNode = (TraceTestTreeNode) obj;

					TraceTreeNode traceNode = null;
					ITreeNode n = traceTestNode;
					while (n != null && !(n instanceof ProjectTreeNode))
					{
						if (n instanceof TraceTreeNode)
						{
							traceNode = (TraceTreeNode) n;
						}
						n = n.getParent();

					}
					new TraceDebugLauncher().Launch(TraceAstUtility.getProject(traceNode.getTraceDefinition()), traceNode.getInfo(), traceTestNode.getNumber());
				}
			}
		};

	}

	private void hookTreeAction()
	{
		viewer.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{

				Object selection = ((ITreeSelection) event.getSelection()).getFirstElement();
				if (selection instanceof TraceTreeNode)
				{
					TraceTreeNode tn = (TraceTreeNode) selection;

					gotoTraceDefinition(tn);

				} else if (selection instanceof TraceTestTreeNode)
				{
					if (!(selection instanceof NotYetReadyTreeNode)
							&& !(selection instanceof TraceTestGroup)
							&& ((TraceTestTreeNode) selection).getStatus() == null)
					{
						try
						{
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IPageLayout.ID_PROGRESS_VIEW);
						} catch (PartInitException e)
						{
							OvertureTracesPlugin.log(e);
						}
					} else
					{
						try
						{
							IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ITracesConstants.TRACES_TEST_ID);
							if (view instanceof TraceTest)
							{
								TraceTest traceTestView = (TraceTest) view;
								traceTestView.selectionChanged(TracesTreeView.this, event.getSelection());
							}
							gotoTraceDefinition(findTraceTreeNode((TraceTestTreeNode) selection));
						} catch (PartInitException e)
						{
							OvertureTracesPlugin.log(e);
						}
					}

				}
			}

			private TraceTreeNode findTraceTreeNode(ITreeNode selection)
			{
				if (selection != null)
				{
					if (selection.getParent() == null)
					{
						return null;
					} else if (selection.getParent() instanceof TraceTreeNode)
					{
						return (TraceTreeNode) selection.getParent();
					} else
					{
						return findTraceTreeNode(selection.getParent());
					}
				}

				return null;
			}

		});
		viewer.addTreeListener(new ITreeViewerListener()
		{

			public void treeCollapsed(TreeExpansionEvent event)
			{
				Object expandingElement = event.getElement();
				if (expandingElement instanceof TraceTreeNode)
				{
					TraceTreeNode node = (TraceTreeNode) expandingElement;
					node.unloadTests();

					refreshTree();
				} else if (expandingElement instanceof TraceTestGroup)
				{
					TraceTestGroup node = (TraceTestGroup) expandingElement;
					node.unloadTests();
					refreshTree();
				}

			}

			public void treeExpanded(TreeExpansionEvent event)
			{
				Object expandingElement = event.getElement();
				if (expandingElement instanceof TraceTreeNode)
				{
					TraceTreeNode node = (TraceTreeNode) expandingElement;
					try
					{
						node.loadTests();
					} catch (Exception e)
					{
						OvertureTracesPlugin.log(e);
					}
					refreshTree();
				} else if (expandingElement instanceof TraceTestGroup)
				{
					TraceTestGroup node = (TraceTestGroup) expandingElement;
					try
					{
						node.loadTests();
					} catch (Exception e)
					{
						OvertureTracesPlugin.log(e);
					}
					refreshTree();
				}
			}
		});

	}

	private void refreshTree()
	{
		display.asyncExec(new Runnable()
		{

			public void run()
			{
				if (viewer != null && !viewer.getControl().isDisposed())
				{
					viewer.refresh();
					viewer.getControl().update();
					actionSetSort.run();
				}
			}
		});
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus()
	{
		viewer.getControl().setFocus();
	}

	private void ConsolePrint(final String message)
	{
		display.asyncExec(new Runnable()
		{

			public void run()
			{
				try
				{
					MessageConsole myConsole = findConsole("TracesConsole");
					MessageConsoleStream out = myConsole.newMessageStream();
					out.println(message);
				} catch (Exception e)
				{
					OvertureTracesPlugin.log(e);
				}
			}
		});

	}

	private void ConsoleError(final String message)
	{
		display.asyncExec(new Runnable()
		{

			public void run()
			{
				try
				{
					MessageConsole myConsole = findConsole("TracesConsole");
					MessageConsoleStream out = myConsole.newMessageStream();

					out.setColor(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
					out.println(message);

					IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					if (activeWorkbenchWindow != null)
					{
						IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
						if (activePage != null)
						{
							activePage.showView(IConsoleConstants.ID_CONSOLE_VIEW, null, IWorkbenchPage.VIEW_VISIBLE);
						}
					}
				} catch (Exception e)
				{
					OvertureTracesPlugin.log(e);
				}
			}
		});

	}

	private void gotoTraceDefinition(TraceTreeNode tn)
	{
		if (tn == null || tn.getTraceDefinition() == null)
		{
			return;
		}

		IVdmProject vdmProject = TraceAstUtility.getProject(tn.getTraceDefinition());

		IFile file = vdmProject.findIFile(tn.getTraceDefinition().getLocation().getFile());

		EditorUtility.gotoLocation(file, tn.getTraceDefinition().getLocation(), tn.getName());
	}

	private MessageConsole findConsole(String name)
	{
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
		{
			if (name.equals(existing[i].getName()))
			{
				return (MessageConsole) existing[i];
			}
		}
		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}

	private void handleEvaluateTrace(Object selectedItem,
			final TraceOptionsDialog optionsDialog)
	{
		IVdmProject project = null;
		INode container = null;
		ANamedTraceDefinition traceDef = null;

		if (selectedItem instanceof ProjectTreeNode)
		{
			ProjectTreeNode pn = (ProjectTreeNode) selectedItem;
			project = pn.project;
		} else if (selectedItem instanceof SClassDefinition
				|| selectedItem instanceof AModuleModules)
		{
			project = TraceAstUtility.getProject((INode) selectedItem);
			container = (INode) selectedItem;
		} else if (selectedItem instanceof TraceTreeNode)
		{
			traceDef = ((TraceTreeNode) selectedItem).getTraceDefinition();
			container = TraceAstUtility.getTraceDefinitionContainer(traceDef);
			project = TraceAstUtility.getProject(traceDef);
		} else if (selectedItem instanceof IVdmProject)
		{
			project = (IVdmProject) selectedItem;
		}

		final IVdmProject finalProject = project;
		final INode finalContainer = container;
		final ANamedTraceDefinition finalTraceDef = traceDef;

		Job executeTestJob = new Job("CT evaluating selected tests")
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				try
				{
					try
					{
						evaluateTraces(finalProject, finalContainer, finalTraceDef, optionsDialog, monitor);
					} catch (CancellationException e)
					{
						ConsolePrint(e.getMessage());
					} catch (Exception e)
					{
						ConsoleError(e.getMessage());
						OvertureTracesPlugin.log(e);
					}

					monitor.done();

				} catch (Exception e)
				{
					OvertureTracesPlugin.log(e);
				}

				// expandTraces(0);
				return new Status(IStatus.OK, ITracesConstants.PLUGIN_ID, IStatus.OK, "CT Test evaluation finished", null);
			}
		};
		executeTestJob.schedule();
	}

	private void evaluateTraces(final IVdmProject project, INode container,
			ANamedTraceDefinition traceDef, TraceOptionsDialog filterDialog,
			IProgressMonitor monitor) throws Exception
	{
		if (project == null)
		{
			return;
		}

		VdmjTracesHelper traceRunner = new VdmjTracesHelper(getSite().getShell(), project);

		if (filterDialog == null)
		{
			traceRunner.evaluateTraces(container, traceDef, monitor, this);
		} else
		{
			traceRunner.evaluateTraces(container, traceDef, filterDialog.getSubset(), filterDialog.getTraceReductionType(), filterDialog.getSeed(), monitor, this);
		}

	}

	public void updateView(final IVdmProject project)
	{
		display.asyncExec(new Runnable()
		{

			public void run()
			{

				if (viewer == null || viewer.getControl().isDisposed())// TODO
				{
					return; // skip if disposed
				}
				TreeItem[] aa = viewer.getTree().getItems();
				// boolean insertProject = true;
				for (TreeItem treeItem : aa)
				{
					if (treeItem.getData() instanceof ProjectTreeNode
							&& ((ProjectTreeNode) treeItem.getData()).getName().equals(project.getName()))
					{
						if (viewer.getContentProvider() instanceof ViewContentProvider)
						{
							((ViewContentProvider) viewer.getContentProvider()).resetCache(project);
						}
						viewer.refresh();
						viewer.getControl().update();
						actionSetSort.run();
					}
				}
			}

		});
		try
		{
			((IProject) project.getAdapter(IProject.class)).refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e)
		{
		}
	}
}
