package org.overture.ide.plugins.traces.views;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CancellationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
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
import org.eclipse.swt.widgets.FileDialog;
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
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.plugins.traces.OvertureTracesPlugin;
import org.overture.ide.plugins.traces.TracesConstants;
import org.overture.ide.plugins.traces.debug.TraceDebugLauncher;
import org.overture.ide.plugins.traces.internal.VdmjTracesHelper;
import org.overture.ide.plugins.traces.views.treeView.ClassTreeNode;
import org.overture.ide.plugins.traces.views.treeView.ITreeNode;
import org.overture.ide.plugins.traces.views.treeView.NotYetReadyTreeNode;
import org.overture.ide.plugins.traces.views.treeView.ProjectTreeNode;
import org.overture.ide.plugins.traces.views.treeView.TraceTestGroup;
import org.overture.ide.plugins.traces.views.treeView.TraceTestTreeNode;
import org.overture.ide.plugins.traces.views.treeView.TraceTreeNode;
import org.overture.ide.ui.utility.EditorUtility;
import org.overturetool.traces.utility.ITracesHelper;
import org.overturetool.traces.utility.TraceHelperNotInitializedException;
import org.overturetool.vdmj.definitions.NamedTraceDefinition;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.traces.Verdict;
import org.xml.sax.SAXException;



public class TracesTreeView extends ViewPart
{
	private TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action actionRunSelected;
	private Action actionRunSelectedAdvanced;
	private Action actionRunAll;
	private Action actionSetOkFilter;
	private Action actionSetSort;
	private Action actionSetInconclusiveFilter;
	private Action actionSendToInterpreter;

	private Action actionSelectToolBoxVDMJ;
	private Action actionSelectToolBoxVDMTools;
	private Action expandSpecInTree;
	
	private Action refreshAction;
	private Map<String, ITracesHelper> traceHelpers;
	final Display display = Display.getCurrent();

//	private IResourceChangeListener resourceChangedListener = null;
	private IProject projectToUpdate = null;
	private String VDMToolsPath = "";
	final String VDM_TOOLS_PATH_DEFAULT = "C:\\Program Files\\The VDM++ Toolbox v8.2b\\bin";
	Button buttonSetSort = null;

	public ITracesHelper getTracesHelper(String projectName)
	{
		return this.traceHelpers.get(projectName);

	}

	private void setTraceHelper(final IProject project)
	{

		// Job initCtJob = new Job("CT init")
		// {
		//
		// @Override
		// protected IStatus run(IProgressMonitor monitor)
		// {

		try
		{

			if (isValidProject(project))
			{
				if (traceHelpers.get(project.getName()) != null)
					traceHelpers.remove(project.getName());

				IVdmProject vdmProject = (IVdmProject) project.getAdapter(IVdmProject.class);
				Assert.isNotNull(vdmProject, "Project could not be adapted");
				ITracesHelper tmpHelper = new VdmjTracesHelper(getSite().getShell(), vdmProject, 3);

				if (tmpHelper.getClassNamesWithTraces().size() > 0)

					traceHelpers.put(project.getName(), tmpHelper);
			}
		} catch (Exception e1)
		{
			System.out.println("CT Init Exception: " + e1.getMessage());
			e1.printStackTrace();
		}

		// return new Status(IStatus.OK,
		// "org.overture.ide.plugins.traces", IStatus.OK,
		// "CT init finished", null);
		// }
		//
		// };
		//
		// initCtJob.schedule();

	}

	public static boolean isValidProject(IProject project)
	{
		return project.isOpen() && project.isAccessible()
				&& project.getAdapter(IVdmProject.class) != null;
		// && (project.hasNature(VdmPpProjectNature.VDM_PP_NATURE)
		// || project.hasNature(VdmRtProjectNature.VDM_RT_NATURE) ||
		// project.hasNature(VdmSlProjectNature.VDM_SL_NATURE)&& AstManager.instance().getProjects().contains(project));
	}

	private void setTraceHelpers()
	{
		IWorkspaceRoot iworkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] iprojects = iworkspaceRoot.getProjects();
		this.traceHelpers = new Hashtable<String, ITracesHelper>();
		new Hashtable<String, IFile>();

		for (int j = 0; j < iprojects.length; j++)
		{
			try
			{
				if (isValidProject(iprojects[j]))
					setTraceHelper(iprojects[j]);
			} catch (Exception e1)
			{
				System.out.println("Exception: " + e1.getMessage());
				e1.printStackTrace();
			}
		}
	}

	/**
	 * The constructor.
	 */
	public TracesTreeView()
	{
	}

	private void init()
	{

		drillDownAdapter = new DrillDownAdapter(viewer);
		setTraceHelpers();
		viewer.setContentProvider(new ViewContentProvider(this.traceHelpers, this));
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(null);
		viewer.setInput(getViewSite());

		// viewer.setFilters(new ViewerFilter[]{okFilter,inconclusiveFilter});

		// buttonSetSort = new Button(parent,SWT.TOGGLE);
		// buttonSetSort.setImage(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_SORT).createImage());

		getSite().setSelectionProvider(viewer);
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		hookTreeAction();
		contributeToActionBars();

		expandTraces(1000);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		// PatternFilter patternFilter = new PatternFilter();
		// viewer = new FilteredTree(parent, SWT.MULTI
		// | SWT.H_SCROLL | SWT.V_SCROLL, patternFilter).getViewer();

		init();

		/*resourceChangedListener =*/ new IResourceChangeListener()
		{
			public void resourceChanged(IResourceChangeEvent event)
			{
				try
				{
					switch (event.getType())
					{
						case IResourceChangeEvent.POST_CHANGE:

							IResourceDelta[] delta = event.getDelta().getAffectedChildren();

							for (IResourceDelta resourceDelta : delta)
							{

								if (resourceDelta.getResource() instanceof IProject
										&& isValidProject(((IProject) resourceDelta.getResource())))
								{

									if (isFileChange(resourceDelta)
											|| (resourceDelta.getKind() & IResourceDelta.ADDED) == IResourceDelta.ADDED)
									{
										projectToUpdate = ((IProject) resourceDelta.getResource());
										expandTraces(0);
									}
								}
							}
							break;
					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}

		};
		// ResourcesPlugin.getWorkspace()
		// .addResourceChangeListener(resourceChangedListener,
		// IResourceChangeEvent.POST_CHANGE);

	}

	private void expandTraces(int delay)
	{
		final Job expandJob = new Job("Expand traces")
		{

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{

				// expandCompleted = false;
				if (projectToUpdate != null)
				{
					monitor.worked(IProgressMonitor.UNKNOWN);
					setTraceHelper(projectToUpdate);

					// final IProject[] iprojects =
					// iworkspaceRoot.getProjects();

					display.asyncExec(new Runnable()
					{

						public void run()
						{
							updateProject(projectToUpdate);
						}

					});

				} else
					expandSpecInTree.run();
				refreshTree();
				monitor.done();
				// expandCompleted = true;

				return new Status(IStatus.OK, "org.overturetool.traces", IStatus.OK, "Expand completed", null);

			}

		};
		expandJob.setPriority(Job.INTERACTIVE);
		expandJob.schedule(delay);
	}

	private boolean isFileChange(IResourceDelta delta)
	{
		boolean ret = false;
		if (delta.getAffectedChildren().length == 0)
		{

			// int a = (delta.getFlags() & IResourceDelta.CONTENT);
			// int b = (delta.getFlags() & IResourceDelta.MARKERS);
			// boolean sync = (delta.getFlags() & IResourceDelta.SYNC) ==
			// IResourceDelta.SYNC;
			boolean add = (delta.getKind() & IResourceDelta.ADDED) == IResourceDelta.ADDED;
			if ((delta.getFlags() & IResourceDelta.CONTENT) == IResourceDelta.CONTENT
					|| add)// &&
				// for (String ex : TracesTreeView.exts)
				// {
				// if (delta.getFullPath().toString().endsWith(ex))
				ret = true;
			// }

			else
				ret = false;
		} else
		{
			for (IResourceDelta d : delta.getAffectedChildren())
			{
				ret = ret || isFileChange(d);
			}
		}
		return ret;
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
		// manager.add(actionRunSelected);

		manager.add(actionRunAll);
		// manager.add(new Separator());
		// manager.add(saveTraceResultsAction);
		manager.add(new Separator());
		manager.add(actionSetSort);
		manager.add(new Separator());
		manager.add(actionSetOkFilter);
		manager.add(actionSetInconclusiveFilter);
		manager.add(new Separator());
		manager.add(actionSelectToolBoxVDMJ);
		// manager.add(actionSelectToolBoxVDMTools);

	}

	private void fillContextMenu(IMenuManager manager)
	{

		// manager.add(actionRunAll);

		ISelection selection = viewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();

		if (obj instanceof ProjectTreeNode || obj instanceof ClassTreeNode) // ||
		// obj
		// instanceof
		// TraceTreeNode
		{
			manager.add(actionRunSelected);

			manager.add(actionRunSelectedAdvanced);
		}
		if (obj instanceof TraceTestTreeNode)
			if (((TraceTestTreeNode) obj).getStatus() != null)
			{
				manager.add(actionSendToInterpreter);
				// if (((TraceTestTreeNode) obj).GetStatus() ==
				// TestResultType.Inconclusive)
				// manager.add(okTraceTestCaseAction);
				// manager.add(failTraceTestCaseAction);

			}

		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(refreshAction);
		manager.add(actionSetSort);
		manager.add(new Separator());
		manager.add(actionRunAll);
		// manager.add(saveTraceResultsAction);
		manager.add(new Separator());
		manager.add(actionSetOkFilter);
		manager.add(actionSetInconclusiveFilter);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
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
				Dictionary<String, List<String>> classTracesTestCase = new Hashtable<String, List<String>>();
				if (obj instanceof ClassTreeNode)
				{
					ClassTreeNode cn = (ClassTreeNode) obj;

					projectName = cn.getParent().getName();

					List<String> tmpTraces = new Vector<String>();

					classTracesTestCase.put(cn.getName(), tmpTraces);

				} else if (obj instanceof ProjectTreeNode)
				{
					ProjectTreeNode pn = (ProjectTreeNode) obj;
					projectName = pn.getName();
				}

				final Dictionary<String, List<String>> finalClassTracesTestCase = classTracesTestCase;
				final String finalProjectName = projectName;

				Job executeTestJob = new Job("CT evaluating selected tests")
				{

					@Override
					protected IStatus run(IProgressMonitor monitor)
					{
						try
						{
							ITracesHelper th = traceHelpers.get(finalProjectName);

							projectToUpdate = getProject(finalProjectName);

							if (finalClassTracesTestCase.size() == 0)
								runTestProject(th, monitor);
							else
							{
								Enumeration<String> classKeys = finalClassTracesTestCase.keys();
								while (classKeys.hasMoreElements())
								{
									String className = classKeys.nextElement();
									try
									{
										th.processClassTraces(className, monitor);
									} catch (CancellationException e)
									{
										ConsolePrint(e.getMessage());
									} catch (ContextException e)
									{
										ConsoleError(e.getMessage());

									} catch (Exception e)
									{
										ConsoleError(e.getMessage());
										e.printStackTrace();

									}

								}
monitor.done();
							}

						} catch (Exception e)
						{
							e.printStackTrace();
						}

						expandTraces(0);
						return new Status(IStatus.OK, "org.overturetool.traces", IStatus.OK, "CT Test evaluation finished", null);
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
			    dialog.setLocation(pt.x-(d.getSize().x/2), pt.y-(d.getSize().y/2));
				dialog.open();
				while (!dialog.isDisposed())
				{
					if (d.isCanceled)
						return;
					if (!display.readAndDispatch())
						display.sleep();
				}
				
				if (d.isCanceled)
					return;

				ISelection selection = viewer.getSelection();
				final Object obj = ((IStructuredSelection) selection).getFirstElement();

				String projectName = "";
				Dictionary<String, List<String>> classTracesTestCase = new Hashtable<String, List<String>>();
				if (obj instanceof ClassTreeNode)
				{
					ClassTreeNode cn = (ClassTreeNode) obj;

					projectName = cn.getParent().getName();

					List<String> tmpTraces = new Vector<String>();

					classTracesTestCase.put(cn.getName(), tmpTraces);

				} else if (obj instanceof ProjectTreeNode)
				{
					ProjectTreeNode pn = (ProjectTreeNode) obj;
					projectName = pn.getName();
				}

				final Dictionary<String, List<String>> finalClassTracesTestCase = classTracesTestCase;
				final String finalProjectName = projectName;

				Job executeTestJob = new Job("CT evaluating selected tests")
				{

					@Override
					protected IStatus run(IProgressMonitor monitor)
					{
						try
						{
							ITracesHelper th = traceHelpers.get(finalProjectName);

							projectToUpdate = getProject(finalProjectName);

							if (finalClassTracesTestCase.size() == 0)
								runTestProject(th, monitor);
							else
							{
								Enumeration<String> classKeys = finalClassTracesTestCase.keys();
								while (classKeys.hasMoreElements())
								{
									String className = classKeys.nextElement();
									try
									{
										projectToUpdate.refreshLocal(IResource.DEPTH_INFINITE, null);
										th.processClassTraces(className, d.getSubset(), d.getTraceReductionType(), d.getSeed(), monitor);
									} catch (CancellationException e)
									{
										ConsolePrint(e.getMessage());
									} catch (ContextException e)
									{
										ConsoleError(e.getMessage());

									} catch (Exception e)
									{
										ConsoleError(e.getMessage());
										e.printStackTrace();

									}

								}

							}

						} catch (Exception e)
						{
							e.printStackTrace();
						}

						expandTraces(0);
						return new Status(IStatus.OK, "org.overturetool.traces", IStatus.OK, "CT Test evaluation finished", null);
					}

				};

				executeTestJob.schedule();

			}
		};
		actionRunSelectedAdvanced.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_RUN_SELECTED_TRACE));
		// actionRunSelected.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

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
						IWorkspaceRoot iworkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
						IProject[] iprojects = iworkspaceRoot.getProjects();
						int totalCount = 0;
						for (final IProject project : iprojects)
						{
							try
							{
								if (isValidProject(project))
								{
									ITracesHelper th = traceHelpers.get(project.getName());
									if (th != null)
										for (String className : th.getClassNamesWithTraces())
										{
											totalCount += th.getTraceDefinitions(className).size();

										}
								}
							} catch (Exception e)
							{

								e.printStackTrace();
							}

						}

						for (final IProject project : iprojects)
						{
							if (monitor.isCanceled())
								break;
							try
							{
								if (isValidProject(project) && traceHelpers.containsKey(project.getName()))
								{
									ITracesHelper th = traceHelpers.get(project.getName());									
									for (String className : th.getClassNamesWithTraces())
									{
										if (monitor.isCanceled())
											break;
										th.processClassTraces(className, monitor);
									}
								}
							} catch (CancellationException e)
							{
								ConsolePrint(e.getMessage());
							} catch (Exception e)
							{

								e.printStackTrace();

							}

						}
						display.asyncExec(new Runnable()
						{

							public void run()
							{

								updateTraceTestCasesNodeStatus();
								//saveTraceResultsAction.setEnabled(true);
							}

						});
						refreshTree();
						return new Status(IStatus.OK, "org.overturetool.traces", IStatus.OK, "CT Test evaluation finished", null);
					}

				};

				runAllTestsJob.schedule();
			}
		};

		actionRunAll.setToolTipText("Run all");
		actionRunAll.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_RUN_ALL_TRACES));

		expandSpecInTree = new Action()
		{
			@Override
			public void run()
			{
				IWorkspaceRoot iworkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
				final IProject[] iprojects = iworkspaceRoot.getProjects();

				display.asyncExec(new Runnable()
				{

					public void run()
					{
						for (IProject project : iprojects)
						{
							updateProject(project);
						}
						viewer.refresh();
					}

				});

			}

		};

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
						isSet = true;
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
						isSet = true;
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

		actionSelectToolBoxVDMJ = new Action("Use VDMJ")
		{
			@Override
			public void run()
			{
				actionSelectToolBoxVDMJ.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_VDMJ_LOGO_PRESSED));
				actionSelectToolBoxVDMTools.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_VDM_TOOLS_LOGO));
				setTraceHelpers();
				updateTraceTestCasesNodeStatus();
				viewer.refresh();
			}
		};
		actionSelectToolBoxVDMJ.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_VDMJ_LOGO_PRESSED));

		actionSelectToolBoxVDMTools = new Action("Use VDM Tools")
		{
			@Override
			public void run()
			{
				if (VDMToolsPath.length() == 0)
				{
					display.asyncExec(new Runnable()
					{

						public void run()
						{
							Shell s = new Shell(display);

							FileDialog fd = new FileDialog(s, SWT.OPEN
									| SWT.SIMPLE);
							fd.setText("Select VDM Tools (e.g. vppgde.exe)");

							if (IsWindows())
							{
								fd.setFilterPath(VDM_TOOLS_PATH_DEFAULT);
								String[] filterExt = { "*.exe", "*.*" };
								fd.setFilterExtensions(filterExt);
							}
							String selected = fd.open();
							VDMToolsPath = selected;
							setTraceHelpers();
							updateTraceTestCasesNodeStatus();
							viewer.refresh();
						}
					});
				} else
				{
					setTraceHelpers();
					updateTraceTestCasesNodeStatus();
					viewer.refresh();
				}

				actionSelectToolBoxVDMTools.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_VDM_TOOLS_LOGO_PRESSED));
				actionSelectToolBoxVDMJ.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_VDMJ_LOGO));
			}
		};
		actionSelectToolBoxVDMTools.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_VDM_TOOLS_LOGO));

	

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

					String projectName ="";
					
					TraceTreeNode traceNode=null;
					ITreeNode n = traceTestNode;
					while(n!=null  && !(n instanceof ProjectTreeNode))
					{
						if(n instanceof TraceTreeNode)
						{
							traceNode = (TraceTreeNode) n;
						}
						n = n.getParent();
						
					}
				projectName = n.getName();
//					String className = traceTestNode.getParent().getParent().getName();
//					String traceName = traceTestNode.getParent().getName();
					ITracesHelper th = traceHelpers.get(projectName);
					IVdmProject project = ((VdmjTracesHelper) th).project;

					//TraceTreeNode traceNode = (TraceTreeNode) traceTestNode.getParent();
					
					
					new TraceDebugLauncher().Launch(project, traceNode.getInfo(), traceTestNode.getNumber());
				}
			}
		};

		actionSendToInterpreter.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_INTERPRETER));
		// actionSendToInterpreter.setEnabled(false);

	}

	// -----------------------------update
	private void updateTraceTestCasesNodeStatus()
	{
		TreeItem[] aa = viewer.getTree().getItems();
		for (TreeItem treeItem : aa)
		{
			if (treeItem.getData() instanceof ProjectTreeNode)
			{
				ProjectTreeNode projectNode = ((ProjectTreeNode) treeItem.getData());
				ITracesHelper th = traceHelpers.get(projectNode.getName());
				for (ITreeNode classNode : projectNode.getChildren())
				{
					for (ITreeNode traceNode : classNode.getChildren())
					{
						updateTraceTestCasesNodeStatus(th, (TraceTreeNode) traceNode);

					}

				}

			}
		}
		viewer.refresh();
	}

	private void updateTraceTestCasesNodeStatus(ITracesHelper th,
			TraceTreeNode traceNode)
	{
		try
		{
			traceNode.setSkippedCount(th.getSkippedCount(traceNode.getParent().getName(), traceNode.getName()));
		} catch (SAXException e)
		{

			e.printStackTrace();
		} catch (IOException e)
		{

			e.printStackTrace();
		} catch (ClassNotFoundException e)
		{
			ConsolePrint(e.toString());
		}

	}

	private void updateProject(IProject project)
	{
		if (viewer == null || viewer.getControl().isDisposed())
		{
			return; // skip if disposed
		}
		TreeItem[] aa = viewer.getTree().getItems();
		boolean insertProject = true;
		for (TreeItem treeItem : aa)
		{
			if (treeItem.getData() instanceof ProjectTreeNode
					&& ((ProjectTreeNode) treeItem.getData()).getName().equals(project.getName()))
			{
				insertProject = false;
				ProjectTreeNode projectNode = ((ProjectTreeNode) treeItem.getData());
				String projectName = projectNode.getName();
				ITracesHelper th = traceHelpers.get(projectName);

				projectNode.getChildren().clear();

				// now no nodes are present
				try
				{
					for (String className : th.getClassNamesWithTraces())
					{
						ClassTreeNode classNode = new ClassTreeNode(className);
						for (NamedTraceDefinition traceName : th.getTraceDefinitions(className))
						{

							TraceTreeNode traceNode = new TraceTreeNode(traceName, th, className);

							classNode.addChild(traceNode);

						}
						projectNode.addChild(classNode);

					}
					viewer.refresh(projectNode);
				} catch (Exception e)
				{

					e.printStackTrace();
				}
				viewer.refresh(projectNode);
				viewer.expandToLevel(projectNode, 2);
			}
		}
		if (insertProject && traceHelpers.get(project.getName()) != null)
		{
			((ViewContentProvider) viewer.getContentProvider()).addChild(new ProjectTreeNode(project));
			viewer.refresh();
			updateProject(project);
		}
	}

	// --------------
	// ---------------- Get results

	private void runTestProject(ITracesHelper th, IProgressMonitor monitor)
			throws IOException
	{

		try
		{
			for (String className : th.getClassNamesWithTraces())
			{
				if (monitor != null && monitor.isCanceled())
					return;

				try
				{
					th.processClassTraces(className, monitor);
				} catch (Exception e)
				{
					e.printStackTrace();
					ConsolePrint(e.getMessage());
				}

			}
		} catch (TraceHelperNotInitializedException e)
		{
			ConsolePrint("Trace helper not initialized for project: "
					+ e.getProjectName());
			e.printStackTrace();
		}

	}

	private void hookDoubleClickAction()
	{
		// viewer.addDoubleClickListener(new IDoubleClickListener() {
		// public void doubleClick(DoubleClickEvent event) {
		// doubleClickAction.run();
		// }
		// });
	}

	// private IFile GetFile(IProject project, File file)
	// {
	// IFile f = (IFile) project.findMember(file.getName(), true);
	// if (f == null)
	// {
	// for (IFile projectFile : getAllMemberFiles(project, exts))
	// {
	// if (projectFile.getLocation().toOSString().equals(
	// file.getPath()))
	// return projectFile;
	// }
	// }
	// return f;
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
						try
						{
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IPageLayout.ID_PROGRESS_VIEW);
						} catch (PartInitException e)
						{
							e.printStackTrace();
						}
					else
						try
						{
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(TracesConstants.TRACES_TEST_ID);
							gotoTraceDefinition(findTraceTreeNode((TraceTestTreeNode)selection));
						} catch (PartInitException e)
						{
							e.printStackTrace();
						}

				}
			}

			private TraceTreeNode findTraceTreeNode(ITreeNode selection)
			{
				if(selection!= null)
				{
					if(selection.getParent() == null)
					{
						return null;
					}else if( selection.getParent() instanceof TraceTreeNode ){
						return (TraceTreeNode) selection.getParent();
					}
					else{
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
					// viewer.remove(node);
					// viewer.getTree().clearAll(true);
					// viewer.refresh(node);
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

						e.printStackTrace();
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

						e.printStackTrace();
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
				return false;
			else
				return true;
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
				return false;
			else
				return true;
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
					return 1;
				else if (res == Verdict.INCONCLUSIVE)
					return 2;
				else if (res == Verdict.PASSED)
					return 3;
			}
			return 3;
			// return super.category(element);
		}
	};

	public static Boolean IsWindows()
	{
		String osName = System.getProperty("os.name");

		return osName.toUpperCase().indexOf("WINDOWS".toUpperCase()) > -1;
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
					e.printStackTrace();
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
							activePage.showView(IConsoleConstants.ID_CONSOLE_VIEW, null, IWorkbenchPage.VIEW_VISIBLE);
					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});

	}
	
	private void gotoTraceDefinition(TraceTreeNode tn)
	{
		if(tn == null)
		{
			return ;
		}
		IWorkspaceRoot iworkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		String projectName = tn.getParent().getParent().getName();
		IProject iproject = iworkspaceRoot.getProject(projectName);

		ITracesHelper helper = traceHelpers.get(projectName);

		try
		{
			IVdmProject vdmProject = (IVdmProject) iproject.getAdapter(IVdmProject.class);
			
			IFile file = vdmProject.findIFile(helper.getFile(tn.getParent().getName()));

			EditorUtility.gotoLocation(file, tn.getTraceDefinition().location, tn.getName());
		} catch (IOException e)
		{
			ConsolePrint("File not found: " + e.getMessage());
			e.printStackTrace();
		} catch (ClassNotFoundException e)
		{
			ConsolePrint(e.toString());
			e.printStackTrace();
		} catch (TraceHelperNotInitializedException e)
		{
			ConsolePrint("Trace helper not initialized for project: "
					+ e.getProjectName());
			e.printStackTrace();
		}
	}

	private MessageConsole findConsole(String name)
	{
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
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

			if (project.isOpen()
			// && project.getNature(VdmPpProjectNature.VDM_PP_NATURE) != null
					&& project.getName().equals(finalProjectName))
				return project;
		}
		return null;
	}

}