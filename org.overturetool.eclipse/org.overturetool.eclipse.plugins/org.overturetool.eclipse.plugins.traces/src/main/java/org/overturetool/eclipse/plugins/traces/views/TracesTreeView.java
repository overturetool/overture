package org.overturetool.eclipse.plugins.traces.views;

import java.io.File;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

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
import org.eclipse.core.runtime.IAdaptable;
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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
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
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.overturetool.ast.itf.IOmlNamedTrace;
import org.overturetool.eclipse.plugins.editor.core.OvertureNature;
import org.overturetool.eclipse.plugins.traces.OvertureTracesPlugin;
import org.overturetool.traces.utility.TracesHelper;
import org.overturetool.traces.utility.TracesHelper.TestResultType;
import org.overturetool.traces.utility.TracesHelper.TraceError;


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

public class TracesTreeView extends ViewPart {
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
	// private Action doubleClickAction;
	private Action treeAction;
	private Action expandSpecInTree;
	private Action saveTraceResultsAction;
	private Dictionary<String, TracesHelper> traceHelpers;
	private Dictionary<String, IFile> pathTofile;
	private String savePath = "";
	// private boolean expandCompleted = false;
	// private static ILock lock = Platform.getJobManager().newLock();
	final Display display = Display.getCurrent();
	static String[] exts = new String[] { "vpp", "tex", "vdm" }; // TODO get
	private IResourceChangeListener resourceChangedListener = null;
	private IProject projectToUpdate = null;

	public TracesHelper GetTracesHelper(String projectName) {
		return this.traceHelpers.get(projectName);
		// ScriptRuntime.getInterpreterInstall(proj);

	}

	class ProjectTreeNode implements IAdaptable {
		private ArrayList<ClassTreeNode> children;
		private TreeParent parent;
		private IProject project;

		public ProjectTreeNode(IProject project) {
			this.project = project;
			children = new ArrayList<ClassTreeNode>();
		}

		public void setParent(TreeParent parent) {
			this.parent = parent;
		}

		public TreeParent getParent() {
			return parent;
		}

		public String toString() {
			return getName();
		}

		public String getName() {
			return project.getName();
		}

		public Object getAdapter(Class adapter) {
			return null;
		}

		public void addChild(ClassTreeNode child) {
			children.add(child);
			child.setParent(this);
		}

		public void removeChild(ClassTreeNode child) {
			children.remove(child);
			child.setParent(null);
		}

		public ClassTreeNode[] getChildren() {
			return children.toArray(new ClassTreeNode[children.size()]);
		}

		public boolean hasChildren() {
			return children.size() > 0;
		}

	}

	class ClassTreeNode implements IAdaptable {
		private ProjectTreeNode parent;
		private String className;
		private ArrayList<TraceTreeNode> children;

		public ClassTreeNode(String className) {
			this.className = className;
			children = new ArrayList<TraceTreeNode>();
		}

		public void setParent(ProjectTreeNode parent) {
			this.parent = parent;
		}

		public ProjectTreeNode getParent() {
			return parent;
		}

		public String toString() {
			return getName();
		}

		public String getName() {
			return className;
		}

		public Object getAdapter(Class adapter) {
			return null;
		}

		public void addChild(TraceTreeNode child) {
			children.add(child);
			child.setParent(this);
		}

		public void removeChild(TraceTreeNode child) {
			children.remove(child);
			child.setParent(null);
		}

		public TraceTreeNode[] getChildren() {
			return children.toArray(new TraceTreeNode[children.size()]);
		}

		public boolean hasChildren() {
			return children.size() > 0;
		}
	}

	class TraceTreeNode implements IAdaptable {
		private IOmlNamedTrace traceDefinition;
		private ClassTreeNode parent;
		private ArrayList<TraceTestCaseTreeNode> children;
		private int testSkippedCount = 0;

		public TraceTreeNode(IOmlNamedTrace traceDef) {
			traceDefinition = traceDef;
			children = new ArrayList<TraceTestCaseTreeNode>();
		}

		public ClassTreeNode getParent() {
			return parent;
		}

		public IOmlNamedTrace GetTraceDefinition() {
			return traceDefinition;
		}

		public void SetSkippedCount(int skippedCount) {
			testSkippedCount = skippedCount;
		}

		public String toString() {
			if (testSkippedCount != 0)
				return getName() + " (" + this.getChildren().length
						+ " skipped " + testSkippedCount + ")";
			else
				return getName() + " (" + this.getChildren().length + ")";
		}

		public String getName() {
			try {
				return org.overturetool.vdmj.util.Utils.listToString(traceDefinition.getName(), "/");
			} catch (CGException e) {
				return "error.. ";
			}
		}

		public void setParent(ClassTreeNode parent) {
			this.parent = parent;
		}

		@SuppressWarnings("unchecked")
		public Object getAdapter(Class adapter) {
			return null;
		}

		public void addChild(TraceTestCaseTreeNode child) {
			if (!children.contains(child)) {
				boolean contains = false;
				for (TraceTestCaseTreeNode node : getChildren()) {
					if (node.getName().equals(child.getName()))
						contains = true;
				}
				if (!contains) {
					children.add(child);
					child.setParent(this);
				}
			}
		}

		public void removeChild(TraceTestCaseTreeNode child) {
			children.remove(child);
			child.setParent(null);
		}

		public TraceTestCaseTreeNode[] getChildren() {
			return children.toArray(new TraceTestCaseTreeNode[children.size()]);
		}

		public boolean hasChildren() {
			return children.size() > 0;
		}

		public boolean hasChild(String name) {
			for (TraceTestCaseTreeNode node : children) {
				if (node.getName().equals(name))
					return true;

			}
			return false;
		}

	}

	class TraceTestCaseTreeNode implements IAdaptable {
		private String name;
		private TraceTreeNode parent;
		private TestResultType status;
		private boolean runTimeError = false;

		public TraceTestCaseTreeNode(String traceTestCaseName, TestResultType status) {
			this.name = traceTestCaseName;
			this.status = status;
		}

		public boolean HasRunTimeError() {
			return runTimeError;
		}

		public void SetRunTimeError() {
			this.runTimeError = true;
		}

		public void SetStatus(TestResultType status) {
			this.runTimeError = false;
			this.status = status;
		}

		public TestResultType GetStatus() {
			return this.status;
		}

		public TraceTreeNode getParent() {
			return parent;
		}

		public String toString() {
			String tmp = name;
			while (tmp.length() < 6)
				tmp = "0" + tmp;
			return "Test " + tmp;
		}

		public String getName() {
			return name;
		}

		public void setParent(TraceTreeNode parent) {
			this.parent = parent;
		}

		@SuppressWarnings("unchecked")
		public Object getAdapter(Class adapter) {
			return null;
		}

		// public boolean equals(Object obj)
		// {
		// if(obj instanceof TraceTestCaseTreeNode)
		// return this.name.equals(((TraceTestCaseTreeNode)obj).getName());
		// else
		// return false;
		// }

	}

	class TreeParent implements IAdaptable {
		private String name;
		private ArrayList<ProjectTreeNode> children;

		public TreeParent(String name) {
			this.name = name;
			children = new ArrayList<ProjectTreeNode>();
		}

		public String toString() {
			return getName();
		}

		public String getName() {
			return name;
		}

		public Object getAdapter(Class adapter) {
			return null;
		}

		public void addChild(ProjectTreeNode child) {
			children.add(child);
			child.setParent(this);
		}

		public void removeChild(ProjectTreeNode child) {
			children.remove(child);
			child.setParent(null);
		}

		public ProjectTreeNode[] getChildren() {
			return children.toArray(new ProjectTreeNode[children.size()]);
		}

		public boolean hasChildren() {
			return children.size() > 0;
		}
	}

	class ViewContentProvider implements IStructuredContentProvider,
			ITreeContentProvider {
		private TreeParent invisibleRoot;
		// private ArrayList<ProjectTreeNode> projectTraceTreeNodes;
		Dictionary<String, TracesHelper> traceHelpers;

		public ViewContentProvider(Dictionary<String, TracesHelper> trs) {
			this.traceHelpers = trs;
		}

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public void addChild(ProjectTreeNode project) {
			invisibleRoot.addChild(project);
		}

		public Object[] getElements(Object parent) {
			if (parent.equals(getViewSite())) {
				if (invisibleRoot == null) {
					initialize();
				}
				return getChildren(invisibleRoot);
			}
			return getChildren(parent);
		}

		public Object getParent(Object child) {
			if (child instanceof ProjectTreeNode) {
				return ((ProjectTreeNode) child).getParent();
			}
			if (child instanceof ClassTreeNode) {
				return ((ClassTreeNode) child).getParent();
			}
			if (child instanceof TraceTreeNode) {
				return ((TraceTreeNode) child).getParent();
			}
			if (child instanceof TraceTestCaseTreeNode) {
				return ((TraceTestCaseTreeNode) child).getParent();
			}
			return null;
		}

		public Object[] getChildren(Object parent) {
			if (parent instanceof TreeParent) {
				return ((TreeParent) parent).getChildren();
			}
			if (parent instanceof ProjectTreeNode) {
				return ((ProjectTreeNode) parent).getChildren();
			}
			if (parent instanceof ClassTreeNode) {
				return ((ClassTreeNode) parent).getChildren();
			}
			if (parent instanceof TraceTreeNode) {
				return ((TraceTreeNode) parent).getChildren();
			}
			return new Object[0];
		}

		public boolean hasChildren(Object parent) {
			if (parent instanceof TreeParent) {
				return ((TreeParent) parent).hasChildren();
			}
			if (parent instanceof ProjectTreeNode) {
				return ((ProjectTreeNode) parent).hasChildren();
			}
			if (parent instanceof ClassTreeNode) {
				return ((ClassTreeNode) parent).hasChildren();
			}
			if (parent instanceof TraceTreeNode) {
				return ((TraceTreeNode) parent).hasChildren();
			}
			return false;
		}

		/*
		 * We will set up a dummy model to initialize tree heararchy. In a real
		 * code, you will connect to a real model and expose its hierarchy.
		 */
		private void initialize() {
			invisibleRoot = new TreeParent("");
			// projectTraceTreeNodes = new ArrayList<ProjectTreeNode>();
			// String[] exts = new String[]{"vpp", "tex" , "vdm"}; // TODO get
			// extension from core xml..
			IWorkspaceRoot iworkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
			IProject[] iprojects = iworkspaceRoot.getProjects();
			// ArrayList<String> fileNameList = new ArrayList<String>();

			ProjectTreeNode projectTreeNode;

			// ArrayList<TreeParent> projectTree = new ArrayList<TreeParent>();
			for (int j = 0; j < iprojects.length; j++) {

				try {
					// if the project is a overture project
					if (iprojects[j].isOpen()
							&& iprojects[j].getNature(OvertureNature.NATURE_ID) != null) {
						// fileNameList = getAllMemberFilesString(iproject,
						// exts);

						// create project node
						projectTreeNode = new ProjectTreeNode(iprojects[j]);
						// creates File array
						// File[] fileArray = new File[fileNameList.size()];
						// for (int i = 0; i < fileNameList.size(); i++) {
						// fileArray[i] = new File(fileNameList.get(i));
						// }
						// children.toArray(new
						// ProjectTreeNode[children.size()]);
						// traceHelper = new
						// TracesHelper("",fileArray,true,100);
						// add a childnode for each class that have a trace
						TracesHelper tr = (TracesHelper) traceHelpers.get(iprojects[j].getName());
						if (tr == null)
							continue;

						ArrayList<String> classes = tr.GetClassNamesWithTraces();
						boolean isTraceProject = false;
						for (String className : classes) {
							if (className != null) {
								isTraceProject = true;
								// ClassTreeNode classTreeNode = new
								// ClassTreeNode(className);
								//
								// // addTraces
								// for (String trace : tr.GetTraces(className))
								// {
								// IOmlNamedTrace omlTrace =
								// tr.GetTraceDefinition(className, trace); //
								// if
								//
								// TraceTreeNode traceNode = new
								// TraceTreeNode(omlTrace);
								// classTreeNode.addChild(traceNode);
								// }
								// // add to project and root
								// projectTreeNode.addChild(classTreeNode);
							}
						}
						if (isTraceProject && classes.size() > 0) {
							invisibleRoot.addChild(projectTreeNode);
						}
					}
				} catch (Exception e1) {
					System.out.println("Exception: " + e1.getMessage());
					e1.printStackTrace();
				}
			}
		}
	}

	// ----------------------------------------------

	/**
	 * This method returns a list of files under the given directory or its
	 * subdirectories. The directories themselves are not returned.
	 * 
	 * @param dir
	 *            a directory
	 * @return list of IResource objects representing the files under the given
	 *         directory and its subdirectories
	 */
	private ArrayList<IFile> getAllMemberFiles(IContainer dir, String[] exts) {
		ArrayList<IFile> list = new ArrayList<IFile>();
		IResource[] arr = null;
		try {
			arr = dir.members();
		} catch (CoreException e) {
		}

		for (int i = 0; arr != null && i < arr.length; i++) {
			if (arr[i].getType() == IResource.FOLDER) {
				list.addAll(getAllMemberFiles((IFolder) arr[i], exts));
			}
			else {
				for (int j = 0; j < exts.length; j++) {
					if (exts[j].equalsIgnoreCase(arr[i].getFileExtension())) {
						list.add((IFile) arr[i]);
						break;
					}
				}
			}
		}
		return list;
	}

	private void SetTraceHelper(IProject project) {
		ArrayList<IFile> fileNameList = new ArrayList<IFile>();

		try {
			// if the project is a overture project
			if (project.isOpen()
					&& project.getNature(OvertureNature.NATURE_ID) != null) {
				fileNameList = getAllMemberFiles(project, exts);

				// create project node
				// creates File array
				File[] fileArray = new File[fileNameList.size()];
				for (int i = 0; i < fileNameList.size(); i++) {
					fileArray[i] = fileNameList.get(i).getLocation().toFile();

					pathTofile.put(fileArray[i].getAbsolutePath(), fileNameList.get(i));
					//						
					final IFile tmpFile = fileNameList.get(i);
					display.asyncExec(new Runnable() {
						public void run() {
							// TODO Auto-generated method stub
							try {
								tmpFile.deleteMarkers(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE);
							} catch (CoreException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
				}
				traceHelpers.remove(project.getName());
				traceHelpers.put(project.getName(), new TracesHelper(VDMToolsPath, fileArray, UseVDMJ, 3));

			}
		} catch (Exception e1) {
			System.out.println("Exception: " + e1.getMessage());
			e1.printStackTrace();
		}

	}

	private String VDMToolsPath = "";
	final String VDM_TOOLS_PATH_DEFAULT = "C:\\Program Files\\The VDM++ Toolbox v8.2b\\bin";
	private boolean UseVDMJ = true;

	private void SetTraceHelpers() {

		IWorkspaceRoot iworkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] iprojects = iworkspaceRoot.getProjects();
		ArrayList<IFile> fileNameList = new ArrayList<IFile>();

		this.traceHelpers = new Hashtable<String, TracesHelper>();
		this.pathTofile = new Hashtable<String, IFile>();

		for (int j = 0; j < iprojects.length; j++) {

			try {
				// if the project is a overture project
				if (iprojects[j].isOpen()
						&& iprojects[j].getNature(OvertureNature.NATURE_ID) != null) {
					fileNameList = getAllMemberFiles(iprojects[j], exts);

					if (fileNameList.size() == 0)
						continue;
					// create project node
					// creates File array
					File[] fileArray = new File[fileNameList.size()];
					for (int i = 0; i < fileNameList.size(); i++) {
						fileArray[i] = fileNameList.get(i).getLocation().toFile();

						pathTofile.put(fileArray[i].getAbsolutePath(), fileNameList.get(i));
						//						
						final IFile tmpFile = fileNameList.get(i);
						display.asyncExec(new Runnable() {
							public void run() {
								// TODO Auto-generated method stub
								try {
									tmpFile.deleteMarkers(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE);
								} catch (CoreException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
					}
					traceHelpers.put(iprojects[j].getName(), new TracesHelper(VDMToolsPath, fileArray, UseVDMJ, 3));

				}
			} catch (Exception e1) {
				System.out.println("Exception: " + e1.getMessage());
				e1.printStackTrace();
			}
		}
	}

	// ----------------------------------------------

	class ViewLabelProvider extends LabelProvider {

		public String getText(Object obj) {
			return obj.toString();
		}

		public Image getImage(Object obj) {
			if (obj instanceof ProjectTreeNode) {
				String imageKey = IDE.SharedImages.IMG_OBJ_PROJECT;
				return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
			}
			if (obj instanceof ClassTreeNode) {
				return OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_CLASS).createImage();
			}
			if (obj instanceof TraceTreeNode) {
				return OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE).createImage();
			}
			if (obj instanceof TraceTestCaseTreeNode) {
				String imgPath = OvertureTracesPlugin.IMG_TRACE_TEST_CASE_UNKNOWN;
				TestResultType status = (((TraceTestCaseTreeNode) obj).GetStatus());
				if (status == TestResultType.Ok)
					imgPath = OvertureTracesPlugin.IMG_TRACE_TEST_CASE_SUCCES;
				else if (status == TestResultType.Unknown)
					imgPath = OvertureTracesPlugin.IMG_TRACE_TEST_CASE_UNKNOWN;
				else if (status == TestResultType.Inconclusive)
					imgPath = OvertureTracesPlugin.IMG_TRACE_TEST_CASE_UNDETERMINED;
				else if (status == TestResultType.Fail)
					imgPath = OvertureTracesPlugin.IMG_TRACE_TEST_CASE_FAIL;
				else if (status == TestResultType.ExpansionFaild)
					imgPath = OvertureTracesPlugin.IMG_TRACE_TEST_CASE_EXPANSIN_FAIL;
				else if (status == TestResultType.Skipped)
					imgPath = OvertureTracesPlugin.IMG_TRACE_TEST_CASE_SKIPPED;

				if (((TraceTestCaseTreeNode) obj).HasRunTimeError())
					imgPath = OvertureTracesPlugin.IMG_ERROR;

				return OvertureTracesPlugin.getImageDescriptor(imgPath).createImage();
			}
			String imageKey = IDE.SharedImages.IMG_OBJ_PROJECT;
			return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
		}
	}

	class NameSorter extends ViewerSorter {
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
	public void createPartControl(Composite parent) {

		// PatternFilter patternFilter = new PatternFilter();
		// viewer = new FilteredTree(parent, SWT.MULTI
		// | SWT.H_SCROLL | SWT.V_SCROLL, patternFilter).getViewer();

		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);
		SetTraceHelpers();
		viewer.setContentProvider(new ViewContentProvider(this.traceHelpers));
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

		final Job expandJob = new Job("Expand traces") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {

				// expandCompleted = false;
				if (projectToUpdate != null) {

					SetTraceHelper(projectToUpdate);
					monitor.worked(50);
					// final IProject[] iprojects =
					// iworkspaceRoot.getProjects();

					display.asyncExec(new Runnable() {

						public void run() {
							UpdateProject(projectToUpdate);
						}

					});

				}
				else
					expandSpecInTree.run();
				monitor.worked(100);
				// expandCompleted = true;

				return new Status(IStatus.OK, "org.overturetool.traces", IStatus.OK, "Expand completed", null);

			}

		};
		expandJob.setPriority(Job.INTERACTIVE);
		expandJob.schedule(1000);

		resourceChangedListener = new IResourceChangeListener() {

			public void resourceChanged(IResourceChangeEvent event) {

				try {

					switch (event.getType()) {
					case IResourceChangeEvent.POST_CHANGE:

						IResourceDelta[] delta = event.getDelta().getAffectedChildren();

						for (IResourceDelta resourceDelta : delta) {

							if (resourceDelta.getResource() instanceof IProject) {

								if (IsFileChange(resourceDelta)
										|| (resourceDelta.getKind() & IResourceDelta.ADDED) == IResourceDelta.ADDED) {

									projectToUpdate = ((IProject) resourceDelta.getResource());
									expandJob.schedule();
								}
							}
						}

						break;
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

		};
		ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceChangedListener, IResourceChangeEvent.POST_CHANGE);

	}

	Button buttonSetSort = null;

	private boolean IsFileChange(IResourceDelta delta) {
		boolean ret = false;
		if (delta.getAffectedChildren().length == 0) {

			int a = (delta.getFlags() & IResourceDelta.CONTENT);
			int b = (delta.getFlags() & IResourceDelta.MARKERS);
			boolean sync = (delta.getFlags() & IResourceDelta.SYNC) == IResourceDelta.SYNC;
			boolean add = (delta.getKind() & IResourceDelta.ADDED) == IResourceDelta.ADDED;
			if ((delta.getFlags() & IResourceDelta.CONTENT) == IResourceDelta.CONTENT
					|| add)// &&
				for (String ex : TracesTreeView.exts) {
					if (delta.getFullPath().toString().endsWith(ex))
						ret = true;
				}

			else
				ret = false;
		}
		else {
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
		manager.add(new Separator());
		manager.add(saveTraceResultsAction);
		manager.add(new Separator());
		manager.add(actionSetSort);
		manager.add(new Separator());
		manager.add(actionSetOkFilter);
		manager.add(actionSetInconclusiveFilter);
		manager.add(new Separator());
		manager.add(actionSelectToolBoxVDMJ);
		manager.add(actionSelectToolBoxVDMTools);

	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(actionRunSelected);
		// manager.add(actionRunAll);

		ISelection selection = viewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		if (obj instanceof TraceTestCaseTreeNode)
			if (((TraceTestCaseTreeNode) obj).GetStatus() != TestResultType.Unknown) {
				manager.add(actionSendToInterpreter);
				if (((TraceTestCaseTreeNode) obj).GetStatus() == TestResultType.Inconclusive)
					manager.add(okTraceTestCaseAction);
				manager.add(failTraceTestCaseAction);

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
		manager.add(saveTraceResultsAction);
		manager.add(new Separator());
		manager.add(actionSetOkFilter);
		manager.add(actionSetInconclusiveFilter);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}

	private void makeActions() {
		actionRunSelected = new Action() {

			public void run() {

				ISelection selection = viewer.getSelection();
				final Object obj = ((IStructuredSelection) selection).getFirstElement();
				int tracesTestCount = 0;

				String projectName = "";
				Dictionary<String, Dictionary<String, ArrayList<String>>> classTracesTestCase = new Hashtable<String, Dictionary<String, ArrayList<String>>>();

				if (obj instanceof TraceTestCaseTreeNode) {

					TraceTestCaseTreeNode node = (TraceTestCaseTreeNode) obj;
					TraceTreeNode tn = node.getParent();
					String className = ((ClassTreeNode) tn.getParent()).getName();
					projectName = ((ProjectTreeNode) ((ClassTreeNode) tn.getParent()).getParent()).getName();

					ArrayList<String> tmpTestCases = new ArrayList<String>();
					tmpTestCases.add(node.getName());

					Dictionary<String, ArrayList<String>> tmpTraces = new Hashtable<String, ArrayList<String>>();
					tmpTraces.put(tn.getName(), tmpTestCases);

					classTracesTestCase.put(className, tmpTraces);
					tracesTestCount = 1;

				}
				else if (obj instanceof TraceTreeNode) {
					TraceTreeNode tn = (TraceTreeNode) obj;
					String className = ((ClassTreeNode) tn.getParent()).getName();
					projectName = ((ProjectTreeNode) ((ClassTreeNode) tn.getParent()).getParent()).getName();

					ArrayList<String> tmpTestCases = new ArrayList<String>();

					Dictionary<String, ArrayList<String>> tmpTraces = new Hashtable<String, ArrayList<String>>();
					tmpTraces.put(tn.getName(), tmpTestCases);

					classTracesTestCase.put(className, tmpTraces);

					tracesTestCount = tn.getChildren().length;

				}
				else if (obj instanceof ClassTreeNode) {
					ClassTreeNode cn = (ClassTreeNode) obj;

					projectName = cn.getParent().getName();

					Dictionary<String, ArrayList<String>> tmpTraces = new Hashtable<String, ArrayList<String>>();

					classTracesTestCase.put(cn.getName(), tmpTraces);

					for (TraceTreeNode tn : cn.getChildren()) {
						tracesTestCount += tn.getChildren().length;
					}

				}
				else if (obj instanceof ProjectTreeNode) {
					ProjectTreeNode pn = (ProjectTreeNode) obj;
					projectName = pn.getName();

					for (ClassTreeNode cn : pn.getChildren()) {
						for (TraceTreeNode tn : cn.getChildren()) {
							tracesTestCount += tn.getChildren().length;
						}
					}

				}

				final Dictionary<String, Dictionary<String, ArrayList<String>>> finalClassTracesTestCase = classTracesTestCase;
				final String finalProjectName = projectName;
				final int tracesTestCountFinal = tracesTestCount;
				Job executeTestJob = new Job("Execute CT tests") {

					@Override
					protected IStatus run(IProgressMonitor monitor) {
						try {
							monitor.beginTask("CT Running tests", tracesTestCountFinal);
							int tmp = tracesTestCountFinal;
							int progress = 0;
							monitor.worked(progress);

							TracesHelper th = traceHelpers.get(finalProjectName);
							if (finalClassTracesTestCase.size() == 0)
								progress = RunTestProject(th, monitor, progress);
							else {

								Enumeration<String> classKeys = finalClassTracesTestCase.keys();
								while (classKeys.hasMoreElements()) {
									String className = classKeys.nextElement();
									if (finalClassTracesTestCase.get(className).size() == 0)
										progress = RunTestClass(th, className, monitor, progress);
									else {
										Enumeration<String> traceKeys = finalClassTracesTestCase.get(className).keys();
										while (traceKeys.hasMoreElements()) {
											String traceName = traceKeys.nextElement();

											if (finalClassTracesTestCase.get(className).get(traceName).size() == 0)
												progress = RunTestTrace(th, className, traceName, monitor, progress);
											else {
												ArrayList<String> tests = finalClassTracesTestCase.get(className).get(traceName);
												for (String testCaseName : tests) {
													progress = RunTestTraceTestCase(th, className, traceName, testCaseName, monitor, progress);

												}
											}

										}
									}
								}

							}

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						display.asyncExec(new Runnable() {

							public void run() {
								TracesHelper th = traceHelpers.get(finalProjectName);
								ISelection selection = viewer.getSelection();
								Object obj1 = ((IStructuredSelection) selection).getFirstElement();
								if (obj1 instanceof TraceTestCaseTreeNode) {
									UpdateTraceTestCasesNodeStatus(th, (TraceTestCaseTreeNode) obj);
									viewer.refresh((TraceTestCaseTreeNode) obj);
								}
								else if (obj1 instanceof TraceTreeNode) {
									UpdateTraceTestCasesNodeStatus(th, (TraceTreeNode) obj);
									viewer.refresh((TraceTreeNode) obj);
								}
								else if (obj1 instanceof ClassTreeNode) {
									UpdateTraceTestCasesNodeStatus(th, (ClassTreeNode) obj);
									viewer.refresh((ClassTreeNode) obj);
								}
								else if (obj1 instanceof ProjectTreeNode) {
									UpdateTraceTestCasesNodeStatus(th, (ProjectTreeNode) obj);
									viewer.refresh((ProjectTreeNode) obj);
								}
								// viewer.refresh();
								saveTraceResultsAction.setEnabled(true);

							}

						});
						monitor.done();
						return new Status(IStatus.OK, "org.overturetool.traces", IStatus.OK, "Traces results saveing", null);
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
			public void run() {

				Job runAllTestsJob = new Job("CT Run all tests") {

					@Override
					protected IStatus run(IProgressMonitor monitor) {
						// TODO Auto-generated method stub

						IWorkspaceRoot iworkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
						IProject[] iprojects = iworkspaceRoot.getProjects();
						int totalCount = 0;
						for (final IProject project : iprojects) {
							try {
								if (project.isOpen()
										&& project.getNature(OvertureNature.NATURE_ID) != null) {
									TracesHelper th = traceHelpers.get(project.getName());
									if (th != null)
										for (String className : th.GetTraceClasNames()) {
											for (String trace : th.GetTraces(className)) {
												for (String testCaseNum : th.GetTraceTestCases(className, trace)) {
													totalCount++;
													
												}
											}
										}
								}
							} catch (CoreException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

						monitor.beginTask("CT Running tests", totalCount);
						int progress = 1;
						monitor.worked(progress);

						for (final IProject project : iprojects) {
							if (monitor.isCanceled())
								break;
							try {
								if (project.isOpen()
										&& project.getNature(OvertureNature.NATURE_ID) != null) {
									TracesHelper th = traceHelpers.get(project.getName());
									for (String className : th.GetTraceClasNames()) {
										if (monitor.isCanceled())
											break;
										for (String trace : th.GetTraces(className)) {
											if (monitor.isCanceled())
												break;
											monitor.subTask(project.getName()
													+ "-" + className + " "
													+ trace);
											for (String testCaseNum : th.GetTraceTestCases(className, trace)) {
												if (monitor.isCanceled())
													break;
												traceHelpers.get(project.getName()).RunSingle(className, trace, testCaseNum);
												progress++;
												monitor.worked(progress);
											}
										}
									}
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();

							}

						}
						display.asyncExec(new Runnable() {

							public void run() {
								// TODO Auto-generated method stub
								UpdateTraceTestCasesNodeStatus();
								saveTraceResultsAction.setEnabled(true);
							}

						});
						return new Status(IStatus.OK, "org.overturetool.traces", IStatus.OK, "CT Run all tests", null);
					}

				};

				runAllTestsJob.schedule();
			}
		};
		actionRunAll.setText("Run all");
		actionRunAll.setToolTipText("Run all");
		actionRunAll.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_RUN_ALL_TRACES));

		// doubleClickAction = new Action() {
		// public void run() {
		// ISelection selection = viewer.getSelection();
		// Object obj = ((IStructuredSelection) selection).getFirstElement();
		// try {
		//
		// if (obj instanceof ClassTreeNode) {
		// ClassTreeNode classNode = (ClassTreeNode) obj;
		// TracesHelper tr = (TracesHelper)
		// traceHelpers.get(classNode.getParent().getName());
		// ExpandClassTreeNode(tr, classNode);
		// } else if (obj instanceof ProjectTreeNode) {
		// ProjectTreeNode projectNode = (ProjectTreeNode) obj;
		// ExpandProjectTreeNode(projectNode);
		// } else if (obj instanceof TraceTreeNode) {
		// TraceTreeNode traceNode = (TraceTreeNode) obj;
		// ClassTreeNode classNode = traceNode.getParent();
		// String className = classNode.getName();
		// TracesHelper tr = (TracesHelper)
		// traceHelpers.get(classNode.getParent().getName());
		// ExpandTraceTreeNode(tr, traceNode, className);
		// }
		// } catch (Exception e) {
		// // TODO: handle exception
		// }
		// }
		// };

		failTraceTestCaseAction = new Action() {
			public void run() {
				try {
					ISelection selection = viewer.getSelection();
					Object obj = ((IStructuredSelection) selection).getFirstElement();
					TraceTestCaseTreeNode node = (TraceTestCaseTreeNode) obj;
					TraceTreeNode tn = node.getParent();
					String className = ((ClassTreeNode) tn.getParent()).getName();
					String project = ((ProjectTreeNode) ((ClassTreeNode) tn.getParent()).getParent()).getName();
					TracesHelper tr = traceHelpers.get(project);

					tr.SetFail(className, tn.getName(), node.getName());
					node.SetStatus(tr.GetResult(className, tn.getName(), node.getName()).Status);
					viewer.refresh(node);
				} catch (CGException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};
		failTraceTestCaseAction.setText("Fail test");
		failTraceTestCaseAction.setToolTipText("Fail selected test");
		failTraceTestCaseAction.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_CASE_FAIL));

		okTraceTestCaseAction = new Action() {
			public void run() {
				try {
					ISelection selection = viewer.getSelection();
					Object obj = ((IStructuredSelection) selection).getFirstElement();
					TraceTestCaseTreeNode node = (TraceTestCaseTreeNode) obj;
					TraceTreeNode tn = node.getParent();
					String className = ((ClassTreeNode) tn.getParent()).getName();
					String project = ((ProjectTreeNode) ((ClassTreeNode) tn.getParent()).getParent()).getName();
					TracesHelper tr = traceHelpers.get(project);

					tr.SetOk(className, tn.getName(), node.getName());
					node.SetStatus(tr.GetResult(className, tn.getName(), node.getName()).Status);
					viewer.refresh(node);
				} catch (CGException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};
		okTraceTestCaseAction.setText("Approve test");
		okTraceTestCaseAction.setToolTipText("Approve selected test");
		okTraceTestCaseAction.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_CASE_SUCCES));

		saveTraceResultsAction = new Action() {
			public void run() {
				Shell shell = new Shell();

				DirectoryDialog save = new DirectoryDialog(shell, SWT.SINGLE);

				save.setText("Save CT results");
				savePath = save.open();
				if (savePath == null)
					return;
				Job job = new Job("Save Trace Results") {

					@Override
					protected IStatus run(IProgressMonitor monitor) {
						// TODO Auto-generated method stub
						monitor.beginTask("Saving trace results", 100);
						monitor.worked(10);
						Enumeration<String> itr = traceHelpers.keys();
						while (itr.hasMoreElements()) {
							String project = itr.nextElement();
							String outputPath = savePath + File.separatorChar
									+ project;
							try {
								traceHelpers.get(project).Save(outputPath);
							} catch (CGException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
						monitor.worked(100);

						return new Status(IStatus.OK, "org.overturetool.traces", IStatus.OK, "Traces results saveing", null);
					}

				};
				job.schedule();

			}
		};
		saveTraceResultsAction.setText("Save results");
		saveTraceResultsAction.setToolTipText("Save result of trace test");
		saveTraceResultsAction.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_RUN_SAVE));
		saveTraceResultsAction.setEnabled(false);
		treeAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				try {

					if (obj instanceof ClassTreeNode) {
						ClassTreeNode classNode = (ClassTreeNode) obj;
						TracesHelper tr = (TracesHelper) traceHelpers.get(classNode.getParent().getName());
						ExpandClassTreeNode(tr, classNode);
					}
					else if (obj instanceof ProjectTreeNode) {
						ProjectTreeNode projectNode = (ProjectTreeNode) obj;
						ExpandProjectTreeNode(projectNode);
					}
					else if (obj instanceof TraceTreeNode) {
						TraceTreeNode traceNode = (TraceTreeNode) obj;
						ClassTreeNode classNode = traceNode.getParent();
						String className = classNode.getName();
						TracesHelper tr = (TracesHelper) traceHelpers.get(classNode.getParent().getName());
						ExpandTraceTreeNode(tr, traceNode, className);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		};

		expandSpecInTree = new Action() {
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
				}
				else {
					viewer.addFilter(okFilter);
					actionSetOkFilter.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_CASE_FILTER_SUCCES_PRESSED));
				}

			}

		};
		actionSetOkFilter.setText("Filter ok results");
		actionSetOkFilter.setToolTipText("Filter all ok results from tree");
		actionSetOkFilter.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_CASE_FILTER_SUCCES));

		actionSetInconclusiveFilter = new Action() {
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
				}
				else {
					viewer.addFilter(inconclusiveFilter);
					actionSetInconclusiveFilter.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_CASE_FILTER_UNDETERMINED_PRESSED));
				}

			}

		};
		actionSetInconclusiveFilter.setText("Filter inconclusive results");
		actionSetInconclusiveFilter.setToolTipText("Filter all inconclusive results from tree");
		actionSetInconclusiveFilter.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_CASE_FILTER_UNDETERMINED));

		actionSetSort = new Action() {
			public void run() {
				if (viewer.getSorter() != null) {
					viewer.setSorter(null);
					actionSetSort.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_SORT));
				}
				else {
					viewer.setSorter(traceSorter);
					actionSetSort.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_SORT_PRESSED));
				}

			}

		};
		actionSetSort.setText("Sort");
		actionSetSort.setToolTipText("Sort by verdict: Fail, Inconclusive, ok, etc.");
		actionSetSort.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_SORT));

		actionSelectToolBoxVDMJ = new Action("Use VDMJ") {
			public void run() {
				UseVDMJ = true;
				actionSelectToolBoxVDMJ.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_VDMJ_LOGO_PRESSED));
				actionSelectToolBoxVDMTools.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_VDM_TOOLS_LOGO));
				SetTraceHelpers();
				UpdateTraceTestCasesNodeStatus();
				viewer.refresh();
			}
		};
		actionSelectToolBoxVDMJ.setImageDescriptor(OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_VDMJ_LOGO_PRESSED));

		actionSelectToolBoxVDMTools = new Action("Use VDM Tools") {
			public void run() {
				UseVDMJ = false;
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
				}
				else {
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
		// TODO Auto-generated method stub
		TreeItem[] aa = viewer.getTree().getItems();
		for (TreeItem treeItem : aa) {
			if (treeItem.getData() instanceof ProjectTreeNode) {
				ProjectTreeNode projectNode = ((ProjectTreeNode) treeItem.getData());
				TracesHelper th = traceHelpers.get(projectNode.getName());
				for (ClassTreeNode classNode : projectNode.getChildren()) {
					for (TraceTreeNode traceNode : classNode.getChildren()) {
						UpdateTraceTestCasesNodeStatus(th, traceNode);

					}

				}

			}
		}
		viewer.refresh();
	}

	private void UpdateTraceTestCasesNodeStatus(TracesHelper th, ProjectTreeNode projectNode) {
		for (ClassTreeNode classNode : projectNode.getChildren()) {
			UpdateTraceTestCasesNodeStatus(th, classNode);
		}
	}

	private void UpdateTraceTestCasesNodeStatus(TracesHelper th, ClassTreeNode classNode) {
		for (TraceTreeNode traceNode : classNode.getChildren()) {
			UpdateTraceTestCasesNodeStatus(th, traceNode);
		}
	}

	private void UpdateTraceTestCasesNodeStatus(TracesHelper th, TraceTreeNode traceNode) {
		for (TraceTestCaseTreeNode testNode : traceNode.getChildren()) {
			try {
				traceNode.SetSkippedCount(th.GetSkippedCount(traceNode.getParent().getName(), traceNode.getName()));
			} catch (CGException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			UpdateTraceTestCasesNodeStatus(th, testNode);
			// viewer.refresh(traceNode);
		}
	}

	private void UpdateTraceTestCasesNodeStatus(TracesHelper th, TraceTestCaseTreeNode testNode) {
		// TODO Auto-generated method stub

		try {
			TraceTreeNode traceNode = testNode.getParent();
			ClassTreeNode classNode = testNode.getParent().getParent();
			TracesHelper.TestResultType status = th.GetStatus(classNode.getName(), traceNode.getName(), testNode.getName());
			if (status == TestResultType.Skipped)
				traceNode.removeChild(testNode);
			else {

				testNode.SetStatus(status);
			}
		} catch (CGException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// viewer.refresh(testNode);
	}

	private void UpdateProject(IProject project) {
		TreeItem[] aa = viewer.getTree().getItems();
		boolean insertProject = true;
		for (TreeItem treeItem : aa) {
			if (treeItem.getData() instanceof ProjectTreeNode
					&& ((ProjectTreeNode) treeItem.getData()).getName().equals(project.getName())) {
				insertProject = false;
				ProjectTreeNode projectNode = ((ProjectTreeNode) treeItem.getData());
				String projectName = projectNode.getName();
				TracesHelper th = traceHelpers.get(projectName);
				ClassTreeNode[] classNodes = projectNode.getChildren();
				for (int i = 0; i < classNodes.length; i++) {
					projectNode.removeChild(classNodes[i]);
				}
				// now no nodes are present
				try {
					for (String className : th.GetClassNamesWithTraces()) {
						ClassTreeNode classNode = new ClassTreeNode(className);
						for (String traceName : th.GetTraces(className)) {
							if (th.HasError(className, traceName)) {
								// set error marker
								ArrayList<TraceError> errors = th.GetError(className, traceName);
								for (TraceError traceError : errors) {
									addMarker(GetFile(project, traceError.File), traceError.Message, traceError.Line, IMarker.SEVERITY_ERROR);
								}
							}
							else {
								TraceTreeNode traceNode = new TraceTreeNode(th.GetTraceDefinition(className, traceName));

								for (String traceTestCaseName : th.GetTraceTestCases(className, traceName)) {

									TraceTestCaseTreeNode testCaseNode = new TraceTestCaseTreeNode(traceTestCaseName, th.GetStatus(className, traceName, traceTestCaseName));
									traceNode.addChild(testCaseNode);

								}

								classNode.addChild(traceNode);
							}
						}
						projectNode.addChild(classNode);

					}
					viewer.refresh(projectNode);
				} catch (Exception e) {
					// TODO Auto-generated catch block
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

	// ---------------- Expand
	private void ExpandProjectTreeNode(ProjectTreeNode projectNode) {
		String projectName = projectNode.getName();
		TracesHelper treaceHelper = (TracesHelper) traceHelpers.get(projectName);
		for (ClassTreeNode classNode : projectNode.getChildren()) {
			ExpandClassTreeNode(treaceHelper, classNode);
		}
		viewer.refresh(projectNode);
	}

	private void ExpandClassTreeNode(TracesHelper treaceHelper, ClassTreeNode classNode) {
		String className = classNode.getName();
		for (TraceTreeNode traceNode : classNode.getChildren()) {
			ExpandTraceTreeNode(treaceHelper, traceNode, className);
		}
		viewer.refresh(classNode);
	}

	private void ExpandTraceTreeNode(TracesHelper traceHelper, TraceTreeNode traceNode, String className) {
		try {
			if (traceHelper.HasError(className, traceNode.getName())) {
				// set error marker
				IWorkspaceRoot iworkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
				IProject[] iprojects = iworkspaceRoot.getProjects();
				String projectName = traceNode.getParent().getParent().getName();
				ArrayList<TraceError> errors = this.traceHelpers.get(projectName).GetError(className, traceNode.getName());
				for (TraceError traceError : errors) {

					IProject project = iworkspaceRoot.getProject(projectName);
					addMarker(GetFile(project, traceError.File), traceError.Message, traceError.Line, IMarker.SEVERITY_ERROR);

				}
			}
			else
				for (String traceTestCase : traceHelper.GetTraceTestCases(className, traceNode.getName())) {
					traceNode.addChild(new TraceTestCaseTreeNode(traceTestCase, TestResultType.Unknown));
				}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		viewer.refresh(traceNode);
	}

	// --------------
	// ---------------- Get results

	private int RunTestProject(TracesHelper th, IProgressMonitor monitor, int progress) throws Exception {
		int tmpProgress = progress;
		for (String className : th.GetTraceClasNames()) {
			if (monitor != null && monitor.isCanceled())
				return progress;

			tmpProgress = RunTestClass(th, className, monitor, tmpProgress);
		}
		return tmpProgress;
	}

	private int RunTestClass(TracesHelper th, String className, IProgressMonitor monitor, int progress) throws Exception {
		int tmpProgress = progress;
		for (String traceName : th.GetTraces(className)) {

			if (monitor != null && monitor.isCanceled())
				return progress;

			tmpProgress = RunTestTrace(th, className, traceName, monitor, tmpProgress);
		}
		return tmpProgress;
	}

	private int RunTestTrace(TracesHelper th, String className, String traceName, IProgressMonitor monitor, int progress) throws CGException, Exception {
		int tmpProgress = progress;
		for (String testCase : th.GetTraceTestCases(className, traceName)) {
			if (monitor != null && monitor.isCanceled())
				return progress;

			monitor.subTask("Execution trace: " + traceName);
			tmpProgress = RunTestTraceTestCase(th, className, traceName, testCase, monitor, tmpProgress);
		}
		return tmpProgress;

	}

	private int RunTestTraceTestCase(TracesHelper th, final String className, final String traceName, final String testCaseName, IProgressMonitor monitor, int progress) throws CGException {
		final TracesHelper finalTh = th;
		try {
			if (monitor != null && monitor.isCanceled())
				return progress;

			th.RunSingle(className, traceName, testCaseName);
			if (monitor != null)
				monitor.worked(progress + 1);
			return progress + 1;
		} catch (Exception e) {
			display.asyncExec(new Runnable() {

				public void run() {
					String projectName = "";
					Enumeration<String> keys = traceHelpers.keys();
					while (keys.hasMoreElements()) {
						String string = (String) keys.nextElement();
						if (traceHelpers.get(string).equals(finalTh))
							projectName = string;

					}
					TreeItem[] aa = viewer.getTree().getItems();
					for (TreeItem treeItem : aa) {
						if (treeItem.getData() instanceof ProjectTreeNode) {
							ProjectTreeNode projectNode = ((ProjectTreeNode) treeItem.getData());
							if (!projectNode.getName().equals(projectName))
								continue;
							for (ClassTreeNode classNode : projectNode.getChildren()) {
								if (!classNode.getName().equals(className))
									continue;
								for (TraceTreeNode traceNode : classNode.getChildren()) {
									if (!traceNode.getName().equals(traceName))
										continue;
									for (TraceTestCaseTreeNode node : traceNode.getChildren()) {
										if (node.getName().equals(testCaseName)) {
											node.SetRunTimeError();
											viewer.refresh(node);
											viewer.refresh(traceNode);
											return;
										}
									}
								}
							}
						}
					}
				}

			});

		}
		return progress;
	}

	// ----------------------

	private void hookDoubleClickAction() {
		// viewer.addDoubleClickListener(new IDoubleClickListener() {
		// public void doubleClick(DoubleClickEvent event) {
		// doubleClickAction.run();
		// }
		// });
	}

	private IFile GetFile(IProject project, File file) {
		IFile f = (IFile) project.findMember(file.getName(), true);
		if (f == null) {
			for (IFile projectFile : getAllMemberFiles(project, exts)) {
				if (projectFile.getLocation().toOSString().equals(file.getPath()))
					return projectFile;
			}
		}
		return f;
	}

	private void hookTreeAction() {
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				// TODO Auto-generated method stub
				if (((ITreeSelection) event.getSelection()).getFirstElement() instanceof TraceTreeNode) {
					TraceTreeNode tn = (TraceTreeNode) ((ITreeSelection) event.getSelection()).getFirstElement();

					IWorkspaceRoot iworkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
					String projectName = tn.getParent().getParent().getName();
					IProject iproject = iworkspaceRoot.getProject(projectName);

					try {
						TracesHelper helper = traceHelpers.get(projectName);

						gotoLine(GetFile(iproject, helper.GetFile(tn.getParent().getName())), tn.GetTraceDefinition().getLine().intValue(), tn.getName());

					} catch (CGException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();

					}

				}
			}

		});

	}

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(), "Combinatorial Testing Overview", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	// private static final String MARKER_TYPE = "org.overturetool.traces";

	private void addMarker(IFile file, String message, int lineNumber, int severity) {
		try {
			if (file == null)
				return;
			lineNumber -= 1;
			IMarker[] markers = file.findMarkers(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE);
			boolean markerExist = false;
			for (IMarker marker : markers) {
				if (marker.getAttribute(IMarker.MESSAGE).equals(message)
						&& marker.getAttribute(IMarker.SEVERITY).equals(severity)
						&& marker.getAttribute(IMarker.LINE_NUMBER).equals(lineNumber))
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
			int jj = 8;
		}
	}

	private void gotoLine(IFile file, int lineNumber, String message) {
		try {
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
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber - 1);

			IDE.gotoMarker(editor, marker);

		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ViewerFilter okFilter = new ViewerFilter() {

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof TraceTestCaseTreeNode
					&& ((TraceTestCaseTreeNode) element).GetStatus() == TestResultType.Ok)
				return false;
			else
				return true;
		}

	};

	private ViewerFilter inconclusiveFilter = new ViewerFilter() {

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof TraceTestCaseTreeNode
					&& ((TraceTestCaseTreeNode) element).GetStatus() == TestResultType.Inconclusive)
				return false;
			else
				return true;
		}

	};

	private ViewerSorter traceSorter = new ViewerSorter() {
		@Override
		public int category(Object element) {
			if (element instanceof TraceTestCaseTreeNode) {
				TestResultType res = ((TraceTestCaseTreeNode) element).GetStatus();
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

	public static Boolean IsWindows() {
		String osName = System.getProperty("os.name");

		return osName.toUpperCase().indexOf("WINDOWS".toUpperCase()) > -1;
	}

}