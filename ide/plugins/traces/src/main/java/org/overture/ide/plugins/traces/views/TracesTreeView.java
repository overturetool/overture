/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.ide.plugins.traces.views;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CancellationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPageLayout;
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
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.plugins.traces.ITracesConstants;
import org.overture.ide.plugins.traces.OvertureTracesPlugin;
import org.overture.ide.plugins.traces.debug.TraceDebugLauncher;
import org.overture.ide.plugins.traces.internal.ITracesDisplay;
import org.overture.ide.plugins.traces.internal.VdmjTracesHelper;
import org.overture.ide.plugins.traces.views.treeView.ITreeNode;
import org.overture.ide.plugins.traces.views.treeView.NotYetReadyTreeNode;
import org.overture.ide.plugins.traces.views.treeView.ProjectTreeNode;
import org.overture.ide.plugins.traces.views.treeView.TraceTestGroup;
import org.overture.ide.plugins.traces.views.treeView.TraceTestTreeNode;
import org.overture.ide.plugins.traces.views.treeView.TraceTreeNode;
import org.overture.ide.ui.utility.EditorUtility;
import org.overturetool.ct.utils.Verdict;

public class TracesTreeView extends ViewPart implements ITracesDisplay
{
	private TreeViewer viewer;
	// private DrillDownAdapter drillDownAdapter;
	private Action actionRunSelected;
	private Action actionRunSelectedAdvanced;
	private Action actionRunAll;
	private Action actionSetOkFilter;
	private Action actionSetSort;
	private Action actionSetInconclusiveFilter;
	private Action actionSendToInterpreter;

	//private Action expandSpecInTree;

	private Action refreshAction;
	// private Map<String, ITracesHelper> traceHelpers;
	final Display display = Display.getCurrent();

	// private IResourceChangeListener resourceChangedListener = null;
	private IProject projectToUpdate = null;
	Button buttonSetSort = null;

	// public ITracesHelper getTracesHelper(String projectName)
	// {
	// return this.traceHelpers.get(projectName);
	//
	// }

	// private void setTraceHelper(final IProject project)
	// {
	// try
	// {
	//
	// if (isValidProject(project))
	// {
	// if (traceHelpers.get(project.getName()) != null)
	// traceHelpers.remove(project.getName());
	//
	// IVdmProject vdmProject = (IVdmProject) project.getAdapter(IVdmProject.class);
	// Assert.isNotNull(vdmProject, "Project could not be adapted");
	// ITracesHelper tmpHelper = new VdmjTracesHelper(getSite().getShell(), vdmProject, 3);
	//
	// if (tmpHelper.getClassNamesWithTraces().size() > 0)
	//
	// traceHelpers.put(project.getName(), tmpHelper);
	// }
	// } catch (Exception e1)
	// {
	// System.out.println("CT Init Exception: " + e1.getMessage());
	// e1.printStackTrace();
	// }
	// }

//	public static boolean isValidProject(IProject project)
//	{
//		return project.isOpen() && project.isAccessible()
//				&& project.getAdapter(IVdmProject.class) != null;
//	}

	// private void setTraceHelpers()
	// {
	// IWorkspaceRoot iworkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
	// IProject[] iprojects = iworkspaceRoot.getProjects();
	// this.traceHelpers = new Hashtable<String, ITracesHelper>();
	// new Hashtable<String, IFile>();
	//
	// for (int j = 0; j < iprojects.length; j++)
	// {
	// try
	// {
	// if (isValidProject(iprojects[j]))
	// setTraceHelper(iprojects[j]);
	// } catch (Exception e1)
	// {
	// System.out.println("Exception: " + e1.getMessage());
	// e1.printStackTrace();
	// }
	// }
	// }

	/**
	 * The constructor.
	 */
	public TracesTreeView()
	{
	}

	private void init()
	{
		// setTraceHelpers();
		viewer.setContentProvider(new ViewContentProvider(this));
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(null);
		viewer.setInput(getViewSite());

		//expandTraces(1000);
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

//		new IResourceChangeListener()
//		{
//			public void resourceChanged(IResourceChangeEvent event)
//			{
//				try
//				{
//					switch (event.getType())
//					{
//						case IResourceChangeEvent.POST_CHANGE:
//
//							IResourceDelta[] delta = event.getDelta().getAffectedChildren();
//
//							for (IResourceDelta resourceDelta : delta)
//							{
//
//								if (resourceDelta.getResource() instanceof IProject
//										&& isValidProject(((IProject) resourceDelta.getResource())))
//								{
//
//									if (isFileChange(resourceDelta)
//											|| (resourceDelta.getKind() & IResourceDelta.ADDED) == IResourceDelta.ADDED)
//									{
//										projectToUpdate = ((IProject) resourceDelta.getResource());
//										expandTraces(0);
//									}
//								}
//							}
//							break;
//					}
//				} catch (Exception e)
//				{
//					OvertureTracesPlugin.log(e);
//				}
//			}
//
//		};
	}

//	private void expandTraces(int delay)
//	{
//		final Job expandJob = new Job("Expand traces")
//		{
//
//			@Override
//			protected IStatus run(IProgressMonitor monitor)
//			{
//				if (projectToUpdate != null)
//				{
//					monitor.worked(IProgressMonitor.UNKNOWN);
//					// setTraceHelper(projectToUpdate);
//
//					display.asyncExec(new Runnable()
//					{
//
//						public void run()
//						{
//							updateProject(projectToUpdate);
//						}
//
//					});
//
//				} else
//					expandSpecInTree.run();
//				refreshTree();
//				monitor.done();
//
//				return new Status(IStatus.OK, ITracesConstants.PLUGIN_ID, IStatus.OK, "Expand completed", null);
//			}
//
//		};
//		expandJob.setPriority(Job.INTERACTIVE);
//		expandJob.schedule(delay);
//	}

//	private boolean isFileChange(IResourceDelta delta)
//	{
//		boolean ret = false;
//		if (delta.getAffectedChildren().length == 0)
//		{
//			boolean add = (delta.getKind() & IResourceDelta.ADDED) == IResourceDelta.ADDED;
//			if ((delta.getFlags() & IResourceDelta.CONTENT) == IResourceDelta.CONTENT
//					|| add)
//			{
//				ret = true;
//			} else
//			{
//				ret = false;
//			}
//		} else
//		{
//			for (IResourceDelta d : delta.getAffectedChildren())
//			{
//				ret = ret || isFileChange(d);
//			}
//		}
//		return ret;
//	}

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
		manager.add(actionRunAll);
		manager.add(new Separator());
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
				|| obj instanceof AModuleModules) // ||
		{
			manager.add(actionRunSelected);
			manager.add(actionRunSelectedAdvanced);
		}
		if (obj instanceof TraceTestTreeNode)
			if (((TraceTestTreeNode) obj).getStatus() != null)
			{
				manager.add(actionSendToInterpreter);
			}

		manager.add(new Separator());
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(refreshAction);
		manager.add(actionSetSort);
		manager.add(new Separator());
		manager.add(actionRunAll);
		manager.add(new Separator());
		manager.add(actionSetOkFilter);
		manager.add(actionSetInconclusiveFilter);
	}

	private void makeActions()
	{
		refreshAction = new Action("Refresh")
		{
			@Override
			public void run()
			{
				init();
			}
		};

		actionRunSelected = new Action("Full Evaluation")
		{

			@Override
			public void run()
			{
				ISelection selection = viewer.getSelection();
				final Object obj = ((IStructuredSelection) selection).getFirstElement();

				String projectName = "";
				Dictionary<INode, List<String>> classTracesTestCase = new Hashtable<INode, List<String>>();
				// if (obj instanceof ClassTreeNode)
				// {
				// ClassTreeNode cn = (ClassTreeNode) obj;
				//
				// projectName = cn.getParent().getName();
				//
				// List<String> tmpTraces = new Vector<String>();
				//
				// classTracesTestCase.put(cn.getName(), tmpTraces);
				//
				// } else
				if (obj instanceof ProjectTreeNode)
				{
					ProjectTreeNode pn = (ProjectTreeNode) obj;
					projectName = pn.getName();
				} else if (obj instanceof SClassDefinition
						|| obj instanceof AModuleModules)
				{
					projectName = TraceAstUtility.getProject((INode) obj).getName();
					classTracesTestCase.put(((INode) obj), new Vector<String>());
				}

				final Dictionary<INode, List<String>> finalClassTracesTestCase = classTracesTestCase;
				final String finalProjectName = projectName;

				Job executeTestJob = new Job("CT evaluating selected tests")
				{
					@Override
					protected IStatus run(IProgressMonitor monitor)
					{
						try
						{
							// ITracesHelper th = traceHelpers.get(finalProjectName);

							projectToUpdate = getProject(finalProjectName);

							if (finalClassTracesTestCase.size() == 0)
								evaluateTraces(projectToUpdate, null, null, null, monitor);
							else
							{
								Enumeration<INode> classKeys = finalClassTracesTestCase.keys();
								while (classKeys.hasMoreElements())
								{
									INode className = classKeys.nextElement();
									try
									{
										evaluateTraces(projectToUpdate, className, null, null, monitor);
									} catch (CancellationException e)
									{
										ConsolePrint(e.getMessage());
									} catch (Exception e)
									{
										ConsoleError(e.getMessage());
										OvertureTracesPlugin.log(e);
									}

								}
								monitor.done();
							}

						} catch (Exception e)
						{
							OvertureTracesPlugin.log(e);
						}

//						expandTraces(0);
						return new Status(IStatus.OK, ITracesConstants.PLUGIN_ID, IStatus.OK, "CT Test evaluation finished", null);
					}
				};
				executeTestJob.schedule();
			}
		};
		actionRunSelected.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_RUN_SELECTED_TRACE));
		actionRunSelectedAdvanced = new Action("Filtered Evaluation")
		{
			@Override
			public void run()
			{
				Shell dialog = new Shell(display, SWT.DIALOG_TRIM);
				dialog.setText("Select filtering options");
				dialog.setSize(200, 200);

				final TraceOptionsDialog d = new TraceOptionsDialog(dialog, SWT.DIALOG_TRIM);
				d.pack();
				dialog.pack();
				Point pt = display.getCursorLocation();
				dialog.setLocation(pt.x - (d.getSize().x / 2), pt.y
						- (d.getSize().y / 2));
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

				if (d.isCanceled)
				{
					return;
				}
				ISelection selection = viewer.getSelection();
				final Object obj = ((IStructuredSelection) selection).getFirstElement();

				String projectName = "";
				Dictionary<INode, List<String>> classTracesTestCase = new Hashtable<INode, List<String>>();
				// if (obj instanceof ClassTreeNode)
				// {
				// ClassTreeNode cn = (ClassTreeNode) obj;
				//
				// projectName = cn.getParent().getName();
				//
				// List<String> tmpTraces = new Vector<String>();
				//
				// classTracesTestCase.put(cn.getName(), tmpTraces);
				//
				// } else
				if (obj instanceof ProjectTreeNode)
				{
					ProjectTreeNode pn = (ProjectTreeNode) obj;
					projectName = pn.getName();
				} else if (obj instanceof SClassDefinition
						|| obj instanceof AModuleModules)
				{
					projectName = TraceAstUtility.getProject((INode) obj).getName();
					classTracesTestCase.put(((INode) obj), new Vector<String>());
				}

				final Dictionary<INode, List<String>> finalClassTracesTestCase = classTracesTestCase;
				final String finalProjectName = projectName;

				Job executeTestJob = new Job("CT evaluating selected tests")
				{
					@Override
					protected IStatus run(IProgressMonitor monitor)
					{
						try
						{
							// ITracesHelper th = traceHelpers.get(finalProjectName);

							projectToUpdate = getProject(finalProjectName);

							if (finalClassTracesTestCase.size() == 0)
							{
								evaluateTraces(projectToUpdate, null, null, d, monitor);
								// runTestProject(th, monitor);
							} else
							{
								Enumeration<INode> classKeys = finalClassTracesTestCase.keys();
								while (classKeys.hasMoreElements())
								{
									INode className = classKeys.nextElement();
									try
									{
										// projectToUpdate.refreshLocal(IResource.DEPTH_INFINITE, null);
										// th.processClassTraces(className, d.getSubset(), d.getTraceReductionType(),
										// d.getSeed(), monitor);
										evaluateTraces(projectToUpdate, className, null, d, monitor);
									} catch (CancellationException e)
									{
										ConsolePrint(e.getMessage());
									} catch (Exception e)
									{
										ConsoleError(e.getMessage());
										OvertureTracesPlugin.log(e);
									}
								}
							}
						} catch (Exception e)
						{
							OvertureTracesPlugin.log(e);
						}
						//expandTraces(0);
						return new Status(IStatus.OK, ITracesConstants.PLUGIN_ID, IStatus.OK, "CT Test evaluation finished", null);
					}

				};
				executeTestJob.schedule();
			}
		};
		actionRunSelectedAdvanced.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_RUN_SELECTED_TRACE));

		actionRunAll = new Action("Run all")
		{
			@Override
			public void run()
			{
				Job runAllTestsJob = new Job("CT evaluation all projects")
				{
					@Override
					protected IStatus run(IProgressMonitor monitor)
					{
//						IWorkspaceRoot iworkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
//						IProject[] iprojects = iworkspaceRoot.getProjects();
//						int totalCount = 0;
//						for (final IProject project : iprojects)
//						{
//							try
//							{
//								if (isValidProject(project))
//								{
//									// ITracesHelper th = traceHelpers.get(project.getName());
//									// if (th != null)
//									// for (String className : th.getClassNamesWithTraces())
//									// {
//									// totalCount += th.getTraceDefinitions(className).size();
//									// }
//								}
//							} catch (Exception e)
//							{
//								OvertureTracesPlugin.log(e);
//							}
//						}
//
//						for (final IProject project : iprojects)
//						{
//							if (monitor.isCanceled())
//							{
//								break;
//							}
//							try
//							{
//								// if (isValidProject(project)
//								// && traceHelpers.containsKey(project.getName()))
//								// {
//								// ITracesHelper th = traceHelpers.get(project.getName());
//								// for (String className : th.getClassNamesWithTraces())
//								// {
//								// if (monitor.isCanceled())
//								// {
//								// break;
//								// }
//								// th.processClassTraces(className, monitor);
//								// }
//								// }
//							} catch (CancellationException e)
//							{
//								ConsolePrint(e.getMessage());
//							} catch (Exception e)
//							{
//								OvertureTracesPlugin.log(e);
//							}
//						}
//						display.asyncExec(new Runnable()
//						{
//							public void run()
//							{
//								updateTraceTestCasesNodeStatus();
//							}
//						});
//						refreshTree();
						return new Status(IStatus.OK, ITracesConstants.PLUGIN_ID, IStatus.OK, "CT Test evaluation finished", null);
					}

				};

				runAllTestsJob.schedule();
			}
		};

		actionRunAll.setToolTipText("Run all");
		actionRunAll.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_RUN_ALL_TRACES));

//		expandSpecInTree = new Action()
//		{
//			@Override
//			public void run()
//			{
//				IWorkspaceRoot iworkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
//				final IProject[] iprojects = iworkspaceRoot.getProjects();
//
//				display.asyncExec(new Runnable()
//				{
//
//					public void run()
//					{
//						for (IProject project : iprojects)
//						{
//							updateProject(project);
//						}
//						viewer.refresh();
//					}
//
//				});
//
//			}
//
//		};

		actionSetOkFilter = new Action("Filter ok results")
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
					actionSetOkFilter.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_CASE_FILTER_SUCCES));
				} else
				{
					viewer.addFilter(okFilter);
					actionSetOkFilter.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_CASE_FILTER_SUCCES_PRESSED));
				}

			}

		};

		actionSetOkFilter.setToolTipText("Filter all ok results from tree");
		actionSetOkFilter.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_CASE_FILTER_SUCCES));

		actionSetInconclusiveFilter = new Action("Filter inconclusive results")
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
					actionSetInconclusiveFilter.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_CASE_FILTER_UNDETERMINED));
				} else
				{
					viewer.addFilter(inconclusiveFilter);
					actionSetInconclusiveFilter.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_CASE_FILTER_UNDETERMINED_PRESSED));
				}

			}

		};

		actionSetInconclusiveFilter.setToolTipText("Filter all inconclusive results from tree");
		actionSetInconclusiveFilter.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_CASE_FILTER_UNDETERMINED));

		actionSetSort = new Action("Sort")
		{
			@Override
			public void run()
			{
				if (viewer.getSorter() != null)
				{
					viewer.setSorter(null);
					actionSetSort.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_SORT));
				} else
				{
					viewer.setSorter(traceSorter);
					actionSetSort.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_SORT_PRESSED));
				}

			}

		};

		actionSetSort.setToolTipText("Sort by verdict: Fail, Inconclusive, ok, etc.");
		actionSetSort.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_SORT));

		actionSendToInterpreter = new Action("Send to Interpreter")
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

		actionSendToInterpreter.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_INTERPRETER));
	}

	// -----------------------------update
//	private void updateTraceTestCasesNodeStatus()
//	{
//		TreeItem[] aa = viewer.getTree().getItems();
//		for (TreeItem treeItem : aa)
//		{
//			if (treeItem.getData() instanceof ProjectTreeNode)
//			{
//				ProjectTreeNode projectNode = ((ProjectTreeNode) treeItem.getData());
//				// ITracesHelper th = traceHelpers.get(projectNode.getName());
//				for (ITreeNode classNode : projectNode.getChildren())
//				{
//					for (ITreeNode traceNode : classNode.getChildren())
//					{
//						// updateTraceTestCasesNodeStatus(th, (TraceTreeNode) traceNode);
//
//					}
//				}
//			}
//		}
//		viewer.refresh();
//	}

//	private void updateTraceTestCasesNodeStatus(TraceTreeNode traceNode)
//	{
//		// try
//		// {
//		// traceNode.setSkippedCount(th.getSkippedCount(traceNode.getParent().getName(), traceNode.getName()));
//		// } catch (SAXException e)
//		// {
//		// OvertureTracesPlugin.log(e);
//		// } catch (IOException e)
//		// {
//		// OvertureTracesPlugin.log(e);
//		// } catch (ClassNotFoundException e)
//		// {
//		// ConsolePrint(e.toString());
//		// OvertureTracesPlugin.log(e);
//		// }
//
//	}

//	private void updateProject(IProject project)
//	{
//		if (viewer == null || viewer.getControl().isDisposed())// TODO
//		{
//			return; // skip if disposed
//		}
//		TreeItem[] aa = viewer.getTree().getItems();
//		// boolean insertProject = true;
//		for (TreeItem treeItem : aa)
//		{
//			if (treeItem.getData() instanceof ProjectTreeNode
//					&& ((ProjectTreeNode) treeItem.getData()).getName().equals(project.getName()))
//			{
//				System.out.println("something changed:" + treeItem.getData());
//				// insertProject = false;
//				// ProjectTreeNode projectNode = ((ProjectTreeNode) treeItem.getData());
//				// String projectName = projectNode.getName();
//				// // ITracesHelper th = traceHelpers.get(projectName);
//				//
//				// projectNode.getChildren().clear();
//
//				// now no nodes are present
//				try
//				{
//					// for (String className : th.getClassNamesWithTraces())
//					// {
//					// ClassTreeNode classNode = new ClassTreeNode(className);
//					// for (ANamedTraceDefinition traceName : th.getTraceDefinitions(className))
//					// {
//					//
//					// //TraceTreeNode traceNode = new TraceTreeNode(traceName, th, className);
//					// //FIXME
//					//
//					// //classNode.addChild(traceNode);
//					//
//					// }
//					// projectNode.addChild(classNode);
//					//
//					// }
//					// viewer.refresh(projectNode);
//				} catch (Exception e)
//				{
//					OvertureTracesPlugin.log(e);
//				}
//				// viewer.refresh(projectNode);
//				// viewer.expandToLevel(projectNode, 2);
//			}
//		}
//		// if (insertProject && traceHelpers.get(project.getName()) != null)
//		// {
//		// // ((ViewContentProvider) viewer.getContentProvider()).addChild(new ProjectTreeNode(project));//TODO
//		// viewer.refresh();
//		// updateProject(project);
//		// }
//	}

	// --------------
	// ---------------- Get results

	// private void runTestProject( IProgressMonitor monitor)
	// throws IOException
	// {
	// try
	// {
	// for (String className : th.getClassNamesWithTraces())
	// {
	// if (monitor != null && monitor.isCanceled())
	// {
	// return;
	// }
	//
	// try
	// {
	// th.processClassTraces(className, monitor);
	// } catch (Exception e)
	// {
	// OvertureTracesPlugin.log(e);
	// ConsolePrint(e.getMessage());
	// }
	//
	// }
	// } catch (TraceHelperNotInitializedException e)
	// {
	// ConsolePrint("Trace helper not initialized for project: "
	// + e.getProjectName());
	// OvertureTracesPlugin.log(e);
	// }

	// }

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
						try
						{
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ITracesConstants.TRACES_TEST_ID);
							gotoTraceDefinition(findTraceTreeNode((TraceTestTreeNode) selection));
						} catch (PartInitException e)
						{
							OvertureTracesPlugin.log(e);
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

	private ViewerFilter okFilter = new ViewerFilter()
	{

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element)
		{
			if (element instanceof TraceTestTreeNode
					&& ((TraceTestTreeNode) element).getStatus() == Verdict.PASSED)
			{
				return false;
			} else
			{
				return true;
			}
		}

	};

	private ViewerFilter inconclusiveFilter = new ViewerFilter()
	{

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element)
		{
			if (element instanceof TraceTestTreeNode
					&& ((TraceTestTreeNode) element).getStatus() == Verdict.INCONCLUSIVE)
			{
				return false;
			} else
			{
				return true;
			}
		}

	};

	private ViewerSorter traceSorter = new ViewerSorter()
	{
		@Override
		public int category(Object element)
		{
			if (element instanceof TraceTestTreeNode)
			{
				Verdict res = ((TraceTestTreeNode) element).getStatus();
				if (res == Verdict.FAILED)
				{
					return 1;
				} else if (res == Verdict.INCONCLUSIVE)
				{
					return 2;
				} else if (res == Verdict.PASSED)
				{
					return 3;
				}
			}
			return 3;
			// return super.category(element);
		}
	};

	// public static Boolean IsWindows()
	// {
	// String osName = System.getProperty("os.name");
	//
	// return osName.toUpperCase().indexOf("WINDOWS".toUpperCase()) > -1;
	// }

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
		// IWorkspaceRoot iworkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		// String projectName = tn.getParent().getParent().getName();
		// IProject iproject = iworkspaceRoot.getProject(projectName);

		IVdmProject vdmProject = TraceAstUtility.getProject(tn.getTraceDefinition());// (IVdmProject)
																						// iproject.getAdapter(IVdmProject.class);

		IFile file = vdmProject.findIFile(tn.getTraceDefinition().getLocation().file);

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

	private IProject getProject(String finalProjectName) throws CoreException
	{
		IWorkspaceRoot iworkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] iprojects = iworkspaceRoot.getProjects();

		for (final IProject project : iprojects)
		{

			if (project.isOpen() && project.getName().equals(finalProjectName))
			{
				return project;
			}
		}
		return null;
	}

	private void evaluateTraces(final IProject project, INode container,
			ANamedTraceDefinition traceDef, TraceOptionsDialog filterDialog,
			IProgressMonitor monitor) throws Exception
	{
		if (project == null)
		{
			return;
		}

		// d.getSubset(), d.getTraceReductionType(), d.getSeed()
		VdmjTracesHelper traceRunner = new VdmjTracesHelper(getSite().getShell(), (IVdmProject) project.getAdapter(IVdmProject.class));

		if (filterDialog == null)
		{
			traceRunner.evaluateTraces(container, traceDef, monitor,this);
		} else
		{
			traceRunner.evaluateTraces(container, traceDef, filterDialog.getSubset(), filterDialog.getTraceReductionType(), filterDialog.getSeed(), monitor,this);
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
						if(viewer.getContentProvider() instanceof ViewContentProvider)
						{
							((ViewContentProvider)viewer.getContentProvider()).resetCache(project);
						}
						viewer.refresh();
						viewer.getControl().update();
					}
				}
			}

		});
		try
		{
			((IProject)project.getAdapter(IProject.class)).refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e)
		{
		}
	}
}