package org.overturetool.eclipse.plugins.traces.views;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.overturetool.eclipse.plugins.editor.core.OvertureNature;
import org.overturetool.eclipse.plugins.traces.OvertureTracesPlugin;
import org.overturetool.eclipse.plugins.traces.views.treeView.ClassTreeNode;
import org.overturetool.eclipse.plugins.traces.views.treeView.NotYetReadyTreeNode;
import org.overturetool.eclipse.plugins.traces.views.treeView.ProjectTreeNode;
import org.overturetool.eclipse.plugins.traces.views.treeView.TraceTestGroup;
import org.overturetool.eclipse.plugins.traces.views.treeView.TraceTestTreeNode;
import org.overturetool.eclipse.plugins.traces.views.treeView.TraceTreeNode;
import org.overturetool.traces.VdmjTracesHelper;
import org.overturetool.traces.utility.ITracesHelper;
import org.overturetool.traces.utility.TraceError;
import org.overturetool.traces.utility.TraceTestStatus;
import org.overturetool.traces.utility.ITracesHelper.TestResultType;
import org.overturetool.vdmj.definitions.NamedTraceDefinition;
import org.xml.sax.SAXException;

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
 * http://www.eclipse.org/articles/Article-TreeViewer/TreeViewerArticle.htm
 */

public class TracesTreeView extends ViewPart
{
	private TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action actionRunSelected;
	private Action actionRunAll;
	private Action actionSetOkFilter;
	private Action actionSetSort;
	private Action actionSetInconclusiveFilter;
	private Action failTraceTestCaseAction;
	private Action okTraceTestCaseAction;
	private Action actionSendToInterpreter;
	private Action actionSelectToolBox;
	private Action actionSelectToolBoxVDMJ;
	private Action actionSelectToolBoxVDMTools;
	private Action actionRefreshTree;
	// private Action doubleClickAction;
	// private Action treeAction;
	private Action expandSpecInTree;
	private Action saveTraceResultsAction;
	private Dictionary<String, ITracesHelper> traceHelpers;
	private Dictionary<String, IFile> pathTofile;
	private String savePath = "";
	// private boolean expandCompleted = false;
	// private static ILock lock = Platform.getJobManager().newLock();
	final Display display = Display.getCurrent();
	static String[] exts = new String[] { "vpp", "tex", "vdm" }; // TODO get
	private IResourceChangeListener resourceChangedListener = null;
	private IProject projectToUpdate = null;
	private String VDMToolsPath = "";
	final String VDM_TOOLS_PATH_DEFAULT = "C:\\Program Files\\The VDM++ Toolbox v8.2b\\bin";
	private boolean UseVDMJ = true;
	Button buttonSetSort = null;

	public ITracesHelper GetTracesHelper(String projectName)
	{
		return this.traceHelpers.get(projectName);
		// ScriptRuntime.getInterpreterInstall(proj);

	}

	/**
	 * This method returns a list of files under the given directory or its
	 * subdirectories. The directories themselves are not returned.
	 * 
	 * @param dir
	 *            a directory
	 * @return list of IResource objects representing the files under the given
	 *         directory and its subdirectories
	 */
	private static List<IFile> getAllMemberFiles(IContainer dir, String[] exts)
	{
		ArrayList<IFile> list = new ArrayList<IFile>();
		IResource[] arr = null;
		try
		{
			arr = dir.members();
		} catch (CoreException e)
		{
		}

		for (int i = 0; arr != null && i < arr.length; i++)
		{
			if (arr[i].getType() == IResource.FOLDER)
			{
				list.addAll(getAllMemberFiles((IFolder) arr[i], exts));
			} else
			{
				for (int j = 0; j < exts.length; j++)
				{
					if (exts[j].equalsIgnoreCase(arr[i].getFileExtension()))
					{
						list.add((IFile) arr[i]);
						break;
					}
				}
			}
		}
		return list;
	}

	private void SetTraceHelper(IProject project)
	{
		List<IFile> fileNameList = new ArrayList<IFile>();

		try
		{
			// if the project is a overture project
			if (project.isOpen()
					&& project.getNature(OvertureNature.NATURE_ID) != null)
			{
				fileNameList = getAllMemberFiles(project, exts);

				// create project node
				// creates File array
				File[] fileArray = new File[fileNameList.size()];
				for (int i = 0; i < fileNameList.size(); i++)
				{
					fileArray[i] = fileNameList.get(i).getLocation().toFile();

					pathTofile.put(
							fileArray[i].getAbsolutePath(),
							fileNameList.get(i));
					//						
					final IFile tmpFile = fileNameList.get(i);
					display.asyncExec(new Runnable()
					{
						public void run()
						{
							// TODO Auto-generated method stub
							try
							{
								tmpFile.deleteMarkers(
										IMarker.PROBLEM,
										false,
										IResource.DEPTH_INFINITE);
							} catch (CoreException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
				}
				traceHelpers.remove(project.getName());
				// traceHelpers.put(project.getName(), new
				// TracesHelper(VDMToolsPath, fileArray, UseVDMJ, 3));
				traceHelpers.put(
						project.getName(),
						new VdmjTracesHelper(
								new File(
										project.getWorkspace().getRoot().getLocation().toOSString()
												+ project.getFullPath().toOSString()),
								fileArray, 3));
			}
		} catch (Exception e1)
		{
			System.out.println("Exception: " + e1.getMessage());
			e1.printStackTrace();
		}

	}

	private void SetTraceHelpers()
	{
		IWorkspaceRoot iworkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] iprojects = iworkspaceRoot.getProjects();
		this.traceHelpers = new Hashtable<String, ITracesHelper>();
		this.pathTofile = new Hashtable<String, IFile>();

		for (int j = 0; j < iprojects.length; j++)
		{
			try
			{
				SetTraceHelper(iprojects[j]);
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

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent)
	{

		// PatternFilter patternFilter = new PatternFilter();
		// viewer = new FilteredTree(parent, SWT.MULTI
		// | SWT.H_SCROLL | SWT.V_SCROLL, patternFilter).getViewer();

		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);
		SetTraceHelpers();
		viewer.setContentProvider(new ViewContentProvider(this.traceHelpers,
				this));
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

		ExpandTraces(1000);

		resourceChangedListener = new IResourceChangeListener()
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

							if (resourceDelta.getResource() instanceof IProject)
							{

								if (IsFileChange(resourceDelta)
										|| (resourceDelta.getKind() & IResourceDelta.ADDED) == IResourceDelta.ADDED)
								{
									projectToUpdate = ((IProject) resourceDelta.getResource());
									ExpandTraces(0);
								}
							}
						}
						break;
					}
				} catch (Exception e)
				{
					// TODO: handle exception
				}
			}

		};
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				resourceChangedListener,
				IResourceChangeEvent.POST_CHANGE);

	}

	private void ExpandTraces(int delay)
	{
		final Job expandJob = new Job("Expand traces")
		{

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{

				// expandCompleted = false;
				if (projectToUpdate != null)
				{

					SetTraceHelper(projectToUpdate);
					monitor.worked(50);
					// final IProject[] iprojects =
					// iworkspaceRoot.getProjects();

					display.asyncExec(new Runnable()
					{

						public void run()
						{
							UpdateProject(projectToUpdate);
						}

					});

				} else
					expandSpecInTree.run();
				monitor.worked(100);
				// expandCompleted = true;

				return new Status(IStatus.OK, "org.overturetool.traces",
						IStatus.OK, "Expand completed", null);

			}

		};
		expandJob.setPriority(Job.INTERACTIVE);
		expandJob.schedule(delay);
	}

	private boolean IsFileChange(IResourceDelta delta)
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
				for (String ex : TracesTreeView.exts)
				{
					if (delta.getFullPath().toString().endsWith(ex))
						ret = true;
				}

			else
				ret = false;
		} else
		{
			for (IResourceDelta d : delta.getAffectedChildren())
			{
				ret = ret || IsFileChange(d);
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
//		manager.add(new Separator());
//		manager.add(saveTraceResultsAction);
		manager.add(new Separator());
		manager.add(actionSetSort);
		manager.add(new Separator());
		manager.add(actionSetOkFilter);
		manager.add(actionSetInconclusiveFilter);
		manager.add(new Separator());
		manager.add(actionSelectToolBoxVDMJ);
//		manager.add(actionSelectToolBoxVDMTools);

	}

	private void fillContextMenu(IMenuManager manager)
	{
		manager.add(actionRunSelected);
		// manager.add(actionRunAll);

		ISelection selection = viewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		if (obj instanceof TraceTestTreeNode)
			if (((TraceTestTreeNode) obj).GetStatus() != TestResultType.Unknown)
			{
				manager.add(actionSendToInterpreter);
//				if (((TraceTestTreeNode) obj).GetStatus() == TestResultType.Inconclusive)
//					manager.add(okTraceTestCaseAction);
//				manager.add(failTraceTestCaseAction);

			}

		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{

		manager.add(actionSetSort);
		manager.add(new Separator());
		manager.add(actionRunAll);
//		manager.add(saveTraceResultsAction);
		manager.add(new Separator());
		manager.add(actionSetOkFilter);
		manager.add(actionSetInconclusiveFilter);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}

	private void makeActions()
	{
		actionRunSelected = new Action()
		{

			@Override
			public void run()
			{

				ISelection selection = viewer.getSelection();
				final Object obj = ((IStructuredSelection) selection).getFirstElement();
				int tracesTestCount = 0;

				String projectName = "";
				Dictionary<String, Dictionary<String, List<Integer>>> classTracesTestCase = new Hashtable<String, Dictionary<String, List<Integer>>>();

				if (obj instanceof TraceTestTreeNode)
				{

					TraceTestTreeNode node = (TraceTestTreeNode) obj;
					TraceTreeNode tn = node.getParent();
					String className = (tn.getParent()).getName();
					projectName = ((tn.getParent()).getParent()).getName();

					List<Integer> tmpTestCases = new ArrayList<Integer>();
					tmpTestCases.add(node.getNumber());

					Dictionary<String, List<Integer>> tmpTraces = new Hashtable<String, List<Integer>>();
					tmpTraces.put(tn.getName(), tmpTestCases);

					classTracesTestCase.put(className, tmpTraces);
					tracesTestCount = 1;

				} else if (obj instanceof TraceTreeNode)
				{
					TraceTreeNode tn = (TraceTreeNode) obj;
					String className = (tn.getParent()).getName();
					projectName = ((tn.getParent()).getParent()).getName();

					List<Integer> tmpTestCases = new ArrayList<Integer>();

					Dictionary<String, List<Integer>> tmpTraces = new Hashtable<String, List<Integer>>();
					tmpTraces.put(tn.getName(), tmpTestCases);

					classTracesTestCase.put(className, tmpTraces);

					tracesTestCount = tn.getChildren().size();

				} else if (obj instanceof ClassTreeNode)
				{
					ClassTreeNode cn = (ClassTreeNode) obj;

					projectName = cn.getParent().getName();

					Dictionary<String, List<Integer>> tmpTraces = new Hashtable<String, List<Integer>>();

					classTracesTestCase.put(cn.getName(), tmpTraces);

					for (TraceTreeNode tn : cn.getChildren())
					{
						tracesTestCount += tn.getChildren().size();
					}

				} else if (obj instanceof ProjectTreeNode)
				{
					ProjectTreeNode pn = (ProjectTreeNode) obj;
					projectName = pn.getName();

					for (ClassTreeNode cn : pn.getChildren())
					{
						for (TraceTreeNode tn : cn.getChildren())
						{
							tracesTestCount += tn.getChildren().size();
						}
					}

				}

				final Dictionary<String, Dictionary<String, List<Integer>>> finalClassTracesTestCase = classTracesTestCase;
				final String finalProjectName = projectName;
				final int tracesTestCountFinal = tracesTestCount;
				Job executeTestJob = new Job("Execute CT tests")
				{

					@Override
					protected IStatus run(IProgressMonitor monitor)
					{
						try
						{
							int progress = 0;
							// monitor.worked(progress);

							ITracesHelper th = traceHelpers.get(finalProjectName);
							if (finalClassTracesTestCase.size() == 0)
								progress = RunTestProject(th, monitor, progress);
							else
							{

								Enumeration<String> classKeys = finalClassTracesTestCase.keys();
								while (classKeys.hasMoreElements())
								{
									String className = classKeys.nextElement();
									try
									{
										th.processClassTraces(
												className,
												monitor);
									} catch (Exception e)
									{
										IWorkspaceRoot iworkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
										IProject[] iprojects = iworkspaceRoot.getProjects();
										for (IProject project2 : iprojects)
										{
											if (project2.getName().equals(
													finalProjectName))
											{
												addMarker(
														GetFile(
																project2,
																th.GetFile(className)),
														e.getMessage(),
														1,
														IMarker.SEVERITY_ERROR);
												ConsolePrint(e.getMessage());
												break;
											}
										}

									}
									// if
									// (finalClassTracesTestCase.get(className).size()
									// == 0)
									// progress = RunTestClass(
									// th,
									// className,
									// monitor,
									// progress);
									// else
									// {
									// Enumeration<String> traceKeys =
									// finalClassTracesTestCase.get(
									// className).keys();
									// while (traceKeys.hasMoreElements())
									// {
									// String traceName =
									// traceKeys.nextElement();
									//
									// if (finalClassTracesTestCase.get(
									// className).get(traceName).size() == 0)
									// progress = RunTestTrace(
									// th,
									// className,
									// traceName,
									// monitor,
									// progress);
									// // else
									// // {
									// // List<Integer> tests =
									// finalClassTracesTestCase.get(
									// // className).get(
									// // traceName);
									// // for (Integer testCaseName : tests)
									// // {
									// // progress = RunTestTraceTestCase(
									// // th,
									// // className,
									// // traceName,
									// // testCaseName,
									// // monitor,
									// // progress);
									// //
									// // }
									// // }
									//
									// }
									// }
								}

							}

						} catch (Exception e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						// display.asyncExec(new Runnable()
						// {
						//
						// public void run()
						// {
						// UpdateProject(projectToUpdate);
						// // ITracesHelper th =
						// traceHelpers.get(finalProjectName);
						// // ISelection selection = viewer.getSelection();
						// // Object obj1 = ((IStructuredSelection)
						// selection).getFirstElement();
						// // if (obj1 instanceof TraceTestTreeNode)
						// // {
						// // UpdateTraceTestCasesNodeStatus(
						// // th,
						// // (TraceTestTreeNode) obj);
						// // viewer.refresh(obj);
						// // } else if (obj1 instanceof TraceTreeNode)
						// // {
						// // UpdateTraceTestCasesNodeStatus(
						// // th,
						// // (TraceTreeNode) obj);
						// // viewer.refresh(obj);
						// // } else if (obj1 instanceof ClassTreeNode)
						// // {
						// // UpdateTraceTestCasesNodeStatus(
						// // th,
						// // (ClassTreeNode) obj);
						// // viewer.refresh(obj);
						// // } else if (obj1 instanceof ProjectTreeNode)
						// // {
						// // UpdateTraceTestCasesNodeStatus(
						// // th,
						// // (ProjectTreeNode) obj);
						// // viewer.refresh(obj);
						// // }
						// // // viewer.refresh();
						// // saveTraceResultsAction.setEnabled(true);
						//
						// }
						//
						// });
						// monitor.done();
						ExpandTraces(0);
						return new Status(IStatus.OK,
								"org.overturetool.traces", IStatus.OK,
								"Traces results saveing", null);
					}

				};

				executeTestJob.schedule();

			}
		};

		actionRunSelected.setText("Run selected");
		actionRunSelected.setToolTipText("Run selected");
		actionRunSelected.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_RUN_SELECTED_TRACE));
		// actionRunSelected.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		actionRunAll = new Action()
		{
			@Override
			public void run()
			{

				Job runAllTestsJob = new Job("CT Run all tests")
				{

					@Override
					protected IStatus run(IProgressMonitor monitor)
					{
						// TODO Auto-generated method stub

						IWorkspaceRoot iworkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
						IProject[] iprojects = iworkspaceRoot.getProjects();
						int totalCount = 0;
						for (final IProject project : iprojects)
						{
							try
							{
								if (project.isOpen()
										&& project.getNature(OvertureNature.NATURE_ID) != null)
								{
									ITracesHelper th = traceHelpers.get(project.getName());
									if (th != null)
										for (String className : th.GetClassNamesWithTraces())
										{
											totalCount += th.GetTraceDefinitions(
													className).size();
											// for (String trace :
											// th.GetTraces(className)) {
											// for (String testCaseNum :
											// th.GetTraceTestCases(className,
											// trace)) {
											// totalCount++;
											//													
											// }
											// }
										}
								}
							} catch (CoreException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (Exception e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

						for (final IProject project : iprojects)
						{
							if (monitor.isCanceled())
								break;
							try
							{
								if (project.isOpen()
										&& project.getNature(OvertureNature.NATURE_ID) != null)
								{
									ITracesHelper th = traceHelpers.get(project.getName());
									for (String className : th.GetClassNamesWithTraces())
									{
										if (monitor.isCanceled())
											break;
										th.processClassTraces(
												className,
												monitor);
									}
								}
							} catch (Exception e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();

							}

						}
						display.asyncExec(new Runnable()
						{

							public void run()
							{
								// TODO Auto-generated method stub
								UpdateTraceTestCasesNodeStatus();
								saveTraceResultsAction.setEnabled(true);
							}

						});
						return new Status(IStatus.OK,
								"org.overturetool.traces", IStatus.OK,
								"CT Run all tests", null);
					}

				};

				runAllTestsJob.schedule();
			}
		};
		actionRunAll.setText("Run all");
		actionRunAll.setToolTipText("Run all");
		actionRunAll.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_RUN_ALL_TRACES));

		

//		failTraceTestCaseAction = new Action()
//		{
//			@Override
//			public void run()
//			{
//				// try
//				// {
//				// ISelection selection = viewer.getSelection();
//				// Object obj = ((IStructuredSelection)
//				// selection).getFirstElement();
//				// TraceTestTreeNode node = (TraceTestTreeNode) obj;
//				// TraceTreeNode tn = node.getParent();
//				// String className = (tn.getParent()).getName();
//				// String project = ((tn.getParent()).getParent()).getName();
//				// ITracesHelper tr = traceHelpers.get(project);
//				//
//				// tr.SetFail(className, tn.getName(), node.getNumber());
//				// node.SetStatus(tr.GetResult(
//				// className,
//				// tn.getName(),
//				// node.getNumber()).getStatus());
//				// viewer.refresh(node);
//				// } catch (CGException e)
//				// {
//				// // TODO Auto-generated catch block
//				// e.printStackTrace();
//				// }
//
//			}
//		};
//		failTraceTestCaseAction.setText("Fail test");
//		failTraceTestCaseAction.setToolTipText("Fail selected test");
//		failTraceTestCaseAction.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_CASE_FAIL));

//		okTraceTestCaseAction = new Action()
//		{
//			@Override
//			public void run()
//			{
//				// try
//				// {
//				// ISelection selection = viewer.getSelection();
//				// Object obj = ((IStructuredSelection)
//				// selection).getFirstElement();
//				// TraceTestTreeNode node = (TraceTestTreeNode) obj;
//				// TraceTreeNode tn = node.getParent();
//				// String className = (tn.getParent()).getName();
//				// String project = ((tn.getParent()).getParent()).getName();
//				// ITracesHelper tr = traceHelpers.get(project);
//				//
//				// tr.SetOk(className, tn.getName(), node.getNumber());
//				// node.SetStatus(tr.GetResult(
//				// className,
//				// tn.getName(),
//				// node.getNumber()).getStatus());
//				// viewer.refresh(node);
//				// } catch (CGException e)
//				// {
//				// // TODO Auto-generated catch block
//				// e.printStackTrace();
//				// }
//
//			}
//		};
//		okTraceTestCaseAction.setText("Approve test");
//		okTraceTestCaseAction.setToolTipText("Approve selected test");
//		okTraceTestCaseAction.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_CASE_SUCCES));

//		saveTraceResultsAction = new Action()
//		{
//			@Override
//			public void run()
//			{
//				Shell shell = new Shell();
//
//				DirectoryDialog save = new DirectoryDialog(shell, SWT.SINGLE);
//
//				save.setText("Save CT results");
//				savePath = save.open();
//				if (savePath == null)
//					return;
//				Job job = new Job("Save Trace Results")
//				{
//
//					@Override
//					protected IStatus run(IProgressMonitor monitor)
//					{
//						// TODO Auto-generated method stub
//						// monitor.beginTask("Saving trace results", 100);
//						// monitor.worked(10);
//						Enumeration<String> itr = traceHelpers.keys();
//						while (itr.hasMoreElements())
//						{
//							String project = itr.nextElement();
//							String outputPath = savePath + File.separatorChar
//									+ project;
//
//							// traceHelpers.get(project).Save(outputPath);
//							ConsolePrint("Save not implemented");
//
//						}
//						// monitor.worked(100);
//
//						return new Status(IStatus.OK,
//								"org.overturetool.traces", IStatus.OK,
//								"Traces results saveing", null);
//					}
//
//				};
//				job.schedule();
//
//			}
//		};
//		saveTraceResultsAction.setText("Save results");
//		saveTraceResultsAction.setToolTipText("Save result of trace test");
//		saveTraceResultsAction.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_RUN_SAVE));
//		saveTraceResultsAction.setEnabled(false);
		

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
							UpdateProject(project);
						}
						viewer.refresh();
					}

				});

			}

		};

		actionSetOkFilter = new Action()
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
		actionSetOkFilter.setText("Filter ok results");
		actionSetOkFilter.setToolTipText("Filter all ok results from tree");
		actionSetOkFilter.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_CASE_FILTER_SUCCES));

		actionSetInconclusiveFilter = new Action()
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
		actionSetInconclusiveFilter.setText("Filter inconclusive results");
		actionSetInconclusiveFilter.setToolTipText("Filter all inconclusive results from tree");
		actionSetInconclusiveFilter.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_CASE_FILTER_UNDETERMINED));

		actionSetSort = new Action()
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
		actionSetSort.setText("Sort");
		actionSetSort.setToolTipText("Sort by verdict: Fail, Inconclusive, ok, etc.");
		actionSetSort.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_SORT));

		actionSelectToolBoxVDMJ = new Action("Use VDMJ")
		{
			@Override
			public void run()
			{
				UseVDMJ = true;
				actionSelectToolBoxVDMJ.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_VDMJ_LOGO_PRESSED));
				actionSelectToolBoxVDMTools.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_VDM_TOOLS_LOGO));
				SetTraceHelpers();
				UpdateTraceTestCasesNodeStatus();
				viewer.refresh();
			}
		};
		actionSelectToolBoxVDMJ.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_VDMJ_LOGO_PRESSED));

		actionSelectToolBoxVDMTools = new Action("Use VDM Tools")
		{
			@Override
			public void run()
			{
				UseVDMJ = false;
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
							SetTraceHelpers();
							UpdateTraceTestCasesNodeStatus();
							viewer.refresh();
						}
					});
				} else
				{
					SetTraceHelpers();
					UpdateTraceTestCasesNodeStatus();
					viewer.refresh();
				}

				actionSelectToolBoxVDMTools.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_VDM_TOOLS_LOGO_PRESSED));
				actionSelectToolBoxVDMJ.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_VDMJ_LOGO));
			}
		};
		actionSelectToolBoxVDMTools.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_VDM_TOOLS_LOGO));

		actionSelectToolBox = new Action("Select toolbox")
		{

		};
		actionSelectToolBox.setText("Select toolbox");

		actionSendToInterpreter = new Action()
		{
		};
		actionSendToInterpreter.setText("Send to Interpreter");
		actionSendToInterpreter.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_INTERPRETER));
		actionSendToInterpreter.setEnabled(false);

	}

	// -----------------------------update
	private void UpdateTraceTestCasesNodeStatus()
	{
		// TODO Auto-generated method stub
		TreeItem[] aa = viewer.getTree().getItems();
		for (TreeItem treeItem : aa)
		{
			if (treeItem.getData() instanceof ProjectTreeNode)
			{
				ProjectTreeNode projectNode = ((ProjectTreeNode) treeItem.getData());
				ITracesHelper th = traceHelpers.get(projectNode.getName());
				for (ClassTreeNode classNode : projectNode.getChildren())
				{
					for (TraceTreeNode traceNode : classNode.getChildren())
					{
						UpdateTraceTestCasesNodeStatus(th, traceNode);

					}

				}

			}
		}
		viewer.refresh();
	}

//	private void UpdateTraceTestCasesNodeStatus(ITracesHelper th,
//			ProjectTreeNode projectNode)
//	{
//		for (ClassTreeNode classNode : projectNode.getChildren())
//		{
//			UpdateTraceTestCasesNodeStatus(th, classNode);
//		}
//	}

//	private void UpdateTraceTestCasesNodeStatus(ITracesHelper th,
//			ClassTreeNode classNode)
//	{
//		for (TraceTreeNode traceNode : classNode.getChildren())
//		{
//			UpdateTraceTestCasesNodeStatus(th, traceNode);
//		}
//	}

	private void UpdateTraceTestCasesNodeStatus(ITracesHelper th,
			TraceTreeNode traceNode)
	{
		try
		{
			traceNode.SetSkippedCount(th.GetSkippedCount(
					traceNode.getParent().getName(),
					traceNode.getName()));
		} catch (SAXException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// for (TraceTestCaseTreeNode testNode : traceNode.getChildren())
		// {
		//			
		// UpdateTraceTestCasesNodeStatus(th, testNode);
		// // viewer.refresh(traceNode);
		// }
	}

//	private void UpdateTraceTestCasesNodeStatus(ITracesHelper th,
//			TraceTestTreeNode testNode)
//	{
//		// TODO Auto-generated method stub
//
//		try
//		{
//			TraceTreeNode traceNode = testNode.getParent();
//			ClassTreeNode classNode = testNode.getParent().getParent();
//			TraceTestStatus status = th.GetStatus(
//					classNode.getName(),
//					traceNode.getName(),
//					testNode.getNumber());
//			if (status.getStatus() == TestResultType.Skipped)
//				traceNode.removeChild(testNode);
//			else
//			{
//
//				testNode.SetStatus(status.getStatus());
//			}
//		}
//		// viewer.refresh(testNode);
//		catch (SAXException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	private void UpdateProject(IProject project)
	{
		TreeItem[] aa = viewer.getTree().getItems();
		boolean insertProject = true;
		for (TreeItem treeItem : aa)
		{
			if (treeItem.getData() instanceof ProjectTreeNode
					&& ((ProjectTreeNode) treeItem.getData()).getName().equals(
							project.getName()))
			{
				insertProject = false;
				ProjectTreeNode projectNode = ((ProjectTreeNode) treeItem.getData());
				String projectName = projectNode.getName();
				ITracesHelper th = traceHelpers.get(projectName);

				projectNode.getChildren().clear();

				// now no nodes are present
				try
				{
					for (String className : th.GetClassNamesWithTraces())
					{
						ClassTreeNode classNode = new ClassTreeNode(className);
						for (NamedTraceDefinition traceName : th.GetTraceDefinitions(className))
						{
							// if (th.HasError(className, traceName.name.name))
							// {
							// // set error marker
							// List<TraceError> errors = th.GetError(
							// className,
							// traceName.name.name);
							// for (TraceError traceError : errors)
							// {
							// addMarker(
							// GetFile(project, traceError.File),
							// traceError.Message,
							// traceError.Line,
							// IMarker.SEVERITY_ERROR);
							// }
							// } else
							{
								TraceTreeNode traceNode = new TraceTreeNode(
										traceName, th);

								Integer totalTests = th.GetTraceTestCount(
										className,
										traceName.name.name);
								traceNode.setTestTotal(totalTests);
								
								traceNode.SetSkippedCount(th.GetSkippedCount(className, traceName.name.name));
								
								if (totalTests > 0)
									traceNode.addChild(new NotYetReadyTreeNode());
								// for (TraceTestStatus traceTestCaseName :
								// th.GetTraceTests(
								// className,
								// traceName.name.name))
								// {
								//
								// TraceTestCaseTreeNode testCaseNode = new
								// TraceTestCaseTreeNode(
								// traceTestCaseName);
								// traceNode.addChild(testCaseNode);
								//
								// }

								classNode.addChild(traceNode);
							}
						}
						projectNode.addChild(classNode);

					}
					viewer.refresh(projectNode);
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				viewer.refresh(projectNode);
			}
		}
		if (insertProject && traceHelpers.get(project.getName()) != null)
		{
			((ViewContentProvider) viewer.getContentProvider()).addChild(new ProjectTreeNode(
					project));
			viewer.refresh();
			UpdateProject(project);
		}
	}

	// ---------------- Expand
	// private void ExpandProjectTreeNode(ProjectTreeNode projectNode)
	// {
	// String projectName = projectNode.getName();
	// ITracesHelper treaceHelper = traceHelpers.get(projectName);
	// for (ClassTreeNode classNode : projectNode.getChildren())
	// {
	// ExpandClassTreeNode(treaceHelper, classNode);
	// }
	// viewer.refresh(projectNode);
	// }
	//
	// private void ExpandClassTreeNode(ITracesHelper treaceHelper,
	// ClassTreeNode classNode)
	// {
	// String className = classNode.getName();
	// for (TraceTreeNode traceNode : classNode.getChildren())
	// {
	// ExpandTraceTreeNode(treaceHelper, traceNode, className);
	// }
	// viewer.refresh(classNode);
	// }
	//
	// private void ExpandTraceTreeNode(ITracesHelper traceHelper,
	// TraceTreeNode traceNode, String className)
	// {
	// try
	// {
	// if (traceHelper.HasError(className, traceNode.getName()))
	// {
	// // set error marker
	// IWorkspaceRoot iworkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
	// IProject[] iprojects = iworkspaceRoot.getProjects();
	// String projectName = traceNode.getParent().getParent().getName();
	// List<TraceError> errors = this.traceHelpers.get(projectName).GetError(
	// className,
	// traceNode.getName());
	// for (TraceError traceError : errors)
	// {
	//
	// IProject project = iworkspaceRoot.getProject(projectName);
	// addMarker(
	// GetFile(project, traceError.File),
	// traceError.Message,
	// traceError.Line,
	// IMarker.SEVERITY_ERROR);
	//
	// }
	// } else
	// {
	// Integer count = traceHelper.GetTraceTests(
	// className,
	// traceNode.getName()).size();
	// traceNode.setTestTotal(count);
	// }
	// // for (TraceTestStatus traceTestCase : traceHelper.GetTraceTests(
	// // className,
	// // traceNode.getName()))
	// // {
	// // traceNode.addChild(new TraceTestCaseTreeNode(traceTestCase));
	// // }
	// } catch (Exception e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// viewer.refresh(traceNode);
	// }

	// --------------
	// ---------------- Get results

	private int RunTestProject(ITracesHelper th, IProgressMonitor monitor,
			int progress) throws Exception
	{
		int tmpProgress = progress;
		for (String className : th.GetClassNamesWithTraces())
		{
			if (monitor != null && monitor.isCanceled())
				return progress;

			tmpProgress = RunTestClass(th, className, monitor, tmpProgress);
		}
		return tmpProgress;
	}

	private int RunTestClass(ITracesHelper th, String className,
			IProgressMonitor monitor, int progress) throws Exception
	{
		int tmpProgress = progress;
		for (NamedTraceDefinition traceName : th.GetTraceDefinitions(className))
		{

			if (monitor != null && monitor.isCanceled())
				return progress;

			tmpProgress = RunTestTrace(
					th,
					className,
					traceName.name.name,
					monitor,
					tmpProgress);
		}
		return tmpProgress;
	}

	private int RunTestTrace(ITracesHelper th, final String className,
			final String traceName, IProgressMonitor monitor, int progress)
			throws Exception
	{
		// int tmpProgress = progress;
		//
		// final ITracesHelper finalTh = th;
		//
		// List<TraceTestStatus> testsStatus = null;
		try
		{
			if (monitor != null && monitor.isCanceled())
				return progress;

			// monitor.subTask("Execution trace: " + traceName);

			// testsStatus =
			th.processSingleTrace(className, traceName, monitor);

			// if (monitor != null)
			// monitor.worked(progress + 1);

		} catch (Exception e)
		{
			ConsolePrint(e.getMessage());
		}

		// final List<TraceTestStatus> testsStatusFinal = testsStatus;
		// display.asyncExec(new Runnable()
		// {
		//
		// public void run()
		// {
		// String projectName = "";
		// Enumeration<String> keys = traceHelpers.keys();
		// while (keys.hasMoreElements())
		// {
		// String string = keys.nextElement();
		// if (traceHelpers.get(string).equals(finalTh))
		// projectName = string;
		//
		// }
		// TreeItem[] aa = viewer.getTree().getItems();
		// for (TreeItem treeItem : aa)
		// {
		// if (treeItem.getData() instanceof ProjectTreeNode)
		// {
		// ProjectTreeNode projectNode = ((ProjectTreeNode) treeItem.getData());
		// if (!projectNode.getName().equals(projectName))
		// continue;
		// for (ClassTreeNode classNode : projectNode.getChildren())
		// {
		// if (!classNode.getName().equals(className))
		// continue;
		// for (TraceTreeNode traceNode : classNode.getChildren())
		// {
		// if (!traceNode.getName().equals(traceName))
		// continue;
		//
		// traceNode.getChildren().clear();
		//
		// for (TraceTestStatus traceTestStatus : testsStatusFinal)
		// {
		// traceNode.addChild(new TraceTestTreeNode(
		// traceTestStatus));
		// }
		// viewer.refresh(traceNode);
		// // for (TraceTestCaseTreeNode node :
		// // traceNode.getChildren()) {
		// // if (node.getName().equals(testCaseName)) {
		// // node.SetRunTimeError();
		// // viewer.refresh(node);
		// // viewer.refresh(traceNode);
		// // return;
		// // }
		// // }
		// }
		// }
		// }
		// }
		// }
		//
		// });
		return progress++;
	}

	// for (String testCase : th.GetTraceTestCases(className, traceName)) {
	// if (monitor != null && monitor.isCanceled())
	// return progress;
	//
	// monitor.subTask("Execution trace: " + traceName);
	// tmpProgress = RunTestTraceTestCase(th, className, traceName, testCase,
	// monitor, tmpProgress);
	// }
	// return tmpProgress;

	// private int RunTestTraceTestCase(ITracesHelper th, final String
	// className,
	// final String traceName, final Integer testCaseName,
	// IProgressMonitor monitor, int progress) throws CGException
	// {
	// final ITracesHelper finalTh = th;
	// TraceTestStatus st=null;
	// try
	// {
	// if (monitor != null && monitor.isCanceled())
	// return progress;
	//
	// st= th.RunSingle(className, traceName, testCaseName);
	// if (monitor != null)
	// monitor.worked(progress + 1);
	// return progress + 1;
	// } catch (Exception e)
	// {
	// final TraceTestStatus status= st;
	// display.asyncExec(new Runnable()
	// {
	//
	// public void run()
	// {
	// String projectName = "";
	// Enumeration<String> keys = traceHelpers.keys();
	// while (keys.hasMoreElements())
	// {
	// String string = keys.nextElement();
	// if (traceHelpers.get(string).equals(finalTh))
	// projectName = string;
	//
	// }
	// TreeItem[] aa = viewer.getTree().getItems();
	// for (TreeItem treeItem : aa)
	// {
	// if (treeItem.getData() instanceof ProjectTreeNode)
	// {
	// ProjectTreeNode projectNode = ((ProjectTreeNode) treeItem.getData());
	// if (!projectNode.getName().equals(projectName))
	// continue;
	// for (ClassTreeNode classNode : projectNode.getChildren())
	// {
	// if (!classNode.getName().equals(className))
	// continue;
	// for (TraceTreeNode traceNode : classNode.getChildren())
	// {
	// if (!traceNode.getName().equals(traceName))
	// continue;
	// for (TraceTestTreeNode node : traceNode.getChildren())
	// {
	// if (node.getName().equals(testCaseName))
	// {
	// node.SetRunTimeError();
	// node.SetStatus(status.getStatus());
	// viewer.refresh(node);
	// viewer.refresh(traceNode);
	// return;
	// }
	// }
	// }
	// }
	// }
	// }
	// }
	//
	// });
	//
	// }
	// return progress;
	// }

	// ----------------------

	private void hookDoubleClickAction()
	{
		// viewer.addDoubleClickListener(new IDoubleClickListener() {
		// public void doubleClick(DoubleClickEvent event) {
		// doubleClickAction.run();
		// }
		// });
	}

	private IFile GetFile(IProject project, File file)
	{
		IFile f = (IFile) project.findMember(file.getName(), true);
		if (f == null)
		{
			for (IFile projectFile : getAllMemberFiles(project, exts))
			{
				if (projectFile.getLocation().toOSString().equals(
						file.getPath()))
					return projectFile;
			}
		}
		return f;
	}

	private void hookTreeAction()
	{
		viewer.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				// TODO Auto-generated method stub
				Object selection = ((ITreeSelection) event.getSelection()).getFirstElement();
				if (selection instanceof TraceTreeNode)
				{
					TraceTreeNode tn = (TraceTreeNode) selection;

				if (selection instanceof TraceTreeNode) {
					TraceTreeNode tn = (TraceTreeNode) selection;

					IWorkspaceRoot iworkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
					String projectName = tn.getParent().getParent().getName();
					IProject iproject = iworkspaceRoot.getProject(projectName);

					ITracesHelper helper = traceHelpers.get(projectName);

					try
					{
						gotoLine(
								GetFile(
										iproject,
										helper.GetFile(tn.getParent().getName())),
								tn.GetTraceDefinition().location.startLine,
								tn.getName());
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else if (selection instanceof TraceTestTreeNode)
				{
					if (!(selection instanceof NotYetReadyTreeNode)
							&& !(selection instanceof TraceTestGroup)
							&& ((TraceTestTreeNode) selection).GetStatus() == TestResultType.Unknown)
						try
						{
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
									IPageLayout.ID_PROGRESS_VIEW);
						} catch (PartInitException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					else
						try
						{
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
									"org.overturetool.eclipse.plugins.traces.views.TraceTest");
						} catch (PartInitException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

				}
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
					node.UnloadTests();
					refreshTree();
				} else if (expandingElement instanceof TraceTestGroup)
				{
					TraceTestGroup node = (TraceTestGroup) expandingElement;
					node.UnloadTests();
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
						node.LoadTests();
					} catch (Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					refreshTree();
				} else if (expandingElement instanceof TraceTestGroup)
				{
					TraceTestGroup node = (TraceTestGroup) expandingElement;
					try
					{
						node.LoadTests();
					} catch (Exception e)
					{
						// TODO Auto-generated catch block
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
				viewer.refresh();
			}

		});
	}

	private void showMessage(String message)
	{
		MessageDialog.openInformation(
				viewer.getControl().getShell(),
				"Combinatorial Testing Overview",
				message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus()
	{
		viewer.getControl().setFocus();
	}

	// private static final String MARKER_TYPE = "org.overturetool.traces";

	private void addMarker(IFile file, String message, int lineNumber,
			int severity)
	{
		try
		{
			if (file == null)
				return;
			lineNumber -= 1;
			IMarker[] markers = file.findMarkers(
					IMarker.PROBLEM,
					false,
					IResource.DEPTH_INFINITE);
			boolean markerExist = false;
			for (IMarker marker : markers)
			{
				if (marker.getAttribute(IMarker.MESSAGE).equals(message)
						&& marker.getAttribute(IMarker.SEVERITY).equals(
								severity)
						&& marker.getAttribute(IMarker.LINE_NUMBER).equals(
								lineNumber))
					return;

			}
			IMarker marker = file.createMarker(IMarker.PROBLEM);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			marker.setAttribute(IMarker.SOURCE_ID, "org.overturetool.traces");
			if (lineNumber == -1)
			{
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e)
		{
			int jj = 8;
		}
	}

	private void gotoLine(IFile file, int lineNumber, String message)
	{
		try
		{
			IWorkspaceRoot iworkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
			// IProject[] iprojects = iworkspaceRoot.getProjects();

			IWorkbench wb = PlatformUI.getWorkbench();
			IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
			// String id =
			// win.getWorkbench().getEditorRegistry().getEditors(file.getName())[0].getId();
			IEditorPart editor = IDE.openEditor(win.getActivePage(), file, true);

			file.deleteMarkers(IMarker.MARKER, false, IResource.DEPTH_INFINITE);

			IMarker marker = file.createMarker(IMarker.MARKER);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
			if (lineNumber == -1)
			{
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);

			IDE.gotoMarker(editor, marker);

		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ViewerFilter okFilter = new ViewerFilter()
	{

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element)
		{
			if (element instanceof TraceTestTreeNode
					&& ((TraceTestTreeNode) element).GetStatus() == TestResultType.Ok)
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
					&& ((TraceTestTreeNode) element).GetStatus() == TestResultType.Inconclusive)
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
				TestResultType res = ((TraceTestTreeNode) element).GetStatus();
				if (res == TestResultType.Fail)
					return 1;
				else if (res == TestResultType.Inconclusive)
					return 2;
				else if (res == TestResultType.Ok)
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
				MessageConsole myConsole = findConsole("TracesConsole");
				MessageConsoleStream out = myConsole.newMessageStream();
				out.println(message);
			}
		});

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

}
