package org.overture.ide.plugins.showtrace.viewer;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.overture.ide.utility.ConsoleWriter;
import org.overturetool.traceviewer.ast.itf.IOmlTraceFile;
import org.overturetool.traceviewer.parser.TraceParser;

public class VdmRtLogEditor extends EditorPart implements IViewCallback {

	// IEditorSite site=null;

	private File selectedFile;

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);

		// System.out.println(input.getName());
		// IFile file = ((IPathEditorInput)input).getPath().getFile();
		IPath path = ((IPathEditorInput) input).getPath();

		selectedFile = path.toFile();
		fileName = selectedFile.getAbsolutePath();
		// selectedFile = ProjectUtility.getFile(
		// ResourcesPlugin.getWorkspace().getRoot(), path);

	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	public VdmRtLogEditor() {
		theConjectures = null;
		theArch = null;
		theOverview = null;
		theDetails = new HashSet<GenericTabItem>();
		fileName = null;
		theTimes = null;
		currentTime = 0L;
		theVisitor = null;
		theMarkers = null;
	}

	@Override
	public void createPartControl(Composite parent) {

		Control[] childern = parent.getChildren();// this.getSite().getShell().getChildren();
		for (Control control : childern) {
			control.setVisible(false);
		}
		form = new SashForm(parent, 512);
		form.setLayout(new FillLayout());
		folder = new TabFolder(form, 128);
		theConjectures = new ValidationTable(form, this);
		form.setWeights(new int[] { 85, 15 });
		theArch = new GenericTabItem("Architecture overview", folder, null);
		theOverview = new GenericTabItem("Execution overview", folder, null);
		try {
			theMarkers = new TracefileMarker();
		} catch (CGException cge) {
			showMessage(cge);
		}
		makeActions();
		contributeToActionBars();

		try {
			parseFile(selectedFile.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void contributeToActionBars() {
		// IActionBars bars = site.getActionBars();
		// fillLocalPullDown(bars.getMenuManager());
		// fillLocalToolBar(bars.getToolBarManager());
	}

//	private void fillLocalPullDown(IMenuManager manager) {
//		manager.add(fileOpenAction);
//		manager.add(exportDiagramAction);
//	}
//
//	private void fillLocalToolBar(IToolBarManager manager) {
//		manager.add(fileOpenAction);
//		manager.add(exportDiagramAction);
//		manager.add(moveHorizontalAction);
//		manager.add(openValidationAction);
//	}

	private void makeActions() {
		fileOpenAction = new Action() {

			@Override
			public void run() {
				openFileAction();
			}

			// final TracefileViewer this$0;
			//
			//	            
			// {
			// this$0 = TracefileViewer.this;
			// super();
			// }
		};
		fileOpenAction.setText("Open trace file");
		fileOpenAction.setToolTipText("Open trace file");
		fileOpenAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
				"IMG_OBJ_FILE"));
		exportDiagramAction = new Action() {

			@Override
			public void run() {
				diagramExportAction();
			}

			// final TracefileViewer this$0;
			//
			//	            
			// {
			// this$0 = TracefileViewer.this;
			// super();
			// }
		};
		exportDiagramAction.setText("Export to JPG");
		exportDiagramAction.setToolTipText("Save all diagrams as JPG");
		exportDiagramAction.setImageDescriptor(TracefileViewerPlugin.getImageDescriptor((new StringBuilder("icons")).append(
				File.separator).append("print.gif").toString()));
		exportDiagramAction.setEnabled(false);
		moveHorizontalAction = new Action() {

			@Override
			public void run() {
				moveHorizontal();
			}

			// final TracefileViewer this$0;
			//
			//	            
			// {
			// this$0 = TracefileViewer.this;
			// super();
			// }
		};
		moveHorizontalAction.setText("Move time");
		moveHorizontalAction.setToolTipText("Move time in the views");
		moveHorizontalAction.setImageDescriptor(TracefileViewerPlugin.getImageDescriptor((new StringBuilder("icons")).append(
				File.separator).append("panhor.gif").toString()));
		moveHorizontalAction.setEnabled(false);
		openValidationAction = new Action() {

			@Override
			public void run() {
				openValidationConjectures();
			}

			// final TracefileViewer this$0;
			//
			//	            
			// {
			// this$0 = TracefileViewer.this;
			// super();
			// }
		};
		openValidationAction.setText("Show failed conjectures");
		openValidationAction.setToolTipText("Open the validation conjecture file");
		openValidationAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
				"IMG_OBJS_WARN_TSK"));
		openValidationAction.setEnabled(false);
	}

	private void openValidationConjectures() {
		FileDialog fDlg = new FileDialog(getSite().getShell());
		String valFileName = fDlg.open();
		theConjectures.parseValidationFile(valFileName);
	}

	private void openFileAction() {
		if (fileName != null)
			deleteTabPages();
		if (!$assertionsDisabled && theVisitor != null) {
			throw new AssertionError();
		} else {
			FileDialog fDlg = new FileDialog(getSite().getShell());
			fileName = fDlg.open();
			parseFile(fileName);
			return;
		}
	}

	private void diagramExportAction() {
		// if(fileName != null)
		// {
		theArch.exportJPG((new StringBuilder(String.valueOf(fileName))).append(
				".arch").toString());
		theOverview.exportJPG((new StringBuilder(String.valueOf(fileName))).append(
				".overview").toString());
		GenericTabItem pgti;
		for (Iterator<GenericTabItem> iter = theDetails.iterator(); iter.hasNext(); pgti.exportJPG((new StringBuilder(String.valueOf(fileName))).append(
				".").append(pgti.getName()).toString()))
			pgti = iter.next();

		// showMessage("Diagrams generated!");
		// } else
		// {
		// showMessage("Please open a trace file first!");
		// }
	}

	private void moveHorizontal() {
		SelectTimeDialog theDialog = new SelectTimeDialog(folder.getShell(), theTimes, currentTime);
		if (theDialog.open() == 0 && theDialog.selectedTime != currentTime) {
			currentTime = theDialog.selectedTime;
			updateOverviewPage();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.overture.ide.plugins.showtrace.viewer.IViewCallback#panToTime(long,
	 * long)
	 */
	public void panToTime(long time, long thrid) {
		for (Iterator<Long> iter = theTimes.iterator(); iter.hasNext();) {
			long theTime = iter.next().longValue();
			if (theTime < time)
				currentTime = theTime;
		}

		updateOverviewPage();
	}

	public void addLowerError(Long x1, Long x2, String name) {
		if (theVisitor != null)
			try {
				theVisitor.addFailedLower(x1, x2, name);
			} catch (CGException cge) {
				showMessage(cge);
			}
	}

	public void addUpperError(Long x1, Long x2, String name) {
		if (theVisitor != null)
			try {
				theVisitor.addFailedUpper(x1, x2, name);
			} catch (CGException cge) {
				showMessage(cge);
			}
	}

	private void parseFile(String fname) {
		TraceParser theParser = new TracefileParser(fname, "UTF8", theMarkers);
		try {
			IOmlTraceFile theAst = theParser.parse();
			if (theParser.errorCount() == 0) {
				TracefileChecker theChecker = new TracefileChecker(theMarkers);
				theChecker.visitNode(theAst);
				if (theChecker.hasErrors().booleanValue()) {
					showMessage((new StringBuilder()).append(
							theMarkers.errorCount()).append(
							" errors encoutered in file \"").append(fname).append(
							"\"").toString());
				} else {
					showMessage((new StringBuilder(String.valueOf(theAst.getTrace().size()))).append(
							" lines read from file \"").append(fname).append(
							"\"").toString());
					theVisitor = new TracefileVisitor();
					theVisitor.visitNode(theAst);
					createTabPages();
				}
			} else {
				showMessage((new StringBuilder(String.valueOf(theParser.errorCount()))).append(
						" errors encoutered in file \"").append(fname).append(
						"\"").toString());
			}
		} catch (CGException cge) {
			showMessage(cge);
		}
	}

	@SuppressWarnings("unchecked")
	private void createTabPages() {
		try {
			theTimes = theVisitor.getAllTimes();
			theVisitor.drawArchitecture(theArch);
			theVisitor.drawOverview(theOverview, new Long(currentTime));
			exportDiagramAction.setEnabled(true);
			moveHorizontalAction.setEnabled(true);
			openValidationAction.setEnabled(true);
			Vector<tdCPU> theCpus = theVisitor.getCpus();
			GenericTabItem theDetail;
			for (Iterator<tdCPU> iter = theCpus.iterator(); iter.hasNext(); theDetails.add(theDetail)) {
				tdCPU theCpu = iter.next();
				theDetail = new GenericTabItem(theCpu.getName(), folder, theCpu);
				theVisitor.drawCpu(theDetail, new Long(currentTime));
			}

		} catch (CGException cge) {
			showMessage(cge);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.overture.ide.plugins.showtrace.viewer.IViewCallback#updateOverviewPage
	 * ()
	 */
	public void updateOverviewPage() {
		try {
			theOverview.disposeFigures();
			theVisitor.drawOverview(theOverview, new Long(currentTime));
			GenericTabItem theDetail;
			for (Iterator<GenericTabItem> iter = theDetails.iterator(); iter.hasNext(); theVisitor.drawCpu(
					theDetail, new Long(currentTime))) {
				theDetail = iter.next();
				theDetail.disposeFigures();
			}

		} catch (CGException cge) {
			showMessage(cge);
		}
	}

	private void deleteTabPages() {
		folder.setSelection(0);
		exportDiagramAction.setEnabled(false);
		moveHorizontalAction.setEnabled(false);
		openValidationAction.setEnabled(false);
		GenericTabItem pgti;
		for (Iterator<GenericTabItem> iter = theDetails.iterator(); iter.hasNext(); pgti.dispose())
			pgti = iter.next();

		theDetails = new HashSet<GenericTabItem>();
		theArch.disposeFigures();
		theOverview.disposeFigures();
		fileName = null;
		theVisitor = null;
		theTimes = null;
		currentTime = 0L;
		try {
			theMarkers.dispose();
			theMarkers = new TracefileMarker();
		} catch (CGException cge) {
			showMessage(cge);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.overture.ide.plugins.showtrace.viewer.IViewCallback#showMessage(java
	 * .lang.String)
	 */
	public void showMessage(String message) {
		ConsoleWriter cw = new ConsoleWriter();
		cw.println(message);
		cw.Show();
		// MessageDialog.openInformation(getSite().getShell(),
		// "Tracefile viewer", message);
	}

	private void showMessage(CGException cge) {
		MessageDialog.openInformation(getSite().getShell(), "Tracefile viewer",
				cge.getMessage());
		cge.printStackTrace(System.out);

		ConsoleWriter cw = new ConsoleWriter();
		cw.println(cge.getMessage());
		ConsoleWriter.getExceptionStackTraceAsString(cge);
		cw.Show();
	}

	@Override
	public void setFocus() {
		folder.setFocus();
	}

	@Override
	public void dispose() {
		try {
			theMarkers.dispose();
		} catch (CGException cge) {
			cge.printStackTrace(System.out);
		}
	}

	public Action getExportDiagramAction() {
		return exportDiagramAction;
	}

	public Action getMoveHorizontalAction() {
		return moveHorizontalAction;

	}

	private SashForm form;
	private TabFolder folder;
	private ValidationTable theConjectures;
	private GenericTabItem theArch;
	private GenericTabItem theOverview;
	private HashSet<GenericTabItem> theDetails;
	private String fileName;
	private Vector<Long> theTimes;
	private long currentTime;
	private Action fileOpenAction;
	private Action exportDiagramAction;
	private Action moveHorizontalAction;
	private Action openValidationAction;
	private TracefileVisitor theVisitor;
	private TracefileMarker theMarkers;
	static final boolean $assertionsDisabled = false;// !org/overturetool/tracefile/viewer/TracefileViewer.desiredAssertionStatus();

}
