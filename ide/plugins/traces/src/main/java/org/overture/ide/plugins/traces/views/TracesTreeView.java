package org.overture.ide.plugins.traces.views;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CancellationException;

import org.eclipse.core.resources.IFile;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
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
import org.overture.ide.plugins.traces.OvertureTracesPlugin;
import org.overture.ide.plugins.traces.TracesConstants;
import org.overture.ide.plugins.traces.internal.VdmjTracesHelper;
import org.overture.ide.plugins.traces.views.treeView.ClassTreeNode;
import org.overture.ide.plugins.traces.views.treeView.ITreeNode;
import org.overture.ide.plugins.traces.views.treeView.NotYetReadyTreeNode;
import org.overture.ide.plugins.traces.views.treeView.ProjectTreeNode;
import org.overture.ide.plugins.traces.views.treeView.TraceTestGroup;
import org.overture.ide.plugins.traces.views.treeView.TraceTestTreeNode;
import org.overture.ide.plugins.traces.views.treeView.TraceTreeNode;
import org.overture.ide.utility.ProjectUtility;
import org.overture.ide.vdmpp.core.VdmPpProjectNature;
import org.overturetool.traces.utility.ITracesHelper;
import org.overturetool.traces.utility.TraceHelperNotInitializedException;
import org.overturetool.vdmj.definitions.NamedTraceDefinition;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.traces.Verdict;
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

public class TracesTreeView extends ViewPart {
	private TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action actionRunSelected;
	private Action actionRunAll;
	private Action actionSetOkFilter;
	private Action actionSetSort;
	private Action actionSetInconclusiveFilter;
	private Action actionSendToInterpreter;
	private Action actionSelectToolBox;
	private Action actionSelectToolBoxVDMJ;
	private Action actionSelectToolBoxVDMTools;
	private Action expandSpecInTree;
	private Action saveTraceResultsAction;
	private Dictionary<String, ITracesHelper> traceHelpers;
	final Display display = Display.getCurrent();

	private IResourceChangeListener resourceChangedListener = null;
	private IProject projectToUpdate = null;
	private String VDMToolsPath = "";
	final String VDM_TOOLS_PATH_DEFAULT = "C:\\Program Files\\The VDM++ Toolbox v8.2b\\bin";
	Button buttonSetSort = null;

	public ITracesHelper GetTracesHelper(String projectName) {
		return this.traceHelpers.get(projectName);

	}

	private void SetTraceHelper(final IProject project) {

		// Job initCtJob = new Job("CT init")
		// {
		//
		// @Override
		// protected IStatus run(IProgressMonitor monitor)
		// {

		try {

			if (project.isOpen()
					&& project.hasNature(VdmPpProjectNature.VDM_PP_NATURE)) {
				if (traceHelpers.get(project.getName()) != null)
					traceHelpers.remove(project.getName());

				ITracesHelper tmpHelper = new VdmjTracesHelper(project, 3);

				if (tmpHelper.GetClassNamesWithTraces().size() > 0)

					traceHelpers.put(project.getName(), tmpHelper);
			}
		} catch (Exception e1) {
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

	private void SetTraceHelpers() {
		IWorkspaceRoot iworkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] iprojects = iworkspaceRoot.getProjects();
		this.traceHelpers = new Hashtable<String, ITracesHelper>();
		new Hashtable<String, IFile>();

		for (int j = 0; j < iprojects.length; j++) {
			try {
				if (iprojects[j].isAccessible()
						&& iprojects[j].isOpen()
						&& iprojects[j].hasNature(VdmPpProjectNature.VDM_PP_NATURE))
					SetTraceHelper(iprojects[j]);
			} catch (Exception e1) {
				System.out.println("Exception: " + e1.getMessage());
				e1.printStackTrace();
			}
		}
	}

	/**
	 * The constructor.
	 */
	public TracesTreeView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent) {

		// PatternFilter patternFilter = new PatternFilter();
		// viewer = new FilteredTree(parent, SWT.MULTI
		// | SWT.H_SCROLL | SWT.V_SCROLL, patternFilter).getViewer();

		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);
		SetTraceHelpers();
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

		ExpandTraces(1000);

		resourceChangedListener = new IResourceChangeListener() {
			public void resourceChanged(IResourceChangeEvent event) {
				try {
					switch (event.getType()) {
					case IResourceChangeEvent.POST_CHANGE:

						IResourceDelta[] delta = event.getDelta().getAffectedChildren();

						for (IResourceDelta resourceDelta : delta) {

							if (resourceDelta.getResource() instanceof IProject
									&& ((IProject) resourceDelta.getResource()).isAccessible() && ((IProject) resourceDelta.getResource()).isOpen() && ((IProject) resourceDelta.getResource()).hasNature(VdmPpProjectNature.VDM_PP_NATURE)) {

								if (IsFileChange(resourceDelta)
										|| (resourceDelta.getKind() & IResourceDelta.ADDED) == IResourceDelta.ADDED) {
									projectToUpdate = ((IProject) resourceDelta.getResource());
									ExpandTraces(0);
								}
							}
						}
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		};
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				resourceChangedListener, IResourceChangeEvent.POST_CHANGE);

	}

	private void ExpandTraces(int delay) {
		final Job expandJob = new Job("Expand traces") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {

				// expandCompleted = false;
				if (projectToUpdate != null) {
					monitor.worked(IProgressMonitor.UNKNOWN);
					SetTraceHelper(projectToUpdate);

					// final IProject[] iprojects =
					// iworkspaceRoot.getProjects();

					display.asyncExec(new Runnable() {

						public void run() {
							UpdateProject(projectToUpdate);
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

	private boolean IsFileChange(IResourceDelta delta) {
		boolean ret = false;
		if (delta.getAffectedChildren().length == 0) {

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
		} else {
			for (IResourceDelta d : delta.getAffectedChildren()) {
				ret = ret || IsFileChange(d);
			}
		}
		return ret;
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				TracesTreeView.this.fillContextMenu(manager);
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

	private void fillContextMenu(IMenuManager manager) {

		// manager.add(actionRunAll);

		ISelection selection = viewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();

		if (obj instanceof ProjectTreeNode || obj instanceof ClassTreeNode) // ||
			// obj
			// instanceof
			// TraceTreeNode
			manager.add(actionRunSelected);

		if (obj instanceof TraceTestTreeNode)
			if (((TraceTestTreeNode) obj).GetStatus() != null) {
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

	private void fillLocalToolBar(IToolBarManager manager) {

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

	private void makeActions() {
		actionRunSelected = new Action() {

			@Override
			public void run() {

				ISelection selection = viewer.getSelection();
				final Object obj = ((IStructuredSelection) selection).getFirstElement();

				String projectName = "";
				Dictionary<String, List<String>> classTracesTestCase = new Hashtable<String, List<String>>();
				if (obj instanceof ClassTreeNode) {
					ClassTreeNode cn = (ClassTreeNode) obj;

					projectName = cn.getParent().getName();

					List<String> tmpTraces = new Vector<String>();

					classTracesTestCase.put(cn.getName(), tmpTraces);

				} else if (obj instanceof ProjectTreeNode) {
					ProjectTreeNode pn = (ProjectTreeNode) obj;
					projectName = pn.getName();

				}

				final Dictionary<String, List<String>> finalClassTracesTestCase = classTracesTestCase;
				final String finalProjectName = projectName;

				Job executeTestJob = new Job("CT evaluating selected tests") {

					@Override
					protected IStatus run(IProgressMonitor monitor) {
						try {
							ITracesHelper th = traceHelpers.get(finalProjectName);
							 
														projectToUpdate= getProject(finalProjectName);
							
							if (finalClassTracesTestCase.size() == 0)
								runTestProject(th, monitor);
							else {

								Enumeration<String> classKeys = finalClassTracesTestCase.keys();
								while (classKeys.hasMoreElements()) {
									String className = classKeys.nextElement();
									try {
										th.processClassTraces(className,
												monitor);
									} catch (CancellationException e) {
										ConsolePrint(e.getMessage());
									} catch (ContextException e) {
										ConsolePrint(e.getMessage());

										IWorkspaceRoot iworkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
										IProject[] iprojects = iworkspaceRoot.getProjects();
										for (IProject project2 : iprojects) {
											if (project2.getName().equals(
													finalProjectName)) {
												addMarker(
														ProjectUtility.findIFile(
																project2,
																e.location.file),
														e.getMessage(),
														e.location.startLine,
														IMarker.SEVERITY_ERROR);
												ConsolePrint(e.getMessage());

												break;
											}
										}
									} catch (Exception e) {

										e.printStackTrace();

										IWorkspaceRoot iworkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
										IProject[] iprojects = iworkspaceRoot.getProjects();
										for (IProject project2 : iprojects) {
											if (project2.getName().equals(
													finalProjectName)) {
												addMarker(
														ProjectUtility.findIFile(
																project2,
																th.GetFile(className)),
														e.getMessage(), 1,
														IMarker.SEVERITY_ERROR);
												ConsolePrint(e.getMessage());

												break;
											}
										}

									}

								}

							}

						} catch (Exception e) {
							e.printStackTrace();
						}

						ExpandTraces(0);
						return new Status(IStatus.OK, "org.overturetool.traces", IStatus.OK, "CT Test evaluation finished", null);
					}

					

				};

				executeTestJob.schedule();

			}
		};

		actionRunSelected.setText("Run selected");
		actionRunSelected.setToolTipText("Run selected");
		actionRunSelected.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_RUN_SELECTED_TRACE));
		// actionRunSelected.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		actionRunAll = new Action() {
			@Override
			public void run() {

				Job runAllTestsJob = new Job("CT evaluation all projects") {

					@Override
					protected IStatus run(IProgressMonitor monitor) {
						IWorkspaceRoot iworkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
						IProject[] iprojects = iworkspaceRoot.getProjects();
						int totalCount = 0;
						for (final IProject project : iprojects) {
							try {
								if (project.isOpen()
										&& project.getNature(VdmPpProjectNature.VDM_PP_NATURE) != null) {
									ITracesHelper th = traceHelpers.get(project.getName());
									if (th != null)
										for (String className : th.GetClassNamesWithTraces()) {
											totalCount += th.GetTraceDefinitions(
													className).size();

										}
								}
							} catch (CoreException e) {

								e.printStackTrace();
							} catch (Exception e) {

								e.printStackTrace();
							}

						}

						for (final IProject project : iprojects) {
							if (monitor.isCanceled())
								break;
							try {
								if (project.isOpen()
										&& project.getNature(VdmPpProjectNature.VDM_PP_NATURE) != null) {
									ITracesHelper th = traceHelpers.get(project.getName());
									for (String className : th.GetClassNamesWithTraces()) {
										if (monitor.isCanceled())
											break;
										th.processClassTraces(className,
												monitor);
									}
								}
							} catch (CancellationException e) {
								ConsolePrint(e.getMessage());
							} catch (Exception e) {

								e.printStackTrace();

							}

						}
						display.asyncExec(new Runnable() {

							public void run() {

								UpdateTraceTestCasesNodeStatus();
								saveTraceResultsAction.setEnabled(true);
							}

						});
						refreshTree();
						return new Status(IStatus.OK, "org.overturetool.traces", IStatus.OK, "CT Test evaluation finished", null);
					}

				};

				runAllTestsJob.schedule();
			}
		};
		actionRunAll.setText("Run all");
		actionRunAll.setToolTipText("Run all");
		actionRunAll.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_RUN_ALL_TRACES));

		expandSpecInTree = new Action() {
			@Override
			public void run() {
				IWorkspaceRoot iworkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
				final IProject[] iprojects = iworkspaceRoot.getProjects();

				display.asyncExec(new Runnable() {

					public void run() {
						for (IProject project : iprojects) {
							UpdateProject(project);
						}
						viewer.refresh();
					}

				});

			}

		};

		actionSetOkFilter = new Action() {
			@Override
			public void run() {
				ViewerFilter[] filters = viewer.getFilters();
				boolean isSet = false;
				for (ViewerFilter viewerFilter : filters) {
					if (viewerFilter.equals(okFilter))
						isSet = true;
				}
				if (isSet) {
					viewer.removeFilter(okFilter);
					actionSetOkFilter.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_CASE_FILTER_SUCCES));
				} else {
					viewer.addFilter(okFilter);
					actionSetOkFilter.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_CASE_FILTER_SUCCES_PRESSED));
				}

			}

		};
		actionSetOkFilter.setText("Filter ok results");
		actionSetOkFilter.setToolTipText("Filter all ok results from tree");
		actionSetOkFilter.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_CASE_FILTER_SUCCES));

		actionSetInconclusiveFilter = new Action() {
			@Override
			public void run() {
				ViewerFilter[] filters = viewer.getFilters();
				boolean isSet = false;
				for (ViewerFilter viewerFilter : filters) {
					if (viewerFilter.equals(inconclusiveFilter))
						isSet = true;
				}
				if (isSet) {
					viewer.removeFilter(inconclusiveFilter);
					actionSetInconclusiveFilter.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_CASE_FILTER_UNDETERMINED));
				} else {
					viewer.addFilter(inconclusiveFilter);
					actionSetInconclusiveFilter.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_CASE_FILTER_UNDETERMINED_PRESSED));
				}

			}

		};
		actionSetInconclusiveFilter.setText("Filter inconclusive results");
		actionSetInconclusiveFilter.setToolTipText("Filter all inconclusive results from tree");
		actionSetInconclusiveFilter.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_CASE_FILTER_UNDETERMINED));

		actionSetSort = new Action() {
			@Override
			public void run() {
				if (viewer.getSorter() != null) {
					viewer.setSorter(null);
					actionSetSort.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_SORT));
				} else {
					viewer.setSorter(traceSorter);
					actionSetSort.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_SORT_PRESSED));
				}

			}

		};
		actionSetSort.setText("Sort");
		actionSetSort.setToolTipText("Sort by verdict: Fail, Inconclusive, ok, etc.");
		actionSetSort.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_SORT));

		actionSelectToolBoxVDMJ = new Action("Use VDMJ") {
			@Override
			public void run() {
				actionSelectToolBoxVDMJ.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_VDMJ_LOGO_PRESSED));
				actionSelectToolBoxVDMTools.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_VDM_TOOLS_LOGO));
				SetTraceHelpers();
				UpdateTraceTestCasesNodeStatus();
				viewer.refresh();
			}
		};
		actionSelectToolBoxVDMJ.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_VDMJ_LOGO_PRESSED));

		actionSelectToolBoxVDMTools = new Action("Use VDM Tools") {
			@Override
			public void run() {
				if (VDMToolsPath.length() == 0) {
					display.asyncExec(new Runnable() {

						public void run() {
							Shell s = new Shell(display);

							FileDialog fd = new FileDialog(s, SWT.OPEN
									| SWT.SIMPLE);
							fd.setText("Select VDM Tools (e.g. vppgde.exe)");

							if (IsWindows()) {
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
				} else {
					SetTraceHelpers();
					UpdateTraceTestCasesNodeStatus();
					viewer.refresh();
				}

				actionSelectToolBoxVDMTools.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_VDM_TOOLS_LOGO_PRESSED));
				actionSelectToolBoxVDMJ.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_VDMJ_LOGO));
			}
		};
		actionSelectToolBoxVDMTools.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_VDM_TOOLS_LOGO));

		actionSelectToolBox = new Action("Select toolbox") {

		};
		actionSelectToolBox.setText("Select toolbox");

		actionSendToInterpreter = new Action() {
		};
		actionSendToInterpreter.setText("Send to Interpreter");
		actionSendToInterpreter.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_INTERPRETER));
		actionSendToInterpreter.setEnabled(false);

	}

	// -----------------------------update
	private void UpdateTraceTestCasesNodeStatus() {
		TreeItem[] aa = viewer.getTree().getItems();
		for (TreeItem treeItem : aa) {
			if (treeItem.getData() instanceof ProjectTreeNode) {
				ProjectTreeNode projectNode = ((ProjectTreeNode) treeItem.getData());
				ITracesHelper th = traceHelpers.get(projectNode.getName());
				for (ITreeNode classNode : projectNode.getChildren()) {
					for (ITreeNode traceNode : classNode.getChildren()) {
						UpdateTraceTestCasesNodeStatus(th,
								(TraceTreeNode) traceNode);

					}

				}

			}
		}
		viewer.refresh();
	}

	private void UpdateTraceTestCasesNodeStatus(ITracesHelper th,
			TraceTreeNode traceNode) {
		try {
			traceNode.SetSkippedCount(th.GetSkippedCount(
					traceNode.getParent().getName(), traceNode.getName()));
		} catch (SAXException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			ConsolePrint(e.toString());
		}

	}

	private void UpdateProject(IProject project) {
		TreeItem[] aa = viewer.getTree().getItems();
		boolean insertProject = true;
		for (TreeItem treeItem : aa) {
			if (treeItem.getData() instanceof ProjectTreeNode
					&& ((ProjectTreeNode) treeItem.getData()).getName().equals(
							project.getName())) {
				insertProject = false;
				ProjectTreeNode projectNode = ((ProjectTreeNode) treeItem.getData());
				String projectName = projectNode.getName();
				ITracesHelper th = traceHelpers.get(projectName);

				projectNode.getChildren().clear();

				// now no nodes are present
				try {
					for (String className : th.GetClassNamesWithTraces()) {
						ClassTreeNode classNode = new ClassTreeNode(className);
						for (NamedTraceDefinition traceName : th.GetTraceDefinitions(className)) {

							TraceTreeNode traceNode = new TraceTreeNode(traceName, th);

							Integer totalTests = th.GetTraceTestCount(
									className, traceName.name.name);
							traceNode.setTestTotal(totalTests);

							traceNode.SetSkippedCount(th.GetSkippedCount(
									className, traceName.name.name));

							if (totalTests > 0)
								traceNode.addChild(new NotYetReadyTreeNode());

							classNode.addChild(traceNode);

						}
						projectNode.addChild(classNode);

					}
					viewer.refresh(projectNode);
				} catch (Exception e) {

					e.printStackTrace();
				}
				viewer.refresh(projectNode);
			}
		}
		if (insertProject && traceHelpers.get(project.getName()) != null) {
			((ViewContentProvider) viewer.getContentProvider()).addChild(new ProjectTreeNode(project));
			viewer.refresh();
			UpdateProject(project);
		}
	}

	// --------------
	// ---------------- Get results

	private void runTestProject(ITracesHelper th, IProgressMonitor monitor)
			throws IOException {

		try {
			for (String className : th.GetClassNamesWithTraces()) {
				if (monitor != null && monitor.isCanceled())
					return;

				try {
					th.processClassTraces(className, monitor);
				} catch (Exception e) {
					ConsolePrint(e.getMessage());
				}

			}
		} catch (TraceHelperNotInitializedException e) {
			ConsolePrint("Trace helper not initialized for project: "
					+ e.getProjectName());
			e.printStackTrace();
		}

	}

	private void hookDoubleClickAction() {
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

	private void hookTreeAction() {
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {

				Object selection = ((ITreeSelection) event.getSelection()).getFirstElement();
				if (selection instanceof TraceTreeNode) {
					TraceTreeNode tn = (TraceTreeNode) selection;

					IWorkspaceRoot iworkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
					String projectName = tn.getParent().getParent().getName();
					IProject iproject = iworkspaceRoot.getProject(projectName);

					ITracesHelper helper = traceHelpers.get(projectName);

					try {
						gotoLine(ProjectUtility.findIFile(iproject,
								helper.GetFile(tn.getParent().getName())),
								tn.GetTraceDefinition().location.startLine,
								tn.getName());
					} catch (IOException e) {
						ConsolePrint("File not found: " + e.getMessage());
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						ConsolePrint(e.toString());
						e.printStackTrace();
					} catch (TraceHelperNotInitializedException e) {
						ConsolePrint("Trace helper not initialized for project: "
								+ e.getProjectName());
						e.printStackTrace();
					}

				} else if (selection instanceof TraceTestTreeNode) {
					if (!(selection instanceof NotYetReadyTreeNode)
							&& !(selection instanceof TraceTestGroup)
							&& ((TraceTestTreeNode) selection).GetStatus() == null)
						try {
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
									IPageLayout.ID_PROGRESS_VIEW);
						} catch (PartInitException e) {

							e.printStackTrace();
						}
					else
						try {
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
								TracesConstants.TRACES_TEST_ID);
						} catch (PartInitException e) {

							e.printStackTrace();
						}

				}
			}

		});
		viewer.addTreeListener(new ITreeViewerListener() {

			public void treeCollapsed(TreeExpansionEvent event) {
				Object expandingElement = event.getElement();
				if (expandingElement instanceof TraceTreeNode) {
					TraceTreeNode node = (TraceTreeNode) expandingElement;
					node.UnloadTests();

					refreshTree();
				} else if (expandingElement instanceof TraceTestGroup) {
					TraceTestGroup node = (TraceTestGroup) expandingElement;
					node.UnloadTests();
					// viewer.remove(node);
					// viewer.getTree().clearAll(true);
					// viewer.refresh(node);
					refreshTree();
				}

			}

			public void treeExpanded(TreeExpansionEvent event) {
				Object expandingElement = event.getElement();
				if (expandingElement instanceof TraceTreeNode) {
					TraceTreeNode node = (TraceTreeNode) expandingElement;
					try {
						node.LoadTests();
					} catch (Exception e) {

						e.printStackTrace();
					}
					refreshTree();
				} else if (expandingElement instanceof TraceTestGroup) {
					TraceTestGroup node = (TraceTestGroup) expandingElement;
					try {
						node.LoadTests();

					} catch (Exception e) {

						e.printStackTrace();
					}
					refreshTree();
				}
			}
		});

	}

	private void refreshTree() {
		display.asyncExec(new Runnable() {

			public void run() {

				viewer.refresh();
				viewer.getControl().update();
			}

		});
	}

	// private void showMessage(String message)
	// {
	// MessageDialog.openInformation(
	// viewer.getControl().getShell(),
	// "Combinatorial Testing Overview",
	// message);
	// }

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	// private static final String MARKER_TYPE = "org.overturetool.traces";

	private void addMarker(IFile file, String message, int lineNumber,
			int severity) {
		try {
			if (file == null)
				return;
			lineNumber -= 1;
			IMarker[] markers = file.findMarkers(IMarker.PROBLEM, false,
					IResource.DEPTH_INFINITE);
			for (IMarker marker : markers) {
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
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private void gotoLine(IFile file, int lineNumber, String message) {
		try {
			// IWorkspaceRoot iworkspaceRoot =
			// ResourcesPlugin.getWorkspace().getRoot();
			// IProject[] iprojects = iworkspaceRoot.getProjects();

			IWorkbench wb = PlatformUI.getWorkbench();
			IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
			// String id =
			// win.getWorkbench().getEditorRegistry().getEditors(file.getName())[0].getId();
			IEditorPart editor = IDE.openEditor(win.getActivePage(), file, true);

			IMarker marker = file.createMarker(IMarker.MARKER);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);

			IDE.gotoMarker(editor, marker);

			marker.delete();

		} catch (CoreException e) {

			e.printStackTrace();
		}
	}

	private ViewerFilter okFilter = new ViewerFilter() {

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			if (element instanceof TraceTestTreeNode
					&& ((TraceTestTreeNode) element).GetStatus() == Verdict.PASSED)
				return false;
			else
				return true;
		}

	};

	private ViewerFilter inconclusiveFilter = new ViewerFilter() {

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			if (element instanceof TraceTestTreeNode
					&& ((TraceTestTreeNode) element).GetStatus() == Verdict.INCONCLUSIVE)
				return false;
			else
				return true;
		}

	};

	private ViewerSorter traceSorter = new ViewerSorter() {
		@Override
		public int category(Object element) {
			if (element instanceof TraceTestTreeNode) {
				Verdict res = ((TraceTestTreeNode) element).GetStatus();
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

	public static Boolean IsWindows() {
		String osName = System.getProperty("os.name");

		return osName.toUpperCase().indexOf("WINDOWS".toUpperCase()) > -1;
	}

	private void ConsolePrint(final String message) {
		display.asyncExec(new Runnable() {

			public void run() {
				try {
					MessageConsole myConsole = findConsole("TracesConsole");
					MessageConsoleStream out = myConsole.newMessageStream();
					out.println(message);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	private MessageConsole findConsole(String name) {
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
	
	private IProject getProject(String finalProjectName) throws CoreException {
		IWorkspaceRoot iworkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] iprojects = iworkspaceRoot.getProjects();
		
		for (final IProject project : iprojects) {
			
				if (project.isOpen()
						&& project.getNature(VdmPpProjectNature.VDM_PP_NATURE) != null && project.getName().equals(finalProjectName))
					return project;
		}
				return null;
	}

}