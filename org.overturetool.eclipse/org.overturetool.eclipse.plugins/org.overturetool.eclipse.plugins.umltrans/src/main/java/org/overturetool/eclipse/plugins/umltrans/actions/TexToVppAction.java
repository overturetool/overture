package org.overturetool.eclipse.plugins.umltrans.actions;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.overturetool.tex.ClassExstractorFromTexFiles;


public class TexToVppAction implements IObjectActionDelegate {

	private Shell shell;
	
	/**
	 * Constructor for Action1.
	 */
	public TexToVppAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
//		MessageDialog.openInformation(
//			shell,
//			"MenuTest Plug-in",
//			"New Action was executed.");
		org.eclipse.swt.widgets.Shell s = new org.eclipse.swt.widgets.Shell();

		org.eclipse.swt.widgets.FileDialog fd = new org.eclipse.swt.widgets.FileDialog(
				s, SWT.MULTI);
		fd.setText("Open");

		String[] filterExt = { "*.tex" };
		fd.setFilterExtensions(filterExt);
		String ret = fd.open();
		String[] fLst = fd.getFileNames();
		if (ret != null) {

			try {
				List<String> files = new Vector<String>();
				for (int i = 0; i < fLst.length; i++) {
					String separator = System.getProperty("file.separator");
					files.add( fd.getFilterPath() + separator + fLst[i]);
				}

				String outputDir = new File(files.get(0)).getParent();

				List<String> vppFiles = ClassExstractorFromTexFiles.exstract(files,
						outputDir);
				String outFile = "";
//				for (int i = 0; i < vppFiles.length; i++) {
//					outFile += "\n" + vppFiles[i];
//				}
				
				for (String string : vppFiles)
				{
					outFile += "\n" +string;
				}
				MessageDialog.openInformation(shell, "Tex 2 vpp",
						"Processing completed: " + outFile);

			} catch (Exception ex) {
				System.err.println(ex.getMessage() + ex.getStackTrace());
				MessageDialog.openInformation(shell, "Error",
						"Processing completed with errors");
			}

		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
